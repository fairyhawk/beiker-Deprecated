package com.beike.core.service.trx.partner.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.biz.service.hessian.TrxHessianService;
import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.bean.trx.partner.ParErrorMsgUtil;
import com.beike.common.bean.trx.partner.ParT800OrderGenerator;
import com.beike.common.bean.trx.partner.ParT800OrderParam;
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
import com.beike.core.service.trx.partner.PartnerReqIdService;
import com.beike.core.service.trx.partner.PartnerRtnPoinService;
import com.beike.core.service.trx.partner.PartnerService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.dao.trx.VoucherDao;
import com.beike.dao.trx.partner.PartnerRtnPointDao;
import com.beike.util.Amount;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.PartnerUtil;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;
import com.beike.util.img.JsonUtil;

/**
 * Title : PartnerFor800ServiceImpl.java <br/>
 * Description : 团800分销商服务实现类<br/>
 * Company : Sinobo <br/>
 * Copyright : Copyright (c) 2010-2012 All rights reserved.<br/>
 * Created : 2012-11-6 下午2:35:04 <br/>
 * 
 * @author Wenzhong Gu
 * @version 1.0
 */
@Service(value = "partnerForT800Service")
public class PartnerForT800ServiceImpl implements PartnerService {

	private final Log logger = LogFactory.getLog(PartnerForT800ServiceImpl.class);

	private static PropertyUtil propertyUtil = PropertyUtil
	.getInstance(Constant.PROPERTY_FILE_NAME);
	private String amountType = propertyUtil.getProperty("tuan800_rtnpoint_amount");
	private String tagType = propertyUtil.getProperty("tuan800_rtnpoint_tag");
	@Autowired
	private PartnerRtnPoinService partnerRtnPoinService;

//	@Autowired
//	private PartnerCommonService partnerCommonService;
	@Autowired
	private PartnerReqIdService partnerReqIdService;
	@Autowired
	private TrxOrderService trxOrderService;
	@Resource(name = "trxHessianService")
	private TrxHessianService trxHessianService;
	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;
	@Autowired
	private TrxorderGoodsService trxorderGoodsService;
	@Autowired
	private TrxSoaService trxSoaService;
	@Autowired
	private VoucherService voucherService;
//	@Autowired
//	private NoticeSendService noticeSendService;
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private TrxOrderDao trxOrderDao;

	@Autowired
	private RefundService refundService;
	@Autowired
	private PartnerRtnPointDao partnerRtnPointDao;

	/**
	 * 检查验签、加密以及IP
	 * 
	 * @param desStr
	 *            密文
	 * @param partnerInfo
	 *            分销商信息
	 * @param partnerIP
	 *            分销商IP
	 * @return
	 */
	@Override
	public String checkHmacData(String desStr, PartnerInfo partnerInfo, String partnerIP) {
		return null;
	}

