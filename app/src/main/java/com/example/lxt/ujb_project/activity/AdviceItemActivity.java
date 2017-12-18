package com.example.lxt.ujb_project.activity;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.lxt.ujb_project.R;

public class AdviceItemActivity extends Activity implements OnClickListener {

	private ImageButton iv_back;
	private TextView tv_title;
	private TextView tv_content;
	
	private int ID;
	private String title;
	private String content;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_item_advice);
		findByView();
		Intent intent = getIntent();
		ID = intent.getIntExtra("id", 0);
		title = intent.getStringExtra("title");
		content = intent.getStringExtra("content");
		tv_title.setText(title);
		tv_content.setText("    "+content);
	}

	private void findByView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_content = (TextView) findViewById(R.id.tv_content);
		iv_back = (ImageButton) findViewById(R.id.iv_back);

		iv_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_back:
			AdviceItemActivity.this.finish();
			break;

		default:
			break;
		}
	}

}
