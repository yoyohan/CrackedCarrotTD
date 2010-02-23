package com.crackedcarrot.fileloader;

import java.io.IOException;
import java.io.InputStream;

import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;
import com.crackedcarrot.Waypoints;
import com.crackedcarrot.menu.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;



public class MapLoader {
	
	private Context context;
	private int difficulty;
	private int level;
	private InputStream in;
	
	private Scaler s;
	private Sprite bkg;
	private Waypoints wps;
	
	public MapLoader(Context context, Scaler s){
		this.context = context;
		this.s = s;
	}
	
	public Map readLevel(int difficulty, int rid){
	    
		in = context.getResources().openRawResource(rid);
		char c = 0;
		int lineNo = 0;
		
		try {
			StringBuffer buf = new StringBuffer();
			while((c = (char)in.read()) != -1){
				if(c != '\n'){
					buf.append(c);
				}
				else if(c == '\n'){
					lineNo++;
				}
				if(lineNo == 1){
					wps = new Waypoints(Integer.parseInt(buf.toString()), s);
					buf = new StringBuffer();
				}
				else{
					String[] wp = buf.toString().split(",");
					wps.setWaypoint(Integer.parseInt(wp[0]), 
							Integer.parseInt(wp[1]), Integer.parseInt(wp[2]));
					buf = new StringBuffer();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new Map(wps);
	}
}
