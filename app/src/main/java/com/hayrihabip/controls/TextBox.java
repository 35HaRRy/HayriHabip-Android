package com.hayrihabip.controls;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.hayrihabip.R;
import com.hayrihabip.items.BaseView;
import com.hayrihabip.items.Functions;
import com.hayrihabip.items.ValidationResult;

public class TextBox extends BaseView{
	private Dialog datePickerDialog;	
	private LayoutParams params;
	
	public TextBox(Context context) {
		super(context);
		Init(context, null);
	}
	
	public TextBox(Context context, AttributeSet attrs) {
		super(context, attrs);		
		Init(context, attrs);
	}
	
	public void Init(Context context, AttributeSet attrs){
		TextBoxFocusChangeListener TextBoxFocusChangeListener = new TextBoxFocusChangeListener();
		TextBoxClickListener TextBoxClickListener = new TextBoxClickListener();
		
		if(!renderType.contains("NoLabel")){
			tVLabel = (TextView)mActivity.getLayoutInflater().inflate(labeltemplateId, null);
			tVLabel.setLayoutParams(params);
			tVLabel.setText(label);
			//tVLabel.setId(getId() + 1000);

			tVLabel.setOnFocusChangeListener(TextBoxFocusChangeListener);
			tVLabel.setOnClickListener(TextBoxClickListener);			
			
			addView(tVLabel);
		}
		
		txt.setLayoutParams(params);
		txt.setOnFocusChangeListener(TextBoxFocusChangeListener);
		txt.setOnClickListener(TextBoxClickListener);
		addView(txt);
		
		if(!renderType.contains("NoIcon")){
			imgIcon = (ImageView)mActivity.getLayoutInflater().inflate(iconTemplateId, null);
			imgIcon.setLayoutParams(params);
			//imgIcon.setId(getId() + 1001);
			imgIcon.setVisibility(View.GONE);
			addView(imgIcon);
			
			imgIcon.setOnFocusChangeListener(TextBoxFocusChangeListener);
			imgIcon.setOnClickListener(TextBoxClickListener);
		}
		
		//Hint
		if( txt.getHint() == null ){
			if(validateType == "Email") 
				txt.setHint(R.string.hintEmail);
			else if(validateType == "Phone")
				txt.setHint(R.string.hintPhone);
			else if(validateType == "Date"){
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				txt.setHint(dateFormat.format( new Date()));
			}
			else if(validateType == "DateTime"){
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
				txt.setHint(dateFormat.format( new Date()));
			}
		}
		
		setFocusable(false);
		setFocusableInTouchMode(false);
	}
	
	private class TextBoxFocusChangeListener implements OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus)  {
            if (hasFocus == true)
            	stateActive();
            else{
            	Validate(validationGroup);
            	statePassive();
            }
        }
    }
	
	private class TextBoxClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {   
			stateActive();   
		}
    }
	
	private OnDateSetListener datePickerListener = new OnDateSetListener() { 
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			TextBox.this.txt.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year );		
		}
	};
	
	public ValidationResult Validate(String _validationGroup){
		ValidationResult validationResult = new ValidationResult(true, null);
		
		if (validationGroup.equals(_validationGroup) ) {
			if(isRequired && Functions.IsNullOrEmpty(txt.getText().toString())){
				validationResult.isValid = false;
				validationResult.message = textRv;
			}
			
			//Regex validation
			if(validateType == "Email") {
				if(!Patterns.EMAIL_ADDRESS.matcher(txt.getText()).matches()){
					validationResult.isValid = false;
					validationResult.message = textRegex;
				}
			}
			else if(validateType == "Phone"){
				if( !Pattern.matches(PHONE_REGEX, txt.getText()) ){
					validationResult.isValid = false;
					validationResult.message = textRegex;
				}
			}
			else if(validateType == "Date"){
				if( !Pattern.matches(DATE_REGEX, txt.getText()) ){
					validationResult.isValid = false;
					validationResult.message = textRegex;
				}
			}
			else if(validateType == "DateTime"){
				if( !Pattern.matches(DATETIME_REGEX, txt.getText()) ){
					validationResult.isValid = false;
					validationResult.message = textRegex;
				}
			}
			
			if(!validationResult.isValid)
				txt.setError(validationResult.message);
		}			
				
		return validationResult;
	}

    private void setDatePicker(){
		if(validateType == "Date"){
			
			Calendar c = Calendar.getInstance();
			Dialog tempDialog = new DatePickerDialog(TextBox.this.mActivity, datePickerListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			
			if(txt.getText() == null)
			    datePickerDialog = tempDialog;
			else{
				if( Pattern.matches(DATE_REGEX, txt.getText()) ){
					String[] values = txt.getText().toString().split("/");
					datePickerDialog = new DatePickerDialog(TextBox.this.mActivity, datePickerListener, Integer.parseInt(values[2]), Integer.parseInt(values[1])-1, Integer.parseInt(values[0]));
				}
				else
				    datePickerDialog = tempDialog;					
			}

			datePickerDialog.setTitle(R.string.datePickerTitle);
		    datePickerDialog.show();
		}
		/*else if((getInputType() & InputType.TYPE_DATETIME_VARIATION_NORMAL) == InputType.TYPE_DATETIME_VARIATION_NORMAL){
			datePickerDialog = new Dialog(context);
			datePickerDialog.setContentView(R.layout.datetimepicker);
			datePickerDialog.setTitle(R.string.datePickerTitle);
			
			Button okButton = (Button)datePickerDialog.findViewById(R.id.okButton);
			okButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {  
					DatePicker dPicker = (DatePicker)datePickerDialog.findViewById(R.id.dPicker);
					TimePicker tPicker = (TimePicker)datePickerDialog.findViewById(R.id.tPicker);
					
					TextBox.this.setText(Functions.getDateFromDatePicker(dPicker) + " " + tPicker.getCurrentHour() + ":" + tPicker.getCurrentMinute());
					Toast.makeText(context, "Click", Toast.LENGTH_LONG).show();
					datePickerDialog.dismiss();
				}
			});
			
			Button cancelButton = (Button)datePickerDialog.findViewById(R.id.cancelButton);		
			cancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					datePickerDialog.dismiss();
				}
			});

			datePickerDialog.show();
		}*/
    }

    private void stateActive(){    	
    	if(!renderType.contains("NoLabel"))
    		tVLabel.requestFocus();
    	
		if(!renderType.contains("NoIcon"))
			imgIcon.requestFocus();
		
    	setDatePicker();
    }

    private void statePassive(){
    	/*if(!renderType.contains("NoLabel")){
    		tVLabel.requestFocus();
    	}
    	
		if(!renderType.contains("NoIcon")){
			imgIcon.setVisibility(View.VISIBLE);
		}*/
    }
}
