package com.crackedcarrot.fileloader;

import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
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
	 * @param  Context  	The context of the activity that requested the map.
	 * @param  Scaler	 	A scaler.	
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
	public Level[] readWave(String waveFile,int difficulty){
		int resID = context.getResources().getIdentifier(waveFile, "raw", context.getPackageName());
		in = context.getResources().openRawResource(resID);
		int i = 0;
		int lineNo = 0;
		int lvlNbr = 0;
		int tmpCount = 0;
		String tmpStr[] = null;
		Level tmpLvl = null;
		double gameDifficulty;
		
		if (difficulty == 2) gameDifficulty = 1;
		if (difficulty == 3) gameDifficulty = 1.2;
		else gameDifficulty = 0.8;
		
		try {
			String buf = "";
			while((i = in.read()) != -1){
				char c = (char)i;
				if(c != '\n'){
					buf += c;
				}
				else if(c == '\n'){
					lineNo++;

					if(lineNo <= 1){
						//Contains info about the file. Do nothing here.
					}
					else if(lineNo == 2){
		            	tmpStr = buf.split("::");
						levelList = new Level[Integer.parseInt(tmpStr[1].trim())];
					}
					else{
			            tmpCount++;
			            if (tmpCount == 1) {
				        	// Do nothing. This line contains wave info
				        }
			            else if (tmpCount == 2) {
			            	tmpStr = buf.split("::");
			            	resID = context.getResources().getIdentifier(tmpStr[1].trim(), "drawable", context.getPackageName());
			            	tmpLvl = new Level(resID);
			            }
			            else if (tmpCount == 3) {
			            	tmpStr = buf.split("::");
			            	resID = context.getResources().getIdentifier(tmpStr[1].trim(), "drawable", context.getPackageName());
			            	tmpLvl.mDeadResourceId = resID;
			            }
			            else if (tmpCount == 4) {
			            	tmpStr = buf.split("::");
			            	Coords recalc = scaler.scale(Integer.parseInt(tmpStr[1].trim()),0);
			            	tmpLvl.width = recalc.getX();
			            	tmpLvl.height = recalc.getX();
			            }
			            else if (tmpCount == 5) {
			            	tmpStr = buf.split("::");
			            	tmpLvl.health = Integer.parseInt(tmpStr[1].trim());
			            	tmpLvl.health = (int)(tmpLvl.health * gameDifficulty);
			            }
			            else if (tmpCount == 6) {
			            	tmpStr = buf.split("::");
			            	tmpLvl.creatureFast = Boolean.parseBoolean(tmpStr[1].trim());
			            	// I will put velocity here
			            	Coords recalc = scaler.scale(30,0);
			        		if (tmpLvl.creatureFast)
				            	tmpLvl.velocity = recalc.getX()* 2;
			        		else tmpLvl.velocity = recalc.getX();
			            }
			            else if (tmpCount == 7) {
			            	tmpStr = buf.split("::");
			            	tmpLvl.creatureFireResistant = Boolean.parseBoolean(tmpStr[1].trim());
			            }
			            else if (tmpCount == 8) {
			            	tmpStr = buf.split("::");
			            	tmpLvl.creatureFrostResistant = Boolean.parseBoolean(tmpStr[1].trim());
			            }
			            else if (tmpCount == 9) {
			            	tmpStr = buf.split("::");
			            	tmpLvl.creaturePoisonResistant = Boolean.parseBoolean(tmpStr[1].trim());
			            }
			            else if (tmpCount == 10) {
			            	tmpStr = buf.split("::");
			            	tmpLvl.goldValue = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 11) {
			            	tmpStr = buf.split("::");
			            	tmpLvl.nbrCreatures = Integer.parseInt(tmpStr[1].trim());
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
