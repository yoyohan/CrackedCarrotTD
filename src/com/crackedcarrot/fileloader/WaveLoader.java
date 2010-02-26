package com.crackedcarrot.fileloader;

import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import android.util.Log;
import com.crackedcarrot.Coords;
import com.crackedcarrot.Scaler;

/**
 * A class that reads the requested waveSet and returns an Level list.
 */
public class WaveLoader {
	
	private Context context;
	private InputStream in;
	private Level[] levelList;
	private Scaler scaler;
	
	/**
	 * Constructor 
	 * 
	 * @param  Context  	The context of the activity that requested the maploader.
	 * @param  Scaler	 	A scaler for the waypoint.	
	 */
	public WaveLoader(Context context, Scaler scaler){
		this.context = context;
		this.scaler = scaler;
	}

	/**
	 * Will read a file and turn all the data from the file to list of Level objects.
	 * <p>
	 * This method is called from GameInit. 
	 *
	 * @param  String	The filename of the requested file
 	 * @return Level[]  A list of Level objects			
	 */
	public Level[] readWave(String waveFile){
		int resID = context.getResources().getIdentifier(waveFile, "raw", context.getPackageName());
		in = context.getResources().openRawResource(resID);
		int i = 0;
		int lineNo = 0;
		int lvlNbr = 0;
		int tmpCount = 0;
		Level tmpLvl = null;
		
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
						//Contains info about the file. Do nothing here.
					}
					else if(lineNo == 4){
						levelList = new Level[Integer.parseInt(buf.trim())];
					}
					else{
			            tmpCount++;
						Log.d("FEL",tmpCount + buf);
			            if (tmpCount == 1) {
				        	// Do nothing. This line contains wave info
				        }
			            else if (tmpCount == 2) {
							resID = context.getResources().getIdentifier(buf.trim(), "drawable", context.getPackageName());
			            	tmpLvl = new Level(resID);
			            }
			            else if (tmpCount == 3) {
			            	Coords recalc = scaler.scale(Integer.parseInt(buf.trim()),0);
			            	tmpLvl.width = recalc.getX();
			            	tmpLvl.height = recalc.getX();
			            	
			            	// I will put velocity here
			            	recalc = scaler.scale(50,0);
			            	tmpLvl.velocity = recalc.getX();
			            }
			            else if (tmpCount == 4) {
			            	tmpLvl.health = Integer.parseInt(buf.trim());
			            }
			            else if (tmpCount == 5) {
			            	tmpLvl.specialAbility = Integer.parseInt(buf.trim());
			            }
			            else if (tmpCount == 6) {
			            	tmpLvl.goldValue = Integer.parseInt(buf.trim());
			            }
			            else if (tmpCount == 7) {
			            	tmpLvl.nbrCreatures = Integer.parseInt(buf.trim());
			            	levelList[lvlNbr] = tmpLvl;
			            	lvlNbr++;
			            	tmpCount = 0;
			            }
					}
					buf = "";
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return levelList;
	}
}
