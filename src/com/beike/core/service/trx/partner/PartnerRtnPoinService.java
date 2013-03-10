package com.beike.core.service.trx.partner;

import com.beike.common.entity.trx.TrxorderGoods;

/**
 * 分销商结算返点业务接口
 * @author yurenli
 *
 */
public interface PartnerRtnPoinService {
	
	/**
	 * 插入分销商订单是否需要结算返点
	 * @param payPrice
	 * @param tagType
	 */
	
	public String processRtnPoin(TrxorderGoods tg,String partnerNo,String outReqId,double configAmount,String configTagType) throws Exception;
}
