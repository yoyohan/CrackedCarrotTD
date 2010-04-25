package com.crackedcarrot.HUD;

import android.os.SystemClock;

import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;
import com.crackedcarrot.menu.R;

public class Grid extends Sprite{
	
	private long startTime;
	private long currentTime;
	private long lastUpdateTime;
	
	private Show showRunner;
	private Hide hideRunner;
	
	public Grid(Scaler s){
		//The grid only has one subtype, and one frame. Magical constants for the win.
		super(R.drawable.grid, OVERLAY, 0);
		this.x = 0; this.y = 0; this.z = 0;
		this.setWidth(s.getScreenResolutionX());
        this.setHeight(s.getScreenResolutionY());
        this.draw = false;
        this.opacity = 0.0f;
		
		showRunner = new Show();
		hideRunner = new Hide();
	}

	public Show getShowRunner() {
		return showRunner;
	}

	public Hide getHideRunner() {
		return hideRunner;
	}

	private class Show implements Runnable{
		//@Override
		public void run() {
			if(draw == true)
				return;
			
			opacity = 0.0f;
			draw = true;
			startTime = SystemClock.uptimeMillis();
			currentTime = SystemClock.uptimeMillis();
			lastUpdateTime = currentTime;
			while((currentTime - startTime) < 500){
				if((currentTime - lastUpdateTime) > 50){
					opacity += 0.1f;
					lastUpdateTime = currentTime;
				}
				SystemClock.sleep(10);
				currentTime = SystemClock.uptimeMillis();
			}
			opacity = 1.0f;
		}
	};
	
	private class Hide implements Runnable{
		//@Override
		public void run() {
			if(draw == false)
				return;
			
			opacity = 1.0f;
			startTime = SystemClock.uptimeMillis();
			currentTime = SystemClock.uptimeMillis();
			lastUpdateTime = currentTime;
			while((currentTime - startTime) < 500){
				if((currentTime - lastUpdateTime) > 50){
					opacity -= 0.1f;
					lastUpdateTime = currentTime;
				}
				SystemClock.sleep(10);
				currentTime = SystemClock.uptimeMillis();
			}
			opacity = 0.0f;
			draw = false;
		}
	};
}