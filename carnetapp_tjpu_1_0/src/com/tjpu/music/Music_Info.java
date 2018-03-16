package com.tjpu.music;

import java.io.Serializable;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Music_Info implements Serializable{
	long id;
	String title;
	String artist;
	long duration;
	long size;
	String url;
	int ismusic;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getIsmusic() {
		return ismusic;
	}
	public void setIsmusic(int ismusic) {
		this.ismusic = ismusic;
	}
	public Music_Info(long id, String title, String artist, long duration,
			long size, String url, int ismusic) {
		super();
		this.id = id;
		this.title = title;
		this.artist = artist;
		this.duration = duration;
		this.size = size;
		this.url = url;
		this.ismusic = ismusic;
	}
	public Music_Info(){
		super();
	}
}
