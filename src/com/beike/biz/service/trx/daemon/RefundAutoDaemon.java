package com.beike.biz.service.trx.daemon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.PartnerApiType;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.ProviderType;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundSourceType;
import com.beike.common.enums.trx.RefundStatus;
import com.beike.core.service.trx.RefundService;
import com.beike.core.service.trx.TrxOrderService;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.partner.PartnerCommonService;
import com.beike.service.common.EmailService;
import com.beike.util.DateUtils;
import com.beike.util.PropertiesReader;
import com.beike.util.TrxConstant;

/**
 * @Title: VoucherAutoCreateDaemonService.java
 * @Package com.beike.biz.service.trx
 * @Description: 定时退款
 * @date May 30, 2011 6:25:08 AM
 * @author wh.cheng
 * @version v1.0
 */
@Service("refundAutoDaemon")
public class RefundAutoDaemon {

	private final Log logger = LogFactory.getLog(VoucherAutoCreateDaemon.class);
	@Autowired
	private RefundService refundService;

	@Autowired
	private TrxorderGoodsService trxorderGoodsService;
	@Autowired
	private TrxOrderService trxOrderService;
	@Autowired
	private PartnerCommonService partnerCommonService;
	
	@Autowired
	private EmailService emailService ;
	
	@Resource(name = "webClient.trxHessianServiceGateWay")
    private TrxHessianServiceGateWay trxHessianServiceGateWay;
	 private static final String toEmail =  PropertiesReader.getValue("project","refund_toer");
	 
