package com.beike.util;

import java.util.List;

import com.beike.entity.catlog.RegionCatlog;

/**
 * <p>
 * Title:属性、地域类别工具
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date Jul 27, 2011
 * @author ye.tian
 * @version 1.0
 */
public class CatlogUtils {

	// private static final String searchurl="/goods/searchGoodsByProperty.do?";
	private static final String searchurl_goods = "/goods/";
	private static final String searchurl_coupon = "/coupon/";
	private static final String searchurl_brand = "/brand/";
	private static final String after_fix = ".html";

	public static String getPriceCatlog(String catlogid, String catlogextid,
			String regionid, String regionextid, String url_type) {

		StringBuilder sb = new StringBuilder(url_type);
		// sb.append("_");
		if (catlogid != null && !"".equals(catlogid)) {
			sb.append(catlogid);
			sb.append("-f");
			sb.append("-");
		}

		if (catlogextid != null && !"".equals(catlogextid)) {

			sb.append(catlogextid);
			sb.append("-t");
			sb.append("-");
		}

		if (regionid != null && !"".equals(regionid)) {
			sb.append(regionid);
			sb.append("-x");
			sb.append("-");
		}

		if (regionextid != null && !"".equals(regionextid)) {

			sb.append(regionextid);
			sb.append("-q");
			sb.append("-");
		}
		String surl = sb.toString();
		if ("-".equals(sb.toString())) {
			surl = "";
		}
		StringBuilder pricesb = new StringBuilder();

		// pricesb.append("<a href=\"javascript:void(0);\" id=\"priceall\"
		// class=\"selected\" rel=\"totalItems\">全部</a>");
		pricesb.append("<a href='" + surl
				+ "50-p?abacusinsid=lj0200' id=\"rangeprice_50\">50元以下</a> | ");
		pricesb.append("<a href='" + surl
				+ "100-p?abacusinsid=lj0300' id=\"rangeprice_100\">50-100元</a> | ");
		pricesb.append("<a href='" + surl
				+ "300-p?abacusinsid=lj0400' id=\"rangeprice_300\">100-300元</a> | ");
		pricesb.append("<a href='" + surl
				+ "500-p?abacusinsid=lj0500' id=\"rangeprice_500\">300-500元</a> | ");
		pricesb.append("<a href='" + surl
				+ "1000-p?abacusinsid=lj0600' id=\"rangeprice_1000\">500元以上</a>");

		return pricesb.toString();
	}

	/**
	 * 设置初始url
	 * 
	 * @param flag
	 *            true 地域 false 属性
	 * @param listCatlog
	 *            地域、属性集合
	 */
	public static void setInitUrl(boolean flag, RegionCatlog regionCatlog,
			String type_url) {
		StringBuilder sb = new StringBuilder(type_url);
		if (regionCatlog.getParentId() == 0) {
			if (flag) {
				// sb.append("___");
				sb.append(regionCatlog.getRegion_enname());
				sb.append("-x");
				// sb.append("__");
			} else {
				// sb.append("_");
				sb.append(regionCatlog.getRegion_enname());
				sb.append("-f");
				// sb.append("____");
			}
			regionCatlog.setUrl(sb.toString());
		}
	}

