package com.crackedcarrot;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class HealthProgressBar extends ProgressBar {
	
	public static ProgressChangeListener proChangeListener = null;
	
	public HealthProgressBar(Context ctx){
		super(ctx);
	}
	
	public HealthProgressBar(Context ctx, AttributeSet attrs){
		super(ctx, attrs);
	}
	
	/** A method that sets the listener for HealthProgressBar object */
	public void setProgressChangeListener(ProgressChangeListener l) {
		proChangeListener = l;
	}
	
	/** Create an interface to notify an activity when total health is updated */
	public interface ProgressChangeListener {
		public abstract void progressUpdate(int number);
	}
	
}