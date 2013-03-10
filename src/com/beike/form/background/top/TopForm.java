package com.beike.form.background.top;

import java.sql.Timestamp;
/**
 * Title : 	TopForm
 * <p/>
 * Description	:置顶表单对象
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
 * <pre>1     2011-6-14    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-6-14  
 */
public class TopForm {
	
	private int topId;
	private int topOldGoodsId;
	private int topNewGoodsId;
	private Timestamp topCreateTime;
	private Timestamp topModifyTime;
	private String topStatus;
	private int brandId;
	public int getTopId() {
		return topId;
	}
	public void setTopId(int topId) {
		this.topId = topId;
	}
	public int getTopOldGoodsId() {
		return topOldGoodsId;
	}
	public void setTopOldGoodsId(int topOldGoodsId) {
		this.topOldGoodsId = topOldGoodsId;
	}
	public int getTopNewGoodsId() {
		return topNewGoodsId;
	}
	public void setTopNewGoodsId(int topNewGoodsId) {
		this.topNewGoodsId = topNewGoodsId;
	}
	public Timestamp getTopCreateTime() {
		return topCreateTime;
	}
	public void setTopCreateTime(Timestamp topCreateTime) {
		this.topCreateTime = topCreateTime;
	}
	public Timestamp getTopModifyTime() {
		return topModifyTime;
	}
	public void setTopModifyTime(Timestamp topModifyTime) {
		this.topModifyTime = topModifyTime;
	}
	public String getTopStatus() {
		return topStatus;
	}
	public void setTopStatus(String topStatus) {
		this.topStatus = topStatus;
	}
	public int getBrandId() {
		return brandId;
	}
	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}
	
}
