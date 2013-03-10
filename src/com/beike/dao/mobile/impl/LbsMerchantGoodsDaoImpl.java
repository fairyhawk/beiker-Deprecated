package com.beike.dao.mobile.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.mobile.LbsMerchantGoodsDao;
import com.beike.entity.mobile.LbsGoodsInfo;
import com.beike.entity.mobile.LbsMerchantGoodsInfo;
import com.beike.entity.mobile.LbsMerchantInfo;

@SuppressWarnings({"unused", "unchecked"})
@Repository("lbsMerchantGoodsDao")
public class LbsMerchantGoodsDaoImpl extends GenericDaoImpl<LbsMerchantInfo, Long> implements LbsMerchantGoodsDao {
	private final Log logger = LogFactory.getLog(LbsMerchantGoodsDaoImpl.class);
	
	public List<Map<String, Object>> getMerInfoByMerIds(String merids){
		List<Map<String, Object>> tempList = null;
		StringBuilder sql = new StringBuilder("");
		sql.append("select merchant1.merchantid, merchant2.merchantid brandId, merchant2.merchantname brandName, merchant2.merchantintroduction, ");
		sql.append("merchantProfile.mc_logo1 logo1, merchantProfile.mc_logo2 logo2, merchantProfile.mc_logo3 logo3,");
		sql.append("merchantProfile.mc_logo4 logo4, merchantProfile.mc_sale_count salecount, ");
		sql.append("merchantProfile.mc_score evascores, merchantProfile.mc_well_count wellcount, ");
		sql.append("merchantProfile.mc_satisfy_count satisfycount, merchantProfile.mc_poor_count poorcount ");
		sql.append("from beiker_merchant merchant1 ");
		sql.append("join beiker_merchant merchant2 on merchant1.parentId=merchant2.merchantid ");
		sql.append("left join beiker_merchant_profile merchantProfile on merchant2.merchantid=merchantProfile.merchantid ");
		sql.append("where merchant1.merchantid in(").append(merids).append(")");
		tempList = this.getJdbcTemplate().queryForList(sql.toString());
		return tempList;
	}
	
	@Override
	public List<LbsMerchantInfo> getLbsMerchantInfo(int dataCount, final Long lastMaxId) {
		StringBuilder sql = new StringBuilder("");
		sql.append("select merchant1.merchantid merId, merchant1.merchantname merName, merchant2.merchantid brandId, ");
		sql.append("merchant2.merchantname brandName, merchant1.displayname displayname, merchant1.addr, merchant1.city, ");
		sql.append("merchant2.merchantintroduction, merchant1.tel, merchant1.buinesstime, merchant1.quality, ");
		sql.append("merchant1.domainname, merchant1.isvipbrand, merchant1.latitude, ");
		sql.append("merchantProfile.mc_logo1 logo1, merchantProfile.mc_logo2 logo2, merchantProfile.mc_logo3 logo3,");
		sql.append("merchantProfile.mc_logo4 logo4, merchantProfile.mc_sale_count salecount, ");
		sql.append("merchantProfile.mc_score evascores, merchantProfile.mc_well_count wellcount, ");
		sql.append("merchantProfile.mc_satisfy_count satisfycount, merchantProfile.mc_poor_count poorcount ");
		sql.append("from beiker_merchant merchant1 ");
		sql.append("join beiker_merchant merchant2 on merchant1.parentId=merchant2.merchantid ");
		sql.append("left join beiker_merchant_profile merchantProfile on merchant2.merchantid=merchantProfile.merchantid ");
		sql.append("where merchant1.merchantid>? ");
		sql.append("order by merId asc ");
		sql.append("limit ").append(dataCount);
		
		List<LbsMerchantInfo> merchantInfoList = getSimpleJdbcTemplate().query(sql.toString(),
				new RowMapperLbsMerchantInfoImpl(), lastMaxId);
		if (merchantInfoList != null && merchantInfoList.size() > 0) {
			return merchantInfoList;
		}
		return null;
	}
	
