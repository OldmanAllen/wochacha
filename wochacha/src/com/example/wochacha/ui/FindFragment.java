package com.example.wochacha.ui;

import com.example.wochacha.R;

import android.R.interpolator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class FindFragment extends FragmentBase {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private HomeFragmentCallback callback;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static FindFragment newInstance(int sectionNumber) {
		FindFragment fragment = new FindFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public FindFragment() {
	}

	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_find, container, false);		
		return rootView;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_scan) {
			if (callback != null) {
				callback.onDrawerToogleClicked();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_scan).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onResume() {		
		super.onResume();
		Log.e("test", "hhhhhh--->>>>>>");
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof HomeFragmentCallback) {
			callback = (HomeFragmentCallback)activity;
		}
	}

	public interface HomeFragmentCallback {
		public void onDrawerToogleClicked();
	}

	@Override
	protected String getTitle() {
		return getString(R.string.tab_find);
	}
}