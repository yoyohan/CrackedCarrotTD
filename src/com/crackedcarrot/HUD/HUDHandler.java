package com.crackedcarrot.HUD;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;
import com.crackedcarrot.menu.R;

public class HUDHandler extends Thread{
	
	private Grid g;
	private RangeIndicator range;
	
	private Handler mHandler;
	
	public HUDHandler(Scaler s){
		g = new Grid(s);
		range = new RangeIndicator(s);
	}
	
	public void run(){
		Looper.prepare();
        
		mHandler = new Handler();
        
        Looper.loop();

	}
	
	
	public void showGrid(){
		Log.d("HUD","Showing grid.");
		this.mHandler.post(g.getShowRunner());
	}
	
	public void hideGrid(){
		Log.d("HUD","Showing grid.");
		this.mHandler.post(g.getHideRunner());

	}
	
	public void showRangeIndicator(){
		Log.d("HUD","Showing range indicator.");
		this.mHandler.post(range.getShowRunner());
	}
	
	public void hideRangeIndicator(){
		Log.d("HUD","Showing range indicator.");
		this.mHandler.post(range.getShowRunner());
	}
	
	public Sprite[] getObjectsToRender(){
		Sprite [] rArray = new Sprite[2];
		rArray[0] = g;
		rArray[1] = range;
		return rArray;
	}
	
}
