package com.crackedcarrot.HUD;

import android.util.Log;

import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;

public class HUDHandler extends Thread{
	
	private Grid g;
	
	public HUDHandler(int gridResId, Scaler s){
		g = new Grid(gridResId, s);
		
	}
	
	public void showGrid(){
		Log.d("HUD","Showing grid.");
		 if(!(g.Show.isAlive() || g.Hide.isAlive())){
			 g.Show.start();
		 }
	}
	
	public void hideGrid(){
		Log.d("HUD","Showing grid.");
		 if(!(g.Show.isAlive() || g.Hide.isAlive())){
			 g.Hide.start();
		 }
	}
	
	public Sprite[] getObjectsToRender(){
		Sprite [] rArray = new Sprite[1];
		rArray[0] = g;
		return rArray;
	}
	
}
