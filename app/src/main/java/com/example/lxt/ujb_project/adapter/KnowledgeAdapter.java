package com.example.lxt.ujb_project.adapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lxt.ujb_project.R;
import com.example.lxt.ujb_project.entity.Knowledge;
import com.example.lxt.ujb_project.libcore.io.DiskLruCache;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


public class KnowledgeAdapter extends ArrayAdapter<Knowledge> {

	private List<Knowledge> insurs;
	/**
	 * ͼƬӲ�̻�������ࡣ
	 */
	private DiskLruCache mDiskLruCache;
	/**
	 * ͼƬ���漼���ĺ����࣬���ڻ����������غõ�ͼƬ���ڳ����ڴ�ﵽ�趨ֵʱ�Ὣ�������ʹ�õ�ͼƬ�Ƴ�����
	 */
	private LruCache<String, Bitmap> mMemoryCache;
	/**
	 * ����ˢ�µ�ʵ��
	 */
	private PullToRefreshListView pullToRefreshListView;
	/**
	 * ��¼�����������ػ�ȴ����ص�����
	 */
	private Set<BitmapWorkerTask> taskCollection;

	public KnowledgeAdapter(Context context, int resource, List<Knowledge> objects,
			PullToRefreshListView listView) {
		super(context, resource, objects);

		this.pullToRefreshListView = listView;
		this.insurs = objects;
		taskCollection = new HashSet<BitmapWorkerTask>();

		// ��ȡӦ�ó����������ڴ�
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// ����ͼƬ�����СΪ�����������ڴ��1/8

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
		try {
			// ��ȡͼƬ����·��
			File cacheDir = getDiskCacheDir(context, "thumb");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			// ����DiskLruCacheʵ������ʼ����������
			mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context),
					1, 10 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Knowledge insur = getItem(position);
		// ���ָ��3���ַ���
		String[] sourceStrArray = null;
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_news, null);
			holder = new ViewHolder();

			holder.rl_typ1 = (RelativeLayout) convertView
					.findViewById(R.id.rl_typ1);
			holder.rl_typ2 = (RelativeLayout) convertView
					.findViewById(R.id.rl_typ2);

			holder.ivPreview = (ImageView) convertView
					.findViewById(R.id.ivPreview);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvContent = (TextView) convertView
					.findViewById(R.id.tvContent);
			// ///////////////
			holder.tvTitle_2 = (TextView) convertView
					.findViewById(R.id.tvTitle_2);
			holder.iv_pic1 = (ImageView) convertView.findViewById(R.id.iv_pic1);
			holder.iv_pic2 = (ImageView) convertView.findViewById(R.id.iv_pic2);
			holder.iv_pic3 = (ImageView) convertView.findViewById(R.id.iv_pic3);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String url = insur.getKnowledge_pic();
		if (!"pic".equals(insur.getKnowledge_desc())) {
			
			holder.rl_typ1.setVisibility(View.VISIBLE);
			holder.rl_typ2.setVisibility(View.GONE);
			holder.ivPreview.setTag(insur.getKnowledge_pic());
			holder.ivPreview.setImageResource(R.drawable.detail_loading);
			loadBitmaps(holder.ivPreview, url);

			holder.tvTitle.setText(insur.getKnowledge_title());
			holder.tvContent.setText(insur.getKnowledge_desc());
		} else {
			holder.rl_typ1.setVisibility(View.GONE);
			holder.rl_typ2.setVisibility(View.VISIBLE);

			holder.tvTitle_2.setText(insur.getKnowledge_title());
			sourceStrArray = url.split("\\|");
			
			holder.iv_pic1.setTag(sourceStrArray[0]);
			loadBitmaps(holder.ivPreview, sourceStrArray[0]);

			holder.iv_pic2.setTag(sourceStrArray[1]);
			loadBitmaps(holder.ivPreview, sourceStrArray[1]);

			holder.iv_pic3.setTag(sourceStrArray[2]);
			loadBitmaps(holder.ivPreview, sourceStrArray[2]);
		}

