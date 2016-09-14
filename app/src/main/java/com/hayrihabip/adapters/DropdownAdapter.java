package com.hayrihabip.adapters;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hayrihabip.R;

@SuppressLint("NewApi")
public class DropdownAdapter extends ArrayAdapter<String> {
	public Context context;
	public ViewGroup.LayoutParams params;
	public int layout_resId;
	
	public int count = 0;
    public List<String> sources = new ArrayList<String>(); 
    
	public DropdownAdapter(Context context, int layout_resId, List<String> sources){
		super(context, layout_resId, sources);
		
		this.context = context;
    	this.sources = sources;
    	this.layout_resId = layout_resId;
    	
    	count = sources.size();
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
        return getAdapterView(position, convertView, parent);  
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent){  
        return getAdapterView(position, convertView, parent);   
	}
	
	public View getAdapterView(int position, View convertView, ViewGroup parent){
        LinearLayout item = (LinearLayout)LayoutInflater.from(context).inflate(layout_resId, null);
        item.setId(position);
	    
	    String text = sources.get(position);  
	    if(text != null){
	    	TextView itemText = (TextView)item.findViewById(R.id.itemText);
	    	itemText.setText(text);
	    }
	    
		return item;
	}
}