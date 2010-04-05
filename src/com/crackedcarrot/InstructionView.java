package com.crackedcarrot;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crackedcarrot.menu.R;

/**
 * Class that functions as the instruction view. It creates the
 * dialog consisting of the level instructions.
 */
public class InstructionView extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	/** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
    	setContentView(R.layout.levelinstruction);
    	
    	Button close = (Button) findViewById(R.id.closedialog);
    	close.setOnClickListener(
    			new View.OnClickListener() {
    				public void onClick(View v) {
    					finish();
    				}
    			});
    	
    	/** Fetch information from previous intent. It passes the resourceId
    	 * of the current level.
    	 */
        Bundle extras  = getIntent().getExtras();
        int currLvId = 0;
        if(extras != null) {
        	currLvId = extras.getInt("com.crackedcarrot.resourceId");
        }        
    	Log.d("INSTRUCTIONVIEW", "" + currLvId);
    	ImageView im = (ImageView) findViewById(R.id.enemydescription);
    	im.setImageResource(currLvId);
    	
    	TextView enemyText = (TextView)findViewById(R.id.enemyText);
    	
    	switch(currLvId) {
    	case R.drawable.mrrabbit :
    		String rabbit = "<b>Mr Rabbit:</b> <br>";
    		rabbit += 		"Mr Rabbit is after the rare carrots, stop " + 
    		"him before he reaches them!<br>";
    		rabbit += 		"Health: 115 <br>";
    		rabbit += 		"Gold value: 1 <br>";
    		rabbit += 		"Special Ability: None <br>";
	    	CharSequence styledRabbitText = Html.fromHtml(rabbit);
		    enemyText.setText(styledRabbitText);
    		break;
    	case R.drawable.mrmonkey:
    		String monkey = "<b>Mr Monkey:</b> <br>";
    		monkey += 		"Mr Monkey wants to climb up the trees in the garden, " + 
    		"stop him!!<br>";
    		monkey += 		"Health: 135 <br>";
    		monkey += 		"Gold value: 1 <br>";
    		monkey += 		"Special Ability: None <br>";
    		CharSequence styledMonkeyText = Html.fromHtml(monkey);
		    enemyText.setText(styledMonkeyText);
    		break;
    	case R.drawable.mrpingu:
    		String pingu = "<b>Mr Pingu:</b> <br>";
    		pingu += 		"Mr Pingu wants to throw snow in the garden, don't let him! <br>";
    		pingu += 		"Health: 160 <br>";
    		pingu += 		"Gold value: 1 <br>";
    		pingu += 		"Special Ability: None <br>";
    		CharSequence styledPinguText = Html.fromHtml(pingu);
		    enemyText.setText(styledPinguText);
    		break;
    	case R.drawable.mrbully:
    		String bully = "<b>Mr Bully:</b> <br>";
    		bully += 		"Mr Bully wants to take a shit in the garden, stop him! <br>";
    		bully += 		"Health: 185 <br>";
    		bully += 		"Gold value: 1 <br>";
    		bully += 		"Special Ability: None <br>";
    		CharSequence styledBullyText = Html.fromHtml(bully);
		    enemyText.setText(styledBullyText);
    		break;
    	case R.drawable.mrlion:
    		String lion = "<b>Mr Lion:</b> <br>";
    		lion += 		"Mr Lion wants to eat all other animals in " + 
    		"in the garden, you better stop him before it's too late! <br>";
    		lion += 		"Health: 220 <br>";
    		lion += 		"Gold value: 1 <br>";
    		lion += 		"Special Ability: None <br>";
    		CharSequence styledLionText = Html.fromHtml(lion);
		    enemyText.setText(styledLionText);
    		break;
    	case R.drawable.mrcat:
    		String cat = "<b>Mr Cat:</b> <br>";
    		cat += 		"Mr Cat wants to dig in the garden, stop " + 
    		"him before he does!<br>";
    		cat += 		"Health: 250 <br>";
    		cat += 		"Gold value: 1 <br>";
    		cat += 		"Special Ability: Fast <br>";
    		CharSequence styledCatText = Html.fromHtml(cat);
		    enemyText.setText(styledCatText);
    		break;
    	case R.drawable.mrbear:
    		String bear = "<b>Mr Bear:</b> <br>";
    		bear += 		"Mr Bear is going to dig in the garden, stop him! <br>";
    		bear += 		"Health: 345 <br>";
    		bear += 		"Gold value: 1 <br>";
    		bear += 		"Special Ability: Fire resistant <br>";
    		CharSequence styledBearText = Html.fromHtml(bear);
		    enemyText.setText(styledBearText);
    		break;
    	case R.drawable.misspiggy:
    		String pig = "<b>Mr Bear:</b> <br>";
    		pig += 		"Mr Bear is going to dig in the garden, stop him! <br>";
    		pig += 		"Health: 10000 <br>";
    		pig += 		"Gold value: 1 <br>";
    		pig += 		"Special Ability: None <br>";
    		CharSequence styledPigText = Html.fromHtml(pig);
		    enemyText.setText(styledPigText);
    		break;
    	}
    	
    	TextView t1Text = (TextView) findViewById(R.id.t1Text);
    	String t1 = "<b>Cannon tower (frost):</b>" + "<br>";
    	t1 += 		"This tower is clearly falling in love <br>";
    	t1 += 		"Price: 10 <br>";
    	t1 += 		"Resell value: 8 <br>";
    	t1 += 		"Range: 150 <br>";
    	t1 += 		"Max damage: 50 <br>";
    	t1 += 		"Min damage: 20";
    	CharSequence styledT1Text = Html.fromHtml(t1);
	    t1Text.setText(styledT1Text);
	    
	    TextView t2Text = (TextView) findViewById(R.id.t2Text);
    	String t2 = "<b>AOE tower:</b> <br>";
    	t2 += 		"This tower is a sour tower <br>";
    	t2 += 		"Price: 10 <br>";
    	t2 += 		"Resell value: 8 <br>";
    	t2 += 		"Range: 70 <br>";
    	t2 += 		"Max damage: 20 <br>";
    	t2 += 		"Min damage: 10";
    	CharSequence styledT2Text = Html.fromHtml(t2);
	    t2Text.setText(styledT2Text);
	    
	    TextView t3Text = (TextView) findViewById(R.id.t3Text);
    	String t3 = "<b>Angry tower:</b> <br>";
    	t3 += 		"This tower is an angry tower <br>";
    	t3 += 		"Price: 10 <br>";
    	t3 += 		"Resell value: 8 <br>";
    	t3 += 		"Range: 70 <br>";
    	t3 += 		"Max damage: 20 <br>";
    	t3 += 		"Min damage: 10";
    	CharSequence styledT3Text = Html.fromHtml(t3);
	    t3Text.setText(styledT3Text);
	    
	    TextView t4Text = (TextView) findViewById(R.id.t4Text);
    	String t4 = "<b>Sad tower:</b> <br>";
    	t4 += 		"This tower is a sad tower <br>";
    	t4 += 		"Price: 10 <br>";
    	t4 += 		"Resell value: 8 <br>";
    	t4 += 		"Range: 70 <br>";
    	t4 += 		"Max damage: 20 <br>";
    	t4 += 		"Min damage: 10";
    	CharSequence styledT4Text = Html.fromHtml(t4);
	    t4Text.setText(styledT4Text);
	    
	    TextView t5Text = (TextView) findViewById(R.id.t5Text);
    	String t5 = "<b>Doom tower:</b>" + "<br>";
    	t5 += 		"The tower of doom, it's the most nasty tower in the " + 
    	"neighbourhood <br>";
    	t5 += 		"Price: 10 <br>";
    	t5 += 		"Resell value: 8 <br>";
    	t5 += 		"Range: 70 <br>";
    	t5 += 		"Max damage: 20 <br>";
    	t5 += 		"Min damage: 10";
    	CharSequence styledT5Text = Html.fromHtml(t5);
	    t5Text.setText(styledT5Text);
    	
    	}
	
}