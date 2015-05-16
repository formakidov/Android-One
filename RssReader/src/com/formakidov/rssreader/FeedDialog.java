package com.formakidov.rssreader;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakidov.rssreader.fragment.FeedListFragment;

public class FeedDialog extends DialogFragment {		
		private FeedListFragment fragment;

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
				getDialog().setTitle(fragment.getString(R.string.add_new_feed));
		
				View addItemView = inflater.inflate(R.layout.add_item_dialog, null, false);
				
				//TODO initialize
				
				return addItemView;
			}
			return null;
		}		
		
		@Override
		public void onStart() {
			super.onStart();
			//TODO
//			Activity activity = getActivity();
//			if (activity != null) {
//				Intent intent = activity.getIntent();
//				if (intent.hasExtra(JSON_EMAIL)) {
//					email.setText(intent.getStringExtra(JSON_EMAIL));
//					pass.setText(intent.getStringExtra(JSON_PASSWORD));
//				}
//			}
		}
		
		@Override
		public void onStop() {
			super.onStop();
			//TODO
//			Activity activity = getActivity();
//			if (activity != null) {
//				Intent intent = activity.getIntent();
//				intent.putExtra(JSON_EMAIL, email.getText().toString());
//				intent.putExtra(JSON_PASSWORD, pass.getText().toString());
//			}
		}
	}
