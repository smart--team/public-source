package com.keyue.qlm.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	 private SQLiteHelper helper;  
	    private SQLiteDatabase db;  
	      
	    public DBManager(Context context) {  
	        helper = new SQLiteHelper(context);
	    }  

	    
	    public void saveorupdate(String sql){
	    	db.beginTransaction();
	    	
	    	try {
	    		db.execSQL(sql);
	    		db.setTransactionSuccessful();
			}catch (Exception e) {
				// TODO: handle exception
			}finally{
				  db.endTransaction();
			}
	  
	    }
	    
	    public List<Object[]> sel(String sql,Object[] lies){
	    	ArrayList<Object[]> objects = new ArrayList<Object[]>();  
	    	 Cursor c = db.rawQuery(sql, null);  
	    	  while (c.moveToNext()) {  
	              Object[] objects2 = new Object[lies.length];
	              for(int i=0;i<lies.length;i++){
	            	  objects2[i]=c.getString(c.getColumnIndex(lies[i].toString()));
	              }
	              objects.add(objects2);
	          }  
	          c.close();  
	          return objects;  
	    }
	    public void closedb(){
	    	db.close();
	    }
	    public void opendb(){
	    	 db = helper.getWritableDatabase();
	    }
}