	/**
	 * 设置类别url
	 * 
	 * @param flag
	 *            是否是地域 true 地域 false 属性
	 * @param listCatlog
	 *            地域或者属性列表
	 * @param catlogid
	 *            类别id
	 * @param catlogextid
	 *            类别二级id
	 * @param regionid
	 *            地域id
	 * @param regionextid
	 *            地域二级id
	 * @param rangeprice
	 *            价格
	 * @return
	 */
	public static void setCatlogUrl(boolean flag,
			List<RegionCatlog> listCatlog, String catlogid, String catlogextid,
			String regionid, String regionextid, String rangeprice,
			String type_url) {
		if (listCatlog != null && listCatlog.size() > 0) {
			for (RegionCatlog regionCatlog : listCatlog) {
				StringBuilder sb = new StringBuilder(type_url);
				Long parentid = regionCatlog.getParentId();
                
				// 地域处理
				if (flag) {
					if (parentid == 0) {
						// 假如没有属性id

						// sb.append("_");
						if (catlogid != null && !"".equals(catlogid)) {
							// 有属性id的话 需要加上属性id
							sb.append(catlogid);
							sb.append("-f");
							sb.append("-");
						}
						if (catlogextid != null && !"".equals(catlogextid)) {
							sb.append(catlogextid);
							sb.append("-t-");
						}
						sb.append(regionCatlog.getRegion_enname());
						sb.append("-x");
						sb.append("-");
						// if(regionextid!=null&&!"".equals(regionextid)){
						// sb.append(regionextid);
						// sb.append("-q");
						// sb.append("-");
						// }
						if (rangeprice != null && !"".equals(rangeprice)) {

							sb.append(rangeprice);
							sb.append("-p");
						}

					}
					// 非父级别
					else {

						// sb.append("_");
						if (catlogid != null && !"".equals(catlogid)) {
							// 有属性id的话 需要加上属性id
							sb.append(catlogid);
							sb.append("-f");
							sb.append("-");
						}
						if (catlogextid != null && !"".equals(catlogextid)) {

							sb.append(catlogextid);
							sb.append("-t");
							sb.append("-");
						}
						if (regionid != null && !"".equals(regionid)) {

							sb.append(regionid);
							sb.append("-x");
							sb.append("-");
						}
						sb.append(regionCatlog.getRegion_enname());
						sb.append("-q");
						sb.append("-");
						if (rangeprice != null && !"".equals(rangeprice)) {
							sb.append(rangeprice);
							sb.append("-p");
						}
					}

				}
				// 属性处理
				else {
					if (parentid == 0) {
						// sb.append("_");
						
						sb.append(regionCatlog.getRegion_enname());
						sb.append("-f");
						sb.append("-");
						// if(catlogextid!=null&&!"".equals(regionextid)){
						//							
						// sb.append(catlogextid);
						// sb.append("-t");
						// sb.append("-");
						// }
						if (regionid != null && !"".equals(regionid)) {

							sb.append(regionid);
							sb.append("-x");
							sb.append("-");
						}
						if (regionextid != null && !"".equals(regionextid)) {

							sb.append(regionextid);
							sb.append("-q");
							sb.append("-");
						}
						if (rangeprice != null && !"".equals(rangeprice)) {

							sb.append(rangeprice);
							sb.append("-p");
						}
					}
					// 非父级
					else {

						// sb.append("_");
						if (catlogid != null && !"".equals(catlogid)) {
							// 有属性id的话 需要加上属性id
							sb.append(catlogid);
							sb.append("-f-");
						}
						sb.append(regionCatlog.getRegion_enname());
						sb.append("-t");
						sb.append("-");
						if (regionid != null && !"".equals(regionid)) {

							sb.append(regionid);
							sb.append("-x");
							sb.append("-");
						}
						if (regionextid != null && !"".equals(regionextid)) {

							sb.append(regionextid);
							sb.append("-q");
							sb.append("-");
						}
						if (rangeprice != null && !"".equals(rangeprice)) {

							sb.append(rangeprice);
							sb.append("-p");
						}

					}

				}
				String lurl = sb.toString();
				String ll = lurl.charAt(lurl.length() - 1) + "";
				if ("-".equals(ll)) {
					lurl = lurl.substring(0, lurl.lastIndexOf("-"));
				}
				// 设置 url
				regionCatlog.setUrl(lurl);
			}
		}

	}
	
	
	/**
	 * 除特色标签外的其它url
	 * janwen
	 * @param flag
	 * @param listCatlog
	 * @param catlog_en_name
	 * @param catlogext_en_name
	 * @param region_en_name
	 * @param regionext_en_name
	 * @param rangeprice
	 * @param featuretag_en_name
	 * @param type_url
	 * 
	 */
	public static void setFeatureCatlogUrl(boolean flag,
			List<RegionCatlog> listCatlog, String catlog_en_name, String catlogext_en_name,
			String region_en_name, String regionext_en_name, String rangeprice,String featuretag_en_name,
			String type_url) {
		// meishi-f-huoguo-t-qingyunpuqu-x-jiefangxilu-q-100-p
		if (listCatlog != null && listCatlog.size() > 0) {
			for (RegionCatlog regionCatlog : listCatlog) {
				StringBuilder sb = new StringBuilder(type_url);
				Long parentid = regionCatlog.getParentId();

				// 地域处理
				if (flag) {
					if (parentid == 0) {
						// 假如没有属性id

						// sb.append("_");
						if (StringUtils.validNull(catlog_en_name)) {
							// 有属性id的话 需要加上属性id
							sb.append(catlog_en_name);
							sb.append("-f");
							sb.append("-");
						}
						if (StringUtils.validNull(catlogext_en_name)) {
							sb.append(catlogext_en_name);
							sb.append("-t-");
						}
						sb.append(regionCatlog.getRegion_enname());
						sb.append("-x");
						sb.append("-");
						
						
						if (rangeprice != null && !"".equals(rangeprice)) {

							sb.append(rangeprice);
							sb.append("-p");
						}
						
						if(StringUtils.validNull(featuretag_en_name)){
							sb.append(featuretag_en_name);
							sb.append("-s-");
						}

					}
					// 非父级别
					else {

						// sb.append("_");
						if (StringUtils.validNull(catlog_en_name)) {
							// 有属性id的话 需要加上属性id
							sb.append(catlog_en_name);
							sb.append("-f");
							sb.append("-");
						}
						if (StringUtils.validNull(catlogext_en_name)) {

							sb.append(catlogext_en_name);
							sb.append("-t");
							sb.append("-");
						}
						if (StringUtils.validNull(region_en_name)) {

							sb.append(region_en_name);
							sb.append("-x");
							sb.append("-");
						}
						sb.append(regionCatlog.getRegion_enname());
						sb.append("-q");
						sb.append("-");
						
						if (rangeprice != null && !"".equals(rangeprice)) {
							sb.append(rangeprice);
							sb.append("-p");
						}
						if(StringUtils.validNull(featuretag_en_name)){
							sb.append(featuretag_en_name);
							sb.append("-s-");
						}
					}

				}
				// 属性处理
				else {
					if (parentid == 0) {

						sb.append(regionCatlog.getRegion_enname());
						sb.append("-f");
						sb.append("-");
						if (StringUtils.validNull(region_en_name)) {

							sb.append(region_en_name);
							sb.append("-x");
							sb.append("-");
						}
						if (StringUtils.validNull(regionext_en_name)) {

							sb.append(regionext_en_name);
							sb.append("-q");
							sb.append("-");
						}
						
						if (rangeprice != null && !"".equals(rangeprice)) {

							sb.append(rangeprice);
							sb.append("-p");
						}
						if(StringUtils.validNull(featuretag_en_name)){
							sb.append(featuretag_en_name);
							sb.append("-s-");
						}
					}
					// 非父级
					else {
						// sb.append("_");

						if (StringUtils.validNull(catlog_en_name)) {
							// 有属性id的话 需要加上属性id
							sb.append(catlog_en_name);
							sb.append("-f-");
						}
						sb.append(regionCatlog.getRegion_enname());
						sb.append("-t");
						sb.append("-");
						if (StringUtils.validNull(region_en_name)) {

							sb.append(region_en_name);
							sb.append("-x");
							sb.append("-");
						}
						if (StringUtils.validNull(regionext_en_name)) {

							sb.append(regionext_en_name);
							sb.append("-q");
							sb.append("-");
						}
						
						if (rangeprice != null && !"".equals(rangeprice)) {

							sb.append(rangeprice);
							sb.append("-p");
						}
						
						if(StringUtils.validNull(featuretag_en_name)){
							sb.append(featuretag_en_name);
							sb.append("-s-");
						}

					}

				}
				String lurl = sb.toString();
				String ll = lurl.charAt(lurl.length() - 1) + "";
				if ("-".equals(ll)) {
					lurl = lurl.substring(0, lurl.lastIndexOf("-"));
				}
				// 设置 url
				regionCatlog.setUrl(lurl);
			}
		}

	}

