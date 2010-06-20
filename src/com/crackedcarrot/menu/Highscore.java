package com.crackedcarrot.menu;

import java.io.IOException;
import java.io.InputStream;

import com.scoreninja.adapter.ScoreNinjaAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Highscore extends Activity {
	
    private int mapSelected;
	
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;
    
    /** References to our images */
    private Bitmap[] mmaps = {
    		bitmap1,
    		bitmap2,
    		bitmap3,
    };
	
    private ScoreNinjaAdapter scoreNinjaAdapter;

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_highscore);

        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize = 8;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        InputStream is = this.getResources().openRawResource(R.drawable.map1);
        
        try {
        	mmaps[0] = BitmapFactory.decodeStream(is, null, options);
            is = this.getResources().openRawResource(R.drawable.map2);
            mmaps[1] = BitmapFactory.decodeStream(is, null, options);
            is = this.getResources().openRawResource(R.drawable.background);
            mmaps[2] = BitmapFactory.decodeStream(is, null, options);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            	//Skip
            }
        }
    	
        Gallery gallery = (Gallery) findViewById(R.id.GalleryHighscore);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemSelectedListener(gItemSelectedHandler);
        gallery.setSelection((gallery.getCount()/2)+1, true);
        
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Sniglet.ttf");
        
        Button ShowButton = (Button)findViewById(R.id.MainMenuHighscoreButtonShow);
        ShowButton.setTypeface(face);
        ShowButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		show();
        	}
        });
        
        Button OkButton = (Button)findViewById(R.id.MainMenuHighscoreButtonOk);
        OkButton.setTypeface(face);
        OkButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		finish();
        	}
        });

    }

    // Unfortunate API, but you must notify ScoreNinja onActivityResult.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      scoreNinjaAdapter.onActivityResult(
          requestCode, resultCode, data);
    }
    
    
    private void show() {
        if (mapSelected == 1) {
        	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzeroone", "E70411F009D4EDFBAD53DB7BE528BFE2");
        } else if (mapSelected == 2) {
        	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerotwo", "26CCAFB5B609DEB078F18D52778FA70B");
        } else if (mapSelected == 3) {
        	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerothree", "41F4C7AEF5A4DEF7BDC050AEB3EA37FC");
        }
        
        scoreNinjaAdapter.show();
    }

    
    public OnItemSelectedListener gItemSelectedHandler = new
    OnItemSelectedListener() {
       //@Override
        public void onItemSelected(AdapterView<?> parent, View v, int _position, long id) {
     	   _position =  _position%3;
     	   switch(_position){
 				case 0:
 					mapSelected = 1;
 					break;
 				case 1: 
 					mapSelected = 2;
 					break;	
 				case 2: 
 					mapSelected = 3;
 					break;
 			}
 			
        }
         //@Override
         public void onNothingSelected(AdapterView<?> arg0) {
             // TODO Auto-generated method stub
         }
     };
     
     
     private class ImageAdapter extends BaseAdapter 
     {
         private Context context;
         //private int itemBackground;
         public int position;
         private int x;
         private int y;
         
         public ImageAdapter(Context c) {
             context = c;
             x = (int) (110 * getResources().getDisplayMetrics().density);
             y = (int) (165 * getResources().getDisplayMetrics().density);
         }
  
         //---returns the number of images---
         public int getCount() {
         	return 1000;
         }
  
         public Object getItem(int position) {
             return position%3;
         }
  
         public long getItemId(int position) {
             return position%3;
         }
  
         //---returns an ImageView view---
         public View getView(int position, View convertView, ViewGroup parent)
         {
         	this.position = position%3;
         	ImageView imageView = new ImageView(context);
             imageView.setImageBitmap(mmaps[this.position]);
             imageView.setScaleType(ImageView.ScaleType.FIT_XY);
             imageView.setLayoutParams(new Gallery.LayoutParams(x,y));
             imageView.setBackgroundResource(R.drawable.xml_gallery);
             return imageView;
         }
    }
    
}
