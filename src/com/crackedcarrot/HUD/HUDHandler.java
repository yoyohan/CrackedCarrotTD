package com.crackedcarrot.HUD;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;

public class HUDHandler extends Thread{
	
	private Grid g;
	public Handler mHandler;
	
	public HUDHandler(int gridResId, Scaler s){
		g = new Grid(gridResId, s);
		
	}
	
	public void run(){
		Looper.prepare();
        
		mHandler = new Handler() {
            public void handleMessage(Message msg) {
                // process incoming messages here
            }
        };
        
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
	
	public Sprite[] getObjectsToRender(){
		Sprite [] rArray = new Sprite[1];
		rArray[0] = g;
		return rArray;
	}
	
}
