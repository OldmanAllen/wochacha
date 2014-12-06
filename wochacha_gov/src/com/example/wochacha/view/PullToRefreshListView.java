package com.example.wochacha.view;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wochacha.gov.R;

public class PullToRefreshListView extends ListView implements OnScrollListener {

    private static final int TAP_TO_REFRESH = 1;
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REFRESH = 3;
    private static final int REFRESHING = 4;

    private static final String TAG = PullToRefreshListView.class.getSimpleName();

    private OnRefreshListener onRefreshListener;

    /**
* Listener that will receive notifications every time the list scrolls.
*/
    private OnScrollListener onScrollListener;
    private LayoutInflater inflater;

    private LinearLayout footView;
    private RelativeLayout refreshView;
    private TextView refreshTextView;
    private ImageView refreshImageView;
    private ProgressBar refreshProgressBar;
    private TextView refreshLastUpdatedTextView;

    private int currentScrollState;
    private int refreshState;

    private RotateAnimation flipAnimation;
    private RotateAnimation reverseFlipAnimation;
    

    private int refreshViewHeight;    
    private int refreshOriginalTopPadding;
    private int lastMotionY;

    private boolean bounceHack;
    private ListAdapter adapter;
   
    private float rowHeight;
    
    public PullToRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshList);
        rowHeight = a.getDimension(R.styleable.PullToRefreshList_listRowHeight, 0f);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshList);
        rowHeight = a.getDimension(R.styleable.PullToRefreshList_listRowHeight, 0f);
        init(context);
    }

    private void init(Context context) {
        // Load all of the animations we need in code rather than through XML
        flipAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        flipAnimation.setInterpolator(new LinearInterpolator());
        flipAnimation.setDuration(250);
        flipAnimation.setFillAfter(true);
        reverseFlipAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseFlipAnimation.setInterpolator(new LinearInterpolator());
        reverseFlipAnimation.setDuration(250);
        reverseFlipAnimation.setFillAfter(true);

        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

refreshView = (RelativeLayout) inflater.inflate(
R.layout.pull_to_refresh_header, this, false);
        refreshTextView =
            (TextView) refreshView.findViewById(R.id.pull_to_refresh_text);
        refreshImageView =
            (ImageView) refreshView.findViewById(R.id.pull_to_refresh_image);
        refreshProgressBar =
            (ProgressBar) refreshView.findViewById(R.id.pull_to_refresh_progress);
        refreshLastUpdatedTextView =
            (TextView) refreshView.findViewById(R.id.pull_to_refresh_updated_at);

        refreshImageView.setMinimumHeight(50);
        refreshView.setOnClickListener(new OnClickRefreshListener());
        refreshOriginalTopPadding = refreshView.getPaddingTop();

        refreshState = TAP_TO_REFRESH;

        addHeaderView(refreshView);

        super.setOnScrollListener(this);

        measureView(refreshView);
        refreshViewHeight = refreshView.getMeasuredHeight();
        
        
        //LinearLayout footerView = (LinearLayout)mInflater.inflate(R.layout.footer_item, this, false);
        //addFooterView(footerView);
        
        //LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) footerView.getLayoutParams(); 
        //linearParams.height = 965; 
        //footerView.setLayoutParams(linearParams); 
        //footerView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onAttachedToWindow() {
        setSelection(1);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        this.adapter = adapter;
        setSelection(1);
        
    }

    /**
* Set the listener that will receive notifications every time the list
* scrolls.
*
* @param l The scroll listener.
*/
    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        onScrollListener = l;
    }

    /**
* Register a callback to be invoked when this list should be refreshed.
*
* @param onRefreshListener The callback to run.
*/
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    /**
* Set a text to represent when the list was last updated.
* @param lastUpdated Last updated at.
*/
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            refreshLastUpdatedTextView.setVisibility(View.VISIBLE);
            refreshLastUpdatedTextView.setText(lastUpdated);
        } else {
            refreshLastUpdatedTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int y = (int) event.getY();
        bounceHack = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (!isVerticalScrollBarEnabled()) {
                    setVerticalScrollBarEnabled(true);
                }
                if (getFirstVisiblePosition() == 0 && refreshState != REFRESHING) {
                    if ((refreshView.getBottom() > refreshViewHeight
                            || refreshView.getTop() >= 0)
                            && refreshState == RELEASE_TO_REFRESH) {
                        // Initiate the refresh
                        refreshState = REFRESHING;
                        prepareForRefresh();
                        onRefresh();
                    } else if (refreshView.getBottom() < refreshViewHeight
                            || refreshView.getTop() < 0) {
                        // Abort refresh and scroll down below the refresh view
                        resetHeader();
                        setSelection(1);
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                lastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                applyHeaderPadding(event);
                break;
        }
        return super.onTouchEvent(event);
    }
    
    // For hiding the refresh button
    private void resetFooter(){
    	int iFooterCount = this.getFooterViewsCount();
    	if(iFooterCount!=0){
    		//iOldFooterViewHeight = mFooterView.getHeight();
    		this.removeFooterView(footView);
    	}
    	
    	//View leafViewAtBottom = getLastLeafChild(this);
    	//int iListBottom = leafViewAtBottom.getBottom();
    	int iScreenBottom = this.getBottom(); 
    	int iItemCount = adapter.getCount();
    	
    	float fFooterHeight = 0f;
    	if(iItemCount*rowHeight < iScreenBottom){
    		fFooterHeight = iScreenBottom - iItemCount*rowHeight;
    	}
    	else
    		return;
    	
        footView = (LinearLayout)inflater.inflate(R.layout.footer_item, this, false);
		footView.setMinimumHeight((int)fFooterHeight);
        //footerView.setLayoutParams(new ViewGroup.LayoutParams(iMaxWidth, iMaxHeight-iListHeight));
        addFooterView(footView);
        footView.setVisibility(View.INVISIBLE);
       
    } 
    
    private void applyHeaderPadding(MotionEvent ev) {
        // Workaround for getPointerCount() which is unavailable in 1.5
        // (it's always 1 in 1.5)
        int pointerCount = 1;
        try {
            Method method = MotionEvent.class.getMethod("getPointerCount");
            pointerCount = (Integer)method.invoke(ev);
        } catch (NoSuchMethodException e) {
            pointerCount = 1;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IllegalAccessException e) {
            System.err.println("unexpected " + e);
        } catch (InvocationTargetException e) {
            System.err.println("unexpected " + e);
        }

        for (int p = 0; p < pointerCount; p++) {
            if (refreshState == RELEASE_TO_REFRESH) {
                if (isVerticalFadingEdgeEnabled()) {
                    setVerticalScrollBarEnabled(false);
                }

                int historicalY = (int) ev.getHistoricalY(p);

                // Calculate the padding to apply, we divide by 1.7 to
                // simulate a more resistant effect during pull.
                int topPadding = (int) (((historicalY - lastMotionY)
                        - refreshViewHeight) / 1.7);

                refreshView.setPadding(
                        refreshView.getPaddingLeft(),
                        topPadding,
                        refreshView.getPaddingRight(),
                        refreshView.getPaddingBottom());
            }
        }
    }

    /**
* Sets the header padding back to original size.
*/
    private void resetHeaderPadding() {
        refreshView.setPadding(
                refreshView.getPaddingLeft(),
                refreshOriginalTopPadding,
                refreshView.getPaddingRight(),
                refreshView.getPaddingBottom());
    }

    /**
* Resets the header to the original state.
*/
    private void resetHeader() {
        if (refreshState != TAP_TO_REFRESH) {
            refreshState = TAP_TO_REFRESH;

            resetHeaderPadding();

            // Set refresh view text to the pull label
            refreshTextView.setText(R.string.pull_to_refresh_tap_label);
            // Replace refresh drawable with arrow drawable
            refreshImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow);
            // Clear the full rotation animation
            refreshImageView.clearAnimation();
            // Hide progress bar and arrow.
            refreshImageView.setVisibility(View.GONE);
            refreshProgressBar.setVisibility(View.GONE);
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
                0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        // When the refresh view is completely visible, change the text to say
        // "Release to refresh..." and flip the arrow drawable.
        if (currentScrollState == SCROLL_STATE_TOUCH_SCROLL
                && refreshState != REFRESHING) {
            if (firstVisibleItem == 0) {
                refreshImageView.setVisibility(View.VISIBLE);
                if ((refreshView.getBottom() > refreshViewHeight + 20
                        || refreshView.getTop() >= 0)
                        && refreshState != RELEASE_TO_REFRESH) {
                    refreshTextView.setText(R.string.pull_to_refresh_release_label);
                    refreshImageView.clearAnimation();
                    refreshImageView.startAnimation(flipAnimation);
                    refreshState = RELEASE_TO_REFRESH;
                } else if (refreshView.getBottom() < refreshViewHeight + 20
                        && refreshState != PULL_TO_REFRESH) {
                    refreshTextView.setText(R.string.pull_to_refresh_pull_label);
                    if (refreshState != TAP_TO_REFRESH) {
                        refreshImageView.clearAnimation();
                        refreshImageView.startAnimation(reverseFlipAnimation);
                    }
                    refreshState = PULL_TO_REFRESH;
                }
            } else {
                refreshImageView.setVisibility(View.GONE);
                resetHeader();
            }
        } else if (currentScrollState == SCROLL_STATE_FLING
                && firstVisibleItem == 0
                && refreshState != REFRESHING) {
            setSelection(1);
            bounceHack = true;
        } else if (bounceHack && currentScrollState == SCROLL_STATE_FLING) {
            setSelection(1);
        }

        if (onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        currentScrollState = scrollState;

        if (currentScrollState == SCROLL_STATE_IDLE) {
            bounceHack = false;
        }

        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    public void prepareForRefresh() {
        resetHeaderPadding();

        refreshImageView.setVisibility(View.GONE);
        // We need this hack, otherwise it will keep the previous drawable.
        refreshImageView.setImageDrawable(null);
        refreshProgressBar.setVisibility(View.VISIBLE);

        // Set refresh view text to the refreshing label
        refreshTextView.setText(R.string.pull_to_refresh_refreshing_label);

        refreshState = REFRESHING;
    }

    public void onRefresh() {   	

        if (onRefreshListener != null) {
            onRefreshListener.onRefresh();
        }
    }

    /**
* Resets the list to a normal state after a refresh.
* @param lastUpdated Last updated at.
*/
    public void onRefreshComplete(CharSequence lastUpdated) {
        setLastUpdated(lastUpdated);
        onRefreshComplete();
    }

    
    /**
* Resets the list to a normal state after a refresh.
*/
    public void onRefreshComplete() {
    	Log.d(TAG, "onRefreshComplete");

        resetHeader();
        resetFooter();
        
        // If refresh view is visible when loading completes, scroll down to
        // the next item.
        if (refreshView.getBottom() > 0) {
            invalidateViews();
            setSelection(1);
        }
    }

    /**
* Invoked when the refresh view is clicked on. This is mainly used when
* there's only a few items in the list and it's not possible to drag the
* list.
*/
    private class OnClickRefreshListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (refreshState != REFRESHING) {
                prepareForRefresh();
                onRefresh();
            }
        }

    }

    /**
* Interface definition for a callback to be invoked when list should be
* refreshed.
*/
    public interface OnRefreshListener {
        /**
* Called when the list should be refreshed.
* <p>
* A call to {@link PullToRefreshListView #onRefreshComplete()} is
* expected to indicate that the refresh has completed.
*/
        public void onRefresh();
    }
}

