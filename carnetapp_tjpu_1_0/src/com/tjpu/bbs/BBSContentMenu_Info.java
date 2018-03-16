package com.tjpu.bbs;
/**
 * 
 * @author jinmaoxu
 *
 */
public class BBSContentMenu_Info {
	private int id;
	private int menu_id;
	private int user_id;
	private String user_logo_path;
	private String user_nickname;
	private String contentmenu_time;
	private String contentmenu_title;
	private String img_path;
	public BBSContentMenu_Info(int id, int menu_id, int user_id,
			String user_logo_path, String user_nickname,
			String contentmenu_time, String contentmenu_title, String img_path) {
		super();
		this.id = id;
		this.menu_id = menu_id;
		this.user_id = user_id;
		this.user_logo_path = user_logo_path;
		this.user_nickname = user_nickname;
		this.contentmenu_time = contentmenu_time;
		this.contentmenu_title = contentmenu_title;
		this.img_path = img_path;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMenu_id() {
		return menu_id;
	}
	public void setMenu_id(int menu_id) {
		this.menu_id = menu_id;
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
	public String getContentmenu_time() {
		return contentmenu_time;
	}
	public void setContentmenu_time(String contentmenu_time) {
		this.contentmenu_time = contentmenu_time;
	}
	public String getContentmenu_title() {
		return contentmenu_title;
	}
	public void setContentmenu_title(String contentmenu_title) {
		this.contentmenu_title = contentmenu_title;
	}
	public String getImg_path() {
		return img_path;
	}
	public void setImg_path(String img_path) {
		this.img_path = img_path;
	}
	@Override
	public String toString() {
		return "BBSContentMenu_Info [id=" + id + ", menu_id=" + menu_id
				+ ", user_id=" + user_id + ", user_logo_path=" + user_logo_path
				+ ", user_nickname=" + user_nickname + ", contentmenu_time="
				+ contentmenu_time + ", contentmenu_title=" + contentmenu_title
				+ ", img_path=" + img_path + "]";
	}
	

}
