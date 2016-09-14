package com.hayrihabip.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.hayrihabip.R;
import com.hayrihabip.adapters.DropdownAdapter;
import com.hayrihabip.items.Functions;
import com.hayrihabip.items.ValidationResult;

public class Dropdownlist extends TableRow {
	private Activity mActivity;
	private AttributeSet attrs;
	
	public Spinner ddl;
	public TextView tVLabel;
	public ImageView imgIcon;
	
	private LayoutParams params;
	
	public boolean isRequired;
	public String label;
	public String validationGroup;
	public String textRv;
	public int selectedItemIndex = 0;
	public String selectedItemValue = null;
	public String selectedItemText = null;
	//public String typeName;
	//public String selectMethod;
	public String renderType;
	
	public int labeltemplateId;
	public int ddlTemplateId;
	public int iconTemplateId;
	public int itemTemplateId;

	public List<String> dataSourceFields = new ArrayList<String>();
	public List<String> dataSourceValues = new ArrayList<String>();
	public int itemCount;
	private int starter = 1;
	
	private boolean isPrepared = false;
	private String[] renderTypes = { "FullRow", "NoLableNoIcon", "NoIcon", "NoLabel" };
	
	public Dropdownlist(Context context) {
		super(context);
		
		mActivity = (Activity)context;
		Init();
	}
	
	public Dropdownlist(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.attrs = attrs;
		mActivity = (Activity)context;
		Init();
	}
	
	@Override
    protected void onMeasure(int width, int height) {
		super.onMeasure(width, height);		
		
		if(!isPrepared){
			for (int i = starter; i < getChildCount(); i++) {
				ListItem listItem = (ListItem)getChildAt(i);
				dataSourceFields.add(listItem.key);
				dataSourceValues.add(listItem.value);
				
				if(listItem.selected){
					selectedItemIndex = i;
					selectedItemText = listItem.key;
					selectedItemValue = listItem.value;
				}
			}
			
			dataSourceFields.add(0, label != null ? label : getContext().getResources().getString(R.string.titleDddl));
			dataSourceValues.add(0, null);
			itemCount = dataSourceFields.size();
	
	        prepareDdl();
			ddl.setSelection(selectedItemIndex);
			
			if(selectedItemText == null){
				if(label != null)
					selectedItemText = label;
				else
					selectedItemText = getContext().getResources().getString(R.string.titleDddl);
	
				selectedItemIndex = 0;
				selectedItemValue = null;
				ddl.setSelection(0);
			}
			ddl.setLayoutParams(this.getLayoutParams());
			
			isPrepared = true;
		}
	}
	
	private void Init(){
		TypedArray tArray = mActivity.obtainStyledAttributes(attrs, R.styleable.ControlAttrs);

		isRequired = tArray.getBoolean(R.styleable.ControlAttrs_isRequired, false);
		label = tArray.getString(R.styleable.ControlAttrs_label);
		
		validationGroup = tArray.getString(R.styleable.ControlAttrs_validationGroup);
		validationGroup = Functions.IsNullOrEmpty(validationGroup) ? "" : validationGroup;
		
		textRv = tArray.getString(R.styleable.ControlAttrs_textRv);
		textRv = !Functions.IsNullOrEmpty(textRv) ? textRv : getContext().getString(R.string.textRv);

		renderType = renderTypes[tArray.getInt(R.styleable.ControlAttrs_renderType, 0)];

		labeltemplateId = tArray.getInt(R.styleable.ControlAttrs_labeltemplateId, R.layout.labeltemplate);
		ddlTemplateId = tArray.getInt(R.styleable.ControlAttrs_ddlTemplateId, R.layout.ddltemplate);
		iconTemplateId = tArray.getInt(R.styleable.ControlAttrs_iconTemplateId, R.layout.icontemplate);
		itemTemplateId = tArray.getInt(R.styleable.ControlAttrs_layoutResId, R.layout.ddlitemtemplate);
		
		/*selectedItemIndex = tArray.getInt(R.styleable.ControlAttrs_selectedItemIndex, 0);
		selectedItemValue = tArray.getString(R.styleable.ControlAttrs_selectedItemValue);
		fieldName = tArray.getString(R.styleable.ControlAttrs_fieldName);
		fieldValue = tArray.getString(R.styleable.ControlAttrs_fielValue);
		typeName = tArray.getString(R.styleable.ControlAttrs_fieldName);
		selectMethod = tArray.getString(R.styleable.ControlAttrs_fielValue);
		
		if(typeName != null && selectMethod != null && fieldValue != null && fieldName != null){
			Class mainClass = Class.forName(typeName);
			Method sourceMethod = mainClass.getMethod(name, parameterTypes);
					
			setItems(fieldValue, fieldName);
		}*/
		
		tArray.recycle();

		//TextBoxFocusChangeListener TextBoxFocusChangeListener = new TextBoxFocusChangeListener();
		//TextBoxClickListener TextBoxClickListener = new TextBoxClickListener();
		
		if(!renderType.contains("NoLabel")){
			tVLabel = (TextView)mActivity.getLayoutInflater().inflate(labeltemplateId, null);
			tVLabel.setLayoutParams(params);
			tVLabel.setText(label);
			tVLabel.setId(getId() + 1000);
			tVLabel.setNextFocusRightId(getId());

			//tVLabel.setOnFocusChangeListener(TextBoxFocusChangeListener);
			//tVLabel.setOnClickListener(TextBoxClickListener);			
			
			addView(tVLabel);
		}
		
		ddl = (Spinner)mActivity.getLayoutInflater().inflate(ddlTemplateId, null);
		ddl.setLayoutParams(params);
		//ddl.setOnFocusChangeListener(TextBoxFocusChangeListener);
		//ddl.setOnClickListener(TextBoxClickListener);
		
		ddl.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
				selectedItemIndex = position;
				selectedItemValue = dataSourceValues.get(position);
				selectedItemText = dataSourceFields.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		addView(ddl);
		
		if(!renderType.contains("NoIcon")){
			imgIcon = (ImageView)mActivity.getLayoutInflater().inflate(iconTemplateId, null);
			imgIcon.setLayoutParams(params);
			imgIcon.setId(getId() + 1001);
			imgIcon.setVisibility(View.GONE);
			addView(imgIcon);
			
			//imgIcon.setOnFocusChangeListener(TextBoxFocusChangeListener);
			//imgIcon.setOnClickListener(TextBoxClickListener);
		}

	}

