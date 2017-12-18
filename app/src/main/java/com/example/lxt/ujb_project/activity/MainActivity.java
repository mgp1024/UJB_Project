package com.example.lxt.ujb_project.activity;

import java.util.HashMap;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lxt.ujb_project.R;
import com.example.lxt.ujb_project.fragment.AIFragment;
import com.example.lxt.ujb_project.fragment.AdviseFragment;
import com.example.lxt.ujb_project.fragment.HelpFragment;
import com.example.lxt.ujb_project.fragment.HomeFragment;
import com.example.lxt.ujb_project.tool.AppContext;
import com.example.lxt.ujb_project.tool.Util;
import com.example.lxt.ujb_project.view.CircleImageView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;

import com.umeng.message.PushAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 *
 */
public class MainActivity extends FragmentActivity implements OnClickListener {

	public static final String TAG_1 = "HOME";
	public static final String TAG_2 = "ADVISE";
	public static final String TAG_3 = "HELP";
	public static final String TAG_4 = "INSURANCE";

	private TabHost mTabHost;
	private TabManager mTabManager;
	private ImageView iv_left;
	private CircleImageView iv_head;
	private ImageView iv_setting;
	private ImageView iv_share;
	private TextView tv_account;

	private long exitTimes;
	protected SlidingMenu side_drawer;

	private AppContext appContext;

