package com.formakidov.rssreader.listeners;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class SimpleImageLoadingListener implements ImageLoadingListener {
    @Override
    public void onLoadingStarted(String imageUri, View view) {
        // Empty implementation
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        // Empty implementation
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        // Empty implementation
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        onLoadingFailed(imageUri, view, new FailReason(FailReason.FailType.UNKNOWN, new Exception("Unknown")));
    }
}
