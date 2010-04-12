package com.crackedcarrot.menu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewSwitcher.ViewFactory;

/**
 * This class/activity consists of three clickable objects, two buttons that
 * switches between the existing maps with belonging description, and the map image
 * that starts the game loop when clicked on. It sends a level variable to the game
 * loop the game loads the data depending on which level the user has chosen.
 */
public class MapOp extends Activity implements ViewFactory {
	
	/** References to our images */
    private Integer[] mmaps = {
    		R.drawable.map1,
    		R.drawable.map1,
    		R.drawable.map1,
    		R.drawable.map1
    };
    
    /** The index for our "maps" array */
    private int indexMaps = 1;
    /** Representing the users choice of level */
    private int level = 0;
    
    private int resume;
    
    private ImageView im;
    private TextView  tv;
    private ImageView imageSwitcher;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_startgame);

        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	

    	
    	
        Gallery gallery = (Gallery) findViewById(R.id.gallery1);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(new OnItemClickListener() 
        {
            public void onItemClick(AdapterView parent, 
            View v, int position, long id) 
            {                
            	imageSwitcher.setImageResource(mmaps[position]);
            }
        });

    	// Added by the king
    	imageSwitcher = (ImageView) findViewById(R.id.switcher1);
    	//int pos = ((ImageView)gallery.getFocusedChild());
//  	imageSwitcher.setImageResource(;
//);
    	imageSwitcher.getDrawable().mutate().setAlpha(120);        
        
        // See if there's any old game saved that can be resumed.
        SharedPreferences settings = getSharedPreferences("Resume", 0);
        resume = settings.getInt("Resume", 0);
        
        /** identifying the image views and text view, 
         *  these are the ones that will be set. */
        //im = (ImageView) findViewById(R.id.image_choose);
        tv = (TextView) this.findViewById(R.id.maptext);
        
        /** Listener for the left button */
        //Button LeftButton = (Button)findViewById(R.id.leftbutton);
        /*LeftButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		//Set the correct map depending in the index value (0,1,2)
        		indexMaps--;
        			// Additional map-option only if resume available.
        		if (resume == -1 || resume > 2) {
        			if(indexMaps < 1) {
        				indexMaps = 3;
        			}
        		} else {
        			if(indexMaps < 0) {
        				indexMaps = 3;
        			}
        		}
                im.setImageResource(mmaps[indexMaps]);
                //Set the text belonging to the current map  
                switch(indexMaps){
                	case 0:
                		tv.setText("You have " + (3 - resume) + " resumes left.");
                		break;
                	case 1: 
                		tv.setText("Map 1: The field of grass.");
                		break;	
                	case 2: 
                		tv.setText("Map 2: The field of longer grass.");
                		break;
                	case 3: 
                		tv.setText("Map 3: The field of longest grass.");
                		break;
                	}	
        	}
        });
        */
        /** Listener for the right button */
        /*Button RightButton = (Button)findViewById(R.id.rightbutton);
        RightButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		//Set the correct map depending in the index value (0,1,2)
        		indexMaps++;
        			// Additional map-option only if resume available.
        		if (resume == -1 || resume > 2) {
        			if(indexMaps > 3) {
        				indexMaps = 1;
        			}
        		} else {
        			if(indexMaps > 3) {
        				indexMaps = 0;
        			}
        		}
                im.setImageResource(mmaps[indexMaps]);
                //Set the text belonging to the current map  
                switch(indexMaps){
                	case 0:
                		tv.setText("You have " + (3 - resume) + " resumes left.");
                		break;
                	case 1: 
                		tv.setText("Map 1: The field of grass.");
                		break;	
                	case 2: 
                		tv.setText("Map 2: The field of longer grass.");
                		break;
                	case 3: 
                		tv.setText("Map 3: The field of longest grass.");
                		break;
                	}	
        	}
        });
        */
        
        /** Listener for the imageView */
        /*ImageView StartGameButton2 = (ImageView)findViewById(R.id.image_choose);
        StartGameButton2.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		//Send the level variable to the game loop and start it
        		Intent StartGame2 = new Intent(v.getContext(),GameInit.class);
        		StartGame2.putExtra("com.crackedcarrot.menu.map", indexMaps);
        		StartGame2.putExtra("com.crackedcarrot.menu.difficulty", level);
        		startActivity(StartGame2);
        	}
        });
        */
        
        // Difficulty listeners.
        ImageView easy = (ImageView) findViewById(R.id.StartGameImageViewEasy);
        easy.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		level = 0;
        	}
        });
        
        ImageView normal = (ImageView) findViewById(R.id.StartGameImageViewEasy);
        normal.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		level = 1;
        	}
        });
        
        ImageView hard = (ImageView) findViewById(R.id.StartGameImageViewEasy);
        hard.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		level = 2;
        	}
        });

    }
    
    	// Called when we get focus again (after a game has ended).
    @Override
    public void onRestart() {
        super.onRestart();
        
        // See if there's any old game saved that can be resumed.
        SharedPreferences settings = getSharedPreferences("Resume", 0);
        resume = settings.getInt("Resume", 0);
        
        	// If we have a possible resume show it directly.
		if (resume == -1 || resume > 2) {
        	indexMaps = 1;
        	tv.setText("Map 1: The field of grass.");
        } else {
        	indexMaps = 0;
        	tv.setText("You have " + (3 - resume) + " resumes left.");
        }
        
        	// and update the Map-Image.
        im.setImageResource(mmaps[indexMaps]);
    }

	public View makeView() {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundColor(0xFF000000);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new 
                ImageSwitcher.LayoutParams(
                        LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT));
        return imageView;
	}
	
   public class ImageAdapter extends BaseAdapter 
    {
        private Context context;
        private int itemBackground;
        public int position;
        
        public ImageAdapter(Context c) 
        {
            context = c;
            //---setting the style---                
            TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            itemBackground = a.getResourceId(
                    R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle();                                                    
        }
 
        //---returns the number of images---
        public int getCount() 
        {
            return mmaps.length;
        }
 
        //---returns the ID of an item--- 
        public Object getItem(int position) 
        {
            return position;
        }
 
        public long getItemId(int position) 
        {
            return position;
        }
 
        //---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent)
        {
        	this.position = position;
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(mmaps[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new Gallery.LayoutParams(200, 270));
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
   }    
}