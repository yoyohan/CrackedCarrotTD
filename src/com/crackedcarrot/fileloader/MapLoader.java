package com.crackedcarrot.fileloader;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

import com.crackedcarrot.Coords;
import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;
import com.crackedcarrot.Waypoints;


/**
 * A class that reads the requested mapfile and returns an map object.
 */
public class MapLoader {
	
	private Context context;
	private InputStream in;
	private Scaler s;
	private Waypoints wps;
	private Sprite[] bckgrd;
	private TowerGrid[][] twg;

	/**
	 * Constructor 
	 * 
	 * @param  Context  	The context of the activity that requested the maploader.
	 * @param  Scaler	 	A scaler for the waypoint.	
	 */
	public MapLoader(Context context, Scaler s){
		this.context = context;
		this.s = s;
		twg = new TowerGrid[8][11];
		for (int x = 0; x < twg.length; x++) {
			for (int y = 0; y < twg[0].length; y++) {
				twg[x][y] = new TowerGrid();
			}
		}
	}

	/**
	 * Will read a file and turn all the data from the file to a Map object
	 * <p>
	 * This method is called from GameInit. 
	 *
	 * @param  String	The filename of the requested lvl
 	 * @return Map     	A new map object			
	 */
	public Map readLevel(String lvlFile){
		int resID = context.getResources().getIdentifier(lvlFile, "raw", context.getPackageName());
		in = context.getResources().openRawResource(resID);
		int i = 0;
		int lineNo = 0;
		
		try {
			String buf = "";
			while((i = in.read()) != -1){
				char c = (char)i;
				if(c != '\n'){
					buf += c;
				}
				else if(c == '\n'){
					lineNo++;
					if(lineNo <= 3){
						//Contains info about the level. Do nothing here.
					}
					else if(lineNo == 4){
						resID = context.getResources().getIdentifier(buf.trim(), "drawable", context.getPackageName());
						
						//If no map was found
						if (resID == 0)
							return null;

				        // Gamemap
						Sprite background = new Sprite(resID);
						//BitmapDrawable backgroundImage = (BitmapDrawable)context.getResources().getDrawable(R.drawable.background2);
				        //Bitmap backgoundBitmap = backgroundImage.getBitmap();
				        background.width = s.getScreenResolutionX();
				        background.height = s.getScreenResolutionY();
				        bckgrd = new Sprite[1];
				        bckgrd[0] = background;
					}
					else if(lineNo == 5){
						// Nbr of waypoints
						wps = new Waypoints(Integer.parseInt(buf.trim()), s);
					}
					else{
						//Each waypoint
						String[] wp = buf.split(",");
						int tmpgridx = Integer.parseInt(wp[0].trim());
						int tmpgridy = Integer.parseInt(wp[1].trim());
						int wpNbr = Integer.parseInt(wp[2].trim());
						Coords tmpCoord = s.getPosFromGrid(tmpgridx, tmpgridy);
						wps.way[wpNbr] = tmpCoord;
						
						Log.d("TEST",""+wps.way[wpNbr].x+";"+wps.way[wpNbr].y);
						if (wpNbr != 0) {						
							Coords cp = wps.way[wpNbr-1];
							cp = s.getGridXandY(cp.x, cp.y);
							
							while (!(cp.y == tmpgridy && cp.x == tmpgridx)){
								if(tmpgridx > cp.x){
						    		cp.x = cp.x + 1;
								}
								else if (tmpgridx < cp.x) {
						    		cp.x = cp.x -1;
						    	}
								else if(cp.y > tmpgridy){
						    		cp.y = cp.y - 1;
						    	}
						    	else if (cp.y < tmpgridy) {
						    		cp.y = cp.y + 1;
						    	}
								twg[cp.x][cp.y].empty = false;
							}
						}
					}
					buf = "";
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return new Map(wps,bckgrd,twg,s);
	}
}
