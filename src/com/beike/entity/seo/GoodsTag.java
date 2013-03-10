package com.beike.entity.seo;

import java.util.Date;

/**
 * SEO 对应的商品标签类
 * 
 * @author zx.liu
 */
public class GoodsTag {

	private Long id;
	// 商品标签名称
	private String tagEnname;
	// 标签添加的时间
	private Date addTime;
	// 该标签对应的商品ID
	private Long goodsId=0L;
	
	public GoodsTag(){		
	}

	public GoodsTag(Long id, String tagEnname, Date addTime, Long goodsId){
		this.id = id;
		this.tagEnname = tagEnname;
		this.addTime = addTime;
		this.goodsId = goodsId;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getTagEnname() {
		return tagEnname;
	}
	public void setTagEnname(String tagEnname) {
		this.tagEnname = tagEnname;
	}

	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
		
	
}
