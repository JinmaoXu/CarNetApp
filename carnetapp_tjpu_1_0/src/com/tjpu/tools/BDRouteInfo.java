package com.tjpu.tools;

import java.io.Serializable;
/**
 * 
 * @author jinmaoxu
 *
 */
public class BDRouteInfo implements Serializable {
	String name;
	double longtitude;
	double latitude;
	public BDRouteInfo(){
		
	}
	public BDRouteInfo(String name, double longtitude, double latitude) {
		super();
		this.name = name;
		this.longtitude = longtitude;
		this.latitude = latitude;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	

}
