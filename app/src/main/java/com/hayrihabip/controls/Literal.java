package com.hayrihabip.controls;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.hayrihabip.R;

public class Literal extends TableRow {
	private Activity mActivity;
	
	public TextView tVLabel;
	public TextView tVText;
	public ImageView imgIcon;
	
	public String label;
	public String renderType;
	
	public int labelTemplateId;
	public int literalTemplateId;
	public int iconTemplateId;
	
	private LayoutParams params;
    
    private String[] renderTypes = { "FullRow", "NoLableNoIcon", "NoIcon", "NoLabel" };
	
	public Literal(Context context) {
		super(context);
		Init(context, null);
	}
	
	public Literal(Context context, AttributeSet attrs) {
		super(context, attrs);		
		Init(context, attrs);
	}
	
	public void Init(Context context, AttributeSet attrs){
		mActivity = (Activity)context;
		
		params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		TypedArray tArray = mActivity.obtainStyledAttributes(attrs, R.styleable.ControlAttrs);

		label = mActivity.getString(tArray.getInt(R.styleable.ControlAttrs_label, R.string.EmptyString));
		renderType = renderTypes[tArray.getInt(R.styleable.ControlAttrs_renderType, 2)];
		
		labelTemplateId = tArray.getInt(R.styleable.ControlAttrs_labeltemplateId, R.layout.labeltemplate);
		literalTemplateId = tArray.getInt(R.styleable.ControlAttrs_textTemplateId, R.layout.literaltemplate);
		iconTemplateId = tArray.getInt(R.styleable.ControlAttrs_iconTemplateId, R.layout.icontemplate);

		tVText = (TextView)mActivity.getLayoutInflater().inflate(literalTemplateId, null);
		tVText.setText(mActivity.getString(tArray.getInt(R.styleable.ControlAttrs_text, R.string.EmptyString)));	
		
		tArray.recycle();

		LiteralFocusChangeListener LiteralFocusChangeListener = new LiteralFocusChangeListener();
		LiteralClickListener LiteralClickListener = new LiteralClickListener();

		if(!renderType.contains("NoLabel")){
			tVLabel = (TextView)mActivity.getLayoutInflater().inflate(labelTemplateId, null);
			tVLabel.setLayoutParams(params);
			tVLabel.setId(getId() + 1000);
			tVLabel.setText(label);

			tVLabel.setOnFocusChangeListener(LiteralFocusChangeListener);
			tVLabel.setOnClickListener(LiteralClickListener);
			
			addView(tVLabel);
		}
		
		tVText.setLayoutParams(params);
		tVText.setOnFocusChangeListener(LiteralFocusChangeListener);
		tVText.setOnClickListener(LiteralClickListener);
		
		addView(tVText);

		if(!renderType.contains("NoIcon")){
			imgIcon = (ImageView)mActivity.getLayoutInflater().inflate(iconTemplateId, null);
			imgIcon.setLayoutParams(params);
			imgIcon.setId(getId() + 1001);

			imgIcon.setOnFocusChangeListener(LiteralFocusChangeListener);
			imgIcon.setOnClickListener(LiteralClickListener);
			
			addView(imgIcon);
		}
		
		setFocusable(false);
		setFocusableInTouchMode(false);
	}
	
	private class LiteralFocusChangeListener implements OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus)  {
            if (hasFocus == true)
            	stateActive();
        }
    }
	
	private class LiteralClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {   
			stateActive();   
		}
    }
	
	private void stateActive(){
    	if(!renderType.contains("NoLabel"))
    		tVLabel.requestFocus();
    	
		if(!renderType.contains("NoIcon"))
			imgIcon.requestFocus();		
	}
}