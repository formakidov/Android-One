package com.formakidov.rssreader.task;

import android.os.AsyncTask;

import com.formakidov.rssreader.tools.RssParser;

public class LoadDefaultImageUrlTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        try {
            return new RssParser(urls[0]).getDefaultImageUrl();
        } catch (Exception e) {
            return null;
        }
    }
}