package com.example.lxt.ujb_project.update;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.lxt.ujb_project.R;
import com.example.lxt.ujb_project.activity.MainActivity;


//�����ڣ��������档
public class WelcomActivity extends Activity {
	private boolean first;	//�ж��Ƿ��һ�δ����
	private View view;
	private Context context;
	private Animation animation;
	private NetManager netManager;
	private SharedPreferences shared;
	private static int TIME = 1000; 										// ������������ӳ�ʱ��

	
	//////
	private final int UPDATA_NONEED = 0;
	private final int UPDATA_CLIENT = 1;
	private final int GET_UNDATAINFO_ERROR = 2;
	private final int SDCARD_NOMOUNTED = 3;
	private final int DOWN_ERROR = 4;

	private UpdataInfo info;
	private int localVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = View.inflate(this, R.layout.wellcom, null);
		setContentView(view);
		context = this;							//�õ�������
		shared = new SharedConfig(context).GetConfig(); 	// �õ������ļ�
		netManager = new NetManager(context); 				// �õ����������
		
		try {
			localVersion = getVersionName();
			CheckVersionTask cv = new CheckVersionTask();
			new Thread(cv).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}
	// ����������ķ���
	private void into() {
		if (netManager.isOpenNetwork()) {
			// �������������ж��Ƿ��һ�ν��룬����ǵ�һ������뻶ӭ����
			first = shared.getBoolean("First", true);

			// ���ö���Ч����alpha����animĿ¼�µ�alpha.xml�ļ��ж��嶯��Ч��
			animation = AnimationUtils.loadAnimation(this, R.drawable.alpha);
			// ��view���ö���Ч��
			view.startAnimation(animation);
			animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
				}

				// ����������������Ķ������ڶ���������ʱ����һ���̣߳�����߳��а�һ��Handler,��
				// �����Handler�е���goHome��������ͨ��postDelayed����ʹ��������ӳ�500����ִ�У��ﵽ
				// �ﵽ������ʾ��һ��500�����Ч��
				@Override
				public void onAnimationEnd(Animation arg0) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							Intent intent;
							//�����һ�Σ����������ҳWelcomeActivity
							if (first) {
								intent = new Intent(WelcomActivity.this,
										GuideActivity.class);
								
							} else {
								intent = new Intent(WelcomActivity.this,
										MainActivity.class);
							}
							startActivity(intent);
							// ����Activity���л�Ч��
							overridePendingTransition(R.drawable.in_from_right,
									R.drawable.out_to_left);
							WelcomActivity.this.finish();
						}
					}, TIME);
				}
			});
		} else {
			// ������粻���ã��򵯳��Ի��򣬶������������
			Builder builder = new Builder(context);
			builder.setTitle("û�п��õ�����");
			builder.setMessage("�Ƿ�������������?");
			builder.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = null;
							try {
								String sdkVersion = android.os.Build.VERSION.SDK;
								if (Integer.valueOf(sdkVersion) > 10) {
									intent = new Intent(
											android.provider.Settings.ACTION_WIRELESS_SETTINGS);
								} else {
									intent = new Intent();
									ComponentName comp = new ComponentName(
											"com.android.settings",
											"com.android.settings.WirelessSettings");
									intent.setComponent(comp);
									intent.setAction("android.intent.action.VIEW");
								}
								WelcomActivity.this.startActivity(intent);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			builder.setNegativeButton("ȡ��",
					new DialogInterface.OnClickListener() {
						@SuppressLint("ResourceType")
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// �������������ж��Ƿ��һ�ν��룬����ǵ�һ������뻶ӭ����
							first = shared.getBoolean("First", true);

							// ���ö���Ч����alpha����animĿ¼�µ�alpha.xml�ļ��ж��嶯��Ч��
							animation = AnimationUtils.loadAnimation(WelcomActivity.this, R.drawable.alpha);
							// ��view���ö���Ч��
							view.startAnimation(animation);
							animation.setAnimationListener(new AnimationListener() {
								@Override
								public void onAnimationStart(Animation arg0) {
								}

								@Override
								public void onAnimationRepeat(Animation arg0) {
								}

								// ����������������Ķ������ڶ���������ʱ����һ���̣߳�����߳��а�һ��Handler,��
								// �����Handler�е���goHome��������ͨ��postDelayed����ʹ��������ӳ�500����ִ�У��ﵽ
								// �ﵽ������ʾ��һ��500�����Ч��
								@Override
								public void onAnimationEnd(Animation arg0) {
									new Handler().postDelayed(new Runnable() {
										@Override
										public void run() {
											Intent intent;
											//�����һ�Σ����������ҳWelcomeActivity
											if (first) {
												intent = new Intent(WelcomActivity.this,
														GuideActivity.class);
												
											} else {
												intent = new Intent(WelcomActivity.this,
														MainActivity.class);
											}
											startActivity(intent);
											// ����Activity���л�Ч��
											overridePendingTransition(R.drawable.in_from_right,
													R.drawable.out_to_left);
											WelcomActivity.this.finish();
										}
									}, TIME);
								}
							});
						}
					});
			builder.show();
		}
	}
	
	
	//////////////
	/*
	 * ��ȡ��ǰ����İ汾��
	 */
	private int getVersionName() throws Exception {
		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = getPackageManager();
		// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		return packInfo.versionCode;
	}

	/*
	 * �ӷ�������ȡxml���������бȶ԰汾��
	 */
	public class CheckVersionTask implements Runnable {

		public void run() {
			try {
				// ����Դ�ļ���ȡ������ ��ַ
				String path = getResources().getString(R.string.url_server);
				// ��װ��url�Ķ���
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5000);
				InputStream is = conn.getInputStream();
				String a = "" + localVersion + "";
				info = UpdataInfoParser.getUpdataInfo(is);

				if (info.getVersion().equals(a)) {
					Message msg = new Message();
					msg.what = UPDATA_NONEED;
					handler.sendMessage(msg);
				} else {

					Message msg = new Message();
					msg.what = UPDATA_CLIENT;
					handler.sendMessage(msg);
				}

			} catch (Exception e) {
				Message msg = new Message();
				msg.what = GET_UNDATAINFO_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			}

		}

	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_NONEED:
				into() ;
				break;

			case UPDATA_CLIENT:
				showUpdataDialog();
				break;
			case GET_UNDATAINFO_ERROR:
				into() ;
				break;
			case SDCARD_NOMOUNTED:
				Toast.makeText(getApplicationContext(), "SD��������", 1).show();
				break;
			case DOWN_ERROR:
				Toast.makeText(getApplicationContext(), "�����°汾ʧ��", 1).show();
				break;
			}
		}
	};

	protected void showUpdataDialog() {
		Builder builer = new Builder(this);
		builer.setTitle(R.string.info);
		builer.setIcon(R.drawable.updateicon);
		builer.setMessage(info.getDescription());
		// ����ȷ����ťʱ�ӷ����������� �µ�apk Ȼ��װ
		builer.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				downLoadApk();
			}
		});
		// ����ȡ����ťʱ���е�¼
		builer.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Message msg = new Message();
				msg.what = UPDATA_NONEED;
				handler.sendMessage(msg);
			}
		});
		AlertDialog dialog = builer.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	/*
	 * �ӷ�����������APK
	 */
	protected void downLoadApk() {
		final ProgressDialog pd; // �������Ի���
		pd = new ProgressDialog(WelcomActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("�������ظ���");
		// pd.setIcon(R.drawable.);
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Message msg = new Message();
			msg.what = SDCARD_NOMOUNTED;
			handler.sendMessage(msg);
		} else {
			pd.show();
			new Thread() {
				@Override
				public void run() {
					try {
						File file = DownLoadManager.getFileFromServer(
								info.getUrl(), pd);
						sleep(1000);
						installApk(file);
						pd.dismiss(); // �������������Ի���

					} catch (Exception e) {
						Message msg = new Message();
						msg.what = DOWN_ERROR;
						handler.sendMessage(msg);
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	// ��װapk
	protected void installApk(File file) {
		Intent intent = new Intent();
		// ִ�ж���
		intent.setAction(Intent.ACTION_VIEW);
		// ִ�е���������
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}
}