	/**
	 * 检查验签、加密以及IP
	 * 
	 * @param desStr
	 *            数据
	 * @param appKey
	 *            公钥
	 * @param sign
	 *            签名
	 * @param partnerInfo
	 *            分销商信息
	 * @param partnerIP
	 *            分销商IP
	 * @return
	 */
	@Override
	public String checkHmacData(String desStr, String publicKey, String sign, PartnerInfo partnerInfo, String partnerIP) {
		boolean boo = checkIp(partnerIP, partnerInfo.getIp());
		if (!boo) {
			logger.info("+++++++++++++++++++IP++{ERROR}++++++++");
			return ParErrorMsgUtil.T800_OTHER_ERROR;// IP受限制返回相关错误信息
		}
		String desryptStr = PartnerUtil.md5SignatureSecret(desStr, publicKey, partnerInfo.getKeyValue());
		logger.info("+++++++++tuan800++++++++++++partnerNo：" + partnerInfo.getPartnerNo() + "+++++++++" + desryptStr + "++++++++++++");
		if (sign.toUpperCase().equals(desryptStr)) {
			return desStr;
		} else {
			logger.info("+++++++++++++++++++sign++{ERROR}++++++++");
			return ParErrorMsgUtil.T800_SIGN_MISMATCH;
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.beike.core.service.trx.partner.PartnerService#transReqInfo(java.lang
	 * .String)
	 */
	@Override
	public Object transReqInfo(String paramInfo) {
		return ParT800OrderGenerator.transReqInfo(paramInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.beike.core.service.trx.partner.PartnerService#generateRspHmac(java
	 * .lang.Object, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String generateRspHmac(Object source, String keyValue) {
		String rspInfo = JsonUtil.getJsonStringFromMap((Map<String, Object>) source);
		return rspInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.beike.core.service.trx.partner.PartnerService#findVouInfoByVouId(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public String findVouInfoByVouId(String partnerNo, String voucherId) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.beike.core.service.trx.partner.PartnerService#findVouInfoByActiveDate
	 * (java.lang.String, java.util.Date, java.util.Date, java.lang.String)
	 */
	@Override
	public String findVouInfoByActiveDate(String partnerNo, Date startTime, Date endTime, String trxStatusStr) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.beike.core.service.trx.partner.PartnerService#findVouInfoByLastUpdateDate
	 * (java.lang.String, java.util.Date, java.util.Date, java.lang.String)
	 */
	@Override
	public String findVouInfoByLastUpdateDate(String partnerNo, Date startTime, Date endTime, String trxStatus) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 团800订单同步
	 */
	@Override
	public String synchroTrxOrder(Object ptopSource, String partnerNo) throws Exception {
		logger.info("++++++start+++++++++++synchroTrxOrder++++");
		if (ptopSource == null) {
			return ParErrorMsgUtil.T800_OTHER_ERROR;
		}
		ParT800OrderParam pop = (ParT800OrderParam) ptopSource;
		List<Long> userIdList = pop.getUserIdList();
		String outReqId = pop.getOutRequestId();
		String payPrice = pop.getPayPrice();
		String totalPrice = pop.getTotalPrice();
		String prdCount = pop.getProdCount();

		double payPriceDou = Double.parseDouble(payPrice);
		double totalPriceDou = Double.parseDouble(totalPrice);
		if (payPriceDou < 0) {
			throw new Exception("payPrice  must be > 0 ");
		}
		if (totalPriceDou < Amount.mul(payPriceDou, Double.parseDouble(prdCount))) {
			throw new Exception("totalPrice < payPrice  * count ");
		}

		List<TrxorderGoods> tgList = null;

		boolean isExist = partnerReqIdService.preQryInWtDBByPNoAndReqId(partnerNo, outReqId);// 先到分销商订单号表查询

		if (isExist) {// 如果存在

			TrxOrder trxOrder = trxOrderService.preQryInWtDBByUIdAndOutReqId(outReqId, userIdList);
			if (trxOrder == null) {// 如果请求方第一次请求未收到响应，然后又请求了一次，则直接返回错误（此时我侧事务还未提交）
				return ParErrorMsgUtil.T800_OTHER_ERROR;
			} else if (TrxStatus.INIT.equals(trxOrder.getTrxStatus())) {// 交易重试
				OrderInfo orderInfo = new OrderInfo();
				orderInfo.setTrxOrder(trxOrder);
				orderInfo.setTrxRetry(true);// 开启重试
				orderInfo.setNeedLock(false);// 不需要锁机制
				orderInfo.setNeedActHis(false);// 不需要走账
				orderInfo = trxHessianService.noTscCreateTrxOrder(orderInfo);// 调用重试

				tgList = trxorderGoodsService.preQryInWtDBFindByTrxId(trxOrder.getId());
			} else {
				logger.info("+++++++++++++++++synchroTrxOrder++++trxOrder-repeat=" + trxOrder.getOutRequestId() + "+++repeat++++");

				tgList = trxorderGoodsService.preQryInWtDBFindByTrxId(trxOrder.getId());
			}
		} else {
			TrxRequestData requestData = new TrxRequestData();
			requestData.setReqChannel(ReqChannel.PARTNER);
			requestData.setUserId(Long.valueOf(pop.getUserId()));
			requestData.setUseOutPayPrice(true);// 使用外部支付价格
			requestData.setUseEndDateComLose(false);
			requestData.setPayPrice(String.valueOf(Amount.div(payPriceDou, 100d, 2)));
			requestData.setGoodsId(pop.getGoodsId());
			requestData.setMobile(pop.getMobile());// 加入手机号，分销商发送此手机号
			requestData.setGoodsCount(pop.getProdCount());
			OrderInfo orderInfo = null;
			orderInfo = trxSoaService.tansTrxReqData(requestData);
			orderInfo.setNeedActHis(false);
			orderInfo.setNeedLock(false);// 不需要应用级别的并发锁
			orderInfo.setOutSmsTemplate(pop.getSmsTemplate());// 分销商自有短信模板
			orderInfo.setPartnerNo(partnerNo);// 分销商编号
			orderInfo.setOutRequestId(outReqId);// 分销商外部请求订单号
			orderInfo.setClientIp(pop.getClientIp());// 分销商客户端IP
			orderInfo = trxHessianService.noTscCreateTrxOrder(orderInfo);// 调用创建订单Service
			tgList = trxorderGoodsService.preQryInWtDBFindByTrxId(orderInfo.getTrxId());
			// tgList = orderInfo.getTgList();
		}

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> orderDetail = null;
		List<Map<String, Object>> orderList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < tgList.size(); i++) {
			TrxorderGoods tg = tgList.get(i);
			Long voucherId = tg.getVoucherId();
			Voucher voucher = voucherService.preQryInWtDBVoucherByid(voucherId);
			//是否需要结算返点
			String rtnPointType = partnerRtnPoinService.processRtnPoin(tg, partnerNo, outReqId,Double.valueOf(amountType),tagType);
			orderDetail = transInfo(pop, voucher, tg);
			addOrderStatus(orderDetail, tg);
			orderDetail.put("send_flag", "0");
			orderDetail.put("rtn_point_type",rtnPointType);

			orderList.add(orderDetail);
		}

		dataMap.put("order_detail", orderList);
		jsonMap.put("ret", "0");
		jsonMap.put("msg", "ok");
		jsonMap.put("data", dataMap);

		logger.info("++++++++++++jsonMap=" + jsonMap);

		return JsonUtil.getJsonStringFromMap(jsonMap);
	}

	/**
	 * 重发码目前未使用
	 */
	@Override
	public String noTscResendVoucher(Object ptop) throws Exception {
		return null;
	}

	/**
	 * 退款
	 */
	@Override
	public String processTrxOrder(Object ptop) throws Exception {

		ParT800OrderParam pop800 = (ParT800OrderParam) ptop;
		String voucherCodes = pop800.getVoucherCode();

		// 目前这个字段是trxgoodsn
		String trxOrderId = pop800.getOrderId();

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> orderDetail = null;
		List<Map<String, Object>> orderList = new ArrayList<Map<String, Object>>();

		List<Long> userIdList = pop800.getUserIdList();
		if (!StringUtils.isEmpty(voucherCodes)) {
			String[] vc = voucherCodes.split(",");
			for (int i = 0; i < vc.length; i++) {
				Voucher voucher = voucherDao.findByVoucherCode(vc[i]);
				TrxorderGoods trxOrderGoods = trxorderGoodsDao.findByVoucherId(voucher.getId());
				if (trxOrderGoods != null) {
					TrxOrder trxorder = trxOrderDao.findById(trxOrderGoods.getTrxorderId());
					if (TrxStatus.REFUNDTOACT.equals(trxOrderGoods.getTrxStatus())) {
						orderDetail = transInfo(pop800, voucher, trxOrderGoods);
						orderDetail.put("tuan_order_no", trxorder.getOutRequestId());
						addOrderStatus(orderDetail, trxOrderGoods);
						orderDetail.put("send_flag", "0");
						orderList.add(orderDetail);
						continue;
					}

					Long userId = trxorder.getUserId();
					if (userIdList.indexOf(userId) >= 0) {
						Long trxGoodsId = trxOrderGoods.getId();
						refundService.processApplyForRefundToAct(trxGoodsId, "分销商", RefundSourceType.PARTNER, RefundHandleType.MANUAL, "分销商接口申请退款");
						trxOrderGoods = refundService.processToAct(trxGoodsId, "分销商", RefundSourceType.PARTNER, RefundHandleType.MANUAL, "分销商接口申请退款");
						orderDetail = transInfo(pop800, voucher, trxOrderGoods);
						orderDetail.put("tuan_order_no", trxorder.getOutRequestId());
						addOrderStatus(orderDetail, trxOrderGoods);
						orderDetail.put("send_flag", "0");
						orderList.add(orderDetail);
					}
				}
			}
		} else if (!StringUtils.isEmpty(trxOrderId)) {
			String[] toi = trxOrderId.split(",");
			for (int i = 0; i < toi.length; i++) {
				TrxorderGoods trxOrderGoods = trxorderGoodsDao.findBySn(toi[i]);
				if (trxOrderGoods != null) {
					TrxOrder trxorder = trxOrderDao.findById(trxOrderGoods.getTrxorderId());
					if (TrxStatus.REFUNDTOACT.equals(trxOrderGoods.getTrxStatus())) {
						Voucher voucher = voucherDao.findById(trxOrderGoods.getVoucherId());
						orderDetail = transInfo(pop800, voucher, trxOrderGoods);
						orderDetail.put("tuan_order_no", trxorder.getOutRequestId());
						addOrderStatus(orderDetail, trxOrderGoods);
						orderDetail.put("send_flag", "0");
						orderList.add(orderDetail);
						continue;
					}

					Long userId = trxorder.getUserId();
					if (userIdList.indexOf(userId) >= 0) {

						Long trxGoodsId = trxOrderGoods.getId();
						refundService.processApplyForRefundToAct(trxGoodsId, "分销商", RefundSourceType.PARTNER, RefundHandleType.MANUAL, "分销商接口申请退款");
						trxOrderGoods = refundService.processToAct(trxGoodsId, "分销商", RefundSourceType.PARTNER, RefundHandleType.MANUAL, "分销商接口申请退款");
						Voucher voucher = voucherDao.findById(trxOrderGoods.getVoucherId());
						orderDetail = transInfo(pop800, voucher, trxOrderGoods);
						orderDetail.put("tuan_order_no", trxorder.getOutRequestId());
						addOrderStatus(orderDetail, trxOrderGoods);
						orderDetail.put("send_flag", "0");
						orderList.add(orderDetail);
					}
				}
			}
		}
		dataMap.put("order_detail", orderList);
		jsonMap.put("ret", "0");
		jsonMap.put("msg", "ok");
		jsonMap.put("data", dataMap);

		return JsonUtil.getJsonStringFromMap(jsonMap);
	}

	private Map<String, Object> transInfo(ParT800OrderParam param, Voucher voucher, TrxorderGoods tg) {
		Map<String, Object> orderDetail = new HashMap<String, Object>();
		orderDetail.put("tuan_order_no", param.getOutRequestId());
		orderDetail.put("site_order_no", tg.getTrxGoodsSn());
		orderDetail.put("deal_id", tg.getGoodsId());
		orderDetail.put("count", "1");
		orderDetail.put("coupon_code", voucher.getVoucherCode());
		orderDetail.put("last_update_time", DateUtils.toString(tg.getLastUpdateDate(), "yyyy-MM-dd HH:mm:ss"));

		return orderDetail;
	}

	private Map<String, Object> addOrderStatus(Map<String, Object> map, TrxorderGoods trxOrderGoods) {

		/**
		 * 订单状态 : 0--新订单 1--等待付款 2--已付款；3--已发货；4--已确认 6--申请退款 7--退款中 8--已退款
		 */
		if (TrxStatus.INIT.equals(trxOrderGoods.getTrxStatus())) {
			map.put("status", "1");
		} else if (TrxStatus.SUCCESS.equals(trxOrderGoods.getTrxStatus()) || TrxStatus.EXPIRED.equals(trxOrderGoods.getTrxStatus())) {
			map.put("status", "3");
		} else if (TrxStatus.USED.equals(trxOrderGoods.getTrxStatus()) || TrxStatus.COMMENTED.equals(trxOrderGoods.getTrxStatus())) {
			map.put("status", "4");
		} else if (TrxStatus.REFUNDTOACT.equals(trxOrderGoods.getTrxStatus()) || TrxStatus.REFUNDTOBANK.equals(trxOrderGoods.getTrxStatus())) {
			map.put("status", "8");
		} else {
			map.put("status", "3");
		}
		/**
		 * 是否支持退款:0可退，1不可退
		 */
		if (!trxOrderGoods.getTrxStatus().isAplyRefundToAct() || !trxOrderGoods.getAuthStatus().isRefund()) {
			map.put("cancel_flag", "1");
			map.put("cancel_msg", "支付成功且未消费,未退款者才能发起退款，抱歉，您不满足以上条件，所以此订单不可退款");
		} else {
			map.put("cancel_flag", "0");
		}
		map.put("price", Amount.mulByInt(trxOrderGoods.getPayPrice(), 100));
		map.put("total_price", Amount.mulByInt(trxOrderGoods.getPayPrice(), 100));
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String findTrxorder(Object ptopSource, String partnerNo) {
		if (ptopSource == null || StringUtils.isEmpty(partnerNo)) {
			return ParErrorMsgUtil.T800_OTHER_ERROR;
		}
		ParT800OrderParam pop800 = (ParT800OrderParam) ptopSource;
		// 订单号，可能是多个，以逗号分隔
		String tgIds = pop800.getOrderId();
		// 凭证码，可能是多个，以逗号分隔
		String voucherCodes = pop800.getVoucherCode();

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> orderDetail = null;
		List<Map<String, Object>> orderList = new ArrayList<Map<String, Object>>();

		if (!StringUtils.isEmpty(voucherCodes)) {
			if (voucherCodes.indexOf(",") != -1) {
				String[] vc = voucherCodes.split(",");
				for (int i = 0; i < vc.length; i++) {
					Voucher voucher = voucherDao.findByVoucherCode(vc[i]);
					TrxorderGoods trxOrderGoods = trxorderGoodsDao.findByVoucherId(voucher.getId());
					TrxOrder trxorder = trxOrderDao.findById(trxOrderGoods.getTrxorderId());
					Map<String,String> condition = new HashMap<String, String>();
					condition.put("trxGoodsId",trxOrderGoods.getId().toString());
					if (trxOrderGoods != null) {
						orderDetail = transInfo(pop800, voucher, trxOrderGoods);
					try {
						List<Object>   partnerList = partnerRtnPointDao.queryPartnerRtnPointByCondition(condition);
						Map<String,Object> prp = (Map<String,Object>)partnerList.get(0);
						orderDetail.put("rtn_point_type",prp.get("rtn_point_type").toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
						orderDetail.put("tuan_order_no", trxorder.getOutRequestId());
						addOrderStatus(orderDetail, trxOrderGoods);
						orderDetail.put("send_flag", "0");
						orderList.add(orderDetail);
					}
				}
			} else {
				Voucher voucher = voucherDao.findByVoucherCode(voucherCodes);
				TrxorderGoods trxOrderGoods = trxorderGoodsDao.findByVoucherId(voucher.getId());
				TrxOrder trxorder = trxOrderDao.findById(trxOrderGoods.getTrxorderId());
				Map<String,String> condition = new HashMap<String, String>();
				condition.put("trxGoodsId",trxOrderGoods.getId().toString());
				if (trxOrderGoods != null) {
					orderDetail = transInfo(pop800, voucher, trxOrderGoods);
				try {
					List<Object>   partnerList = partnerRtnPointDao.queryPartnerRtnPointByCondition(condition);
					Map<String,Object> prp = (Map<String,Object>)partnerList.get(0);
					orderDetail.put("rtn_point_type",prp.get("rtn_point_type").toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
					orderDetail.put("tuan_order_no", trxorder.getOutRequestId());
					addOrderStatus(orderDetail, trxOrderGoods);
					orderDetail.put("send_flag", "0");
					orderList.add(orderDetail);
				}
			}
		}
		if (!StringUtils.isEmpty(tgIds)) {

			if (tgIds.indexOf(",") != -1) {
				String[] toi = tgIds.split(",");
				for (int i = 0; i < toi.length; i++) {
					TrxorderGoods trxOrderGoods = trxorderGoodsDao.findBySn(toi[i]);
					if (trxOrderGoods != null) {
						TrxOrder trxorder = trxOrderDao.findById(trxOrderGoods.getTrxorderId());
						Voucher voucher = voucherDao.findById(trxOrderGoods.getVoucherId());
						Map<String,String> condition = new HashMap<String, String>();
						condition.put("trxGoodsId",trxOrderGoods.getId().toString());
						orderDetail = transInfo(pop800, voucher, trxOrderGoods);
						try {
							List<Object>   partnerList = partnerRtnPointDao.queryPartnerRtnPointByCondition(condition);
							Map<String,Object> prp = (Map<String,Object>)partnerList.get(0);
							orderDetail.put("rtn_point_type",prp.get("rtn_point_type").toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
						orderDetail.put("tuan_order_no", trxorder.getOutRequestId());
						addOrderStatus(orderDetail, trxOrderGoods);
						orderDetail.put("send_flag", "0");
						orderList.add(orderDetail);
					}
				}
			} else {
				TrxorderGoods trxOrderGoods = trxorderGoodsDao.findBySn(tgIds);
				if (trxOrderGoods != null) {
					TrxOrder trxorder = trxOrderDao.findById(trxOrderGoods.getTrxorderId());
					Voucher voucher = voucherDao.findById(trxOrderGoods.getVoucherId());
					Map<String,String> condition = new HashMap<String, String>();
					condition.put("trxGoodsId",trxOrderGoods.getId().toString());
					orderDetail = transInfo(pop800, voucher, trxOrderGoods);
					try {
						List<Object>   partnerList = partnerRtnPointDao.queryPartnerRtnPointByCondition(condition);
						Map<String,Object> prp = (Map<String,Object>)partnerList.get(0);
						orderDetail.put("rtn_point_type",prp.get("rtn_point_type").toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					orderDetail.put("tuan_order_no", trxorder.getOutRequestId());
					addOrderStatus(orderDetail, trxOrderGoods);
					orderDetail.put("send_flag", "0");
					orderList.add(orderDetail);
				}
			}
		}
		dataMap.put("order_detail", orderList);
		jsonMap.put("ret", "0");
		jsonMap.put("msg", "ok");
		jsonMap.put("data", dataMap);

		return JsonUtil.getJsonStringFromMap(jsonMap);
	}

	

	
	@Override
	public String checkHmacData(String params, String publicKey, String privateKey) {
		if (StringUtils.isEmpty(params) || StringUtils.isEmpty(publicKey) || StringUtils.isEmpty(privateKey)) {
			logger.info("+++++++++++++++++++数据不完整++++++++++");
			return ParErrorMsgUtil.T800_OTHER_ERROR;
		}
		String desryptStr = PartnerUtil.md5SignatureSecret(params, publicKey, privateKey);
		logger.info("+++++++++tuan800++++++++++++md5 sign +++++++++++++" + desryptStr + "++++++++++++");
		if (!StringUtils.isEmpty(desryptStr)) {
			return desryptStr;
		} else {
			logger.info("+++++++++++++++++++sign++{ERROR}++++++++");
			return ParErrorMsgUtil.T800_SIGN_MISMATCH;
		}
	}

	@Override
	public String findVoucher(Object ptop, String partnerNo) throws Exception {
		return null;
	}
}