package com.jtronlabs.fun_groups;

import models.QuestionModel;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


//@author James Lowrey

public class PracticeActivity extends Activity implements OnClickListener {

	private RadioGroup group;
	Toast toast;
	TextView textQ,toastTitle,toastMsg;
	ImageView imageQ,toastImage;
	QuestionModel myModel;
	LinearLayout currentLayout,externalLayout;
	String correctAns;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice);
		
		//action bar
		ActionBar myBar=getActionBar();	    
		myBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		myBar.setCustomView(R.layout.action_bar_default);
		Button backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		
		//setup toast
		LayoutInflater inflater = getLayoutInflater();
		toast = new Toast(getApplicationContext());
		View view = inflater.inflate(R.layout.toast_answer,
                (ViewGroup) findViewById(R.id.toast_answer_id));
		toast.setView(view);
		
		toastTitle = (TextView) view.findViewById(R.id.toastTitle);
		toastMsg = (TextView) view.findViewById(R.id.toastMsg);
		toastImage = (ImageView) view.findViewById(R.id.toastImage);

		toast.setDuration(Toast.LENGTH_SHORT);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		currentLayout=(LinearLayout) findViewById(R.id.practice_intro);//use to add items to layout
		externalLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.custom_xml_views,null);//Awesome workaround to set create button's attributes to predefined xml attributes
		myModel= new QuestionModel(this); 
		
		group = (RadioGroup) findViewById(R.id.answerGroup);
		
		textQ = (TextView)externalLayout.findViewById(R.id.textQuestion);
		externalLayout.removeView(textQ);
		currentLayout.addView(textQ,0);
		
		imageQ = (ImageView)externalLayout.findViewById(R.id.imageQ);
		externalLayout.removeView(imageQ);
		currentLayout.addView(imageQ , 0);
		
		Button submitBtn = (Button) findViewById(R.id.testSubmitBtn);
		submitBtn.setOnClickListener(this);
		
		setUpView(currentLayout);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()){
		//clicks submit button
		case R.id.testSubmitBtn :
			int id = group.getCheckedRadioButtonId();
			//no answer selected
			if(id<0){
				AlertDialog.Builder builder = new AlertDialog.Builder(this)
		        .setTitle("")
			        .setMessage("Select an answer")
			        .setPositiveButton(R.string.ok, null);
				AlertDialog alert = builder.create();
				alert.show();
			}else{
				//answer is selected. check text of radio button and see if it matches correct answer
				RadioButton btn = (RadioButton) findViewById(id);
				String txt=btn.getText().toString();
				boolean ansCorrect = txt.equals(this.correctAns);
				if(ansCorrect){
					Toast.makeText(getApplicationContext(), "Correct!",
		 					   Toast.LENGTH_SHORT).show();
				}else{
					//if answer is incorrect make toast showing correct name and image
					toastTitle.setText("Correct Answer:");
					toastMsg.setText(this.correctAns);
					toastImage.setImageResource(myModel.getFGPicSource(this.correctAns));
					
					toast.show();
				}
				myModel.questionAnswered(ansCorrect);
					
				this.setUpView(currentLayout);
			}
			group.clearCheck();
			break;
		case R.id.backBtn:
			finish();
			break;
		}	
	}
	
	/**
	 * using the QuestionModel, set up the layout completely
	 * @param currentLayout the layout to populate
	 */
	private void setUpView(LinearLayout currentLayout){
		if(myModel.numQuestionsRemaining()>0){
			
			TextView tmp = (TextView)findViewById(R.id.qRemain);
			tmp.setText("Questions Remaining: "+myModel.numQuestionsRemaining());
			
			String[] ans = myModel.getAns();
			correctAns=ans[0];
			myModel.shuffleArray(ans);//randomize
			
			if(myModel.showTextAnswers()){
				imageQ.setImageResource(myModel.getImageSourceQ());//gets next question
				imageQ.setVisibility(View.VISIBLE);
				textQ.setVisibility(View.GONE);
			
				for(int i=0;i<ans.length;i++){
					RadioButton btn = (RadioButton)group.getChildAt(i);
					btn.setText(ans[i]);
					//make text visible
					btn.setTextColor(Color.WHITE);
					//Remove image
					btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				}
			}else{
				textQ.setText(myModel.getTextQ());//gets next question
				imageQ.setVisibility(View.GONE);
				textQ.setVisibility(View.VISIBLE);
			
				int[] ids = new int[ans.length];
				
				for(int i =0;i<ans.length;i++){
					ids[i] = myModel.getFGPicSource(ans[i]);
				}
			
				for(int i=0;i<group.getChildCount();i++){
					RadioButton btn = (RadioButton) group.getChildAt(i);
					
					btn.setText(ans[i]);
					btn.setTextColor(Color.TRANSPARENT);
					//Display the image to the right of the button (left of text). 
					
					//Neat way to resize images
					Drawable dr = getResources().getDrawable(ids[i]);
					Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
					int len = (int)getResources().getDimension(R.dimen.practice_answer_image_length);
					Drawable d = new BitmapDrawable(Bitmap.createScaledBitmap(bitmap, len, len, true));
					
					btn.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
				}
			}
		}else{
			currentLayout.removeAllViews();
			TextView done = (TextView)externalLayout.findViewById(R.id.gameOverText);
			externalLayout.removeView(done);
			currentLayout.addView(done);
			done.setText("Congrats!\nYou got "+myModel.getNumCorrect()+" correct. Keep studying hard!");
		}
	}
}
