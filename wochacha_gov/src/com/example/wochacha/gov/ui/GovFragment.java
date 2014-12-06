package com.example.wochacha.gov.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.wochacha.adapter.EnterpriseHomeAdapter;
import com.example.wochacha.entity.EnterpriseHomeItem;
import com.example.wochacha.gov.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

public class GovFragment extends Fragment {

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */

	PullToRefreshGridView gridView;
	EnterpriseHomeAdapter adapter;
	List<EnterpriseHomeItem> items = new ArrayList<>();

	public static GovFragment newInstance() {
		GovFragment fragment = new GovFragment();
		return fragment;
	}

	public GovFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		int[] array = new int[] { 7, R.drawable.home_warning,
				R.string.enterprise_warning, 8, R.drawable.home_dashboard,
				R.string.enterprise_dashboard, 1,
				R.drawable.home_product_analytics,
				R.string.enterprise_product_analytics, 2,
				R.drawable.home_marketing,
				R.string.enterprise_marketing_management, 3,
				R.drawable.home_tag_management,
				R.string.enterprise_tag_management, 4,
				R.drawable.home_fleeing_analytics,
				R.string.enterprise_fleeing_analytics, 5,
				R.drawable.home_customer_analytics,
				R.string.enterprise_customer_analytics, 6,
				R.drawable.home_logistics_analytics,
				R.string.enterprise_logistics_analytics };
		String[] urls = new String[] {
				"http://wweb.chinacloudapp.cn/admin/pages/tables/simple.html",
				"http://wweb.chinacloudapp.cn/admin/pages/demo/kanban.html",
				"http://wweb.chinacloudapp.cn/admin/pages/charts/flot.html",
				"http://wweb.chinacloudapp.cn/admin/pages/charts/morris.html",
				"http://wweb.chinacloudapp.cn/admin/pages/tables/simple.html",
				"http://wweb.chinacloudapp.cn/admin/pages/demo/flot.html",
				"http://wweb.chinacloudapp.cn/admin/pages/demo/morris.html",
				"http://wweb.chinacloudapp.cn/admin/pages/tables/simple.html" };
		int count = array.length / 3;
		for (int i = 0; i < count; i++) {
			EnterpriseHomeItem item = new EnterpriseHomeItem();
			item.setIconResourceId(array[i * 3 + 1]);
			item.setItemId(array[i * 3]); // start from 1.
			item.setTitle(getString(array[i * 3 + 2]));
			items.add(item);
		}
		int i = 0;
		for (EnterpriseHomeItem item : items) {
			item.setActionUrl(urls[i++]);
		}

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_enterprise,
				container, false);
		gridView = (PullToRefreshGridView) rootView.findViewById(R.id.gv_actions);
		adapter = new EnterpriseHomeAdapter(getActivity());
		adapter.setListData(items);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EnterpriseHomeItem item = (EnterpriseHomeItem) adapter
						.getItem(position);
				item.setNewMessageCount(0);
				adapter.notifyDataSetChanged();

				Intent intent = new Intent(GovFragment.this
						.getActivity(), GovTodoActivity.class);
				intent.putExtra(GovTodoActivity.IntentKey.TITLE,
						item.getTitle());
				intent.putExtra(GovTodoActivity.IntentKey.URL,
						item.getActionUrl());
				startActivity(intent);

			}
		});

		gridView.setOnRefreshListener(new OnRefreshListener<GridView>() {

			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						gridView.onRefreshComplete();
						items.get(0).setNewMessageCount(2);
						items.get(1).setNewMessageCount(1);
						adapter.notifyDataSetChanged();

					}
				}, 1000);

			}

		});

		return rootView;
	}

}
