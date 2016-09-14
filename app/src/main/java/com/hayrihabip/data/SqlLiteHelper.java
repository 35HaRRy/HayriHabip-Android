package com.hayrihabip.data;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlLiteHelper extends SQLiteOpenHelper{
	static final String DBNAME = "SQLDB";
	static final int version = 1;
	
	private static SqlLiteHelper sqlLiteHelper;
	
	private SqlLiteHelper(Application mApp, String dbName) {
		super(mApp, dbName, null, version);
	}
	
	public static SqlLiteHelper getInstance(Application mApp) {				
		if (sqlLiteHelper == null)
			sqlLiteHelper = new SqlLiteHelper(mApp, DBNAME);
            
        return sqlLiteHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
