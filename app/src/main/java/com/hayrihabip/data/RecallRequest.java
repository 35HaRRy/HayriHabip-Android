package com.hayrihabip.data;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.hayrihabip.items.Functions;

public class RecallRequest extends BroadcastReceiver {
	private Application mApp;
	private RestService rS;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mApp = (Application)context.getApplicationContext();
		
		if(Functions.checkConnection(mApp)){
			rS = new RestService(mApp);
			
			String isExist = "Select RequestName from CACHES where Status = 0";
			Cursor cursor = rS.db.rawQuery(isExist, null);

			String requestName;
			String[] items;
			
			String pageName;
			String functionName;
			Map<String, Object> values = new HashMap<String, Object>();
			
			if(cursor.moveToFirst()){
				do {
					requestName = cursor.getString(cursor.getColumnIndex("RequestName"));
					items = requestName.split("/");
					
					pageName = items[0];
					functionName = items[1];
					
					for (String value : items[2].split(",")) 
						values.put(value.split("=")[0], value.split("=")[1]);
					
					//Burada 
					rS.call(true, pageName, functionName, values);
					
				} while (cursor.moveToNext());
			}
		}
	}
}