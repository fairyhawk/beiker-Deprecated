package com.beike.core.service.trx.partner.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.biz.service.hessian.TrxHessianService;
import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.bean.trx.partner.Par360buyOrderGenerator;
import com.beike.common.bean.trx.partner.Par360buyOrderParam;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundSourceType;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.core.service.trx.RefundService;
import com.beike.core.service.trx.TrxOrderService;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.partner.PartnerBindVoucherService;
import com.beike.core.service.trx.partner.PartnerReqIdService;
import com.beike.core.service.trx.partner.PartnerService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.entity.partner.PartnerBindVoucher;
import com.beike.util.StringUtils;

/**   
 * @title: PartnerFor360buyServiceImpl.java
 * @package com.beike.core.service.trx.partner.impl
 * @description: 
 * @author wangweijie  
 * @date 2012-8-29 下午02:50:44
 * @version v1.0   
 */
@Service("partnerFor360buyService")
public class PartnerFor360buyServiceImpl implements PartnerService {

	private static final Log logger = LogFactory.getLog(PartnerFor360buyServiceImpl.class);
	
	@Autowired
	private TrxOrderDao trxOrderDao;
	@Autowired
	private TrxOrderService trxOrderService;
	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;
	@Autowired
	private TrxorderGoodsService trxorderGoodsService;
	
	@Resource(name="trxHessianService")
	private TrxHessianService trxHessianService;
	@Autowired
	private RefundService refundService;
	@Autowired
	private TrxSoaService trxSoaService;

	@Autowired
	private PartnerReqIdService partnerReqIdService;
	
	@Autowired
	private PartnerBindVoucherService partnerBindVoucherService;
	
	/**
	 * 校验京东传入报文
	 */
	@Override
	public String checkHmacData(String sourceMessage, PartnerInfo partnerInfo, String reqIp) {
		logger.info("++++++++++++reqIp=" + reqIp + "+++++++++++++");
		String legalIP = StringUtils.toTrim(partnerInfo.getIp());
		boolean boo = checkIp(reqIp, legalIP);
		if (!boo) {
			logger.info("+++++++++++++360buy..IP++{ERROR}++++++++");
			return "10100";	//身份验证失败---IP受限制返回相关错误信息
		}
		
		//解析京东报文--Map
		try {
			return Par360buyOrderGenerator.get360buyDataMessage(sourceMessage,partnerInfo.getPartnerNo(),partnerInfo.getSessianKey(),partnerInfo.getKeyValue());
		} catch (Exception e) {
			logger.error("handle 360buy error:",e);
			e.printStackTrace();
			return "10100";	//接口调用异常
		}
	}

	@Override
	public String findVouInfoByActiveDate(String partnerNo, Date startTime, Date endTime, String trxStatusStr) {
		return null;
	}

	@Override
	public String findVouInfoByLastUpdateDate(String partnerNo, Date startTime, Date endTime, String trxStatus) {
		return null;
	}

	@Override
	public String findVouInfoByVouId(String partnerNo, String voucherId) {
		return null;
	}
	
	/**
	 * 获得响应报文
	 */
	@Override
	public String generateRspHmac(Object source, String key) {
		
		Par360buyOrderParam resParam = (Par360buyOrderParam)source;
		return Par360buyOrderGenerator.packageResponseMsg(resParam,key);
	}

