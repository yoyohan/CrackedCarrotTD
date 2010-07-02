package com.crackedcarrot.multiplayer;

import java.util.UUID;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.crackedcarrot.GameInit;
import com.crackedcarrot.menu.R;

public class Server extends Activity {

	// Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // The server thread
    private AcceptThread mAcceptThread;
    // Name for the Service Discovery Protocol (SDP) record when creating server socket
    private static final String NAME = "CrackedCarrotTD";
    // The Universally Unique Identifier (UUID) for this application
    private static final UUID MY_UUID = UUID.fromString("9a8aa173-eaf0-4370-80e1-3a13ed5efae9");
    // The request codes for startActivity and onActivityResult
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_DISCOVERABLE = 3;
    
    private final int PROGRESS_DIALOG = 1;
    
    public BluetoothSocket socket = null;
    
    private final int DIFFICULTY = 1; //Default diff. for multiplayer is normal
    private final int MAP = 4; // Default map for multiplayer
	
    /** if user presses back button, this activity will finish */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		Log.d("Server", "onKeyDown KEYCODE_BACK");
    		finish();
    		return true;
       	}
    	return super.onKeyDown(keyCode, event);
    } 
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);
        
        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
    	// Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available on this device", 
            		Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);
        
        showDialog(PROGRESS_DIALOG);
    }
    
    /** When the activity first starts, do following */
    @Override
    public void onStart() {
        super.onStart();
        
        /** Request that Bluetooth will be activated if not on.
         *  setupServer() will then be called during onActivityResult */
       // if (!mBluetoothAdapter.isEnabled()) {
       //     Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //    startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        //} else {
        //    setupServer();
       // } 
    }
    
    private void setupServer() {
    	if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
            Log.d("SERVER", "Start server thread");
    	} else {
    		Log.d("SERVER", "The Accept thread already exists!!");
    	}
    }
    
    /** This method is called after the startActivityForResult() is called with
        parameters containing activity id and user choice */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_ENABLE_BLUETOOTH:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a server
                setupServer();
            } else {
                // If the user did not want to turn BT on, or error occurred
                Toast.makeText(this, "Bluetooth not enabled...leaving", Toast.LENGTH_SHORT).show();
                finish();
            }
        break;
        case REQUEST_DISCOVERABLE:
        	if (resultCode == 300) {
                // The device is made discoverable and bluetooth is activated
        		setupServer();
            } else {
                // User did not accept the request or an error occured
                Toast.makeText(this, "The device was not made discoverable. Leaving multiplayer"
                		, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // mmServerSocket is final so use a temporary object first
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (Exception e) {
            	Toast.makeText(Server.this, "Bluetooth not active, returned null-object as socket. " + 
            			"Reactivate Bluetooth on device!",Toast.LENGTH_LONG).show();
            	finish();
            }
            mmServerSocket = tmp;
            Log.d("SERVER", "Serverthread constructor");
        }

        public void run() {
            BluetoothSocket socket = null;
            // Listen, by calling accept(), until exception occurs or a socket is returned
            while (true) {
            	if(mmServerSocket == null){
                	finish();
                    break;
            	}
            	else{
            		try {
                    	Log.d("SERVER", "K�r server accept()");
                        socket = mmServerSocket.accept();
                    } catch (Exception e) {
                        break;
                    }
            	}
                Log.d("SERVER", "Serverthread running");
                // Connection accepted?
                if (socket != null) {
                	Log.d("SERVER", "connection established!");
                	startGame(socket);
                	try{
                		mmServerSocket.close();
                	} catch (Exception e){
                		Log.d("SERVER", "Can't close the server socket");
                	}
                    break;
                }
            }
        }
    }
    
    /** Method that sets the AcceptThread to null and starts the game in multiplayer mode */
    private void startGame(BluetoothSocket socket){
        // Start the game and finish the activity
    	Log.d("SERVER", "Start game");
    	GameInit.setMultiplayer(socket);
    	Intent StartGame = new Intent(this, GameInit.class);
		StartGame.putExtra("com.crackedcarrot.menu.map", MAP);
		StartGame.putExtra("com.crackedcarrot.menu.difficulty", DIFFICULTY);
		startActivity(StartGame);
		// Cancel the accept thread because we only want to connect to one device
		mAcceptThread = null;
		finish();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PROGRESS_DIALOG: {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Waiting for client to connect...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
                	
                	public void onCancel(DialogInterface dialog){
                		Server.this.finish();
                	}
                });
                return dialog;
            }
        }
        return null;
    }
    
    protected void onStop() {
    	super.onStop();
    	super.onDestroy();
    }
    
    protected void onResume(){
    	super.onResume();
    }
}