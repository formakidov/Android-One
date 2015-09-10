package com.formakidov.rssreader.listeners;

import android.text.Editable;
import android.text.TextWatcher;

public class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Empty implementation
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Empty implementation
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Empty implementation
    }
}
