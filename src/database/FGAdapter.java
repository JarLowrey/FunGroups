package database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.jtronlabs.fun_groups.R;

//@author James Lowrey

public class FGAdapter extends DbAdapter{

	//data in the fglists table
    public static final String KEY_NAME = "name";
    public static final String KEY_LIST = "list";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_SELECTED = "selected";//boolean whether or not button is the selected one

    //private static final String TAG = "FGlistsDbAdapter";


    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     * @return 
     */
    public FGAdapter(Context ctx) {
        super(ctx);
    }
    @Override
    public FGAdapter open() throws SQLException{
    	super.open();
    	if(!dbContainsListName("All")){
    		String[] list = ctx.getResources().getStringArray(R.array.fg_array); 
    		createFGList("All",list,true);
    	}
		return null;
    	
    }
    
//    /**
//     * Call this when instantiating the Db.
//     */
//    public void addDefault(){
//    	if(!this.dbContainsListName("All")){
//    		Resources res = ctx.getResources();
//        	String[] allFgs = res.getStringArray(R.array.fg_array); 
//        	this.createFGList("All", allFgs, true);
//    	}
//    }

    /**
     * Create a new FGlist using the name and list provided. If the FGlist is
     * successfully created return the new rowId for that FGlist, otherwise return
     * a -1 to indicate failure.
     * 
     * @param name the name of the FGlist
     * @param list the list of the FGlist
     * @return rowId or -1 if failed
     */
    public long createFGList(String name, String[] list, boolean checked) {
        ContentValues initialValues = new ContentValues();
        
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_LIST, this.convertArrayToString(list));
        
        if(checked){
        	initialValues.put(KEY_SELECTED, 1);
        }else{
        	initialValues.put(KEY_SELECTED, 0);
        }

        return mDb.insert(DATABASE_TABLE1, null, initialValues);
    }
    
    //UNEEDED METHOD
//    /**
//     * Delete the FGlist with the given rowId
//     * 
//     * @param rowId id of FGlist to delete
//     * @return true if deleted, false otherwise
//     */
//    public boolean deleteFGList(long rowId) {
//        return mDb.delete(DATABASE_TABLE1, KEY_ROWID + "=" + rowId, null) > 0;
//    }
    
    /**
     * Delete the FGlist with the given name
     * 
     * @param name it is the name of the list to be deleted
     * @return true if deleted, false otherwise
     */
    public boolean deleteFGList(String name) {
        return mDb.delete(DATABASE_TABLE1, KEY_NAME + " = " + "\"" + name + "\"", null) > 0;
        /*
         * ---VERY IMPORTANT NOTE---
         * When querying using a string, the string must be wrapped in quotations
         */
    }

    /**
     * Return a Cursor over the list of all FGlists in the database
     * 
     * @return Cursor over all FGlists
     */
    public Cursor fetchAllFGLists() {
        return mDb.query(DATABASE_TABLE1, new String[] {KEY_ROWID, KEY_NAME,
                KEY_LIST,KEY_SELECTED}, null, null, null, null, null);
    }

    /**
     * 
     * @return a string[] of all the names of the lists in the database
     */
    public String[] allFgNames(){
    	ArrayList<String> names = new ArrayList<String>();
		Cursor c= fetchAllFGLists();
		if(c.moveToFirst()) {
		    do{
		        int columnIndex=c.getColumnIndex(KEY_NAME);
		        names.add(c.getString(columnIndex));
		    }while(c.moveToNext()); 
		}
		return names.toArray(new String[names.size()]);
    }
    
    //UNEEDED METHOD
