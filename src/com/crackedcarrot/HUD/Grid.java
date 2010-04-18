package com.crackedcarrot.HUD;

import android.os.SystemClock;

import com.crackedcarrot.NativeRender;
import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;

public class Grid extends Sprite{
	
	private long startTime;
	private long timeIncrement;
	private long currentTime;
	private long lastUpdateTime;
	
	public Grid(int resourceId, Scaler s){
		//The grid only has one subtype, and one frame. Magical constants for the win.
		super(resourceId, NativeRender.HUD, 0);
		this.x = 0; this.y = 0; this.z = 0;
		this.setWidth(s.getScreenResolutionX());
        this.setHeight(s.getScreenResolutionY());
        this.draw = false;
        this.opacity = 0.0f;
		setType(NativeRender.HUD, 0);
	}
	
	public Thread Show = new Thread(){
		@Override
		public void run() {
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
	
	public Thread Hide = new Thread(){
		@Override
		public void run() {
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
		}
	};
}
