package com.example.lxt.ujb_project.entity;

/**
 * ����ʵ����
 * 
 * @author love
 *
 */
public class Insur{

	private int insur_id;    //����ID
	private String insur_name;  //������
	private String insur_desc;  //��������
	private String insur_pic;   //����ͼƬ
	private String insur_content;  //��������
	private String insur_tel;    //��ϵ��ʽ
	public int getInsur_id() {
		return insur_id;
	}
	public void setInsur_id(int insur_id) {
		this.insur_id = insur_id;
	}
	public String getInsur_name() {
		return insur_name;
	}
	public void setInsur_name(String insur_name) {
		this.insur_name = insur_name;
	}
	public String getInsur_desc() {
		return insur_desc;
	}
	public void setInsur_desc(String insur_desc) {
		this.insur_desc = insur_desc;
	}
	public String getInsur_pic() {
		return insur_pic;
	}
	public void setInsur_pic(String insur_pic) {
		this.insur_pic = insur_pic;
	}
	public String getInsur_content() {
		return insur_content;
	}
	public void setInsur_content(String insur_content) {
		this.insur_content = insur_content;
	}
	
	public String getInsur_tel() {
		return insur_tel;
	}
	public void setInsur_tel(String insur_tel) {
		this.insur_tel = insur_tel;
	}
	@Override
	public String toString() {
		return "Insur [insur_id=" + insur_id + ", insur_name=" + insur_name
				+ ", insur_desc=" + insur_desc + ", insur_pic=" + insur_pic
				+ ", insur_content=" + insur_content + ", insur_tel="
				+ insur_tel + "]";
	}
}
