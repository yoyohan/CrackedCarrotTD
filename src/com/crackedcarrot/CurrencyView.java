package com.crackedcarrot;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CurrencyView extends TextView {

	public static CurrencyUpdateListener listener = null;
	
	public CurrencyView(Context ctx){
		super(ctx);
	}
	
	public CurrencyView(Context ctx, AttributeSet attrs){
		super(ctx, attrs);
	}
	
	/** A method that sets the listener for NrCreTextView object */
	public void setCurrencyUpdateListener(CurrencyUpdateListener l) {
		listener = l;
	}
	
	/** Create an interface to notify an activity when number of enemies is updated */
	public interface CurrencyUpdateListener {
		public abstract void currencyUpdate(int health);
	}
	
}