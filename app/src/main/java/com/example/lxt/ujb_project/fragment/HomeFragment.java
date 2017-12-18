package com.example.lxt.ujb_project.fragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lxt.ujb_project.R;
import com.example.lxt.ujb_project.bluetooth.BluetoothCommService;
import com.example.lxt.ujb_project.entity.Data;
import com.example.lxt.ujb_project.tool.AppContext;
import com.example.lxt.ujb_project.tool.CHexConver;
import com.example.lxt.ujb_project.view.RoundProgressBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * 首页的Fragment
 * 
 * @author 策划
 * 
 */
public class HomeFragment extends Fragment implements OnClickListener {

	// 请求开启蓝牙的requestCode
	static final int REQUEST_ENABLE_BT = 1;

	//
	static final int UPDATE_UI = 0;


	// 获取一个本地蓝牙适配器 从中得到蓝牙对象
	private BluetoothAdapter bluetoothAdapter;

	//
	private BluetoothDevice device;

	// 指定要连接到的蓝牙模块 地址
	private static String NAME = "HC-05";

	// 创建一个监听器的实例
	private BluetoothReceiver receiver;

	//
	public boolean bluetoothFlag = true;

	// /////////////////////////////////////////////

	// bluetoothCommService 传来的消息状态
	public static final int MESSAGE_STATE_CHANGE = 1;

	public static final int MESSAGE_READ = 2;

	public static final int MESSAGE_WRITE = 3;

	public static final int MESSAGE_DEVICE_NAME = 4;

	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";

	public static final String TOAST = "toast";

	// 创建一个蓝牙串口服务对象
	private BluetoothCommService mCommService = null;

	private RoundProgressBar ProgressBar;
	private String msCmdEndFlg = "\r\n";
	private StringBuilder localStringBuilder = new StringBuilder();
	private TextView tv_tip;
	private AppContext appContext;
	Activity activity;

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

		View v = inflater.inflate(R.layout.fragment_home, container, false);

		ProgressBar = (RoundProgressBar) v.findViewById(R.id.ProgressBar);
		tv_tip = (TextView) v.findViewById(R.id.tv_tip);

		ProgressBar.setOnClickListener(this);
		OpenBT();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		appContext = (AppContext) activity.getApplication();
		receiver = new BluetoothReceiver();

