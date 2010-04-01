package com.crackedcarrot;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class PlayerHealthView extends TextView {

	public static PlayerHealthUpdateListener listener = null;
	
	public PlayerHealthView(Context ctx){
		super(ctx);
	}
	
	public PlayerHealthView(Context ctx, AttributeSet attrs){
		super(ctx, attrs);
	}
	
	/** A method that sets the listener for NrCreTextView object */
	public void setPlayerHealthUpdateListener(PlayerHealthUpdateListener l) {
		listener = l;
	}
	
	/** Create an interface to notify an activity when number of enemies is updated */
	public interface PlayerHealthUpdateListener {
		public abstract void playerHealthUpdate(int health);
	}
	
}