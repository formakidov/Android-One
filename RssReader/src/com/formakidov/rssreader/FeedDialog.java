package com.formakidov.rssreader;

import java.net.URI;
import java.net.URISyntaxException;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.formakidov.rssreader.fragment.FeedListFragment;

public class FeedDialog extends DialogFragment {
	private FeedListFragment fragment;
	private EditText etName;
	private EditText etUrl;

	public FeedDialog(FeedListFragment fragment) {
		super();
		this.fragment = fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (fragment.isAdded()) {
			super.onCreateView(inflater, container, savedInstanceState);
			getDialog().setTitle(fragment.getString(R.string.feed_properties));

			View addItemView = inflater.inflate(R.layout.add_item_dialog, null, false);
			etName = (EditText) addItemView.findViewById(R.id.et_name);
			etUrl = (EditText) addItemView.findViewById(R.id.et_url);
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
						showErrorMessage(Constants.INVALID_URL);
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
					fragment.addFeed(name, url);
					getDialog().dismiss();
				}
			});

			return addItemView;
		}
		return null;
	}

	private void showErrorMessage(String message) {
		//TODO
		Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
	}
}