package com.crackedcarrot.fileloader;

import com.crackedcarrot.Sprite;
import com.crackedcarrot.Waypoints;

/**
 * A class for an Map.
 */
public class Map {
	private Waypoints points;
	private Sprite[] bkg;

	/**
	 * Constructor 
	 *
	 * @param  Waypoints 	waypoints that will be used by the Map.
	 * @param  Sprite[]		the background sprite that the Map uses.
	 */
	public Map(Waypoints p, Sprite[] bkg){
		points = p;
		this.bkg = bkg;
	}

	/** 
	 * return all waypoints of this map
	 *
 	 * @return Waypoints
	 */
	public Waypoints getWaypoints(){
		return points;
	}

	/** 
	 * return the background of this map
	 *
 	 * @return Sprite[]
	 */
	public Sprite[] getBackground(){
		return bkg;
	}
}
