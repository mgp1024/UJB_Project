package com.example.lxt.ujb_project.update;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class UpdataInfoParser {

	/*
	 * ��pull�������������������ص�xml�ļ� (xml��װ�˰汾��)
	 */
	public static UpdataInfo getUpdataInfo(InputStream is) throws Exception{
		XmlPullParser  parser = Xml.newPullParser();  
		parser.setInput(is, "utf-8");//���ý���������Դ 
		int type = parser.getEventType();
		UpdataInfo info = new UpdataInfo();//ʵ��
		while(type != XmlPullParser.END_DOCUMENT ){
			switch (type) {
			case XmlPullParser.START_TAG:
				if("version".equals(parser.getName())){
					info.setVersion(parser.nextText());	//��ȡ�汾��
				}else if ("url".equals(parser.getName())){
					info.setUrl(parser.nextText());	//��ȡҪ������APK�ļ�
				}else if ("description".equals(parser.getName())){
					info.setDescription(parser.nextText());	//��ȡ���ļ�����Ϣ
				}
				break;
			}
			type = parser.next();
		}
		return info;
	}
}