	protected class RowMapperLbsMerchantInfoImpl implements ParameterizedRowMapper<LbsMerchantInfo> {
		String[] latlon = null;
		public LbsMerchantInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			LbsMerchantInfo merInfo = new LbsMerchantInfo();
			merInfo.setId(rs.getLong("merId"));                           		  //分店id
			merInfo.setMerchantName(rs.getString("merName"));             		  //分店名称
			merInfo.setDisplayName(rs.getString("displayname"));          		  //显示名称
			merInfo.setAddr(rs.getString("addr"));                        		  //分店地址
			merInfo.setCity(rs.getString("city"));                       		  //分店城市
			merInfo.setMerchantIntroduction(rs.getString("merchantintroduction"));//分店介绍
			merInfo.setTel(rs.getString("tel"));                                  //分店电话
			merInfo.setBusinessTime(rs.getString("buinesstime"));                 //营业时间
			merInfo.setQuality(rs.getLong("quality"));                            //质量保证
			merInfo.setBrandId(rs.getLong("brandId"));                            //品牌id
			merInfo.setBrandName(rs.getString("brandName"));                      //品牌名称
			String latitude = rs.getString("latitude");
			if(latitude != null && !"".equals(latitude) && latitude.indexOf("-") > -1){
				latlon = latitude.split("-");
				merInfo.setOriginalLon(latlon[0]);                    			   //经度
				merInfo.setOriginalLat(latlon[1]);                    			   //纬度
			}
			merInfo.setDomainName(rs.getString("domainname"));                     //店铺域名
			merInfo.setVipBrand(rs.getShort("isvipbrand"));                        //是否VIP商户
			merInfo.setMcLogo1(rs.getString("logo1"));
			merInfo.setMcLogo2(rs.getString("logo2"));
			merInfo.setMcLogo3(rs.getString("logo3"));
			merInfo.setMcLogo4(rs.getString("logo4"));
			merInfo.setMcSaleCount(rs.getInt("salecount"));                         //商家累积销售量
			merInfo.setMcScore(rs.getLong("evascores"));                            //商家评价得分
			merInfo.setMcWellCount(rs.getLong("wellcount"));                        //商家很好评价次数
			merInfo.setMcSatisfyCount(rs.getLong("satisfycount"));                  //商家满意评价次数
			merInfo.setMcPoorCount(rs.getLong("poorcount"));                        //商家差评价次数
			return merInfo;
		}
	}
	
	@Override
	public List<Map<String, Object>> getMerExpands(String merchantIds) {
		List<Map<String, Object>> tempList = null;
		StringBuilder sql = new StringBuilder("");
		sql.append("select brapro.branchid merchantId, brapro.well_count merWellCount, brapro.satisfy_count merSatisfyCount, brapro.poor_count merPoorCount ");
		sql.append("from beiker_branch_profile brapro ");
		sql.append("where brapro.branchid in(");
		sql.append(merchantIds).append(") ");
		tempList = this.getJdbcTemplate().queryForList(sql.toString());
		return tempList;
	}

	@Override
	public List<Map<String, Object>> getBranchRegions(String merchantIds) {
		List<Map<String, Object>> tempList = null;
		StringBuilder sql = new StringBuilder("");
		sql.append("select regionbranch.branchid, regionpro1.id id1, regionpro1.region_name rename1, regionpro2.id id2, regionpro2.region_name rename2 ");
		sql.append("from beiker_region_branch regionbranch ");
		sql.append("join beiker_region_property regionpro1 on regionbranch.regionextid=regionpro1.id ");
		sql.append("join beiker_region_property regionpro2 on regionpro1.parentid=regionpro2.id ");
		sql.append("where regionbranch.branchid in(");
		sql.append(merchantIds).append(")");
		tempList = this.getJdbcTemplate().queryForList(sql.toString());
		return tempList;
	}
	
	public List<Map<String, Object>> getGoodIdsAndTypeTages(String merchantIds){
		List<Map<String, Object>> tempList = null;
		StringBuilder sql = new StringBuilder("");
		sql.append("select distinct merchant.merchantid, catalog.goodid, tagpro1.id id, ");
		sql.append("tagpro1.tag_name name,tagpro2.id parentid,tagpro2.tag_name parentname ");
		sql.append("from beiker_goods_merchant merchant ");
		sql.append("join beiker_catlog_good catalog on catalog.goodid=merchant.goodsid ");
		sql.append("join beiker_tag_property tagpro1 on catalog.tagextid=tagpro1.id ");
		sql.append("join beiker_tag_property tagpro2 on tagpro1.parentid=tagpro2.id ");
		sql.append("where catalog.isavaliable=1 and merchant.merchantid in(").append(merchantIds).append(") ");
		sql.append("order by merchant.merchantid, catalog.goodid");
		tempList = this.getJdbcTemplate().queryForList(sql.toString());
		return tempList;
	}
	
	public List<Map<String, Object>> getTypeTagesByGoodids(String goodsidstr){
		List<Map<String, Object>> tempList = null;
		StringBuilder sql = new StringBuilder("");
		sql.append("select distinct catalog.goodid, tagpro1.id id, tagpro1.tag_name name,tagpro2.id parentid,tagpro2.tag_name parentname ");
		sql.append("from beiker_catlog_good catalog ");
		sql.append("join beiker_tag_property tagpro1 on catalog.tagextid=tagpro1.id ");
		sql.append("join beiker_tag_property tagpro2 on tagpro1.parentid=tagpro2.id ");
		//sql.append("where catalog.isavaliable=1 and catalog.goodid in(").append(goodsidstr).append(") ");
		sql.append("where catalog.goodid in(").append(goodsidstr).append(") ");
		tempList = this.getJdbcTemplate().queryForList(sql.toString());
		return tempList;
	}
	
	@Override
	public List<LbsGoodsInfo> getLbsGoodsInfo(int dataCount, Long lastMaxMerchantId, Long lastMaxGoodsId) {
		StringBuilder sql = new StringBuilder("");
		sql.append("select merchant.latitude, goodmerchant.merchantid, merchant.merchantname, goods1.goodsid, ");
		sql.append("goods1.goodsname, goods1.goods_title, goods1.city, goods1.sourcePrice, goods1.currentPrice, ");
		sql.append("goods1.dividePrice, goods1.rebatePrice, goods1.logo1, goods1.logo2, goods1.logo3, goods1.logo4,");
		sql.append("goods1.maxcount, goods1.goods_single_count, goods1.qpsharepic, goods1.order_lose_abs_date, ");
		sql.append("goods1.order_lose_date, goods1.startTime, goods1.endTime, goods1.isavaliable, goods1.isTop, ");
		sql.append("goods1.kindlywarnings, goods1.isRefund, goods1.couponcash, goods1.isadvance, goods1.is_scheduled, ");
		sql.append("goodpro.sales_count, goodpro.detailpageurl, goodpro.well_count,goodpro.satisfy_count, goodpro.poor_count, ");
		sql.append("merchant.tel, merchant.addr, merchant.displayname, merchant.buinesstime, merchant.quality, merchant.domainname, ");
		sql.append("merchant.isvipbrand ");
		sql.append("from beiker_goods goods1 ");
		sql.append("join beiker_goods_profile goodpro on goods1.goodsid=goodpro.goodsid ");
		sql.append("join beiker_goods_merchant goodmerchant on goods1.goodsid=goodmerchant.goodsid ");
		sql.append("join beiker_merchant merchant on goodmerchant.merchantid=merchant.merchantid and merchant.parentId != 0 ");
		sql.append("where (goods1.goodsid = ? and merchant.merchantid > ? ) or goods1.goodsid >? ");
		sql.append("order by goods1.goodsid asc, merchant.merchantid asc ");
		sql.append("limit ").append(dataCount);
		
		List<LbsGoodsInfo> goodsInfoList = getSimpleJdbcTemplate().query(sql.toString(),
				new RowMapperLbsGoodsInfoImpl(), lastMaxGoodsId, lastMaxMerchantId, lastMaxGoodsId);
		if (goodsInfoList != null && goodsInfoList.size() > 0) {
			return goodsInfoList;
		}
		return null;
	}
	
	protected class RowMapperLbsGoodsInfoImpl implements ParameterizedRowMapper<LbsGoodsInfo> {
		String[] latlon = null;
		public LbsGoodsInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			LbsGoodsInfo goodInfo = new LbsGoodsInfo();
			goodInfo.setGoodsId(rs.getLong("goodsid"));                            //商品id
			goodInfo.setGoodsName(rs.getString("goodsname"));             		   //商品名称
			goodInfo.setGoodsTitle(rs.getString("goods_title"));          		   //商品标题（短名称）
			goodInfo.setMerchantId(rs.getLong("merchantid"));                      //分店id
			goodInfo.setMerchantName(rs.getString("merchantname"));                //分店名称
			goodInfo.setMerDisplayName(rs.getString("displayname"));               //显示名称
			goodInfo.setAddr(rs.getString("addr"));                                //分店地址
			goodInfo.setMerBusinessTime(rs.getString("buinesstime"));              //分店营业时间
			goodInfo.setMerQuality(rs.getLong("quality"));                         //质量保证
			goodInfo.setMerDomainName(rs.getString("domainname"));                 //店铺域名
			goodInfo.setMerVipBrand(rs.getInt("isvipbrand"));                      //是否VIP商户:0否 1是
			goodInfo.setTel(rs.getString("tel"));                       		   //电话
			goodInfo.setCity(rs.getString("city"));							       //城市
			goodInfo.setGoodsSourcePrice(rs.getBigDecimal("sourcePrice"));         //原价格
			goodInfo.setGoodsCurrentPrice(rs.getBigDecimal("currentPrice"));  //当前价格
			goodInfo.setGoodsDividePrice(rs.getBigDecimal("dividePrice"));    //分成价格
			goodInfo.setGoodsRebatePrice(rs.getBigDecimal("rebatePrice"));    //返现价格
			String latitude = rs.getString("latitude");
			if(latitude != null && !"".equals(latitude) && latitude.indexOf("-") > -1){
				latlon = latitude.split("-");
				goodInfo.setOriginalLon(latlon[0]);                    //经度
				goodInfo.setOriginalLat(latlon[1]);                    //纬度
			}
			goodInfo.setMaxCount(rs.getLong("maxcount"));                     //售销上限最大数量
			goodInfo.setGoodsSingleCount(rs.getInt("goods_single_count"));                         //个人可购买数量
			goodInfo.setGoodsLogo(rs.getString("logo1"));
			goodInfo.setGoodsLogo2(rs.getString("logo2"));
			goodInfo.setGoodsLogo3(rs.getString("logo3"));
			goodInfo.setGoodsLogo4(rs.getString("logo4"));
			goodInfo.setQpsharePic(rs.getString("qpsharepic"));                       //千品物语图片
			goodInfo.setGoodsOrderLoseAbsDate(rs.getLong("order_lose_abs_date"));     //商品订单过期时间段。比如用户购买后10天不消费，则此订单过期。（时间单位由PM另定）
			goodInfo.setGoodsOrderLoseDate(rs.getTimestamp("order_lose_date"));       //商品订单过期时间点
			goodInfo.setGoodsStartTime(rs.getTimestamp("startTime"));                 //到开始时间自动发布上
			goodInfo.setGoodsEndTime(rs.getTimestamp("endTime"));                     //结束时间
			goodInfo.setGoodsIsAvaliable(rs.getLong("isavaliable"));                  //1可用(上线)  0不可用(下线) 
			goodInfo.setGoodsIsTop(rs.getLong("isTop"));                              //1置顶 0不置顶
			goodInfo.setKindlyWarnings(rs.getString("kindlywarnings"));               //温馨提示
			goodInfo.setGoodsIsRefund(rs.getInt("isRefund"));                        //是否支持退款 0:不支持退款 1:支持退款
			goodInfo.setCouponCash(rs.getLong("couponcash"));                         //1是现金券 0不是现金券
			goodInfo.setGoodsIsAdvance(rs.getByte("isadvance"));                      //是否预付款 0：否; 1：是
			goodInfo.setGoodsScheduled(rs.getLong("is_scheduled"));                   //是否支持预定0:否1:是
			goodInfo.setSalesCount(rs.getInt("sales_count"));                         //销售量
			goodInfo.setDetailPageurl(rs.getString("detailpageurl"));                 //商品详细页
			goodInfo.setWellCount(rs.getLong("well_count"));                          //商品很好评价次数
			goodInfo.setSatisfyCount(rs.getLong("satisfy_count"));                    //商品满意评价次数
			goodInfo.setPoorCount(rs.getLong("poor_count"));                          //商品差评价次数
			return goodInfo;
		}
	}
	
	public List<Map<String, Object>> getGoodsByGids(String goodids){
		if(goodids == null || goodids.length() == 0){
			return null;
		}
		List<Map<String, Object>> goodList = null;
		StringBuilder sql = new StringBuilder("");
		sql.append("select distinct goodmerchant.merchantid, goods.goodsid, ");
		sql.append("goods.goodsname, goods.goods_title, goods.city, goods.sourcePrice, goods.currentPrice, ");
		sql.append("goods.dividePrice, goods.rebatePrice, goods.logo1, goods.logo2, goods.logo3, goods.logo4,");
		sql.append("goods.maxcount, goods.goods_single_count, goods.qpsharepic, goods.order_lose_abs_date, ");
		sql.append("goods.order_lose_date, goods.startTime, goods.endTime, goods.isavaliable, goods.isTop, ");
		sql.append("goods.kindlywarnings, goods.isRefund, goods.couponcash, goods.isadvance, goods.is_scheduled, ");
		sql.append("goodpro.sales_count, goodpro.detailpageurl, goodpro.well_count,goodpro.satisfy_count, goodpro.poor_count ");
		sql.append("from beiker_goods goods ");
		sql.append("join beiker_goods_merchant goodmerchant on goods.goodsid=goodmerchant.goodsid ");
		sql.append("join beiker_goods_profile goodpro on goods.goodsid=goodpro.goodsid ");
		sql.append("where goods.goodsid in(").append(goodids).append(") ");
		
		goodList = getSimpleJdbcTemplate().queryForList(sql.toString());
		if (goodList != null && goodList.size() > 0) {
			return goodList;
		}
		return null;
	}
}