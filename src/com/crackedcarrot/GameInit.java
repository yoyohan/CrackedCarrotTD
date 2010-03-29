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
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crackedcarrot.CurrencyView.CurrencyUpdateListener;
import com.crackedcarrot.EnemyImageView.EnemyUpdateListener;
import com.crackedcarrot.HealthProgressBar.ProgressChangeListener;
import com.crackedcarrot.LevelInstrView.LevelInstrUpdateListener;
import com.crackedcarrot.NrCreTextView.CreatureUpdateListener;
import com.crackedcarrot.PlayerHealthView.PlayerHealthUpdateListener;
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
    private PlayerHealthView playerHealthView;
    private CurrencyView currencyView;
    private HealthProgressBar healthProgressBar;
    private int healthProgress = 100;
    private NrCreTextView nrCreText;
    private EnemyImageView enImView;
    private LevelInstrView levelInstrView;
    //private int nextLevel_creatures = 0;
    private Dialog dialog = null;
    
    //private int highlightIcon = R.drawable.map_choose;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuItem sound = menu.add(0, Menu.NONE, 0, "Sound");
    	sound.setIcon(R.drawable.button_sound_on);
        MenuItem quit = menu.add(0, Menu.NONE, 0, "Quit");
        quit.setIcon(R.drawable.button_quit);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	MenuItem sound = menu.getItem(0);
		if (simulationRuntime.soundManager.playSound)
	    	sound.setIcon(R.drawable.button_sound_off);
		else
	    	sound.setIcon(R.drawable.button_sound_on);
    	
    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getTitle().toString().startsWith("Sound")) {
    		if (simulationRuntime.soundManager.playSound)
    			simulationRuntime.soundManager.playSound = false;
    		else
    			simulationRuntime.soundManager.playSound = true;
    	}

        return false;
    }

    
    
    final int DIALOG_NEXTLEVEL_ID = 1;
    final int DIALOG_WON_ID       = 2;
    final int DIALOG_LOST_ID      = 3;
    final int DIALOG_UPGRADE_ID   = 4;
    
	/*
	 * Creates our NextLevel-dialog.
	 */
	protected Dialog onCreateDialog(int id) {

    	
	    AlertDialog alertDialog;
    	AlertDialog.Builder builder;
    	Context mContext;
    	LayoutInflater inflater;
    	View layout;
    	//TextView text;
    	//ImageView image;
    	
	    switch(id) {
	    case DIALOG_NEXTLEVEL_ID:
	    	dialog = new Dialog(this,R.style.NextlevelTheme);
	        dialog.setContentView(R.layout.nextlevel);
	    	dialog.setOwnerActivity(this);	    	
	    	
	    	// A button	    	
	    	Button butt= (Button) dialog.findViewById(R.id.NextLevelButton);
	    	butt.setOnClickListener(
	    			new View.OnClickListener() {
	    				public void onClick(View v) {
	    					dialog.dismiss();

				    }
				});
	    	
	    	dialog.setOnDismissListener(
	    			new DialogInterface.OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {
							simulationRuntime.nextLevelClick();
						}
	    			});
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
	    	
	    	break;
	    case DIALOG_UPGRADE_ID:
	    	mContext = this;
	    	inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
	    	layout = inflater.inflate(R.layout.upgradetower,
	    	                               (ViewGroup) findViewById(R.id.layout_root));

	    	builder = new AlertDialog.Builder(mContext);
	    	builder.setView(layout)
	    	       .setCancelable(true)
	    	       ;

	    	/*
	        Button upgradeButton1 = (Button) findViewById(R.id.UpgradeTower1);
	        upgradeButton1.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		Log.d("TEST", "test");
	        	}
	        });

	        Button upgradeButton2 = (Button) findViewById(R.id.UpgradeTower2);
	        upgradeButton2.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		simulationRuntime.upgradeTower(1);
	        	}
	        });
	        
	        Button upgradeButton3 = (Button) findViewById(R.id.UpgradeTower3);
	        upgradeButton3.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		//dialog.cancel();
	        	}
	        });
	        */
	    	
	    	alertDialog = builder.create();
	    	dialog = alertDialog;

	    		// This will remove the fading effect of the dialog.
	    		// It's not suitable for upgrading towers to dim the screen...
	    	WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
	    	dialog.getWindow().setAttributes(lp); // sets the updated windows attributes
	    	dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	    	
	    	break;
	    default:
	    	Log.d("GAMEINIT", "onCreateDialog got unknown dialog id: " + id);
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

	    	int currLvlnbr = simulationRuntime.getLvlNumber() +1;
	    	Level currLvl = simulationRuntime.getLevelData();

	    	// Title:
	    	TextView title = (TextView) dialog.findViewById(R.id.NextLevelTitle);
	    	String titleText ="<b>Level " + currLvlnbr + "</b><br>" + currLvl.creepTitle +"<br>";
		    CharSequence styledText = Html.fromHtml(titleText);
	    	title.setText(styledText);

    		// And an icon.
	    	ImageView image = (ImageView) dialog.findViewById(R.id.NextLevelImage);
	    	image.setImageResource(currLvl.getResourceId());
	    	
	    	// Text for next level goes here.
	    	TextView text = (TextView) dialog.findViewById(R.id.NextLevelText);
	    	Player currPlayer = simulationRuntime.getPlayerData();
	    	String lvlText ="<b>Number of creeps:</b> " + currLvl.nbrCreatures +"<br>";
	    	lvlText += 		"<b>Bounty:</b> " + currLvl.goldValue + "g/creep<br>";
	    	lvlText += 		"<b>Health:</b> " + (int)currLvl.getHealth() + "hp/creep<br>";
	    	lvlText += 		"<br>";
	    	lvlText += 		"<b>Special abillites:</b><br>";
	    	int tmpAbil = 0;
	    	if (currLvl.creatureFast) {
		    	lvlText += 		"<font color=yellow>Fast level</font><br>";
		    	tmpAbil++;
	    	}
		    if (currLvl.creatureFireResistant) {
		    	lvlText += 		"<font color=red>Fire resistant</font><br>";
		    	tmpAbil++;
		    }
		    if (currLvl.creatureFrostResistant) {
		    	lvlText += 		"<font color=blue>Frost resistant</font><br>";
		    	tmpAbil++;
		    }
		    if (currLvl.creaturePoisonResistant) {
		    	lvlText += 		"<font color=green>Posion resistant</font><br>";
		    	tmpAbil++;
		    }
		    if (tmpAbil == 0)
		    	lvlText += 		"No special abbilities<br>";
		    
		    if (currLvlnbr > 1) {
		    	lvlText += 		"<br><b>Previous level:</b><br>";
		    	lvlText += 		"Interest gained:" + currPlayer.getInterestGainedThisLvl() + "<br>";
		    	lvlText += 		"Health lost:" + currPlayer.getHealthLostThisLvl();		    	
		    	
		    }
		    styledText = Html.fromHtml(lvlText);
		    text.setText(styledText);
	    	break;
	    default:
	    	Log.d("GAMEINIT", "onPrepareDialog got unknown dialog id: " + id);
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
        
        /** Create the text view showing the amount of currency */
        currencyView = (CurrencyView)findViewById(R.id.currency);
        currencyView.setCurrencyUpdateListener(new CurrencyUpdateListener(){
        	//@Override
        	public void currencyUpdate(int currency){
        		currencyView.setText("" + currency);
        	}
        });
        
        /** Create the text view showing a players health */
        playerHealthView = (PlayerHealthView)findViewById(R.id.playerHealth);
        playerHealthView.setPlayerHealthUpdateListener(new PlayerHealthUpdateListener(){
        	//@Override
        	public void playerHealthUpdate(int health){
        		playerHealthView.setText("" + health);
        	}
        });
        
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
        
        /** Create the ImageView showing current creature */
        enImView = (EnemyImageView)findViewById(R.id.enemyImVi);
        enImView.setEnemyUpdateListener(new EnemyUpdateListener() {
        	//@Override
        	public void enemyUpdate(int imageId){
        		enImView.setImageResource(imageId);
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
        
        /** Create the ScrollView showing the level instructions */
        levelInstrView = (LevelInstrView) findViewById(R.id.text_instr);
        levelInstrView.setLevelInstrUpdateListener(new LevelInstrUpdateListener() {
        	//@Override
        	public void levelInstrUpdate(String s){
        		levelInstrView.setText(s);
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
        		mGLSurfaceView.setTowerType(0);
        		/**inMenu2.setBackgroundResource(R.drawable.icon_selected);
        		v.setOnTouchListener(o); */
        	}
        });
        Button inMenu3 = (Button)findViewById(R.id.inmenu3);
        inMenu3.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		// A tower of type 2 has been chosen, where to put it?
        		mGLSurfaceView.setTowerType(1);
        	}
        });
        Button inMenu4 = (Button)findViewById(R.id.inmenu4);
        inMenu4.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		// A tower of type 3 has been chosen, where to put it?
        		mGLSurfaceView.setTowerType(2);
        	}
        });
        Button inMenu5 = (Button)findViewById(R.id.inmenu5);
        inMenu5.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		// A tower of type 4 has been chosen, where to put it?
        		mGLSurfaceView.setTowerType(3);
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
        	p = new Player(difficulty, 60, 100, 1000);
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
                     showDialog(1);
            		 break;
            	 case DIALOG_WON_ID:
            		 showDialog(2);
            		 break;
            	 case DIALOG_LOST_ID:
            		 showDialog(3);
            		 break;
            	 case DIALOG_UPGRADE_ID:
            		 showDialog(4);
            		 break;
            	 default:
                     Log.e("GAMEINIT", "nextLevelHandler error, msg.what = " + msg.what);
                     break;
            }
        }
    }; 

}