	@Override
	public String processTrxOrder(Object ptop) throws Exception {
		Par360buyOrderParam par360param =(Par360buyOrderParam) ptop;
		String jdOrderId = par360param.getJdOrderId(); 		//京东订单号
		String trxOrderId=par360param.getVenderOrderId();	//合作伙伴订单ID
		String isAllBack = par360param.getIsAllBack();		//是否该笔订单全退 
		
		String reason = "京东退款请求";
		//判断订单id是否是京东的订单
		TrxOrder trxorder = trxOrderDao.findById(Long.parseLong(trxOrderId));
		List<Long> userIdList = par360param.getUserIdList();
		if(!userIdList.contains(trxorder.getUserId())){
			throw new Exception("illegal——360buy order orderId=" + trxOrderId);
		}

		List<TrxorderGoods> trxOrderGoodsList;		
		
		//
		List<PartnerBindVoucher> pbvList = partnerBindVoucherService.preQryInWtDBByPartnerBindVoucherList(par360param.getPartnerNo(), jdOrderId);
		Set<String> couponSet = par360param.getCouponMap().keySet();
		Map<String,String> bothSidesCouponMap = new LinkedHashMap<String,String>();
		for(PartnerBindVoucher pbv : pbvList){
			if(couponSet.contains(pbv.getOutCouponId())){
				bothSidesCouponMap.put(pbv.getTrxGoodsSn(), pbv.getOutCouponId());
			}
		}
		Set<String> goodsSnSet = bothSidesCouponMap.keySet();

		//如果该笔订单全退--根据订单号查询 商品订单
		if("true".equalsIgnoreCase(isAllBack)){
			logger.info("++++++++++++++360buy fefund by trxOrderId=" + trxOrderId );		//记录日志
			trxOrderGoodsList = trxorderGoodsDao.findByTrxId(trxorder.getId());
			
			//如果京东要退款的优惠券数量和千品平台的数量不相等。则返回数据不匹配错误
			if(trxOrderGoodsList.size() != par360param.getCouponMap().size()){
				throw new Exception("++++++++++++++++++++360buy refund coupon's size not equals qianpin ordergoods's size[qianpin.size="+trxOrderGoodsList.size()+"360buy.size="+par360param.getCouponMap().size()+"]");
			}else{
				
				for(TrxorderGoods trxorderGoods : trxOrderGoodsList){
					
					//如果商品订单列表中 有京东coupons字段中不存在的商品订单号，则抛出异常
					if(!goodsSnSet.contains(trxorderGoods.getTrxGoodsSn())){
						throw new Exception("++++++++++++++++++++360buy refund coupon's not exist [trxOrderId="+trxorder.getId()+";trxGoodsSn="+trxorderGoods.getTrxGoodsSn()+"]");
					}
				}
			}
		}
		
		//根据商品订单号退款
		else{
			trxOrderGoodsList = new ArrayList<TrxorderGoods>();
			 	
			//			
			logger.info("++++++++++++++360buy fefund++++++trxGoodsSn=" + goodsSnSet );		//记录日志
			for(String goodsSn : goodsSnSet){
				TrxorderGoods trxOrderGoods = trxorderGoodsDao.findBySn(StringUtils.toTrim(goodsSn));
				
				//必须是该笔订单下的商品订单号
				if(null != trxOrderGoods && (trxOrderGoods.getTrxorderId().intValue() == trxorder.getId().intValue())){
					trxOrderGoodsList.add(trxOrderGoods);
				}else{
					logger.error("++++++++++++++360buy fefund+{ERROR}{NOT FOUND}+++++trxGoodsSn=" + goodsSnSet );		//记录日志
				}
			}
			
			//如果京东要退款的优惠券数量和千品平台的数量不相等。则返回数据不匹配错误
			if(trxOrderGoodsList.size() != par360param.getCouponMap().size()){
				throw new Exception("++++++++++++++++++++360buy refund coupon's size not equals qianpin ordergoods's size[qianpin.size="+trxOrderGoodsList.size()+";360buy.size="+par360param.getCouponMap().size()+"]");
			}
		}
				
		for(TrxorderGoods trxOrderGoods : trxOrderGoodsList){
			if(trxOrderGoods.getTrxorderId().equals(Long.valueOf(trxOrderId))){
				
				//判断是否已经退款成功
				if(TrxStatus.REFUNDTOACT.equals(trxOrderGoods.getTrxStatus())){
					logger.info("++++++++++360buy refund+++++++trxGoodsSn="+trxOrderGoods.getTrxGoodsSn()+" has been refunded...");
					continue;
				}
				
				//对订单进行退款
				refundService.processApplyForRefundToAct(trxOrderGoods.getId(), "分销商",RefundSourceType.PARTNER, RefundHandleType.MANUAL,reason);
				refundService.processToAct(trxOrderGoods.getId(), "分销商",RefundSourceType.PARTNER, RefundHandleType.MANUAL,reason);
				logger.info("++++++++++360buy refund+++++++trxGoodsSn="+trxOrderGoods.getTrxGoodsSn()+" refunded success...");
			}
		}
		
		StringBuffer responseData = new StringBuffer();
		//申请退款 data返回报文
		responseData.append("<Message xmlns=\"http://tuan.360buy.com/SendOrderRefundResponse\">");
			responseData.append("<JdOrderId>"+StringUtils.toTrim(par360param.getJdOrderId())+"</JdOrderId>");		//京东订单ID
			responseData.append("<VenderOrderId>"+StringUtils.toTrim(par360param.getVenderOrderId())+"</VenderOrderId>");		//合作伙伴订单ID
			
			responseData.append("<Coupons>");
			for(String coupon : goodsSnSet){
				responseData.append("<Coupon>"+bothSidesCouponMap.get(coupon)+"</Coupon>");//券号
			}
			responseData.append("</Coupons>");
			
		responseData.append("</Message>");
		
		return responseData.toString();
	}

//	@Override
//	public String resendVoucher(Object ptop) throws Exception {
//		return null;
//	}

