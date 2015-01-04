package com.example.wochacha.fragment;

import java.util.ArrayList;
import java.util.List;

import com.example.wochacha.R;
import com.example.wochacha.adapter.ScanPathAdapter;
import com.example.wochacha.entity.ScanResult.ScanPath;
import com.example.wochacha.ui.ScanResultActivity;
import com.example.wochacha.util.ViewHelper;

import android.R.anim;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;

public class ProductPathInfoFragment extends Fragment {

	ListView listView;
	List<ScanPath> paths = new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e("test", "ProductPathInfoFragment onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("test", "ProductPathInfoFragment onCreateView");
		View view = inflater.inflate(R.layout.fragment_tracking_info,
				container, false);
		listView = (ListView) view.findViewById(R.id.lv_tracking_info);
		ScanPathAdapter pathAdapter = new ScanPathAdapter(getActivity());
		pathAdapter.setListData(paths);
		listView.setAdapter(pathAdapter);
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

	@Override
	public void onResume() {
		Log.e("test", "ProductPathInfoFragment onResume");
		super.onResume();
	}

	public void setData(List<ScanPath> pathList) {
		paths.clear();
		paths.addAll(pathList);

	}
}
