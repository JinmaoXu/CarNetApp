package com.tjpu.bbs;
/**
 * 
 * @author jinmaoxu
 *
 */
public class BBSComment_Info {
	private int user_id;
	private String user_logo_path;
	private String user_nickname;
	private String content_time;
	private String img_path;
	private String content_content;
	public BBSComment_Info(int user_id, String user_logo_path,
			String user_nickname, String content_time, String img_path,
			String content_content) {
		super();
		this.user_id = user_id;
		this.user_logo_path = user_logo_path;
		this.user_nickname = user_nickname;
		this.content_time = content_time;
		this.img_path = img_path;
		this.content_content = content_content;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUser_logo_path() {
		return user_logo_path;
	}
	public void setUser_logo_path(String user_logo_path) {
		this.user_logo_path = user_logo_path;
	}
	public String getUser_nickname() {
		return user_nickname;
	}
	public void setUser_nickname(String user_nickname) {
		this.user_nickname = user_nickname;
	}
	public String getContent_time() {
		return content_time;
	}
	public void setContent_time(String content_time) {
		this.content_time = content_time;
	}
	public String getImg_path() {
		return img_path;
	}
	public void setImg_path(String img_path) {
		this.img_path = img_path;
	}
	public String getContent_content() {
		return content_content;
	}
	public void setContent_content(String content_content) {
		this.content_content = content_content;
	}
	@Override
	public String toString() {
		return "BBSComment_Info [user_id=" + user_id + ", user_logo_path="
				+ user_logo_path + ", user_nickname=" + user_nickname
				+ ", content_time=" + content_time + ", img_path=" + img_path
				+ ", content_content=" + content_content + "]";
	}
	
}
