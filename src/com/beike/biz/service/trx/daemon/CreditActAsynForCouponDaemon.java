package com.beike.biz.service.trx.daemon;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.core.service.trx.coupon.TrxCouponService;

/**   
 * @title: creditActAsynForCouponDaemon.java
 * @package com.beike.biz.service.trx.daemon
 * @description: 优惠券异步出账
 * @author wangweijie  
 * @date 2012-11-5 下午03:21:17
 * @version v1.0     
 */
@Service("creditActAsynForCouponDaemon")
public class CreditActAsynForCouponDaemon {
	private static final Log logger = LogFactory.getLog(CreditActAsynForCouponDaemon.class);
	
	@Autowired
	private TrxCouponService trxCouponService;
	public void executeCreditActAsyn(){
		Date beginDate = new Date();
		List<TrxCoupon> couponList = trxCouponService.queryNoCreditActCoupon();
		logger.info("+++++++++++++++++++++creditActAsynForCouponDaemon begin++++++++++++couponList="+couponList.size()+"++++++++");
		int count = 0;
		for(TrxCoupon trxCoupon : couponList){
			try {
				trxCouponService.processCreditVmAccountAsyn(trxCoupon);
				count ++;
				logger.error("++++++++++++creditActAsynForCouponDaemon++couponId="+trxCoupon.getId()+"+++asyc account success++++");
			} catch (Exception e) {
				logger.error("++++++++++++creditActAsynForCouponDaemon++{ASYCACCOUNT ERROR}+++++couponId="+trxCoupon.getId(),e);
			}
		}
		logger.info("+++++++++++++++++creditActAsynForCouponDaemon end++total="+couponList.size()+"++++success="+count+"+++" + (new Date().getTime()-beginDate.getTime())+"ms++++");
	}
}
