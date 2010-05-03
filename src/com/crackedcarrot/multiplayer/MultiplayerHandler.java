package com.crackedcarrot.multiplayer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MultiplayerHandler extends Thread {
	
	public Handler mMultiplayerHandler;
	
	// Message types sent to the MultiplayerService Handler
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;
    
    // Message read types sent to the MultiplayerService Handler: MESSAGE_READ
    private final String SYNCH_LEVEL = "synchLevel";
	
	public MultiplayerHandler(){
		
	}
	
	public void run(){
		
		Looper.prepare();
		
		mMultiplayerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf);
	                readMessage = readMessage.substring(0, msg.arg2);
                    if(readMessage.equals(SYNCH_LEVEL)){
                    	Log.d("GUIHANDLER", "Release synchSemaphore");
                    	MultiplayerGameLoop.synchLevelClick();
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    //mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    //Toast.makeText(getApplicationContext(), "Connected to "
                    //               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
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
	
}
