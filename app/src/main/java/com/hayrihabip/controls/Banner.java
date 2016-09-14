package com.hayrihabip.controls;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.hayrihabip.R;

public class Banner extends LinearLayout {
	private Activity mActivity;
	private AttributeSet attrs;

	public BannerPager vPager;	
	private LinearLayout llTabIcons;
	private LayoutParams params;
	
	private final int VPAGERID = 1000;
	private final int LLTABICONCID = 1001;
	
	private int selectedTabPosition;
	private int width;
	private int height;
	
	public int tabIconTemplateId;	
	public boolean isThereTabIcons;	
	
	private TimerTask scrollerSchedule;
	private Timer scrollTimer =	null;
	
	public Banner(Context context) {
		super(context);
		
		this.mActivity = (Activity)context;
		
		Init();
	}

	public Banner(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.attrs = attrs;
		this.mActivity = (Activity)context;
		
		Init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.v("widthMeasureSpec, heightMeasureSpec", widthMeasureSpec + " " + heightMeasureSpec);
		
		if(isThereTabIcons){
			width = widthMeasureSpec;
			height = heightMeasureSpec;
			
			params = new LayoutParams(width, height);
			params.height = ( params.height * 19 )/20;			
			vPager.setLayoutParams(params);

			params.height = params.height * 19;
			params.setMargins(0, width/20, 0, width/20);
			llTabIcons.setLayoutParams(params);
			llTabIcons.setGravity(Gravity.CENTER_HORIZONTAL);
			llTabIcons.setOrientation(LinearLayout.HORIZONTAL);
			
			llTabIcons.setId(LLTABICONCID);
			addView(llTabIcons);

			vPager.setOnPageChangeListener(new OnPageChangeListener() {			
				@Override
				public void onPageSelected(int position) {
					llTabIcons.getChildAt(selectedTabPosition).setSelected(false);
					llTabIcons.getChildAt(position).setSelected(true);

					Log.v("selectedTabPosition", "selectedTabPosition " + selectedTabPosition);
					Log.v("position", "position " + position);
					Log.v("llTabIcons", "llTabIcons " + llTabIcons.getChildAt(position));
					
					selectedTabPosition = position;
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		
		int act  = event.getAction();
		
		if(act == MotionEvent.ACTION_DOWN)
			stopAutoSliding();
		else if(act == MotionEvent.ACTION_UP || act == MotionEvent.ACTION_CANCEL )
			startAutoSliding();

		return true;
	}
    
	private void Init(){
		TypedArray tArray = mActivity.obtainStyledAttributes(attrs, R.styleable.ControlAttrs);

		isThereTabIcons = tArray.getBoolean(R.styleable.ControlAttrs_isThereTabIcons, false);
		tabIconTemplateId = tArray.getInt(R.styleable.ControlAttrs_tabIconTemplateId, R.layout.tabicontemplate);
		
		tArray.recycle();
		
		setOrientation(LinearLayout.VERTICAL);
		
		vPager = new BannerPager(mActivity);
		vPager.setId(VPAGERID);
		addView(vPager);
	}
    
    public void startAutoSliding(){
		if (scrollTimer == null) {
			scrollTimer = new Timer();
			
			final Runnable Timer_Tick =	new Runnable() {
			    public void run() {
			    	moveSlideView();
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
    
	public void moveSlideView(){
		selectedTabPosition = (selectedTabPosition + 1) % ( vPager.getChildCount() - 1);
		vPager.setCurrentItem(selectedTabPosition);
	}
    
    public void stopAutoSliding(){
		if (scrollTimer != null) {
			scrollTimer.cancel();
			scrollTimer	= null;
		}
	}
	
	public class BannerPager extends ViewPager {
		public BannerPager(Context context) {
			super(context);
		}

		public BannerPager(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		
		@Override
		public void setAdapter(PagerAdapter pAdapter) {
			super.setAdapter(pAdapter);
			
			if(isThereTabIcons){
				for (int i = 0; i < pAdapter.getCount(); i++) {
					View v = mActivity.getLayoutInflater().inflate(tabIconTemplateId, null);
					if(i == 1)
						v.setSelected(true);
					
					params = new LinearLayout.LayoutParams(width/10 , height/60);
					if (i != pAdapter.getCount() - 1)
						params.setMargins(0, 0, width/100, 0);
					v.setLayoutParams(params);
					
					llTabIcons.addView(v);
				}
			}
		}
	}
}