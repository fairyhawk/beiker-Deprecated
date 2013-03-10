package com.beike.common.catlog.service;

import java.util.List;

import com.beike.form.MerchantForm;

/**
 * <p>Title:品牌类别Service</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 26, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface BrandCatlogService extends AbstractCatlogService {
	
	
	public List<MerchantForm> getGoodsFromIds(List<Long> listids);
	/**
	 * 验证品牌是否有上架商品
	 * @param merchantid
	 * @return
	 */
	public boolean checkMerchantStatus(Long merchantid);
	
}
