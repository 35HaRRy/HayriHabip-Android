package com.hayrihabip.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;

import com.hayrihabip.R;
import com.hayrihabip.items.ValidationResult;

public class PixButton extends Button {
	public String validationGroup;
	public Boolean isValid = true;
	public String message = null;
	
	ValidationResult validationResult;

	public PixButton(Context context) {
		super(context);
		Init(context, null, 0);
	}
	public PixButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		Init(context, attrs, 0);
	}
	public PixButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);		
		Init(context, attrs, defStyle);
	}
	
	public void Init(Context context, AttributeSet attrs, int defStyle){
		TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.ControlAttrs, defStyle, 0);
		validationGroup = tArray.getString(R.styleable.ControlAttrs_validationGroup);		
		tArray.recycle();		
	}
	
	public void ValidateToControls() {
		isValid = true;
		ValidateToViewGroup((ViewGroup)this.getParent());		
	}
	
	private void ValidateToViewGroup(ViewGroup vGroup){
	    String label = null;
		
	    if(vGroup != null){
			for (int i = 0; i < vGroup.getChildCount(); i++) {
				String tempMessage = null;
				boolean tempIsValid = true;
				
				try {
					if( vGroup.getChildAt(i).getClass().getName() == TextBox.class.getName() ){
						TextBox child = (TextBox)vGroup.getChildAt(i);
						
						validationResult = child.Validate(validationGroup);
						label = child.label != null ? child.label : child.getClass().getName();
					}
					else if( vGroup.getChildAt(i).getClass().getName() == Dropdownlist.class.getName() ){
						Dropdownlist child = (Dropdownlist)vGroup.getChildAt(i);

						validationResult = child.Validate(validationGroup);
						label = child.label != null ? child.label : child.getClass().getName();
					}
					else if( vGroup.getChildAt(i).getClass().getName() == Checkboxlist.class.getName() ){
						Checkboxlist child = (Checkboxlist)vGroup.getChildAt(i);

						validationResult = child.Validate(validationGroup);
						label = child.label != null ? child.label : child.getClass().getName();
					}
					else{
						tempIsValid = true;
						
						try {
							ValidateToViewGroup((ViewGroup)vGroup.getChildAt(i));
						} catch (Exception e) {	}
					}
					
					tempIsValid = validationResult.isValid;
					tempMessage = validationResult.message;
				} catch (Exception e) {
					tempIsValid = false;
					tempMessage = e.toString();
					e.printStackTrace();	
				} 
				
				if (!tempIsValid) {
					isValid = false;
					
					if(message == null)
						message = label + " " + tempMessage;
					else
						message += "\n" + label + " " + tempMessage;
				}
			}
	    }
	}
}