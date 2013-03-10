package com.beike.common.entity.trx;    
/**   
 * @Title: GuidItem.java
 * @Package com.beike.common.entity.trx
 * @Description:  全局序列号
 * @author wh.cheng@sinobogroup.com
 * @date May 6, 2011 8:36:50 PM
 * @version V1.0   
 */
public class GuidItem {
	
	 private Long id;
	 
	 private Long autoId;
	 
	 private String project;
	 
	 private String description="";

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAutoId() {
		return autoId;
	}

	public void setAutoId(Long autoId) {
		this.autoId = autoId;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	 
	 
	

}
 