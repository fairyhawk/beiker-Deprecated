package com.beike.biz.service.trx.daemon;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.CreditStatus;
import com.beike.common.enums.trx.MerSettleStatus;
import com.beike.common.exception.BaseException;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.settle.GuestSettleService;
import com.beike.service.common.EmailService;
import com.beike.util.DateUtils;
import com.beike.util.PropertyUtil;
/**
 * @Title: guestCreditDaemon.java
 * @Package com.beike.biz.service.trx.daemon
 * @Description: 商家新清结算逻辑同步入账失败补结算定时
 * @date 2 20, 2013 6:53:25 PM
 * @author qinggang.liu update by wenhua.cheng
 * @version v1.0
 */
@Service("guestCreditDaemon")
public class GuestCreditDaemon {
    private final Log logger = LogFactory.getLog(GuestCreditDaemon.class);
    
    
    @Autowired
    private TrxorderGoodsService trxorderGoodsService;
    @Autowired
	private GuestSettleService guestSettleService;
    
	@Autowired
	private EmailService emailService;
	PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	public String fromEmail = propertyUtil.getProperty("settle_sender");
	public String toEmails = propertyUtil.getProperty("settle_toer");
    /**
     * 查询前七天中，未成功入账的商品订单,重新调用入账
     */
    public void executeGuestCredit(){
        try {
            logger.info("+++++ executeGuestCredit Daemon start:" +new Date());
            Long s1=System.currentTimeMillis();
            String todayBef = DateUtils.getTimeBeforeORAfter(-7);
            Date dateBef=DateUtils.toDate(todayBef, "yyyy-MM-dd HH:mm:ss");
            String startDate= DateUtils.toString(dateBef,"yyyy-MM-dd 00:00:00");
            String endDate =DateUtils.toString(dateBef,"yyyy-MM-dd 59:59:59");
            //查询未入账成功的商品订单
            List<TrxorderGoods> list=  trxorderGoodsService.qryTrxOrderGoodsByCreditStatus(startDate, endDate, CreditStatus.CREDITING);
            if(list!=null && list.size()>0){
                for(TrxorderGoods trxorderGoods:list){
                    try {
                    	if(MerSettleStatus.UNSETTLE.equals(trxorderGoods.getMerSettleStatus())){
                    		guestSettleService.guestCreditForAsyn(trxorderGoods);
                            logger.info("+++ executeGuestCredit Daemon success:"+trxorderGoods.getId());
                    	}else{
                    		//报警邮件（如果代码运营到此处，则为重大BUG） 
                    		 sendMailErrorForGuestSettle(toEmails,fromEmail,"新商家清算失败：MersttleStatus 错误 ", "tgId:"+trxorderGoods.getId()+":MersttleStatus:"+trxorderGoods.getMerSettleStatus());
                    		 logger.info("+++ executeGuestCredit Daemon merSettleStatus error->bug:"+trxorderGoods.getId());
                    	}
                        
                    }catch (BaseException be) {
                    	//报警邮件（业务）
                    	sendMailErrorForGuestSettle(toEmails,fromEmail,"新商家清算失败 :异常代码："+be.getCode(), "tgId:"+trxorderGoods.getId()+"msg:"+be.getMessage());
                        logger.error("+++ executeGuestCredit Daemon for error:"+trxorderGoods.getId(),be);
                    }catch (Exception e) {
                    	//报警邮件（系统错误）
                    	sendMailErrorForGuestSettle(toEmails,fromEmail,"新商家清算失败:系统错误 ", "tgId:"+trxorderGoods.getId()+"msg:"+e.getMessage());
                    	logger.error("+++ executeGuestCredit Daemon for error:"+trxorderGoods.getId(),e);
                 }
                   
                }
            }
            logger.info("+++++ executeGuestCredit Daemon end:" +new Date() +",takes"+(System.currentTimeMillis()-s1));
            
        } catch (Exception e) {
            logger.error("+++++ executeGuestCredit Daemon error",e);
            
        }
    }
    
    /**
     * 报警邮件方法
     * @param toMailAry
     * @param formMail
     * @param mailTitle
     * @param mailText
     */
    public void sendMailErrorForGuestSettle(String toEmails,String formMail,String mailTitle,String mailText){
    	
		// 发送邮件报警
		try {
			String[] toMailAry = toEmails.split(",");
			for (String toEmail : toMailAry) {
				emailService.sendMail(toEmail, fromEmail,"trxGoodsId:" +mailText,mailTitle);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
