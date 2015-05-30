package com.formakidov.rssreader.data;

public class RssItem {
	private String title;
	private String description;
	private String imageUrl = "";
	private String link;
	private String pubDate;
	private String defDescription;
	private String defImageUrl = "";
	private String defTitle = "";
	private String defLink = "";

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

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
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
}