	public void executeAutoRefund() {

		logger.info("++++++++++++++++start auto refund  time:" + new Date()+ "++++++++++++");
		int notifyCount = trxorderGoodsService.qryLoseListByIsRefundCount(new Date(), true);
		int leng = 0;
		int daemonLength = TrxConstant.DAENON_LENGTH;
		if(notifyCount>0){
			leng = (notifyCount+daemonLength)/daemonLength;
		
		}else{
			return;
		}
		for(int i=0;i<leng;i++){
			int start = i*daemonLength;
		
		// 取出过期的订单且支持退款的商品订单
		List<TrxorderGoods> trxorderGoodsList = trxorderGoodsService.qryLoseListByIsRefund(new Date(), true,start,daemonLength);

		if (trxorderGoodsList == null || trxorderGoodsList.size() == 0) {
			return;
		}

		// 调用退款申请
		for (TrxorderGoods item : trxorderGoodsList) {
			TrxOrder trxorder = trxOrderService.findById(item.getTrxorderId());
			logger.info("+++++++start auto refund->item+++++++++tgId:"+item.getId()+"++++++");
			Long userId = trxorder.getUserId();
			logger.info("+++++++start auto refund->item+++++++++userId:"+userId+"++++++++++");
			PartnerInfo partnerInfo = partnerCommonService.qryParterByUserIdInMem(userId);
			if(partnerInfo!=null){
				//淘宝支持千品侧自动退款
				if(PartnerApiType.TAOBAO.name().equals(partnerInfo.getApiType())){
					try {
						//退款申请
						refundService.processApplyForRefundToAct(item.getId(), "系统自动",
								RefundSourceType.TIMING, RefundHandleType.AUTO,"订单过期，系统自动退款申请");
						
						// 进行退款
						refundService.processToAct(item.getId(), "系统自动",
								RefundSourceType.TIMING, RefundHandleType.AUTO,"订单过期，系统自动退款处理");
						logger.info("+++++++start auto refund->item+++++tgId:"+item.getId()+"++++auto refund suc!++");
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e);

					}
					}
			}
			

		}
		}
	}
	
	/**
	 * 超过3天退款申请处理
	 * 1、原路返回申请超3天自动执行退款，每天凌晨3点执行
       2、支付宝超过3个月，都要发邮件（支付宝退款3个月过期时间不是很精确，误差为几分钟）
       3、老支付宝无线账号，退款就要发邮件。支付宝新账号上线时间为（2012-07-23之后几天），因为老账号不支持无线交易退款
	 */
	public void executeRefundTimeout(){
	    logger.info("++++++++ executeRefundTimeout start"+new Date());
        Long currentTime =System.currentTimeMillis();
        String refundDate_3=DateUtils.getTimeBeforeORAfter(-3);
        executeRefundToBankOutTime3Days(refundDate_3);//原路返回超3天
        
        String refundAlipayDate=DateUtils.getTimeBeforeORAfter(-90);
        String refundUopoDate=DateUtils.getTimeBeforeORAfter(-180);
        
        String todayBef = DateUtils.getTimeBeforeORAfter(-1);
        Date dateBef=DateUtils.toDate(todayBef, "yyyy-MM-dd HH:mm:ss");
        String startDate= DateUtils.toString(dateBef,"yyyy-MM-dd 00:00:00");
        String endDate =DateUtils.toString(dateBef,"yyyy-MM-dd 59:59:59");
        
        
        executeRefundToBankOutTime3Month(startDate,endDate,refundAlipayDate);//支付宝超90天
        executeRefundToBankOutTime6Month(startDate,endDate,refundUopoDate);//银联超180天
        
        logger.info("++++++++ executeRefundTimeout end takes "+(System.currentTimeMillis()-currentTime)+" ms ");
	}
	
	/**
	 * 原路返回超过3天的执行自动退款
	 * @param date
	 */
	public void executeRefundToBankOutTime3Days(String date) {
        try {
            List<TrxorderGoods> list =refundService.getRefundRecheckTrxgoods(date);
            //执行自动退款
            if(list!=null && list.size()>0){
                logger.info("++++++++ executeRefundTimeout  OutTime3Days size:"+list.size());
                Map<String, String> map= null;
                for(TrxorderGoods trxorderGoods :list){
                	try {
                		logger.info("++++++++ executeRefundTimeout  OutTime3Days TrxorderGoodsID:"+trxorderGoods.getId()+"+++trxGoodsSn:"+trxorderGoods.getTrxGoodsSn());
                		Map<String, String> sourceMap = new HashMap<String, String>();
                		sourceMap.put("reqChannel", "WEB");
                		sourceMap.put("trxGoodsId", String.valueOf(trxorderGoods.getId()));// 商品订单id
                		sourceMap.put("operator", "systemdaemon");// 操作人
                		sourceMap.put("description","系统定时调用");// 描述
                        map= trxHessianServiceGateWay.refundToBank(sourceMap);
                         logger.info("executeRefundTimeout  OutTime3Days map:"+map);
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("executeRefundTimeout  OutTime3Days refundToBank erroe",e);
                    }
                    
                }
            }else{
                logger.info("++++++++ executeRefundTimeout OutTime3Days no data ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("++++++++  executeRefundTimeout error OutTime3Days ",e);
        }
    }
	/**
	 * 银联超过6个月的发邮件
	 * @param date
	 */
    public void executeRefundToBankOutTime6Month(String startDate,String endDate,String payDate) {
        try {
            List<Map<String, Object>> list = refundService.getRefundtoBankTimeOutUPOP(PaymentType.PAYCASH,ProviderType.UPOP,RefundStatus.REFUNDTOACT,startDate,endDate,payDate);
            if(list!=null && list.size()>0){
                StringBuilder builder = new StringBuilder();
                logger.info("++++++++ executeRefundTimeout  ToBankOutTime6Month size:"+list.size());
                for(Map<String, Object> refundRecord :list){
                    builder.append("<tr>");
                    builder.append("<td>").append(refundRecord.get("ruddetail_id")).append("</td> ");//退款明细ID
                    builder.append("<td>").append(refundRecord.get("trxorder_id")).append("</td> ");//订单ID
                    builder.append("<td> ").append(refundRecord.get("user_id")).append("</td>");//用户ID
                    builder.append("<td>").append(refundRecord.get("trx_goods_id")).append("</td> ");//商品订单ID
                    builder.append("<td>").append(refundRecord.get("operator")).append("</td>");//操作人
                    builder.append("<td>").append(refundRecord.get("order_amount")).append("</td>");//交易订单金额
                    builder.append("<td>").append(refundRecord.get("trx_goods_amount")).append("</td>");//商品订单金额
                    builder.append("<td>").append(refundRecord.get("amount")).append("</td>");//退款金额
                    builder.append("<td>").append(refundRecord.get("payment_amount")).append("</td>");//支付金额
                    builder.append("<td>").append(refundRecord.get("pro_external_id")).append("</td>");// 交易流水号
                    builder.append("<td>").append(refundRecord.get("pro_refund_request_id")).append("</td></tr>");//退款请求号
                    logger.info("++++++++ executeRefundTimeout ToBankOutTime6Month ruddetail_id:"+refundRecord.get("ruddetail_id"));
                }
                try {
                    // 设置动态参数
                    Object[] emailParams =  new Object[] { "退款申请银联支付超180天:",builder.toString() };
                    String [] toemails=toEmail.split(",");
                     // 邮件模板参数未设置
                    for(String toer:toemails){
                        emailService.send(null, null, null, null, null, "退款申请银联超180天["+DateUtils.getNowDateShort()+"]", new String[]{toer}, null, null,
                                new Date(), emailParams,"REFUND_TIMEOUT_AUTOEMAIL");
                    }
                    logger.info("executeRefundTimeout ToBankOutTime6Month sendmail success");
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("executeRefundTimeout ToBankOutTime6Month sendmail error");
                }
                
            }else{
                logger.info("++++++++ executeRefundTimeout ToBankOutTime6Month no data ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("++++++++  executeRefundTimeout error ToBankOutTime6Month ",e);
        }
    }
    /**
     * 支付宝超过3个月的发邮件
     * @param date
     */
    public void executeRefundToBankOutTime3Month(String startDate,String endDate,String payDate) {
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date2= dateFormat.parse("2012-10-12 00:00:00");//此日期之后的无需发邮件。
            if(dateFormat.parse(payDate).after(date2)){
                logger.info("++++++++ executeRefundTimeout ToBankOutTime3Month return ");
                return;
            }
            List<Map<String, Object>> list = refundService.getRefundtoBankTimeOutAlipay(PaymentType.PAYCASH,ProviderType.ALIPAY,RefundStatus.REFUNDTOACT,startDate,endDate,payDate);
            
            if(list!=null && list.size()>0){
                
                StringBuilder builder = new StringBuilder();
                for(Map<String, Object> refundRecord :list){
                    logger.info("++++++++ executeRefundTimeout ToBankOutTime3Month refundDetailId:"+refundRecord.get("ruddetail_id"));
                    builder.append("<td>").append(refundRecord.get("ruddetail_id")).append("</td> ");//退款明细ID
                    builder.append("<td>").append(refundRecord.get("trxorder_id")).append("</td> ");//订单ID
                    builder.append("<td> ").append(refundRecord.get("user_id")).append("</td>");//用户ID
                    builder.append("<td>").append(refundRecord.get("trx_goods_id")).append("</td> ");//商品订单ID
                    builder.append("<td>").append(refundRecord.get("operator")).append("</td>");//操作人
                    builder.append("<td>").append(refundRecord.get("order_amount")).append("</td>");//交易订单金额
                    builder.append("<td>").append(refundRecord.get("trx_goods_amount")).append("</td>");//商品订单金额
                    builder.append("<td>").append(refundRecord.get("amount")).append("</td>");//退款金额
                    builder.append("<td>").append(refundRecord.get("payment_amount")).append("</td>");//支付金额
                    builder.append("<td>").append(refundRecord.get("pro_external_id")).append("</td>");// 交易流水号
                    builder.append("<td>").append(refundRecord.get("pro_refund_request_id")).append("</td></tr>");//退款请求号
                }
                logger.info("++++++++ executeRefundTimeout  ToBankOutTime3Month size:"+list.size());
                try {
                    // 设置动态参数
                    Object[] emailParams = new Object[] { "退款申请支付宝超90天:",builder.toString()};
                    String [] toemails=toEmail.split(",");
                    //邮件模板参数未设置
                    for (String toer:toemails){
                        emailService.send(null, null, null, null, null,
                                "退款申请支付宝超90天["+DateUtils.getNowDateShort()+"]", new String[]{toer}, null, null,
                                new Date(), emailParams,
                                "REFUND_TIMEOUT_AUTOEMAIL");
                    }
                    logger.info("executeRefundTimeout ToBankOutTime3Month sendmail success");
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("executeRefundTimeout ToBankOutTime3Month sendmail error");
                }
                
            }else{
                logger.info("++++++++ executeRefundTimeout ToBankOutTime3Month no data ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("++++++++  executeRefundTimeout error ToBankOutTime3Month ",e);
        }
    }
    
    
}
