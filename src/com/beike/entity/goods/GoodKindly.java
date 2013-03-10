package com.beike.entity.goods;

import java.io.Serializable;
import java.sql.Timestamp;
/**  
* @Title:  商品温馨提示
* @Package com.beike.entity.goods
* @Description: TODO
* @author wenjie.mai  
* @date Aug 10, 2012 10:47:54 AM
* @version V1.0  
*/
public class GoodKindly implements Serializable {

	
	public GoodKindly() {
		
	}

	/**
	 * 商品ID
	 */
	private int goodId;
	
	/**
	 * 温馨提示内容
	 */
	private String kindlywarnings;
	
	/**
	 * 温馨提示是否高亮  1:高亮;0:非高亮
	 */
	private int highlight;
	
	/**
	 * 添加时间
	 */
	private Timestamp createTime;

	public int getGoodId() {
		return goodId;
	}

	public void setGoodId(int goodId) {
		this.goodId = goodId;
	}

	public String getKindlywarnings() {
		return kindlywarnings;
	}

	public void setKindlywarnings(String kindlywarnings) {
		this.kindlywarnings = kindlywarnings;
	}

	public int getHighlight() {
		return highlight;
	}

	public void setHighlight(int highlight) {
		this.highlight = highlight;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
}
