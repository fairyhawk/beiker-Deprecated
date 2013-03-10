package com.beike.core.service.trx.partner.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.biz.service.hessian.TrxHessianService;
import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.bean.trx.partner.ParTaobaoOrderGenerator;
import com.beike.common.bean.trx.partner.ParTaobaoOrderParam;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundSourceType;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.core.service.trx.RefundService;
import com.beike.core.service.trx.TrxOrderService;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.VoucherService;
import com.beike.core.service.trx.notice.NoticeService;
import com.beike.core.service.trx.partner.PartnerReqIdService;
import com.beike.core.service.trx.partner.PartnerService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.util.DateUtils;
import com.beike.util.PartnerUtil;
import com.beike.util.StringUtils;
import com.beike.util.img.JsonUtil;

/*
 * @Title: PartnerServiceImpl.java
 * @Package  com.beike.core.service.trx.parter.impl
 * @Description: 合作分销商API交易相关ServiceImpl(淘宝)
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("partnerForTBService")
public class PartnerForTBServiceImpl implements PartnerService {

	private final Log logger = LogFactory.getLog(PartnerForTBServiceImpl.class);

	@Autowired
	private TrxOrderDao trxOrderDao;
	@Autowired
	private TrxOrderService trxOrderService;
	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;

	@Autowired
	private TrxorderGoodsService trxorderGoodsService;
	@Resource(name = "trxHessianService")
	private TrxHessianService trxHessianService;
	@Autowired
	private RefundService refundService;
	@Autowired
	private TrxSoaService trxSoaService;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private VoucherService voucherService;

	@Autowired
	private PartnerReqIdService partnerReqIdService;

	@Override
	/**
	 * 检查验签、加密以及IP
	 * @param desStr   密文
	 * @param partnerNo 分销商编号
	 * @return
	 */
	public String checkHmacData(String desStr, PartnerInfo partnerInfo, String reqIp) {
		logger.info("+++++++++desStr=" + desStr + "++++++++++++reqIp=" + reqIp + "+++++++++++++");
		boolean boo = checkIp(reqIp, partnerInfo.getIp());
		if (!boo) {
			logger.info("+++++++++++++++++++IP++{ERROR}++++++++");
			return "10100";// IP受限制返回相关错误信息
		}
		String sign = "";
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		String desStrArray[] = desStr.split("&");
		for (int i = 0; i < desStrArray.length; i++) {
			String keyValue[] = desStrArray[i].split("=");
			if (keyValue[1] == null || "".equals(keyValue[1])) {
				continue;
			}
			if ("sign".equals(keyValue[0])) {
				sign = keyValue[1];
				continue;
			}
			treeMap.put(keyValue[0], keyValue[1]);
		}
		logger.info("++++++++++++++++++++++++" + treeMap + "++++++++++++++++++++");
		String desryptStr = PartnerUtil.md5SignatureSecret(treeMap, partnerInfo.getKeyValue());
		logger.info("+++++++++taobao++++++++++++partnerNo：" + partnerInfo.getPartnerNo() + "+++++++++" + desryptStr + "++++++++++++");
		if (sign.equals(desryptStr)) {
			return desStr;
		} else {
			logger.info("+++++++++++++++++++sign++{ERROR}++++++++");
			return "10100";
		}
	}

	/**
	 * taobao 请求参数转换
	 */
	@Override
	public ParTaobaoOrderParam transReqInfo(String paramInfo) {
		return ParTaobaoOrderGenerator.transReqInfo(paramInfo);

	}

	/**
	 * 响应的密文或者签名数据
	 * 
	 * @param source
	 * @param keyValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String generateRspHmac(Object source, String keyValue) {

		String rspInfo = JsonUtil.getJsonStringFromMap((Map<String, Object>) source);

		return rspInfo;

	}

	/**
	 * 订单同步接口
	 * 
	 * @param ptop
	 * @return
	 * @throws Exception
	 */
	public String synchroTrxOrder(Object ptopSource, String partnerNo) throws Exception {
		logger.info("++++++start+++++++++++synchroTrxOrder++++");
		if (ptopSource == null) {
			return "500";
		}
		StringBuilder verifyCodes = new StringBuilder();// 获取此次下单的所有凭证码
		ParTaobaoOrderParam ptop = (ParTaobaoOrderParam) ptopSource;
		List<Long> userIdList = ptop.getUserIdList();
		String outReqId = ptop.getOutRequestId();
		List<TrxorderGoods> tgList = null;
		String outRequestId = "";

		boolean isExist = partnerReqIdService.preQryInWtDBByPNoAndReqId(partnerNo, outReqId);// 先到分销商订单号表查询

		if (isExist) {// 如果存在

			TrxOrder trxOrder = trxOrderService.preQryInWtDBByUIdAndOutReqId(outReqId, userIdList);
			if (trxOrder == null) {// 如果请求方第一次请求未收到响应，然后又请求了一次，则直接返回错误（此时我侧事务还未提交）
				return "500";
			} else if (TrxStatus.INIT.equals(trxOrder.getTrxStatus())) {// 交易重试
				OrderInfo orderInfo = new OrderInfo();
				orderInfo.setTrxOrder(trxOrder);
				orderInfo.setTrxRetry(true);// 开启重试
				orderInfo.setNeedLock(false);// 不需要锁机制
				orderInfo.setNeedActHis(false);// 不需要走账
				orderInfo = trxHessianService.noTscCreateTrxOrder(orderInfo);// 调用重试
				outRequestId = trxOrder.getOutRequestId();
				tgList = trxorderGoodsService.preQryInWtDBFindByTrxId(trxOrder.getId());
			} else {
				logger.info("+++++++++++++++++synchroTrxOrder++++trxOrder-repeat=" + trxOrder.getOutRequestId() + "+++repeat++++");
				outRequestId = trxOrder.getOutRequestId();
				tgList = trxorderGoodsService.preQryInWtDBFindByTrxId(trxOrder.getId());
			}
		} else {
			TrxRequestData requestData = new TrxRequestData();
			requestData.setReqChannel(ReqChannel.PARTNER);
			requestData.setUserId(Long.valueOf(ptop.getUserId()));
			requestData.setGoodsId(ptop.getGoodsId());
			requestData.setMobile(ptop.getMobile());// 加入手机号，分销商发送此手机号
			requestData.setGoodsCount(ptop.getNum());
			OrderInfo orderInfo = null;
			orderInfo = trxSoaService.tansTrxReqData(requestData);
			orderInfo.setNeedActHis(false);
			orderInfo.setOutGoodsId(ptop.getNumIid());
			orderInfo.setNeedLock(false);// 不需要应用级别的并发锁
			orderInfo.setOutSmsTemplate(ptop.getSmsTemplate());// 分销商自有短信模板
			orderInfo.setPartnerNo(partnerNo);// 分销商编号
			orderInfo.setOutRequestId(outReqId);// 分销商外部请求订单号
			orderInfo.setOutGoodsId(ptop.getNumIid());// 分销商外部商品ID
			orderInfo.setClientIp(ptop.getClientIp());// 分销商客户端IP
			orderInfo = trxHessianService.noTscCreateTrxOrder(orderInfo);// 调用创建订单Service
			outRequestId = orderInfo.getTrxOrder().getOutRequestId();
			tgList = orderInfo.getTgList();
		}

		for (int i = 0; i < tgList.size(); i++) {
			Long voucherId = tgList.get(i).getVoucherId();
			Voucher voucher = voucherService.preQryInWtDBVoucherByid(voucherId);
			verifyCodes.append(voucher.getVoucherCode());
			verifyCodes.append(":1,");
		}
		String strCodes = verifyCodes.toString();
		strCodes = strCodes.substring(0, strCodes.length() - 1);
		TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();
		apiparamsMap.put("format", "json");
		apiparamsMap.put("method", "taobao.vmarket.eticket.send");
		apiparamsMap.put("sign_method", "md5");
		apiparamsMap.put("app_key", ptop.getAppKey());
		apiparamsMap.put("v", "2.0");
		apiparamsMap.put("session", ptop.getSession());// 他用型需要sessionkey
		String timestamp = DateUtils.getStringDate();
		apiparamsMap.put("timestamp", timestamp);
		apiparamsMap.put("order_id", outRequestId);// 平台订单号
		apiparamsMap.put("verify_codes", strCodes);// 平台商品订单号
		apiparamsMap.put("token", ptop.getToken());// 请求时获取token
		String sign = PartnerUtil.md5Signature(apiparamsMap, ptop.getNoticeKeyValue());
		apiparamsMap.put("sign", sign);// 平台商品订单号
		logger.info("++++++++++++apiparamsMap=" + apiparamsMap);
		try {
			noticeService.createNotice(ptop.getTaobaoSid(), "TAOBAO", outRequestId, apiparamsMap, "taobao.vmarket.eticket.send");
		} catch (Exception e) {
			logger.debug(e);
			e.printStackTrace();
		}

		return "200";
	}

	/**
	 * 凭证重发接口
	 * 
	 * @param ptop
	 * @return
	 * @throws Exception
	 */
	public String noTscResendVoucher(Object ptopSource) throws Exception {

		if (ptopSource == null) {
			return "500";
		}

		ParTaobaoOrderParam ptop = (ParTaobaoOrderParam) ptopSource;
		List<Long> userIdList = ptop.getUserIdList();
		TrxOrder trxOrder = trxOrderDao.findByUserIdOutRequestId(ptop.getOutRequestId(), userIdList);
		if (trxOrder == null) {
			return "500";// 无此订单
		} else {
			Long trxorderId = trxOrder.getId();
			String mobile = ptop.getMobile();
			// String goodsId = ptop.getGoodsId();
			List<TrxorderGoods> tgList1 = trxorderGoodsDao.findByTrxId(trxOrder.getId());
			// List<TrxorderGoods> tgList =
			// trxorderGoodsDao.findInTrxId(trxorderId.toString(), goodsId);
			if (mobile == null || mobile.length() == 0) {// 手机号为空不重发
				return "500";
			}
			List<TrxorderGoods> tgList = new ArrayList<TrxorderGoods>();
			for (int i = 0; i < tgList1.size(); i++) {
				TrxorderGoods tog = tgList1.get(i);
				if(tog.getTrxStatus().equals(TrxStatus.SUCCESS)){
					tgList.add(tog);
				}
			}
			
			for (int i = 0; i < tgList.size(); i++) {
				TrxorderGoods tog = tgList.get(i);
				// trxHessianService调用凭证重发接口
				TrxRequestData requestData = new TrxRequestData();
				requestData.setMobile(mobile);
				requestData.setTrxorderGoodsId(tog.getId());
				requestData.setVerifyForTg(false);// 对于商品订单是否需要鉴权.上步已做鉴权操作。避免多次查询
				requestData.setReqChannel(ReqChannel.PARTNER);
				requestData.setOutSmsTemplate(ptop.getSmsTemplate());
				try {
					trxHessianService.reSendVoucher(requestData);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			StringBuilder verifyCodes = new StringBuilder();// 获取此次下单的所有凭证码
			for (int i = 0; i < tgList.size(); i++) {
				Long voucherId = tgList.get(i).getVoucherId();
				Voucher voucher = voucherService.preQryInWtDBVoucherByid(voucherId);
				verifyCodes.append(voucher.getVoucherCode());
				verifyCodes.append(":1,");
			}
			String strCodes = verifyCodes.toString();
			strCodes = strCodes.substring(0, strCodes.length() - 1);
			// 更新trxOrder手机号
			trxOrderDao.updateMobileById(trxorderId, mobile);

			TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();
			apiparamsMap.put("format", "json");
			apiparamsMap.put("method", "taobao.vmarket.eticket.resend");
			apiparamsMap.put("sign_method", "md5");
			apiparamsMap.put("app_key", ptop.getAppKey());
			apiparamsMap.put("v", "2.0");
			apiparamsMap.put("session", ptop.getSession());// 他用型需要sessionkey
			String timestamp = DateUtils.getStringDate();
			apiparamsMap.put("timestamp", timestamp);
			apiparamsMap.put("order_id", trxOrder.getOutRequestId());// 平台订单号
			apiparamsMap.put("verify_codes", strCodes);// 平台商品订单号
			apiparamsMap.put("token", ptop.getToken());// token
			String sign = PartnerUtil.md5Signature(apiparamsMap, ptop.getNoticeKeyValue());
			apiparamsMap.put("sign", sign);// 平台商品订单号
			logger.info("++++++++++++apiparamsMap=" + apiparamsMap);

			noticeService.createNotice(ptop.getTaobaoSid(), "TAOBAO", trxOrder.getOutRequestId(), apiparamsMap, "taobao.vmarket.eticket.resend");// ***此参数需要和王伟杰沟通
		}

		return "200";
	}

	/**
	 * 退款接口
	 * 
	 * @param ptop
	 * @return
	 */
	@Override
	public String processTrxOrder(Object ptopSource) throws Exception {
		logger.info("++++++++++++processTrxOrder+++++++++start+++++++++");
		if (ptopSource == null) {
			return "500";
		}
		ParTaobaoOrderParam ptop = (ParTaobaoOrderParam) ptopSource;
		List<Long> userIdList = ptop.getUserIdList();
		TrxOrder trxOrder = trxOrderService.preQryInWtDBByUIdAndOutReqId(ptop.getOutRequestId(), userIdList);
		if (trxOrder == null) {
			return "500";// 无此订单
		} else {
			// Long userId = trxOrder.getUserId();
			List<TrxorderGoods> tgList = trxorderGoodsService.preQryInWtDBFindByTrxId(trxOrder.getId());
			// List<TrxorderGoods> tgList =
			// trxorderGoodsDao.findInTrxId(trxOrder.getId().toString(),
			// ptop.getGoodsId().toString());

			for (int i = 0; i < tgList.size(); i++) {
				TrxorderGoods tog = tgList.get(i);
				Long trxGoodsId = tog.getId();
				TrxStatus status = tog.getTrxStatus();
				if (TrxStatus.REFUNDTOACT.equals(status)) {
					return "200";
				}
				refundService.processApplyForRefundToAct(trxGoodsId, "分销商(淘宝)", RefundSourceType.PARTNER, RefundHandleType.AUTO, "分销商接口申请退款");

				tog = refundService.processToAct(trxGoodsId, "系统", RefundSourceType.PARTNER, RefundHandleType.AUTO, "分销商接口申请退款");

			}
		}

		return "200";
	}

	/**
	 * 根据分销商订单号查询分销商订单信息
	 * 
	 * @param userId
	 * @param outRequestId
	 * @return
	 */
	public String findTrxorderByPartnerNo(List<Long> userIdList, String outRequestId) {
		return null;
	}

	@Override
	public String findVouInfoByActiveDate(String partnerNo, Date startTime, Date endTime, String trxStatusStr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findVouInfoByLastUpdateDate(String partnerNo, Date startTime, Date endTime, String trxStatus) {
		// TODO Auto-generated method stub
		return null;
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
	public String findVouInfoByVouId(String partnerNo, String voucherId) {
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