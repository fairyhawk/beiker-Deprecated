package com.beike.common.bean.trx;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.util.Configuration;
import com.beike.util.DigestUtil;
import com.beike.util.HttpUtils;

public class PaymentInfoGenerator {

	private static Log log = LogFactory.getLog(PaymentInfoGenerator.class);
	private static String p1_MerId = Configuration.getInstance().getValue(
			"yeepayMerId");
	private static String yeepayCommonReqURL = Configuration.getInstance()
			.getValue("yeepayCommonReqURL");
	private static String queryRefundReqURL = Configuration.getInstance()
			.getValue("yeepayQueryRefundReqURL");

	private static String yeepayCallbackURL = Configuration.getInstance()
			.getValue("yeepayCallbackURL");
	private static String keyValue = Configuration.getInstance().getValue(
			"yeepayKeyValue"); //
	private static String query_Cmd = "QueryOrdDetail"; //

	private static String buy_Cmd = "Buy"; // 
	private static String refund_Cmd = "RefundOrd"; // 
	private static String decodeCharset = "GBK"; // 
	//private static String EMPTY = "";
	private static String p4_Cur = "CNY";
	private static String pr_NeedResponse = "1";

	private static String noLoadingPage = "1";// 不显示易宝网关LOGO

	public static String getReqMd5HmacForOnlinePayment(String p2_Order,
			String p3_Amt, String p5_Pid, String p6_Pcat, String p7_Pdesc,
			String p9_SAF, String pa_MP, String pd_FrpId) {

		// String p5_Pid_GBK = "";
		// try {
		// p5_Pid_GBK = URLEncoder.encode(p5_Pid, decodeCharset);
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// if(p5_Pid!=null || !"".equals(p5_Pid)){
		// if(p5_Pid.length()>7){
		// p5_Pid=p5_Pid.substring(0, 7);
		// }
		// }

		StringBuffer sValue = new StringBuffer();

		sValue.append(buy_Cmd);

		sValue.append(p1_MerId);

		sValue.append(p2_Order);

		sValue.append(p3_Amt);

		sValue.append(p4_Cur);

		sValue.append(p5_Pid);

		sValue.append(p6_Pcat);

		sValue.append(p7_Pdesc);
		sValue.append(yeepayCallbackURL);
		sValue.append(p9_SAF);

		sValue.append(pa_MP);

		sValue.append(pd_FrpId);

		sValue.append(pr_NeedResponse);
		log.info("+++++++++++++++++++++paySource:" + sValue.toString());
		String sNewString = DigestUtil.hmacSign(sValue.toString(), keyValue);
		//String p5_Pid_Source = "";
		// try {
		// p5_Pid_Source=URLDecoder.decode(p5_Pid,decodeCharset);
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		Map<String, String> reParams = new HashMap<String, String>();
		reParams.put("p0_Cmd", buy_Cmd);
		reParams.put("p1_MerId", p1_MerId);
		reParams.put("p2_Order", p2_Order);
		reParams.put("p3_Amt", p3_Amt);
		reParams.put("p4_Cur", p4_Cur);
		reParams.put("p5_Pid", p5_Pid);
		reParams.put("p6_Pcat", p6_Pcat);
		reParams.put("p7_Pdesc", p7_Pdesc);
		reParams.put("p8_Url", yeepayCallbackURL);
		reParams.put("p9_SAF", p9_SAF);
		reParams.put("pa_MP", pa_MP);
		reParams.put("pd_FrpId", pd_FrpId);
		reParams.put("pr_NeedResponse", pr_NeedResponse);

		String payLinkInfopro = "";
		for (String item : reParams.keySet()) {
			payLinkInfopro += item + "=" + reParams.get(item) + "&";

		}
		payLinkInfopro += "noLoadingPage=" + noLoadingPage + "&";

		return yeepayCommonReqURL + "?" + payLinkInfopro + "hmac=" + sNewString;
	}

