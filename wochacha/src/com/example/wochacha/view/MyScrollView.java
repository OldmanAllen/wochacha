package com.example.wochacha.view;

import com.example.wochacha.util.DensityUtil;
import com.viewpagerindicator.TabPageIndicator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

	private GestureDetector mGestureDetector;

	private View fixedHeaderView;

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(context, new YScrollDetector());
	}

	public void setFixedHeaderView(View view) {
		fixedHeaderView = view;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		return super.onInterceptTouchEvent(ev);
		// && mGestureDetector.onTouchEvent(ev);

	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		Log.e("tes", "lt" + t + "oldt" + oldt + "x"+getScrollY());
		
		
		if (t < oldt) { // down
			if (getScrollY() < fixedPos) {
				if (fixedHeaderView.isShown())
					fixedHeaderView.setVisibility(View.GONE);
			}
		} else if (t > oldt) { // up
			if (getScrollY() >= fixedPos) {
				if (!fixedHeaderView.isShown())
					fixedHeaderView.setVisibility(View.VISIBLE);
			}
		}

	}

	class YScrollDetector extends SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			if (distanceY < 0 && Math.abs(distanceY) >= Math.abs(distanceX))
				return false;
			else
				return true;
		}
	}

	int fixedPos;

	public void setFixedPosition(int fixedPos) {
		this.fixedPos = fixedPos;

	}
	
	

	
}
