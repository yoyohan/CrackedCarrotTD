package com.crackedcarrot;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


public class LevelInstrView extends TextView {

	public static LevelInstrUpdateListener listener = null;
	
	public LevelInstrView(Context ctx){
		super(ctx);
	}
	
	public LevelInstrView(Context ctx, AttributeSet attrs){
		super(ctx, attrs);
	}
	
	/** A method that sets the listener for NrCreTextView object */
	public void setLevelInstrUpdateListener(LevelInstrUpdateListener l) {
		listener = l;
	}
	
	/** Create an interface to notify an activity when number of enemies is updated */
	public interface LevelInstrUpdateListener {
		public abstract void levelInstrUpdate(String s);
	}
	
}