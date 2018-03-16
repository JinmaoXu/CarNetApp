package com.tjpu.music;


/**
 * 
 * @author jinmaoxu
 *
 */
public interface OnMediaChangeListener {

	public void onMediaPlay();
	public void onMediaPause();
	public void onMediaStop();
	public void onMediaCompletion();
	public void onMediaNextMusicAuto();
}
