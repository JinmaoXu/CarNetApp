package com.tjpu.mainmenu;

import java.util.ArrayList;

import com.tjpu.mycar.Mycar_Menu_fr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;


public class FragmentVPAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragments; 
	private FragmentManager fm;
	public FragmentVPAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
		super(fm);
		this.fm = fm;
		this.fragments = fragments;
	}
	
	public void setFragments(ArrayList<Fragment> fragments) {
		if(this.fragments != null){
			FragmentTransaction ft = fm.beginTransaction();
			for(Fragment f:this.fragments){
				ft.remove(f);
			}
			ft.commit();
			ft=null;
			fm.executePendingTransactions();
		}
		this.fragments = fragments;
		this.notifyDataSetChanged();
	}
	
	@Override  
	public int getItemPosition(Object object) {  
	    return POSITION_NONE;  
	}  

	@Override
	public Fragment getItem(int arg0) {
		
		return fragments.get(arg0);
	}
	
	@Override
	public int getCount() {
		return fragments.size();
	}
	
}
