package com.beike.model.lucene;

import java.io.Serializable;

/**
 * 
 * idΨһ
 * @author ye.tian
 *
 */
public class QueryParam implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String city;
	
	private String content;
	
	public QueryParam(Integer id, String content) {
		this.id = id;
		this.content = content;
	}

	public QueryParam(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		if(!(obj instanceof QueryParam))
			return false;
		
		QueryParam queryParam=(QueryParam) obj;
		
		return this.id.equals(queryParam.getId());
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	
}
