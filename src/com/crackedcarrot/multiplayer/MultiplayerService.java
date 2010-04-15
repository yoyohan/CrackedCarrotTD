package com.crackedcarrot.multiplayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class MultiplayerService extends Thread {
	
	private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mMultiplayerHandler;

    public MultiplayerService(BluetoothSocket socket, Handler handler) {
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
        mMultiplayerHandler = handler;
    }

    public void run() {
        Log.d("MultiplayerService", "BEGIN MultiplayerService");
        byte[] buffer = new byte[1024];
        int bytes;

        // Keep listening to the InputStream while connected
        while (true) {
          /**  try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);

                // Send the obtained bytes to the UI Activity
                mHandler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                connectionLost();
                break;
            } */
        }
           
    }
    
    /**
     * Write to the connected OutStream.
     * @param buffer  The bytes to write
     */
    public void write(byte[] buffer) {
       try {
            mmOutStream.write(buffer);
            
            // Share the sent message back to the UI Activity
            //mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
            //        .sendToTarget();
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
}