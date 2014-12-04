package com.example.wochacha.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wochacha.R;
import com.example.wochacha.entity.EnterpriseAccount;
import com.example.wochacha.manager.EnterpriseSessionManager;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer. See the <a
 * href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"> design guidelines</a> for a
 * complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 0;

	// private boolean mFromSavedInstanceState;
	// private boolean mUserLearnedDrawer;

	public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Select either the default item (0) or the last selected item.
		selectItem(mCurrentSelectedPosition);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mDrawerListView = (ListView)inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});
		mDrawerListView.setAdapter(new ArrayAdapter<String>(getActionBar().getThemedContext(),
				android.R.layout.simple_list_item_activated_1, android.R.id.text1, new String[] {
						getString(R.string.enterprise_home), getString(R.string.enterprise_settings),
						getString(R.string.enterprise_about), }));
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		return mDrawerListView;
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	protected void setupActionBar() {
		EnterpriseAccount account = EnterpriseSessionManager.getInstance().getAuthUser();

		ActionBar actionBar = getActivity().getActionBar();
		LayoutInflater inflater = getActivity().getLayoutInflater();

		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setIcon(R.drawable.ic_drawer);

		View actionBarView = inflater.inflate(R.layout.enterprise_title_bar, null);
		TextView tv_title = (TextView)actionBarView.findViewById(R.id.tv_title);
		tv_title.setText(account.getName());
		actionBar.setCustomView(actionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER));
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener

		setupActionBar();

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.

		mDrawerLayout.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDrawerOpened(View arg0) {

				getActionBar().setIcon(R.drawable.br_prev);
			}

			@Override
			public void onDrawerClosed(View arg0) {
				getActionBar().setIcon(R.drawable.ic_drawer);

			}
		});

	}

	private void selectItem(int position) {
		mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
		if (position == 1) {
			Intent intent = new Intent(NavigationDrawerFragment.this.getActivity(), TodoActivity.class);
			intent.putExtra(TodoActivity.IntentKey.TITLE, getString(R.string.enterprise_settings));
			startActivity(intent);
			mCurrentSelectedPosition = 0;
		} else if (position == 2) {
			Intent intent = new Intent(NavigationDrawerFragment.this.getActivity(), TodoActivity.class);
			intent.putExtra(TodoActivity.IntentKey.TITLE, getString(R.string.enterprise_about));			
			startActivity(intent);
			mCurrentSelectedPosition = 0;
		}
	}

	public void toggleDrawer() {
		if (mDrawerLayout.isDrawerOpen(mFragmentContainerView)) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		} else {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.
		if (mDrawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.global, menu);
			// showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		// it's drawer indeed.
		if (itemId == android.R.id.home) {
			if (isDrawerOpen()) {
				getActionBar().setIcon(R.drawable.ic_drawer);
			} else {
				getActionBar().setIcon(R.drawable.br_prev);
			}
			toggleDrawer();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to show the global app 'context', rather than
	 * just what's in the current screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return getActivity().getActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
	}
}
