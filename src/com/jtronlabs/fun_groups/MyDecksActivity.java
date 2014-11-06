package com.jtronlabs.fun_groups;

import java.util.ArrayList;

import models.QuestionModel;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jtronlabs.fun_groups.R;

import database.FGAdapter;

//@author James Lowrey

public class MyDecksActivity extends Activity implements OnClickListener{
    
    //all of the views to be displayed in this activity
	private EditText listName;
	private TextView explain;
	private RadioGroup group;
	private CheckBox[] ckBoxes;
	private Button createNew, submit, cancel, deleteOld, viewList;
	private String currentSelectedListName;
		
	//Database
	private FGAdapter mDbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_fgcards);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//action bar
		ActionBar myBar=getActionBar();	    
		myBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		myBar.setCustomView(R.layout.action_bar_default);
		Button backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		
		//Set up database and the views to be manipulated
		setContentView(R.layout.activity_my_fgcards);
		LinearLayout currentLayout=(LinearLayout)findViewById(R.id.my_fg_cards);//use to add items to layout
		LinearLayout externalLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.custom_xml_views,null);//Awesome workaround to set create button's attributes to predefined xml attributes
		mDbHelper = new FGAdapter(this);
        mDbHelper.open();
        
        //Short explanatory blurb
        explain = (TextView)this.findViewById(R.id.myCardsExplained);
        
		//Set up radio buttons
		group=new RadioGroup(this);
		populateRadioGroupFromDb();//I automated the button adding process
		currentLayout.addView(group);
		
		//This button is used to create a new list
		createNew = (Button) externalLayout.findViewById(R.id.createNewBtn);
		externalLayout.removeView(createNew);//Need to removeView from externalLayout in order to add it to currentLayout
		currentLayout.addView(createNew);
		createNew.setOnClickListener(this);
		
		//Button used to delete old list
		deleteOld = (Button) externalLayout.findViewById(R.id.deleteOldBtn);
		externalLayout.removeView(deleteOld);
		currentLayout.addView(deleteOld);
		deleteOld.setOnClickListener(this);
		
		//Button used to view list
		viewList = (Button) externalLayout.findViewById(R.id.viewListBtn);
		externalLayout.removeView(viewList);
		currentLayout.addView(viewList);
		viewList.setOnClickListener(this);
				
		//--------------SET UP CHECKBOXES--------------
		String[] allFGs = mDbHelper.fetchFGList("All");
		ckBoxes= new CheckBox[allFGs.length];
		for (int i=0;i<allFGs.length;i++) {
		    CheckBox ckBox=new CheckBox(this);
		    ckBox.setTextColor(Color.WHITE);
		    ckBox.setText(allFGs[i]);
		    ckBoxes[i]=ckBox;
		    currentLayout.addView(ckBox);
		}
		
		//Set up a place for user to type in name of new list and prevent weird text
		listName= new EditText(this);
		listName.setTextColor(Color.WHITE);
		listName.setHint("Deck Name:");
		InputFilter filter = new InputFilter() { 
	        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) { 
	                for (int i = start; i < end; i++) { 
	                        if (!Character.isLetterOrDigit(source.charAt(i))) { 
	                                return ""; 
	                        } 
	                } 
	                return null; 
	        } 
		}; 
		listName.setFilters(new InputFilter[]{filter});
		currentLayout.addView(listName);
		
		//This button is used to submit checkbox selections
		submit = (Button) externalLayout.findViewById(R.id.submitBtn);
		externalLayout.removeView(submit);
		currentLayout.addView(submit);
		submit.setOnClickListener(this);
		
		//This button is used to cancel new list creation, if the createNewBtn was accidentally clicked
		cancel= (Button) externalLayout.findViewById(R.id.cancelBtn);
		externalLayout.removeView(cancel);
		currentLayout.addView(cancel);
		cancel.setOnClickListener(this);
		
		//hide and show the relevant buttons and views
		setUpView(true);
		
		
		//This code ensures ALL of the strings located in the array have matching pictures in the drawable folder
		///////////////////////////////////////////////////////////////////////////////
