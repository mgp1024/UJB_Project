package com.example.lxt.ujb_project.fragment;

import android.R.anim;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.lxt.ujb_project.R;
import com.example.lxt.ujb_project.activity.DaCheActivity;
import com.example.lxt.ujb_project.activity.DaiJiaActivity;
import com.example.lxt.ujb_project.tool.ClickDouble;
import com.example.lxt.ujb_project.view.RippleView;


/**
 * 打车/代驾Fragment
 * 
 * @author 策划
 * 
 */
public class HelpFragment extends Fragment implements OnClickListener {

	Activity activity;

	private RippleView iv_dache;
	private RippleView iv_daijia;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_help, container, false);
		iv_dache = (RippleView) v.findViewById(R.id.iv_dache);
		iv_daijia = (RippleView) v.findViewById(R.id.iv_daijia);

		iv_dache.setOnClickListener(this);
		iv_daijia.setOnClickListener(this);

		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;

		super.onAttach(activity);
	}

	@Override
	public void onClick(View v) {
		if (!ClickDouble.isFastDoubleClick()) {
			switch (v.getId()) {
			case R.id.iv_dache:
				new  Thread() {
					public void run() {
						try {
							sleep(300);

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						startActivity(new Intent(activity,DaCheActivity.class));
						activity.overridePendingTransition(anim.fade_in,anim.fade_out);
					};
				}.start();

				break;
			case R.id.iv_daijia:
				new Thread() {
					public void run() {
						try {
							sleep(300);

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						startActivity(new Intent(activity,
								DaiJiaActivity.class));
					};
				}.start();

				break;

			default:
				break;
			}
		}
	}
}
