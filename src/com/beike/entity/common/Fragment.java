package com.beike.entity.common;

import java.io.Serializable;

/**
 * <p>Title:碎片实体 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Jul 2, 2011
 * @author ye.tian
 * @version 1.0
 */

@SuppressWarnings("serial")
public class Fragment implements Serializable{
	private Long fragmentid;
	
	private String city;
	
	private String name;
	
	private String title;
	
	private String page;
	
	private String content;
	
	private Long version;
	
	private Integer ispublish;
	
	private String type;
	
	private Long count; 
	


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getFragmentid() {
		return fragmentid;
	}

	public void setFragmentid(Long fragmentid) {
		this.fragmentid = fragmentid;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Integer getIspublish() {
		return ispublish;
	}

	public void setIspublish(Integer ispublish) {
		this.ispublish = ispublish;
	}
}
