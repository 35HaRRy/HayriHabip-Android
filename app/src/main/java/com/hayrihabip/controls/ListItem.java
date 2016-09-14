package com.hayrihabip.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.hayrihabip.R;

public class ListItem extends View {
	public boolean selected;
	public String key;
	public String value;
	
	Context context;
	
	public ListItem(Context context) {
		super(context);
		Init(context, null, 0);
	}
	public ListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		Init(context, attrs, 0);
	}
	public ListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);		
		Init(context, attrs, defStyle);
	}
	
	private void Init(Context context, AttributeSet attrs, int defStyle){
		this.context = context;
		
		TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.ControlAttrs, defStyle, 0);

		selected = tArray.getBoolean(R.styleable.ControlAttrs_selected, false);
		key = tArray.getString(R.styleable.ControlAttrs_key);
		value = tArray.getString(R.styleable.ControlAttrs_value);
		
		tArray.recycle();
	}	
}
