package com.beike.action.trx.partner;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.pay.BaseTrxAction;
import com.beike.common.bean.trx.partner.Par1mallOrderGenerator;
import com.beike.common.bean.trx.partner.Par1mallOrderParam;
import com.beike.common.bean.trx.partner.Par360buyOrderGenerator;
import com.beike.common.bean.trx.partner.Par360buyOrderParam;
import com.beike.common.bean.trx.partner.Par58OrderParam;
import com.beike.common.bean.trx.partner.ParErrorMsgUtil;
import com.beike.common.bean.trx.partner.ParT800OrderParam;
import com.beike.common.bean.trx.partner.ParTaobaoOrderParam;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.common.enums.trx.PartnerApiType;
import com.beike.core.service.trx.partner.PartnerCommonService;
import com.beike.core.service.trx.partner.PartnerGoodsService;
import com.beike.core.service.trx.partner.PartnerService;
import com.beike.core.service.trx.partner.PartnerServiceFactory;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.soa.proxy.GoodsSoaDao;
import com.beike.entity.goods.Goods;
import com.beike.service.goods.GoodsService;
import com.beike.util.Amount;
import com.beike.util.Configuration;
import com.beike.util.DateUtils;
import com.beike.util.HttpUtils;
import com.beike.util.PartnerUtil;
import com.beike.util.StringUtils;
import com.beike.util.img.JsonUtil;

