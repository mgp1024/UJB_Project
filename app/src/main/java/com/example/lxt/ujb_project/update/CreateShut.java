package com.example.lxt.ujb_project.update;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

import com.example.lxt.ujb_project.R;


//���������ݷ�ʽ�Ĺ���
public class CreateShut {
	public CreateShut(Activity activity) {
		
		
		// intent������ʽ��ת,�����洴����ݷ�ʽ
		Intent addIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		//�������ؽ�
		addIntent.putExtra("duplicate", false);
		// �õ�Ӧ�õ�����
		String title = activity.getResources().getString(R.string.app_name);
		// ��Ӧ�õ�ͼ������ΪParceable����
		Parcelable icon = Intent.ShortcutIconResource.fromContext(
				activity, R.drawable.ic_launcher);
		// ���ͼ��֮�����ͼ����
		Intent myIntent = new Intent(activity, WelcomActivity.class);
		// ���ÿ�ݷ�ʽ������
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// ���ÿ�ݷ�ʽ��ͼ��
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// ���ÿ�ݷ�ʽ����ͼ
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
		// ���͹㲥
		activity.sendBroadcast(addIntent);
	}
}
