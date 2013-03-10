package com.beike.entity.background.area;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Title : 	Area
 * <p/>
 * Description	:城市实体对象
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : SinoboGroup.com
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-5-30    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-30  
 */
@Entity
public class Area {
	@Id
	@GeneratedValue
	private int areaId;
	private String areaCnName;
	private String areaEnName;
	private int areaParentId;
	private String areaCode;
	private String areaType;
	private int areaLevel;
	private String areaIsActive;
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public String getAreaCnName() {
		return areaCnName;
	}
	public void setAreaCnName(String areaCnName) {
		this.areaCnName = areaCnName;
	}
	public String getAreaEnName() {
		return areaEnName;
	}
	public void setAreaEnName(String areaEnName) {
		this.areaEnName = areaEnName;
	}
	public int getAreaParentId() {
		return areaParentId;
	}
	public void setAreaParentId(int areaParentId) {
		this.areaParentId = areaParentId;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getAreaType() {
		return areaType;
	}
	public void setAreaType(String areaType) {
		this.areaType = areaType;
	}
	public int getAreaLevel() {
		return areaLevel;
	}
	public void setAreaLevel(int areaLevel) {
		this.areaLevel = areaLevel;
	}
	public String getAreaIsActive() {
		return areaIsActive;
	}
	public void setAreaIsActive(String areaIsActive) {
		this.areaIsActive = areaIsActive;
	}
	
}