/**
 * @Title: ParTrxOrderAction.java
 * @Package com.beike.action.trx.partner
 * @Description: 合作分销商API订单相关Action
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Controller
public class ParTrxOrderAction extends BaseTrxAction {
	@Resource(name = "partnerServiceFactory")
	private PartnerServiceFactory partnerServiceFactory;

	@Resource(name = "partnerCommonService")
	private PartnerCommonService partnerCommonService;
	private final Log logger = LogFactory.getLog(ParTrxOrderAction.class);

	@Resource(name = "partnerGoodsService")
	private PartnerGoodsService partnerGoodsService;
	@Resource(name = "goodsService")
	private GoodsService goodsService;

	@Autowired
	private TrxSoaService trxSoaService;
	@Autowired
	private GoodsSoaDao goodsSoaDao;

	/**
	 * 订单同步
	 * 
	 * @return
	 */
	@RequestMapping("/partner/synchroTrxOrder.do")
	public void synchroTrxOrder(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> map = new HashMap<String, Object>();
		String apiType = PartnerApiType.TC58.name();
		PartnerService partnerService = null;
		String keyValue = "";
		String rspCode = "";
		String partnerNo = getPartnerNo(request);
		String desStr = getPartnerdDes(request);
		logger.info("++++++++++++++++++++/partner/synchroTrxOrder.do++++++++++++++++++");
		try {
			PartnerInfo partnerInfo = partnerCommonService.qryAvaParterByNoInMem(partnerNo);
			List<Long> userIdList = partnerCommonService.qryAllUserIdByNoInMem(partnerNo);
			logger.info("++++++++++++++desStr=" + desStr + "++++++++++partnerNo=" + partnerNo);
			apiType = partnerInfo.getApiType();
			keyValue = partnerInfo.getKeyValue();
			String reqIP = StringUtils.getIpAddr(request);// 获取服务器IP
			Long userId = partnerInfo.getUserId();
			partnerService = partnerServiceFactory.getPartnerService(apiType);
			// 传入密文和商家编号以及与预置IP
			String paramInfo = partnerService.checkHmacData(desStr, partnerInfo, reqIP);
			if ("10100".equals(paramInfo)) {
				map.put("status", paramInfo);
				map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(paramInfo, apiType));
				map.put("data", "");
			} else {
				// 调用58参数数据转换工具类
				Par58OrderParam par58OrderParam = (Par58OrderParam) partnerService.transReqInfo(paramInfo);
				par58OrderParam.setUserId(userId);
				par58OrderParam.setUserIdList(userIdList);
				par58OrderParam.setClientIp(StringUtils.getIpAddr(request));
				String jsonStrArr = partnerService.synchroTrxOrder(par58OrderParam, partnerNo);
				if (StringUtils.isEmpty(jsonStrArr)) {
					rspCode = "10100";
				} else {
					rspCode = "10000";
				}
				map.put("status", rspCode);
				map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(rspCode, apiType));
				map.put("data", jsonStrArr);

			}

		} catch (Exception e) {
			map.put("status", "10100");
			map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode("10100", apiType));
			map.put("data", "");
			e.printStackTrace();
		}
		logger.info("++++++++partnerNo=" + partnerNo + "+++++++++++++++++dataReturn=" + map);

		String dataReturn = partnerService.generateRspHmac(map, keyValue);
		try {
			response.getWriter().write(dataReturn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 订单修改(退款、过期)
	 * 
	 * @return
	 */
	@RequestMapping("/partner/modifyTrxOrder.do")
	public void modifyTrxOrder(HttpServletRequest request, HttpServletResponse response) {
		logger.info("++++++++++++++++++++/partner/modifyTrxOrder.do++++++++++++++++++");
		String dataReturn = "";
		Map<String, Object> jodnMap = new HashMap<String, Object>();
		String apiType = "";
		String keyValue = "";
		String partnerNo = getPartnerNo(request);
		String desStr = getPartnerdDes(request);
		PartnerService partnerService = null;
		try {

			PartnerInfo partnerInfo = partnerCommonService.qryAvaParterByNoInMem(partnerNo);
			List<Long> userIdList = partnerCommonService.qryAllUserIdByNoInMem(partnerNo);
			apiType = partnerInfo.getApiType();
			keyValue = partnerInfo.getKeyValue();
			partnerService = partnerServiceFactory.getPartnerService(apiType);
			String reqIp = StringUtils.getIpAddr(request);// 获取服务器IP
			if (PartnerApiType.TC58.name().equals(apiType)) {
				// 传入密文和商家编号以及与预置IP
				String paramInfo = partnerService.checkHmacData(desStr, partnerInfo, reqIp);
				// 调用58参数数据转换工具类
				Par58OrderParam par58OrderParam = (Par58OrderParam) partnerService.transReqInfo(paramInfo);
				par58OrderParam.setUserIdList(userIdList);// 隶属该分销商的所有userId
				String trxStatus = par58OrderParam.getStatus();
				if ("10".equals(trxStatus) || "11".equals(trxStatus)) {
					// 跟去券信息查询券状态是否符合

					// 调用退款接口TODO
					String modify = partnerService.processTrxOrder(par58OrderParam);
					if ("SUCCESS".equals(modify)) {
						String rspCodes = "10000";
						jodnMap.put("status", rspCodes);
						jodnMap.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(rspCodes, apiType));
						jodnMap.put("data", "SUCCESS");
					} else {
						jodnMap.put("status", modify);
						jodnMap.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(modify, apiType));
						jodnMap.put("data", "");
					}
				} else {
					logger.info("++++++++partnerNo=" + partnerNo + "+++++++++++++++++par58OrderParam.getStatus()=" + par58OrderParam.getStatus() + "++++++++");
					// 请求券状态不符合
					jodnMap.put("status", "10100");
					jodnMap.put("msg", ParErrorMsgUtil.getParErrorMsgByCode("10100", apiType));
					jodnMap.put("data", "");
				}
			} else {
				logger.info("++++++++partnerNo=" + partnerNo + "+++++++++++++++++分销商不符++++++++");
				jodnMap.put("status", "10100");
				jodnMap.put("msg", ParErrorMsgUtil.getParErrorMsgByCode("10100", apiType));
				jodnMap.put("data", "");
			}

			logger.info("++++++++partnerNo=" + partnerNo + "+++++++++++++++++dataReturn=" + jodnMap);

		} catch (Exception e) {
			logger.info("++++++++partnerNo=" + partnerNo + "+++++++++++++++++分销商不符++++++++");
			jodnMap.put("status", "10100");
			jodnMap.put("msg", ParErrorMsgUtil.getParErrorMsgByCode("10100", apiType));
			jodnMap.put("data", "");
			e.printStackTrace();
		}
		try {
			dataReturn = partnerService.generateRspHmac(jodnMap, keyValue);
			response.getWriter().write(dataReturn);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 基于券ids进行券信息查询接口-58
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/partner/qryVouBySn.do")
	public void qryVouBySn(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		String dataReturn = "";// 返回值
		String apiType = "";// api类型
		String partnerNo = getPartnerNo(request);
		String desStr = getPartnerdDes(request);
		PartnerInfo partnerInfo = null;
		PartnerService partnerService = null;
		try {
			partnerInfo = partnerCommonService.qryAvaParterByNoInMem(partnerNo);// 根据partnerNo从mem中获取生效分销商信息
			apiType = partnerInfo.getApiType();
			String reqIp = StringUtils.getIpAddr(request);
			partnerService = partnerServiceFactory.getPartnerService(apiType);// 获取工厂服务实现
			String paramInfo = partnerService.checkHmacData(desStr, partnerInfo, reqIp);// //检查验签、加密以及IP.只做3DES验证
			if (!"10100".equals(paramInfo) && paramInfo.length() > 0 && paramInfo != null) {
				// 调用58参数转换工具类
				Par58OrderParam par58OrderParam = (Par58OrderParam) partnerService.transReqInfo(paramInfo);// 转换并组装分销商请求相关参数
				String voucherId = par58OrderParam.getVoucherId();// 58的第三方券ID
				// "["和"]"必须只能出现一次，而且"["在开始位置，"]"在结束位置
				int i = voucherId.indexOf("[");
				int j = voucherId.lastIndexOf("[");
				int m = voucherId.indexOf("]");
				int n = voucherId.lastIndexOf("]");
				if (voucherId == null || voucherId.length() == 0 || i != j || m != n || m != (voucherId.length() - 1) || i != 0) {
					map.put("status", "10201");
					map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode("10201", apiType));
					map.put("data", "");
				} else {
					String jsonStrArr = partnerService.findVouInfoByVouId(partnerNo, voucherId);// 获取凭证信息-58的券信息
					if (!"10100".equals(jsonStrArr) && jsonStrArr != null && jsonStrArr.length() > 0) {
						String rspCode = "10000";
						map.put("status", rspCode);
						map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(rspCode, apiType));
						map.put("data", jsonStrArr);
					} else {
						map.put("status", jsonStrArr);
						map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(jsonStrArr, apiType));
						map.put("data", jsonStrArr);
					}
				}
			}

		} catch (Exception e) {
			logger.debug(e);
			map.put("status", "10100");
			map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode("10100", apiType));
			map.put("data", "");
			e.printStackTrace();
		} finally {
			logger.info("++++++++partnerNo=" + partnerNo + "+++++++++++++++++dataReturn=" + map);
			dataReturn = partnerService.generateRspHmac(map, partnerInfo.getKeyValue());// 响应的密文或者签名数据
		}
		try {
			response.getWriter().write(dataReturn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 基于券新建时间进行券信息查询接口-58
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/partner/qryVouByActiveDate.do")
	public void qryVouByActiveDate(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();

		String dataReturn = "";// 返回值
		String apiType = "";// api类型
		String partnerNo = getPartnerNo(request);
		String desStr = getPartnerdDes(request);
		PartnerInfo partnerInfo = null;// 分销商信息
		PartnerService partnerService = null;
		try {
			partnerInfo = partnerCommonService.qryAvaParterByNoInMem(partnerNo);// 根据partnerNo从mem中取出PartnerInfo
			apiType = partnerInfo.getApiType();
			String reqIp = StringUtils.getIpAddr(request);// 获取服务器IP地址
			partnerService = partnerServiceFactory.getPartnerService(apiType);// 获取工厂业务实现
			String paramInfo = partnerService.checkHmacData(desStr, partnerInfo, reqIp);// 检查验签、加密以及IP.
			if (!"10100".equals(paramInfo) && paramInfo.length() > 0 && paramInfo != null) {
				Par58OrderParam par58OrderParam = (Par58OrderParam) partnerService.transReqInfo(paramInfo);
				Date startTime = par58OrderParam.getStartTime();// 获取创建时间
				Date endTime = par58OrderParam.getEndTime();// 获取失效时间
				String trxStatus = par58OrderParam.getStatus();
				if (startTime == null || endTime == null || trxStatus == null || trxStatus.length() == 0) {
					map.put("status", "10201");
					map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode("10201", apiType));
					map.put("data", "");
				} else {
					String sTime = DateUtils.toString(startTime, "yyyy-MM-dd");
					String eTime = DateUtils.toString(endTime, "yyyy-MM-dd");
					if (DateUtils.getDistinceMonth(sTime, eTime) <= 3) {
						// 根据创建时间查询凭证信息
						String jsonStrArr = partnerService.findVouInfoByActiveDate(partnerNo, startTime, endTime, trxStatus);
						if (!"[10202]".equals(jsonStrArr) && jsonStrArr != null && jsonStrArr.length() > 0) {
							String rspCode = "10000";
							map.put("status", rspCode);
							map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(rspCode, apiType));
							map.put("data", jsonStrArr);
						} else {
							logger.info("++++++partnerNo=" + partnerNo + "++++++++++rspCode=" + jsonStrArr + "++++++++++++++++++++++");
							map.put("status", jsonStrArr);
							map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(jsonStrArr, apiType));
							map.put("data", "");
						}
					} else {
						logger.info("++++++++partnerNo=" + partnerNo + "++++++++" + startTime + "+++++and+++" + endTime + "++++error+++++++++++++++++");
						map.put("status", "10100");
						map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode("10100", apiType));
						map.put("data", "");
					}
				}
			}
		} catch (Exception e) {
			logger.debug(e);
			map.put("status", "10100");
			map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode("10100", apiType));
			map.put("data", "");
			e.printStackTrace();
		} finally {
			logger.info("++++partnerNo=" + partnerNo + "+++++++jsonMap=" + map + "+++++++++++++++++++");
			dataReturn = partnerService.generateRspHmac(map, partnerInfo.getKeyValue());// 响应的密文或者签名数据
		}
		try {
			response.getWriter().write(dataReturn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 基于券修改时间进行券信息查询接口-58
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/partner/qryVouByUpDate.do")
	public void qryVouByLastUpdateDate(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		String apiType = "";// api类型
		String dataReturn = "";// 返回值
		String partnerNo = getPartnerNo(request);
		String desStr = getPartnerdDes(request);
		PartnerInfo partnerInfo = null;// 分销商信息
		PartnerService partnerService = null;
		try {
			partnerInfo = partnerCommonService.qryAvaParterByNoInMem(partnerNo);// 根据partnerNo从mem中取出PartnerInfo
			apiType = partnerInfo.getApiType();
			String reqIp = StringUtils.getIpAddr(request);
			partnerService = partnerServiceFactory.getPartnerService(apiType);// 获取工厂业务实现
			String paramInfo = partnerService.checkHmacData(desStr, partnerInfo, reqIp);// 检查验签、加密以及IP.只做3DES验证
			if (!"10100".equals(paramInfo) && paramInfo.length() > 0 && paramInfo != null) {
				Par58OrderParam par58OrderParam = (Par58OrderParam) partnerService.transReqInfo(paramInfo);
				Date startTime = par58OrderParam.getStartTime();
				Date endTime = par58OrderParam.getEndTime();
				String trxStatus = par58OrderParam.getStatus();
				if (startTime == null || endTime == null || trxStatus == null || trxStatus.length() == 0) {
					map.put("status", "10201");
					map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode("10201", apiType));
					map.put("data", "");
				} else {
					String sTime = DateUtils.toString(startTime, "yyyy-MM-dd");
					String eTime = DateUtils.toString(endTime, "yyyy-MM-dd");
					if (DateUtils.getDistinceMonth(sTime, eTime) <= 3) {// 判断更新时间的时间差小于等于三个月
						String jsonStrArr = partnerService.findVouInfoByLastUpdateDate(partnerNo, startTime, endTime, trxStatus);
						if (!"[10202]".equals(jsonStrArr) && jsonStrArr != null && jsonStrArr.length() > 0) {
							String rspCode = "10000";
							map.put("status", rspCode);
							map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(rspCode, apiType));
							map.put("data", jsonStrArr);
						} else {
							logger.info("++++++partnerNo=" + partnerNo + "++++++++++rspCode=" + jsonStrArr + "++++++++++++++++++++++");
							map.put("status", jsonStrArr);
							map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(jsonStrArr, apiType));
							map.put("data", "");
						}
					} else {
						logger.info("++++++++partnerNo=" + partnerNo + "+++++++++" + startTime + "+++++and+++" + endTime + "++++error++++");
						map.put("status", "10100");
						map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode("10100", apiType));
						map.put("data", "");
					}
				}
			}
		} catch (Exception e) {
			logger.debug(e);
			map.put("status", "10100");
			map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode("10100", apiType));
			map.put("data", "");
			e.printStackTrace();
		} finally {
			logger.info("++++partnerNo=" + partnerNo + "+++++++jsonMap=" + map + "+++++++++++++++++++");
			dataReturn = partnerService.generateRspHmac(map, partnerInfo.getKeyValue());// 响应的密文或者签名数据
		}
		try {
			response.getWriter().write(dataReturn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 淘宝分销商统一入口
	 * 
	 * @return
	 */
	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/partner/synchroTaobaoTrxOrder.do")
	public void synchroTaobaoTrxOrder(HttpServletRequest request, HttpServletResponse response) {

		try {
			// 淘宝请求参数为GBK
			request.setCharacterEncoding("GBK");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		String method = request.getParameter("method");// 淘宝请求类型
		logger.info("+++++++++++synchroTaobaoTrxOrder.do:method=" + method + "+++++++++++++++++++");
		String dataReturn = "";// 淘宝响应状态值
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			StringBuilder paramBuilder = new StringBuilder();
			Enumeration<String> enume = request.getParameterNames();
			while (enume.hasMoreElements()) {
				String key = enume.nextElement();
				String[] value = request.getParameterValues(key);
				if (paramBuilder.toString().length() > 0) {
					paramBuilder.append("&");
				}
				logger.info("+++++++++++value[0]=" + value[0] + "+++++++++++++++++++");
				paramBuilder.append(key).append("=").append(value[0]);

			}
			String queryString = paramBuilder.toString();
			logger.info("+++++++++++queryString-->" + queryString + "+++++++++++++++++++");

			String partnerNo = request.getParameter("taobao_sid");
			PartnerInfo partnerInfo = partnerCommonService.qryAvaParterByNoInMem(partnerNo);
			List<Long> userIdList = partnerCommonService.qryAllUserIdByNoInMem(partnerNo);

			String apiType = partnerInfo.getApiType();
			String reqIp = StringUtils.getIpAddr(request);// 获取服务器IP
			PartnerService partnerService = partnerServiceFactory.getPartnerService(apiType);
			String paramInfo = partnerService.checkHmacData(queryString.toString(), partnerInfo, reqIp);// 传入密文和商家编号以及与请求IP
			if ("10100".equals(paramInfo)) {
				dataReturn = "500";
			} else {
				logger.info("++++++++ip AND sign is SUCCESS+++++++++paramInfo=" + paramInfo);
				ParTaobaoOrderParam parTaobaoOrderParam = (ParTaobaoOrderParam) partnerService.transReqInfo(paramInfo);
				parTaobaoOrderParam.setUserId(partnerInfo.getUserId().toString());// 当前有效user_id
				parTaobaoOrderParam.setUserIdList(userIdList);// 隶属该分销商的所有userId
				parTaobaoOrderParam.setSession(partnerInfo.getSessianKey());
				parTaobaoOrderParam.setAppKey(partnerInfo.getDescription());
				parTaobaoOrderParam.setKeyValue(partnerInfo.getKeyValue());
				parTaobaoOrderParam.setNoticeKeyValue(partnerInfo.getNoticeKeyValue());
				parTaobaoOrderParam.setClientIp(StringUtils.getIpAddr(request));
				if ("send".equals(method)) {
					// 调用下单接口
					dataReturn = partnerService.synchroTrxOrder(parTaobaoOrderParam, partnerNo);
				} else if ("resend".equals(method)) {
					// 接收重新发码通知
					dataReturn = partnerService.noTscResendVoucher(parTaobaoOrderParam);
				} else if ("cancel".equals(method)) {
					// 接收退款成功通知
					dataReturn = partnerService.processTrxOrder(parTaobaoOrderParam);
				} else if ("modified".equals(method)) {
					// 接收用户修改手机通知
					dataReturn = partnerService.noTscResendVoucher(parTaobaoOrderParam);
				}
				/*
				 * else if("mrights".equals(method)){ //接受维权成功通知 }
				 */
			}
		} catch (Exception e) {
			dataReturn = "500";
			e.printStackTrace();
		}

		map.put("code", dataReturn);
		String jsonMapStr = JsonUtil.getJsonStringFromMap(map);
		// request.setAttribute("status",jsonMapStr);
		logger.info("++++++++partnerNo=" + request.getParameter("taobao_sid") + "+++++++++++++++++jsonMapStr=" + jsonMapStr);
		try {
			response.getWriter().write(jsonMapStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 基于券id进行查询58订单
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/partner/qryByVoucherId.do")
	public void qryByVoucherId(HttpServletRequest request, HttpServletResponse response) {

		// 调用58查询接口
		String ticketId = String.valueOf(request.getParameter("voucherId"));
		String tickets[] = new String[] { ticketId };
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		ticketId = Arrays.toString(tickets);
		jsonMap.put("ticketIds", ticketId);
		String jsonStrArr = JsonUtil.getJsonStringFromMap(jsonMap);
		jsonStrArr = jsonStrArr.replace("\\", "");
		jsonStrArr = jsonStrArr.replace("\"[", "[\"");
		jsonStrArr = jsonStrArr.replace("]\"", "\"]");
		String param = PartnerUtil.cryptDes(jsonStrArr, "304faa16a5c74484b33ce57427a2c13b");

		Map<String, String> map = new HashMap<String, String>();
		map.put("m", "emc.groupbuy.ticket.findinfo.byid");
		map.put("sn", "1");
		map.put("appid", "100532");
		map.put("f", "json");
		map.put("param", param);
		logger.info("+++++++++++++58TC++++++++++qryVouBy+++map:" + map);
		List<String> responseStr = null;
		try {
			responseStr = HttpUtils.URLPost("http://eapi.58.com/api/rest", map);
			logger.info("++++++++++++responseStr=++++" + responseStr);
			if (responseStr.size() == 0 || responseStr == null) {
				response.getWriter().write("responseStr.size() == 0 || responseStr == null");
			} else {
				String status = "";// 返回数据状态
				String msg = "";// 返回数据状态信息
				String data = "";// 返回数据信息
				// String trxStatus = "";//返回数据订单信息状态
				// String result = "";//验券成功与否
				String currentResult = responseStr.get(0);
				Map<String, Object> objMap = JsonUtil.getMapFromJsonString(currentResult);
				if (objMap.get("status") != null) {
					status = objMap.get("status").toString();
				}
				if (objMap.get("msg") != null) {
					msg = objMap.get("msg").toString();
				}
				data = objMap.get("data").toString();
				// String desryptStr =
				// PartnerUtil.decryptDes(currentResult,partnerInfo.getKeyValue());
				logger.info("+++++58TC++++++++++qryVoucherInfoToPar++++++++ticketId=" + ticketId + "+++++++++" + "++++++++");
				if ("10000".equals(status)) {
					if (data == null || data.equals("")) {
						response.getWriter().write("data == null || data.equals(\"\")");
					} else {
						String returnJson = PartnerUtil.decryptDes(data, "304faa16a5c74484b33ce57427a2c13b");
						returnJson = returnJson.replace("[", "");
						returnJson = returnJson.replace("]", "");
						logger.info("+++++58TC++++++++++qryVoucherInfoToPar++++++++returnJson=" + returnJson + "+++++++++++++++++");
						// 58订单状态判断
						response.getWriter().write(returnJson + "<br>状态对应表：0 未使用 1 已使用 2 已退款 3退款中4 已锁定9 过期  10未使用退款，11已过期退款，12已消费退款（58责任），13已消费退款（商家责任）");
					}
				} else {
					/*
					 * 0 未使用 1 已使用 2 已退款 3退款中 4 已锁定 9 过期
					 * 10未使用退款，11已过期退款，12已消费退款（58责任），13已消费退款（商家责任）
					 */
					response.getWriter().write("!\"10000\".equals(status)+" + status);
					logger.info("++++++++++++status=" + status + "++++++++msg=" + msg);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 京东分销商统一入口
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/partner/synchro360buyTrxOrder.do")
	public void synchro360buyTrxOrder(HttpServletRequest request, HttpServletResponse response) {
		logger.info("++++++++++++++++++++/partner=360buy/synchro360buyTrxOrder.do++++++++++++");
		String partnerNo = "";
		try {
			String sourceMessage = Par360buyOrderGenerator.get360buyReqMessage(request.getInputStream());
			partnerNo = Par360buyOrderGenerator.getPartnerNo(sourceMessage); // 获得报文传来的partnerNo
			logger.info("+++++++++++++++partnerNo=" + partnerNo + "nchro360buyTrxOrder request message:" + sourceMessage.toString());
			PartnerInfo partnerInfo = partnerCommonService.qryAvaParterByNoInMem(partnerNo);
			List<Long> userIdList = partnerCommonService.qryAllUserIdByNoInMem(partnerNo);
			PartnerService partenerService = partnerServiceFactory.getPartnerService(PartnerApiType.BUY360.name()); // 获得京东分销商service

			String reqIp = StringUtils.getIpAddr(request);// 获取服务器IP
			logger.info("+++++++++++++++partnerNo=" + partnerNo + "++++reqIp=" + reqIp);
			// 校验报文合法性
			String paramInfo = partenerService.checkHmacData(sourceMessage, partnerInfo, reqIp);

			// 错误返回 -校验失败
			if ("10100".equals(paramInfo)) {
				Par360buyOrderParam responseParam = new Par360buyOrderParam(); // 响应报文
				responseParam.setVenderId(partnerInfo.getPartnerNo()); // 分销商号
				responseParam.setResultCode("-100"); // 返回码-- 身份验证失败
				responseParam.setResultMessage("illegal request"); // 返回信息

				String resMessage = partenerService.generateRspHmac(responseParam, partnerInfo.getKeyValue());
				response.getWriter().write(resMessage);
				return;
			}

			Par360buyOrderParam par360buyParam = (Par360buyOrderParam) partenerService.transReqInfo(paramInfo);
			par360buyParam.setUserId(partnerInfo.getUserId());
			par360buyParam.setUserIdList(userIdList);
			par360buyParam.setPartnerNo(partnerNo);
			par360buyParam.setClientIp(reqIp);
			String data = "";
			String resultCode = "-1";
			String resultMessage = "interface handle error";

			// 请求名称不存在
			if (null != par360buyParam && !"".equals(StringUtils.toTrim(par360buyParam.getMessage()))) {

				// 订单同步
				if (par360buyParam.getMessage().endsWith("SendOrderRequest")) {
					data = partenerService.synchroTrxOrder(par360buyParam, partnerNo);
					resultCode = "200";
					resultMessage = "success";
				}

				// 接收退款成功通知
				else if (par360buyParam.getMessage().endsWith("SendOrderRefundRequest")) {
					data = partenerService.processTrxOrder(par360buyParam);
					resultCode = "200";
					resultMessage = "success";
				}

				// 团购销量查询接口
				else if (par360buyParam.getMessage().endsWith("QueryTeamSellCountRequest")) {
					data = partnerGoodsService.processGoodsSellCount(par360buyParam);
					resultCode = "200";
					resultMessage = "success";
				}
			}

			Par360buyOrderParam responseParam = new Par360buyOrderParam(); // 响应报文
			responseParam.setVenderId(partnerInfo.getPartnerNo()); // 分销商号
			responseParam.setData(data);
			responseParam.setResultCode(resultCode); // 返回码-- 接口调用异常
			responseParam.setResultMessage(resultMessage); // 返回信息--接口调用异常
			String resMessage = partenerService.generateRspHmac(responseParam, partnerInfo.getKeyValue());
			response.getWriter().write(resMessage);

			return;

		} catch (Exception e) {
			logger.error("partnerNo:" + partnerNo + "360buy interface handle error", e);
			Par360buyOrderParam resParam = new Par360buyOrderParam(); // 响应报文
			resParam.setVenderId(""); // 分销商号
			resParam.setResultCode("-1"); // 返回码-- 接口调用异常
			resParam.setResultMessage("interface handle error"); // 返回信息--接口调用异常
			resParam.setData("");
			String resMessage = Par360buyOrderGenerator.packageResponseMsg(resParam, "error");
			try {
				response.getWriter().write(resMessage);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
	}

	/**
	 * 统一查询接口
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/partner/queryGoodsBeforePay.do")
	public void queryGoodsBeforePay(HttpServletRequest request, HttpServletResponse response) {
		logger.info("===into queryGoodsBeforePay===");
		String sign = request.getParameter("sign");
		String params = request.getParameter("params");
		// 做为partnerNO
		String publicKey = request.getParameter("appKey");

		response.setCharacterEncoding("UTF-8");
		String apiType = PartnerApiType.TUAN800.name();
		Map<String, Object> map = new HashMap<String, Object>();
		PartnerService partnerService = null;
		try {
			PartnerInfo partnerInfo = partnerCommonService.qryAvaParterByNoInMem(publicKey);
			if (partnerInfo != null) {
				apiType = partnerInfo.getApiType();
			}
			String reqIP = StringUtils.getIpAddr(request);// 获取服务器IP

			partnerService = partnerServiceFactory.getPartnerService(apiType);
			// 传入密文和商家编号以及与预置IP
			String paramInfo = partnerService.checkHmacData(params, publicKey, sign, partnerInfo, reqIP);
			if (ParErrorMsgUtil.T800_OTHER_ERROR.equals(paramInfo) || ParErrorMsgUtil.T800_SIGN_MISMATCH.equals(paramInfo)) {
				map.put("ret", paramInfo);
				map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(paramInfo, apiType));
			} else {
				// 调用团800参数数据转换工具类
				ParT800OrderParam par800OrderParam = (ParT800OrderParam) partnerService.transReqInfo(paramInfo);

				Map<String, Object> maxcountMap = trxSoaService.getMaxCountAndIsAvbByIdInMem(Long.parseLong(par800OrderParam.getGoodsId()));
				Long maxCount = Long.parseLong(maxcountMap.get("maxcount").toString());
				// 是否下线：0表示下线不可用
				String isavaliable = maxcountMap.get("isavaliable").toString();
				if ("0".equals(isavaliable)) {
					map.put("ret", ParErrorMsgUtil.T800_OFF_LINE);
					map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_OFF_LINE, apiType));
				} else {
					List<Map<String, Object>> salesCountMapList = goodsSoaDao.getGoodsProfileByGoodsid(par800OrderParam.getGoodsId());// 商品已经购买量

					Long salesCount = Long.valueOf(salesCountMapList.get(0).get("salesCount").toString());
					Long allowBuyCount = maxCount - salesCount > 0 ? maxCount - salesCount : 0;// 总量限购如果为负，则归零
					if (Long.parseLong(par800OrderParam.getProdCount()) > allowBuyCount.longValue()) {
						map.put("ret", ParErrorMsgUtil.T800_SALE_OUT);
						map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_SALE_OUT, apiType));
					} else {
						Goods goods = goodsService.findById(Long.parseLong(par800OrderParam.getGoodsId()));
						if (goods != null) {
							if (goods.getSendRules() != 0) {
								map.put("ret", ParErrorMsgUtil.T800_SALE_OUT);
								map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_SALE_OUT, apiType));
							} else {
								map.put("ret", ParErrorMsgUtil.T800_SUCCESS);
							}
						} else {
							map.put("ret", ParErrorMsgUtil.T800_OFF_LINE);
							map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_OFF_LINE, apiType));
						}
					}
				}
			}
		} catch (Exception e) {
			map.put("ret", ParErrorMsgUtil.T800_OFF_LINE);
			map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_OFF_LINE, apiType));
			e.printStackTrace();
		}
		try {
			String jsonStr = JsonUtil.getJsonStringFromMap(map);
			response.getWriter().write(jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 团800订单同步
	 * 
	 * @param request
	 * @param response
	 * @author wz.gu for tuan800 synchro order 2012-11-08
	 */
	@RequestMapping("/partner/synchroTuan800TrxOrder.do")
	public void synchroTuan800TrxOrder(HttpServletRequest request, HttpServletResponse response) {

		String sign = request.getParameter("sign");
		String params = request.getParameter("params");
		// 做为partnerNO
		String publicKey = request.getParameter("appKey");

		response.setCharacterEncoding("UTF-8");
		Map<String, Object> map = new HashMap<String, Object>();
		String apiType = PartnerApiType.TUAN800.name();
		PartnerService partnerService = null;
		String dataReturn = null;
		String keyValue = "";

		logger.info("++++++++++++++++++++/partner/synchroTuan800TrxOrder.do++++++++++++++++++");

		try {
			PartnerInfo partnerInfo = partnerCommonService.qryAvaParterByNoInMem(publicKey);

			logger.info("++++++++++++++params=" + params + "++++++++++partnerNo=" + publicKey);
			if (partnerInfo != null) {
				apiType = partnerInfo.getApiType();
				keyValue = partnerInfo.getKeyValue();
			}

			String reqIP = StringUtils.getIpAddr(request);// 获取服务器IP
			Long userId = partnerInfo.getUserId();
			partnerService = partnerServiceFactory.getPartnerService(apiType);
			// 传入密文和商家编号以及与预置IP
			String paramInfo = partnerService.checkHmacData(params, publicKey, sign, partnerInfo, reqIP);
			if (ParErrorMsgUtil.T800_OTHER_ERROR.equals(paramInfo) || ParErrorMsgUtil.T800_SIGN_MISMATCH.equals(paramInfo)) {
				map.put("ret", paramInfo);
				map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(paramInfo, apiType));
			} else {
				// 调用团800参数数据转换工具类
				ParT800OrderParam par800OrderParam = (ParT800OrderParam) partnerService.transReqInfo(paramInfo);

				String payPrice = par800OrderParam.getPayPrice();
				String totalPrice = par800OrderParam.getTotalPrice();
				String prdCount = par800OrderParam.getProdCount();

				double payPriceDou = Double.parseDouble(payPrice);
				double totalPriceDou = Double.parseDouble(totalPrice);

				if (totalPriceDou < Amount.mul(payPriceDou, Double.parseDouble(prdCount))) {
					map.put("ret", ParErrorMsgUtil.T800_AMOUNT_MISMATCH);
					map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_AMOUNT_MISMATCH, apiType));
				} else {
					List<Long> userIdList = partnerCommonService.qryAllUserIdByNoInMem(publicKey);
					par800OrderParam.setUserId(userId);
					par800OrderParam.setUserIdList(userIdList);
					par800OrderParam.setClientIp(StringUtils.getIpAddr(request));
					dataReturn = partnerService.synchroTrxOrder(par800OrderParam, publicKey);
					if (StringUtils.isEmpty(dataReturn) || ParErrorMsgUtil.T800_OTHER_ERROR.equals(dataReturn)) {
						map.put("ret", ParErrorMsgUtil.T800_OTHER_ERROR);
						map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_OTHER_ERROR, apiType));
					}
				}
			}
		} catch (Exception e) {
			map.put("ret", ParErrorMsgUtil.T800_OTHER_ERROR);
			map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_OTHER_ERROR, apiType));
			e.printStackTrace();
		}
		if (map != null && !map.isEmpty()) {
			dataReturn = partnerService.generateRspHmac(map, keyValue);
		}
		logger.info("++++++++partnerNo=" + publicKey + "+++++++++++++++++dataReturn=" + dataReturn);
		try {
			response.getWriter().write(dataReturn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param request
	 * @param response
	 * @author wz.gu for tuan800 refund order 2012-11-08
	 */
	@RequestMapping("/partner/refundT800TrxOrder.do")
	public void refundT800TrxOrder(HttpServletRequest request, HttpServletResponse response) {

		String sign = request.getParameter("sign");
		String params = request.getParameter("params");
		// 即partnerNO
		String publicKey = request.getParameter("appKey");

		logger.info("++++++++++++++++++++/partner/refundTuan800TrxOrder.do++++++++++++++++++");

		response.setCharacterEncoding("UTF-8");
		String apiType = PartnerApiType.TUAN800.name();
		PartnerService partnerService = null;
		String keyValue = "";
		String dataReturn = "";
		Map<String, Object> jsonMap = new HashMap<String, Object>();

		try {
			PartnerInfo partnerInfo = partnerCommonService.qryAvaParterByNoInMem(publicKey);
			if (partnerInfo != null) {
				apiType = partnerInfo.getApiType();
				keyValue = partnerInfo.getKeyValue();
			}
			partnerService = partnerServiceFactory.getPartnerService(apiType);
			String reqIp = StringUtils.getIpAddr(request);// 获取服务器IP
			if (PartnerApiType.TUAN800.name().equals(apiType)) {
				// 传入密文和商家编号以及与预置IP
				String paramInfo = partnerService.checkHmacData(params, publicKey, sign, partnerInfo, reqIp);
				if (ParErrorMsgUtil.T800_OTHER_ERROR.equals(paramInfo) || ParErrorMsgUtil.T800_SIGN_MISMATCH.equals(paramInfo)) {
					jsonMap.put("ret", paramInfo);
					jsonMap.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(paramInfo, apiType));
				} else {
					// 调用团800参数数据转换工具类
					ParT800OrderParam parT800OrderParam = (ParT800OrderParam) partnerService.transReqInfo(paramInfo);
					List<Long> userIdList = partnerCommonService.qryAllUserIdByNoInMem(publicKey);
					parT800OrderParam.setUserIdList(userIdList);// 隶属该分销商的所有userId

					if (!StringUtils.isEmpty(org.apache.commons.lang.StringUtils.trim(parT800OrderParam.getOrderId())) || !StringUtils.isEmpty(org.apache.commons.lang.StringUtils.trim(parT800OrderParam.getVoucherCode()))) {
						dataReturn = partnerService.processTrxOrder(parT800OrderParam);
					} else {
						logger.info("++++++++partnerNo=" + publicKey + "+++++++++++++++++请求数据异常" + "++++++++");
						// 数据不正确
						jsonMap.put("ret", ParErrorMsgUtil.T800_OTHER_ERROR);
						jsonMap.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_OTHER_ERROR, apiType));
					}
				}
			} else {
				logger.info("++++++++partnerNo=" + publicKey + "+++++++++++++++++分销商不符++++++++");
				jsonMap.put("ret", ParErrorMsgUtil.T800_OTHER_ERROR);
				jsonMap.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_OTHER_ERROR, apiType));
			}

		} catch (Exception e) {
			logger.info("++++++++partnerNo=" + publicKey + "+++++++++++++++++系统异常++++++++");
			jsonMap.put("ret", ParErrorMsgUtil.T800_OTHER_ERROR);
			jsonMap.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_OTHER_ERROR, apiType));
			e.printStackTrace();
		}
		if (jsonMap != null && !jsonMap.isEmpty()) {
			dataReturn = partnerService.generateRspHmac(jsonMap, keyValue);
		}
		logger.info("++++++++partnerNo=" + publicKey + "+++++++++++++++++dataReturn=" + dataReturn);
		try {
			response.getWriter().write(dataReturn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 为团800提供订单查询接口
	 * 
	 * @param request
	 * @param response
	 * @author wz.gu
	 */
	@RequestMapping("/partner/queryTrxOrderFromT800.do")
	public void queryTrxOrderFromT800(HttpServletRequest request, HttpServletResponse response) {

		String sign = request.getParameter("sign");
		String params = request.getParameter("params");
		// 即partnerNo
		String publicKey = request.getParameter("appKey");

		logger.info("++++++++++++++++++++/partner/queryTrxOrderFromT800.do++++++++++++++++++");

		response.setCharacterEncoding("UTF-8");
		Map<String, Object> map = new HashMap<String, Object>();
		String apiType = PartnerApiType.TUAN800.name();
		PartnerService partnerService = null;

		String keyValue = "";
		String dataReturn = "";

		try {
			PartnerInfo partnerInfo = partnerCommonService.qryAvaParterByNoInMem(publicKey);

			if (partnerInfo != null) {
				keyValue = partnerInfo.getKeyValue();
				apiType = partnerInfo.getApiType();
			}
			partnerService = partnerServiceFactory.getPartnerService(apiType);
			String reqIp = StringUtils.getIpAddr(request);// 获取服务器IP
			if (PartnerApiType.TUAN800.name().equals(apiType)) {
				// 传入密文和商家编号以及与预置IP
				String paramInfo = partnerService.checkHmacData(params, publicKey, sign, partnerInfo, reqIp);
				if (ParErrorMsgUtil.T800_OTHER_ERROR.equals(paramInfo) || ParErrorMsgUtil.T800_SIGN_MISMATCH.equals(paramInfo)) {
					map.put("ret", paramInfo);
					map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(paramInfo, apiType));
				} else {
					// 调用团800参数数据转换工具类
					ParT800OrderParam parT800OrderParam = (ParT800OrderParam) partnerService.transReqInfo(paramInfo);
					if (!StringUtils.isEmpty(parT800OrderParam.getOrderId()) || !StringUtils.isEmpty(parT800OrderParam.getVoucherCode())) {
						dataReturn = partnerService.findTrxorder(parT800OrderParam, publicKey);
						if (ParErrorMsgUtil.T800_OTHER_ERROR.equals(dataReturn)) {
							map.put("ret", ParErrorMsgUtil.T800_OTHER_ERROR);
							map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(dataReturn, apiType));
						}
					} else {// 传过来的数据不正确
						map.put("ret", ParErrorMsgUtil.T800_OTHER_ERROR);
						map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_OTHER_ERROR, apiType));
					}
				}
			} else {
				logger.info("++++++++++++++++++++分销商不符++++++++++++++++++");
				map.put("ret", ParErrorMsgUtil.T800_OTHER_ERROR);
				map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_OTHER_ERROR, apiType));
			}
		} catch (Exception e) {
			map.put("ret", ParErrorMsgUtil.T800_OTHER_ERROR);
			map.put("msg", ParErrorMsgUtil.getParErrorMsgByCode(ParErrorMsgUtil.T800_OTHER_ERROR, apiType));
			e.printStackTrace();
		}
		if (map != null && !map.isEmpty()) {
			dataReturn = partnerService.generateRspHmac(map, keyValue);
		}
		try {
			response.getWriter().write(dataReturn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用团800接口查询订单
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/partner/queryTrxOrderToT800.do")
	public void queryTrxOrderToT800(HttpServletRequest request, HttpServletResponse response) {

		response.setCharacterEncoding("UTF-8");

		// 团800提供的URL
		String url = Configuration.getInstance().getValue("tuan800_queryurl");// "http://110.173.1.14:8020/client-api/query-order";

		// 即 partnerNO
		String publicKey = Configuration.getInstance().getValue("tuan800_publicKey");// "859de8d7112c4bc7b6c768760102ca62";
		String orderId = request.getParameter("orderId");
		String vouCode = request.getParameter("voucherCode");

		Map<String, Object> paramMap = new TreeMap<String, Object>();
		if (!StringUtils.isEmpty(orderId)) {
			paramMap.put("site_order_no", orderId);
		}
		if (!StringUtils.isEmpty(vouCode)) {
			paramMap.put("coupons", vouCode);
		}
		String apiType = PartnerApiType.TUAN800.name();
		PartnerService partnerService = null;
		logger.info("++++++++++++++++++++/partner/queryTrxOrderToT800.do++++++++++++++++++");
		try {
			PartnerInfo partnerInfo = partnerCommonService.qryAvaParterByNoInMem(publicKey);
			if (partnerInfo != null) {
				apiType = partnerInfo.getApiType();
				partnerService = partnerServiceFactory.getPartnerService(apiType);

				if (!paramMap.isEmpty()) {
					String params = JsonUtil.getJsonStringFromMap(paramMap);
					params = params.replace("\\", "");
					params = params.replace("\"[", "[\"");
					params = params.replace("]\"", "\"]");
					String sign = partnerService.checkHmacData(params, publicKey, partnerInfo.getKeyValue());
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("params", params);
					map.put("sign", sign);
					map.put("appKey", publicKey);
					List<String> responseStr = null;
					responseStr = HttpUtils.URLPost(url, map);
					logger.info("++++++++++++responseStr=++++" + responseStr);
					if (responseStr.size() == 0 || responseStr == null) {
						response.getWriter().write("responseStr.size() == 0 || responseStr == null");
					} else {
						String result = ""; // 请求返回的结果状态
						String msg = ""; // 请求返回的消息
						String data = "";// 请求返回的数据

						String currentResult = responseStr.get(0);
						Map<String, Object> objMap = JsonUtil.getMapFromJsonString(currentResult);
						if (objMap.get("ret") != null) {
							result = objMap.get("ret").toString();
						}
						if (objMap.get("msg") != null) {
							msg = objMap.get("msg").toString();
						}
						if (objMap.get("data") != null) {
							data = objMap.get("data").toString();
						}

						logger.info("++++++++++++++queryTrxOrderToT800+++++++++result=" + result + "+++++++++++++++");

						if ("0".equals(result)) {
							if (data == null || data.equals("")) {
								response.getWriter().write("data == null || data.equals(\"\")");
							} else {
								logger.info("++++++++++++++queryTrxOrderToT800++++++++data = " + data + "+++++++++");
								// TODO 数据如何展示，未定
								response.getWriter().write(data);
							}
						} else {
							/**
							 * 0 请求成功 1 失败
							 */
							response.getWriter().write("获取信息失败");
							logger.info("++++++++++++result=" + result + "++++++++msg=" + msg);
						}
					}
				}
			} else {
				response.getWriter().write("此分销商不存在");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 1号店分销商统一入口
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/partner/synchro1mallTrxOrder.do")
	public void synchro1mallTrxOrder(HttpServletRequest request, HttpServletResponse response) {

		String method = StringUtils.toTrim(request.getParameter("method"));// 一号店请求类型
		String format = StringUtils.toTrim(request.getParameter("format"));
		logger.info("+++++++++++++++synchro1mallTrxOrder+++++++++++method=" + method);
		// 一号店编码为UTF-8
		response.setCharacterEncoding("UTF-8");
		Par1mallOrderParam param;
		try {
			StringBuilder paramBuilder = new StringBuilder();
			Enumeration<String> enume = request.getParameterNames();
			while (enume.hasMoreElements()) {
				String key = enume.nextElement();
				String[] value = request.getParameterValues(key);
				if (paramBuilder.toString().length() > 0) {
					paramBuilder.append("&");
				}
				paramBuilder.append(key).append("=").append(value[0]);
			}
			logger.info("++++++++1mall request message=" + paramBuilder.toString());
			String partnerNo = StringUtils.toTrim(request.getParameter("merchantId")); // 获得一号店发送的partnerNo
			if ("".equals(partnerNo)) {// 一号店无商家ID直接赋值
				partnerNo = Par1mallOrderGenerator.PARTERNO_1MALL;
			}
			PartnerInfo partnerInfo = partnerCommonService.qryAvaParterByNoInMem(partnerNo);
			List<Long> userIdList = partnerCommonService.qryAllUserIdByNoInMem(partnerNo);
			PartnerService partenerService = partnerServiceFactory.getPartnerService(PartnerApiType.YHD.name()); // 获得一号店分销商service

			String reqIp = StringUtils.getIpAddr(request);// 获取服务器IP
			logger.info("+++++++++++++++partnerNo=" + partnerNo + "++++reqIp=" + reqIp);
			String paramInfo = partenerService.checkHmacData(paramBuilder.toString(), partnerInfo, reqIp);// 传入密文和商家编号以及与请求IP
			logger.info("++++++1mall checkHmacData.... paramInfo=" + paramInfo + "+++++++++");
			if ("10100".equals(paramInfo)) {
				param = new Par1mallOrderParam();
				param.setErrorCount(1);
				param.setUpdateCount(0);
				param.setErrorCode("invalid.request");
				param.setErrorDes("非法的请求报文");
				param.setPkInfo("sign");
			} else {
				logger.info("+++++++++++++1mall+++message check ok+++++++++");
				param = (Par1mallOrderParam) partenerService.transReqInfo(paramBuilder.toString());
				param.setUserIdList(userIdList);
				param.setUserId(partnerInfo.getUserId());
				param.setPartnerNo(partnerNo);
				param.setClientIp(reqIp);
				String resMessage = "";
				// 订单信息通知接口
				if (method.equals("yhd.group.buy.order.inform")) {
					logger.info("+++++++++++++1mall+++synchroTrxOrder+++++++++");
					param.setCheckCode(partnerInfo.getKeyValue());
					param.setSecretKey(partnerInfo.getSessianKey());
					resMessage = partenerService.synchroTrxOrder(param, partnerNo);
				}
				// 消费券退款申请
				else if (method.equals("yhd.group.buy.refund.request")) {
					logger.info("+++++++++++++1mall+++processTrxOrder+++++++++");
					param.setCheckCode(partnerInfo.getKeyValue());
					param.setSecretKey(partnerInfo.getSessianKey());
					resMessage = partenerService.processTrxOrder(param);
				}
				// 消费券短信重新发送
				else if (method.equals("yhd.group.buy.voucher.resend")) {
					logger.info("+++++++++++++1mall+++noTscResendVoucher+++++++++");
					resMessage = partenerService.noTscResendVoucher(param);
				}
				// 查询消费券信息
				else if (method.equals("yhd.group.buy.vouchers.get")) {
					logger.info("+++++++++++++1mall+++findVoucher+++++++++");
					resMessage = partenerService.findVoucher(param, partnerNo);
				}
				try {
					logger.info("+++++1mall response message=" + resMessage);
					response.getWriter().write(resMessage);
				} catch (IOException e1) {
					logger.error(e1);
				}
				return;
			}
		} catch (Exception e) {
			logger.error("++++++++++++++1mall interface handle error", e);
			param = new Par1mallOrderParam();
			param.setErrorCount(1);
			param.setUpdateCount(0);
			param.setTotalCount(0);
			param.setFormat(format.equals("") ? Par1mallOrderGenerator.FORMAT_JSON : format);
			param.setErrorCode("yhd.invoices.invalid.message");
			param.setErrorDes("请求报文非法或请求参数有误");
			param.setPkInfo("");
		}

		String resMessage = Par1mallOrderGenerator.packageResponseMsg(param);
		logger.info("+++++1mall response message=" + resMessage);
		try {
			response.getWriter().write(resMessage);
		} catch (IOException e1) {
			logger.error(e1);
		}
		return;
	}

}