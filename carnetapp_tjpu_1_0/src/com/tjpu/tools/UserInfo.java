package com.tjpu.tools;
/**
 * 
 * @author jinmaoxu
 *
 */
public class UserInfo {
	private int id;
	private String user_account;
	private String user_pwd;
	private String user_name;
	private String user_sex;
	private String user_nickname;
	private String user_address;
	private String user_logo_path;
	private String user_age;

	public UserInfo(int id, String user_account, String user_pwd,
			String user_name, String user_sex, String user_nickname,
			String user_address, String user_logo_path, String user_age) {
		super();
		this.id = id;
		this.user_account = user_account;
		this.user_pwd = user_pwd;
		this.user_name = user_name;
		this.user_sex = user_sex;
		this.user_nickname = user_nickname;
		this.user_address = user_address;
		this.user_logo_path = user_logo_path;
		this.user_age = user_age;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUser_account() {
		return user_account;
	}

	public void setUser_account(String user_account) {
		this.user_account = user_account;
	}

	public String getUser_pwd() {
		return user_pwd;
	}

	public void setUser_pwd(String user_pwd) {
		this.user_pwd = user_pwd;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_sex() {
		return user_sex;
	}

	public void setUser_sex(String user_sex) {
		this.user_sex = user_sex;
	}

	public String getUser_nickname() {
		return user_nickname;
	}

	public void setUser_nickname(String user_nickname) {
		this.user_nickname = user_nickname;
	}

	public String getUser_address() {
		return user_address;
	}

	public void setUser_address(String user_address) {
		this.user_address = user_address;
	}

	public String getUser_logo_path() {
		return user_logo_path;
	}

	public void setUser_logo_path(String user_logo_path) {
		this.user_logo_path = user_logo_path;
	}

	public String getUser_age() {
		return user_age;
	}

	public void setUser_age(String user_age) {
		this.user_age = user_age;
	}

}

