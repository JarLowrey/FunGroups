package models;

import java.util.ArrayList;

import android.content.Context;

import com.jtronlabs.fun_groups.R;

import database.FGAdapter;

//@author James Lowrey

public class QuestionModel {
	Context myContext;
	boolean textAnswers=true;
	int numQ, numCorrect=0,currentFGNum;
	String[] fgList;
	String currentFG;
	
	private FGAdapter mDbHelper;

	
	public QuestionModel(Context ctx){
		myContext=ctx;
		
		mDbHelper = new FGAdapter(myContext);
        mDbHelper.open();
//        mDbHelper.addDefault();
        
        String name = mDbHelper.currentlySelectedListName();
        fgList = mDbHelper.fetchFGList(name);
        shuffleArray(fgList);
        currentFGNum=0;
        
        if(fgList.length>15){//max number of questions = 15
        	numQ=15;
        }else{
            numQ=fgList.length;
        }
	}
	
	public String getTextQ(){
		return fgList[currentFGNum++];
	}
	
	public int getImageSourceQ(){
		return getFGPicSource(fgList[currentFGNum++]);
	}
	
	public String[] getAns(){
		String[] ans = {fgList[currentFGNum],"","",""};
		
		//NOTE: There must be more than 4 FG in database/in the string array fg_array for this to work.
		String[] currentList = fgList;
		if(fgList.length<5){
			currentList = myContext.getResources().getStringArray(R.array.fg_array);
		}
		
		ArrayList<String> checkList = new ArrayList<String>();
		checkList.add(fgList[currentFGNum]);
		
		for(int i=1;i<4;i++){
			String item=fgList[currentFGNum];
			while(checkList.contains(item)){
				int index = (int)(Math.random()*currentList.length);
				item=currentList[index];
			}
			checkList.add(item);
			ans[i]=item;
		}
		
		return ans;
	}
	
	public int numQuestionsRemaining(){
		return numQ;
	}
	
	/**
	 * Modify the class for the answered question
	 * @param correct whether or not the question was answered correctly
	 */
	public void questionAnswered(boolean correct){
		if(correct){
			this.numCorrect++;
		}
		this.textAnswers=!textAnswers;
		numQ--;
	}
	public boolean showTextAnswers(){
		return textAnswers;
	}
	public int getNumCorrect(){
		return this.numCorrect;
	}
	
	public int getFGPicSource(String name){
		//look in the R.java file for the appropriate ID
		int resID = myContext.getResources().getIdentifier(name.replaceAll(" ", "_"), "drawable", myContext.getPackageName());
		return resID;
	}
	
	public void shuffleArray(String[] ar){
	    for (int i = 1;i<ar.length;i++)
	    {
	      int index = (int)(Math.random()*i);
	      // swapping, inefficient for large array
	      String a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
}
}
