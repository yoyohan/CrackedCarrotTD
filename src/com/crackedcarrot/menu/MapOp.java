package com.crackedcarrot.menu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crackedcarrot.GameInit;

/**
 * This class/activity consists of three clickable objects, two buttons that
 * switches between the existing maps with belonging description, and the map image
 * that starts the game loop when clicked on. It sends a level variable to the game
 * loop the game loads the data depending on which level the user has chosen.
 */
public class MapOp extends Activity {
	
	/** References to our images */
    private Integer[] mmaps = {
    		R.drawable.map_resume,
            R.drawable.map_choose,
            R.drawable.map_choose2,
            R.drawable.map_choose3};
    /** The index for our "maps" array */
    private int indexMaps = 1;
    /** Representing the users choice of level */
    private int level = 0;
    
    private int resume;
    
    private ImageView im;
    private TextView  tv;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_startgame);

        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    	
        // See if there's any old game saved that can be resumed.
        SharedPreferences settings = getSharedPreferences("Resume", 0);
        resume = settings.getInt("Resume", 0);
        
        /** identifying the image views and text view, 
         *  these are the ones that will be set. */
        im = (ImageView) findViewById(R.id.image_choose);
        tv = (TextView) this.findViewById(R.id.maptext);
        
        /** Listener for the left button */
        Button LeftButton = (Button)findViewById(R.id.leftbutton);
        LeftButton.setOnClickListener(new OnClickListener() {
        	
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
        /** Listener for the right button */
        Button RightButton = (Button)findViewById(R.id.rightbutton);
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
        /** Listener for the imageView */
        ImageView StartGameButton2 = (ImageView)findViewById(R.id.image_choose);
        StartGameButton2.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		/** Send the level variable to the game loop and start it */
        		Intent StartGame2 = new Intent(v.getContext(),GameInit.class);
        		StartGame2.putExtra("com.crackedcarrot.menu.map", indexMaps);
        		StartGame2.putExtra("com.crackedcarrot.menu.difficulty", level);
        		startActivity(StartGame2);
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

}