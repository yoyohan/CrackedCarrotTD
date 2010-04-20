package com.crackedcarrot.multiplayer;

import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.crackedcarrot.menu.R;

public class Client extends Activity {

	// Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // The client thread
    private ConnectThread mConnectThread;
    // The Universally Unique Identifier (UUID) for this application
    private static final UUID MY_UUID = UUID.fromString("9a8aa173-eaf0-4370-80e1-3a13ed5efae9");
    // The request codes for startActivity and onActivityResult
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_CONNECT_DEVICE = 2;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);
        
        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
    	Button ScanButton = (Button)findViewById(R.id.scan);
    	ScanButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
                Intent serverIntent = new Intent(Client.this, ScanDevices.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        	}
        });
        
    	// Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available on this device", 
            		Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }
    
    /** When the activity first starts, do following */
    @Override
    public void onStart() {
        super.onStart();
        
        /** Request that Bluetooth will be activated if not on.
         *  setupClient() will then be called during onActivityResult */
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
            Log.d("CLIENT", "Request enable Bluetooth");
        } 
    }
    
    /**
     * This synchronized method starts the thread that
     * does the actual connection with the available server
     * @param device
     */
    private synchronized void connect(BluetoothDevice device) {
    	if (mConnectThread == null) {
    		mConnectThread = new ConnectThread(device);
    		mConnectThread.start();
    		Log.d("CLIENT", "Start connect thread");
    	}
    }
    
    /** This method is called after the startActivityForResult() is called with
        parameters containing activity id and user choice */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When ScanDevices returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the MAC address of the device
                String address = data.getExtras()
                                     .getString(ScanDevices.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Try to connect to the device
                connect(device);
            }
            break;
        case REQUEST_ENABLE_BLUETOOTH:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so do nothing
            } else {
                // User did not enable Bluetooth or an error occured
                Toast.makeText(this, "Bluetooth was not enabled. Leaving Bluetooth Chat."
                		, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmClientSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
        	// mmClientSocket is final so use a temporary object first
            BluetoothSocket tmp = null;
            mmDevice = device;
            Log.d("CLIENT", "Connectthread constructor");
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmClientSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect through the socket. This will block until it succeeds or throws an exception
                mmClientSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmClientSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            // manageConnectedSocket(mmClientSocket);
            // Start the service over to restart listening mode
            //BluetoothChatService.this.start();
            //Bättre att när connectad så avsluta denna tråd.
        	Toast.makeText(Client.this, "Connection established", Toast.LENGTH_LONG).show();

        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmClientSocket.close();
            } catch (IOException e) { }
        }
    }

}