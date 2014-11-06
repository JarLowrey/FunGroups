package models;

import java.util.ArrayList;

/**
 * @author James Lowrey
 * 
 * the class for modeling flash cards. contains methods for accessing current card and incrementing to the next one. Cards are selected randomly.
 */
public class FlashCardModel {

	String[] fgArray;//current, full list of flash cards
	ArrayList<String> fgAL=new ArrayList<String>();//arraylist to make it easy to select random cards
	String currentFG;// current card
	
	public FlashCardModel(String[] tmpArray){
		fgArray=tmpArray;
		
		for(int i=0;i<fgArray.length;i++){
			fgAL.add(fgArray[i]);
		}
		currentFG=fgAL.remove(0);
	}
	
	public String getCurrentFG(){
		return currentFG;
	}
	
	public String getNextFG(){
		if(fgAL.size()>0){
			int pos=(int)(Math.random()*fgAL.size());
			currentFG=fgAL.remove(pos);
			}
		else{
			for(int i=0;i<fgArray.length;i++){
				fgAL.add(fgArray[i]);
			}
			currentFG=fgAL.remove(0);
		}
		return currentFG;
	}
}
