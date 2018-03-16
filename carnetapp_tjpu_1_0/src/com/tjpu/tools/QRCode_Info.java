package com.tjpu.tools;

/**
 * 
 * @author jinmaoxu
 *
 */
public class QRCode_Info {
	String user_id;
	String stationname;
	String ordertime;
	String longtitude;
	String latitude;
	String gastype;
	String gasnumber;
	String QR_Code_Path;
	String id;
	public QRCode_Info(String user_id, String stationname, String ordertime,
			String longtitude, String latitude, String gastype,
			String gasnumber, String qR_Code_Path, String id) {
		super();
		this.user_id = user_id;
		this.stationname = stationname;
		this.ordertime = ordertime;
		this.longtitude = longtitude;
		this.latitude = latitude;
		this.gastype = gastype;
		this.gasnumber = gasnumber;
		QR_Code_Path = qR_Code_Path;
		this.id = id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getStationname() {
		return stationname;
	}
	public void setStationname(String stationname) {
		this.stationname = stationname;
	}
	public String getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}
	public String getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getGastype() {
		return gastype;
	}
	public void setGastype(String gastype) {
		this.gastype = gastype;
	}
	public String getGasnumber() {
		return gasnumber;
	}
	public void setGasnumber(String gasnumber) {
		this.gasnumber = gasnumber;
	}
	public String getQR_Code_Path() {
		return QR_Code_Path;
	}
	public void setQR_Code_Path(String qR_Code_Path) {
		QR_Code_Path = qR_Code_Path;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
