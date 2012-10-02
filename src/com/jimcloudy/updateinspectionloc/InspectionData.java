package com.jimcloudy.updateinspectionloc;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InspectionData {
	private static final String TAG = InspectionData.class.getSimpleName();
	static final String DATABASE = "inspections.db";
	static final int VERSION = 1;
	static final String TABLE = "inspections";
	static final String C_ID = "_id";
	static final String C_NAME = "name";
	static final String C_NAME2 = "name2";	
	static final String C_ADDRESS = "address";
	static final String C_ADDRESS2 = "address2";
	static final String C_CITYST = "cityst";
	//static final String C_STATE = "state";
	static final String C_PHONE = "phone";
	static final String C_ZIP = "zip";
	static final String C_LAT = "lat";
	static final String C_LONG = "long";
	private static final String GET_ALL_ORDER_BY = C_ID;
	private static final String[] DB_TEXT_COLUMNS = { C_ID, C_NAME, C_NAME2, C_ADDRESS, C_ADDRESS2, C_CITYST, C_ZIP, C_PHONE};
	
	Context context;
	
	class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
		    super(context, DATABASE, null, VERSION);
		}

	    @Override
	    public void onCreate(SQLiteDatabase db) {
	      String sql = "CREATE TABLE inspections ( _id text primary key , name text, name2 text, address text, address2 text, cityst text, zip text, phone text, lat text, long text );";
	      db.execSQL(sql);
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	      db.execSQL("drop table " + TABLE);
	      this.onCreate(db);
	    }
	  }
	
	final DbHelper dbHelper;
	
	public InspectionData(Context c){
		this.dbHelper = new DbHelper(c);
		context = c;
	}
	
	public void close(){
		this.dbHelper.close();
	}
	
	public void insertOrIgnore(ContentValues values){
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		
		try{		
			 db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		}
		finally{
			db.close();
		}
	}
	
	public Cursor getInspections(){
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(TABLE, null, C_LAT + " is null and " + C_LONG + " is null", null, null, null, GET_ALL_ORDER_BY);
	}
	
	public Cursor getUpdatedInspections(){
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(TABLE, null, C_LAT + " not null and " + C_LONG + " not null", null, null, null, GET_ALL_ORDER_BY);
	}
	
	public String getInspectionByPolicy(String policy) {
	    SQLiteDatabase db = this.dbHelper.getReadableDatabase();
	    try {
	      Cursor cursor = db.query(TABLE, DB_TEXT_COLUMNS, C_ID + "=" + policy, null,
	          null, null, null);
	      try {
	        return cursor.moveToNext() ? cursor.getString(0) : null;
	      } finally {
	        cursor.close();
	      }
	    } finally {
	      db.close();
	    }
	  }
	
	public boolean updateInspectionByPolicy(String[] policy, ContentValues values) {
	    SQLiteDatabase db = this.dbHelper.getWritableDatabase();
	    boolean flag;
	    try {
	    	db.update(TABLE, values, C_ID + "=?", policy);
	    	flag = true;
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    	flag = false;
	    }
	    finally {
	      db.close();
	    }
	    return flag;
	  }
	
	public void deleteUpdatedInspections(List<String> policies) {
	    SQLiteDatabase db = this.dbHelper.getWritableDatabase();
	    try {
	    	for(String policy : policies)
	    	{
	    		String[] arg = new String[]{policy};
	    		db.delete(TABLE, C_ID + "=?", arg);
	    	}
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	    finally {
	      db.close();
	    }
	  }
	
	public void delete() {
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
   	    db.delete(TABLE, null, null);
	    db.close();
	  }
}
