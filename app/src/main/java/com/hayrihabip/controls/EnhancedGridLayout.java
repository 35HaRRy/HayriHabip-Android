package com.hayrihabip.controls;

import java.sql.ResultSet;
import java.sql.SQLException;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class EnhancedGridLayout extends TableLayout {
	public Context mContext;	
	public ResultSet mResultSet;
	//public boolean AutoGenerateColumn = true;
	
	public EnhancedGridLayout(Context context){
	    super(context);        
		mContext = context;
	}
	public EnhancedGridLayout(Context context, AttributeSet attrs){
		super(context, attrs);        
		mContext = context;
	}
	
	//List<Map<String, Object>> dataSource ile bind olan override ını yaz !!
	public void BindGrid(ResultSet _resultSet) throws SQLException{
		mResultSet = _resultSet;
		SatirlariEkle();
	}

	private void SatirlariEkle() throws SQLException {
		int satirSayisi = 0, tvId = 0;
		int rowColor = Color.BLACK;

		while (mResultSet.next()) {
			TableRow row = new TableRow(mContext);

			if (rowColor == Color.BLACK) {
				rowColor = Color.DKGRAY;
				row.setBackgroundColor(Color.DKGRAY);
			} else {
				rowColor = Color.BLACK;
				row.setBackgroundColor(Color.BLACK);
			}

			for (int i = 1; i < mResultSet.getMetaData().getColumnCount() + 1; i++) {
				TextView tv = new TextView(mContext);
				
				String s = "";
				if (mResultSet.getString(i) != null)
					s = mResultSet.getString(i).toString() + "";
				else
					s = "Null";
				
				if (s.length() > 7)
					s = s.substring(0, 7) + ".";
				
				tv.setText(s + "   ");
				tv.setId(tvId);
				row.addView(tv);
				tvId++;
			}
			
			satirSayisi++;
			this.addView(row);
		}
		
		//Toast.makeText(mContext, satirSayisi + " tane satır listelendi", Toast.LENGTH_LONG).show();
	}
}
