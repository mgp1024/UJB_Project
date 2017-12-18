package com.example.lxt.ujb_project.entity;

public class Knowledge {
	
	private int knowledge_id;  //科普ID
	private String knowledge_title; //科普标题
	private String knowledge_pic;   //科普图片
	private String knowledge_desc;  //科普描述
	private String knowledge_content; //科普内容
	private int knowledge_cate;     //科普类型
	public int getKnowledge_id() {
		return knowledge_id;
	}
	public void setKnowledge_id(int knowledge_id) {
		this.knowledge_id = knowledge_id;
	}
	public String getKnowledge_title() {
		return knowledge_title;
	}
	public void setKnowledge_title(String knowledge_title) {
		this.knowledge_title = knowledge_title;
	}
	public String getKnowledge_pic() {
		return knowledge_pic;
	}
	public void setKnowledge_pic(String knowledge_pic) {
		this.knowledge_pic = knowledge_pic;
	}
	public String getKnowledge_desc() {
		return knowledge_desc;
	}
	public void setKnowledge_desc(String knowledge_desc) {
		this.knowledge_desc = knowledge_desc;
	}
	public String getKnowledge_content() {
		return knowledge_content;
	}
	public void setKnowledge_content(String knowledge_content) {
		this.knowledge_content = knowledge_content;
	}
	public int getKnowledge_cate() {
		return knowledge_cate;
	}
	public void setKnowledge_cate(int knowledge_cate) {
		this.knowledge_cate = knowledge_cate;
	}
	@Override
	public String toString() {
		return "Knowledge [knowledge_id=" + knowledge_id + ", knowledge_title="
				+ knowledge_title + ", knowledge_pic=" + knowledge_pic
				+ ", knowledge_desc=" + knowledge_desc + ", knowledge_content="
				+ knowledge_content + ", knowledge_cate=" + knowledge_cate
				+ "]";
	}
	
	
	

}
