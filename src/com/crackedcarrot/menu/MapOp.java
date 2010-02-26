package com.crackedcarrot.menu;

import android.app.Activity;
import android.content.Intent;
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
            R.drawable.map_choose, R.drawable.map_choose2,
            R.drawable.map_choose3};
    /** The index for our "maps" array */
    private int indexMaps = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapop);
        
        /** identifying the image view and text view, 
         *  these are the ones that will be set.
         */
        final ImageView im = (ImageView) findViewById(R.id.image_choose);
        final TextView tv = (TextView) findViewById(R.id.maptext);
        
        /** Listener for the left button */
        Button LeftButton = (Button)findViewById(R.id.leftbutton);
        LeftButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		//Set the correct map depending in the index value (0,1,2)
        		indexMaps--;
        		if(indexMaps < 0){
        			indexMaps = 2;
        		}
                im.setImageResource(mmaps[indexMaps]);
                //Set the text belonging to the current map  
                switch(indexMaps){
                	case 0: 
                		tv.setText("Map 1 -> Cool");
                		break;	
                	case 1: 
                		tv.setText("Map 2 -> Soft");
                		break;
                	case 2: 
                		tv.setText("Map 3 -> Booring");
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
        		if(indexMaps > 2){
        			indexMaps = 0;
        		}
                im.setImageResource(mmaps[indexMaps]);
                //Set the text belonging to the current map  
                switch(indexMaps){
                	case 0: 
                		tv.setText("Map 1 -> Cool");
                		break;	
                	case 1: 
                		tv.setText("Map 2 -> Soft");
                		break;
                	case 2: 
                		tv.setText("Map 3 -> Booring");
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
        		StartGame2.putExtra("com.crackedcarrot.menu.levelVal", indexMaps);
        		startActivity(StartGame2);
        	}
        });
    }
}