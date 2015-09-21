package com.formakidov.rssreader.task;

import android.os.AsyncTask;

import com.formakidov.rssreader.tools.Constants;
import com.formakidov.rssreader.tools.RssParser;

public class LoadDefaultImageUrlTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        try {
            return new RssParser(urls[0]).getFeedLogoUrl();
        } catch (Exception e) {
            return Constants.EMPTY_STRING;
        }
    }
}