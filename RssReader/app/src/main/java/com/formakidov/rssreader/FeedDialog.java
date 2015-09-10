package com.formakidov.rssreader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.formakidov.rssreader.data.FeedItem;
import com.formakidov.rssreader.listeners.SimpleTextWatcher;
import com.formakidov.rssreader.tools.Constants;
import com.formakidov.rssreader.tools.Tools;

public class FeedDialog extends DialogFragment implements Constants {
	private FeedDialogCallback mCallback;
	private EditText etName;
	private EditText etUrl;
	private boolean isEdit;
	private String uuid;
	private int position = -1;

	private final MaterialDialog.ButtonCallback mButtonCallback = new MaterialDialog.ButtonCallback() {
		@Override
		public void onPositive(MaterialDialog dialog) {
			String url = etUrl.getText().toString();
			if (!Tools.validateUrl(url)) {
				showErrorMessage(Constants.ERROR_INVALID_URL);
				return;
			}
			if (etName.getText().toString().isEmpty()) { //TODO disable positive button button if empty
				showErrorMessage(Constants.ERROR_NO_NAME);
				return;
			}
			String name = etName.getText().toString();
			if (isEdit) {
				mCallback.onFeedChanged(position, new FeedItem(uuid, name, url));
			} else {
				mCallback.onFeedCreated(new FeedItem(name, url));
			}
			dialog.dismiss();
		}

		@Override
		public void onNegative(MaterialDialog materialDialog) {
			materialDialog.dismiss();
			mCallback.onCancel();
		}
	};

	public interface FeedDialogCallback {
		void onFeedChanged(int position, FeedItem changedItem);
		void onFeedCreated(FeedItem newItem);
		void onCancel();
	}

	public FeedDialog(FeedDialogCallback callback) {
		super();
		this.mCallback = callback;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setRetainInstance(true);
		Context context = getContext();
		isEdit = getTag() == context.getString(R.string.edit_feed);
		final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
				.title(isEdit ? R.string.add_feed : R.string.edit_feed)
				.callback(mButtonCallback)
				.customView(R.layout.add_item_dialog, false)
				.autoDismiss(true)
				.positiveText(isEdit ? R.string.save : R.string.create)
				.negativeText(android.R.string.cancel)
				.build();


		etName = ((EditText)dialog.getCustomView().findViewById(R.id.et_name));
		etUrl = ((EditText)dialog.getCustomView().findViewById(R.id.et_url));
		Bundle args = getArguments();
		if (null != args) {
			isEdit = true;
			position = args.getInt(FEED_POSITION, -1);
			uuid = args.getString(FEED_UUID, EMPTY_STRING);
			etName.setText(args.getString(FEED_NAME, EMPTY_STRING));
			etUrl.setText(args.getString(FEED_URL, EMPTY_STRING));
		}
		dialog.getActionButton(DialogAction.POSITIVE).setEnabled(isEdit);
		etName.addTextChangedListener(new SimpleTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				//TODO validation, highlighting, hints
//				((FloatingEditText) editText).setHighlightedColor(defColor);
//				editText.setHint(R.string.playlist_name);
				View positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
				positiveBtn.setEnabled(true);
				if (s.toString().trim().isEmpty()) {
					positiveBtn.setEnabled(false);
				}
			}
		});

		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		mCallback.onCancel();
	}

	private void showErrorMessage(String message) {
		//TODO material text input with error
		Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
	}
}