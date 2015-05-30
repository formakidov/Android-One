package com.formakidov.rssreader.interfaces;

public interface RssProgressListener {
	public void onProgressUpdate(int percent);
	public void onLoadingComplete();
}
