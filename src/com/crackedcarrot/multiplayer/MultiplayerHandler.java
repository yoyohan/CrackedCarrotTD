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
	
	// Message types sent to the MultiplayerService Handler
	public static final int MESSAGE_READ = 10;
    public static final int MESSAGE_SYNCH_LEVEL = 1;
    public static final int MESSAGE_PLAYER_SCORE = 2;
    public static final int MESSAGE_PLAYER_DEAD = 3;
    public static final int MESSAGE_DEVICE_NAME = 30;
    public static final int MESSAGE_TOAST = 40;
    
    private int opponentScore;
    
    // Message read types sent to the MultiplayerService Handler: MESSAGE_READ
    private final String SYNCH_LEVEL = "synchLevel";
    private final String PLAYER_SCORE = "Score";
    private final String PLAYER_DEAD = "Dead";
    private final String INCREASE_ENEMY_SPEED = "incEnSp";
    private final String DECREASE_OPP_LIFE = "decOppLife";
    private final String DESTROY_TOWER = "desTower";
	
	public MultiplayerHandler(GameLoopGUI glGui){
		gameLoopGui = glGui;
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
 	                	MultiplayerGameLoop.synchLevelClick();
	                    
 	                }
 	                // The opponent is dead
	                else if(readMessage.equals(PLAYER_DEAD)){
	                	Log.d("YYYYY", readMessage);
	                	MultiplayerGameLoop mpGL = (MultiplayerGameLoop) gameLoopGui.getGameInit()
	                	.gameLoop;
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
	                	CharSequence text = "An enemy has gained more health and speed";
	            		int duration = Toast.LENGTH_SHORT;
	            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
	            		toast.show();
	                }
	                else if(readMessage.equals(DECREASE_OPP_LIFE)){
	                	Log.d("MULTIPLAYERHANDLER", "Decrease opponents life!!");
	                	CharSequence text = "Your life has been decreased";
	            		int duration = Toast.LENGTH_SHORT;
	            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
	            		toast.show();
	                }
	                else if(readMessage.equals(DESTROY_TOWER)){
	                	Log.d("MULTIPLAYERHANDLER", "Destroy tower!!");
	                	CharSequence text = "A random tower has been destroyed";
	            		int duration = Toast.LENGTH_SHORT;
	            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
	            		toast.show();
	                }
	                else{
	                	Log.d("!!!!!!!", "Got wrong message!!: " + readMessage);
	                }
                	break;
                case MESSAGE_TOAST:
                	//Do something to inform user that connection is lost
                    //Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                    //               Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        };
        
        Looper.loop();
	}
	
	public int getOpponentScore(){
		return this.opponentScore;
	}
	
}
