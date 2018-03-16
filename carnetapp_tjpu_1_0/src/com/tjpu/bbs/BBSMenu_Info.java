package com.tjpu.bbs;
/**
 * 
 * @author jinmaoxu
 *
 */
public class BBSMenu_Info {
	private String str_id;
	private String group_name;
	private String img_path;
	public BBSMenu_Info(String str_id, String group_name, String img_path) {
		super();
		this.str_id = str_id;
		this.group_name = group_name;
		this.img_path = img_path;
	}
	public String getStr_id() {
		return str_id;
	}
	public void setStr_id(String str_id) {
		this.str_id = str_id;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getImg_path() {
		return img_path;
	}
	public void setImg_path(String img_path) {
		this.img_path = img_path;
	}
	@Override
	public String toString() {
		return "BBSMenu_Info [str_id=" + str_id + ", group_name=" + group_name
				+ ", img_path=" + img_path + "]";
	}
	
}
