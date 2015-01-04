package com.example.wochacha.fragment;

import com.example.wochacha.R;
import com.example.wochacha.adapter.ScanHistoryAdapter;
import com.example.wochacha.entity.ScanResult.ScanRecords;
import com.example.wochacha.ui.ScanResultActivity;
import com.example.wochacha.util.ViewHelper;

import android.R.integer;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ProductScanInfoFragment extends Fragment {

	ListView listView;
	ScanRecords records;
	int height;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_scan_history, container,
				false);
		listView = (ListView) view.findViewById(R.id.lv_scan_history);
		ScanHistoryAdapter adapter = new ScanHistoryAdapter(getActivity());
		adapter.setListData(records.getScanRecordList());
		listView.setAdapter(adapter);
		this.height = ViewHelper.calcFragmentHeight(listView, view);
		return view;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && getView() != null) {
			int height = ViewHelper.calcFragmentHeight(listView, null);
			ScanResultActivity activity = (ScanResultActivity) getActivity();
			activity.setViewPagerHeight(height);
		}
	}

	public void setData(ScanRecords scanRecords) {
		this.records = scanRecords;

	}
}
