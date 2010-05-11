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
    private int mResourceId;
	private int towerType;
	private float range;
	private float rangeAOE;
	private String title;
	private int price;
	private int resellPrice;
	private int minDamage;
	private int maxDamage;
	private int aoeDamage;
	private int velocity;
	private boolean hasFrostDamage;
	private int frostTime;
	private boolean hasFireDamage;
	private boolean hasPoisonDamage;
	private int poisonDamage;
	private int poisonTime;
	private int upgrade1;
	private int upgrade2;
    private float coolDown;
    private Shot relatedShot;
	private float width;
	private float height;
	private float animationTime;
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
		String tmpStr[] = null; 
		
		try {
			String buf = "";
			while((i = in.read()) != -1){
				char c = (char)i;
				if(c != '\n'){
					buf += c;
				}
				else if(c == '\n'){
					lineNo++;
					switch (lineNo) {
					case 1:
						//Contains info about the file. Do nothing here.
						break;
					case 2: 
		            	tmpStr = buf.split("::");
		            	nbrTwr = Integer.parseInt(tmpStr[1].trim());
		            	towerList = new Tower[nbrTwr];
		            	break;
		            default:
						tmpCount++;
			            if (tmpCount >= 4) {
							tmpStr = buf.split("::");
			            }
						switch (tmpCount) {
			            case 4:
			            	// Load tower texture
			            	mResourceId = context.getResources().getIdentifier(tmpStr[1].trim(), "drawable", context.getPackageName());

			            	// Tower size ALWAYS 60
			            	Coords recalc = scaler.scale(60,60);
			            	width = recalc.getX();
			            	height =  recalc.getY();
			            	break;
			            case 5:  
			            	// Tower title(name)
			            	title = tmpStr[1].trim();
			            	break;
			            case 6:  
			            	// Tower price
			            	price = Integer.parseInt(tmpStr[1].trim());
			            	break;
			            case 7:  
			            	//Tower resell value
			            	resellPrice = Integer.parseInt(tmpStr[1].trim());
			            	break;
			            case 8: 
			            	//Tower minimum damage
			            	minDamage = Integer.parseInt(tmpStr[1].trim());
			            	break;
			            case 9:
			            	//Tower maximum damage
			            	maxDamage = Integer.parseInt(tmpStr[1].trim());
			            	break;
				        case 10:
			            	//Tower velocity of bullets
			            	recalc = scaler.scale(Integer.parseInt(tmpStr[1].trim()),0);
			            	velocity = recalc.getX();
			            	break;
				        case 11:
			            	//Cooldown between each shot
			            	coolDown = Float.valueOf(tmpStr[1].trim());
				        case 12:
			            	// has tower frost damage?
			            	hasFrostDamage = Boolean.parseBoolean(tmpStr[1].trim());
			            	break;
						case 13:
			            	// if tower has frost damage? for how long
			            	frostTime = Integer.parseInt(tmpStr[1].trim());
			            	break;
						case 14:
			            	// has tower firedamage?
			            	hasFireDamage = Boolean.parseBoolean(tmpStr[1].trim());
			            	break;
						case 15:
			            	// has tower posion damage?
							hasPoisonDamage = Boolean.parseBoolean(tmpStr[1].trim());
			            	break;
						case 16:
			            	// Posion damage
			            	poisonDamage = Integer.parseInt(tmpStr[1].trim());
			            	break;
						case 17:
			            	// Posion time
			            	poisonTime = Integer.parseInt(tmpStr[1].trim());
			            	break;
						case 18:
			            	// Towertype
			            	towerType = Integer.parseInt(tmpStr[1].trim());
			            	break;
						case 19:
			            	// 1 upgrade (LEFT)
							upgrade1 = Integer.parseInt(tmpStr[1].trim());
			            	break;
						case 20:
			            	// 2 upgrade (RIGHT)
			            	upgrade2 = Integer.parseInt(tmpStr[1].trim());
			            	break;
						case 21:
			            	// Tower range
			            	recalc = scaler.scale(Integer.parseInt(tmpStr[1].trim()),0);
			            	range = recalc.getX();
			            	break;
						case 22:
			            	// AOE range
			            	recalc = scaler.scale(Integer.parseInt(tmpStr[1].trim()),0);
			            	rangeAOE = recalc.getX();
			            	break;
						case 23:
			            	// AOE damage
			            	aoeDamage = Integer.parseInt(tmpStr[1].trim());
			            	break;
						case 24:
			            	// Shot animation time
			            	animationTime = Float.parseFloat(tmpStr[1].trim());
			            	break;
						case 25:
			            	// Shot texture
			            	resID = context.getResources().getIdentifier(tmpStr[1].trim(), "drawable", context.getPackageName());
			            	// Shot size
			            	recalc = scaler.scale(16,16);
			            	towerList[twrNbr] = new Tower(mResourceId, 0, null, null);
			            	relatedShot = new Shot(resID, 0, towerList[twrNbr]);
			            	relatedShot.setHeight(recalc.getY());
			            	relatedShot.setWidth(recalc.getX());
			            	relatedShot.setAnimationTime(animationTime);
			            	towerList[twrNbr].relatedShot = relatedShot;
			            	towerList[twrNbr].cloneTower(
			            			mResourceId,
			            			towerType,
			            			twrNbr,
					            	range,
					            	rangeAOE,
					            	title,
					            	price,
					            	resellPrice,
					            	minDamage,
					            	maxDamage,
					            	aoeDamage,
					            	velocity,
					            	hasFrostDamage,
					            	frostTime,
					            	hasFireDamage,
					            	hasPoisonDamage,
					            	poisonDamage,
					            	poisonTime,
					            	upgrade1,
					            	upgrade2,
					            	coolDown,
					            	width,
					            	height,
					            	relatedShot
			            			);		
			            	
			            	twrNbr++;
			            	tmpCount = 0;
			            	break;
				        default:
				        	break;
						}
					buf = "";
					break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return towerList;
	}
}
