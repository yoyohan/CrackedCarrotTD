package com.crackedcarrot;



/**
* A class that defines the specific way that each
* creature will take, by adding specific Coords to
* an ArrayList
*/
public class WayPoints {
	
public Coords[] way;
private Scaler res;
	
	public WayPoints(int nrWP, Scaler scale){
		this.res = scale;
		way = new Coords[nrWP];
		
		// Make the specific coordinates
		setWayPoint(100,720,0);
		setWayPoint(100,600,1);
		setWayPoint(300,600,2);
		setWayPoint(300,400,3);
		setWayPoint(100,400,4);
		setWayPoint(100,40,5);
		setWayPoint(440,40,6);
		setWayPoint(440,720,7);
	}
	
	/**
	 * 
	 * @param x
	 * @param i
	 * @param i
	 */
	public void setWayPoint(int x,int y, int i){
		way[i] = res.scale(x,y);
	}
	
	public Coords[] getCoords(){
		return way;
	}
	
	public Coords getFirstWP(){
		return way[0];
	}
	
}