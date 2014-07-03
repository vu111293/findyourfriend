package com.example.frindyourfriend.model;

public class FriendRequest {
	String ImageUrl="";
	String Name="";
	int MutualFriends=0;
	public FriendRequest(){}
	public FriendRequest(String name , int mutualfriends,String imgurl){
		this.Name=name;
		this.MutualFriends=mutualfriends;
		this.ImageUrl=imgurl;
	}
	public String getImageUrl() {
		return ImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getMutualFriends() {
		return MutualFriends;
	}
	public void setMutualFriends(int mutualFriends) {
		MutualFriends = mutualFriends;
	}
}
