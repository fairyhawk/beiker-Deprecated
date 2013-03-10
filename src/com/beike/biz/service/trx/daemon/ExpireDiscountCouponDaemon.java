package com.beike.biz.service.trx.daemon;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.entity.discountcoupon.DiscountCoupon;
import com.beike.common.enums.trx.DiscountCouponStatus;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.core.service.trx.coupon.TrxCouponService;
import com.beike.core.service.trx.discountcoupon.DiscountCouponService;

/**   
 * @title: ExpireDiscountCouponDaemon
 * @package com.beike.biz.service.trx.daemon
 * @description: 线下优惠券过期处理定时
 * @author wangweijie  
 * @date 2012-7-16 下午07:46:21
 * @version v1.0   
 */
@Service("expireDiscountCouponDaemon")
public class ExpireDiscountCouponDaemon {
	private static final Log log = LogFactory.getLog(ExpireDiscountCouponDaemon.class);
	@Autowired
	private DiscountCouponService discountCouponService;
	@Autowired
	private TrxCouponService trxCouponService;
	
	public void noTscExecute(){
		Date date = new Date();
		timeoutDiscountCoupon(date);
		timeoutTrxCoupon(date);
	}
	
	public void processExpire(DiscountCoupon coupon) throws StaleObjectStateException{
		discountCouponService.updateCouponStatus(DiscountCouponStatus.TIMEOUT,"系统超时自动处理", coupon.getId(), coupon.getVersion());
	}
	
	/**
	 * 优惠券2期过期
	 * @param date
	 */
	public void timeoutDiscountCoupon(Date date){
		log.info("daemon the expired discount coupon begin....");
		int expiredNumber = 0;		//超时优惠券数量
		int successNumber = 0;		//处理成功数量
		int exceptionNumber = 0;	//处理异常处理
		List<DiscountCoupon> expiredCouponList = discountCouponService.findExpireCouponInActiveStatus();
		expiredNumber = expiredCouponList.size();
		
		for(DiscountCoupon coupon : expiredCouponList){
			log.info("discount coupon [id="+coupon.getId()+"] is expired");
			try {
				processExpire(coupon);
				successNumber += 1;
			} catch (Exception e) {
				log.debug("update discount coupon [id="+coupon.getId()+"] error:"+e);
				exceptionNumber += 1;
			}
		}
		log.info("####expired number=" + expiredNumber+"####successNumber=" + successNumber+"####exceptionNumber=" + exceptionNumber);
		log.info("daemon the expired discount coupon end......the task takes "+ (new Date().getTime()-date.getTime())+ "ms");
	}
	
	
	/**
	 * 优惠券3期过期
	 * @param date
	 */
	public void timeoutTrxCoupon(Date date){
		log.info("daemon the expired coupon3 begin....");
		int expiredNumber = 0;		//超时优惠券数量
		int successNumber = 0;		//处理成功数量
		int exceptionNumber = 0;	//处理异常处理
		List<TrxCoupon> couponList = trxCouponService.queryTimeoutTrxCoupon();
		expiredNumber = couponList.size();
		
		for(TrxCoupon coupon : couponList){
			log.info("coupon3 [id="+coupon.getId()+"] is expired");
			try {
				processTrxCouponExpire(coupon);
				successNumber += 1;
			} catch (Exception e) {
				log.error("update coupon3 [id="+coupon.getId()+"] error:"+e);
				exceptionNumber += 1;
			}
		}
		
		log.info("####expired number=" + expiredNumber+"####successNumber=" + successNumber+"####exceptionNumber=" + exceptionNumber);
		log.info("daemon the expired coupon3 end......the task takes "+ (new Date().getTime()-date.getTime())+ "ms");
	
	}
	
	public void processTrxCouponExpire(TrxCoupon coupon) throws StaleObjectStateException{
		trxCouponService.updateTrxCouponForTimeout(coupon.getId(), coupon.getVersion(), "优惠券超时，系统自动处理");
	}
	
}
