package com.example.wochacha.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wochacha.R;
import com.example.wochacha.adapter.ProductAdapter;
import com.example.wochacha.entity.ScanResult.ScanProduct;
import com.example.wochacha.entity.ScanResult.ScanProductType;
import com.example.wochacha.ui.ScanResultActivity;
import com.example.wochacha.ui.ScanResultActivity.AutoSizePager;
import com.example.wochacha.util.ViewHelper;

public class ProductInfoFragment extends Fragment {

	TextView tv_product_bar_value;
	TextView tv_product_name_value;

	ListView lv_details;

	ScanProductType productType;
	int height;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e("test", "ProductInfoFragment onCreate");
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("test", "ProductInfoFragment onCreateView");
		View view = inflater.inflate(R.layout.fragment_product_info, container,
				false);

		tv_product_bar_value = (TextView) view
				.findViewById(R.id.tv_product_bar_value);
		tv_product_name_value = (TextView) view
				.findViewById(R.id.tv_product_name_value);
		lv_details = (ListView) view.findViewById(R.id.lv_details);

		tv_product_bar_value.setText(productType.getBarCode());
		tv_product_name_value.setText(productType.getProductName());

		ProductAdapter adapter = new ProductAdapter(this.getActivity());
		adapter.setListData(productType.getKeyValuePairsFromDetails());
		lv_details.setAdapter(adapter);
		if (getUserVisibleHint() == true) {
			this.height = ViewHelper.calcFragmentHeight(lv_details, view);
			ScanResultActivity activity = (ScanResultActivity) getActivity();
			activity.setViewPagerHeight(height);
		}
		return view;

	}

	@Override
	public void onResume() {
		Log.e("test", "ProductInfoFragment onResume");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.e("test", "ProductInfoFragment onPause");
		super.onPause();
	}

	public void setData(ScanProduct product) {
		this.productType = product.getProductType();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && getView() != null) {

			ScanResultActivity activity = (ScanResultActivity) getActivity();
			activity.setViewPagerHeight(this.height);
		}
	}

	

}
