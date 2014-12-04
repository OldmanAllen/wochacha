package com.example.wochacha.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wochacha.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends FragmentBase {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private HomeFragmentCallback callback;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static ProfileFragment newInstance(int sectionNumber) {
		ProfileFragment fragment = new ProfileFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public ProfileFragment() {
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
		View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
		setupActionItems(rootView);
		return rootView;
	}

	private void setupActionItems(View contentView) {

		buildViewContent(contentView.findViewById(R.id.rl_my_scan), R.drawable.ic_my_scan, R.string.my_scan);
		buildViewContent(contentView.findViewById(R.id.rl_my_credits), R.drawable.ic_my_credits, R.string.my_credits);
		buildViewContent(contentView.findViewById(R.id.rl_my_favor), R.drawable.ic_my_favorite, R.string.my_favorite);
		buildViewContent(contentView.findViewById(R.id.rl_settings), R.drawable.ic_my_settings, R.string.settings);

	}

	private void buildViewContent(View view, int imageResourceId, int textResourceId) {
		ImageView iv = (ImageView)view.findViewById(R.id.iv_image);
		iv.setImageResource(imageResourceId);
		TextView tv = (TextView)view.findViewById(R.id.tv_action_name);
		tv.setText(textResourceId);
		view.setOnClickListener(actionClickedListener);
	}

	View.OnClickListener actionClickedListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String title = "";
			switch (v.getId()) {

			case R.id.rl_my_scan:
				title = getString(R.string.my_scan);
				break;
			case R.id.rl_my_credits:
				title = getString(R.string.my_credits);
				break;
			case R.id.rl_my_favor:
				title = getString(R.string.my_favorite);
				break;
			case R.id.rl_settings:
				title = getString(R.string.settings);
				break;
			default:
				break;
			}
			Intent intent = new Intent(ProfileFragment.this.getActivity(), TodoActivity.class);
			intent.putExtra(TodoActivity.IntentKey.TITLE, title);
			startActivity(intent);
		}
	};

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
		Log.e("test", "ME--->>>>>>");
		super.onResume();
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
		return getString(R.string.tab_profile);
	}
}