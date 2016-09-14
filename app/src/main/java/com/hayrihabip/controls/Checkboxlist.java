package com.hayrihabip.controls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.hayrihabip.R;
import com.hayrihabip.items.Functions;
import com.hayrihabip.items.PixDialog;
import com.hayrihabip.items.ValidationResult;

public class Checkboxlist extends TableRow {
	private Activity mActivity;
	private PixDialog dialog;
	private LayoutInflater inflater;
	
	public TextView selectedItems;
	public TextView tVLabel;
	public ImageView imgOpenIcon;
	public ImageView imgCloseIcon;
	
	public ListView lvItems;
	private LayoutParams params = null;
	private Animation animSlideDown;
	private Animation animSlideUp;
	
	public boolean isRequired;
	public String label;
	public String validationGroup;
	public String textRv;
	public String fieldName;
	public String fieldValue;
	//public String typeName;
	//public String selectMethod;
	public String renderType;
	public String usageType;
	
	private int labeltemplateId;
	private int textTemplateId;
	private int openIconTemplateId;
	private int closeIconTemplateId;
	private int containerTemplateId;
	private int cbItemTemplateId;
	private int TemplateId;
	
	public String selectedItemValues = null;
	public String selectedItemTexts = null;
	public List<String> selectedItemIndexes = new ArrayList<String>();

	public ResultSet dataSource;
	public List<String> dataSourceFields = new ArrayList<String>();
	public List<String> dataSourceValues = new ArrayList<String>();
	public int itemCount;
	
	private boolean isOpen = false;
	private boolean isPrepared = false;
	private int starter = 1;
	
	private String[] renderTypes = { "FullRow", "NoLableNoIcon", "NoIcon", "NoLabel" };
	private String[] usageTypes = { "ExpandAbleList", "PopUp" };
	
	public Checkboxlist(Context context) {
		super(context);
		Init(context, null);
	}
	
