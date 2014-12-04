package com.example.wochacha.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.wochacha.R;
import com.example.wochacha.adapter.MessageAdapter;
import com.example.wochacha.entity.Message;
import com.example.wochacha.exception.BaseException;
import com.example.wochacha.network.CacheService;
import com.example.wochacha.service.DataServiceImpl;
import com.example.wochacha.service.MessageService;
import com.example.wochacha.service.DataServiceImpl.DataServiceDelegate;
import com.example.wochacha.util.ToastMessageHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MessageFragment extends FragmentBase implements
		DataServiceDelegate {

	private PullToRefreshListView listView;
	private List<Message> listData = new ArrayList<>();
	private boolean isServiceCalled = false;
	private MessageAdapter adapter;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static MessageFragment newInstance(int sectionNumber) {
		MessageFragment fragment = new MessageFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public MessageFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_message, container,
				false);
		setupMessageList(rootView);
		return rootView;
	}

	private void setupMessageList(View rootView) {
		listView = (PullToRefreshListView) rootView
				.findViewById(R.id.pull_refresh_list);
		listView.setMode(Mode.PULL_FROM_START);
		adapter = new MessageAdapter(getActivity());
		listView.setAdapter(adapter);

		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				loadMessageList();
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Message item = (Message) adapter.getItem(position - 1);
				if (item.getNewMessageCount() > 0) {
					item.read();

					String dataString = CacheService.getCacheServiceInstance()
							.getObjectFromCache("/message/1", true);
					JSONObject object;
					try {
						object = new JSONObject(dataString);
						JSONArray array = object.getJSONArray("array");

						if (item.getCompanyId() == 1) {
							array.getJSONObject(0).put("message_count", 0);
						} else {
							array.getJSONObject(1).put("message_count", 0);
						}
						CacheService.getCacheServiceInstance().refreshCache(
								"/message/1", object);
						
						adapter.notifyDataSetChanged();

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				Intent intent = new Intent(MessageFragment.this.getActivity(),
						TodoActivity.class);
				intent.putExtra(TodoActivity.IntentKey.TITLE,
						item.getCompanyName());
				intent.putExtra(TodoActivity.IntentKey.RESOURCE_ID,
						R.layout.message_detail_layout);
				startActivity(intent);

				
			}

		});

	}

	@Override
	public void setVisibility(boolean visible) {
		super.setVisibility(visible);
		if (visible && !isServiceCalled) {
			listView.setRefreshing(true);
		}
	}

	private void loadMessageList() {
		MessageService service = new MessageService(1);
		service.setDelegate(this);
		service.start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_scan).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public interface HomeFragmentCallback {
		public void onDrawerToogleClicked();
	}

	@Override
	protected String getTitle() {
		return getString(R.string.tab_message);
	}

	@Override
	public void onRequestSucceeded(DataServiceImpl service,
			final JSONObject data, final boolean isCached) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				listData.clear();
				isServiceCalled = true;
				if (!isCached) {
					listView.onRefreshComplete();
				}
				if (data != null) {
					try {
						JSONArray array = data.getJSONArray("array");
						for (int i = 0; i < array.length(); i++) {
							JSONObject object = array.getJSONObject(i);
							Message message = new Message();
							message.populate(object);
							listData.add(message);
							adapter.setListData(listData);
							adapter.notifyDataSetChanged();
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

			}
		});

	}

	@Override
	public void onRequestFailed(DataServiceImpl service, BaseException exception) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ToastMessageHelper.showErrorMessage(
						MessageFragment.this.getActivity(),
						R.string.load_failed, false);
				listView.onRefreshComplete();
			}
		});
	}
}