//		QuestionModel a = new QuestionModel(this);
//		Log.d("as","fgf");
//		String[] list = this.getResources().getStringArray(R.array.fg_array);
//		Log.d("asdasd","fgf");
//		for(String item:list){
//			Log.d(item,"fgf");
//			Drawable dr = getResources().getDrawable(a.getFGPicSource(item));
//			Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
//			Drawable d = new BitmapDrawable(Bitmap.createScaledBitmap(bitmap, 100, 100, true));
//			
//			ImageView ab = new ImageView(this);
//			ab.setImageDrawable(d);
//			currentLayout.addView(ab);
//		}
		///////////////////////////////////////////////////////////////////////////////
		
		
		
		
		
		
		
		
		
		
		
	}
	
	@Override
	public void onClick(View v) {
		int id=v.getId();	
		switch(id){
		case R.id.submitBtn:
			//Grab the new name and list of strings
			String currentName=listName.getText().toString();
			listName.setText("");
			ArrayList<String> newFGListAL = new ArrayList<String>();
			for(CheckBox tempBox:ckBoxes){
				if(tempBox.isChecked()){
					newFGListAL.add(tempBox.getText().toString());
				}
			}
			String[] newFGListArray = newFGListAL.toArray(new String[newFGListAL.size()]);
			
			//check to see if the name and list are ok, if so add to Database. If not, give error message
			if(isAcceptableDbEntry(currentName, newFGListArray)){
				//add new list to the database and the view
				mDbHelper.createFGList(currentName, newFGListArray, false);
				this.populateRadioGroupFromDb();
				
				setUpView(true);
			}
			
			else{ //the new list name won't work, user must re-enter it. Use an AlertDialog
				showErrorDialog("New Deck Error", "New entries must be non empty and unique");
			}
			break;
		case R.id.createNewBtn:
			//clear the check boxes and the text input areas, then set up these views
			for(int i=0;i<ckBoxes.length;i++){
				ckBoxes[i].setChecked(false);
			}
			listName.setText("");
			setUpView(false);
			break;
		case R.id.cancelBtn:
			setUpView(true);
			break;
		case R.id.deleteOldBtn:
			//Find which list is to be deleted
			RadioButton selectedBtn=(RadioButton)findViewById(group.getCheckedRadioButtonId());
			currentSelectedListName=selectedBtn.getText().toString();
			
				if(!currentSelectedListName.equals("All")){//check if the list is the default
					AlertDialog.Builder builder = new AlertDialog.Builder(this)
			        .setTitle("")
				        .setMessage("Delete: "+currentSelectedListName+" ?")
				        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				            @Override
				            public void onClick(DialogInterface dialog, int which) {
				            	//bad way to do this, but whatever...
				                   alertOnPositiveClick();
				            }
				        })
						.setNegativeButton(R.string.cancel,null);
					AlertDialog alert = builder.create();
					alert.show();					
				}  else{//if it is the default (That is, the name of the list is "All") tell user of error
					showErrorDialog("Delete Error","The default deck cannot be deleted");
				}
				break;
		case R.id.viewListBtn:
			RadioButton bttn=(RadioButton) findViewById(group.getCheckedRadioButtonId());
			
			String listName = bttn.getText().toString();
			String[] listArray = mDbHelper.fetchFGList(listName);
			
			String list="";
			for(int i=0;i<listArray.length;i++){
				if(i==listArray.length-1){
					list=list+listArray[i];
				}else{
					list=list+listArray[i]+"\n";
				}
			}
			this.showErrorDialog(listName, list);
			break;
		case R.id.backBtn:
			finish();
			break;
		}
	}
	
	/**
	 * 
	 */
	public void alertOnPositiveClick(){
		mDbHelper.deleteFGList(currentSelectedListName);
        this.populateRadioGroupFromDb();
	}
	
	/**
	 * When exiting this activity update the database to make the currently selected RadioButton the database's selected entry
	 */
	@Override
	public void onBackPressed() {
		RadioButton rdBtn = (RadioButton)findViewById(group.getCheckedRadioButtonId());
		String name = rdBtn.getText().toString();
		mDbHelper.updateFGList(name, mDbHelper.fetchFGList(name), true);
		finish();
	    return;
	}  
	
	/**
	 * Simplify code to switch between the two different purposes of this Activity
	 * @param radioBtnsOn
	 */
	private void setUpView(boolean radioBtnsOn) {
		int radioBtnView, ckBtnView;
		if(radioBtnsOn){
			radioBtnView=View.VISIBLE;
			ckBtnView=View.GONE;
		}else{
			radioBtnView=View.GONE;
			ckBtnView=View.VISIBLE;
		}
		group.setVisibility(radioBtnView);
		createNew.setVisibility(radioBtnView);
		deleteOld.setVisibility(radioBtnView);
		viewList.setVisibility(radioBtnView);
		explain.setVisibility(radioBtnView);
		
		
		for(int i=0;i<ckBoxes.length;i++){ 
			ckBoxes[i].setVisibility(ckBtnView);
		}
		listName.setVisibility(ckBtnView);
		submit.setVisibility(ckBtnView);
		cancel.setVisibility(ckBtnView);
	}
	
	/**
	 * Check to see if proposed entry is a-OK to be put into database. Proposed entry must have unique name, unique list, and neither can be empty.
	 * @param name 
	 * @param list all functional groups that are proposed to be entered into the database
	 * @return a boolean that is true if the entry passes test
	 */
	public boolean isAcceptableDbEntry(String name, String[] list){
		if(name.length()==0 || list.length==0 || mDbHelper.dbContainsListName(name) || mDbHelper.dbContainsList(list)){
			return false;
		}
		return true;
	}
	
	public void populateRadioGroupFromDb(){
		//clear the group
		group.removeAllViews();
		
		//For every entry in the database, make a radiobutton out of it and add to the group
		String[] allFgNames=mDbHelper.allFgNames();
		for(String item: allFgNames){
			RadioButton radioButton=new RadioButton(this);
			radioButton.setText(item);
			radioButton.setTextColor(Color.WHITE);
			group.addView(radioButton);
			//default check the "All" radioButton
			if(radioButton.getText().equals("All")){
				group.check(radioButton.getId());
			}
		}
	}
	
	/**
	 * method to create an AlertDialog and show on the layout with given text and message.
	 * @param title displayed at top of dialog
	 * @param message displayed in the middle of dialog
	 */
	private void showErrorDialog(String title, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setTitle(title)
	        .setMessage(message)
	        .setPositiveButton(R.string.ok, null);
		AlertDialog alert = builder.create();
		alert.show();
	}
}
