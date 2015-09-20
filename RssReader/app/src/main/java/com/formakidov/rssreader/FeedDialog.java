package com.formakidov.rssreader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

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
	private TextInputLayout tlName;
	private TextInputLayout tlUrl;
	private String uuid;
	private int position = -1;
	private boolean isEdit;

	private final MaterialDialog.ButtonCallback mButtonCallback = new MaterialDialog.ButtonCallback() {
		@Override
		public void onPositive(MaterialDialog dialog) {
			String name = etName.getText().toString();
			String url = etUrl.getText().toString();
			if (name.trim().isEmpty()) {
				tlName.setError(getString(R.string.error_empty_name));
				return;
			}
			if (!Tools.isValidUrl(url)) {
				tlUrl.setError(getString(R.string.error_invalid_url));
				return;
			}
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
		isEdit = getTag().equals(context.getString(R.string.edit_feed));
		final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
				.title(isEdit ? R.string.edit_feed : R.string.add_feed)
				.callback(mButtonCallback)
				.autoDismiss(false)
				.customView(R.layout.feed_dialog, false)
				.positiveText(isEdit ? R.string.save : R.string.create)
				.negativeText(android.R.string.cancel)
				.build();

		View customView = dialog.getCustomView();
		etName = (EditText)customView.findViewById(R.id.et_name);
		etUrl = (EditText)customView.findViewById(R.id.et_url);
		tlName = (TextInputLayout) customView.findViewById(R.id.tl_name);
		tlUrl = (TextInputLayout) customView.findViewById(R.id.tl_url);
		Bundle args = getArguments();
		if (null != args) {
			isEdit = true;
			position = args.getInt(FEED_POSITION, -1);
			uuid = args.getString(FEED_UUID, EMPTY_STRING);
			etName.setText(args.getString(FEED_NAME, EMPTY_STRING));
			etUrl.setText(args.getString(FEED_URL, EMPTY_STRING));
		}
		etName.addTextChangedListener(new SimpleTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (!s.toString().trim().isEmpty()) {
					tlName.setError(null);
				}
			}
		});
		etUrl.addTextChangedListener(new SimpleTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				tlUrl.setError(null);
			}
		});
		etUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					dialog.getActionButton(DialogAction.POSITIVE).performClick();
					return true;
				}
				return false;
			}
		});

		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		mCallback.onCancel();
	}
}