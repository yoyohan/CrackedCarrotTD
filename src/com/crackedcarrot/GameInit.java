package com.crackedcarrot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crackedcarrot.HealthProgressBar.ProgressChangeListener;
import com.crackedcarrot.NrCreTextView.CreatureUpdateListener;
import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;
import com.crackedcarrot.fileloader.MapLoader;
import com.crackedcarrot.fileloader.TowerLoader;
import com.crackedcarrot.fileloader.WaveLoader;
import com.crackedcarrot.menu.R;

public class GameInit extends Activity {

    public SurfaceView mGLSurfaceView;
    private GameLoop simulationRuntime;
    private Thread RenderThread;
    private MapLoader mapLoad;
    private ExpandMenu expandMenu = null;
    private HealthProgressBar healthProgressBar;
    private int healthProgress = 100;
    private NrCreTextView nrCreText;
    private int nextLevel_creature;
    private int nextLevel_creatures = 0;

    
    //private int highlightIcon = R.drawable.map_choose;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem restart = menu.add(0, Menu.NONE, 0, "Restart");
        restart.setIcon(R.drawable.restart_key_button);
        MenuItem quit = menu.add(0, Menu.NONE, 0, "Quit");
        quit.setIcon(R.drawable.quit_key_button);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    
    
    static final int DIALOG_NEXTLEVEL_ID = 1;
    static final int DIALOG_WON_ID       = 2;
    static final int DIALOG_LOST_ID      = 3;
    
	/*
	 * Creates our NextLevel-dialog.
	 */
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
    	
	    AlertDialog alertDialog;
    	AlertDialog.Builder builder;
    	Context mContext;
    	LayoutInflater inflater;
    	View layout;
    	TextView text;
    	ImageView image;
    	
