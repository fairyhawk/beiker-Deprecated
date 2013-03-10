package com.beike.service.cps.tuan800.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.cps.tuan800.CPSTuan800Dao;
import com.beike.service.common.EmailService;
import com.beike.service.cps.tuan360.CPSTuan360Service;
import com.beike.service.cps.tuan800.CPSTuan800Service;
import com.beike.util.DateUtils;
import com.beike.util.Digest;
import com.beike.util.HttpClientUtil;
import com.beike.util.PropertiesReader;

/**
 * @author janwen Apr 17, 2012
 */
@Service("cpsTuan800Service")
public class CPSTuan800ServiceImpl implements CPSTuan800Service {
	@Autowired
	private CPSTuan800Dao cpsTuan800Dao;

	@Autowired
	private CPSTuan360Service cpsTuan360Service;

	private static ResourceBundle rb = ResourceBundle.getBundle("smsconfig");
	private static final String sender = rb.getString("sender");
	private static final String toEmail = rb.getString("toer");
	final static String sk = PropertiesReader.getValue("project",
			"CPS_TUAN800_SK");
	final static double fanli = new Double(PropertiesReader.getValue("project",
			"CPS_TUAN800_FANLI"));
	final static String siteKey = PropertiesReader.getValue("project",
			"CPS_TUAN800_SITEKEY");
	final static String siteSecurity = PropertiesReader.getValue("project",
			"CPS_TUAN800_SITESECURITY");
	final static String pre_url = "http://cps.tuan800.com/handleCpsIn?sk=";
	final static Logger logger = Logger.getLogger(CPSTuan800ServiceImpl.class);
	
	private Map<Long, String> getTypeIdMap(List<Map> map){
		Map<Long, String> typeIdMap = new HashMap<Long, String>();  //key:商品id； value:佣金类型
		try{
			if(map!=null && map.size()>0){
				//1.循环出所有的商品ID
				StringBuffer goodsIds = new StringBuffer("");
				for(Map temp : map){
					goodsIds.append(((Long)temp.get("goodsid")).longValue()).append(",");
				}
				if(goodsIds.length() > 0){
					goodsIds.deleteCharAt(goodsIds.length() - 1);
				}
				//2.根据商品id从beiker_catlog_good查询出所属的分类ID，用HashMap保存：key:商品id， value：分类ID
				List<Map<String, Object>> classificationIdsLst = cpsTuan800Dao.getClassificationIds(goodsIds.toString());
				for(Map<String, Object> claMap : classificationIdsLst){
					Long goodId = (Long)claMap.get("goodid");
					String type = typeIdMap.get(goodId);
					if("BUFANLI".equals(type)){
						continue;
					}
					if(((Long)claMap.get("tagextid")).longValue() == 10206L){
						type = "BUFANLI";
					}
					typeIdMap.put(goodId, type);
				}
			}
		}catch(Exception ex){
		}
		return typeIdMap;
	}