	private void prepareDdl(){
		DropdownAdapter dAdapter = new DropdownAdapter(mActivity, itemTemplateId, dataSourceFields);
		dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
        ddl.setAdapter(dAdapter);
	}
	
	public void Fill(List<Map<String, Object>> dataSource, String fieldName, String valueName, String title){
		this.dataSourceFields = Functions.getStringList(dataSource, fieldName);
		this.dataSourceValues = Functions.getStringList(dataSource, valueName);
		
		if(title == null){
			if(label != null)
				title = label;
			else
				title = getContext().getResources().getString(R.string.titleDddl);
		}
		
		dataSourceFields.add(0, title);
		dataSourceValues.add(0, null);			
		
		selectedItemIndex = 0;
		selectedItemText = title;
		selectedItemValue = null;
		ddl.setSelection(0);
		
		itemCount = dataSourceFields.size();
	}
	
	public ValidationResult Validate(String _validationGroup){
		ValidationResult validationResult = new ValidationResult(true, null);
		
		if (validationGroup.equals(_validationGroup) ) {
			if(isRequired && Functions.IsNullOrEmpty(selectedItemValue)){
				validationResult.isValid = false;
				validationResult.message = this.textRv;
			}
			
			//if(!validationResult.isValid)
				//this.setError(validationResult.message);
		}		
		return validationResult;
	}
	
	public void setSelectedItemValue(String value){
		if(dataSourceValues.contains(value)){
			selectedItemIndex = dataSourceValues.indexOf(value);
			selectedItemValue = value;
			selectedItemText = dataSourceFields.get(selectedItemIndex);
			ddl.setSelection(selectedItemIndex);
		}
		else{
			selectedItemValue = dataSourceValues.get(0);
			selectedItemText = dataSourceFields.get(0);
			ddl.setSelection(0);
		}
	}

	public void setSelectedItemText(String text){
		if(dataSourceFields.contains(text)){
			selectedItemIndex = dataSourceFields.indexOf(text);
			selectedItemValue = dataSourceValues.get(selectedItemIndex);
			selectedItemText = dataSourceFields.get(selectedItemIndex);
			ddl.setSelection(selectedItemIndex);
		}
		else{
			selectedItemValue = dataSourceValues.get(0);
			selectedItemText = dataSourceFields.get(0);
			ddl.setSelection(0);
		}
	}
	
	public void setSelectedItemIndex(int index){
		if(dataSourceFields.size() > index ){
			selectedItemIndex = index;
			selectedItemValue = dataSourceValues.get(index);
			selectedItemText = dataSourceFields.get(index);
			ddl.setSelection(selectedItemIndex);
		}
		else{
			selectedItemValue = dataSourceValues.get(0);
			selectedItemText = dataSourceFields.get(0);
			ddl.setSelection(0);
		}
	}

	public String getSelectedItemValue(){
		return selectedItemValue;
	}

	public String getSelectedItemText(){
		return selectedItemText;
	}

	public int getSelectedItemIndex(){
		return selectedItemIndex;
	}
}