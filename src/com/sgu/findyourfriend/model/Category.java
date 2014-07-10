package com.sgu.findyourfriend.model;

public class Category {
	String Name;
	String ImgResID;
	int News = 0;
	String Title;

	public Category() {
	}

	public Category(String title) {
		this.Title = title;
	}

	public Category(String name, String url, int number) {
		this.Name = name;
		this.ImgResID = url;
		this.News = number;
	}

	public Category(String name, String url) {
		this.Name = name;
		this.ImgResID = url;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getImgResID() {
		return ImgResID;
	}

	public void setImgResID(String imgResID) {
		ImgResID = imgResID;
	}

	public int getNews() {
		return News;
	}

	public void setNews(int news) {
		News = news;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

}
