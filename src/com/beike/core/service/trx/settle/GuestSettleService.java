package com.beike.core.service.trx.settle;

import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxorderGoodsException;

/**
 * @Title: GuestSettleService.java
 * @Package com.beike.core.service.trx.settle
 * @Description:商家新清结算接口
 * @date May 17, 2011 6:31:57 PM
 * @author wh.cheng
 * @version v1.0
 */

public interface GuestSettleService {
 
	  /**
	   * 商家新清结算同步入账
	   * @param requestData
	   * @param trxorderGoods
	   * @param voucherId
	   */
	 public void guestCreditForSync(TrxRequestData requestData,TrxorderGoods trxorderGoods,Long voucherId);
  
  
	  /**
	   * 商家新清结算异步入账
	   * @param requestData
	   * @param trxorderGoods
	   * @param voucherId
	 * @throws StaleObjectStateException 
	 * @throws TrxorderGoodsException 
	 * @throws Exception 
	   */
     public void guestCreditForAsyn(TrxorderGoods trxorderGoods) throws TrxorderGoodsException, StaleObjectStateException, Exception;
}
