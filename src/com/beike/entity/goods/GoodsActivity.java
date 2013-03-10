/**  
* @Title: GoodsActivity.java
* @Package com.beike.entity.goods
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Sep 11, 2012 7:34:37 PM
* @version V1.0  
*/
package com.beike.entity.goods;

import java.sql.Timestamp;

/**
 * @ClassName: GoodsActivity
 * @Description: 活动商品
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Sep 11, 2012 7:34:37 PM
 *
 */
public class GoodsActivity {

	private Long actId;
	private Long goodsId;
	private Timestamp createDate;
	
	public Long getActId() {
		return actId;
	}
	public void setActId(Long actId) {
		this.actId = actId;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
}
