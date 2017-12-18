package com.example.lxt.ujb_project.activity;

import java.io.File;
import java.io.IOException;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lxt.ujb_project.R;
import com.example.lxt.ujb_project.libcore.io.DiskLruCache;
import com.example.lxt.ujb_project.tool.AppContext;


public class SystemSettingActivity extends Activity implements OnClickListener {
	private AppContext appContext;
	private Button btn_back;
	private ImageView iv_txtsize; // 字体设置
	private ImageView iv_line; // 离线设置
	private CheckBox cb_push; // 推送
	private CheckBox cb_wifi; // WIFI下加载图片
	private ImageView iv_clear; // 缓存清理
	private CheckBox cb_day; // 夜间模式
	private ImageView iv_ret; // 意见反馈
	private ImageView iv_about;
	private TextView tv_ram; // 缓存数量
	private Button btn_exit;

	private DiskLruCache TDiskLruCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sys_setting);
		appContext = (AppContext) this.getApplication();

		InitiCache();
		findById();
		tv_ram.setText(TDiskLruCache.size() + "");

	}

	private void findById() {
		btn_back = (Button) findViewById(R.id.btn_back);
		tv_ram = (TextView) findViewById(R.id.tv_ram);
		iv_clear = (ImageView) findViewById(R.id.iv_clear);
		iv_ret = (ImageView) findViewById(R.id.iv_ret);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		iv_about = (ImageView) findViewById(R.id.iv_about);
		iv_txtsize = (ImageView) findViewById(R.id.iv_txtsize);

		iv_txtsize.setOnClickListener(this);
		iv_about.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		iv_clear.setOnClickListener(this);
		iv_ret.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
	}

	private void InitiCache() {
		try {
			// 获取图片缓存路径
			File Bitcache = getDiskCacheDir(SystemSettingActivity.this, "thumb");
			if (!Bitcache.exists()) {
				Bitcache.mkdirs();
			}

			// 创建DiskLruCache实例，初始化缓存数据
			TDiskLruCache = DiskLruCache.open(Bitcache, getAppVersion(SystemSettingActivity.this), 1, 10 * 1024 * 1024);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据传入的uniqueName获取硬盘缓存的路径地址。
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取当前应用程序的版本号。
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
	 * 清除所有的缓存
	 */
	private void DeleCache() {
		try {
			TDiskLruCache.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_back:
			SystemSettingActivity.this.finish();
			setResult(200);
			break;
		case R.id.iv_clear:
			DeleCache();
			tv_ram.setText(" 0 ");
			break;
		case R.id.iv_ret:
			startActivity(new Intent(SystemSettingActivity.this, RetActivity.class));
			break;
		case R.id.btn_exit:
			appContext.setLogin_name("");
			appContext.setNickname("");
			appContext.setHead_url("");
			SystemSettingActivity.this.finish();
			break;
		case R.id.iv_about:
			startActivity(new Intent(SystemSettingActivity.this, AboutActivity.class));
			break;
		case R.id.iv_txtsize:
			showDialog();

			break;

		default:
			break;
		}
	}

	private void showDialog() {

		final String[] arrayFruit = new String[] { "大号", "中号", "小号" };
final int[] array = new int[]{1,2,3};
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("正文字体大小？").setIcon(R.drawable.ic_launcher).setItems(arrayFruit, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (array[which]) {
				case 1:
					appContext.setTextSize(3);
					break;
				case 2:
					appContext.setTextSize(2);
					break;
				case 3:
					appContext.setTextSize(1);
					break;

				default:
					break;
				}
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).create();
		alertDialog.show();
	}
}
