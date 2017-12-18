package com.example.lxt.ujb_project.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.lxt.ujb_project.R;


public class MenuAdapter extends BaseAdapter {

	private Context mContext;
	private int mChoose = 0;
	private int[] mIcon = { R.drawable.btn_hotspot, R.drawable.btn_subscribe,
			R.drawable.btn_follow, R.drawable.btn_read, R.drawable.btn_collect,
			R.drawable.btn_setup };

	public MenuAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return 6;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setChoose(int choose) {
		mChoose = choose;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.menu_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.iv_menu);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == mChoose) {
			holder.icon.setImageResource(mIcon[position]);
			convertView.setBackgroundColor(Color.parseColor("#10000000"));
		} else {
			holder.icon.setImageResource(mIcon[position]);
			convertView.setBackgroundColor(Color.parseColor("#00000000"));
		}
		return convertView;
	}

	class ViewHolder {
		LinearLayout ll_bk;
		ImageView icon;
	}
}
