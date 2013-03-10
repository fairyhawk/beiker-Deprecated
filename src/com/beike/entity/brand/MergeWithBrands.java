/**  
* @Title: MergeWithBrands.java
* @Package com.beike.entity.brand
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Aug 29, 2012 11:28:43 AM
* @version V1.0  
*/
package com.beike.entity.brand;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.beike.entity.goods.Goods;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.util.Constant;

/**
 * @ClassName: MergeWithBrands
 * @Description: 品牌聚合
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Aug 29, 2012 11:28:43 AM
 *
 */
public class MergeWithBrands implements Serializable{

    private Long id;//标识
	
    private String serialnum;//序号
	
    private Long goodsid;//商品标识
	
//	public List<GoodsForm> tpbbkList = new ArrayList<GoodsForm>();//同品比比看商品Ids
//	
//	public List<GoodsForm> zbqggList = new ArrayList<GoodsForm>();//周边去逛逛商品Ids
	
    private String tpbbk;//同品比比看商品
    
    private String zbqgg;//周边去逛逛商品
	
    private Long createucid;//创建人Id
	
    private Long updateucid;//修改人Id

    private Date createtime;//创建时间
	
    private Date updatetime;//修改时间
	
    private Goods goods;//主商品
	
    private Integer viewSalesCount;//销售数量
	
    private MerchantForm merchantForm;//品牌
	
    private String regionName;//区域
	
	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public Integer getViewSalesCount() {
		return viewSalesCount;
	}

	public void setViewSalesCount(Integer viewSalesCount) {
		this.viewSalesCount = viewSalesCount;
	}

	public MerchantForm getMerchantForm() {
		return merchantForm;
	}

	public void setMerchantForm(MerchantForm merchantForm) {
		this.merchantForm = merchantForm;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialnum() {
		return serialnum;
	}

	public void setSerialnum(String serialnum) {
		this.serialnum = serialnum;
	}

	public Long getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(Long goodsid) {
		this.goodsid = goodsid;
	}

	public String getTpbbk() {
		return tpbbk;
	}

	public void setTpbbk(String tpbbk) {
		this.tpbbk = tpbbk;
	}

	public String getZbqgg() {
		return zbqgg;
	}

	public void setZbqgg(String zbqgg) {
		this.zbqgg = zbqgg;
	}

	public Long getCreateucid() {
		return createucid;
	}

	public void setCreateucid(Long createucid) {
		this.createucid = createucid;
	}

	public Long getUpdateucid() {
		return updateucid;
	}

	public void setUpdateucid(Long updateucid) {
		this.updateucid = updateucid;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	
}
