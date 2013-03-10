package com.beike.service.adweb.impl;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.adweb.AdWebTrxInfo;
import com.beike.dao.adweb.AdWebTrxInfoDao;
import com.beike.service.adweb.AdWebTrxInfoService;
import com.beike.service.common.EmailService;
import com.beike.util.DateUtils;
import com.beike.util.HttpClientUtil;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Feb 29, 2012
 * @author ye.tian
 * @version 1.0
 */
@Service("adWebTrxInfoService")
public class AdWebTrxInfoServiceImpl implements AdWebTrxInfoService {
	
	private static Log log=LogFactory.getLog(AdWebTrxInfoServiceImpl.class);
	private static ResourceBundle rb = ResourceBundle.getBundle("smsconfig");
	private static final String sender=rb.getString("sender");
	private static final String toEmail=rb.getString("toer");
	
	@Autowired
	private EmailService emailService;
	
	@Override
	public AdWebTrxInfo getAdWebTrxInfo(Long adwebid, String adcid, String adwi) {
		return adWebTrxInfoDao.getAdWebTrxInfo(adwebid, adcid, adwi);
	}

	@Override
	public void updateAdWebTrxInfo(Long adtrxid, String trxid,
			Integer buycount, Double buymoney) {
		
		adWebTrxInfoDao.updateAdWebTrxInfo(adtrxid, trxid, buycount, buymoney);
	}
	@Override
	public String generateAdWebTrxInfoList(String date, String trxOrderid) {
		String fromDate = date+" 00:00:00";
		String endDate = date+" 23:59:59";
		List<AdWebTrxInfo>	listAdWebTrxList=adWebTrxInfoDao.getAdWebTrxInfoList(fromDate, endDate, trxOrderid);
		
		return generateStatusTxt(listAdWebTrxList);
	}
	
	@Override
	public String generateAdWebTrxInfoList(String srcCode, String cid,
			String date) {
		if(date.indexOf("-")<0 && date.length()==8){
			date = date.substring(0,4) + "-" + date.substring(4,6) + "-" + date.substring(6,8);
		}
		String fromDate = date+" 00:00:00";
		String endDate = date+" 23:59:59";
		List<AdWebTrxInfo> listAdWebTrxInfo=adWebTrxInfoDao.getAdWebTrxInfoList(fromDate, endDate, srcCode, cid);
		return generateTxt(listAdWebTrxInfo);
	}
	
	private String generateStatusTxt(List<AdWebTrxInfo> listInfo){
		StringBuilder sb=new StringBuilder();
		if(listInfo==null||listInfo.size()==0)return sb.toString();
		for (AdWebTrxInfo adWebTrxInfo : listInfo) {
			sb.append(adWebTrxInfo.getAdwi());
			sb.append("||");
			sb.append(adWebTrxInfo.getTrxorderid());
			sb.append("||");
			sb.append(adWebTrxInfo.getTrxStatus());
			sb.append("||");
			sb.append(DateUtils.dateToStr(adWebTrxInfo.getOrderTime(), "yyyy-MM-dd HH:mm:ss"));
			sb.append("||");
			sb.append(adWebTrxInfo.getTrxStatus());
			sb.append("||");
			sb.append("||");
			sb.append("||");
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private String generateTxt(List<AdWebTrxInfo> listInfo){
		StringBuilder sb=new StringBuilder();
		if(listInfo==null||listInfo.size()==0)return sb.toString();
		for (AdWebTrxInfo adWebTrxInfo : listInfo) {
			sb.append(adWebTrxInfo.getAdwi());
			sb.append("||");
			sb.append(DateUtils.dateToStr(adWebTrxInfo.getOrderTime(), "yyyy-MM-dd HH:mm:ss"));
			sb.append("||");
			sb.append(adWebTrxInfo.getTrxorderid());
			sb.append("||");
			sb.append(adWebTrxInfo.getOrderMoney());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
	@Override
	public String httpRequestUrl(String url,String cid, String wi, String on, String ta,
			String pp, String date) {
		String str="1";
		AdWebTrxInfo ati = adWebTrxInfoDao.getAdWebTrxInfoByTrxId(on);
		if(ati!=null){
			url = ati.getAdweb_trxurl();
			cid = ati.getAdcid();
			wi = ati.getAdwi();
			pp = ati.getOrderMoney().toString();
			date = DateUtils.dateToStrLong(ati.getOrderTime()).replace(" ", "%20");
			
			StringBuilder sb=new StringBuilder(url);
			sb.append("?");
			if(!StringUtils.isEmpty(cid)){
				sb.append("cid=");
				sb.append(cid);
				sb.append("&");
			}
			if(!StringUtils.isEmpty(wi)){
				sb.append("wi=");
				sb.append(wi);
				sb.append("&");
			}
			sb.append("on=");
			sb.append(on);
			sb.append("&");
			sb.append("ta=");
			if(StringUtils.isEmpty(ta)){
				sb.append("1");
			}else{
				sb.append(ta);
			}
			sb.append("&");
			
			sb.append("pp=");
			sb.append(pp);
			sb.append("&");
			sb.append("sd=");
			sb.append(date);
			log.info("request adweb url is :"+sb.toString());
			
			int i=0;
			while(!"0".equals(str)&&i<3){
				str=HttpClientUtil.getResponseByGet(sb.toString(), null).trim();
				i++;
			}
			//失败
			if(!"0".equals(str)){
				//发送邮件
				sendErrorEmail("广告联盟....地址：" + sb.toString() +"\t时间：" + new Date());
			}
		}
		return str;
	}

	private void sendErrorEmail(String content) {
		if (toEmail != null) {
			String[] emails = toEmail.split(",");
			if (emails != null && emails.length > 0) {
				for (String string : emails) {
					try {
						emailService.sendMail(string, sender, content,
								"广告联盟回传数据失败");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	@Override
	public AdWebTrxInfo getAdWebTrxInfoById(Long adtrxid) {
		return adWebTrxInfoDao.getAdWebTrxInfoById(adtrxid);
	}
	@Autowired
	private AdWebTrxInfoDao adWebTrxInfoDao;
	
	
	public AdWebTrxInfoDao getAdWebTrxInfoDao() {
		return adWebTrxInfoDao;
	}

	public void setAdWebTrxInfoDao(AdWebTrxInfoDao adWebTrxInfoDao) {
		this.adWebTrxInfoDao = adWebTrxInfoDao;
	}

	@Override
	public Long addAdWebTrxInfo(AdWebTrxInfo adWebTrx) {
			return adWebTrxInfoDao.addAdWebTrxInfo(adWebTrx);
	}

	@Override
	public void updateAdWebTrxStatus(String trxOrderId) {
		adWebTrxInfoDao.updateWebTrxStatus(trxOrderId);
	}


}
