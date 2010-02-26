package com.crackedcarrot.fileloader;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.crackedcarrot.Coords;
import com.crackedcarrot.Scaler;
import com.crackedcarrot.Tower;

/**
 * A class that reads the requested towerConf and returns an list of Towers.
 */
public class TowerLoader {
	
	private Context context;
	private InputStream in;
	private Tower[] towerList;
	private Scaler scaler;
	
	/**
	 * Constructor 
	 * 
	 * @param  Context  	The context of the activity that requested the loader.
	 * @param  Scaler	 	A scaler.	
	 */
	public TowerLoader(Context context, Scaler scaler){
		this.context = context;
		this.scaler = scaler;
	}

	/**
	 * Will read a file and turn all the data from the file to list of towers.
	 * <p>
	 * This method is called from GameInit. 
	 *
	 * @param  String	The filename of the requested file
 	 * @return Tower[]  A list of Tower objects			
	 */
	public Tower[] readTowers(String towerFile){
		int resID = context.getResources().getIdentifier(towerFile, "raw", context.getPackageName());
		in = context.getResources().openRawResource(resID);
		int i = 0;
		int lineNo = 0;
		int tmpCount = 0;
		int nbrTwr = 0;
		int twrNbr = 0;
		String tmpStr[]; 
		Tower tmpTwr = null;
		
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
		            	nbrTwr = Integer.parseInt(tmpStr[1].trim());
		            	towerList = new Tower[nbrTwr];
					}
					else{
			            tmpCount++;
			            
			            if (tmpCount == 1) {
				        	// Do nothing. This line contains tower information
				        }
			            else if (tmpCount == 2) {
			            	tmpStr = buf.split("::");
			            	resID = context.getResources().getIdentifier(tmpStr[1].trim(), "drawable", context.getPackageName());
			            	tmpTwr = new Tower(resID);
			            }
			            else if (tmpCount == 3) {
			            	tmpStr = buf.split("::");
			            	tmpTwr.title = tmpStr[1].trim();
			            }
			            else if (tmpCount == 4) {
			            	tmpStr = buf.split("::");
			            	Coords recalc = scaler.scale(Integer.parseInt(tmpStr[1].trim()),0);
			            	tmpTwr.width = recalc.getX();
			            	tmpTwr.height = recalc.getX();
			            }
			            else if (tmpCount == 5) {
			            	tmpStr = buf.split("::");
			            	tmpTwr.price = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 6) {
			            	tmpStr = buf.split("::");
			            	tmpTwr.resellPrice = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 7) {
			            	tmpStr = buf.split("::");
			            	tmpTwr.minDamage = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 8) {
			            	tmpStr = buf.split("::");
			            	tmpTwr.maxDamage = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 9) {
			            	tmpStr = buf.split("::");
			            	Coords recalc = scaler.scale(Integer.parseInt(tmpStr[1].trim()),0);
			            	tmpTwr.velocity = recalc.getX();
			            }
			            else if (tmpCount == 10) {
			            	tmpStr = buf.split("::");
			            	tmpTwr.cooldown = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 11) {
			            	tmpStr = buf.split("::");
			            	tmpTwr.specialAbility = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 12) {
			            	tmpStr = buf.split("::");
			            	tmpTwr.upgrade1 = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 13) {
			            	tmpStr = buf.split("::");
			            	tmpTwr.upgrade2 = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 14) {
			            	tmpStr = buf.split("::");
			            	Coords recalc = scaler.scale(Integer.parseInt(tmpStr[1].trim()),0);
			            	tmpTwr.range = recalc.getX();
			            	towerList[twrNbr] = tmpTwr;
			            	twrNbr++;
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
		
		return towerList;
	}
}