	@Override
	public void saveOrderNoPay(Map<String, Object> params) {
		String md5value = null;

		try {
			// cps站点保存在cookie信息{_src + '|' + _cid + '|' + _outsrc "|" + _wi +'|'
			// + _uid}
			String[] cps_value = params.get("cps_cookie").toString().split(
					"\\|");

			List<Map> order_info_map = cpsTuan800Dao.getOrderInfo(params.get(
					"trxorder_id").toString());
			// Map<String,String> goods_cat_map = getGoodsCat(order_info_map);
			
			Map<Long, String> typeIdMap = getTypeIdMap(order_info_map);
			
			for (int i = 0; i < order_info_map.size(); i++) {
				StringBuilder url_param = new StringBuilder();
				StringBuilder digest = new StringBuilder();
				Map temp = order_info_map.get(i);
				// 返利分类
				String C_CD = "1-0";
				// 返利金额
				String money = "0";
				
				//10206  :  电影品类ID
				String type = typeIdMap.get((Long)temp.get("goodsid"));
				if("BUFANLI".equals(type) || ((Long)temp.get("trx_rule_id")).longValue() == 3){
					C_CD = "5-1";
				}else{
					if (((BigDecimal) temp.get("pay_price")).doubleValue() >= 15) {
						// 15元以下返现0
						money = formatNumber(fanli
								* ((BigDecimal) temp.get("pay_price"))
										.doubleValue());
					} else {
						// 15元以下C_CD=5-1
						C_CD = "5-1";
					}
				}

				digest.append(sk)
					.append("|")
					.append(cps_value[1])
					.append("|")
					.append(cps_value[3])
					.append("|")
					.append(cps_value[4])
					.append("|")
					.append(temp.get("trx_goods_sn"))
					.append("|")
					.append(1)
					.append("|")
					.append(formatNumber(((BigDecimal) temp.get("pay_price")).doubleValue()))
					.append("|")
					.append(((Timestamp) temp.get("create_date")).getTime())
					.append("|").append(0).append("|").append(siteKey)
					.append("|").append(siteSecurity);

				md5value = Digest.signMD5(digest.toString(), "utf-8");
				logger.info("saveOrderNoPay cps_tuan800 md5_src=" + digest.toString() + "  cps_tuan800_md5_value=" + md5value);
				url_param.append(pre_url)
						.append(sk)
						.append("&cid=")
						.append(cps_value[1])
						.append("&wi=")
						.append(cps_value[3])
						.append("&uid=")
						.append(cps_value[4])
						.append("&on=")
						.append(temp.get("trx_goods_sn"))
						.append("&ta=1")
						.append("&pp=")
						.append(formatNumber((((BigDecimal) temp.get("pay_price"))).doubleValue()))
						.append("&sd=")
						.append(((Timestamp) temp.get("create_date")).getTime())
						.append("&status=0").append("&sign=").append(md5value)
						.append("&t_comm=").append(money).append("&p_cd=")
						.append(temp.get("goodsid")).append("&price=").append(formatNumber(((BigDecimal) temp.get("pay_price")).doubleValue()))
						.append("&it_cnt=1").append("&comm=").append(money)
						.append("&c_cd=").append(C_CD).append("&it_status=0");
				logger.info("cps tuan800 saveOrderNoPay url=" + url_param.toString());
				if (sendtoTuan800(url_param.toString()) == 0) {
					logger.info("saveOrderNoPay params " + params);
					cpsTuan800Dao.saveOrderNoPay(params, cps_value, temp);
				}
			}
		} catch (Exception e) {
			logger.info("Tuan800 CPS saveOrderNoPay ERROR");
			sendErrorEmail("CPS 团800下订单回调异常[trxorder_id=" + params.get("trxorder_id").toString() + "][md5=" + md5value + "]");
			e.printStackTrace();
		}
	}

	static final DecimalFormat df = new DecimalFormat("###");

	private String formatNumber(double input) {
		input = input * 100;
		return df.format(input);
	}

