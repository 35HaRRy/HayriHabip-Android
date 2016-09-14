package com.hayrihabip.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.hayrihabip.R;
import com.hayrihabip.items.Functions;

public class RestService extends ContentProvider {
	private static final String PROVIDINGTABLES = "PROVIDINGTABLES";
	private static final String CACHES = "CACHES";
	
	public String domainName;
	public String dbName;
	public SQLiteDatabase db;
	
	public HttpClient httpClient;
	public HttpPost httpPost;
	
	private Application mApp;
	private Resources resource;
	
	private List<Map<String, Object>> result;
	private Map<String, Object> map;
	
	public RestService(){
	}
	
	public RestService(Application mApp){
		this(mApp,  mApp.getString(R.string.DomainName), "SQLDB");
	}
	
	public RestService(Application mApp, String domainName, String dbName){
		this.domainName = domainName;
		this.dbName = dbName;
		this.mApp = mApp; 
		resource = mApp.getResources();
		
		httpClient = new DefaultHttpClient();		
		db = SqlLiteHelper.getInstance(mApp).getWritableDatabase();

	    //Eğer istek bir sebepten dolayı yerine getirilememişse daha sonra bu isteğin tekrar denenmesi gerektiğini belirtmek için
	    //status kolonu 0 olarak kaydedilebilir.(Gerekli izinler verilmişse)
		String sql = "CREATE TABLE IF NOT EXISTS %s ( RequestName TEXT, Response TEXT, RegDate TEXT, Status INTEGER );";
		db.execSQL(String.format(sql, CACHES));
		
		//ContentProvider ile kullanılacak tabloları tutar.
		sql = "CREATE TABLE IF NOT EXISTS %s ( TableName TEXT, UseContentProvider INTEGER, RowCount INTEGER, ColumnCount INTEGER, RegDate TEXT );";
		db.execSQL(String.format(sql, PROVIDINGTABLES));
	}
	
	// int values are Column data type like R.string.COLUMN_TEXT
	@SuppressWarnings("unchecked")
	public void createTable(String tableName, Map<String, Integer> columns, Boolean useContentProvider){	
		if(!Functions.isTableExsists(db, tableName) && !tableName.equals(PROVIDINGTABLES)){
			String sqlCreateTable = "CREATE TABLE IF NOT EXISTS %s ( %s );";
			String sqlColumns = "";
			
			Iterator<?> it = columns.entrySet().iterator();
		    while (it.hasNext()) {
				Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();	
				sqlColumns += "," + pairs.getKey().toString() + " " + resource.getString(pairs.getValue());
		    }
		    
		    sqlColumns = sqlColumns.substring(1);		
			db.execSQL(String.format(sqlCreateTable, tableName, sqlColumns));
			
			//Insert table to ProvidingTables
			Calendar c = Calendar.getInstance();
		    String regDate = c.get(Calendar.DATE) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
		    
			ContentValues cv = new ContentValues();
			cv.put("TableName", tableName);
			cv.put("UseContentProvider", (useContentProvider ? 1 : 0));
			cv.put("RowCount", 0);
			cv.put("ColumnCount", columns.size());
			cv.put("RegDate", regDate);
			db.insert(PROVIDINGTABLES, null, cv);
		}
	}
	
	public List<Map<String, Object>> call(String pageName, String functionName, Map<String, Object> values){
		return call(pageName, functionName, values, false, false);
	}
	
	public List<Map<String, Object>> call(boolean insertRequest, String pageName, String functionName, Map<String, Object> values){
		return call(pageName, functionName, values, false, insertRequest);
	}

	public List<Map<String, Object>> call(String pageName, String functionName, Map<String, Object> values, boolean useOnlyInternet){
		return call(pageName, functionName, values, useOnlyInternet, false);
	}
	
	//allways return List have more than 1 row.
	public List<Map<String, Object>> call(String pageName, String functionName, Map<String, Object> values, boolean useOnlyInternet, boolean insertRequest) {		
		try{
			if(Functions.checkConnection(mApp)){
				httpPost = new HttpPost(domainName + pageName);	
				Log.v("RestService call", "RestService call post pageName : " + domainName + pageName);
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				
				if(values != null){			
					Iterator<?> it = values.entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry pairs = (Map.Entry)it.next();	
						nameValuePair.add(new BasicNameValuePair(pairs.getKey().toString(), pairs.getValue().toString()));
				    }	
				}	
				nameValuePair.add(new BasicNameValuePair("FunctionName", functionName));			
			    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));	    
			    
