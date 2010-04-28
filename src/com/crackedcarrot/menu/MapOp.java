package com.crackedcarrot.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ViewSwitcher.ViewFactory;

import com.crackedcarrot.GameInit;

/**
 * This class/activity consists of three clickable objects, two buttons that
 * switches between the existing maps with belonging description, and the map image
 * that starts the game loop when clicked on. It sends a level variable to the game
 * loop the game loads the data depending on which level the user has chosen.
 */
public class MapOp extends Activity implements ViewFactory {
	
	/** References to our images */
    private Integer[] mmaps = {
    		R.drawable.map1,
    		R.drawable.map2,
    		R.drawable.background,
    };
    
    /** The index for our "maps" array */
    private int difficulty = 1;
    private int mapSelected;

    private TextView    tv;
    private RadioButton radioEasy;
    private RadioButton radioNormal;
    private RadioButton radioHard;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_startgame);

        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /** identifying the image views and text view, 
         *  these are the ones that will be set. */
        tv = (TextView) this.findViewById(R.id.maptext);

        Gallery gallery = (Gallery) findViewById(R.id.gallery1);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemSelectedListener(gItemSelectedHandler);
        gallery.setSelection((gallery.getCount()/2)+1, true);

        Button StartGameButton = (Button)findViewById(R.id.startmap);
        StartGameButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		//Send the level variable to the game loop and start it
        		Intent StartGame = new Intent(v.getContext(),GameInit.class);
        		StartGame.putExtra("com.crackedcarrot.menu.map", mapSelected);
        		StartGame.putExtra("com.crackedcarrot.menu.difficulty", difficulty);
        		startActivity(StartGame);
                finish();
        	}
        });
        
        // Difficulty listeners.
        radioEasy = (RadioButton) findViewById(R.id.radioEasy);
        radioNormal = (RadioButton) findViewById(R.id.radioNormal);
        radioHard = (RadioButton) findViewById(R.id.radioHard);

        radioEasy.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		difficulty = 0;
        		setRadioButtons(0);
			}

        });
        radioNormal.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		difficulty = 1;
        		setRadioButtons(1);
			}
        });
        radioHard.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		difficulty = 2;
        		setRadioButtons(2);
			}
        });

        ImageView easy = (ImageView) findViewById(R.id.StartGameImageViewEasy);
        easy.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		difficulty = 0;
        		setRadioButtons(0);
        	}
        });
        
        ImageView normal = (ImageView) findViewById(R.id.StartGameImageViewNormal);
        normal.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		difficulty = 1;
        		setRadioButtons(1);
        	}
        });
        
        ImageView hard = (ImageView) findViewById(R.id.StartGameImageViewHard);
        hard.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		difficulty = 2;
        		setRadioButtons(2);
        	}
        });

    }

    private void setRadioButtons(int i) {        		
    	radioEasy.setChecked(false);
    	radioNormal.setChecked(false);
    	radioHard.setChecked(false);
    	
    	switch(i) {
    	case 0:
        	radioEasy.setChecked(true);
    		break;
    	case 1:
        	radioNormal.setChecked(true);
    		break;
    	case 2:
        	radioHard.setChecked(true);
    		break;
    	}
    }

    
    
    	// Called when we get focus again (after a game has ended).
    @Override
    public void onRestart() {
        super.onRestart();

        		// TODO: Ta bort allt detta?
        	// Reset the selected map?
        // mapSelected = 1;
       	// tv.setText("Map 1: The field of grass.");
    }

	public View makeView() {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.xml_gallery);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new 
                ImageSwitcher.LayoutParams(
                        LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT));
        return imageView;
	}
	
   public OnItemSelectedListener gItemSelectedHandler = new
   OnItemSelectedListener() {
      //@Override
       public void onItemSelected(AdapterView<?> parent, View v, int _position, long id) {
    	   _position =  _position%3;
    	   switch(_position){
				case 0:
					tv.setText("Map 1: The field of longest grass.");
					mapSelected = 1;
					break;
				case 1: 
					mapSelected = 2;
					tv.setText("Map 2: The field of very cold grass.");
					break;	
				case 2: 
					mapSelected = 3;
					tv.setText("Map 3: The field of longer grass.");
					break;
			}
			
       }
        //@Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    };

    public class ImageAdapter extends BaseAdapter 
    {
        private Context context;
        //private int itemBackground;
        public int position;
        private int x;
        private int y;
        
        public ImageAdapter(Context c) {
            context = c;
            x = (int) (110 * getResources().getDisplayMetrics().density);
            y = (int) (165 * getResources().getDisplayMetrics().density);
        }
 
        //---returns the number of images---
        public int getCount() {
        	return 1000;
        }
 
        public Object getItem(int position) {
            return position%3;
        }
 
        public long getItemId(int position) {
            return position%3;
        }
 
        //---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent)
        {
        	this.position = position%3;
        	ImageView imageView = new ImageView(context);
            imageView.setImageResource(mmaps[this.position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new Gallery.LayoutParams(x,y));
            imageView.setBackgroundResource(R.drawable.xml_gallery);
            return imageView;
        }
   }
    
}
