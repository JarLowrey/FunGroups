package com.jtronlabs.fun_groups;

import models.FlashCardModel;
import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import database.FGAdapter;

/**
 * 
 * @author James Lowrey
 * 
 * The controller between the flashcard model and the flash card layout
 *
 */
public class FlashCardsActivity extends Activity implements OnClickListener{

	private FlashCardModel myModel;	
	private TextView frontCardTxt;
	private ImageView currentFGPic, flashCardPic;
	
	/*
	 * When click on screen dsplayText will show the TextView if true ImageView if false
	 * orderForward determines if the cards go text->pic or pic->text
	 */
	private boolean displayText=true, orderForward=true; 
	private String name;
	
	//Database
	private FGAdapter mDbHelper;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fgflash);
		//force into Landscape mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	    
		//action bar
		ActionBar myBar=getActionBar();	    
		myBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		myBar.setCustomView(R.layout.action_bar_flash_cards);
		Button backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		Button flashCardBtn = (Button)findViewById(R.id.flashCardBtn);
		flashCardBtn.setOnClickListener(this);
		
		//turn on click listeners for the card image and the text, gesturelistener for reversing card direction
	    flashCardPic =(ImageView) findViewById(R.id.flashCardImage);
	    flashCardPic.setOnClickListener(this);
	    frontCardTxt = (TextView) findViewById(R.id.fgNameTextView);
	    frontCardTxt.setOnClickListener(this);
	    currentFGPic=(ImageView) findViewById(R.id.fgImageView);
	    currentFGPic.setOnClickListener(this);
		  
	    //Set up model class using the database
	    mDbHelper = new FGAdapter(this);
        mDbHelper.open();
        
        name = mDbHelper.currentlySelectedListName();
        myModel = new FlashCardModel(mDbHelper.fetchFGList(name));
        
        mDbHelper.close();
		
        setUpCard();
	}
	/**
	 * Implementation of required method from OnClickListener-grants click functionality
	 */
	@Override
	public void onClick(View v) {
		int id=v.getId();
		switch(id){
		case R.id.flashCardBtn:
			switchOrder();
			break;
		case R.id.fgNameTextView:
			setUpCard();
			break;
		case R.id.flashCardImage:
			setUpCard();
			break;
		case R.id.fgImageView:
			setUpCard();
			break;
		case R.id.backBtn:
			finish();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Method to flip cards. 
	 */
	private void setUpCard(){
		//update card
			if(displayText){
				currentFGPic.setVisibility(View.GONE); //hide picture of FG, take up no layout space
				frontCardTxt.setVisibility(View.VISIBLE); //show  text
				
				if(orderForward){
					frontCardTxt.setText(myModel.getNextFG()); //set text to new FG
					flashCardPic.setImageResource(R.drawable.empty_flash_card_front);
				}else{
					frontCardTxt.setText(myModel.getCurrentFG());
					flashCardPic.setImageResource(R.drawable.empty_flash_card_back);
				}
			}else{
				frontCardTxt.setVisibility(View.GONE); 
				currentFGPic.setVisibility(View.VISIBLE); 
				
				if(orderForward){
					currentFGPic.setImageResource(getFGPicSource(myModel.getCurrentFG())); 
					flashCardPic.setImageResource(R.drawable.empty_flash_card_back);
				}else{
					currentFGPic.setImageResource(getFGPicSource(myModel.getNextFG())); 
					flashCardPic.setImageResource(R.drawable.empty_flash_card_front);
				}
				
			}
			displayText=!displayText;
	}
	
	/**
	 * Method to change the order flash cards are displayed. Causes the flash card picture to be set to the front. if the order is forward then cards are text->pic, if order is backwards it is opposite
	 */
	private void switchOrder(){
		//display toast	
		if(orderForward){
        	Toast.makeText(getApplicationContext(), "Given name, learn structures",
					   Toast.LENGTH_SHORT).show();}
        	else{
        		Toast.makeText(getApplicationContext(), "Given structure, learn names",
    					   Toast.LENGTH_SHORT).show();
        	}
		flashCardPic.setImageResource(R.drawable.empty_flash_card_front);//flash card is on front
			if(orderForward){
				frontCardTxt.setVisibility(View.GONE); 
				currentFGPic.setVisibility(View.VISIBLE); 
				currentFGPic.setImageResource(getFGPicSource(myModel.getNextFG())); 
				displayText=true;//on next screen click display text
			}else{
				frontCardTxt.setVisibility(View.VISIBLE); 
				currentFGPic.setVisibility(View.GONE); 
				frontCardTxt.setText(myModel.getNextFG()); 
				displayText=false;//on next screen click display picture
			}
		orderForward=!orderForward;
	}
	
	
	public int getFGPicSource(String name){
		//look in the R.java file for the appropriate ID
		int resID = getResources().getIdentifier(name.replaceAll(" ", "_"), "drawable", getPackageName());
		return resID;
	}
}
