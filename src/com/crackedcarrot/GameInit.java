package com.crackedcarrot;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;
import com.crackedcarrot.fileloader.MapLoader;
import com.crackedcarrot.fileloader.TowerLoader;
import com.crackedcarrot.fileloader.WaveLoader;
import com.crackedcarrot.menu.R;

public class GameInit extends Activity {

    public GLSurfaceView mGLSurfaceView;
    private GameLoop simulationRuntime;
    private Thread RenderThread;
    private MapLoader mapLoad;

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
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuItem restart = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, "Restart");
		// Apparently Android doesnt support icons in a Context-menu.
		//restart.setIcon(R.drawable.restart_key_button);

		SubMenu quitMenu = menu.addSubMenu("Quit");
		quitMenu.setHeaderTitle("Save?");
		quitMenu.add(Menu.NONE,Menu.NONE,Menu.NONE, "Yes");
		quitMenu.add(Menu.NONE,Menu.NONE,Menu.NONE, "No");
	}
	
	@Override
	public boolean onContextItemSelected (MenuItem item) {
		String title = (String) item.getTitle();
		
		if (title.matches("Restart")) {
			return true;
			
		} else if (title.matches("Quit")) {
			return true;
			
		} else if (title.matches("Yes")) {
			return true;
			
		} else if (title.matches("No")) {
			return true;
			
		} else {
			// This should never happen.
			Log.d("GameInit", "ContextMenu: " + item.getTitle());
			return super.onContextItemSelected(item);
		}
		
	}
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    	
    	/** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
        mGLSurfaceView = new GLSurfaceView(this);
        NativeRender nativeRenderer = new NativeRender(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        // Create Levels;// Will propebly be taken from main menu or something
        //////////////////////////////////        
        Scaler res= new Scaler(dm.widthPixels, dm.heightPixels);
        
        mapLoad = new MapLoader(this,res);
        Map m = mapLoad.readLevel("level1");
        Waypoints w = m.getWaypoints();
        WaveLoader waveLoad = new WaveLoader(this,res);
        Level[] waveList  = waveLoad.readWave("wave1");
        TowerLoader towerLoad = new TowerLoader(this,res);
        Tower[] allTowers  = towerLoad.readTowers("towers");
              
        Coords recalc;
    	int nrCrLvl = 20; //We will start with 20 creatures on every level
        int maxNbrTowers = 1;
    	Creature[] creatureList = new Creature[nrCrLvl]; // Maximum of creatures
    	Tower[] towerList = new Tower[maxNbrTowers];
    	Shot[] shotList = new Shot[maxNbrTowers];
        
        //Creature init. We dont want to send an empty list to addSprite(). This can probably be done in a better way
        for (int i = 0; i < creatureList.length; i++) {
        	Creature tmpCr = new Creature(R.drawable.skate1);
            creatureList[i] = tmpCr;
        }
        
        //Tower init. This for can probably be better
        for (int i = 0; i < maxNbrTowers; i++) {
        	Tower tmpTw = allTowers[0];
        	Shot tmpSh = new Shot(R.drawable.skate3, tmpTw);
        	tmpTw.draw = true; //Tower drawable
        	tmpSh.draw = false; //Shot not drawable until launch
            recalc = res.scale(16,16); //Shot size
        	tmpSh.width = recalc.getX(); //Shot width
            tmpSh.height = recalc.getY(); //Shot height
        	recalc = res.scale(220,300);
        	tmpSh.velocity = tmpTw.velocity;
        	tmpSh.coolDown = tmpTw.cooldown;
        	tmpTw.x = recalc.getX();//Tower location x
            tmpTw.y = recalc.getY();//Tower location y
            tmpSh.resetShotCordinates();//Same location as midpoint of Tower
            shotList[i] = tmpSh;
            towerList[i] = tmpTw;
        }
        
        // TODO: define player specific variables.
        Player p = new Player();
        p.difficulty = 1;
        p.health     = 60;
        p.money      = 100;
        
        // Sending data to GAME LOOP
        simulationRuntime = new GameLoop();
        simulationRuntime.setCreatures(creatureList);
        simulationRuntime.setLevels(waveList);
        simulationRuntime.setWP(w);
        simulationRuntime.setShots(shotList);
        simulationRuntime.setPlayer(p);
        simulationRuntime.setSoundManager(new SoundManager(getBaseContext()));
        RenderThread = new Thread(simulationRuntime);

        // Sends an array with sprites to the renderer
        nativeRenderer.setSprites(m.getBackground(), NativeRender.BACKGROUND);
        nativeRenderer.setSprites(creatureList, NativeRender.CREATURE);
        nativeRenderer.setSprites(towerList, NativeRender.TOWER);
        nativeRenderer.setSprites(shotList, NativeRender.SHOT);
        // Nåt sånt här skulle jag vilja att renderaren hanterar. Denna lista behöver aldig
        // ritas men vi behöver texturen som ligger i varje "lvl"
        // nativeRenderer.setSprites(waveList, NativeRender.WAVE);
        
        nativeRenderer.finalizeSprites();

        mGLSurfaceView.setRenderer(nativeRenderer);        

        // Now's a good time to run the GC.  Since we won't do any explicit
        // allocation during the test, the GC should stay dormant and not
        // influence our results.
        Runtime r = Runtime.getRuntime();
        r.gc();
   	
        setContentView(mGLSurfaceView);
        
        	// Allows for long-click menus.
        registerForContextMenu(mGLSurfaceView);
        
        RenderThread.start();
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }
    
    protected void onStop() {
    	simulationRuntime.run = false;
    	super.onStop();
    }
}