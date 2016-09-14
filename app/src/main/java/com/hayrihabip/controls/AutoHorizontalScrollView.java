package com.hayrihabip.controls;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.hayrihabip.R;

public class AutoHorizontalScrollView extends HorizontalScrollView {
	private Activity mActivity;
	private AttributeSet attrs;
	
	public LinearLayout llItems;
	public BaseAdapter mAdapter;
	
	private FrameLayout.LayoutParams fParams;	
	private LinearLayout.LayoutParams lParams;
	
	private int scrollMax;
	private int scrollPos =	0;
	
	private TimerTask scrollerSchedule;
	private Timer scrollTimer =	null;
	
	public AutoHorizontalScrollView(Context context) {
		super(context);
		
		this.mActivity = (Activity)context;
		
		llItems = new LinearLayout(context);
		Init();
	}
	
	public AutoHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.attrs = attrs;
		this.mActivity = (Activity)context;
		
		llItems = new LinearLayout(mActivity, attrs);
		Init();
	}
	
	public AutoHorizontalScrollView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		this.attrs = attrs;
		this.mActivity = (Activity)context;
		
		llItems = new LinearLayout(mActivity, attrs);
		Init();
	}
	
	protected void Init(){
		TypedArray tArray = mActivity.obtainStyledAttributes(attrs, R.styleable.ControlAttrs);

		//isRequired = tArray.getBoolean(R.styleable.ControlAttrs_isRequired, false);
		
		tArray.recycle();

		setHorizontalScrollBarEnabled(false);
        setHorizontalFadingEdgeEnabled(false);

		llItems.setOrientation(LinearLayout.HORIZONTAL);
		addView(llItems);
		
		ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            	getViewTreeObserver().removeGlobalOnLayoutListener(this);
            	
            	getScrollMaxAmount();
            	startAutoScrolling();            	          	
            }
        });
    }
	
	public void setAdapter(BaseAdapter adapter){
		mAdapter = adapter;
		
		llItems.removeAllViews();
			
		for (int i = 0; i < mAdapter.getCount(); i++) 
			llItems.addView(mAdapter.getView(i, null, llItems));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {	
		super.onTouchEvent(event);
		
		int act  = event.getAction();
		
		if(act == MotionEvent.ACTION_DOWN)
			stopAutoScrolling();
		else if(act == MotionEvent.ACTION_UP || act == MotionEvent.ACTION_CANCEL )
			startAutoScrolling();

		return true;
	}
    
    public void getScrollMaxAmount(){
    	/*int actualWidth = (getMeasuredWidth()-512);
    	scrollMax   = actualWidth;*/
    	scrollMax = getMeasuredWidth();
    }
    
    public void startAutoScrolling(){
		if (scrollTimer == null) {
			scrollTimer = new Timer();
			final Runnable Timer_Tick =	new Runnable() {
			    public void run() {
			    	moveScrollView();
			    }
			};
			
			if(scrollerSchedule != null){
				scrollerSchedule.cancel();
				scrollerSchedule = null;
			}
			
			scrollerSchedule = new TimerTask(){
				@Override
				public void run(){
					mActivity.runOnUiThread(Timer_Tick);
				}
			};
			
			scrollTimer.schedule(scrollerSchedule, 30, 30);
		}
	}
    
    @SuppressLint("NewApi")
	public void moveScrollView(){
    	scrollPos =	(int) (getScrollX() + 1.0);
		if(scrollPos >= scrollMax)
			scrollPos =	0;

		//scrollTo(scrollPos, 0);
		scrollBy(532, 0);
	}
    
    public void stopAutoScrolling(){
		if (scrollTimer != null) {
			scrollTimer.cancel();
			scrollTimer	= null;
		}
	}
    
    /*public void onBackPressed(){
		super.onBackPressed();
		finish();
	}
	
	public void onPause() {
		super.onPause();
		finish();
	}
	
	public void onDestroy(){
		clearTimerTaks(scrollerSchedule);		
		clearTimers(scrollTimer);
		
		scrollerSchedule      = null;
		scrollTimer           = null;

		super.onDestroy();
	}*/
	
	private void clearTimers(Timer timer){
	    if(timer != null) {
		    timer.cancel();
	        timer = null;
	    }
	}
	
	private void clearTimerTaks(TimerTask timerTask){
		if(timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
	}
	
}