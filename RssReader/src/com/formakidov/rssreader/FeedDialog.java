package com.formakidov.rssreader;

import java.net.URI;
import java.net.URISyntaxException;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.formakidov.rssreader.data.FeedItem;
import com.formakidov.rssreader.fragment.FeedListFragment;

public class FeedDialog extends DialogFragment implements Constants {
	private FeedListFragment fragment;
	private EditText etName;
	private EditText etUrl;
	private boolean isEdit;
	private String uuid;
	
	public FeedDialog(FeedListFragment fragment) {
		super();
		this.fragment = fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@SuppressLint("InflateParams") 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		if (fragment.isAdded()) {
			super.onCreateView(inflater, container, savedInstanceState);

			View addItemView = inflater.inflate(R.layout.add_item_dialog, null, false);
			etName = (EditText) addItemView.findViewById(R.id.et_name);
			etUrl = (EditText) addItemView.findViewById(R.id.et_url);
			final Bundle args = getArguments();
			if (null != args) {
				isEdit = true;
				uuid = args.getString(FEED_UUID, EMPTY_STRING);
				etName.setText(args.getString(FEED_NAME, EMPTY_STRING));
				etUrl.setText(args.getString(FEED_URL, EMPTY_STRING));
			}

			getDialog().setTitle(fragment.getString(isEdit ? R.string.edit_feed : R.string.add_feed));
			
			addItemView.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					getDialog().dismiss();
				}
			});
			
			addItemView.findViewById(R.id.btn_save).setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					String url = etUrl.getText().toString();
					if (!Tools.validateUrl(url)) {
						showErrorMessage(Constants.ERROR_INVALID_URL);
						return;
					} 
					if (etName.getText().toString().isEmpty()) {
						showErrorMessage(Constants.ERROR_NO_NAME);
						return;
					}
					
					String name = etName.getText().toString();
					if (name.isEmpty()) {
						try {
							name = new URI(url).getHost();
						} catch (URISyntaxException e) {
							name = url;
						}
					}
					
					if (isEdit) {
						fragment.feedChanged(args.getInt(FEED_POSITION, -1), new FeedItem(uuid, name, url));
					} else {
						fragment.addFeed(new FeedItem(name, url));
					}
					
					getDialog().dismiss();
				}
			});

			return addItemView;
		}
		return null;
	}

	private void showErrorMessage(String message) {
		//TODO material text input with error
		Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
	}
}