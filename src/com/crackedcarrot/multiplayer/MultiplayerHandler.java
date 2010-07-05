package com.crackedcarrot.multiplayer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.crackedcarrot.GameLoopGUI;

public class MultiplayerHandler extends Thread {
	
	public Handler mMultiplayerHandler;
	private GameLoopGUI gameLoopGui;
	private MultiplayerGameLoop mpGL;
	
	// Message types sent to the MultiplayerService Handler
	public static final int MESSAGE_READ = 10;
    public static final int MESSAGE_SYNCH_LEVEL = 1;
    public static final int MESSAGE_PLAYER_SCORE = 2;
    public static final int MESSAGE_PLAYER_DEAD = 3;
    public static final int MESSAGE_DEVICE_NAME = 30;
    public static final int MESSAGE_BT_KILLED = 40;
    
    private int opponentScore;
    
    // Message read types sent to the MultiplayerService Handler: MESSAGE_READ
    private final String SYNCH_LEVEL = "synchLevel";
    private final String PLAYER_SCORE = "Score";
    private final String PLAYER_DEAD = "Dead";
    private final String INCREASE_ENEMY_SPEED = "incEnSp";
    private final String DECREASE_OPP_LIFE = "decOppLife";
    private final String DESTROY_TOWER = "desTower";
    private final String MAKE_ELEMENTAL = "mkElem";
    private final String MAKE_SHIELD = "mkShield";
	
	public MultiplayerHandler(GameLoopGUI glGui){
		gameLoopGui = glGui;
		//mpGL = gameLoopGui.getGameInit()
    	//.gLoop;
		
	}
	
	public void run(){
		
		Looper.prepare();
		
		mMultiplayerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MESSAGE_READ:
                	byte[] readBuff = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuff);
 	                readMessage = readMessage.substring(0, msg.arg2);
 	                // Level synchronization
 	                if(readMessage.equals(SYNCH_LEVEL)){
 	                	Log.d("MULTIPLAYERHANDLER", "Release synchSemaphore");
 	                	//MultiplayerGameLoop.synchLevelClick();                    
 	                	mpGL.synchLevelClick();                    
 	 	                
 	                }
 	                // The opponent is dead
	                else if(readMessage.equals(PLAYER_DEAD)){
	                	Log.d("YYYYY", readMessage);
	                    mpGL.setOpponentLife(false);
	                }
	                // The data consists of the opponents score
	                else if((readMessage.substring(0, 5)).equals(PLAYER_SCORE)){
	                	readMessage = readMessage.substring(5, msg.arg2);
	                	 Log.d("MULTIPLAYERHANDLER", "Opponents score: " + readMessage);
	                     opponentScore = Integer.parseInt(readMessage);
	                     gameLoopGui.setOpponentScore(opponentScore);
	                }
	                else if(readMessage.equals(INCREASE_ENEMY_SPEED)){
	                	Log.d("MULTIPLAYERHANDLER", "Increase enemy speed and health!!");
	                	if(mpGL.multiplayerShield){
	                		mpGL.multiplayerShield = false;
	                		CharSequence text = "Your shield was used against an enemy upgrade attack";
		            		int duration = Toast.LENGTH_SHORT;
		            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
		            		toast.show();
	                	}
	                	else{
		                	mpGL.incEnSp(0);
		                	
		                	CharSequence text = "An enemy has gained more health and speed";
		            		int duration = Toast.LENGTH_SHORT;
		            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
		            		toast.show();
	                	}
	                }
	                else if(readMessage.equals(DECREASE_OPP_LIFE)){
	                	Log.d("MULTIPLAYERHANDLER", "Decrease opponents life!!");
	                	if(mpGL.multiplayerShield){
	                		mpGL.multiplayerShield = false;
	                		CharSequence text = "Your shield was used against a life attack";
		            		int duration = Toast.LENGTH_SHORT;
		            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
		            		toast.show();
	                	}
	                	else{
		                	mpGL.decOppLife();
		                	
		                	CharSequence text = "Your life has been decreased";
		            		int duration = Toast.LENGTH_SHORT;
		            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
		            		toast.show();
	                	}
	                }
	                else if(readMessage.equals(DESTROY_TOWER)){
	                	Log.d("MULTIPLAYERHANDLER", "Destroy tower!!");
	                	if(mpGL.multiplayerShield){
	                		mpGL.multiplayerShield = false;
	                		CharSequence text = "Your shield was used against a tower attack";
		            		int duration = Toast.LENGTH_SHORT;
		            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
		            		toast.show();
	                	}
	                	else {
		                	mpGL.desTower(0);
		                	
		                	CharSequence text = "A random tower has been destroyed";
		            		int duration = Toast.LENGTH_SHORT;
		            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
		            		toast.show();
	                	}
	                }
	                else if(readMessage.equals(MAKE_ELEMENTAL)){
	                	Log.d("MULTIPLAYERHANDLER", "Make elemental");
	                	if(mpGL.multiplayerShield){
	                		mpGL.multiplayerShield = false;
	                		CharSequence text = "Your shield was used against an elemental attack";
		            		int duration = Toast.LENGTH_SHORT;
		            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
		            		toast.show();
	                	}
	                	else{
		                	int tmp = mpGL.mkElem();
		                	CharSequence text = "";
		                	
		            		if (tmp == 0) {
			                	text = "The enemies have gained speed";
		            		}
		            		else if (tmp == 1) {
			                	text = "The enemies have gained fire resistans";
		            		}
		            		else if (tmp == 2) {
			                	text = "The enemies have gained frost resistans";
		            		} else {
			                	text = "The enemies have gained poison resistans";
		            		}

		            		int duration = Toast.LENGTH_SHORT;
		            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
		            		toast.show();
	                	}
	                }
	                /*
	                else if(readMessage.equals(MAKE_SHIELD)){
	                	Log.d("MULTIPLAYERHANDLER", "opponent made a shield");
	                	//Do something? No, don't notify opponent about this
	                } */
	                else {
	                	Log.d("!!!!!!!", "Got wrong message!!: " + readMessage);
	                }
                	break;
                case MESSAGE_BT_KILLED:
                	CharSequence text = "Bluetooth connection was lost, closing battle...";
            		int duration = Toast.LENGTH_SHORT;
            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
            		toast.show();
            		mpGL.stopGameLoop();
                    break;
                }
            }
        };
        
        Looper.loop();
	}
	
	public int getOpponentScore(){
		return this.opponentScore;
	}

	public void setGameLoop(MultiplayerGameLoop gLoop) {
		mpGL = gLoop;
	}
	
}
