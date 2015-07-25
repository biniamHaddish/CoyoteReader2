package com.wilee8.coyotereader2.containers;

import org.parceler.Parcel;

@Parcel
public class ArticleItem {
	private String            id;
	private String            title;
	private String            summary;
	private String            author;
	private String            canonical;
	private String            origin;
	private Boolean           starred;
	private Boolean           unread;
	private Boolean           isFooter;

	public ArticleItem() {
		isFooter = false;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCanonical() {
		return canonical;
	}

	public void setCanonical(String canonical) {
		this.canonical = canonical;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getIsFooter() {
		return isFooter;
	}

	public void setIsFooter(Boolean isFooter) {
		this.isFooter = isFooter;
	}

	public Boolean getStarred() {
		return starred;
	}

	public void setStarred(Boolean starred) {
		this.starred = starred;
	}

	public Boolean getUnread() {
		return unread;
	}

	public void setUnread(Boolean unread) {
		this.unread = unread;
	}
}
