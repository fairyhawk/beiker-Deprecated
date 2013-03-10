package com.beike.model.lucene;


public class AppSearchQuery {

	@Override
	public String toString() {
		return "AppSearchQuery [cityid=" + cityid + ", isext=" + isext
				+ ", tagid=" + tagid + ", tagextid=" + tagextid + ", regionid="
				+ regionid + ", regionextid=" + regionextid + "]";
	}

	private Integer cityid;
	
	public Integer getCityid() {
		return cityid;
	}

	public void setCityid(Integer cityid) {
		this.cityid = cityid;
	}

	//搜索二级分类/商圈
	private boolean isext = false;
	
	public boolean isIsext() {
		return isext;
	}

	public void setIsext(boolean isext) {
		this.isext = isext;
	}

	private Integer tagid;
	
	private Integer tagextid;
	
	public Integer getTagid() {
		return tagid;
	}

	public void setTagid(Integer tagid) {
		this.tagid = tagid;
	}

	public Integer getTagextid() {
		return tagextid;
	}

	public void setTagextid(Integer tagextid) {
		this.tagextid = tagextid;
	}

	public Integer getRegionid() {
		return regionid;
	}

	public void setRegionid(Integer regionid) {
		this.regionid = regionid;
	}

	public Integer getRegionextid() {
		return regionextid;
	}

	public void setRegionextid(Integer regionextid) {
		this.regionextid = regionextid;
	}

	private Integer regionid;
	
	private Integer regionextid;
	
	
	public void clear(){
		this.tagextid = null;
		this.tagid = null;
		this.regionextid= null;
		this.regionid= null;
		
		
	}
}
