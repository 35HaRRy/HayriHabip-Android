package com.hayrihabip.items;

import java.util.regex.Pattern;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Toast;

import com.hayrihabip.controls.TextBox;

public class Validator implements TextWatcher  {
	TextBox txt;
	
	public Validator(TextBox txt){
		this.txt = txt;
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		ValidationResult validationResult = Validate(s.toString());
		
		if (validationResult.isValid) {
			
		}
		else{
			txt.txt.setError(validationResult.message);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		//txt.Validate(txt.validationGroup);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		//txt.Validate(txt.validationGroup);
	}
	
	public ValidationResult Validate(String s){
		ValidationResult validationResult = new ValidationResult(true, null);
		
		//String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	    String PHONE_REGEX = "^[(]\\d{3}[)] \\d{3} \\d{2} \\d{2}";
	    String DATE_REGEX = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
	    String DATETIME_REGEX = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d) ([01]\\d|2[0123])[:]([0-5]\\d)";

		if(txt.isRequired && Functions.IsNullOrEmpty(s)){
			validationResult.isValid = false;
			validationResult.message = txt.textRv;
		}
		
		//Regex validation
		if(txt.validateType == "Email") {
			if(!Patterns.EMAIL_ADDRESS.matcher(s).matches()){
				validationResult.isValid = false;
				validationResult.message = txt.textRegex;
			}
			Toast.makeText(txt.getContext(), "EMAIL_ADDRESS", Toast.LENGTH_SHORT).show();
		}
		else if(txt.validateType == "Phone"){
			if( !Pattern.matches(PHONE_REGEX, s)){
				validationResult.isValid = false;
				validationResult.message = txt.textRegex;
			}
			Toast.makeText(txt.getContext(), "PHONE", Toast.LENGTH_SHORT).show();
		}
		else if(txt.validateType == "Date"){
			if( !Pattern.matches(DATE_REGEX, s) ){
				validationResult.isValid = false;
				validationResult.message = txt.textRegex;
			}
			Toast.makeText(txt.getContext(), "TYPE_DATETIME_VARIATION_DATE", Toast.LENGTH_SHORT).show();
		}
		else if(txt.validateType == "DateTime"){
			if( !Pattern.matches(DATETIME_REGEX, s) ){
				validationResult.isValid = false;
				validationResult.message = txt.textRegex;
			}
			Toast.makeText(txt.getContext(), "TYPE_DATETIME_VARIATION_NORMAL", Toast.LENGTH_SHORT).show();
		}
			
		return validationResult;
	}
}
