package com.keyue.qlm.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
	 private static final String DATABASE_NAME = "qlm.db";  
	    private static final int DATABASE_VERSION = 1;  
	

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL("CREATE TABLE IF NOT EXISTS user" +  
	                "(_id INTEGER PRIMARY KEY AUTOINCREMENT,userid VARCHAR, username VARCHAR, userimage VARCHAR,userphone VARCHAR,userwd VARCHAR,userjd VARCHAR,useraddress VARCHAR,useremail VARCHAR)"); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME);   
		        onCreate(db);   
	}

}
