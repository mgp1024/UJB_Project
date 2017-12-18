package com.example.lxt.ujb_project.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lxt.ujb_project.R;

public class RetActivity extends Activity implements OnClickListener {

	private Button btn_back;
	private EditText et_email;
	private EditText ed_content;
	private Button btn_submit;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ret);
		findByView();
	}

	private void findByView() {
		btn_back = (Button) findViewById(R.id.btn_back);
		et_email =(EditText)findViewById(R.id.et_email);
		ed_content = (EditText)findViewById(R.id.ed_content);
		btn_submit = (Button)findViewById(R.id.btn_submit);
		

		btn_back.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_back:

			RetActivity.this.finish();
			break;
		case R.id.btn_submit:
			if("".equals(et_email.getText().toString())||"".equals(ed_content.getText().toString())){
				Toast.makeText(getApplication(), "����д�������ı������", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getApplication(), "��л��ı���������ǻ�ͨ������������ϵ��", Toast.LENGTH_SHORT).show();
				ed_content.setText("");
				et_email.setText("");
				
			}
			break;
		default:
			break;
		}
	}
}
