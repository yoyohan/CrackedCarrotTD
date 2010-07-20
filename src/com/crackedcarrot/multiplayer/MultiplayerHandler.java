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
    private int opponentEnLeft;
    
    // Message read types sent to the MultiplayerService Handler: MESSAGE_READ
    private final String SYNCH_LEVEL = "synchLevel";
    private final String PLAYER_SCORE = "Score";
    private final String PLAYER_DEAD = "Dead";
    private final String INCREASE_ENEMY_SPEED = "incEnSp";
    private final String DECREASE_OPP_LIFE = "decOppLife";
    private final String DESTROY_TOWER = "desTower";
    private final String MAKE_ELEMENTAL = "mkElem";
    private final String MAKE_SHIELD = "mkShield";
    private final String OPP_CRE_LEFT = "cre";
	
    //Handshake variables
 	public int MAP;
 	public int DIFFICULTY;
 	public int GAMEMODE;
 	public boolean OK;
    
	public MultiplayerHandler(){
		//gameLoopGui = glGui;
		//mpGL = gameLoopGui.getGameInit()
    	//.gLoop;
		
	}
	
	public void run(){
		
		Looper.prepare();
		
		mMultiplayerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	
            	Log.d("MULTIPLAYER!", "Message: " + msg.arg1);
            	
                switch (msg.what) {
                case MESSAGE_READ:
                	
                	try {
                	
                	byte[] readBuff = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuff);
 	                readMessage = readMessage.substring(0, msg.arg2);
 	                
 	                Log.d("MULTIPLAYER!", "Message: " + readMessage);
 	                
 	                if (readMessage.equals("0"))
 	                	return;
 	                
 	                if(readMessage.startsWith("SERVER")) {
 	                	String[] temp = readMessage.split(":");
 	                	MAP = Integer.parseInt(temp[1]);
 	                	DIFFICULTY = Integer.parseInt(temp[2]);
 	                	GAMEMODE = Integer.parseInt(temp[3]);
 	                	Client.handshakeSemaphore.release();

 	                }
 	                else if(readMessage.startsWith("CLIENT")) {
 	                	String[] temp = readMessage.split(":");
 	                	// If we receive an ok from client then run with map selection otherwise set to default.
 	                	OK = Boolean.parseBoolean(temp[1]);
 	                	Server.handshakeSemaphore.release();

 	                }
 	                
 	                // Level synchronization
 	                else if(readMessage.equals(SYNCH_LEVEL)){
 	                	Log.d("MULTIPLAYERHANDLER", "Release synchSemaphore");
 	                	//MultiplayerGameLoop.synchLevelClick();                    
 	                	mpGL.synchLevelClick();                    
 	 	                
 	                }
 	                // The opponent is dead
	                else if(readMessage.equals(PLAYER_DEAD)){
	                	Log.d("YYYYY", readMessage);
	                    mpGL.setOpponentLife(false);
	                }
 	                // The data consists of the opponents enemies left
	                else if((readMessage.substring(0, 3)).equals(OPP_CRE_LEFT)){
	                	readMessage = readMessage.substring(3, msg.arg2);
	                	Log.d("MULTIPLAYERHANDLER", "Opponents enemies left: " + readMessage);
	                    opponentEnLeft = Integer.parseInt(readMessage);
	                    
	                    if (gameLoopGui == null)
	                    	Log.d("MULTIPLAYER", "HH");
	                    
	                    gameLoopGui.sendMessage(gameLoopGui.OPP_CREATURELEFT, opponentEnLeft, 0);

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
		            		
		            		if (mpGL.isSurvivalGame())
		            			gameLoopGui.sendMessage(gameLoopGui.GUI_SHOWSHIELDBUTTON, 0, 0);
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

		            		if (mpGL.isSurvivalGame())
		            			gameLoopGui.sendMessage(gameLoopGui.GUI_SHOWSHIELDBUTTON, 0, 0);
	                	}
	                	else{
		                	boolean tmp = mpGL.decOppLife();
		                	
		                	CharSequence text;
		                	if (tmp)
		                		text = "Your health has been decreased";
		                	else 
		                		text = "Your opponent tried to decrease your health below zero and failed";
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
		            		
		            		if (mpGL.isSurvivalGame())
		            			gameLoopGui.sendMessage(gameLoopGui.GUI_SHOWSHIELDBUTTON, 0, 0);
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
		            		
		            		if (mpGL.isSurvivalGame())
		            			gameLoopGui.sendMessage(gameLoopGui.GUI_SHOWSHIELDBUTTON, 0, 0);
	                	}
	                	else{
		                	String tmp = String.valueOf(mpGL.mkElem());
		                	String text = "Enemies have gained: ";
		                	
		            		if (tmp.charAt(0) == '1')
			                	text += "speed ";
		            		else if (tmp.charAt(1) == '1')
			                	text = "fireresistans ";
		            		else if (tmp.charAt(2) == '1')
			                	text = "frostresistans ";
			                else if (tmp.charAt(1) == '1')
			                	text = "poisonresistans";

		            		int duration = Toast.LENGTH_SHORT;
		            		Toast toast = Toast.makeText(gameLoopGui.getGameInit(), text, duration);
		            		toast.show();
	                	}
	                }
	                else {
	                	Log.d("!!!!!!!", "Got wrong message!!: " + readMessage);
	                }
                	break;
                	
                	}
                	catch (NumberFormatException nfe) {
                		// do nothing really. a message was distorted, we consider it lost and move on.
                	}
                	
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

	public void setGameLoopGui(GameLoopGUI glGui) {
		gameLoopGui = glGui;
		
	}
	
}
