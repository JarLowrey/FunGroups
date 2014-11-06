package com.jtronlabs.fun_groups;

import com.jtronlabs.fun_groups.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

//@author James Lowrey

public class MainMenuActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		
		/*App Main Menu*/
		Button playBtn = (Button) findViewById(R.id.fgButton);
		playBtn.setOnClickListener(this);
		Button settingsBtn = (Button) findViewById(R.id.myCardsBtn);
		settingsBtn.setOnClickListener(this);
		Button rulesBtn = (Button) findViewById(R.id.practiceButton);
		rulesBtn.setOnClickListener(this);
	}

	/**
	 * Implementation of required method from OnClickListener-grants click functionality
	 */
	@Override
	public void onClick(View v) {
		Intent i;
		
		switch (v.getId()){
		case R.id.fgButton :
			i = new Intent(this, FlashCardsActivity.class);
			startActivity(i);
			break;
			
		case R.id.myCardsBtn :
			i = new Intent(this, MyDecksActivity.class);
			startActivity(i);
			break;
			
		case R.id.practiceButton :
			i = new Intent(this, PracticeActivity.class);
			startActivity(i);
			break;
		}	
	}
	
}
