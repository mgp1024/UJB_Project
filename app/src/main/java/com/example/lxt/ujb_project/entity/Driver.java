package com.example.lxt.ujb_project.entity;

/**
 * ˾��ʵ����
 * 
 * @author Joney
 *
 */
public class Driver {
	
	private int driver_id;  //˾��ID
	private int driver_fav;      //˾��������
	private String driver_number; //˾�����
	private String driver_name;  //˾������
	private String driver_tel;   //˾���ֻ���
	private String driver_year;  //����
	private String driver_pic;   //˾��ͷ��
	private String driver_desc;  //˾������
	private int driver_cate;     //˾�����ͣ�0����ʿ��1�����ݣ�
	public int getDriver_id() {
		return driver_id;
	}
	public void setDriver_id(int driver_id) {
		this.driver_id = driver_id;
	}
	public int getDriver_fav() {
		return driver_fav;
	}
	public void setDriver_fav(int driver_fav) {
		this.driver_fav = driver_fav;
	}
	public String getDriver_number() {
		return driver_number;
	}
	public void setDriver_number(String driver_number) {
		this.driver_number = driver_number;
	}
	public String getDriver_name() {
		return driver_name;
	}
	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}
	public String getDriver_tel() {
		return driver_tel;
	}
	public void setDriver_tel(String driver_tel) {
		this.driver_tel = driver_tel;
	}
	public String getDriver_year() {
		return driver_year;
	}
	public void setDriver_year(String driver_year) {
		this.driver_year = driver_year;
	}
	public String getDriver_pic() {
		return driver_pic;
	}
	public void setDriver_pic(String driver_pic) {
		this.driver_pic = driver_pic;
	}
	public String getDriver_desc() {
		return driver_desc;
	}
	public void setDriver_desc(String driver_desc) {
		this.driver_desc = driver_desc;
	}
	public int getDriver_cate() {
		return driver_cate;
	}
	public void setDriver_cate(int driver_cate) {
		this.driver_cate = driver_cate;
	}
	@Override
	public String toString() {
		return "Driver [driver_id=" + driver_id + ", driver_fav=" + driver_fav + ", driver_number=" + driver_number + ", driver_name=" + driver_name + ", driver_tel=" + driver_tel + ", driver_year=" + driver_year + ", driver_pic=" + driver_pic + ", driver_desc=" + driver_desc + ", driver_cate=" + driver_cate + "]";
	}
	
}
