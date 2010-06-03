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
		boolean fireResistant = false;
		boolean poisonResistant = false;
		boolean frostResistant = false;
		boolean fast = false;
		
		if (difficulty == 1) gameDifficulty = 1;
		else if (difficulty == 2) gameDifficulty = 1.5;
		else gameDifficulty = 0.6;
		
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
		            	if (tmpCount != 1)
		            			tmpStr = buf.split("::");
		            			
						switch (tmpCount) {
						case 1:
				        	// Do nothing. This line contains wave info
							break;
						case 2:
			            	resID = context.getResources().getIdentifier(tmpStr[1].trim(), "drawable", context.getPackageName());
			            	tmpLvl = new Level(resID);
							break;
						case 3:
			            	resID = context.getResources().getIdentifier(tmpStr[1].trim(), "drawable", context.getPackageName());
			            	tmpLvl.setDisplayResourceId(resID);
							break;
						case 4:
			            	resID = context.getResources().getIdentifier(tmpStr[1].trim(), "drawable", context.getPackageName());
			            	tmpLvl.setDeadResourceId(resID);
							break;
						case 5:
							tmpLvl.scale = Float.valueOf(tmpStr[1].trim());
							break;
						case 6:
							tmpLvl.creepTitle = tmpStr[1].trim();
							break;
						case 7:
			            	tmpLvl.setHealth(Integer.parseInt(tmpStr[1].trim()));
			            	tmpLvl.setHealth((int)(tmpLvl.getHealth() * gameDifficulty));
							break;
						case 8:
			            	fast = Boolean.parseBoolean(tmpStr[1].trim());

			            	// Define creature size
			            	Coords recalc = scaler.scale(60,60);
			            	tmpLvl.setWidth(recalc.getX());
			            	tmpLvl.setHeight(recalc.getY());

			            	// I will put velocity here
			            	recalc = scaler.scale(60,0);
			        		if (fast)
				            	tmpLvl.setVelocity(recalc.getX()* 1.5f);
			        		else tmpLvl.setVelocity(recalc.getX());
							break;
						case 9:
							fireResistant = (Boolean.parseBoolean(tmpStr[1].trim()));
							break;
						case 10:
							frostResistant = (Boolean.parseBoolean(tmpStr[1].trim()));
							break;
						case 11:
							poisonResistant = (Boolean.parseBoolean(tmpStr[1].trim()));			            	
			            	tmpLvl.setCreatureSpecials(fast,fireResistant,frostResistant,poisonResistant);
			            	break;
						case 12:
			            	tmpLvl.setDamagePerCreep(Integer.parseInt(tmpStr[1].trim()));
							break;
						case 13:
			            	tmpLvl.setGoldValue(Integer.parseInt(tmpStr[1].trim()));
							break;
						case 14:
			            	tmpLvl.nbrCreatures = Integer.parseInt(tmpStr[1].trim());
			            	levelList[lvlNbr] = tmpLvl;
			            	lvlNbr++;
			            	tmpCount = 0;
			            	break;
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
