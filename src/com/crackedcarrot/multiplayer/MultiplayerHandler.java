package com.crackedcarrot.multiplayer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.crackedcarrot.GameLoopGUI;

public class MultiplayerHandler extends Thread {
	
	public Handler mMultiplayerHandler;
	private GameLoopGUI gameLoopGui;
	
	// Message types sent to the MultiplayerService Handler
    public static final int MESSAGE_SYNCH_LEVEL = 1;
    public static final int MESSAGE_PLAYER_SCORE = 2;
    public static final int MESSAGE_PLAYER_DEAD = 3;
    public static final int MESSAGE_DEVICE_NAME = 30;
    public static final int MESSAGE_TOAST = 40;
    
    private int opponentScore;
    
    // Message read types sent to the MultiplayerService Handler: MESSAGE_READ
    private final String SYNCH_LEVEL = "synchLevel";
	
	public MultiplayerHandler(GameLoopGUI glGui){
		gameLoopGui = glGui;
	}
	
	public void run(){
		
		Looper.prepare();
		
		mMultiplayerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MESSAGE_SYNCH_LEVEL:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf);
	                readMessage = readMessage.substring(0, msg.arg2);
                    if(readMessage.equals(SYNCH_LEVEL)){
                    	Log.d("GUIHANDLER", "Release synchSemaphore");
                    	MultiplayerGameLoop.synchLevelClick();
                    }
                    break;
                case MESSAGE_PLAYER_SCORE:
                    byte[] readBuf2 = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage2 = new String(readBuf2);
	                readMessage = readMessage2.substring(5, msg.arg2);
                    Log.d("GUIHANDLER", "Opponents score: " + readMessage);
                    opponentScore = Integer.parseInt(readMessage);
                    gameLoopGui.setOpponentScore(opponentScore);
                    break;
                case MESSAGE_PLAYER_DEAD:
                	MultiplayerGameLoop mpGL = (MultiplayerGameLoop) gameLoopGui.getGameInit().gameLoop;
                    mpGL.setOpponentLife(false);
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
