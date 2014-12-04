package com.example.wochacha.ui;

import com.example.wochacha.R;

import android.app.ActionBar;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public abstract class FragmentBase extends Fragment {

	TextView tv_title;

	@Override
	public void onResume() {
		super.onResume();
		if (visible) {
			setupActionBar();
		}
	}

	protected boolean visible = false;

	public void setVisibility(boolean visible) {
		this.visible = visible;

	}

	protected abstract String getTitle();

	protected void setupActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		LayoutInflater inflater = getActivity().getLayoutInflater();

		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);

		View actionBarView = inflater.inflate(R.layout.title_bar, null);
		tv_title = (TextView)actionBarView.findViewById(R.id.tv_title);
		tv_title.setText(getTitle());
		actionBar.setCustomView(actionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER));
	}

}
