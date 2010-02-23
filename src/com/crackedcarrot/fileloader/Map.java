package com.crackedcarrot.fileloader;

import com.crackedcarrot.Sprite;
import com.crackedcarrot.Waypoints;

public class Map {
	private Waypoints points;
	private Sprite bkg;
	
	public Map(Waypoints p /*Sprite bkg*/){
		points = p;
		//this.bkg = bkg;
	}
	
	public Waypoints getWaypoints(){
		return points;
	}
}