//    /**
//     * Return a Cursor positioned at the FGlist that matches the given rowId
//     * 
//     * @param rowId id of FGlist to retrieve
//     * @return Cursor positioned to matching FGlist, if found
//     * @throws SQLException if FGlist could not be found/retrieved
//     */
//    public Cursor fetchFGList(long rowId) throws SQLException {
//        Cursor mCursor =
//
//            mDb.query(true, DATABASE_TABLE1, new String[] {KEY_ROWID,
//                    KEY_NAME, KEY_LIST,KEY_SELECTED}, KEY_ROWID + "=" + rowId, null,
//                    null, null, null, null);
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//    }
//    
    /**
     * Return a list from Database given the KEY_NAME of that list 
     * 
     * @param name the name of FGlist to retrieve
     * @return String[] the entries corresponding to that name
     * @throws SQLException if FGlist could not be found/retrieved
     */
    public String[] fetchFGList(String name) throws SQLException {
        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE1, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_LIST,KEY_SELECTED}, KEY_NAME + "=" + "\"" + name + "\"", null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
		String list=mCursor.getString(mCursor.getColumnIndex(KEY_LIST));
        return this.convertStringToArray(list);
    }
    
    /**
     * Returns currently selected list utilizing the database
     * 
     * @return String the name of currently selected list
     * @throws SQLException if FGlist could not be found/retrieved
     */
    public String currentlySelectedListName() throws SQLException {
        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE1, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_LIST,KEY_SELECTED}, KEY_SELECTED + "=" + 1, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
		String name=mCursor.getString(mCursor.getColumnIndex(KEY_NAME));
        return name;
    }

    /**
     * Update the FGlist using the details provided. The FGlist to be updated is
     * specified using the rowId, and it is altered to use the name and list
     * values passed in
     * 
     * @param name value to set FGlist name to
     * @param list value to set FGlist list to
     * @return true if the FGlist was successfully updated, false otherwise
     */
    public boolean updateFGList(String name, String[] list, boolean checked) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_LIST, this.convertArrayToString(list));
        int innerUpdateCheck= -1;
        
        if(checked){
        	//The currently Selected list must be unselected
        	Cursor c = mDb.query(true, DATABASE_TABLE1, new String[] {KEY_ROWID,
					KEY_NAME, KEY_LIST}, KEY_SELECTED + "=1", null,
					null, null, null, null);
        	if(c.moveToFirst()){
        		String newName=c.getString(c.getColumnIndex(KEY_NAME));
        		String newList=c.getString(c.getColumnIndex(KEY_LIST));
        		
        		ContentValues newArgs = new ContentValues();
                newArgs.put(KEY_NAME, newName);
                newArgs.put(KEY_LIST, newList);
                newArgs.put(KEY_SELECTED, 0);
                
		        innerUpdateCheck=mDb.update(DATABASE_TABLE1, newArgs, KEY_NAME + " = " + "\""+newName+"\"" , null);
        	}
        	//the list to be entered will now be selected
        	args.put(KEY_SELECTED, 1);
        }else{
        	args.put(KEY_SELECTED, 0);
        }

        return mDb.update(DATABASE_TABLE1, args, KEY_NAME + " = " + "\""+name+"\"" , null) > 0 || innerUpdateCheck > 0;
    }
    
    /**
     * Search database to see if a given name is already utilized
     * 
     * @param name 
     * @return true if the name is used, false otherwise
     */
    public boolean dbContainsListName(String name) throws SQLException{
    	//using a rawQuery function (just a different way to do it)
//    	Cursor c = mDb.rawQuery("SELECT * FROM " + DATABASE_TABLE1+ " WHERE " + KEY_NAME + "= ?; ",new String[]{"\""+name+"\""});
//    	return c.moveToFirst();
    	Cursor c = mDb.query(true, DATABASE_TABLE1, new String[] {KEY_ROWID,
    					KEY_NAME, KEY_LIST, KEY_SELECTED}, KEY_NAME + "=" + "\""+ name + "\"", null,
    					null, null, null, null);
    	
    	// query() call never returns null, only throws exception. To see if the query() call found a value, use c.moveToFirst()
    	return c.moveToFirst();
    }
    
    /**
     * Search database to see if a given list is already utilized
     * 
     * @param list 
     * @return true if the name is used, false otherwise
     */
    public boolean dbContainsList(String[] list) throws SQLException{
    	Cursor c = mDb.query(true, DATABASE_TABLE1, new String[] {KEY_ROWID,
    					KEY_NAME, KEY_LIST, KEY_SELECTED}, KEY_LIST + "=" + "\""+ this.convertArrayToString(list) + "\"", null,
    					null, null, null, null);
    	return c.moveToFirst();
    }
}
