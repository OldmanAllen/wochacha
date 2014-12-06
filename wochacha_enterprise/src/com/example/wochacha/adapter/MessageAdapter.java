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
import com.example.wochacha.entity.Message;
import com.example.wochacha.network.ImageManager;
import com.example.wochacha.network.ImageViewInfo;
import com.example.wochacha.view.BadgeView;

public class MessageAdapter extends BaseAdapter {

	LayoutInflater inflater;

	List<Message> listData = new ArrayList<Message>();

	public MessageAdapter(Activity activity) {
		inflater = activity.getLayoutInflater();
	}

	public void setListData(List<Message> list) {
		this.listData.clear();
		for (Message message : list) {
			this.listData.add(message);
		}
	}

	public void appendListData(List<Message> list) {
		for (Message message : list) {
			this.listData.add(message);
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

		Message item = (Message) getItem(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.message_item_layout,
					parent, false);
			ViewHolder holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.iv_message_icon);
			holder.titleTextView = (TextView) convertView
					.findViewById(R.id.tv_message_title);
			holder.badgeView = new BadgeView(parent.getContext(),
					holder.imageView);
			holder.badgeView.setBadgeMargin(0);
			convertView.setTag(holder);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();

		int count = item.getNewMessageCount();
		if (count == 0) {
			holder.badgeView.hide();
		} else {
			holder.badgeView.setText(Integer.toString(count));
			holder.badgeView.show();
		}
		ImageViewInfo info = new ImageViewInfo(item.getIconUrl(), position);
		holder.imageView.setTag(info);
		ImageManager.getInstance().displayImage(item.getIconUrl(),
				(Activity) convertView.getContext(), holder.imageView);

		holder.titleTextView.setText(item.getCompanyName());

		return convertView;
	}

	private static class ViewHolder {
		public ImageView imageView;
		public TextView titleTextView;
		public BadgeView badgeView;
	}

}
