package com.example.lxt.ujb_project.activity;

import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lxt.ujb_project.R;
import com.example.lxt.ujb_project.tool.AppContext;
import com.example.lxt.ujb_project.tool.AsyncHttpHelper;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * 用户登录界面
 * 
 * @author love 2014/12/20 修改
 * 
 */
public class LoginActivity extends Activity implements OnClickListener {

	private Button btn_back;
	private EditText et_username;
	private EditText et_password;
	private TextView tv_register;
	private TextView tv_forget;
	private Button btn_login;

	private Button btn_qq;
	private Button btn_sina;
	private AppContext appContext;

	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_login);

		appContext = (AppContext) this.getApplication();
		findByView();
		inItSsoHolder();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void inItSsoHolder() {
		// qq授权
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this, "1104587605", "pmaD0FRatx3CoM1R");
		qqSsoHandler.addToSocialSDK();
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
	}

	private void findByView() {
		btn_back = (Button) findViewById(R.id.btn_back);
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		tv_register = (TextView) findViewById(R.id.tv_register);
		tv_forget = (TextView) findViewById(R.id.tv_forget);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_sina = (Button) findViewById(R.id.btn_sina);
		btn_qq = (Button) findViewById(R.id.btn_qq);

		btn_back.setOnClickListener(this);
		tv_register.setOnClickListener(this);
		tv_forget.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		btn_qq.setOnClickListener(this);
		btn_sina.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tv_register:
//			startActivity(new Intent(LoginActivity.this, RegisteActivity.class));

			break;

		case R.id.tv_forget:

//			startActivity(new Intent(LoginActivity.this, FoundPwdActivity.class));
			break;

		case R.id.btn_login:
			break;
		case R.id.btn_back:
			LoginActivity.this.finish();
			setResult(200);
			break;
		case R.id.btn_qq:
			mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.QQ, new UMAuthListener() {
				@Override
				public void onStart(SHARE_MEDIA platform) {

				}

				@Override
				public void onError(SocializeException e, SHARE_MEDIA platform) {
					Toast.makeText(LoginActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(Bundle value, SHARE_MEDIA platform) {
					appContext.setLogin_name(value.get("uid").toString());
					// 获取相关授权信息
					mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, new UMDataListener() {
						@Override
						public void onStart() {

						}

						@Override
						public void onComplete(int status, Map<String, Object> info) {
							if (status == 200 && info != null) {

								appContext.setNickname(info.get("screen_name").toString());

								appContext.setHead_url(info.get("profile_image_url").toString());
								login();
							
							} else {
								Log.d("TestData", "授权失败" + status);
							}
						}
					});

				}

				@Override
				public void onCancel(SHARE_MEDIA platform) {
					Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
				}
			});

			break;
		case R.id.btn_sina:
			mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.SINA, new UMAuthListener() {
				@Override
				public void onError(SocializeException e, SHARE_MEDIA platform) {
				}

				@Override
				public void onComplete(Bundle value, SHARE_MEDIA platform) {
					if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
						mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, new UMDataListener() {
							@Override
							public void onStart() {
								Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onComplete(int status, Map<String, Object> info) {
								if (status == 200 && info != null) {
									appContext.setNickname(info.get("nickname").toString());

									appContext.setLogin_name(info.get("openid").toString());
									appContext.setHead_url(info.get("headimgurl").toString());
									login();
									
								
								} else {
									Log.d("TestData", "授权失败" + status);
								}
							}
						});

					} else {
						Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onCancel(SHARE_MEDIA platform) {
				}

				@Override
				public void onStart(SHARE_MEDIA platform) {
				}
			});

			break;
		default:
			break;
		}

	}

	private void login() {
		
//		String url ="http://localhost:8080/Watch/UserThreeLogin?operation=threeLogin&nickname=1232&phoneNumber=34567fdgfdbgv&avatorId=http://local;8080/dgdf.jsp";
		
		String url ="http://123.56.143.204:8080/Watch/UserThreeLogin";
		String operation = "threeLogin";
		String username = appContext.getLogin_name();
		String nickname = appContext.getNickname();
		String head_img = appContext.getHead_url();
		
		RequestParams params = new RequestParams();
		params.add("operation", operation);
		params.add("phoneNumber", username);
		params.add("nickname", nickname);
		params.add("avatorId", head_img);
	
		AsyncHttpHelper.postAbsoluteUrl(url, params, new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				try {
					
					JSONObject obj = new JSONObject(arg2);
					int user_id = obj.optInt("userId");
					appContext.setUser_id(user_id);
					Toast.makeText(appContext, user_id+"", Toast.LENGTH_SHORT).show();
					LoginActivity.this.finish();
					setResult(200);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable t) {
				Log.d("访问服务器失败", "--------------------->");
			}

		});
	}
}
