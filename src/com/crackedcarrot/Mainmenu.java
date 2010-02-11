package com.crackedcarrot;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class Mainmenu extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Scaler res= new Scaler(metrics.widthPixels, metrics.heightPixels);
        Coords recalc = res.scale(290,520);
        TextView tt = (TextView) this.findViewById(R.id.top);
        tt.setText(recalc.getX() + "  :  "+ recalc.getY() + "  |  "+ metrics.widthPixels +"   "+ metrics.heightPixels);
    }
}