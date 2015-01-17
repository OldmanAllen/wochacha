package com.example.wochacha.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.wochacha.R;
import com.example.wochacha.fragment.FindFragment;
import com.example.wochacha.fragment.FragmentBase;
import com.example.wochacha.fragment.HomeFragment;
import com.example.wochacha.fragment.MessageFragment;
import com.example.wochacha.fragment.ProfileFragment;
import com.example.wochacha.fragment.HomeFragment.HomeFragmentCallback;
import com.example.wochacha.manager.DeviceGeoLocationManager;
import com.example.wochacha.manager.MessageManager;
import com.example.wochacha.manager.MessageManager.onMessageNofify;
import com.example.wochacha.util.ToastMessageHelper;

public class MainActivity extends Activity implements HomeFragmentCallback, RadioGroup.OnCheckedChangeListener {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	// private NavigationDrawerFragment mNavigationDrawerFragment;

	private ViewPager vp_fragment;

	List<FragmentBase> fragments = new ArrayList<FragmentBase>();
	List<Integer> ids = new ArrayList<>();

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */

	RadioGroup maintabs_tabs_bar;
	int lastCheckedId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * mNavigationDrawerFragment = (NavigationDrawerFragment)getFragmentManager().findFragmentById(
		 * R.id.navigation_drawer);
		 * 
		 * // Set up the drawer. mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
		 * (DrawerLayout)findViewById(R.id.drawer_layout));
		 */

		vp_fragment = (ViewPager)findViewById(R.id.vp_fragment);

		getActionBar().setDisplayShowTitleEnabled(false);

		lastCheckedId = R.id.maintabs_tab_home;

		HomeFragment homeFragment = new HomeFragment();
		homeFragment.setVisibility(true);
		
		
		//fragments.add(new FindFragment());
		fragments.add(new MessageFragment());
		fragments.add(homeFragment);
		fragments.add(new ProfileFragment());

		
		//ids.add(R.id.maintabs_tab_find);
		ids.add(R.id.maintabs_tab_message);
		ids.add(R.id.maintabs_tab_home);
		ids.add(R.id.maintabs_tab_profile);

		maintabs_tabs_bar = (RadioGroup)findViewById(R.id.maintabs_tabs_bar);
		maintabs_tabs_bar.check(R.id.maintabs_tab_home);
		maintabs_tabs_bar.setOnCheckedChangeListener(this);

		SectionsPagerAdapter adapter = new SectionsPagerAdapter(getFragmentManager(), fragments);
		vp_fragment.setAdapter(adapter);
		vp_fragment.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				maintabs_tabs_bar.check(ids.get(position));
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		vp_fragment.setOffscreenPageLimit(2);
		vp_fragment.setCurrentItem(1,false);

		MessageManager.getInstance().registerMessageNotification(notifyListener);

		toggleMessgeIcon(MessageManager.getInstance().hasUnreadMessage());

		DeviceGeoLocationManager geoManager = DeviceGeoLocationManager.getInstance();
		if (!geoManager.isLocationServiceEnabled()) {
			geoManager.enableLocation(this);
		} else {
			geoManager.registerLocationUpdate();
		}

	}

	protected void onDestroy() {
		super.onDestroy();
		MessageManager.getInstance().unRegisterMessageNotificaiton(notifyListener);

	};

	onMessageNofify notifyListener = new onMessageNofify() {

		@Override
		public void onAllMessageReaded() {
			toggleMessgeIcon(false);

		}

		@Override
		public void onMessageNotified() {
			toggleMessgeIcon(true);

		}
	};

	private void toggleMessgeIcon(boolean hasNewMessage) {
		int drawableId = hasNewMessage ? R.drawable.maintabs_tab_message_new_btn : R.drawable.maintabs_tab_message_btn;
		RadioButton radioButton = (RadioButton)maintabs_tabs_bar.findViewById(R.id.maintabs_tab_message);
		Drawable drawable = getResources().getDrawable(drawableId);

		radioButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(drawableId), null, null);
	}

	private void resetFragments(int position) {

		int idx = 0;
		for (FragmentBase fragment : fragments) {
			if (position == idx) {
				fragment.setVisibility(true);				
				fragment.resetActionBar();
			} else {
				fragment.setVisibility(false);
				//fragment.onPause();
			}
			idx++;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case DeviceGeoLocationManager.REQUEST_LOCATION_CODE:
			if (DeviceGeoLocationManager.getInstance().isLocationServiceEnabled()) {
				DeviceGeoLocationManager.getInstance().registerLocationUpdate();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		if (checkedId != lastCheckedId) {
			int position = ids.indexOf(checkedId);
			vp_fragment.setCurrentItem(position, false);
			resetFragments(position);

		}
		lastCheckedId = checkedId;

	}

	public static class SectionsPagerAdapter extends android.support.v13.app.FragmentPagerAdapter {

		private List<FragmentBase> fragmentList;

		public SectionsPagerAdapter(FragmentManager fm, Collection<FragmentBase> list) {
			super(fm);
			fragmentList = new ArrayList<>();
			Iterator<FragmentBase> iterator = list.iterator();
			while (iterator.hasNext()) {
				FragmentBase fragmentBase = (FragmentBase)iterator.next();
				fragmentList.add(fragmentBase);
			}
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}

		@Override
		public int getCount() {

			return 3;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * if (!mNavigationDrawerFragment.isDrawerOpen()) { // Only show items in the action bar relevant to this screen
		 * // if the drawer is not showing. Otherwise, let the drawer // decide what to show in the action bar.
		 * getMenuInflater().inflate(R.menu.global, menu); return true; }
		 */
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDrawerToogleClicked() {
		// mNavigationDrawerFragment.toggleDrawer();
	}

	private int backPressCount = 0;

	@Override
	public void onBackPressed() {
		if (backPressCount++ == 0) {
			ToastMessageHelper.showErrorMessage(this, R.string.backpress_to_exit, 1500);

		} else {
			this.finish();
		}

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				backPressCount = 0;
			}
		}, 1500);

	}

}
