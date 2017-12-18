 package com.example.lxt.ujb_project.tool;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AsyncHttpHelper {

	public static final String BASE_URL = "";

	public static AsyncHttpClient client = new AsyncHttpClient();

	private static String appUserAgent;

	static {
		client.setUserAgent(getUserAgent());
		client.setMaxRetriesAndTimeout(3, 10 * 1000);

	}

	private static String getUserAgent() {
		if (appUserAgent == null || appUserAgent == "") {
			StringBuilder sb = new StringBuilder("itvk");
			sb.append("|Android");
			sb.append("|" + android.os.Build.VERSION.RELEASE);
			sb.append("|" + android.os.Build.MODEL); 
			appUserAgent = sb.toString();
		}
		return appUserAgent;
	}

	public static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}

	public static void get(String url, AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), responseHandler);
	}

	public static void getAbsoluteUrl(String absoluteUrl,
			AsyncHttpResponseHandler responseHandler) {
		client.get(absoluteUrl, responseHandler);
	}

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void getAbsoluteUrl(String absoluteUrl, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(absoluteUrl, params, responseHandler);
	}

	public static void post(String url, AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), responseHandler);
	}

	public static void postAbsoluteUrl(String absoluteUrl,
			AsyncHttpResponseHandler responseHandler) {
		client.post(absoluteUrl, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void postAbsoluteUrl(String absoluteUrl,
			RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(absoluteUrl, params, responseHandler);
	}
}
