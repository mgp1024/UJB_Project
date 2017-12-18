package com.example.lxt.ujb_project.fragment;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.lxt.ujb_project.R;
import com.example.lxt.ujb_project.activity.InsurItemActivity;
import com.example.lxt.ujb_project.adapter.NewsAdapter;
import com.example.lxt.ujb_project.entity.Insur;
import com.example.lxt.ujb_project.libcore.io.DiskLruCache;
import com.example.lxt.ujb_project.tool.AsyncHttpHelper;
import com.example.lxt.ujb_project.tool.NetUtils;
import com.example.lxt.ujb_project.tool.URLCollect;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;


/**
 * 保险推广页
 * 
 * @author Joney
 * 
 */
public class AIFragment extends Fragment {

	Activity activity;
	ArrayList<Insur> arrayList = new ArrayList<Insur>();
	ArrayList<Insur> insurs;
	PullToRefreshListView mListView;
	NewsAdapter mAdapter;
	ImageView detail_loading;
	DiskLruCache mDiskLruCache = null;
	static int page = 1;
	static int TAG = 6;
	public final static int Net_NO = 0;
	public final static int Net_OK = 1;
	public final static int UPDATE = -1;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	String tag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			mDiskLruCache.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// 设定点击事件
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				int size = arrayList.size();

				if (size != 0) {
					Intent intent = new Intent(activity, InsurItemActivity.class);

					intent.putExtra("id", arrayList.get(position - 1).getInsur_id());
					intent.putExtra("title", arrayList.get(position - 1).getInsur_name());
					intent.putExtra("desc", arrayList.get(position - 1).getInsur_desc());
					intent.putExtra("content", arrayList.get(position - 1).getInsur_content());
					intent.putExtra("tel", arrayList.get(position - 1).getInsur_tel());
					startActivity(intent);
				}

			}
		});

	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Net_OK:
				detail_loading.setVisibility(View.GONE);
				insurs = new ArrayList<Insur>();
				mAdapter = new NewsAdapter(activity, 0, insurs, mListView);
				initPullToRefreshNewsListView(mListView, mAdapter);
				getSimulationNews();
				break;
			case Net_NO:
				detail_loading.setVisibility(View.GONE);
				insurs = new ArrayList<Insur>();
				mAdapter = new NewsAdapter(activity, 0, insurs, mListView);
				initPullToRefreshNewsListView(mListView, mAdapter);
				getLocalNews();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_ai, null);
		mListView = (PullToRefreshListView) view.findViewById(R.id.myListView);
		detail_loading = (ImageView) view.findViewById(R.id.detail_loading);

		super.onCreate(savedInstanceState);
		try {
			File path = getDiskCacheDir(activity, "File");
			if (!path.exists()) {
				path.mkdirs();
			}
			mDiskLruCache = DiskLruCache.open(path, getAppVersion(getActivity()), 1, 10 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
		NetWork();
		return view;
	}

	private void NetWork() {
		if (NetUtils.isConnected(activity) == true) {
			// 当有网络时去刷新 并加载
			Message message = new Message();
			message.what = Net_OK;
			handler.sendMessage(message);
		} else {
			// 当没网络时直接读取本地的文件
			Message message = new Message();
			message.what = Net_NO;
			handler.sendMessage(message);
		}

	}

	public void getLocalNews() {
		String url = URLCollect.BASE_URL + URLCollect.INSUR_URL + "page="+page+"&pagesize=10";
		ArrayList<Insur> ret = new ArrayList<Insur>();
		String data = null;
		try {
			String key = hashKeyForDisk(url);
			DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
			if (snapShot != null) {
				data = snapShot.getString(0);

			} else {

				data= "[]";

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Gson gson = new Gson();
		ArrayList<Insur> mList = gson.fromJson(data, new TypeToken<List<Insur>>() {
		}.getType());
		for (Insur news : mList) {
			ret.add(news);
		}
		mAdapter.addNews(ret);
		mAdapter.notifyDataSetChanged();
	}



	public void getSimulationNews() {
		final String url = URLCollect.BASE_URL + URLCollect.INSUR_URL + "page="+page+"&pagesize=10";
		AsyncHttpHelper.getAbsoluteUrl(url, new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, String data) {
				Gson gson = new Gson();
				ArrayList<Insur> mList = gson.fromJson(data, new TypeToken<List<Insur>>() {
				}.getType());
				for (Insur news : mList) {
					arrayList.add(news);
				}
				mAdapter.addNews(mList);
				mAdapter.notifyDataSetChanged();
				WriteToLocal(url, data);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String data, Throwable arg3) {

			}
		});
	}

	/**
	 * 将文本写入 本地
	 */
	private void WriteToLocal(String url, final String data) {
		final String key = hashKeyForDisk(url);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						editor.set(0, data);
						editor.commit();
					}
					mDiskLruCache.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void initPullToRefreshNewsListView(PullToRefreshListView ptrlv, NewsAdapter adapter) {
		ptrlv.setMode(Mode.BOTH);
		ptrlv.setOnRefreshListener(new LvOnRefreshListener(ptrlv));
		ptrlv.setAdapter(adapter);
	}

	class LvOnRefreshListener implements OnRefreshListener2<ListView> {

		private PullToRefreshListView mPtflv;

		public LvOnRefreshListener(PullToRefreshListView ptflv) {
			this.mPtflv = ptflv;
		}

		// 下拉刷新
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

			String label = DateUtils.formatDateTime(activity.getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
			new GetDownTask(mPtflv).execute();
		}

		// 上拉加载启动异步
		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			// 新闻加载判断是否还有未显示数据
			new GetUpTask(mPtflv).execute();

		}

	}

	class GetDownTask extends AsyncTask<String, Void, Integer> {
		private PullToRefreshListView mPtrlv;

		public GetDownTask() {
		}

		public GetDownTask(PullToRefreshListView ptrlv) {
			this.mPtrlv = ptrlv;

		}

		@Override
		protected Integer doInBackground(String... params) {

			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			page = 1;
			arrayList.clear();
			NetWork();
			mAdapter.notifyDataSetChanged();
			mPtrlv.onRefreshComplete();
		}

	}

	class GetUpTask extends AsyncTask<String, Void, Integer> {
		private PullToRefreshListView mPtrlv;
		public String content = null;

		public GetUpTask() {
		}

		public GetUpTask(PullToRefreshListView ptrlv) {
			this.mPtrlv = ptrlv;

		}

		@Override
		protected Integer doInBackground(String... params) {

			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			page = page + 1;
			if (NetUtils.isConnected(activity) == true) {
				getSimulationNews();
			} else {
				getLocalNews();
			}

			mAdapter.notifyDataSetChanged();
			mPtrlv.onRefreshComplete();
		}

	}

	/**
	 * 取缓存地址
	 * 
	 * @param context
	 *            获取到的系统路径
	 * @param uniqueName
	 *            对不同类型的数据进行区分而设定的一个唯一值
	 * @return
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		// 将系统的路径和唯一值拼接
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取当前系统的版本号
	 * 
	 * @param context
	 * @return
	 */
	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 用MD5去加密URL作为文件名
	 * 
	 * @param key
	 * @return
	 */
	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}
