package com.crackedcarrot.fileloader;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.crackedcarrot.Coords;
import com.crackedcarrot.Scaler;
import com.crackedcarrot.Shot;
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
			            
			            if (tmpCount == 1 || tmpCount == 2 || tmpCount == 3) {
				        	// Do nothing. This line contains tower information
				        }
			            else if (tmpCount == 4) {
			            	// Load tower texture
			            	tmpStr = buf.split("::");
			            	resID = context.getResources().getIdentifier(tmpStr[1].trim(), "drawable", context.getPackageName());
			            	tmpTwr = new Tower(resID);
			            }
			            else if (tmpCount == 5) {
			            	// Tower title(name)
			            	tmpStr = buf.split("::");
			            	tmpTwr.title = tmpStr[1].trim();
			            }
			            else if (tmpCount == 6) {
			            	// Tower size
			            	tmpStr = buf.split("::");
			            	Coords recalc = scaler.scale(Integer.parseInt(tmpStr[1].trim()),0);
			            	tmpTwr.width = recalc.getX();
			            	tmpTwr.height = recalc.getX();
			            }			            
			            else if (tmpCount == 7) {
			            	// Tower price
			            	tmpStr = buf.split("::");
			            	tmpTwr.price = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 8) {
			            	//Tower resell value
			            	tmpStr = buf.split("::");
			            	tmpTwr.resellPrice = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 9) {
			            	//Tower minimum damage
			            	tmpStr = buf.split("::");
			            	tmpTwr.minDamage = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 10) {
			            	//Tower maximum damage
			            	tmpStr = buf.split("::");
			            	tmpTwr.maxDamage = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 11) {
			            	//Tower velocity of bullets
			            	tmpStr = buf.split("::");
			            	Coords recalc = scaler.scale(Integer.parseInt(tmpStr[1].trim()),0);
			            	tmpTwr.velocity = recalc.getX();
			            }
			            else if (tmpCount == 12) {
			            	//Cooldown between each shot
			            	tmpStr = buf.split("::");
			            	tmpTwr.coolDown = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 13) {
			            	// has tower frost damage?
			            	tmpStr = buf.split("::");
			            	tmpTwr.hasFrostDamage = Boolean.parseBoolean(tmpStr[1].trim());
			            }
			            else if (tmpCount == 14) {
			            	// if tower has frost damage? for how long
			            	tmpStr = buf.split("::");
			            	tmpTwr.frostTime = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 15) {
			            	// has tower firedamage?
			            	tmpStr = buf.split("::");
			            	tmpTwr.hasFireDamage = Boolean.parseBoolean(tmpStr[1].trim());
			            }
			            else if (tmpCount == 16) {
			            	// has tower posion damage?
			            	tmpStr = buf.split("::");
			            	tmpTwr.hasPoisonDamage = Boolean.parseBoolean(tmpStr[1].trim());
			            }
			            else if (tmpCount == 17) {
			            	// Posion damage
			            	tmpStr = buf.split("::");
			            	tmpTwr.poisonDamage = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 18) {
			            	// Posion time
			            	tmpStr = buf.split("::");
			            	tmpTwr.poisonTime = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 19) {
			            	// Towertype
			            	tmpStr = buf.split("::");
			            	tmpTwr.towerType = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 20) {
			            	// 1 upgrade (LEFT)
			            	tmpStr = buf.split("::");
			            	tmpTwr.upgrade1 = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 21) {
			            	// 2 upgrade (RIGHT)
			            	tmpStr = buf.split("::");
			            	tmpTwr.upgrade2 = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 22) {
			            	// Tower range
			            	tmpStr = buf.split("::");
			            	Coords recalc = scaler.scale(Integer.parseInt(tmpStr[1].trim()),0);
			            	tmpTwr.range = recalc.getX();
			            }
			            else if (tmpCount == 23) {
			            	// AOE range
			            	tmpStr = buf.split("::");
			            	Coords recalc = scaler.scale(Integer.parseInt(tmpStr[1].trim()),0);
			            	tmpTwr.rangeAOE = recalc.getX();
			            }
			            else if (tmpCount == 24) {
			            	// AOE damage
			            	tmpStr = buf.split("::");
			            	tmpTwr.aoeDamage = Integer.parseInt(tmpStr[1].trim());
			            }
			            else if (tmpCount == 25) {
			            	// Shot texture
			            	tmpStr = buf.split("::");
			            	resID = context.getResources().getIdentifier(tmpStr[1].trim(), "drawable", context.getPackageName());
			            	tmpTwr.relatedShot = new Shot(resID, tmpTwr);
			            }
			            else if (tmpCount == 26) {
			            	// Shot size
			            	tmpStr = buf.split("::");
			            	Coords recalc = scaler.scale(Integer.parseInt(tmpStr[1].trim()),0);
			            	tmpTwr.relatedShot.width = recalc.getX();
			            	tmpTwr.relatedShot.height = recalc.getX();
			            	towerList[twrNbr] = tmpTwr;
			            	twrNbr++;
			            	tmpCount = 0;
			            }
					}
					buf = "";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return towerList;
	}
}
