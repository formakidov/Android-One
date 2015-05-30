package com.formakidov.rssreader;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.formakidov.rssreader.data.RssItem;
import com.formakidov.rssreader.interfaces.RssProgressListener;

import android.annotation.SuppressLint;

public class RssParser {
	private String rssUrl;

	public RssParser(String url) {
		rssUrl = url;
	}

	@SuppressLint("NewApi") 
	public List<RssItem> getItems(RssProgressListener listener) {
		List<RssItem> items = new ArrayList<RssItem>();
		HttpURLConnection conn = null;
		try {
			InputStream streamer = null;
			URL url = new URL(rssUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			streamer = conn.getInputStream();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(streamer);
			doc.getDocumentElement().normalize();

			String defTitle = "";
			String defDescription = "";
			String defLink = "";
			String defImageUrl = "";
			
			NodeList nodeListTitle = doc.getElementsByTagName("title");
			Node nodeTitle = nodeListTitle.item(0);
			if (nodeTitle != null) {
				defTitle = StringEscapeUtils.unescapeHtml4(nodeTitle.getChildNodes().item(0).getNodeValue());
			}
			
			NodeList nodeListLink = doc.getElementsByTagName("link");
			Node nodeLink = nodeListLink.item(0);
			if (nodeLink != null) {
				defLink = nodeLink.getChildNodes().item(0).getNodeValue();
			}
			
			NodeList nodeListDescription = doc.getElementsByTagName("description");
			Node nodeDescription = nodeListDescription.item(0);
			if (nodeDescription != null) {
				defDescription = 
						StringEscapeUtils.unescapeHtml4(nodeDescription.getChildNodes().item(0).getNodeValue())
						.replaceAll(Constants.REGEX_DELETE_TAGS, "");
			}

			NodeList nodeListImage = doc.getElementsByTagName("image");
			Node nodeImage = nodeListImage.item(0);
			if (nodeImage != null) {
				Element ElmntImg = (Element) nodeImage;
				Element defTitleNmElmnt = (Element) ElmntImg.getElementsByTagName("url").item(0);
				if (defTitleNmElmnt != null) {
					defImageUrl = ((Node) defTitleNmElmnt.getChildNodes().item(0)).getNodeValue();
				}
			}
			
			NodeList nodeList = doc.getElementsByTagName("item");
			for (int i = 0; i < nodeList.getLength(); i++) {
				RssItem rssItem = new RssItem();
				Node node = nodeList.item(i);
				if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
					Element Elmnt = (Element) node;
					
					Element titleNmElmnt = (Element) Elmnt.getElementsByTagName("title").item(0);
					if (titleNmElmnt != null) {
						String title = StringEscapeUtils.unescapeHtml4(((Node) titleNmElmnt.getChildNodes().item(0)).getNodeValue());
						rssItem.setTitle(title);
					}
					
					Element linkNmElmnt = (Element) Elmnt.getElementsByTagName("link").item(0);
					if (linkNmElmnt != null) {
						String link = ((Node) linkNmElmnt.getChildNodes().item(0)).getNodeValue();
						rssItem.setLink(link);
					}
					
					Element pubDateNmElmnt = (Element) Elmnt.getElementsByTagName("pubDate").item(0);
					if (pubDateNmElmnt != null) {
						rssItem.setPubDate(((Node) pubDateNmElmnt.getChildNodes().item(0)).getNodeValue());
					}
					
					Element enclosureNmElmnt = (Element) Elmnt.getElementsByTagName("enclosure").item(0);
					if (enclosureNmElmnt != null) {
						String imageUrl = enclosureNmElmnt.getAttribute("url");
						if (rssItem.getImageUrl().isEmpty()) {
		                	List<String> links = getImageUrls(imageUrl);
	                		rssItem.setImageUrl(links.size() > 0 ? links.get(0) : "");
	                	}
					}
					
					Element mediaThumbnailNmElmnt = (Element) Elmnt.getElementsByTagName("media:thumbnail").item(0);
					if (mediaThumbnailNmElmnt != null) {
						String imageUrl = mediaThumbnailNmElmnt.getAttribute("url");
						if (rssItem.getImageUrl().isEmpty()) {
		                	List<String> links = getImageUrls(imageUrl);
	                		rssItem.setImageUrl(links.size() > 0 ? links.get(0) : "");
	                	}
					}
					
					Element mediaContentNmElmnt = (Element) Elmnt.getElementsByTagName("media:content").item(0);
					if (mediaContentNmElmnt != null) {
						String imageUrl = mediaContentNmElmnt.getAttribute("url");
						if (rssItem.getImageUrl().isEmpty()) {
		                	List<String> links = getImageUrls(imageUrl);
	                		rssItem.setImageUrl(links.size() > 0 ? links.get(0) : "");
	                	}
					}

					Element contentEncodedNmElmnt = (Element) Elmnt.getElementsByTagName("content:encoded").item(0);
					if (contentEncodedNmElmnt != null) {						
						String content = ((Node) contentEncodedNmElmnt.getChildNodes().item(0)).getNodeValue();
						if (rssItem.getImageUrl().isEmpty()) {
		                	List<String> links = getImageUrls(content);
	                		rssItem.setImageUrl(links.size() > 0 ? links.get(0) : "");
	                	}
					}
					
					Element descriptionNmElmnt = (Element) Elmnt.getElementsByTagName("description").item(0);
					if (descriptionNmElmnt != null) {
						String description = StringEscapeUtils.unescapeHtml4(((Node) descriptionNmElmnt.getChildNodes().item(0)).getNodeValue());
						rssItem.setDescription(description.replaceAll(Constants.REGEX_DELETE_TAGS, ""));
	                	if (rssItem.getImageUrl().isEmpty()) {
		                	List<String> links = getImageUrls(description);
	                		rssItem.setImageUrl(links.size() > 0 ? links.get(0) : "");
	                	}
					}
				}
				rssItem.setDefTitle(defTitle);
				rssItem.setDefDescription(defDescription);
				rssItem.setDefImageUrl(defImageUrl);
				rssItem.setDefLink(defLink);
				items.add(rssItem);
				listener.onProgressUpdate(i*100/nodeList.getLength());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (conn != null) {
			conn.disconnect();
		}
		listener.onLoadingComplete();
		return items;
	}
	
	private List<String> getImageUrls(String text) {
		List<String> list = new ArrayList<String>();
		Pattern p = Pattern.compile(Constants.REGEX_GET_LINK, Pattern.DOTALL | Pattern.CASE_INSENSITIVE );
		Matcher m = p.matcher(text);
		while (m.find()) {
            String link = m.group().replaceAll("\"", "");
            if (link.contains(".jpeg") || link.contains(".jpg") || link.contains(".png")) {
                list.add(link);
            }
        }
		return list;
	}
}