package com.tjpu.tools;

import java.io.Serializable;
import java.util.Map;
/**
 * 
 * @author jinmaoxu
 *
 */
public class SerializableMap implements Serializable{
	private Map<String,String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
