package com.example.lxt.ujb_project.entity;

import java.io.Serializable;

public class Data implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7685103231685652592L;

	private int density;
	private int date;
	
	public int getDensity() {
		return density;
	}

	public void setDensity(int density) {
		this.density = density;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Data [density=" + density + ", date=" + date + "]";
	}
	
}
