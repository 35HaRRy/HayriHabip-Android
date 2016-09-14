package com.hayrihabip.items;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.widget.DatePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Functions {

	public static boolean IsNullOrEmpty(String text){		
		return text == null || text.length() == 0;
	}

	public static boolean IsNullOrEmpty(CharSequence text) {
		if( text != null)
			return IsNullOrEmpty(text.toString());
		else
			return false;
	}
	
	public static Date getDateFromDatePicker(DatePicker datePicker){
	    int day = datePicker.getDayOfMonth();
	    int month = datePicker.getMonth();
	    int year =  datePicker.getYear();

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(year, month, day);

	    return calendar.getTime();
	}
	
	public static List<String> getStringList(Cursor cursor, String columnName){
		List<String> listItems = new ArrayList<String>();
		
		int columnIndex = cursor.getColumnIndex(columnName);
		if(cursor.moveToFirst()){
			do {
				listItems.add(cursor.getString(columnIndex));
			} while (cursor.moveToNext());
		}
		
		return listItems;
	}
	
	public static List<String> getStringList(List<Map<String, Object>> dataSource, String columnName){
		List<String> listItems = new ArrayList<String>();
		
		for (int i = 0; i < dataSource.size(); i++) 
			listItems.add(dataSource.get(i).get(columnName).toString());
		
		return listItems;
	}
	
	public static List<String> getStringList(ResultSet resultset, String columnName) throws SQLException{
		List<String> listItems = new ArrayList<String>();
		
		while(resultset.next())
			listItems.add(resultset.getString(columnName));
		
		return listItems;
	}
	
	// convert InputStream to String
	public static String getStringFromInputStream(InputStream is) throws IOException { 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
 
		br = new BufferedReader(new InputStreamReader(is));
		while ((line = br.readLine()) != null)
			sb.append(line);

		if (br != null) 
			br.close();
 
		return sb.toString(); 
	}
	
	public static List<Map<String, Object>> jSonToList(String strJSon){
		TypeToken<List<Map<String, Object>>> token = new TypeToken<List<Map<String, Object>>>(){};
		return new Gson().fromJson(strJSon, token.getType());		
	}

	public static boolean checkConnection(Application mApp){
		ConnectivityManager cMngr = (ConnectivityManager)mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cMngr.getActiveNetworkInfo();
		
		if (nInfo == null)
			return false;
		else if (!nInfo.isConnected())
			return false;
		else if (!nInfo.isAvailable())
			return false;
		else
			return true;		
	}
	
	public static int getResId(String variableName, Context context, Class<?> c) {
	    try {
	        Field idField = c.getDeclaredField(variableName);
	        return idField.getInt(idField);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    } 
	}
	
	public static List<Map<String, Object>> getListByEquality(List<Map<String, Object>> source, String key, Object equality){
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		for (Map<String, Object> map : source) {
			if(map.get(key).equals(equality))
				result.add(map);
		}
		
		return result;	
	}
	
	public static boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) throws IOException {
        String[] files = assetManager.list(fromAssetPath);
        new File(toPath).mkdirs();
        boolean res = true;
        
        for (String file : files)
            if (file.contains("."))
                res &= copyAsset(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
            else 
                res &= copyAssetFolder(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
        
        return res;
    }

	public static boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) throws IOException {
        InputStream inStream = assetManager.open(fromAssetPath);
        new File(toPath).createNewFile();
        OutputStream outStream = new FileOutputStream(toPath);
        
        copyFile(inStream, outStream);        
        inStream.close();
        inStream = null;
        
        outStream.flush();
        outStream.close();
        outStream = null;
  
        return true;
    }

	public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1)
          out.write(buffer, 0, read);
    }
	
	public static String getAndroidId(Application mApp){
		return Secure.getString(mApp.getContentResolver(), Secure.ANDROID_ID);
	}
	
	public static List<Map<String, Object>> getListFromCursor(Cursor cs){
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		Map<String, Object> map;
		
		if (cs.moveToFirst()) {
			do {
				map = new HashMap<String, Object>();
				for (int i = 0; i < cs.getColumnCount(); i++)
					map.put(cs.getColumnName(i), cs.getString(i));
				
				result.add(map);
			} while (cs.moveToNext());
		}
		
		return result;
	}
	
	public static Bitmap getBitmapOverlay(Bitmap bmp1, Bitmap bmp2, float left, float top) {
	    Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(),  bmp1.getConfig());
	    Canvas canvas = new Canvas(bmOverlay);    
	    
	    canvas.drawBitmap(bmp1, 0, 0, null);
	    canvas.drawBitmap(bmp2, left, top, null);
	    
	    return bmOverlay;
	}

	public static void downloadFile(String from, String to) throws IOException{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy); 
		} 
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost  = new HttpPost(from);
		InputStream input = httpClient.execute(httpPost).getEntity().getContent();
		
	    /*HttpURLConnection conn = (HttpURLConnection)new URL(from.replace(" ", "%20")).openConnection();
	    conn.setConnectTimeout(10000); // timeout 10 secs
	    conn.setDoOutput(false);
	    conn.setDoInput(true);
	    conn.connect();
	    
	    InputStream input = conn.getInputStream();*/
	    FileOutputStream fOut = new FileOutputStream(to);
	    
	    byte[] buffer = new byte[4096];
	    int bytesRead = -1;	    
	    while ((bytesRead = input.read(buffer)) != -1)
	        fOut.write(buffer, 0, bytesRead);
	    
	    fOut.flush();
	    fOut.close();
	    fOut = null;
	    
	    //conn.disconnect();
	}
	
	public static boolean isPackageExisted(PackageManager pm, String targetPackage){
	   try {
		   pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
	   } catch (NameNotFoundException e) {
		   return false;
	   }
	   
	   return true;
   }
	
	public static boolean isTableExsists(SQLiteDatabase db, String tableName){
		String sql = "select * from " + tableName;
		try {
			db.execSQL(sql);			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