	/**
	 * 
	 * 特色标签单独处理
	 * @param featuretags
	 * @param catlog_en_name
	 * @param type_url
	 * 
	 */
	public static void setFeatureCatlogUrl(List<RegionCatlog> featuretags,
			String catlog_en_name, String catlogext_en_name, String region_en_name,
			String regionext_en_name, String rangeprice, String type_url) {
		if (featuretags != null && featuretags.size() > 0) {
			for (RegionCatlog ft : featuretags) {
				StringBuilder sb = new StringBuilder(type_url);
				if (StringUtils.validNull(catlog_en_name)) {
					sb.append(catlog_en_name);
					sb.append("-f-");
				}
				// meishi-f-huoguo-t-qingyunpuqu-x-jiefangxilu-q-100-p
				if(StringUtils.validNull(catlogext_en_name)){
					sb.append(catlogext_en_name).append("-t-");
				}
				if(StringUtils.validNull(region_en_name)){
					sb.append(region_en_name).append("-x-");
				}
				
				if(StringUtils.validNull(regionext_en_name)){
					sb.append(regionext_en_name).append("-q-");
				}
				
				
				if(StringUtils.validNull(rangeprice)){
					sb.append(rangeprice).append("-p-");
				}
				sb.append(ft.getRegion_enname()).append("-s-");
				String url = sb.toString();
				if(url.endsWith("-")){
					url = url.substring(0,url.lastIndexOf("-"));
				}
				ft.setUrl(url);
			}
		}
	}

	public static void setCatlogUrl_new(boolean flag,
			RegionCatlog regionCatlog, String catlog_enname,
			String catlogext_enname, String regionid, String regionextid,
			String rangeprice, String type_url) {

		StringBuilder sb = new StringBuilder(type_url);
		// catlogextid = regionCatlog.getRegion_enname();
		// 地域处理
		// sb.append("_");

		sb.append(catlog_enname);
		sb.append("-f");
		sb.append("-");
		if (catlogext_enname != null) {

			sb.append(catlogext_enname);
			sb.append("-t");
			// sb.append("-");
		}
		if (regionid != null && !"".equals(regionid)) {

			sb.append(regionid);
			sb.append("-x");
			sb.append("-");
		}
		if (regionextid != null && !"".equals(regionextid)) {

						sb.append(regionextid);
						sb.append("-q");
						sb.append("-");
					}
					if (rangeprice != null && !"".equals(rangeprice)) {

						sb.append(rangeprice);
						sb.append("-p");
					}
					
					String lurl = sb.toString();
					String ll = lurl.charAt(lurl.length() - 1) + "";
					if ("-".equals(ll)) {
						lurl = lurl.substring(0, lurl.lastIndexOf("-"));
					}
					// 设置 url
					regionCatlog.setUrl(lurl);
			
			
		
	}
	
}