	@Override
	public String synchroTrxOrder(Object ptopSource, String partnerNo) throws Exception {
		Par360buyOrderParam par360param =(Par360buyOrderParam) ptopSource;

		List<Long> userIdList = par360param.getUserIdList();
		String outReqId= par360param.getJdOrderId();	//京东订单ID--已经转换为元
		String payPrice= par360param.getTeamPrice();	//订单售价 -- 已经转换为元
		String clientIp=par360param.getClientIp();//客户端IP
		
		//验证数据有效性
		double payPriceDou=Double.parseDouble(payPrice);
		if(payPriceDou<0){
			throw new Exception("++++360buy+++payPrice  must be > 0 ");
		}
		if(par360param.getCouponMap().size() != Integer.parseInt(par360param.getCount())){
			throw new Exception("++++360buy+++count not equals coupons number ");
		}
		
		
		List<Map<String, Object>>  listMap =null;
		TrxOrder trxOrder = null;
		boolean isExist=partnerReqIdService.preQryInWtDBByPNoAndReqId(partnerNo, outReqId);//先到分销商订单号表查询
		if (isExist) {// 如果存在
			trxOrder = trxOrderService.preQryInWtDBByUIdAndOutReqId(outReqId,userIdList);
			if (trxOrder == null) {
				logger.error("++++++++++++++not found any order in db...++++");
				throw new Exception("not found any order in db...");
			}else if(TrxStatus.INIT.equals(trxOrder.getTrxStatus())){//交易重试
				OrderInfo  orderInfo=new OrderInfo();
				orderInfo.setTrxOrder(trxOrder);
				orderInfo.setTrxRetry(true);//开启重试
				orderInfo.setNeedLock(false);//不需要锁机制
				orderInfo.setNeedActHis(false);//不需要走账
				orderInfo = trxHessianService.noTscCreateTrxOrder(orderInfo);//调用重试
				listMap = trxorderGoodsService.preQryInWtDBByByTrxId(trxOrder.getId());
				logger.info("++++++++++++++++listMap:++" + listMap);
			}else{
				listMap = trxorderGoodsService.preQryInWtDBByByTrxId(trxOrder.getId());
				logger.info("++++++++++++++++listMap:++" + listMap);
			}
		}else{
			TrxRequestData requestData = new TrxRequestData();
			requestData.setReqChannel(ReqChannel.PARTNER);
			requestData.setMobile(par360param.getMobile());// 加入手机号，分销商发送此手机号
			requestData.setUserId(par360param.getUserId());
			requestData.setGoodsId(par360param.getVenderTeamId()); // 合作伙伴团购ID
			requestData.setGoodsCount(par360param.getCount()); // 订购数量
			requestData.setUseEndDateComLose(true);// 使用下架时间作为下单时间计算过期时间
			requestData.setUseOutPayPrice(true);// 使用外部支付价格
			requestData.setPayPrice(payPrice);
			OrderInfo orderInfo = trxSoaService.tansTrxReqData(requestData);
			orderInfo.setNeedActHis(false);
			orderInfo.setOutRequestId(par360param.getJdOrderId()); // 京东订单ID
			orderInfo.setNeedLock(false);// 不需要应用级别的并发锁
			orderInfo.setPartnerNo(partnerNo);//分销商编号
			orderInfo.setOutRequestId(outReqId);//分销商外部请求订单号
			orderInfo.setOutGoodsId(par360param.getJdTeamId()); // 京东团购ID（京东商品ID）
			orderInfo.setClientIp(clientIp);//分销商IP
			logger.info("+++++++++create new partner order:[ReqChannel="+requestData.getReqChannel()+",Mobile="+requestData.getMobile()+",UserId="+requestData.getUserId()+
					",GoodsId="+requestData.getGoodsId()+",GoodsCount="+requestData.getGoodsCount()+",OutRequestId="+orderInfo.getOutRequestId()+",OutGoodsId="+orderInfo.getOutGoodsId()+"]");
			
			orderInfo = trxHessianService.noTscCreateTrxOrder(orderInfo);// 调用创建订单Service
			
			trxOrder = orderInfo.getTrxOrder();
			listMap = trxorderGoodsService.preQryInWtDBByByTrxId(trxOrder.getId());

			logger.info("++++++++++++listMap=" + listMap);
			
			logger.info("+++++++++++360buy and to partner_bind_voucher+++++jd.size="+par360param.getCouponMap().size() +"+++qp.size="+listMap.size());
			List<PartnerBindVoucher> pbvList = new ArrayList<PartnerBindVoucher>();
			
			//如果此种情况发生则需要手工处理。。
			if(par360param.getCouponMap().size() != listMap.size()){
				throw new Exception("++++360buy${ERROR}{ERROR}+++++jd.size not eq qp.size:"+par360param.getCouponMap().size() +" = "+ listMap.size());
			}else{
				int i=0;
				for(Entry<String,String> couponInfo : par360param.getCouponMap().entrySet()){
					Map<String,Object> orderGoodsMap = listMap.get(i);
					PartnerBindVoucher pbv = new PartnerBindVoucher();
					pbv.setTrxOrderId(trxOrder.getId());		//订单ID
					pbv.setTrxGoodsId((Long)orderGoodsMap.get("trxGoodsId"));		//商品订单ID
					pbv.setVoucherId((Long)orderGoodsMap.get("ticketId"));	//凭证id
					pbv.setPartnerNo(partnerNo);		//分销商编号
					pbv.setOutRequestId(trxOrder.getOutRequestId()); //外部请求
					pbv.setTrxGoodsSn((String)orderGoodsMap.get("ticketCode"));	//商品订单号
					pbv.setVoucherCode((String)orderGoodsMap.get("ticketPass")); //千品凭证号
					pbv.setOutCouponId(couponInfo.getKey());	//分销商券号
					pbv.setOutCouponPwd(couponInfo.getValue()); //分销商券密码
					pbvList.add(pbv);
					i++;
				}
			}
			partnerBindVoucherService.savePartnerVouchers(pbvList);
		}
		
		
		List<PartnerBindVoucher> qbvList = partnerBindVoucherService.preQryInWtDBByPartnerBindVoucherList(partnerNo,trxOrder.getOutRequestId());
		if(qbvList.size() != par360param.getCouponMap().size()){
			throw new Exception("++++360buy${ERROR}{ERROR}+++++jd.size not eq qp_partner_voucher.size:"+par360param.getCouponMap().size() +" = "+ qbvList.size());
		}
		
		StringBuffer resData = new StringBuffer("");
		
		//订单同步 data返回报文
			
		resData.append("<Message xmlns=\"http://tuan.360buy.com/SendOrderResponse\">");
			resData.append("<JdTeamId>"+StringUtils.toTrim(par360param.getJdTeamId())+"</JdTeamId>");		//京东团购ID
			resData.append("<VenderTeamId>"+par360param.getVenderTeamId()+"</VenderTeamId>");		//合作伙伴团购ID
			resData.append("<SellCount>"+listMap.size()+"</SellCount>");		//购买数量
			resData.append("<VenderOrderId>"+trxOrder.getId()+"</VenderOrderId>");		//合作伙伴方订单ID
			
			resData.append("<Coupons>");
			for(Entry<String,String> couponInfo : par360param.getCouponMap().entrySet()){
				for(PartnerBindVoucher pbv : qbvList){
					if(pbv.getOutCouponId().equals(couponInfo.getKey())){
						resData.append("<Coupon>");
							resData.append("<CouponId>"+pbv.getTrxGoodsSn()+"</CouponId>");//券号
							resData.append("<CouponPwd>"+pbv.getVoucherCode()+"</CouponPwd>");//券密码
						resData.append("</Coupon>");
						
						qbvList.remove(pbv);
						break;
					}
				}
			}
			resData.append("</Coupons>");
		resData.append("</Message>");
		
		return resData.toString();
	}

	/**
	 * 报文解析
	 */
	@Override
	public Object transReqInfo(String paramInfo){
		return Par360buyOrderGenerator.transReqInfo(paramInfo);
	}

	
	/**
	 * ip 白名单检测（如果平台没有配置，则直接放行）
	 * 
	 * @param reqIp
	 * @param legalIP
	 * @return
	 */
	public boolean checkIp(String reqIp, String legalIP) {
		boolean boo = StringUtils.checkIp(reqIp, legalIP);
		return boo;

	}

	@Override
	public String noTscResendVoucher(Object ptop) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String checkHmacData(String desStr, String publicKey, String sign, PartnerInfo partnerInfo, String partnerIP) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.beike.core.service.trx.partner.PartnerService#findTrxorder(java.lang.Object, java.lang.String)
	 */
	@Override
	public String findTrxorder(Object ptopSource, String partnerNo) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.beike.core.service.trx.partner.PartnerService#checkHmacData(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String checkHmacData(String params, String publicKey, String privateKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findVoucher(Object ptop, String partnerNo) throws Exception{
		return null;
	}

}