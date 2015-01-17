package com.example.wochacha.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wochacha.R;
import com.example.wochacha.entity.KeyValuePair;
import com.example.wochacha.entity.Message;
import com.example.wochacha.network.ImageManager;
import com.example.wochacha.network.ImageViewInfo;
import com.example.wochacha.view.BadgeView;

public class ProductAdapter extends BaseAdapter {

	LayoutInflater inflater;

	List<KeyValuePair> listData = new ArrayList<KeyValuePair>();

	public ProductAdapter(Activity activity) {
		inflater = activity.getLayoutInflater();
	}

	public void setListData(List<KeyValuePair> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		this.listData.clear();
		this.listData.addAll(list);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {

		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		KeyValuePair item = (KeyValuePair) getItem(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.scan_detail_item_layout,
					parent, false);
			ViewHolder holder = new ViewHolder();

			holder.keyTextView = (TextView) convertView
					.findViewById(R.id.tv_key);
			holder.valueTextView = (TextView) convertView
					.findViewById(R.id.tv_value);

			convertView.setTag(holder);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();

		holder.keyTextView.setText(item.getKey());
		holder.valueTextView.setText(item.getValue());

		return convertView;
	}

	private static class ViewHolder {

		public TextView keyTextView;
		public TextView valueTextView;
	}

}
