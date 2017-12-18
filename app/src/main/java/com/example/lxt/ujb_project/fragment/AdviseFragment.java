package com.example.lxt.ujb_project.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.example.lxt.ujb_project.activity.FirstAdviceActivity;
import com.example.lxt.ujb_project.activity.FourAdviceActivity;
import com.example.lxt.ujb_project.activity.SecondAdviceActivity;
import com.example.lxt.ujb_project.activity.ThirdAdviceActivity;
import com.example.lxt.ujb_project.tool.AppContext;
import com.example.lxt.ujb_project.view.DemoBase;
import com.example.lxt.ujb_project.view.MyImageView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.github.mikephil.charting.utils.YLabels;
import com.example.lxt.ujb_project.R;

/**
 * 分类建议的Fragment
 * 
 * @author 策划
 * 
 */
public class AdviseFragment extends DemoBase implements OnSeekBarChangeListener, OnChartValueSelectedListener, OnClickListener {

	Activity activity;
	private LineChart mChart;
	private AppContext appContext;
	private MyImageView tv_one;
	private MyImageView tv_two;
	private MyImageView tv_three;
	private MyImageView tv_four;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_advise, container, false);
		appContext = (AppContext) activity.getApplication();
		mChart = (LineChart) v.findViewById(R.id.chart);
		tv_one = (MyImageView) v.findViewById(R.id.tv_one);
		tv_two = (MyImageView) v.findViewById(R.id.tv_two);
		tv_three = (MyImageView) v.findViewById(R.id.tv_three);
		tv_four = (MyImageView) v.findViewById(R.id.tv_four);

		tv_one.setOnClickIntent(new MyImageView.OnViewClick() {

			@Override
			public void onClick() {

			}
		});
		tv_two.setOnClickIntent(new MyImageView.OnViewClick() {

			@Override
			public void onClick() {
			}
		});
		tv_three.setOnClickIntent(new MyImageView.OnViewClick() {

			@Override
			public void onClick() {
			}
		});
		tv_four.setOnClickIntent(new MyImageView.OnViewClick() {

			@Override
			public void onClick() {
			}
		});

		tv_one.setOnClickListener(this);
		tv_two.setOnClickListener(this);
		tv_three.setOnClickListener(this);
		tv_four.setOnClickListener(this);

		mChart.setDrawYValues(false);
		mChart.setDescription("");
		mChart.setDrawVerticalGrid(false);
		mChart.setDrawGridBackground(false);
		mChart.setUnit(" %");
		mChart.setDrawUnitsInChart(true);
		Typeface mTf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
		XLabels xl = mChart.getXLabels();
		xl.setCenterXLabelText(true);
		xl.setPosition(XLabelPosition.BOTTOM);
		xl.setTypeface(mTf);

		YLabels yl = mChart.getYLabels();
		yl.setTypeface(mTf);
		yl.setLabelCount(5);

		setData(33, 100);

		mChart.animateX(2500);

		return v;
	}

	private void setData(int count, float range) {

		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 1; i <= 31; i++) {
			xVals.add((i) + "");
		}

		ArrayList<Entry> yVals = new ArrayList<Entry>();

		for (int i = 0; i < appContext.getDatas().size(); i++) {
			yVals.add(new Entry(appContext.getDatas().get(i).getDensity(), i));

		}

		// create a dataset and give it a type
		LineDataSet set1 = new LineDataSet(yVals, "酒精浓度");
		set1.setColor(ColorTemplate.getHoloBlue());
		set1.setCircleColor(ColorTemplate.getHoloBlue());
		set1.setLineWidth(2f);
		set1.setCircleSize(4f);
		set1.setFillAlpha(65);
		set1.setFillColor(ColorTemplate.getHoloBlue());
		set1.setHighLightColor(Color.rgb(255, 0, 0));
		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		dataSets.add(set1); // add the datasets

		// create a data object with the datasets
		LineData data = new LineData(xVals, dataSets);

		// set data
		mChart.setData(data);
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onValueSelected(Entry e, int dataSetIndex) {

	}

	@Override
	public void onNothingSelected() {

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_one:
			startActivity(new Intent(activity, FirstAdviceActivity.class));

			break;
		case R.id.tv_two:
			startActivity(new Intent(activity, SecondAdviceActivity.class));
			break;
		case R.id.tv_three:
			startActivity(new Intent(activity, ThirdAdviceActivity.class));
			break;
		case R.id.tv_four:
			startActivity(new Intent(activity, FourAdviceActivity.class));
			break;

		default:
			break;
		}

	}

}
