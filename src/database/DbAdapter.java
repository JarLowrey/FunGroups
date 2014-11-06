package database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//@author James Lowrey

public abstract class DbAdapter {
	
    private static String strSeparator = " , ";//variable used for converting string arrays, relevant methods at bottom of class

    public static String DATABASE_NAME = "data.db";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE1 = "FGTable";
    public static final String DATABASE_TABLE2 = "PracticeTable";

    public static DatabaseHelper ourHelper;
    public Context ctx;
    public SQLiteDatabase mDb;

    public boolean ourConstructorBool = true;//Used to determine if database has already been constructed
    public boolean ourDB = true;

    public static final String FGTable = "CREATE TABLE IF NOT EXISTS "+DATABASE_TABLE1+" (" +
        FGAdapter.KEY_ROWID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
        FGAdapter.KEY_NAME+" TEXT NOT NULL, " +
        FGAdapter.KEY_LIST+" TEXT NOT NULL, " +
        FGAdapter.KEY_SELECTED+" INTEGER DEFAULT 0 );";

     static class DatabaseHelper extends SQLiteOpenHelper{
    	 public static DatabaseHelper getInstance(Context context) {
     		// http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
     	    if (ourHelper == null) {
     	      ourHelper = new DatabaseHelper(context.getApplicationContext());
     	    }
     	    return ourHelper;
     	 }
    	 
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(FGTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE2);
            onCreate(db);
        }
    }

    public DbAdapter(Context mctx){   
        ctx = mctx;
        this.ourHelper = ourHelper.getInstance(ctx);
    }

    public DbAdapter open() throws SQLException{
    	ourHelper = new DatabaseHelper(ctx);
    	mDb = ourHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        if(mDb.isOpen())
            ourHelper.close();
    }
    
    /**
	* convert string array to a delimited string so that it may be stored in a database
	* 
	* @param array string array to be stored in database
	* @return a string with values from the array delimited using variable strSeperator
	*/
	public String convertArrayToString(String[] array){
		 String str = "";
		 for (int i = 0;i<array.length; i++) {
			 // Do not append comma at the end of last element
		     if(i<array.length-1){
		    	 str = str+array[i]+strSeparator;
		     }else{
		    	 str = str+array[i];
		     }
		 }
		 return str;
	}
	
	/**
	* convert a delimited string back into a string array when accessing the database.
	* @param str delimited String
	* @return array of strings from database
	*/
	public String[] convertStringToArray(String str){
		String[] arr = str.split(strSeparator);
		return arr;
	}
}