	/**
	 * 
	 * 
	 * @param p2_Order
	 * @return queryResult
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static QueryResult queryByOrder(String p2_Order) throws Exception {

		QueryResult qr = null;
		String hmac = DigestUtil.getHmac(new String[] { query_Cmd, p1_MerId,
				p2_Order }, keyValue);
		Map reParams = new HashMap();
		reParams.put("p0_Cmd", query_Cmd);
		reParams.put("p1_MerId", p1_MerId);
		reParams.put("p2_Order", p2_Order);
		reParams.put("hmac", hmac);
		List responseStr = null;

		try {
			log.debug("Begin http communications.data[" + reParams + "]");
			responseStr = HttpUtils.URLGet(queryRefundReqURL, reParams);
			log.debug("End http communications.responseStr.data[" + responseStr
					+ "]");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		if (responseStr.size() == 0) {
			throw new RuntimeException("No response.");
		}
		qr = new QueryResult();
		for (int t = 0; t < responseStr.size(); t++) {
			String currentResult = (String) responseStr.get(t);
			if (currentResult == null || currentResult.equals("")) {
				continue;
			}
			int i = currentResult.indexOf("=");
			//int j = currentResult.length();
			if (i >= 0) {
				String sKey = currentResult.substring(0, i);
				String sValue = currentResult.substring(i + 1);
				try {
					sValue = URLDecoder.decode(sValue, decodeCharset);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e.getMessage());
				}
				if (sKey.equals("r0_Cmd")) {
					qr.setR0_Cmd(sValue);
				} else if (sKey.equals("r1_Code")) {
					qr.setR1_Code(sValue);
				} else if (sKey.equals("r2_TrxId")) {
					qr.setR2_TrxId(sValue);
				} else if (sKey.equals("r3_Amt")) {
					qr.setR3_Amt(sValue);
				} else if (sKey.equals("r4_Cur")) {
					qr.setR4_Cur(sValue);
				} else if (sKey.equals("r5_Pid")) {
					qr.setR5_Pid(sValue);
				} else if (sKey.equals("r6_Order")) {
					qr.setR6_Order(sValue);
				} else if (sKey.equals("r8_MP")) {
					qr.setR8_MP(sValue);
				} else if (sKey.equals("rb_PayStatus")) {
					qr.setRb_PayStatus(sValue);
				} else if (sKey.equals("rc_RefundCount")) {
					qr.setRc_RefundCount(sValue);
				} else if (sKey.equals("rd_RefundAmt")) {
					qr.setRd_RefundAmt(sValue);
				} else if (sKey.equals("hmac")) {
					qr.setHmac(sValue);
				}
			}
		}
		if (!qr.getR1_Code().equals("1")) {
			throw new RuntimeException("Query fail.Error code:"
					+ qr.getR1_Code());
		}
		String newHmac = "";
		newHmac = DigestUtil.getHmac(new String[] { qr.getR0_Cmd(),
				qr.getR1_Code(), qr.getR2_TrxId(), qr.getR3_Amt(),
				qr.getR4_Cur(), qr.getR5_Pid(), qr.getR6_Order(),
				qr.getR8_MP(), qr.getRb_PayStatus(), qr.getRc_RefundCount(),
				qr.getRd_RefundAmt() }, keyValue);
		if (!newHmac.equals(qr.getHmac())) {
			throw new Exception("Hmac error.");
		}

		return (qr);

	}

	@SuppressWarnings("unchecked")
	public static RefundResult refundByTrxId(String p2_Order, String pb_TrxId,
			String p3_Amt, String p4_Cur, String p5_Desc) throws Exception {
		RefundResult rr = null;
		String hmac = DigestUtil.getHmac(new String[] { refund_Cmd, p1_MerId,
				p2_Order, pb_TrxId, p3_Amt, p4_Cur, p5_Desc }, keyValue);
		Map reParams = new HashMap();
		reParams.put("p0_Cmd", refund_Cmd);
		reParams.put("p1_MerId", p1_MerId);
		reParams.put("p2_Order", p2_Order);
		reParams.put("pb_TrxId", pb_TrxId);
		reParams.put("p3_Amt", p3_Amt);
		reParams.put("p4_Cur", p4_Cur);
		reParams.put("p5_Desc", p5_Desc);
		reParams.put("hmac", hmac);
		List responseStr = null;
		try {
			log.debug("++++++++++++++++refund request.data[" + reParams
					+ "]+++++++++++++++++++");
			System.out.print("++++++++++++++++refund request.data[" + reParams
					+ "]+++++++++++++++++++");
			responseStr = HttpUtils.URLGet(queryRefundReqURL, reParams);
			System.out.print("++++++++++++++++refund response.data["
					+ responseStr + "]+++++++++++++++++++");
			log.debug("++++++++++++++++++++++++++++refund response.data["
					+ responseStr + "]++++++++++++++++++++++++");
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		if (responseStr.size() == 0 || responseStr == null) {
			throw new Exception("No response.");
		}

		rr = new RefundResult();
		for (int t = 0; t < responseStr.size(); t++) {
			String currentResult = (String) responseStr.get(t);
			if (currentResult == null || currentResult.equals("")) {
				continue;
			}
			try {
				URLDecoder.decode(currentResult, decodeCharset);
			} catch (UnsupportedEncodingException e) {
				throw new Exception(e.getMessage());
			}
			int i = currentResult.indexOf("=");
			//int j = currentResult.length();
			if (i >= 0) {
				String sKey = currentResult.substring(0, i);
				String sValue = currentResult.substring(i + 1);
				if (sKey.equals("r0_Cmd")) {
					rr.setR0_Cmd(sValue);
				} else if (sKey.equals("r1_Code")) {
					rr.setR1_Code(sValue);
				} else if (sKey.equals("r2_TrxId")) {
					rr.setR2_TrxId(sValue);
				} else if (sKey.equals("r3_Amt")) {
					rr.setR3_Amt(sValue);
				} else if (sKey.equals("r4_Cur")) {
					rr.setR4_Cur(sValue);
				} else if (sKey.equals("hmac")) {
					rr.setHmac(sValue);
				}
			}
		}
		log.debug("++++++++++++refund  code is" + rr.getR1_Code()
				+ "+++++++++++++++++");
		/*
		 * if (rr.getR1_Code().equals("-1")) {
		 * 
		 * throw new Exception("refund  fail.error  is  -1:" // 验证签名失败或支付结构系统异常
		 * + rr.getR1_Code()); }
		 */
		/*
		 * if(!p3_Amt.equals(rr.getR3_Amt())){
		 * 
		 * throw new Exception("p3_Amt not  equals r3_amount:" +
		 * rr.getR1_Code()); }
		 */
		String newHmac = "";
		newHmac = DigestUtil.getHmac(new String[] { rr.getR0_Cmd(),
				rr.getR1_Code(), rr.getR2_TrxId(), rr.getR3_Amt(),
				rr.getR4_Cur() }, keyValue);
		if (!newHmac.equals(rr.getHmac())) {
			throw new Exception("Hmac error.");
		}
		return (rr);
	}

	public static boolean verifyCallback(String hmac, String r1_Code,
			String r2_TrxId, String r3_Amt, String r4_Cur, String r5_Pid,
			String r6_Order, String r7_Uid, String r8_MP, String r9_BType) {
		StringBuffer sValue = new StringBuffer();

		sValue.append(p1_MerId);

		sValue.append(buy_Cmd);

		sValue.append(r1_Code);

		sValue.append(r2_TrxId);

		sValue.append(r3_Amt);

		sValue.append(r4_Cur);

		sValue.append(r5_Pid);

		sValue.append(r6_Order);

		sValue.append(r7_Uid);

		sValue.append(r8_MP);

		sValue.append(r9_BType);
		String sNewString = null;
		sNewString = DigestUtil.hmacSign(sValue.toString(), keyValue);

		if (hmac.equals(sNewString)) {
			return (true);
		}
		return (false);
	}

	public static void main(String[] args) throws Exception {

		refundByTrxId("3232323", "310118425844288H", "0.01", "CNY", "");
		// queryByOrder("refund1234567");
	}

}