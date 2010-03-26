package com.crackedcarrot.menu;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crackedcarrot.GameInit;

/**
 * This class/activity consists of three clickable objects, two buttons that
 * switches between the existing maps with belonging description, and the map image
 * that starts the game loop when clicked on. It sends a level variable to the game
 * loop the game loads the data depending on which level the user has chosen.
 */
public class MapOp extends Activity {
	
	/** The three different menu items */
	private static final int LEVEL1_MENU_ITEM = Menu.FIRST;
	private static final int LEVEL2_MENU_ITEM = LEVEL1_MENU_ITEM + 1;
	private static final int LEVEL3_MENU_ITEM = LEVEL2_MENU_ITEM + 1;
	
	/** References to our images */
    private Integer[] mmaps = {
            R.drawable.map_choose, R.drawable.map_choose2,
            R.drawable.map_choose3};
    /** The index for our "maps" array */
    private int indexMaps = 0;
    /** Representing the users choice of level */
    private int level = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapop);

        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        /** identifying the image views and text view, 
         *  these are the ones that will be set. */
        final ImageView im = (ImageView) findViewById(R.id.image_choose);
        final TextView tv = (TextView) this.findViewById(R.id.maptext);
        final ImageView im2 = (ImageView) findViewById(R.id.lev_choose1);
        
        /** Register the ImageView for a contextMenu */
        registerForContextMenu(im2);
        
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
                		tv.setText("Map 1: The field of grass.");
                		break;	
                	case 1: 
                		tv.setText("Map 2: The field of longer grass.");
                		break;
                	case 2: 
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
        		if(indexMaps > 2){
        			indexMaps = 0;
        		}
                im.setImageResource(mmaps[indexMaps]);
                //Set the text belonging to the current map  
                switch(indexMaps){
                	case 0: 
                		tv.setText("Map 1: The field of grass.");
                		break;	
                	case 1: 
                		tv.setText("Map 2: The field of longer grass.");
                		break;
                	case 2: 
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
        		StartGame2.putExtra("com.crackedcarrot.menu.levelVal", indexMaps);
        		StartGame2.putExtra("com.crackedcarrot.menu.dificultVal", level);
        		startActivity(StartGame2);
        	}
        });
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case LEVEL1_MENU_ITEM:
			this.level = 1;
			showMsg("Level 1");
			return true;
		case LEVEL2_MENU_ITEM:
			this.level = 2;
			showMsg("Level 2");
			return true;
		case LEVEL3_MENU_ITEM:
			this.level = 3;
			showMsg("Level 3");
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Choose level");
		menu.add(0, LEVEL1_MENU_ITEM, 0, "Level 1");
		menu.add(0, LEVEL2_MENU_ITEM, 1, "Level 2");
		menu.add(0, LEVEL3_MENU_ITEM, 2, "Level 3");
	}

	private void showMsg(String message) {
		Toast msg = Toast.makeText(MapOp.this, message, Toast.LENGTH_LONG);
		msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2,
				msg.getYOffset() / 2);
		msg.show();
	}

}