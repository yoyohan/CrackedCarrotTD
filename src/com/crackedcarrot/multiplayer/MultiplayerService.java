package com.crackedcarrot.multiplayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class MultiplayerService extends Thread {
	
	private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    //private Handler mMultiplayerHandler;
    
    // Message types sent to the MultiplayerService Handler
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;
    
    // Message read types sent to the MultiplayerService Handler: MESSAGE_READ
    private final String SYNCH_LEVEL = "synchLevel";
    private final String SHOW_SCORE = "showScore";

    public MultiplayerService(BluetoothSocket socket) {
        Log.d("MultiplayerService", "create ConnectedThread");
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {}

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }
    
    public Handler mMultiplayerHandler = new Handler() {
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
            	Log.d("GUIHANDLER", "Message read");
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf);
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

    public void run() {
        Log.d("MultiplayerService", "BEGIN MultiplayerService");
        byte[] buffer = new byte[1024];
        int bytes;
        
        //Looper.prepare();
		
		

		//Looper.loop();

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                
                // Send the obtained bytes to the UI Activity
                mMultiplayerHandler.obtainMessage(MESSAGE_READ, buffer)
                        .sendToTarget();
         	   Log.d("MPSERVICE LOOP", "Send to handler");
            } catch (IOException e) {
            	Log.d("MPSERVICE LOOP", "Connection lost");
                connectionLost();
                break;
            } 
        }    
    }
    
    public synchronized void connected() {
        this.start();
    }
    
    /**
     * Write to the connected OutStream.
     * @param buffer  The bytes to write
     */
    public void write(byte[] buffer) {
       try {
    	   Log.d("MPSERVICE Write", "Write to OutputStream");
            mmOutStream.write(buffer);
        } catch (IOException e) {
            Log.e("MultiplayerService", "Exception during write", e);
        }
    }
    
    
    
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
    	/**
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothChat.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg); */
    }
    
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        mMultiplayerHandler.obtainMessage(MESSAGE_TOAST).sendToTarget();
    }
    
}