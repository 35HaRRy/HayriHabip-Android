package com.hayrihabip.items;

import android.app.AlertDialog;
import android.content.Context;

public class PixDialog extends AlertDialog {

	public PixDialog(Context context, int theme, int width, int height) {
		super(context, theme);
		
		getWindow().setLayout(width, height);
	}
}