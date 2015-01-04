package com.example.wochacha.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wochacha.R;
import com.example.wochacha.entity.ScanResult.ScanPath;
import com.example.wochacha.entity.ScanResult.ScanRecord;

public class ScanPathAdapter extends BaseAdapter {

	LayoutInflater inflater;

	List<ScanPath> paths = new ArrayList<ScanPath>();
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ScanPathAdapter(Activity activity) {
		inflater = activity.getLayoutInflater();
	}

	public void setListData(List<ScanPath> list) {
		this.paths.clear();
		this.paths.addAll(list);
	}

	@Override
	public int getCount() {

		return paths.size();
	}

	@Override
	public Object getItem(int position) {
		return paths.get(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.scan_tracking_item_layout,
					parent, false);
			ViewHolder holder = new ViewHolder();
			holder.topView = convertView.findViewById(R.id.v_top);
			holder.processIndicator = (ImageView) convertView
					.findViewById(R.id.iv_process_indicator);
			holder.text1 = (TextView) convertView.findViewById(R.id.tv_text1);			
			holder.timeTextView = (TextView) convertView
					.findViewById(R.id.tv_time);
			convertView.setTag(holder);
		}

		ScanPath path = paths.get(position);

		ViewHolder holder = (ViewHolder) convertView.getTag();

		int processIndicatorRes = position == 0 ? R.drawable.process_current
				: R.drawable.process_past;
		int topLineVisiblity = position == 0 ? View.INVISIBLE : View.VISIBLE;

		int processIndicatorSize = position == 0 ? R.dimen.process_current_indicator_size
				: R.dimen.process_past_indicator_size;
		Resources resources = parent.getResources();

		int topMargin = position == 0 ? R.dimen.process_current_text_margin_top
				: R.dimen.process_past_text_margin_top;

		int colorRes = position == 0 ? R.color.scan_current_color : R.color.scan_past_color;
		int color = resources.getColor(colorRes);
		
		holder.topView.setVisibility(topLineVisiblity);
		holder.processIndicator.setImageResource(processIndicatorRes);
		ViewGroup.LayoutParams params = holder.processIndicator
				.getLayoutParams();
		params.width = resources.getDimensionPixelSize(processIndicatorSize);
		params.height = params.width;
		//holder.processIndicator.setLayoutParams(params);
		holder.text1.setText(path.getPathInfo());
		holder.text1.setTextColor(color);
		RelativeLayout.LayoutParams paramsText = (RelativeLayout.LayoutParams) holder.text1
				.getLayoutParams();
		paramsText.setMargins(0, resources.getDimensionPixelSize(topMargin), 0,
				0);

		
		holder.timeTextView.setText(format.format(path.getLogDate()));
		holder.timeTextView.setTextColor(color);
		return convertView;
	}

	private final static class ViewHolder {
		View topView;
		ImageView processIndicator;
		TextView text1;		
		TextView timeTextView;
	}

}