	public Checkboxlist(Context context, AttributeSet attrs) {
		super(context, attrs);
		Init(context, attrs);
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
					if( selectedItemIndexes == null )
						selectedItemIndexes.add("" + ( i + 1 ));
				}
			}
			
			dataSourceFields.add(0, label != null ? label : getContext().getResources().getString(R.string.titleDddl));
			dataSourceValues.add(0, null);
			itemCount = dataSourceFields.size();
	
			setSelections();
			
			if(selectedItemTexts == null){	
				selectedItemIndexes.add("0");
				setSelections();
			}
			
			isPrepared = true;
		}
	}
	
	private void Init(Context context, AttributeSet attrs){
		mActivity = (Activity)context;
		inflater = mActivity.getLayoutInflater();
		
		TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.ControlAttrs);

		/*isRequired = tArray.getBoolean(R.styleable.ControlAttrs_isRequired, false);
		label = tArray.getString(R.styleable.ControlAttrs_label);
		
		validationGroup = tArray.getString(R.styleable.ControlAttrs_validationGroup);
		validationGroup = Functions.IsNullOrEmpty(validationGroup) ? "" : validationGroup;
		
		renderType = renderTypes[tArray.getInt(R.styleable.ControlAttrs_renderType, 0)];
		usageType = usageTypes[tArray.getInt(R.styleable.ControlAttrs_usageType, 0)];
		
		labeltemplateId = tArray.getInt(R.styleable.ControlAttrs_labeltemplateId, R.layout.labeltemplate);
		textTemplateId = tArray.getInt(R.styleable.ControlAttrs_textTemplateId, R.layout.texttemplate);
		openIconTemplateId = tArray.getInt(R.styleable.ControlAttrs_openIconTemplate, R.layout.openicontemplate);
		closeIconTemplateId = tArray.getInt(R.styleable.ControlAttrs_closeIconTemplate, R.layout.closeicontemplate);
		containerTemplateId = tArray.getInt(R.styleable.ControlAttrs_containerTemplateId, R.layout.containertemplate);
		cbItemTemplateId = tArray.getInt(R.styleable.ControlAttrs_cbItemTemplateId, R.layout.cbitemtemplate);*/
		
		//selectedItemIndex = tArray.getInt(R.styleable.ControlAttrs_selectedItemIndex, 0);
		//selectedItemValue = tArray.getString(R.styleable.ControlAttrs_selectedItemValue);
		//fieldName = tArray.getString(R.styleable.ControlAttrs_fieldName);
		//fieldValue = tArray.getString(R.styleable.ControlAttrs_fielValue);
		//typeName = tArray.getString(R.styleable.ControlAttrs_fieldName);
		//selectMethod = tArray.getString(R.styleable.ControlAttrs_fielValue);
		
		tArray.recycle();
		
		//if(typeName != null && selectMethod != null && fieldValue != null && fieldName != null){
			//Class mainClass = Class.forName(typeName);
			//Method sourceMethod = mainClass.getMethod(name, parameterTypes);
					
			//setItems(fieldValue, fieldName);
		//}

		params = new LayoutParams(context, attrs);
		
		//TextBoxFocusChangeListener TextBoxFocusChangeListener = new TextBoxFocusChangeListener();
		//TextBoxClickListener TextBoxClickListener = new TextBoxClickListener();
		
		if(!renderType.contains("NoLabel")){
			tVLabel = (TextView)inflater.inflate(labeltemplateId, null);
			tVLabel.setLayoutParams(params);
			tVLabel.setText(label);
			tVLabel.setId(getId() + 1000);
			tVLabel.setNextFocusRightId(getId());

			//tVLabel.setOnFocusChangeListener(TextBoxFocusChangeListener);
			//tVLabel.setOnClickListener(TextBoxClickListener);			
			
			addView(tVLabel);
		}
		
		selectedItems = (TextView)inflater.inflate(textTemplateId, null);
		selectedItems.setLayoutParams(params);
		//txt.setOnFocusChangeListener(TextBoxFocusChangeListener);
		//txt.setOnClickListener(TextBoxClickListener);
		addView(selectedItems);
		
		if(!renderType.contains("NoIcon")){
			imgOpenIcon = (ImageView)inflater.inflate(openIconTemplateId, null);
			imgOpenIcon.setLayoutParams(params);
			imgOpenIcon.setId(getId() + 1001);
			addView(imgOpenIcon);

			imgOpenIcon = (ImageView)inflater.inflate(closeIconTemplateId, null);
			imgOpenIcon.setLayoutParams(params);
			imgOpenIcon.setId(getId() + 1002);
			imgOpenIcon.setVisibility(View.GONE);
			addView(imgCloseIcon);
		}

		if(usageType == "ExpandAbleList"){
			animSlideDown = AnimationUtils.loadAnimation(mActivity, R.anim.slide_down);
			animSlideUp = AnimationUtils.loadAnimation(mActivity, R.anim.slide_up);
		}
		
		setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(usageType == "ExpandAbleList"){
					//add lvItems
					
					if(isOpen){
						lvItems.startAnimation(animSlideUp);
						lvItems.setVisibility(View.GONE);

						if(!renderType.contains("NoIcon")){
							imgCloseIcon.setVisibility(View.GONE);
							imgOpenIcon.setVisibility(View.VISIBLE);
						}
					}	
					else{
						lvItems.setVisibility(View.VISIBLE);
						lvItems.startAnimation(animSlideDown);

						if(!renderType.contains("NoIcon")){
							imgOpenIcon.setVisibility(View.GONE);
							imgCloseIcon.setVisibility(View.VISIBLE);
						}
					}					
				}
				else if(usageType == "PopUp")
					setDialog();
			}
		});
	}

	@SuppressLint("NewApi")
	private void setDialog(){
		int param = WindowManager.LayoutParams.FILL_PARENT;
		dialog = new PixDialog(mActivity, R.style.AlertDialogCustom, param, param);
		
		LinearLayout container = (LinearLayout)inflater.inflate(containerTemplateId, null);
		/*((ViewGroup)container.findViewById(R.id.itemContainer)).addView(lvItems);
		((View)container.findViewById(R.id.closeDialog)).setOnClickListener(new OnClickListener(){			
			@Override
			public void onClick(View v) {
				setSelections();				
				dialog.cancel();
			}
		});*/
		
		dialog.setContentView(container);
		dialog.show();
	}
	
 	private void setSelections(){
		for (int i = 0; i < selectedItemIndexes.size(); i++) {
			if(selectedItemIndexes.get(i) != "0"){
				if(selectedItemValues == null)
					selectedItemValues = selectedItemIndexes.get(i);
				else
					selectedItemValues += "," + selectedItemIndexes.get(i);

				if(selectedItemTexts == null)
					selectedItemTexts = dataSourceFields.get(Integer.parseInt(selectedItemIndexes.get(i)));
				else
					selectedItemTexts += " " + dataSourceFields.get(Integer.parseInt(selectedItemIndexes.get(i)));
				
				int CblMaxTextLength = Integer.parseInt(mActivity.getString(R.string.CblMaxTextLength));
				if(CblMaxTextLength < selectedItemTexts.length())
					selectedItemTexts = selectedItemTexts.substring(0, CblMaxTextLength) + " ...";
			}
			else{
				selectedItemValues = null;
				selectedItemTexts = dataSourceFields.get(0);
			}
		}
		
		selectedItems.setText(selectedItemTexts);

		lvItems.setAdapter(new CheckboxAdapter());
	}
	
 	public void Fill(ResultSet dataSource, String fieldName, String fieldValue, String title){
		this.dataSource = dataSource;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		
		try {
			dataSourceFields = Functions.getStringList(dataSource, fieldName);
			dataSourceValues = Functions.getStringList(dataSource, fieldValue);
			
			if(title == null){
				if(label != null)
					title = label;
				else
					title = getContext().getResources().getString(R.string.titleDddl);
			}
			
			dataSourceFields.add(0, title);
			dataSourceValues.add(0, null);
			
			selectedItemIndexes.add("0");
			setSelections();
			
			itemCount = dataSourceFields.size();
		} catch (SQLException e) {
			e.printStackTrace();
			
			dataSourceFields = dataSourceValues = null;
			Toast.makeText(mActivity, label + mActivity.getResources().getText(R.string.errorDdl), Toast.LENGTH_LONG).show();
		}
	}
	
	public ValidationResult Validate(String _validationGroup){
		ValidationResult validationResult = new ValidationResult(true, null);
		
		if (validationGroup.equals(_validationGroup) ) {
			if(isRequired && Functions.IsNullOrEmpty(selectedItemValues)){
				validationResult.isValid = false;
				validationResult.message = this.textRv;
			}
			
			//if(!validationResult.isValid)
				//this.setError(validationResult.message);
		}		
		return validationResult;
	}
	
	public void setSelectedItemValues(String values){
		selectedItemIndexes = null;
		
		for (String value : values.split(","))
			if(dataSourceValues.contains(value))
				selectedItemIndexes.add("" + dataSourceValues.indexOf(value));
		
		setSelections();
	}

	public void setSelectedItemText(String texts){
		selectedItemIndexes = null;
		
		for (String text : texts.split(","))
			if(dataSourceFields.contains(text))
				selectedItemIndexes.add("" + dataSourceValues.indexOf(text));
		
		setSelections();
	}
	
	public void setSelectedItemIndex(List<String> indexes){
		selectedItemIndexes = indexes;		
		setSelections();
	}

	public String getSelectedItemValues(){
		return selectedItemValues;
	}

	public String getSelectedItemTexts(){
		return selectedItemTexts;
	}

	public List<String> getSelectedItemIndexes(){
		return selectedItemIndexes;
	}

	public class CheckboxAdapter extends BaseAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	        View item = inflater.inflate(cbItemTemplateId, null);
	    	item.setId(1000 + position);
		    
		    if(dataSourceFields.get(position) != null)
		    	((TextView)item.findViewById(R.id.itemText)).setText(dataSourceFields.get(position));  

	    	CheckBox cb = (CheckBox)item.findViewById(R.id.cb);
	    	cb.setSelected(selectedItemIndexes.contains(position));
	    	cb.setTag(position);
	    	
	    	cb.setOnCheckedChangeListener( new OnCheckedChangeListener() {			
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					int which = Integer.parseInt(buttonView.getTag().toString());
					
			    	if(isChecked){
					    if (!selectedItemIndexes.contains(which)) 
					    	selectedItemIndexes.add("" + which);
			    	}
			    	else{
					    if (selectedItemIndexes.contains(which))
					    	selectedItemIndexes.remove(which);
			    	}
				}
			});
			    
			return item;
		}
	
		@Override
		public int getCount() {
			return dataSourceFields.size();
		}
	
		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}
}