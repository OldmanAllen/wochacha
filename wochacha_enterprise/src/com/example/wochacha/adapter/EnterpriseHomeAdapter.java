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

import com.example.wochacha.enterprise.R;
import com.example.wochacha.entity.EnterpriseHomeItem;

public class EnterpriseHomeAdapter extends BaseAdapter {

	LayoutInflater inflater;

	List<EnterpriseHomeItem> listData = new ArrayList<EnterpriseHomeItem>();

	public EnterpriseHomeAdapter(Activity activity) {
		inflater = activity.getLayoutInflater();
	}

	public void setListData(List<EnterpriseHomeItem> list) {
		this.listData.clear();
		for (EnterpriseHomeItem item : list) {
			this.listData.add(item);
		}
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

		EnterpriseHomeItem item = (EnterpriseHomeItem)getItem(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.grid_item_layout, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.imageView = (ImageView)convertView.findViewById(R.id.iv_icon);
			holder.titleTextView = (TextView)convertView.findViewById(R.id.tv_name);
			holder.newMessageCountTextView = (TextView)convertView.findViewById(R.id.tv_new_message_count);
			convertView.setTag(holder);
		}
		ViewHolder holder = (ViewHolder)convertView.getTag();
		holder.imageView.setImageResource(item.getIconResourceId());
		holder.titleTextView.setText(item.getTitle());
		int newMessageCount = item.getNewMessageCount();
		if (newMessageCount > 0) {
			holder.newMessageCountTextView.setVisibility(View.VISIBLE);
			int displayCount = newMessageCount > 99 ? 99 : newMessageCount;
			holder.newMessageCountTextView.setText(String.valueOf(displayCount));
		} else {
			holder.newMessageCountTextView.setVisibility(View.GONE);
		}

		return convertView;
	}

	private static class ViewHolder {
		public ImageView imageView;
		public TextView titleTextView;
		public TextView newMessageCountTextView;
	}

}