		getActivity().registerReceiver(receiver, filter);
		return v;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onResume() {
		super.onResume();
		String str = "[{'density':73,'date':0},{'density':82,'date':1},{'density':77,'date':2},{'density':180,'date':3},{'density':77,'date':4},{'density':0,'date':5},{'density':0,'date':6},{'density':0,'date':7},{'density':0,'date':8},{'density':0,'date':9},{'density':89,'date':10},{'density':72,'date':11},{'density':0,'date':12},{'density':0,'date':13},{'density':0,'date':14},{'density':85,'date':15},{'density':90,'date':16},{'density':88,'date':17}]";
		Gson gson = new Gson();
		ArrayList<Data> datas = gson.fromJson(str, new TypeToken<List<Data>>() {
		}.getType());

		appContext.setDatas(datas);
		if (mCommService != null) {

			if (mCommService.getState() == BluetoothCommService.STATE_NONE) {

				mCommService.start();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 当程序退出时注销掉广播监听，不然会有线程 卡死的情况
		this.getActivity().unregisterReceiver(receiver);

		if (mCommService != null)
			mCommService.stop();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {

				Toast.makeText(getActivity(), "成功打开蓝牙", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), "不能打开蓝牙,程序即将关闭", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 开启蓝牙
	 */
	@SuppressLint("MissingPermission")
	private void OpenBT() {

		// 获取到实例
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (!bluetoothAdapter.isEnabled()) {

			// 请求打开蓝牙设备
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		bluetoothAdapter.startDiscovery();

	}

	/**
	 * 创建一个广播用来监听 是否发现设备
	 * 
	 * @author Joney
	 * 
	 */
	private class BluetoothReceiver extends BroadcastReceiver {

		@SuppressLint("MissingPermission")
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				if (isLock(device)) {
					bluetoothAdapter.cancelDiscovery();
					if (Matches(device)) {
						new Thread() {

							@Override
							public void run() {
								Message msg = new Message();
								msg.what = UPDATE_UI;
								myHandler.sendMessage(msg);
							}
						}.start();
					}
				}

			}

		}

	}

	/**
	 * 是否锁定目标蓝牙
	 * 
	 * @param device
	 * @return
	 */
	private boolean isLock(BluetoothDevice device) {

		@SuppressLint("MissingPermission") boolean isLockName = (device.getName()).equals(NAME);

		return isLockName;
	}

	/**
	 * 蓝牙的配对操作
	 */
	@SuppressLint("MissingPermission")
	private boolean Matches(BluetoothDevice device) {
		try {
			if (device.getBondState() == BluetoothDevice.BOND_NONE) {

				Method creMethod = BluetoothDevice.class.getMethod("createBond");

				creMethod.invoke(device);
				bluetoothFlag = true;
			}
			// 否则 直接尝试连接 到蓝牙模块
			if (mCommService == null) {

				// 去连接设备 用myHandler 来接受返回的结果
				mCommService = new BluetoothCommService(getActivity(), myHandler);

				// 客户端只负责去连接到指定的设备
				mCommService.connect(device);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return bluetoothFlag;

	}

	/**
	 * 创建handle 用来 和UI做交互
	 */
	private final Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_UI:
				break;
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {

				case BluetoothCommService.STATE_CONNECTED:
					Toast.makeText(getActivity(), "连接成功", Toast.LENGTH_LONG).show();
					break;
				case BluetoothCommService.STATE_CONNECTING:
					Toast.makeText(getActivity(), "连接中...", Toast.LENGTH_LONG).show();
					break;
				case BluetoothCommService.STATE_LISTEN:

					break;
				case BluetoothCommService.STATE_NONE:

					Toast.makeText(getActivity(), "未连接，请等待连接。。", Toast.LENGTH_LONG).show();
					mCommService.start();
					break;
				}
				break;
			case MESSAGE_WRITE:

				break;
			case MESSAGE_READ:

				byte[] readBuf = (byte[]) msg.obj;
				String readMessage = new String(readBuf, 0, msg.arg1);
				String str = String.format(readMessage, new Object[] { "\\r\\n" });

				inToListView(str);

				break;

			case MESSAGE_DEVICE_NAME:

				break;
			case MESSAGE_TOAST:

				break;

			default:
				break;
			}

		}

	};

	private void inToListView(String str) {

		if (str.length() > 0) {
			localStringBuilder.append(str);
			if ((localStringBuilder.length() > 256) || ((localStringBuilder.length() > msCmdEndFlg.length()) && (localStringBuilder.substring(localStringBuilder.length() - msCmdEndFlg.length()).equals(msCmdEndFlg)))) {
				if (localStringBuilder.substring(localStringBuilder.length() - msCmdEndFlg.length()).equals(msCmdEndFlg))
					str = localStringBuilder.substring(0, localStringBuilder.length() - msCmdEndFlg.length());

				WriteToProgress(str);

				localStringBuilder = new StringBuilder();

			}
		} else {
			try {
				Thread.sleep(10L);
			} catch (InterruptedException localInterruptedException) {
			}
		}

	};

	private void WriteToProgress(String str) {

		if (str.equals("error")) {
			Toast.makeText(getActivity(), "" + "请重新点击！", Toast.LENGTH_SHORT).show();
		} else if (str.equals("right")) {
			Toast.makeText(getActivity(), "" + "请对着仪器用力呼气，谢谢合作！", Toast.LENGTH_SHORT).show();
		} else if (str.equals("complete")) {
			Toast.makeText(getActivity(), "" + "预热完毕！", Toast.LENGTH_SHORT).show();
		} else if (str.equals("heating")) {
			Toast.makeText(getActivity(), "" + "系统正在预热中，请稍候！", Toast.LENGTH_SHORT).show();
		} else {
			str = str.trim();
			char[] b = str.toCharArray();
			String result = "";
			for (int i = 0; i < b.length; i++) {
				if (("0123456789.").indexOf(b[i] + "") != -1) {
					result += b[i];
				}
			}
			if (!"".equals(result) && result != null) {
				Double d = Double.valueOf(result.trim());
				int i = Double.valueOf(d*2200).intValue();
				if (i < 200) {
					tv_tip.setText("您的酒精浓度低于 80% 您可以驾驶，但请注意安全");
				} else {
					tv_tip.setText("您的酒精浓度超标，请勿驾车！");
				}
				ProgressBar.setProgress(i);
				ProgressBar.setTextIsDisplayable(true);
				

			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ProgressBar:
//
//			if (progress != 0) {
//				progress = 0;
//				ProgressBar.setTextIsDisplayable(true);
//				Start();
//
//			} else {
				Start();
//			}

			break;
		default:
			break;
		}
	}

	public void Start() {
		if (mCommService != null) {
			Toast.makeText(getActivity(), "命令已发送请等待。。", Toast.LENGTH_SHORT).show();
			String max = "start measure\r\n";
			byte[] arrayOfByte1 = CHexConver.hexStr2Bytes(CHexConver.str2HexStr(max));
			mCommService.write(arrayOfByte1);

		}

	};

}
