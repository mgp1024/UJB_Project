package com.example.lxt.ujb_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lxt.ujb_project.R;


public class InsurItemActivity extends Activity implements OnClickListener{
	
	private ImageButton iv_back; //回退
	private TextView tv_title;   //标题
	private TextView tv_desc;    //描述
	private TextView tv_content; //内容
	private ImageView iv_call;
	private int ID;
	private String tel;
	private String title;
	private String desc;
	private String content;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_insur);
		
		Intent intent = getIntent();
		
		ID = intent.getIntExtra("id", 0);
		title = intent.getStringExtra("title");
		content = intent.getStringExtra("content");
		desc =  intent.getStringExtra("desc");
		tel =   intent.getStringExtra("tel");
		findByView();
		
		
	}

	private void findByView() {
		
		iv_back = (ImageButton)findViewById(R.id.iv_back);
		tv_title  =(TextView)findViewById(R.id.tv_title);
		tv_content = (TextView)findViewById(R.id.tv_content);
		tv_desc = (TextView)findViewById(R.id.tv_desc);
		iv_call = (ImageView)findViewById(R.id.iv_call);
		
		iv_back.setOnClickListener(this);
		
		tv_title.setText(title);
		tv_desc.setText(desc);
		iv_call.setOnClickListener(this);
		tv_content.setText("    "+content);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			InsurItemActivity.this.finish();
			break;

		default:
		case R.id.iv_call:
			Intent intent = new Intent();

			//系统默认的action，用来打开默认的电话界面
			intent.setAction(Intent.ACTION_CALL);

			//需要拨打的号码

			intent.setData(Uri.parse("tel:"+tel));
			InsurItemActivity.this.startActivity(intent);
			break;
		}
	}

}