	@Override
	public void saveOrderPay(Map<String, Object> params) {
		String md5value = null;
		try {
			// cps站点保存在cookie信息{_src + '|' + _cid + '|' + _outsrc "|" + _wi +'|'
			// + _uid}
			String[] cps_value = params.get("cps_cookie").toString().split(
					"\\|");
			List<Map> order_info_map = cpsTuan800Dao.getOrderInfo(params.get(
					"trxorder_id").toString());
			// Map<String,String> goods_cat_map = getGoodsCat(order_info_map);
			if (order_info_map.size() > 0) {
				Map<Long, String> typeIdMap = getTypeIdMap(order_info_map);
				for (int i = 0; i < order_info_map.size(); i++) {
					StringBuilder url_param = new StringBuilder();
					StringBuilder digest = new StringBuilder();
					Map temp = order_info_map.get(i);
					// 返利分类
					String C_CD = "1-0";
					// 返利金额
					String money = "0";
					
					//10206  :  电影品类ID
					String type = typeIdMap.get((Long)temp.get("goodsid"));
					if("BUFANLI".equals(type) || ((Long)temp.get("trx_rule_id")).longValue() == 3){
						C_CD = "5-1";
					}else{
						if (((BigDecimal) temp.get("pay_price")).doubleValue() >= 15) {
							// 15元以下返现0
							money = formatNumber(fanli * ((BigDecimal) temp.get("pay_price")).doubleValue());
						} else {
							// 15元以下C_CD=5-1
							C_CD = "5-1";
						}
					}

					digest.append(sk).append("|").append(cps_value[1]).append(
							"|").append(cps_value[3]).append("|").append(
							cps_value[4]).append("|").append(
							temp.get("trx_goods_sn")).append("|").append(1)
							.append("|").append(
									formatNumber(((BigDecimal) temp
											.get("pay_price")).doubleValue()))
							.append("|").append(
									((Timestamp) temp.get("create_date"))
											.getTime()).append("|").append(1)
							.append("|").append(siteKey).append("|").append(
									siteSecurity);
					md5value = Digest.signMD5(digest.toString(), "utf-8");
					logger.info("saveOrderPay cps_tuan800  md5_src="
							+ digest.toString() + "  cps_tuan800_md5_value="
							+ md5value);
					url_param.append(pre_url).append(sk).append("&cid=")
							.append(cps_value[1]).append("&wi=").append(
									cps_value[3]).append("&uid=").append(
									cps_value[4]).append("&on=").append(
									temp.get("trx_goods_sn")).append("&ta=1")
							.append("&pp=").append(
									(formatNumber(((BigDecimal) temp
											.get("pay_price")).doubleValue())))
							.append("&sd=").append(
									((Timestamp) temp.get("create_date"))
											.getTime()).append("&status=1")
							.append("&sign=").append(md5value).append(
									"&t_comm=").append(money).append("&p_cd=")
							.append(temp.get("goodsid")).append("&price=")
							.append(
									formatNumber(((BigDecimal) temp
											.get("pay_price")).doubleValue()))
							.append("&it_cnt=1").append("&comm=").append(money)
							.append("&c_cd=").append(C_CD).append(
									"&it_status=0");
					logger.info("cps tuan800 saveOrderPay url="
							+ url_param.toString());
					if (sendtoTuan800(url_param.toString()) == 0) {
						logger.info("saveOrderPay params " + params);
						cpsTuan800Dao.saveOrderPay(params, temp);
					}
				}

			}

		} catch (Exception e) {
			logger.info("Tuan800 CPS saveOrderPay method ERROR");
			sendErrorEmail("CPS 团800支付成功回调异常[trxorder_id="
					+ params.get("trxorder_id").toString() + "][md5="
					+ md5value + "]  " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void cancelOrder(Map<String, Object> params) {
		String md5value = null;
		try {
			List<Map> order_info_map = cpsTuan800Dao.getOrderInfoCps(params
					.get("trxorder_id").toString(), params.get("trx_goods_sn")
					.toString());
			// Map<String,String> goods_cat_map = getGoodsCat(order_info_map);
			
			if (order_info_map.size() > 0) {
				Map<Long, String> typeIdMap = getTypeIdMap(order_info_map);
				for (int i = 0; i < order_info_map.size(); i++) {
					StringBuilder url_param = new StringBuilder();
					StringBuilder digest = new StringBuilder();
					Map temp = order_info_map.get(i);
					// 返利分类
					String C_CD = "1-0";
					// 返利金额
					String money = "0";
					
					//10206  :  电影品类ID
					String type = typeIdMap.get((Long)temp.get("goodsid"));
					if("BUFANLI".equals(type) || ((Long)temp.get("trx_rule_id")).longValue() == 3){
						C_CD = "5-1";
					}else{
						if (((BigDecimal) temp.get("pay_price")).doubleValue() >= 15) {
							// 15元以下返现0
							money = formatNumber(fanli * ((BigDecimal) temp.get("pay_price")).doubleValue());
						} else {
							// 15元以下C_CD=5-1
							C_CD = "5-1";
						}
					}
					
					digest.append(sk).append("|").append(temp.get("cid"))
							.append("|").append(temp.get("wi")).append("|")
							.append(temp.get("uid")).append("|").append(
									temp.get("trx_goods_sn")).append("|")
							.append(1).append("|").append(
									formatNumber(((BigDecimal) temp
											.get("pay_price")).doubleValue()))
							.append("|").append(
									((Timestamp) temp.get("create_date"))
											.getTime()).append("|").append(5)
							.append("|").append(siteKey).append("|").append(
									siteSecurity);
					md5value = Digest.signMD5(digest.toString(), "utf-8");
					logger.info("cancelOrder cps_tuan800  md5_src="
							+ digest.toString() + "  cps_tuan800_md5_value="
							+ md5value);
					url_param.append(pre_url).append(sk).append("&cid=")
							.append(temp.get("cid")).append("&wi=").append(
									temp.get("wi")).append("&uid=").append(
									temp.get("uid")).append("&on=").append(
									temp.get("trx_goods_sn")).append("&ta=1")
							.append("&pp=").append(
									formatNumber(((BigDecimal) temp
											.get("pay_price")).doubleValue()))
							.append("&sd=").append(
									((Timestamp) temp.get("create_date"))
											.getTime()).append("&status=5")
							.append("&sign=").append(md5value).append(
									"&t_comm=").append(money).append("&p_cd=")
							.append(temp.get("goodsid")).append("&price=")
							.append(
									formatNumber(((BigDecimal) temp
											.get("pay_price")).doubleValue()))
							.append("&it_cnt=1").append("&comm=").append(money)
							.append("&c_cd=").append(C_CD).append(
									"&it_status=1");
					logger.info("cps tuan800 cancelOrder url="
							+ url_param.toString());
					if (sendtoTuan800(url_param.toString()) == 0) {
						logger.info("cancelOrder params " + params);
						cpsTuan800Dao.cancelOrder(params);
					}
				}
			}

		} catch (Exception e) {
			logger.info("Tuan800 CPS ERROR");
			sendErrorEmail("CPS 团800退款成功回调异常[trxorder_id="
					+ params.get("trxorder_id").toString() + "][md5="
					+ md5value + "] " + e.getMessage());
			e.printStackTrace();
		}

		try {
			cpsTuan360Service.cancelOrder((Long) params
					.get("trxorder_goods_id"));
		} catch (Exception ex) {
		}
	}

	@Override
	public int sendtoTuan800(String url) {
		String back = HttpClientUtil.getResponseByGet(url, null, "utf-8")
				.trim();
		logger.info("CPS_Tuan800 Response Message=" + back);
		int returnValue = 1;
		try {
			returnValue = new Integer(back);
		} catch (Exception e) {

		}
		return returnValue;
	}

	private Map<String, String> convertGoodsCat(List<Map> firCat,
			List<Map> secCat) throws UnsupportedEncodingException {
		Map<String, String> goods_cat_map = new HashMap<String, String>();
		for (int i = 0; i < firCat.size(); i++) {
			if (goods_cat_map.get(firCat.get(i).get("goodsid").toString()) != null) {
				goods_cat_map.put(firCat.get(i).get("goodsid").toString(),
						goods_cat_map.get(firCat.get(i).get("goodsid")
								.toString())
								+ encode("|_|" + firCat.get(i).get("tagid")));
			} else {
				goods_cat_map.put(firCat.get(i).get("goodsid").toString(),
						encode(firCat.get(i).get("tagid").toString()));
			}
		}

		for (int i = 0; i < secCat.size(); i++) {
			if (goods_cat_map.get(secCat.get(i).get("goodsid").toString()) != null) {
				goods_cat_map
						.put(
								secCat.get(i).get("goodsid").toString(),
								goods_cat_map.get(secCat.get(i).get("goodsid")
										.toString())
										+ encode("|_|"
												+ secCat.get(i).get("tagextid")));
			} else {
				goods_cat_map.put(firCat.get(i).get("goodsid").toString(),
						encode(secCat.get(i).get("tagextid").toString()));
			}
		}
		return goods_cat_map;
	}

	@Autowired
	private EmailService emailService;

	private void sendErrorEmail(String content) {
		if (toEmail != null) {
			String[] emails = toEmail.split(",");
			if (emails != null && emails.length > 0) {
				for (String string : emails) {
					try {
						emailService.sendMail(string, sender, content,
								"Tuan800 CPS Error");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public String getOrder4Tuan800(Long beginIndex, String begindate,
			String enddate, String cid) {
		List<Map> results = cpsTuan800Dao.getOrder4Tuan800(beginIndex,
				begindate, enddate, cid);
		List<String> goodsidList = new ArrayList<String>();
		for (int i = 0; i < results.size(); i++) {
			goodsidList.add(results.get(i).get("goodsid").toString());
		}
		List<Map> goodstitle = cpsTuan800Dao.getGoodsTitle(goodsidList);
		// 标题
		Map<String, String> goods_title_map = new HashMap<String, String>();
		for (int i = 0; i < goodstitle.size(); i++) {
			goods_title_map.put(goodstitle.get(i).get("goodsid").toString(),
					goodstitle.get(i).get("goodsname").toString().replaceAll(
							"\r|\n", ""));
		}
		StringBuilder record = new StringBuilder();
		List<String> back_reuslt = new ArrayList<String>();
		
		Map<Long, String> typeIdMap = getTypeIdMap(results);
		
		for (int i = 0; i < results.size(); i++) {
			Map map = results.get(i);
			// 返利分类
			String C_CD = "1-0";
			// 返利金额
			String money = "0";
			
			
			//10206  :  电影品类ID
			String type = typeIdMap.get((Long)map.get("goodsid"));
			if("BUFANLI".equals(type) || ((Long)map.get("trx_rule_id")).longValue() == 3){
				C_CD = "5-1";
			}else{
				if (((BigDecimal) map.get("pay_price")).doubleValue() >= 15) {
					// 15元以下返现0
					money = formatNumber(fanli * ((BigDecimal) map.get("pay_price")).doubleValue());
				} else {
					// 15元以下C_CD=5-1
					C_CD = "5-1";
				}
			}
			
			String it_status = map.get("order_status").toString();
			record.append(
					DateUtils.formatDate((Timestamp) map.get("create_date"),
							"yyyy-MM-dd HH:mm:ss")).append("||").append(
					map.get("trx_goods_sn")).append("||").append(
					formatNumber(((BigDecimal) map.get("pay_price"))
							.doubleValue())).append("||").append(money).append(
					"||").append(map.get("order_status")).append("||").append(
					map.get("wi")).append("||").append(map.get("cid")).append(
					"||").append("p_cd=").append(map.get("goodsid")).append(
					"|_|&title=").append(
					goods_title_map.get(map.get("goodsid").toString())).append(
					"|_|&price=").append(
					formatNumber(((BigDecimal) map.get("pay_price"))
							.doubleValue())).append("|_|&it_cnt=1|_|&comm=")
					.append(money).append("|_|&c_cd=").append(C_CD).append(
							"|_|&it_status=").append(
							it_status_map.get(it_status)).append("|_|").append(
							"\n");
		}
		return record.toString();
	}

	static Map<String, String> it_status_map = new HashMap<String, String>();
	static {
		it_status_map.put("0", "0");
		it_status_map.put("1", "0");
		it_status_map.put("5", "1");
	}

	@Override
	public Long getTotalResults(String startdate, String enddate, String cid) {
		return cpsTuan800Dao.getTotalResults(startdate, enddate, cid);
	}

	private String encode(String input) throws UnsupportedEncodingException {
		return URLEncoder.encode(input, "utf-8");
	}

}