		return convertView;
	}

	public void addNews(List<Knowledge> addNews) {
		for (Knowledge hm : addNews) {
			insurs.add(hm);
		}
	}

	/**
	 * ����Bitmap���󡣴˷�������LruCache�м��������Ļ�пɼ���ImageView��Bitmap����
	 * ��������κ�һ��ImageView��Bitmap�����ڻ����У��ͻῪ���첽�߳�ȥ����ͼƬ��
	 */
	public void loadBitmaps(ImageView imageView, String imageUrl) {
		try {
			Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
			if (bitmap == null) {
				BitmapWorkerTask task = new BitmapWorkerTask();
				taskCollection.add(task);
				task.execute(imageUrl);
			} else {
				if (imageView != null && bitmap != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��һ��ͼƬ�洢��LruCache�С�
	 * 
	 * @param key
	 *            LruCache�ļ������ﴫ��ͼƬ��URL��ַ��
	 * @param bitmap
	 *            LruCache�ļ������ﴫ������������ص�Bitmap����
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * ��LruCache�л�ȡһ��ͼƬ����������ھͷ���null��
	 * 
	 * @param key
	 *            LruCache�ļ������ﴫ��ͼƬ��URL��ַ��
	 * @return ��Ӧ�������Bitmap���󣬻���null��
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * �첽����ͼƬ������
	 * 
	 * @author guolin
	 */
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		/**
		 * ͼƬ��URL��ַ
		 */
		private String imageUrl;

		@Override
		protected Bitmap doInBackground(String... params) {
			imageUrl = params[0];
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			DiskLruCache.Snapshot snapShot = null;
			try {
				// ����ͼƬURL��Ӧ��key
				final String key = hashKeyForDisk(imageUrl);
				// ����key��Ӧ�Ļ���
				snapShot = mDiskLruCache.get(key);
				if (snapShot == null) {
					// ���û���ҵ���Ӧ�Ļ��棬��׼�����������������ݣ���д�뻺��
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (downloadUrlToStream(imageUrl, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					// ���汻д����ٴβ���key��Ӧ�Ļ���
					snapShot = mDiskLruCache.get(key);
				}
				if (snapShot != null) {
					fileInputStream = (FileInputStream) snapShot
							.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}
				// ���������ݽ�����Bitmap����
				Bitmap bitmap = null;
				if (fileDescriptor != null) {
					bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
				}
				if (bitmap != null) {
					// ��Bitmap������ӵ��ڴ滺�浱��
					addBitmapToMemoryCache(params[0], bitmap);
				}
				return bitmap;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileDescriptor == null && fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			// ����Tag�ҵ���Ӧ��ImageView�ؼ��������غõ�ͼƬ��ʾ������
			ImageView imageView = (ImageView) pullToRefreshListView
					.findViewWithTag(imageUrl);
			if (imageView != null && bitmap != null) {
				imageView.setImageBitmap(bitmap);
			}
			taskCollection.remove(this);
		}

		/**
		 * ����HTTP���󣬲���ȡBitmap����
		 * 

		 *            ͼƬ��URL��ַ
		 * @return �������Bitmap����
		 */
		private boolean downloadUrlToStream(String urlString,
				OutputStream outputStream) {
			HttpURLConnection urlConnection = null;
			BufferedOutputStream out = null;
			BufferedInputStream in = null;
			try {
				final URL url = new URL(urlString);
				urlConnection = (HttpURLConnection) url.openConnection();
				in = new BufferedInputStream(urlConnection.getInputStream(),
						8 * 1024);
				out = new BufferedOutputStream(outputStream, 8 * 1024);
				int b;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				return true;
			} catch (final IOException e) {
				e.printStackTrace();
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				try {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			return false;
		}

	}

	/**
	 * ʹ��MD5�㷨�Դ����key���м��ܲ����ء�
	 */
	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * ���ݴ����uniqueName��ȡӲ�̻����·����ַ��
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * ��ȡ��ǰӦ�ó���İ汾�š�
	 */
	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	static class ViewHolder {
		RelativeLayout rl_typ1;
		ImageView ivPreview;
		TextView tvTitle;
		TextView tvContent;
		// /////
		RelativeLayout rl_typ2;
		TextView tvTitle_2;
		ImageView iv_pic1;
		ImageView iv_pic2;
		ImageView iv_pic3;
	}
}
