package com.beike.entity.common;

import java.io.Serializable;

/**
 * <p>Title:email实体 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 5, 2011
 * @author ye.tian
 * @version 1.0
 */

@SuppressWarnings("serial")
public class Email implements Serializable{
	
	private int id;
	
	private String templatecode;
	
	private String templatecontent;
	
	private String templatesubject;
	
	public Email(){
		
	}

	public Email(int id, String templatecode, String templatecontent,
			String templatesubject) {
		this.id = id;
		this.templatecode = templatecode;
		this.templatecontent = templatecontent;
		this.templatesubject = templatesubject;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTemplatecode() {
		return templatecode;
	}

	public void setTemplatecode(String templatecode) {
		this.templatecode = templatecode;
	}

	public String getTemplatecontent() {
		return templatecontent;
	}

	public void setTemplatecontent(String templatecontent) {
		this.templatecontent = templatecontent;
	}

	public String getTemplatesubject() {
		return templatesubject;
	}

	public void setTemplatesubject(String templatesubject) {
		this.templatesubject = templatesubject;
	}
	
	
}
