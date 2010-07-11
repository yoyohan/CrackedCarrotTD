package com.crackedcarrot.menu;

import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

import com.crackedcarrot.GameInit;

/**
 * This class/activity consists of three clickable objects, two buttons that
 * switches between the existing maps with belonging description, and the map image
 * that starts the game loop when clicked on. It sends a level variable to the game
 * loop the game loads the data depending on which level the user has chosen.
 */
public class MapOp extends Activity implements ViewFactory {
	
		// DEMO. Only let the player play on Normal difficulty.
	boolean demo = false;
	
    /** The index for our "maps" array */
    private int difficulty = 1;
    private int mapSelected;
    private int wave = 1;
        
    private TextView    tv;
    
    private ImageView mBackground;
    
    private ImageView easy;
    private ImageView hard;
    private ImageView normal;
    
    private RadioButton radioEasy;
    private RadioButton radioNormal;
    private RadioButton radioHard;
    
    private Button StartGameButton;
    
    private Gallery gallery;
    
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;
    private Bitmap bitmap4;
    private Bitmap bitmap5;
    private Bitmap bitmap6;
    
    /** References to our images */
    private Bitmap[] mmaps = {
    		bitmap1,
    		bitmap2,
    		bitmap3,
    		bitmap4,
    		bitmap5,
    		bitmap6,
    };
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)  {
    	
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_startgame);    
        
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize = 8;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        InputStream is = this.getResources().openRawResource(R.drawable.map1);
        
        try {
        	mmaps[0] = BitmapFactory.decodeStream(is, null, options);
            is = this.getResources().openRawResource(R.drawable.map2);
            mmaps[1] = BitmapFactory.decodeStream(is, null, options);
            is = this.getResources().openRawResource(R.drawable.map3);
            mmaps[2] = BitmapFactory.decodeStream(is, null, options);
            is = this.getResources().openRawResource(R.drawable.map4);
            mmaps[3] = BitmapFactory.decodeStream(is, null, options);
            is = this.getResources().openRawResource(R.drawable.map5);
            mmaps[4] = BitmapFactory.decodeStream(is, null, options);
            is = this.getResources().openRawResource(R.drawable.map6);
            mmaps[5] = BitmapFactory.decodeStream(is, null, options);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            	//Skip
            }
        }
        
        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
        /** identifying the image views and text view, 
         *  these are the ones that will be set. */
    	mBackground = (ImageView) findViewById(R.id.mBackground);
    	
        tv = (TextView) this.findViewById(R.id.maptext);
    	Typeface face = Typeface.createFromAsset(this.getAssets(), "fonts/MuseoSans_500.otf");
    	tv.setTypeface(face);
    	
        gallery = (Gallery) findViewById(R.id.gallery1);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemSelectedListener(gItemSelectedHandler);
        gallery.setSelection((gallery.getCount()/2)-2, true);

        StartGameButton = (Button)findViewById(R.id.startmap);
        StartGameButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		//Send the level variable to the game loop and start it
        		
        		StartGameButton.setVisibility(View.INVISIBLE);
        		
        		tv.setVisibility(View.INVISIBLE);
        		
        		radioEasy.setVisibility(View.INVISIBLE);
        		radioNormal.setVisibility(View.INVISIBLE);
        		radioHard.setVisibility(View.INVISIBLE);
        		
        		easy.setVisibility(View.INVISIBLE);
        		normal.setVisibility(View.INVISIBLE);
        		hard.setVisibility(View.INVISIBLE);
        		
        		gallery.setVisibility(View.INVISIBLE);
        		
        		mBackground.setImageResource(R.drawable.loadimage);
        		mBackground.setScaleType(ScaleType.CENTER_INSIDE);
        		        		
        		Intent StartGame = new Intent(v.getContext(),GameInit.class);
        		if (demo) // If demo we always play map #1.
        			StartGame.putExtra("com.crackedcarrot.menu.map", 1);
        		else
        			StartGame.putExtra("com.crackedcarrot.menu.map", mapSelected);
        		StartGame.putExtra("com.crackedcarrot.menu.difficulty", difficulty);
        		// Since this is not a multiplayergame we will send 1 to gameinit
        		StartGame.putExtra("com.crackedcarrot.menu.wave", wave);
        		startActivity(StartGame);
        		finish();
        	}
        });
        
        // Difficulty listeners.
    	face = Typeface.createFromAsset(this.getAssets(), "fonts/MuseoSans_500.otf");
        radioEasy = (RadioButton) findViewById(R.id.radioEasy);
       	radioEasy.setTypeface(face);
        radioNormal = (RadioButton) findViewById(R.id.radioNormal);
       	radioNormal.setTypeface(face);
        radioHard = (RadioButton) findViewById(R.id.radioHard);
       	radioHard.setTypeface(face);
       	
       		// DEMO. Only let people play on Normal in the demo-release.
       	if (demo == true) {
       		radioEasy.setEnabled(false);
       		radioHard.setEnabled(false);
       	} else {
	        radioEasy.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		difficulty = 0;
	        		setRadioButtons(0);
				}
	
	        });
	        radioHard.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		difficulty = 2;
	        		setRadioButtons(2);
				}
	        });  		
       	}
        radioNormal.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		difficulty = 1;
        		setRadioButtons(1);
			}
        });

        easy = (ImageView) findViewById(R.id.StartGameImageViewEasy);
        easy.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		difficulty = 0;
        		setRadioButtons(0);
        	}
        });
        
        normal = (ImageView) findViewById(R.id.StartGameImageViewNormal);
        normal.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		difficulty = 1;
        		setRadioButtons(1);
        	}
        });
        
        hard = (ImageView) findViewById(R.id.StartGameImageViewHard);
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
    	   _position =  _position%6;
    	   switch(_position){
				case 0:
					tv.setText("Map 1: The field of long grass.");
					mapSelected = 1;
					wave = 1;
					break;
				case 1: 
					mapSelected = 2;
					tv.setText("Map 2: The field of cold grass.");
					wave = 1;
					break;	
				case 2: 
					mapSelected = 3;
					tv.setText("Map 3: The field of no grass.");
					wave = 1;
					break;
				case 3: 
					mapSelected = 4;
					tv.setText("Map 4: The field of long grass v2.");
					wave = 1;
					break;
				case 4: 
					mapSelected = 5;
					tv.setText("Map 5: The field of cold grass v2.");
					wave = 1;
					break;	
				case 5: 
					mapSelected = 6;
					tv.setText("Map 6: The field of no grass v2.");
					wave = 1;
					break;
			}
			
       }
        //@Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    };

    private class ImageAdapter extends BaseAdapter 
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
            return position%6;
        }
 
        public long getItemId(int position) {
            return position%5;
        }
 
        //---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent)
        {
        	this.position = position%6;
        	ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(mmaps[this.position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new Gallery.LayoutParams(x,y));
            imageView.setBackgroundResource(R.drawable.xml_gallery);
            return imageView;
        }
   }
    
}
