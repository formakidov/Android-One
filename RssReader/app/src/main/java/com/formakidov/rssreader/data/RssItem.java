package com.formakidov.rssreader.data;

import com.formakidov.rssreader.tools.Constants;
import com.formakidov.rssreader.tools.Tools;

import java.text.ParseException;
import java.util.Date;

public class RssItem implements Constants{
	private String title = EMPTY_STRING;
	private String description = EMPTY_STRING;
	private String imageUrl = EMPTY_STRING;
	private String link;
	private String formattedPubDate;
	private String pubDate;
	private String defDescription;
	private String defImageUrl = EMPTY_STRING;
	private String defTitle = EMPTY_STRING;
	private String defLink = EMPTY_STRING;
	private boolean isSaved;
	private String uuid;
	private long rssBuildDate; // 0 if no rssBuildDate
	private String rssUrl;
	private long pubDateMs;

	public RssItem(String rssUrl) {
		this.rssUrl = rssUrl;
		this.uuid = Tools.getRandomUUID();
	}
	
	public RssItem(String rssUrl, String uuid) {
		this.rssUrl = rssUrl;
		this.uuid = uuid;
	}
	
	public boolean hasDefTitle() {
		return !defTitle.isEmpty();
	}

	public String getDefImageUrl() {
		return defImageUrl;
	}

	public void setDefImageUrl(String defaultImageUrl) {
		this.defImageUrl = defaultImageUrl;
	}

	public String getDefTitle() {
		return defTitle;
	}

	public void setDefTitle(String defTitle) {
		this.defTitle = defTitle;
	}

	public String getFormattedPubDate() {
		return formattedPubDate;
	}

	public String getFullFormattedPubDate() {
		try {
			Date date = Tools.RFC822_DATE_FORMAT.parse(pubDate);
			return Constants.dateFormatFull.format(date);
		} catch (ParseException e) {
			return pubDate;
		}
	}

	public String getPubDate() {
		return pubDate;
	}

	public long getPubDateMs() {
		return pubDateMs;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
		try {
			Date date = Tools.RFC822_DATE_FORMAT.parse(pubDate);
			this.pubDateMs = date.getTime();
			this.formattedPubDate = Constants.dateFormatPubDate.format(date);
		} catch (ParseException e) {
			this.formattedPubDate = pubDate;
		}
	}

	public String getDescription() {
		return description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDefDescription() {
		return defDescription;
	}

	public void setDefDescription(String defDescription) {
		this.defDescription = defDescription;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDefLink() {
		return defLink;
	}

	public void setDefLink(String defLink) {
		this.defLink = defLink;
	}

	public String getUUID() {
		return uuid;
	}

	public boolean isSaved() {
		return isSaved;
	}

	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}

	public long getRssBuildDate() {
		return rssBuildDate;
	}

	public void setBuildDateMs(long buildDate) {
		this.rssBuildDate = buildDate;
	}

	public String getRssUrl() {
		return rssUrl;
	}
}