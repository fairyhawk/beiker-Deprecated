package com.beike.entity.merchant;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**      
 * project:beiker  
 * Title:店铺属性表
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 15, 2012 2:09:26 PM     
 * @version 1.0
 */
public class BranchProfile {
	private Long id;
	//商家ID
	private Long merchantId;
	//分店ID
	private Long branchId;
	//好评数
	private Long wellCount = 0l;
	public Long getWellCount() {
		return wellCount;
	}
	public void setWellCount(Long wellCount) {
		this.wellCount = wellCount;
	}
	public Long getSatisfyCount() {
		return satisfyCount;
	}
	public void setSatisfyCount(Long satisfyCount) {
		this.satisfyCount = satisfyCount;
	}
	public Long getPoorCount() {
		return poorCount;
	}
	public void setPoorCount(Long poorCount) {
		this.poorCount = poorCount;
	}
	public float getSatisfyRate() {
		return satisfyRate;
	}
	public void setSatisfyRate(float satisfyRate) {
		this.satisfyRate = satisfyRate;
	}
	public float getPartWellRate() {
		return partWellRate;
	}
	public void setPartWellRate(float partWellRate) {
		this.partWellRate = partWellRate;
	}
	public float getPartSatisfyRate() {
		return partSatisfyRate;
	}
	public void setPartSatisfyRate(float partSatisfyRate) {
		this.partSatisfyRate = partSatisfyRate;
	}
	public float getPartPoorRate() {
		return partPoorRate;
	}
	public void setPartPoorRate(float partPoorRate) {
		this.partPoorRate = partPoorRate;
	}



	//满意评价数
	private Long satisfyCount = 0l;
	//差评数
	private Long poorCount = 0l;
	//总满意率 = (好评价数+满意评价数)*100/(好评价数+满意评价数+差评价数)
	private float satisfyRate;
	 //分好评率 = 好评价数*100/(好评价数+满意评价数+差评价数)
	private float partWellRate;
	//分满意率 = 满意评价数*100/(好评价数+满意评价数+差评价数)
	private float partSatisfyRate;
	//分满意率 = 差评价数*100/(好评价数+满意评价数+差评价数)
	private float partPoorRate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}
	public Long getBranchId() {
		return branchId;
	}
	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	
	
	/**
	 * 计算满意率
	 */
	public BranchProfile calculateScore() {
		//满意率计算
		Long evalCount = getWellCount() + getSatisfyCount() + getPoorCount();
		if(evalCount>10){
			setSatisfyRate(new BigDecimal((float)(getWellCount() + getSatisfyCount())* 100 / evalCount)
				.setScale(2,RoundingMode.HALF_UP).floatValue());
			
			setPartWellRate(new BigDecimal((float)getWellCount() * 100 / evalCount)
				.setScale(2,RoundingMode.HALF_UP).floatValue());
			
			setPartSatisfyRate(new BigDecimal((float)getSatisfyCount() * 100 / evalCount)
				.setScale(2,RoundingMode.HALF_UP).floatValue());
					
			setPartPoorRate(new BigDecimal((float)getPoorCount() * 100 / evalCount)
				.setScale(2,RoundingMode.HALF_UP).floatValue());
		}else{
			setSatisfyRate(-1);
			setPartWellRate(-1);
			setPartSatisfyRate(-1);
			setPartPoorRate(-1);
		}
		return this;
	}
}