			    String jSonResult = Functions.getStringFromInputStream(httpClient.execute(httpPost).getEntity().getContent());
			    result = Functions.jSonToList(jSonResult);
			    
			    if(result.size() > 0)
			    	result.get(0).put("IsOfflineData", false);
			    else{
				    map = new HashMap<String, Object>();
				    map.put("IsOfflineData", false);
				    result.add(map);
			    }
			    	
				if(!useOnlyInternet)
					insertOrUpdate(pageName, functionName, values, jSonResult, 1);
			    
				return result;
			}
			else{		
				if(!useOnlyInternet)
					return getOfflineData(pageName, functionName, values);
				else{
				    map = new HashMap<String, Object>();
				    map.put("IsOfflineData", false);
				    map.put("MessageType", 0);
				    map.put("Message", mApp.getString(R.string.CheckInternet));

					result = new ArrayList<Map<String, Object>>();
				    result.add(map);
					
					return result;
				}
			}
		}catch(Exception ex){			
			map = new HashMap<String, Object>();
			map.put("MessageType", 0);
			map.put("Message", ex.getMessage());
		    map.put("IsOfflineData", false);
			
		    result = new ArrayList<Map<String,Object>>();
			result.add(map);
			
			if(insertRequest)
				insertOrUpdate(pageName, functionName, values, "", 0);
			
			return result;
		}
	}

	public String callStringResult(String pageName, String functionName, Map<String, Object> values, boolean useOnlyInternet, boolean insertRequest) {		
		try{
			if(Functions.checkConnection(mApp)){
				httpPost = new HttpPost(domainName + pageName);	
				Log.v("RestService call", "RestService call post pageName : " + domainName + pageName);
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				
				if(values != null){			
					Iterator<?> it = values.entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry pairs = (Map.Entry)it.next();	
						nameValuePair.add(new BasicNameValuePair(pairs.getKey().toString(), pairs.getValue().toString()));
				    }	
				}	
				nameValuePair.add(new BasicNameValuePair("FunctionName", functionName));			
			    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));	    
			    
			    String jSonResult = Functions.getStringFromInputStream(httpClient.execute(httpPost).getEntity().getContent());
			    	
				if(!useOnlyInternet)
					insertOrUpdate(pageName, functionName, values, jSonResult, 1);
			    
				return jSonResult;
			}
			else{		
				if(!useOnlyInternet)
					return getOfflineStringData(pageName, functionName, values);
				else{
					String strResult = "[{\"Message\" : \"" + mApp.getString(R.string.CheckInternet) 
							           + "\", \"MessageType\" : \"0\", \"IsOfflineData\" : \"false\"}]";
					
					return strResult;
				}
			}
		}catch(Exception ex){	
			String strResult = "[{\"Message\" : \"" + ex.getMessage()
			                   + ", \"MessageType\" : \"0\", \"IsOfflineData\" : \"false\"}]";
			
			if(insertRequest)
				insertOrUpdate(pageName, functionName, values, "", 0);
			
			return strResult;
		}
	}
	
	private void insertOrUpdate(String pageName, String functionName, Map<String, Object> values, String jSonResult, int status){
	    Calendar c = Calendar.getInstance();
	    String regDate = c.get(Calendar.DATE) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
		
		String requestName = pageName + "/" + functionName + "/";
		String temp = "";
		
		if(values != null){
			Iterator<?> it = values.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();	        
		        temp += "," + pairs.getKey() + "=" + pairs.getValue();
		    }
		    requestName += temp.substring(1);
		}
	    
		ContentValues cv = new ContentValues();
		cv.put("RequestName", requestName);
		cv.put("Response", jSonResult);
		cv.put("RegDate", regDate);
		cv.put("Status", status);
	    
		String isExist = "Select Response from CACHES where RequestName = '" + requestName + "'";
		Cursor cursor = db.rawQuery(isExist, null);
		
		if(cursor.getCount() > 0)
			db.update("CACHES", cv, "RequestName = '" + requestName + "'", null);
		else
			db.insertOrThrow("CACHES", null, cv);
	}
	
	@SuppressWarnings("rawtypes")
	private List<Map<String, Object>> getOfflineData(String pageName, String functionName, Map<String, Object> values){
		List<Map<String, Object>> result;
		
		String sql = "Select Response, RegDate from CACHES where RequestName = '" + pageName + "/" + functionName + "/";		
		String whereClause = "";
		
		if(values != null){
			Iterator<?> it = values.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();		        
		        whereClause += "," + pairs.getKey() + "=" + pairs.getValue();			
		    }
		    sql += whereClause.substring(1);
		}
		
		Cursor cursor = db.rawQuery(sql + "' and Status = 1", null);
		cursor.moveToFirst();		
		if(cursor.getCount() > 0){
			result = Functions.jSonToList(cursor.getString(cursor.getColumnIndex("Response")));
			if(result.size() > 0)
				result.get(0).put("IsOfflineData", true);
		}
		else{
			map = new HashMap<String, Object>();
			map.put("IsOfflineData", true);

			result = new ArrayList<Map<String, Object>>();
			result.add(map);
		}
		
		return result;
	}

	@SuppressWarnings("rawtypes")
	private String getOfflineStringData(String pageName, String functionName, Map<String, Object> values){
		String strResult;
		
		String sql = "Select Response, RegDate from CACHES where RequestName = '" + pageName + "/" + functionName + "/";		
		String whereClause = "";
		
		if(values != null){
			Iterator<?> it = values.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();		        
		        whereClause += "," + pairs.getKey() + "=" + pairs.getValue();			
		    }
		    sql += whereClause.substring(1);
		}
		
		Cursor cursor = db.rawQuery(sql + "' and Status = 1", null);
		cursor.moveToFirst();		
		if(cursor.getCount() > 0)
			strResult = cursor.getString(cursor.getColumnIndex("Response"));
		else
			strResult = "[\"Message\" : \"\", \"MessageType\" : \"0\", \"IsOfflineData\" : \"false\"]";
		
		return strResult;
	}
	
	public void InsertLog(String logDescription){
		String pageName = mApp.getString(R.string.Log_PageName);
		String functionName = mApp.getString(R.string.Log_FunctionName);
		
		map = new HashMap<String, Object>();
		map.put("LogDescription", logDescription);
		map.put("AndroidId", Functions.getAndroidId(mApp));
		
		Log.v("InsertLog", "InsertLog logDescription : " + logDescription);
		call(true, pageName, functionName, map);
	}

	//CONTENT PROVIDER FUNCTIONS AND ITEMS
	@Override
	public boolean onCreate() {
		mApp = (Application)getContext();		
		db = SqlLiteHelper.getInstance(mApp).getWritableDatabase();
		
		return false;
	};
	
	@Override
	public Cursor query(Uri cpUri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor cs = null;
		String tableName = cpUri.getPathSegments().get(0);

		if(isUseContentProvider(tableName))
			cs = db.query(cpUri.getPathSegments().get(0), projection, selection, selectionArgs, null, null, sortOrder);
		else
			throw new IllegalArgumentException("Invalid Uri : " + cpUri);
		
		return cs;
	}	
	
	@Override
	public Uri insert(Uri cpUri, ContentValues values) {
		String tableName = cpUri.getPathSegments().get(0);
		long id = 0;

		if(isUseContentProvider(tableName))
			id = db.insert(tableName, null, values);
		else
			throw new IllegalArgumentException("Invalid Uri : " + cpUri);
		
		return Uri.parse(cpUri.toString() + "/Id/" + id);
	}

	@Override
	public int update(Uri cpUri, ContentValues values, String whereClause, String[] whereClauseArgs) {
		String tableName = cpUri.getPathSegments().get(0);
		int rowCount = 0;

		if(isUseContentProvider(tableName))
			rowCount = db.update(tableName, values, whereClause, whereClauseArgs);
		else
			throw new IllegalArgumentException("Invalid Uri : " + cpUri);
		
		return rowCount;
	}
	
	@Override
	public int delete(Uri cpUri, String whereClause, String[] whereClauseArgs) {
		String tableName = cpUri.getPathSegments().get(0);
		int rowCount = 0;

		if(isUseContentProvider(tableName))
			rowCount = db.delete(tableName, whereClause, whereClauseArgs);
		else
			throw new IllegalArgumentException("Invalid Uri : " + cpUri);
		
		return rowCount;
	}
	
	@Override
	public String getType(Uri cpUri) {
		String tableName = cpUri.getPathSegments().get(0);
		String type = "";

		if(isUseContentProvider(tableName))
			type = "vdn.hayrihabip.cursor.dir/" + cpUri.getPathSegments().get(0);
		else
			throw new IllegalArgumentException("Invalid Uri : " + cpUri);
		
		return type;
	}
	
	private boolean isUseContentProvider(String tableName){
		boolean result = false;
		
		if(!tableName.equals(PROVIDINGTABLES)){
			String sql = "Select UseContentProvider from %s where TableName = ?";
			Cursor cs = db.rawQuery(String.format(sql, PROVIDINGTABLES), new String[]{ tableName });
			if(cs.getCount() > 0)
				result = true;
		}
		else
			result = false;
		
		return result;
	}
}