	    switch(id) {
	    case DIALOG_NEXTLEVEL_ID:
	    	mContext = this;
	    	inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
	    	layout = inflater.inflate(R.layout.nextlevel,
	    	                               (ViewGroup) findViewById(R.id.layout_root));

	    		// Text for next level goes here.
	    	text = (TextView) layout.findViewById(R.id.NextLevelText);
	    	text.setText("blahblahblah" + nextLevel_creatures);
	    		// And an icon.
	    	image = (ImageView) layout.findViewById(R.id.NextLevelImage);
	    	image.setImageResource(R.drawable.bjoern);

	    	builder = new AlertDialog.Builder(mContext);
	    	builder.setView(layout)
	    	       .setCancelable(true)
	    	       .setPositiveButton("Next Wave!", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                simulationRuntime.nextLevelClick();
	    	           }
	    	       });
	    	alertDialog = builder.create();

	    	dialog = alertDialog;
	    	dialog.setOwnerActivity(this);
	    	break;
	    case DIALOG_WON_ID:
	    	mContext = this;
	    	inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
	    	layout = inflater.inflate(R.layout.levelwon,
	    	                               (ViewGroup) findViewById(R.id.layout_root));

	    	builder = new AlertDialog.Builder(mContext);
	    	builder.setView(layout)
	    	       .setCancelable(true)
	    	       .setPositiveButton("Yeah!", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                dialog.cancel();
	    	           }
	    	       });
	    	alertDialog = builder.create();

	    	dialog = alertDialog;
	    	dialog.setOwnerActivity(this);
	    	break;
	    case DIALOG_LOST_ID:
	    	mContext = this;
	    	inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
	    	layout = inflater.inflate(R.layout.levellost,
	    	                               (ViewGroup) findViewById(R.id.layout_root));

	    	builder = new AlertDialog.Builder(mContext);
	    	builder.setView(layout)
	    	       .setCancelable(true)
	    	       .setPositiveButton("Yeah!", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                dialog.cancel();
	    	           }
	    	       });
	    	alertDialog = builder.create();

	    	dialog = alertDialog;
	    	dialog.setOwnerActivity(this);
	    	break;
	    default:
	    	Log.d("GAMEINIT", "onCreateDialog got unknown dialog id!");
	        dialog = null;
	    }
	    return dialog;
	}

	/*
	 * Creates our NextLevel-dialog.
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
	    switch(id) {
	    case DIALOG_NEXTLEVEL_ID:
	    		// Text for next level goes here.
	    	TextView text = (TextView) dialog.findViewById(R.id.NextLevelText);
	    	text.setText("Way to go! Next level has " + nextLevel_creatures + " creatures.");
	    		// And an icon.
	    	ImageView image = (ImageView) dialog.findViewById(R.id.NextLevelImage);
	    	image.setImageResource(nextLevel_creature);

	    	break;
	    default:
	    	Log.d("GAMEINIT", "onPrepareDialog got unknown dialog id!");
	        dialog = null;
	    }
	}
	
	
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	/** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
    	/** Use the xml layout file. The GLSufaceView is declared in this */
    	setContentView(R.layout.gameinit);
    	
    	/** Create objects of GLSurfaceView, NativeRender and the two objects
    	 *  that are used for define the pixel resolution of current display;
    	 *  DisplayMetrics & Scaler */
        mGLSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        NativeRender nativeRenderer = new NativeRender(this, mGLSurfaceView);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Scaler res= new Scaler(dm.widthPixels, dm.heightPixels);
        
        /** Create the progress bar, showing the enemies total health*/
        healthProgressBar = (HealthProgressBar)findViewById(R.id.health_progress);
        healthProgressBar.setMax(healthProgress);
        healthProgressBar.setProgress(healthProgress);
        healthProgressBar.setProgressChangeListener(new ProgressChangeListener(){
        	//@Override
        	public void progressUpdate(int health){
        		healthProgressBar.setProgress(health);
        	}
        });
        
        /** Create the TextView showing number of enemies left and add a listener to it */
        nrCreText = (NrCreTextView) findViewById(R.id.nrEnemyLeft);
        nrCreText.setCreatureUpdateListener(new CreatureUpdateListener() {
        	//@Override
        	public void creatureUpdate(int number){
        		nrCreText.setText("" + number);
        	}
        });
        
        
        /** Create the expandable menu */
        expandMenu =(ExpandMenu)findViewById(R.id.expand_menu);
        
        /** Listeners for the five icons in the in-game menu.
         *  When clicked on, it's possible to place a tower
         *  on an empty space on the map. The first button
         *  expands the menu. */
        Button inMenu1 = (Button)findViewById(R.id.inmenu1);
        inMenu1.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		expandMenu.switchMenu();
        	}
        });
        /**final OnTouchListener o = new View.OnTouchListener() {
			
			public boolean onTouch(View v1, MotionEvent event){
				v1.setBackgroundResource(R.drawable.inmenu2_button);
				o = null;
				return true;
			}
		}; */
        final Button inMenu2 = (Button)findViewById(R.id.inmenu2);
        inMenu2.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		// A tower of type 1 has been chosen, where to put it?
        		/**inMenu2.setBackgroundResource(R.drawable.icon_selected);
        		v.setOnTouchListener(o); */
        	}
        });
        Button inMenu3 = (Button)findViewById(R.id.inmenu3);
        inMenu3.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		// A tower of type 2 has been chosen, where to put it?
        	}
        });
        Button inMenu4 = (Button)findViewById(R.id.inmenu4);
        inMenu4.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		// A tower of type 3 has been chosen, where to put it?
        	}
        });
        Button inMenu5 = (Button)findViewById(R.id.inmenu5);
        inMenu5.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		// A tower of type 4 has been chosen, where to put it?
        	}
        });
        Button inMenu6 = (Button)findViewById(R.id.inmenu6);
        inMenu6.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		expandMenu.switchMenu();
        	}
        });
        
        // Fetch information from previous intent. The information will contain the
        // map and difficulty decided by the player.
        Bundle extras  = getIntent().getExtras();
        int levelChoice = 0;
        int difficulty = 0;
        if(extras != null) {
        	levelChoice = extras.getInt("com.crackedcarrot.menu.levelVal");
        	difficulty = extras.getInt("com.crackedcarrot.menu.dificultVal");
        }        
        
        // Create the map requested by the player
        mapLoad = new MapLoader(this,res);
        Map gameMap = null;
        if (levelChoice == 0) 
        	gameMap = mapLoad.readLevel("level1");
        else if (levelChoice == 1)
        	gameMap = mapLoad.readLevel("level2");
        else
        	gameMap = mapLoad.readLevel("level3");

        //Define player specific variables depending on difficulty.
        Player p;
        if (difficulty == 2) {
        	p = new Player(difficulty, 50, 100, 1000);
        }
        else if (difficulty == 1) {
        	p = new Player(difficulty, 40, 100, 1000);
        }
        else {
        	p = new Player(difficulty, 1, 100, 1000);
        }
        
      //Load the creature waves and apply the correct difficulty
        WaveLoader waveLoad = new WaveLoader(this,res);
        Level[] waveList  = waveLoad.readWave("wave1",difficulty);
        
        // Load all available towers and the shots related to the tower
        TowerLoader towerLoad = new TowerLoader(this,res);
        Tower[] tTypes  = towerLoad.readTowers("towers");
        
    	// Sending data to GAMELOOP
        simulationRuntime = new GameLoop(nativeRenderer,gameMap,waveList,tTypes,p,nextLevelHandler,new SoundManager(getBaseContext()));
        RenderThread = new Thread(simulationRuntime);
        
        mGLSurfaceView.setRenderer(nativeRenderer);        
        
        mGLSurfaceView.setSimulationRuntime(simulationRuntime);

        // Start GameLoop
        RenderThread.start();
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }
    
    protected void onStop() {
    	simulationRuntime.stopGameLoop();
    	super.onStop();
    }

    	// This is used to handle calls from the GameLoop to show
    	// our dialogs.
    private Handler nextLevelHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
            	 case DIALOG_NEXTLEVEL_ID:
            		 nextLevel_creature  = msg.arg2;
            		 nextLevel_creatures = msg.arg1;
                     showDialog(1);
            		 break;
            	 case DIALOG_WON_ID:
            		 showDialog(2);
            	 case DIALOG_LOST_ID:
            		 showDialog(3);
            	 default:
                     Log.e("GAMEINIT", "nextLevelHandler error, msg.what = " + msg.what);
                     break;
            }
        }
    }; 

}