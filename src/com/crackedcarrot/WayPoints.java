package com.crackedcarrot;


/**
* A class that defines the specific way that each
* creature will take, by adding specific Coords to
* an ArrayList
*/
public class WayPoints {
	
public Coords[] way;
	
	public WayPoints(int nrWP){
		
		way = new Coords[nrWP];
		
		// Make the specific coordinates
		Coords coord1 = new Coords(10,10);
		Coords coord2 = new Coords(10,40);
		Coords coord3 = new Coords(10,80);
		Coords coord4 = new Coords(10,120);
		Coords coord5 = new Coords(10,160);
		Coords coord6 = new Coords(10,200);
		Coords coord7 = new Coords(10,220);
		
		// Add the coordinates to the array
		way[0] = coord1;
		way[2] = coord2;
		way[3] = coord3;
		way[4] = coord4;
		way[5] = coord5;
		way[6] = coord6;
		way[7] = coord7;
		
	}
	
	/**
	 * 
	 * @param coo
	 * @param i
	 */
	public void setWayPoint(Coords coo, int i){
		way[i] = coo;
	}
	
}