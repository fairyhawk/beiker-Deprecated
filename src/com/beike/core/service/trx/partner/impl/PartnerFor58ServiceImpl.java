package com.beike.core.service.trx.partner.impl;

import java.util.Arrays;
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
import com.beike.common.bean.trx.partner.Par58OrderGenerator;
import com.beike.common.bean.trx.partner.Par58OrderParam;
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
import com.beike.core.service.trx.partner.PartnerCommonService;
import com.beike.core.service.trx.partner.PartnerReqIdService;
import com.beike.core.service.trx.partner.PartnerService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.dao.trx.VoucherDao;
import com.beike.util.DateUtils;
import com.beike.util.EnumUtil;
import com.beike.util.PartnerUtil;
import com.beike.util.StringUtils;
import com.beike.util.img.JsonUtil;

/**
 * @Title: PartnerServiceImpl.java
 * @Package com.beike.core.service.trx.parter.impl
 * @Description: 合作分销商API交易相关ServiceImpl(58)
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("partnerFor58Service")
public class PartnerFor58ServiceImpl implements PartnerService {

	private final Log logger = LogFactory.getLog(PartnerFor58ServiceImpl.class);
	@Autowired
	private PartnerCommonService partnerCommonService;
	@Autowired
	private TrxOrderDao trxOrderDao;
	@Autowired
	private TrxOrderService trxOrderService;
	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;
	@Autowired
	private TrxorderGoodsService trxorderGoodsService;
	@Autowired
	private VoucherDao voucherDao;

	@Resource(name = "trxHessianService")
	private TrxHessianService trxHessianService;
	@Autowired
	private RefundService refundService;
	@Autowired
	private TrxSoaService trxSoaService;

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

		boolean boo = checkIp(reqIp, partnerInfo.getIp());
		if (!boo) {
			logger.info("+++++++++++++++++++IP++{ERROR}++++++++");
			return "10100";// IP受限制返回相关错误信息
		}
		String desryptStr = PartnerUtil.decryptDes(desStr, partnerInfo.getKeyValue());
		if ("".equals(desryptStr)) {
			return "10100";
		}
		logger.info("+++++++++58TC++++++++++++partnerNo:" + partnerInfo.getPartnerNo() + "+++++++++" + desryptStr + "++++++++++++");
		return desryptStr;
	}

	/**
	 * 转换并组装分销商58请求相关参数
	 * 
	 * @param parmInfo
	 * @return
	 */

	public Par58OrderParam transReqInfo(String paramInfo) {

		return Par58OrderGenerator.transReqOrderInfo(paramInfo);
	}

	/**
	 * 根据voucherId获取凭证信息
	 */
	@Override
	public String findVouInfoByVouId(String partnerNo, String voucherId) {
		String jsonTickets = "";
		if (voucherId != null && voucherId.length() > 0) {
			String voucherIdStr = this.getStr(voucherId);
			List<TrxorderGoods> trxOrderGoodsList = trxorderGoodsDao.findTgByVoucherId(voucherIdStr);
			if (trxOrderGoodsList == null || trxOrderGoodsList.size() == 0) {
				jsonTickets = "";
			} else {
				Map<String, Object> jsonMap = new HashMap<String, Object>();
				for (TrxorderGoods trxOrderGoods : trxOrderGoodsList) {
					if (trxOrderGoods != null && !TrxStatus.INIT.equals(trxOrderGoods.getTrxStatus())) {
						Long trxorderId = trxOrderGoods.getTrxorderId();
						Long vouId = trxOrderGoods.getVoucherId();
						TrxStatus trxStatus = trxOrderGoods.getTrxStatus();// 对应58的status，券状态
						Long goodsId = trxOrderGoods.getGoodsId();// 对应58的groupbuyIdThirdpart，第三方团购ID
						double payPrice = trxOrderGoods.getPayPrice();
						Date orderLoseDate = trxOrderGoods.getOrderLoseDate();// 对应58的createtime，失效时间
						String outGoodsId = trxOrderGoods.getOutGoodsId();// 对应58的团购ID
						String orderLoseDateRst = DateUtils.toString(orderLoseDate, "yyyy-MM-dd HH:mm:ss");
						String voucherCode = trxOrderGoods.getTrxGoodsSn();// 对应58的ticketCode，券号
						TrxOrder trxOrder = trxOrderDao.findById(trxorderId);
						Long userId = trxOrder.getUserId();
						String outRequestId = trxOrder.getOutRequestId();// 对应58的orderId58，58订单Id
						String orderIdThirdpart = String.valueOf(trxOrder.getId());// 对应58的orderIdThirdpart，我方trxorderId
						boolean result = partnerCommonService.checkIsUIdBelongPNo(partnerNo, userId);// 根据user_id
																										// 检查是否归属该分销商
						if (result) {
							Voucher voucher = voucherDao.findById(vouId);
							String voucherPass = voucher.getVoucherCode();// 对应58的ticketpass，密码
							Date activeDate = voucher.getActiveDate();// 对应58的createtime
																		// ，生成时间
							String activeDateRst = DateUtils.toString(activeDate, "yyyy-MM-dd HH:mm:ss");
							jsonMap.put("ticketIdThirdpart", vouId);
							jsonMap.put("orderIdThirdpart", orderIdThirdpart);
							jsonMap.put("groupbuyIdThirdpart", goodsId);
							jsonMap.put("orderId58", outRequestId);
							jsonMap.put("ticketCode", voucherCode);
							jsonMap.put("ticketPass", voucherPass);
							jsonMap.put("orderPrice", payPrice);
							jsonMap.put("status", Par58OrderGenerator.transRspTrxStatus(trxStatus));
							jsonMap.put("createTime", activeDateRst);
							jsonMap.put("endTime", orderLoseDateRst);
							jsonMap.put("groupbuyId58", outGoodsId);
							jsonTickets = jsonTickets + JsonUtil.getJsonStringFromMap(jsonMap) + ",";

						}
					}
				}

			}
		} else {
			return "10100";
		}
		String str = jsonTickets.substring(0, jsonTickets.length() - 1);
		jsonTickets = "[" + str + "]";
		return jsonTickets;
	}

	private String getStrArray(String[] jsonStr) {
		String str = Arrays.toString(jsonStr);
		str = str.replace("\\", "");
		str = str.replace("\"[", "[");
		str = str.replace("]\"", "]");
		return str;
	}

	/**
	 * 根据创建时间和状态获取凭证信息
	 */
	@Override
	public String findVouInfoByActiveDate(String partnerNo, Date startTime, Date endTime, String trxStatus) {
		String tickets = "";
		String userIdStr = "";
		Map<String, Object> voucherMap = new HashMap<String, Object>();
		// Map<String,Object> jsonMap = new HashMap<String,Object>();
		StringBuilder userIdSB = new StringBuilder();// userId组装所在SB
		if (endTime.after(startTime)) {
			List<PartnerInfo> partnerInfoList = partnerCommonService.qryAllPartnerByNoInMem(partnerNo);
			if (partnerInfoList != null && partnerInfoList.size() > 0) {
				for (PartnerInfo partnerInfo : partnerInfoList) {
					userIdStr = partnerInfo.getUserId().toString();
					userIdSB.append(userIdStr);// userId组装
					userIdSB.append(",");
				}
				if (userIdSB.toString().length() > 0 && userIdSB != null) {
					userIdSB.deleteCharAt(userIdSB.length() - 1);// 删除最后一个","
					String trxGoodsStatus = Par58OrderGenerator.transReqTrxStatus(trxStatus);
					List<Map<String, Object>> listMap = voucherDao.findByActiveDateAndStatus(startTime, endTime, userIdSB.toString(), trxGoodsStatus);
					String[] ticketsArray = new String[listMap.size()];
					if (!TrxStatus.INIT.name().equals(trxGoodsStatus) && listMap.size() > 0) {

						for (int i = 0; i < listMap.size(); i++) {
							Map<String, Object> map = listMap.get(i);
							Date orderLoseDate = (Date) map.get("orderLoseDate");
							String eTime = DateUtils.toString(orderLoseDate, "yyyy-MM-dd HH:mm:ss");
							Date activeDate = (Date) map.get("activeDate");
							String cTime = DateUtils.toString(activeDate, "yyyy-MM-dd HH:mm:ss");
							voucherMap.put("ticketIdThirdpart", map.get("voucherId"));
							voucherMap.put("orderIdThirdpart", map.get("trxorderId"));// 对应58的orderIdThirdpart，第三方订单号
							voucherMap.put("groupbuyIdThirdpart", map.get("goodsId"));// 对应58的groupbuyIdThirdpart，第三方团购ID
							voucherMap.put("orderId58", map.get("outRequestId"));// 对应58的orderId58，58订单Id
							voucherMap.put("ticketCode", map.get("trxGoodsSn"));// 对应58的ticketCode，券号
							voucherMap.put("ticketPass", map.get("voucherCode"));// 对应58的ticketpass，密码
							voucherMap.put("orderPrice", map.get("payPrice"));
							voucherMap.put("status", Par58OrderGenerator.transRspTrxStatus(EnumUtil.transStringToEnum(TrxStatus.class, map.get("trxStatus").toString())));// 对应58的status，券状态
							voucherMap.put("createTime", cTime);// 对应58的createtime
																// ，生成时间
							voucherMap.put("endTime", eTime);// 对应58的endtime，失效时间
							voucherMap.put("groupbuyId58", map.get("outGoodsId"));// 对应58团购ID
							ticketsArray[i] = JsonUtil.getJsonStringFromMap(voucherMap);
						}
					}
					tickets = getStrArray(ticketsArray);
				}
			}

			return tickets;
		}
		return "10202";
	}

	/**
	 * 根据更新时间和状态获取凭证信息
	 */
	@Override
	public String findVouInfoByLastUpdateDate(String partnerNo, Date startTime, Date endTime, String trxStatus) {
		String tickets = "";
		String userIdStr = "";
		StringBuilder userIdSB = new StringBuilder();
		Map<String, Object> voucherMap = new HashMap<String, Object>();
		// Map<String,Object> jsonMap = new HashMap<String,Object>();
		if (endTime.after(startTime)) {
			List<PartnerInfo> partnerInfoList = partnerCommonService.qryAllPartnerByNoInMem(partnerNo);
			if (partnerInfoList != null && partnerInfoList.size() > 0) {
				for (PartnerInfo partnerInfo : partnerInfoList) {
					userIdStr = partnerInfo.getUserId().toString();
					userIdSB.append(userIdStr);
					userIdSB.append(",");
				}
				if (userIdSB.toString().length() > 0 && userIdSB != null) {
					userIdSB.deleteCharAt(userIdSB.length() - 1);// 删除最后一个","
					List<Map<String, Object>> listMap = trxorderGoodsDao.findByLastUpdateDateAndStatus(startTime, endTime, userIdSB.toString(), Par58OrderGenerator.transReqTrxStatus(trxStatus));
					String[] ticketsArray = new String[listMap.size()];
					if (!TrxStatus.INIT.name().equals(trxStatus) && listMap.size() > 0) {
						for (int i = 0; i < listMap.size(); i++) {
							Map<String, Object> map = listMap.get(i);
							Date orderLoseDate = (Date) map.get("orderLoseDate");
							String eTime = DateUtils.toString(orderLoseDate, "yyyy-MM-dd HH:mm:ss");
							Date activeDate = (Date) map.get("activeDate");
							String cTime = DateUtils.toString(activeDate, "yyyy-MM-dd HH:mm:ss");
							voucherMap.put("ticketIdThirdpart", map.get("voucherId"));
							voucherMap.put("orderIdThirdpart", map.get("trxOrderId"));// 对应58的orderIdThirdpart，第三方订单号
							voucherMap.put("groupbuyIdThirdpart", map.get("goodsId"));// 对应58的groupbuyIdThirdpart，第三方团购ID
							voucherMap.put("orderId58", map.get("outRequestId"));// 对应58的orderId58，58订单Id
							voucherMap.put("ticketCode", map.get("trxGoodsSn"));// 对应58的ticketCode，券号
							voucherMap.put("ticketPass", map.get("voucherCode"));// 对应58的ticketpass，密码
							voucherMap.put("orderPrice", map.get("payPrice"));
							voucherMap.put("status", Par58OrderGenerator.transRspTrxStatus(EnumUtil.transStringToEnum(TrxStatus.class, map.get("trxStatus").toString())));// 对应58的status，券状态
							voucherMap.put("createTime", cTime);// 对应58的createtime
																// ，生成时间
							voucherMap.put("endTime", eTime);// 对应58的endtime，失效时间
							voucherMap.put("groupbuyId58", map.get("outGoodsId"));// 对应58团购ID
							ticketsArray[i] = JsonUtil.getJsonStringFromMap(voucherMap);
						}
						tickets = getStrArray(ticketsArray);
						return tickets;
					}
				}
			}
		}
		return "10202";
	}

	@Override
	public String processTrxOrder(Object ptop) throws Exception {
		Par58OrderParam p58Op = (Par58OrderParam) ptop;
		String voucherId = p58Op.getVoucherId();
		String trxOrderId = p58Op.getOrderId();
		String reason = p58Op.getReason();
		if (reason != null && reason.length() > 45) {
			reason = reason.substring(0, 40);
		}
		List<Long> userIdList = p58Op.getUserIdList();
		TrxorderGoods trxOrderGoods = trxorderGoodsDao.findByVoucherId(Long.valueOf(voucherId));
		if (trxOrderGoods != null) {

			if (trxOrderGoods.getTrxorderId().equals(Long.valueOf(trxOrderId))) {
				if (TrxStatus.REFUNDTOACT.equals(trxOrderGoods.getTrxStatus())) {
					return "SUCCESS";
				}
				TrxOrder trxorder = trxOrderDao.findById(trxOrderGoods.getTrxorderId());
				Long userId = trxorder.getUserId();
				if (userIdList.indexOf(userId) >= 0) {
					Long trxGoodsId = trxOrderGoods.getId();
					refundService.processApplyForRefundToAct(trxGoodsId, "分销商", RefundSourceType.PARTNER, RefundHandleType.MANUAL, reason);

					trxOrderGoods = refundService.processToAct(trxGoodsId, "分销商", RefundSourceType.PARTNER, RefundHandleType.MANUAL, reason);

					return "SUCCESS";

				}

			}

		}

		return "10010";
	}

	/**
	 * 凭证重发。58临时取消此接口。 //TODO鉴权需放在此方法内部
	 * 
	 */
	@Override
	public String noTscResendVoucher(Object ptop) {
		Par58OrderParam p58Op = (Par58OrderParam) ptop;
		TrxorderGoods trxOrderGoods = trxorderGoodsDao.findBySn(p58Op.getTrxGoodsSn());
		Long trxGoodsId = trxOrderGoods.getId();

		TrxRequestData requestData = new TrxRequestData();
		requestData.setMobile(p58Op.getMobile());
		requestData.setVerifyForTg(false);// 对于商品订单是否需要鉴权.避免多次查询.
		requestData.setTrxorderGoodsId(trxGoodsId);
		requestData.setReqChannel(ReqChannel.PARTNER);
		try {
			trxHessianService.reSendVoucher(requestData);
			return "SUCCESS";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "10010";
	}

	@Override
	public String synchroTrxOrder(Object ptop, String partnerNo) throws Exception {

		Map<String, Object> voucherMap = new HashMap<String, Object>();
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Par58OrderParam p58op = (Par58OrderParam) ptop;
		List<Long> userIdList = p58op.getUserIdList();
		String outReqId = p58op.getOrderId();
		String payPrice = p58op.getPayPrice();

		double payPriceDou = Double.parseDouble(payPrice);
		if (payPriceDou < 0) {
			throw new Exception("payPrice  must be > 0 ");
		}
		String jsonStrArr = "";
		List<Map<String, Object>> listMap = null;
		TrxOrder trxOrder = null;
		boolean isExist = partnerReqIdService.preQryInWtDBByPNoAndReqId(partnerNo, outReqId);// 先到分销商订单号表查询
		if (isExist) {// 如果存在
			trxOrder = trxOrderService.preQryInWtDBByUIdAndOutReqId(outReqId, userIdList);
			if (trxOrder == null) {
				logger.debug("+++partnerNo:" + partnerNo + "+++outReqId:" + outReqId + "++++trxOrder is null!++");
				throw new Exception();
			} else if (TrxStatus.INIT.equals(trxOrder.getTrxStatus())) {// 交易重试

				OrderInfo orderInfo = new OrderInfo();
				orderInfo.setTrxOrder(trxOrder);
				orderInfo.setTrxRetry(true);// 开启重试
				orderInfo.setNeedLock(false);// 不需要锁机制
				orderInfo.setNeedActHis(false);// 不需要走账
				orderInfo = trxHessianService.noTscCreateTrxOrder(orderInfo);// 调用重试
				listMap = trxorderGoodsService.preQryInWtDBByByTrxId(trxOrder.getId());
			} else {
				listMap = trxorderGoodsService.preQryInWtDBByByTrxId(trxOrder.getId());
				logger.info("++++++++++++++++listMap:++" + listMap);
			}
		} else {
			TrxRequestData requestData = new TrxRequestData();
			requestData.setReqChannel(ReqChannel.PARTNER);
			requestData.setMobile(p58op.getMobile());// 加入手机号，分销商发送此手机号
			requestData.setUserId(p58op.getUserId());
			requestData.setGoodsId(p58op.getGoodsId());
			requestData.setGoodsCount(p58op.getProdCount());
			requestData.setUseEndDateComLose(true);// 使用下架时间作为下单时间计算过期时间
			requestData.setUseOutPayPrice(true);// 使用外部支付价格
			requestData.setPayPrice(payPrice);
			OrderInfo orderInfo = trxSoaService.tansTrxReqData(requestData);
			orderInfo.setNeedActHis(false);
			orderInfo.setNeedLock(false);// 不需要应用级别的并发锁
			orderInfo.setPartnerNo(partnerNo);// 分销商编号
			orderInfo.setOutRequestId(outReqId);// 分销商外部请求订单号
			orderInfo.setOutGoodsId(p58op.getOutGoodsId());// 分销商外部请求商品ID
			orderInfo.setClientIp(p58op.getClientIp());
			orderInfo = trxHessianService.noTscCreateTrxOrder(orderInfo);// 调用创建订单Service
			trxOrder = orderInfo.getTrxOrder();

			listMap = trxorderGoodsService.preQryInWtDBByByTrxId(trxOrder.getId());

		}
		String[] tickets = new String[listMap.size()];
		for (int i = 0; i < listMap.size(); i++) {
			Map<String, Object> map = listMap.get(i);
			String ticketId = map.get("ticketId").toString();
			String ticketCode = (String) map.get("ticketCode");
			String ticketPass = (String) map.get("ticketPass");
			Date createDate = (Date) map.get("createDate");
			String sqlCreateDate = DateUtils.toString(createDate, "yyyy-MM-dd HH:mm:ss");
			Date lostDate = (Date) map.get("lostDate");
			String sqlLostDate = DateUtils.toString(lostDate, "yyyy-MM-dd HH:mm:ss");
			voucherMap.put("ticketId", ticketId);
			voucherMap.put("ticketCode", ticketCode);
			voucherMap.put("ticketPass", ticketPass);
			voucherMap.put("createTime", sqlCreateDate);
			voucherMap.put("endTime", sqlLostDate);
			tickets[i] = JsonUtil.getJsonStringFromMap(voucherMap);
		}
		jsonMap.put("orderId", trxOrder.getOutRequestId());
		jsonMap.put("orderIdThirdpart", trxOrder.getId());
		jsonMap.put("tickets", Arrays.toString(tickets));

		jsonStrArr = JsonUtil.getJsonStringFromMap(jsonMap);
		jsonStrArr = jsonStrArr.replace("\\", "");
		jsonStrArr = jsonStrArr.replace("\"[", "[");
		jsonStrArr = jsonStrArr.replace("]\"", "]");
		logger.info("++++++++++++++++jsonStrArr:++" + jsonStrArr);

		return jsonStrArr;
	}

	/**
	 * 响应的密文或者签名数据
	 * 
	 * @param source
	 * @param keyValue
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String generateRspHmac(Object source, String keyValue) {
		Map<String, Object> map = (Map<String, Object>) source;
		String data = (String) map.get("data");
		logger.info("++++++++++++generateRspHmac+++++++++++++" + data);
		String cryptDes = "";
		if (data != null && !"".equals(data)) {
			cryptDes = PartnerUtil.cryptDes(data, keyValue);
		}
		if ("".equals(cryptDes)) {
			map.put("status", "10100");
		} else {
			map.put("data", cryptDes);
		}
		String jsonStr = JsonUtil.getJsonStringFromMap(map);
		return jsonStr;

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

	public String getStr(String value) {
		value = value.replace("[", "");
		value = value.replace("]", "");
		value = value.replace("\"", "");
		return value;
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