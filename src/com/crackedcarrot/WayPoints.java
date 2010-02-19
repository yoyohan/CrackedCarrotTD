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
		Coords recalc = res.scale(100,200);
		setWayPoint(recalc.getX(),recalc.getY(),0);
		recalc = res.scale(200,40);
		setWayPoint(recalc.getX(),recalc.getY(),1);
		recalc = res.scale(100,200);
		setWayPoint(recalc.getX(),recalc.getY(),2);
		recalc = res.scale(200,300);
		setWayPoint(recalc.getX(),recalc.getY(),3);
		recalc = res.scale(100,400);
		setWayPoint(recalc.getX(),recalc.getY(),4);
		recalc = res.scale(200,600);
		setWayPoint(recalc.getX(),recalc.getY(),5);
		recalc = res.scale(400,800);
		setWayPoint(recalc.getX(),recalc.getY(),6);
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
	
	public Coords getFirstWP(){
		return way[0];
	}
	
}