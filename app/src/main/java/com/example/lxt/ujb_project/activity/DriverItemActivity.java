package com.example.lxt.ujb_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lxt.ujb_project.R;
import com.example.lxt.ujb_project.tool.Util;


/**
 * 打的 、 代驾 详情类
 * 
 * @author Joney
 * 
 */

public class DriverItemActivity extends Activity implements OnClickListener{

	private ImageView iv_head;
	private TextView tv_content;
	private ImageView iv_call;
	private ImageButton iv_back;
	
	private int ID;
	private String tel;
	private String url;
	private String desc;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver);
		// 强制抑制线程警报
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		Intent intent = getIntent();
		ID = intent.getIntExtra("id", 0);
		url = intent.getStringExtra("url");
		desc = intent.getStringExtra("desc");
		tel = intent.getStringExtra("tel");
		
		findById();
		
		
	}

	private void findById() {
		iv_head = (ImageView)findViewById(R.id.iv_head);
		tv_content = (TextView)findViewById(R.id.tv_content);
		iv_call = (ImageView)findViewById(R.id.iv_call);
		iv_back = (ImageButton)findViewById(R.id.iv_back);
		
		iv_back.setOnClickListener(this);
		iv_call.setOnClickListener(this);
		tv_content.setText(desc);
		iv_head.setImageBitmap(Util.getbitmap(url));
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_call:
			Intent intent = new Intent();

			//系统默认的action，用来打开默认的电话界面
			intent.setAction(Intent.ACTION_CALL);

			//需要拨打的号码

			intent.setData(Uri.parse("tel:"+tel));
			DriverItemActivity.this.startActivity(intent);
			break;
		case R.id.iv_back:
			DriverItemActivity.this.finish();
			break;
			
		default:
			break;
		}
		
	}

}
