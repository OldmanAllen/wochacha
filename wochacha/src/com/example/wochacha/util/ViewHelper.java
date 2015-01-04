package com.example.wochacha.util;

import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public final class ViewHelper {

	public static int calcFragmentHeight(ListView listView, View container) {
		// ��ȡListView��Ӧ��Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return 0;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			// listAdapter.getCount()�������������Ŀ
			View listItem = listAdapter.getView(i, null, listView);
			// ��������View �Ŀ��
			listItem.measure(0, 0);
			// ͳ������������ܸ߶�
			totalHeight += listItem.getMeasuredHeight();
		}

		totalHeight = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		int containerHeight = 0;
		if (container != null){
			container.measure(0, 0);
			containerHeight = container.getMeasuredHeight();
		}
		return totalHeight + containerHeight;
	}
}