	private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tabs);
		PushAgent mPushAgent = PushAgent.getInstance(this.getApplication());
		mPushAgent.enable();

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

		mTabManager.addTab(mTabHost.newTabSpec(TAG_1).setIndicator(createTabView("安全自测", R.drawable.tabbar_home_selector)), HomeFragment.class, null);
		mTabManager.addTab(mTabHost.newTabSpec(TAG_2).setIndicator(createTabView("健康助手", R.drawable.tabbar_msg_selector)), AdviseFragment.class, null);

		mTabManager.addTab(mTabHost.newTabSpec(TAG_3).setIndicator(createTabView("打车/代驾", R.drawable.tabbar_profile_selector)), HelpFragment.class, null);
		mTabManager.addTab(mTabHost.newTabSpec(TAG_4).setIndicator(createTabView("无忧险", R.drawable.tabbar_discover_selector)), AIFragment.class, null);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		inItActionBar();

		inItPlatform();

		OncheckLogin();
		side_drawer.setOnOpenListener(new OnOpenListener() {
			
			@Override
			public void onOpen() {
				Choose();
			}
		});

		mController.setShareContent("U酒保，您随身必带的小酒保！");
		mController.setShareMedia(new UMImage(MainActivity.this, R.drawable.icon));

	}

	private View createTabView(String text, int icon_id) {

		appContext = (AppContext) this.getApplication();
		View view = LayoutInflater.from(this).inflate(R.layout.tab_indicator, null);

		TextView tv = (TextView) view.findViewById(R.id.tv_title);
		tv.setText(text);

		ImageView iv = (ImageView) view.findViewById(R.id.iv_icon);
		iv.setImageResource(icon_id);

		return view;
	}

	private void inItPlatform() {
		// 添加微信分享平台
		addWXPlatform();
		// 添加QQ分享平台
		addQQQZonePlatform();
		// 添加新浪分享平台
		addSinaPlatform();
		// 添加分享
		addShareContent();
	}

	private void addShareContent() {
		WeiXinShareContent weiXinShareContent = new WeiXinShareContent();
		weiXinShareContent.setShareContent("U酒保，您随身必带的小酒保！");
		weiXinShareContent.setTitle("SpiritHelper");
		weiXinShareContent.setShareImage(new UMImage(MainActivity.this, R.drawable.icon));
		weiXinShareContent.setTargetUrl("http://www.umeng.com/social");
		mController.setShareMedia(weiXinShareContent);

		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent("U酒保，您随身必带的小酒保！");
		qqShareContent.setTitle("SpiritHelper");
		qqShareContent.setShareImage(new UMImage(MainActivity.this, R.drawable.icon));
		qqShareContent.setTargetUrl("http://www.umeng.com/social");
		mController.setShareMedia(qqShareContent);

		QZoneShareContent qZoneShareContent = new QZoneShareContent();
		qZoneShareContent.setShareContent("U酒保，您随身必带的小酒保！");
		qZoneShareContent.setTitle("SpiritHelper");
		qZoneShareContent.setShareImage(new UMImage(MainActivity.this, R.drawable.icon));
		qZoneShareContent.setTargetUrl("http://www.umeng.com/social");
		mController.setShareMedia(qZoneShareContent);

		SinaShareContent SinaShareContent = new SinaShareContent();
		SinaShareContent.setShareContent("U酒保，您随身必带的小酒保！");
		SinaShareContent.setTitle("SpiritHelper");
		SinaShareContent.setShareImage(new UMImage(MainActivity.this, R.drawable.icon));
		SinaShareContent.setTargetUrl("http://www.umeng.com/social");
		mController.setShareMedia(SinaShareContent);

	}

	/**
	 * @功能描述 : 添加新浪平台分享
	 * @return
	 */
	private void addSinaPlatform() {

		SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
		sinaSsoHandler.addToSocialSDK();

	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private void addWXPlatform() {

		String appId = "wx9d6b6d59cfe13bea";
		String appSecret = "97f64d53a275b1477c4078327f4de6a8";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(MainActivity.this, appId, appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(MainActivity.this, appId, appSecret);
		wxCircleHandler.setToCircle(true);

		wxCircleHandler.addToSocialSDK();
	}

	/**
	 * @功能描述 : 添加QQ空间平台分享
	 * @return
	 */
	private void addQQQZonePlatform() {

		String appId = "1103838283";
		String appKey = "bAPjZ48G237mKfjR";
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(MainActivity.this, appId, appKey);
		qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(MainActivity.this, appId, appKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	/**
	 * @功能描述 : 自定义的ActionBar
	 * @return
	 */
	private void inItActionBar() {

		View customView = LayoutInflater.from(this).inflate(R.layout.actionbar, new LinearLayout(this), false);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(customView);// 自定义ActionBar布局

	

		iv_left = (ImageView) customView.findViewById(R.id.iv_left);

		iv_left.setOnClickListener(this);

		side_drawer = new SlidingMenu(this);
		side_drawer.setMode(SlidingMenu.LEFT);
		side_drawer.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
		//
		side_drawer.setMenu(R.layout.slidingmenu_left);
		side_drawer.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 将抽屉菜单与主页面关联起来
		side_drawer.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// 设置滑动时菜单的是否淡入淡出
		side_drawer.setFadeEnabled(true);
		// 设置淡入淡出的比例
		side_drawer.setFadeDegree(0.4f);
		side_drawer.setShadowWidthRes(R.dimen.shadow_width);
		
		iv_head = (CircleImageView) side_drawer.findViewById(R.id.iv_head);
		tv_account = (TextView) side_drawer.findViewById(R.id.tv_account);
		iv_setting = (ImageView)side_drawer.findViewById(R.id.iv_setting);
		iv_share = (ImageView)side_drawer.findViewById(R.id.iv_share);
		iv_head.setOnClickListener(this);
		iv_setting.setOnClickListener(this);
		iv_share.setOnClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - exitTimes > 3000L) {
				Toast.makeText(this, "再次点击退出程序", Toast.LENGTH_SHORT).show();
				exitTimes = System.currentTimeMillis();
			} else {
				this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	/**
	 * This is a helper class that implements a generic mechanism for
	 */
	public static class TabManager implements TabHost.OnTabChangeListener {
		private final FragmentActivity mActivity;
		private final TabHost mTabHost;
		private final int mContainerId;
		private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
		TabInfo mLastTab;

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;
			private Fragment fragment;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
			mActivity = activity;
			mTabHost = tabHost;
			mContainerId = containerId;
			mTabHost.setOnTabChangedListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mActivity));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);

			info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);

			mTabs.put(tag, info);
			mTabHost.addTab(tabSpec);
		}

		@Override
		public void onTabChanged(String tabId) {
			TabInfo newTab = mTabs.get(tabId);
			if (mLastTab != newTab) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();

				if (mLastTab != null) {
					if (mLastTab.fragment != null) {
						ft.hide(mLastTab.fragment);
					}
				}
				if (newTab != null) {
					if (newTab.fragment == null) {
						newTab.fragment = Fragment.instantiate(mActivity, newTab.clss.getName(), newTab.args);
						ft.add(mContainerId, newTab.fragment, newTab.tag);
					} else {
						ft.show(newTab.fragment);
					}
				}

				mLastTab = newTab;
				ft.commit();
				mActivity.getSupportFragmentManager().executePendingTransactions();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			OncheckLogin();
		}
	}

	/**
	 * 检查是否登录
	 */
	public void OncheckLogin() {
		if (appContext.isLogin() == true) {
			if (!appContext.getHead_url().equals("")) {
				iv_head.setImageBitmap(Util.getbitmap(appContext.getHead_url()));
			}
			if (!appContext.getNickname().equals("")) {
				tv_account.setText(appContext.getNickname());
			}
		} else {
			iv_head.setImageResource(R.drawable.login_head);
			tv_account.setText("立即登陆");
		}
	}

	private void Choose() {
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_left:
			if (side_drawer.isMenuShowing()) {
				side_drawer.showContent();
			} else {
				side_drawer.showMenu();
			
			}
			break;
		case R.id.iv_head:
			if (appContext.isLogin() == true) {

			} else {
				 startActivityForResult(new Intent(MainActivity.this,
				 LoginActivity.class), 200);
			}

			break;
		case R.id.iv_setting:
			startActivity(new Intent(MainActivity.this,SystemSettingActivity.class));
			break;
		case R.id.iv_share:
			mController.openShare(MainActivity.this, false);
			break;
		default:
			break;
		}

	}
}
