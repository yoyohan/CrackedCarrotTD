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
		setWayPoint(100,10,0);
		setWayPoint(200,40,1);
		setWayPoint(100,200,2);
		setWayPoint(200,300,3);
		setWayPoint(100,400,4);
		setWayPoint(200,600,5);
		setWayPoint(400,800,6);
		
	}
	
	/**
	 * 
	 * @param coo
	 * @param i
	 */
	public void setWayPoint(int x,int y, int i){
		way[i] = res.scale(x,y);
	}
	
	public Coords[] getCoords(){
		return way;
	}
	
}