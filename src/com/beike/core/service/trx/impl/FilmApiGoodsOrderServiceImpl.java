package com.beike.core.service.trx.impl;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.FilmApiOrderParam;
import com.beike.core.service.trx.FilmApiGoodsOrderService;
import com.beike.util.HttpUtils;
import com.beike.util.PropertyUtil;



/**
 * @author yurenli
 * 网票网对接接口实现
 */
@Service("filmApiGoodsOrderService")
public class FilmApiGoodsOrderServiceImpl implements FilmApiGoodsOrderService {

	PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	
	public String filmUrl = propertyUtil.getProperty("film_api_url");
	public String userName = propertyUtil.getProperty("film_api_userName");
	public String privateKey = propertyUtil.getProperty("film_api_privateKey");
	
	/**
	 * @param filmApiOrderParam
	 * 调用网票网下单接口
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String createFilmOrder(FilmApiOrderParam filmApiOrderParam) throws IOException {
		HashMap<String,String> urlMap = new HashMap<String, String>();
		String target = "Sell_ApplyTicket";
		String uspStr = userName+target+privateKey;
		String sign = checkData(uspStr);
		urlMap.put("UserName",userName);
		urlMap.put("Target",target);
		urlMap.put("Sign",sign);
		urlMap.put("SID",filmApiOrderParam.getFilmSid());
		urlMap.put("AID","0");
		urlMap.put("PayType",filmApiOrderParam.getPayType());
		urlMap.put("Mobile", filmApiOrderParam.getMobile());
		urlMap.put("MsgType", filmApiOrderParam.getMsgType());
		urlMap.put("Amount", filmApiOrderParam.getAmount()+"");
		urlMap.put("GoodsType",filmApiOrderParam.getGoodsType());
		StringBuffer sb = new StringBuffer();
			ArrayList list  = (ArrayList)HttpUtils.URLPost(filmUrl, urlMap);
			if(list!=null&&list.size()>0){
				for(int i=0;i<list.size();i++){
				sb.append(list.get(i));
				}
			}
		return sb.toString();
	}

	/**
	 * @param filmApiOrderParam
	 * 调用网票网完成支付接口
	 * @return
	 * @throws IOException 
	 */
	@Override
	public String findFilmInfo(FilmApiOrderParam filmApiOrderParam) throws IOException {
		
		return null;
	}
	
	/**
	 * @param filmApiOrderParam
	 * 调用网票网重发验票码接口
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String resendFilmCode(FilmApiOrderParam filmApiOrderParam) throws IOException {
		HashMap<String,String> urlMap = new HashMap<String, String>();
		String target = "Sell_ReSendMsg";
		String uspStr = userName+target+privateKey;
		String sign = checkData(uspStr);
		urlMap.put("UserName",userName);
		urlMap.put("Target",target);
		urlMap.put("Sign",sign);
		urlMap.put("SID",filmApiOrderParam.getFilmSid());
		StringBuffer sb = new StringBuffer();
			ArrayList list  = (ArrayList)HttpUtils.URLPost(filmUrl, urlMap);
			if(list!=null&&list.size()>0){
				for(int i=0;i<list.size();i++){
				sb.append(list.get(i));
				}
			}
		return sb.toString();
	}

	/**
	 * @param filmApiOrderParam
	 * 调用网票网选择座位接口
	 * @return
	 */
	@Override
	public String selectFilmSeat(FilmApiOrderParam filmApiOrderParam) throws IOException {
		
		HashMap<String,String> urlMap = new HashMap<String, String>();
		String target = "Sell_LockSeatPage";
		String uspStr = userName+target+privateKey;
		String sign = checkData(uspStr);
		urlMap.put("UserName",userName);
		urlMap.put("Target",target);
		urlMap.put("Sign",sign);
		urlMap.put("SeqNo",filmApiOrderParam.getSeqNo());
		urlMap.put("LockFlag",filmApiOrderParam.getLockFlag());
		System.out.println(urlMap);
		//url = "http://test.api1.wangpiao.com/LockSeat";
		return HttpUtils.createWPWURL(filmUrl+"/LockSeat", urlMap);
		
	}

	/**
	 * 网票网完成支付接口
	 * @param filmApiOrderParam
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String updateFilmOrder(FilmApiOrderParam filmApiOrderParam) throws IOException {
		HashMap<String,String> urlMap = new HashMap<String, String>();
		String target = "Sell_BuyTicket";
		String uspStr = userName+target+privateKey;
		String sign = checkData(uspStr);
		urlMap.put("UserName",userName);
		urlMap.put("Target",target);
		urlMap.put("Sign",sign);
		urlMap.put("SID",filmApiOrderParam.getFilmSid());
		urlMap.put("PayNo",filmApiOrderParam.getFilmPayNo());
		urlMap.put("PlatformPayNo", filmApiOrderParam.getPlatformPayNo());
		StringBuffer sb = new StringBuffer();
			ArrayList list  = (ArrayList)HttpUtils.URLPost(filmUrl, urlMap);
			if(list!=null&&list.size()>0){
				for(int i=0;i<list.size();i++){
				sb.append(list.get(i));
				}
			}
		return sb.toString();
	}
	
	/**
	 * 对网票网数据生成签名
	 * @param orgin
	 * @param sign
	 * @return
	 */
	public String checkData(String orgin) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			String result = byte2hex(md.digest(orgin.toString().getBytes("utf-8")));
			return result.toLowerCase();
		} catch (Exception e) {
			throw new java.lang.RuntimeException("sign error !");
		}
	}
	/**
	 * 二行制转字符串
	 */
	private static String byte2hex(byte[] b) {

		StringBuffer hs = new StringBuffer();

		String stmp = "";

		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs.append("0").append(stmp);
			} else {
				hs.append(stmp);
			}
		}
		return hs.toString().toUpperCase();
	}
	
	public static void main(String[] args) {
		FilmApiOrderParam fop = new FilmApiOrderParam();
		fop.setSeqNo("2859157");
		fop.setSeqNo("");
		try {
			new FilmApiGoodsOrderServiceImpl().selectFilmSeat(fop);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
