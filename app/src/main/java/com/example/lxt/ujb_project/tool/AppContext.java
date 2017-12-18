package com.example.lxt.ujb_project.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.lxt.ujb_project.entity.Data;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;


public class AppContext extends Application {

	private int user_id;
	private String login_name = "";
	private String login_password = "";
	private String head_url = "";
	private String nickname = "";
	private int textSize = 2;
	private ArrayList<Data> datas = null;

	private SharedPreferences sysInitSharedPreferences;

	private static File cacheDir;

	private static AppContext mAppApplication;

	public static AppContext getApp() {
		return mAppApplication;
	}

	public static void initImageLoader(Context context) {

		cacheDir = StorageUtils.getOwnCacheDirectory(context, "Watch");

		Log.d("cacheDir", cacheDir.getPath());

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()

		.tasksProcessingOrder(QueueProcessingType.LIFO).discCacheFileCount(1024).discCache(new UnlimitedDiscCache(cacheDir)).defaultDisplayImageOptions(DisplayImageOptions.createSimple()).writeDebugLogs().build();

		ImageLoader.getInstance().init(config);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader(getApplicationContext());
		mAppApplication = this;
		initSetting();
	}

	private void initSetting() {
		sysInitSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		login_name = sysInitSharedPreferences.getString("login_name", "");
		login_password = sysInitSharedPreferences.getString("login_password", "");
		nickname = sysInitSharedPreferences.getString("nickname", "");
		head_url = sysInitSharedPreferences.getString("head_url", "");
		user_id = sysInitSharedPreferences.getInt("user_id", -1);
	}

	public SharedPreferences getPreference() {
		return sysInitSharedPreferences;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
		this.sysInitSharedPreferences.edit().putInt("user_id", user_id).commit();
	}

	public String getLogin_name() {
		return login_name;
	}

	public void setLogin_name(String login_name) {
		this.login_name = login_name;
		this.sysInitSharedPreferences.edit().putString("login_name", login_name).commit();
	}

	public String getLogin_password() {
		return login_password;

	}

	public void setLogin_password(String login_password) {
		this.login_password = login_password;
		this.sysInitSharedPreferences.edit().putString("login_password", login_password).commit();
	}

	public String getHead_url() {
		return head_url;
	}

	public void setHead_url(String head_url) {
		this.head_url = head_url;
		this.sysInitSharedPreferences.edit().putString("head_url", head_url).commit();
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
		this.sysInitSharedPreferences.edit().putString("nickname", nickname).commit();
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
		this.sysInitSharedPreferences.edit().putInt("textSize", textSize).commit();
	}

	public ArrayList<Data> getDatas() {
		Gson gson = new Gson();
		datas = gson.fromJson(sysInitSharedPreferences.getString("Status_", null), new TypeToken<List<Data>>() {
		}.getType());

		return datas;
	}

	public void setDatas(ArrayList<Data> datas) {
		this.datas = datas;
		SharedPreferences.Editor mEdit1 = sysInitSharedPreferences.edit();

		mEdit1.remove("Status_");
		Gson gson = new Gson();
		String jsonStr = gson.toJson(datas);
		mEdit1.putString("Status_", jsonStr);

		mEdit1.commit();

	}

	public boolean isLogin() {

		if ("".equals(getLogin_name())) {
			return false;
		} else {
			return true;
		}

	}

}
