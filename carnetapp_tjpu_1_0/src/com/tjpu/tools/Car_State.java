package com.tjpu.tools;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Car_State {
	public String transmissionstate = "บร";
	public String lightsstate = "บร";
	public String engineerstate = "บร";
	public String getTransmissionstate() {
		return transmissionstate;
	}
	public void setTransmissionstate(String transmissionstate) {
		this.transmissionstate = transmissionstate;
	}
	public String getLightsstate() {
		return lightsstate;
	}
	public void setLightsstate(String lightsstate) {
		this.lightsstate = lightsstate;
	}
	public String getEngineerstate() {
		return engineerstate;
	}
	public void setEngineerstate(String engineerstate) {
		this.engineerstate = engineerstate;
	}
	public Car_State(String transmissionstate, String lightsstate,
			String engineerstate) {
		super();
		this.transmissionstate = transmissionstate;
		this.lightsstate = lightsstate;
		this.engineerstate = engineerstate;
	}
	
}
