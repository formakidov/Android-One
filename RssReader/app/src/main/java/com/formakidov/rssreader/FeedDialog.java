package com.formakidov.rssreader;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.formakidov.rssreader.data.FeedItem;
import com.formakidov.rssreader.fragment.FeedListFragment;
import com.formakidov.rssreader.tools.Constants;
import com.formakidov.rssreader.tools.Tools;

import java.net.URI;
import java.net.URISyntaxException;

public class FeedDialog extends DialogFragment implements Constants, View.OnClickListener {
	private FeedListFragment fragment;
	private EditText etName;
	private EditText etUrl;
	private boolean isEdit;
	private String uuid;
	private int position = -1;

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
			Bundle args = getArguments();
			if (null != args) {
				isEdit = true;
				position = args.getInt(FEED_POSITION, -1);
				uuid = args.getString(FEED_UUID, EMPTY_STRING);
				etName.setText(args.getString(FEED_NAME, EMPTY_STRING));
				etUrl.setText(args.getString(FEED_URL, EMPTY_STRING));
			}

			getDialog().setTitle(fragment.getString(isEdit ? R.string.edit_feed : R.string.add_feed));

			addItemView.findViewById(R.id.btn_cancel).setOnClickListener(this);
			addItemView.findViewById(R.id.btn_save).setOnClickListener(this);

			return addItemView;
		}
		return null;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		fragment.feedChanged(-1, null);
	}

	private void showErrorMessage(String message) {
		//TODO material text input with error
		Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_save:
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
					fragment.feedChanged(position, new FeedItem(uuid, name, url));
				} else {
					fragment.addFeed(new FeedItem(name, url));
				}
				getDialog().dismiss();
				break;
			case R.id.btn_cancel:
				getDialog().dismiss();
				fragment.feedChanged(-1, null);
				break;
			default:
				break;
		}
	}
}