package com.hayrihabip.items;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.hayrihabip.R;

public class  BaseView extends TableRow {	
	public Activity mActivity;
	
	public BaseView(Context context) {
		super(context);
		Init(context, null);
	}
	
	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		Init(context, null);
	}
	
	public void Init(Context context, AttributeSet attrs){
		mActivity = (Activity)context;
		
		TypedArray tArray = mActivity.obtainStyledAttributes(attrs, R.styleable.ControlAttrs);		
		
		isRequired = tArray.getBoolean(R.styleable.ControlAttrs_isRequired, false);
		label = mActivity.getString(tArray.getInt(R.styleable.ControlAttrs_label, R.string.EmptyString));
		
		validationGroup = tArray.getString(R.styleable.ControlAttrs_validationGroup);
		validationGroup = Functions.IsNullOrEmpty(validationGroup) ? "" : validationGroup;
		
		renderType = renderTypes[tArray.getInt(R.styleable.ControlAttrs_renderType, 0)];
		
		labeltemplateId = tArray.getInt(R.styleable.ControlAttrs_labeltemplateId, R.layout.labeltemplate);
		iconTemplateId = tArray.getInt(R.styleable.ControlAttrs_iconTemplateId, R.layout.icontemplate);

		//For TextBox
		validateType = validateTypes[tArray.getInt(R.styleable.ControlAttrs_validateType, 0)];
		textTemplateId = tArray.getInt(R.styleable.ControlAttrs_textTemplateId, R.layout.texttemplate);
		
		txt = (EditText)mActivity.getLayoutInflater().inflate(textTemplateId, null);
		txt.setText(mActivity.getString(tArray.getInt(R.styleable.ControlAttrs_text, R.string.EmptyString)));	
		
		textRv = tArray.getString(R.styleable.ControlAttrs_textRv);
		textRv = !Functions.IsNullOrEmpty(textRv) ? textRv : getContext().getString(R.string.textRv);
		
		textRegex = tArray.getString(R.styleable.ControlAttrs_textRegex);
		textRegex = !Functions.IsNullOrEmpty(textRegex) ? textRegex : getContext().getString(R.string.textRegex);
		//End TextBox

		tArray.recycle();
	}
	
	//Subcontrols
	public EditText txt = null;
	public TextView tVLabel = null;
	public ImageView imgIcon = null;
	
	//Subcontrol ids
	public int labeltemplateId = 0;
	public int textTemplateId = 0;
	public int iconTemplateId = 0;

	//Params
	public boolean isRequired = false;
	public String label = "";
	public String validationGroup = "";
	public String validateType = "";
	public String textRv = "";
	public String textRegex = "";
	public String renderType = "";
	
	//Constans
	
	//For textbox
	//public static final static String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public final static String PHONE_REGEX = "^[(]\\d{3}[)] \\d{3} \\d{2} \\d{2}";
	public final static String DATE_REGEX = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
	public final static String DATETIME_REGEX = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d) ([01]\\d|2[0123])[:]([0-5]\\d)";
    
	public static String[] renderTypes = { "FullRow", "NoLableNoIcon", "NoIcon", "NoLabel" };
	//For textbox
	public static String[] validateTypes = { "Text", "Integer","Decimel", "Email", "Phone", "Date", "DateTime" };

	//Constans End
}