package com.formakidov.rssreader.task;

import android.os.AsyncTask;

import com.formakidov.rssreader.tools.RssParser;

public class GetLastBuildDateTask extends AsyncTask<String, Void, Long> {
    @Override
    protected Long doInBackground(String... urls) {
        try {
            return new RssParser(urls[0]).getLastBuildDate();
        } catch (Exception e) {
            return 1L;
        }
    }
}
