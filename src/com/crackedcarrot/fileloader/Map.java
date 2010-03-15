package com.crackedcarrot.fileloader;

import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;
import com.crackedcarrot.Waypoints;

/**
 * A class for an Map.
 */
public class Map {
	private Waypoints points;
	private Sprite[] bkg;
	private TowerGrid[][] twg;
	private Scaler mapScaler;
	//public int gridSizeX;
	//public int gridSizeY;
	
	/**
	 * Constructor 
	 *
	 * @param  Waypoints 	waypoints that will be used by the Map.
	 * @param  Sprite[]		the background sprite that the Map uses.
	 */
	public Map(Waypoints p, Sprite[] bkg, TowerGrid[][] twg, Scaler mapScaler){
		points = p;
		this.bkg = bkg;
		this.twg = twg;
		//gridSizeX = twg.length;
		//gridSizeY = twg[0].length;
		this.mapScaler = mapScaler;
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

	/** 
	 * return towergrid for this map
	 *
 	 * @return Sprite[]
	 */
	public TowerGrid[][] getTowerGrid() {
		return twg;
	}
	
	/** 
	 * return towergrid for this map
	 *
 	 * @return Sprite[]
	 */
	public Scaler getScaler() {
		return mapScaler;
	}	
}