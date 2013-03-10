package com.beike.wap.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.entity.shopcart.ShopItem;
import com.beike.util.StringUtils;
import com.beike.wap.dao.MGoodsDao;
import com.beike.wap.entity.MGoods;
import com.beike.wap.entity.MGoodsCatlog;

/**
 * <p>
 * Title:商品数据库实现
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
 * @date 2011-09-19
 * @author lvjx
 * @version 1.0
 */
@Repository("wapGoodsDao")
public class MGoodsDaoImpl extends GenericDaoImpl<MGoods, Long> implements
		MGoodsDao {

	/*
	 * @see com.beike.wap.dao.goods.GoodsDao#queryIndexShowMes(int, int, int,
	 * java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<MGoods> queryIndexShowMes(int typeType, int typeFloor,
			int typePage, Date currentDate, String typeArea) throws Exception {
		List tempList = null;
		List<MGoods> goodsList = null;
		StringBuilder sql = new StringBuilder();
		sql
				.append("SELECT DISTINCT g.goodsid goodsId,g.goodsname goodsname,g.sourcePrice sourcePrice,");
		sql
				.append("g.currentPrice currentPrice,g.discount discount,g.rebatePrice rebatePrice, ");
		sql
				.append("p.sales_count profilevalue,t.type_url logo2 FROM beiker_wap_type_info t ");
		sql.append("LEFT JOIN beiker_goods g ON t.type_id = g.goodsid ");
		sql
				.append("LEFT JOIN beiker_goods_profile p ON p.goodsid = t.type_id   ");
		sql
				.append("WHERE t.type_type = ? AND t.type_floor = ? AND t.type_page = ? AND t.type_date = ? AND t.type_area = ? ");
		Object[] params = new Object[] { typeType, typeFloor, typePage,
				currentDate, typeArea };
		int[] types = new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER,
				Types.DATE, Types.VARCHAR };
		tempList = this.getJdbcTemplate().queryForList(sql.toString(), params,
				types);
		if (null != tempList && tempList.size() > 0) {
			goodsList = this.convertResultToObjectList(tempList);
		}
		return goodsList;
	}

	/*
	 * @see com.beike.wap.dao.goods.MGoodsDao#queryDetailShowMes(int)
	 */
	@Override
	public MGoods queryDetailShowMes(int goodsId) throws Exception {
		MGoods mGoods = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT g.goodsid goodsId,g.goodsname goodsname,g.sourcePrice sourcePrice,");
		sql.append("g.currentPrice currentPrice,g.dividePrice dividePrice,g.discount discount,g.rebatePrice rebatePrice,");
		sql.append("g.guest_id guestId,g.order_lose_abs_date orderLoseAbsDate,g.order_lose_date orderLoseDate,g.goods_single_count goodsSingleCount,");
		sql.append("p.sales_count+g.virtual_count profilevalue,g.logo2 logo2,g.maxcount maxcount,g.isavaliable isavaliable FROM beiker_goods g ");
		sql.append("LEFT JOIN beiker_goods_profile p ON p.goodsid = g.goodsid  ");
		sql.append("WHERE g.goodsid = ? ");
		int[] types = new int[]{Types.INTEGER};
		Object[] params = new Object[]{goodsId};
		mGoods = (MGoods) this.getJdbcTemplate().queryForObject(sql.toString(),
				params, types, new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int i)
							throws SQLException {
						MGoods goods = new MGoods();
						goods.setGoodsId(rs.getLong("goodsId"));
						goods.setGoodsname(rs.getString("goodsname"));
						goods.setSourcePrice(rs.getDouble("sourcePrice"));
						goods.setCurrentPrice(rs.getDouble("currentPrice"));
						goods.setDividePrice(rs.getDouble("dividePrice"));
						goods.setDiscount(rs.getDouble("discount"));
						goods.setRebatePrice(rs.getDouble("rebatePrice"));
						goods.setGuestId(rs.getLong("guestId"));
						goods.setOrderLoseAbsDate(rs.getLong("orderLoseAbsDate"));
						goods.setOrderLoseDate(rs.getDate("orderLoseDate"));
						goods.setVirtualCount(rs.getInt("goodsSingleCount"));
						goods.setGoodsCount(rs.getString("profilevalue"));
						goods.setLogo2(rs.getString("logo2"));
						goods.setMaxcount(rs.getInt("maxcount"));
						goods.setIsavaliable(rs.getInt("isavaliable"));
						return goods;
					}

				});
		return mGoods;
	}

	/*
	 * @see com.beike.wap.dao.goods.MGoodsDao#getMerchantById(int,
	 * java.util.Date, java.lang.String)
	 */
	@Override
	public MGoods getMerchantById(int goodsId) throws Exception {
		MGoods mGoods = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m.merchantname AS merchantname,m.merchantid   AS id FROM beiker_merchant m ");
		sql.append("LEFT JOIN beiker_goods_merchant bm ON m.merchantid = bm.merchantid ");
		sql.append("WHERE  bm.goodsid = ? AND m.parentid = 0 ");
		int[] types = new int[]{Types.INTEGER};
		Object[] params = new Object[]{goodsId};
		mGoods = (MGoods) this.getJdbcTemplate().queryForObject(sql.toString(),
				params, types, new RowMapper() {

					@Override
					public Object mapRow(ResultSet rs, int i)
							throws SQLException {
						MGoods goods = new MGoods();
						goods.setMerchantname(rs.getString("merchantname"));
						goods.setMerchantid(rs.getString("id"));
						return goods;
					}

				});
		return mGoods;
	}

	/*
	 * @see com.beike.wap.dao.goods.MGoodsDao#getBranchById(int, java.util.Date,
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MGoods> getBranchById(int goodsId) throws Exception {
		List tempList = null;
		List<MGoods> goodsList = new ArrayList<MGoods>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m.merchantid id,m.addr addr,m.merchantname mname,m.tel tel,m.buinesstime btime ");
		sql.append("FROM beiker_merchant m ");
		sql.append("LEFT JOIN beiker_goods_merchant bm ON m.merchantid = bm.merchantid ");
		sql.append("WHERE m.parentid != 0 AND bm.goodsid  = ? ");
		int[] types = new int[]{Types.INTEGER};
		Object[] params = new Object[]{goodsId};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(), params,
				types);
		if (null != tempList && tempList.size() > 0) {
			MGoods goods = null;
			for (int i = 0; i < tempList.size(); i++) {
				goods = new MGoods();
				Map result = (Map) tempList.get(i);
				Long id = ((Number) result.get("id")).longValue();
				if (null != id) {
					goods.setGoodsId(id);
				}
				if (StringUtils.validNull((String) result.get("addr"))) {
					goods.setCity(result.get("addr").toString());// 分店地址
				}
				if (StringUtils.validNull((String) result.get("mname"))) {
					goods.setMerchantname(result.get("mname").toString());// 分店名称
				}
				if (StringUtils.validNull((String) result.get("tel"))) {
					goods.setGoodsTitle(result.get("tel").toString());// 分店电话
				}
				if (StringUtils.validNull((String) result.get("btime"))) {
					goods.setGoodsname(result.get("btime").toString());// 分店营业时间
				}
				goodsList.add(goods);
			}
		}
		return goodsList;
	}
	
	/*
	 * @see com.beike.wap.dao.goods.MGoodsDao#queryGoodsIds(com.beike.wap.entity.GoodsCatlog)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> queryGoodsIds(int page,MGoodsCatlog goodsCatlog) throws Exception {
		List tempList = null;
		List<Long> goodsIdList = new ArrayList<Long>();
		StringBuilder sql = new StringBuilder();
		
		List<Object> paramList = new ArrayList<Object>();
		
		sql.append("SELECT DISTINCT goodid FROM beiker_catlog_good WHERE enddate >= DATE_FORMAT(CURDATE(),'%Y%m%d') ");
		sql.append("AND createdate <= DATE_FORMAT(CURDATE(),'%Y%m%d') AND isavaliable = '1' AND area_id = ? ");
		paramList.add(goodsCatlog.getCityid());
		if(null!=goodsCatlog.getRegionid()&&goodsCatlog.getRegionid()>0){
			sql.append(" AND regionid =  ?");
			paramList.add(goodsCatlog.getRegionid());
		}
		if(null!=goodsCatlog.getRegionextid()&&goodsCatlog.getRegionextid()>0){
			sql.append(" AND regionextid = ?");
			paramList.add(goodsCatlog.getRegionextid());
		}
		if(null!=goodsCatlog.getTagid()&&goodsCatlog.getTagid()>0){
			sql.append(" AND tagid = ?");
			paramList.add(goodsCatlog.getTagid());
		}
		if(null!=goodsCatlog.getTagextid()&&goodsCatlog.getTagextid()>0){
			sql.append(" AND tagextid = ?");
			paramList.add(goodsCatlog.getTagextid());
		}
		sql.append(" LIMIT ?,5 ");//此参数第一个需要替换
		paramList.add(page);
		
		tempList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
		if(null!=tempList&&tempList.size()>0){
			for(int i=0;i<tempList.size();i++){
				Map result = (Map)tempList.get(i);
				Long goodsId = ((Number)result.get("goodid")).longValue();
				if(null!=goodsId){
					goodsIdList.add(goodsId);
				}
			}
		}
		return goodsIdList;
	}

	/*
	 * @see com.beike.wap.dao.goods.MGoodsDao#queryGoodsIdsSum(com.beike.wap.entity.GoodsCatlog)
	 */
	@Override
	public int queryGoodsIdsSum(MGoodsCatlog goodsCatlog) throws Exception {
		int sum = 0;
		StringBuilder sql = new StringBuilder();
		
		List<Object> paramList = new ArrayList<Object>();
		
		sql.append("SELECT COUNT(DISTINCT goodid) FROM beiker_catlog_good WHERE enddate >= DATE_FORMAT(CURDATE(),'%Y%m%d') ");
		sql.append("AND createdate <= DATE_FORMAT(CURDATE(),'%Y%m%d') AND isavaliable = '1' AND area_id = ? ");
		paramList.add(goodsCatlog.getCityid());
		if(null!=goodsCatlog.getRegionid()&&goodsCatlog.getRegionid()>0){
			sql.append(" AND regionid = ?");
			paramList.add(goodsCatlog.getRegionid());
		}
		if(null!=goodsCatlog.getRegionextid()&&goodsCatlog.getRegionextid()>0){
			sql.append(" AND regionextid = ?");
			paramList.add(goodsCatlog.getRegionextid());
		}
		if(null!=goodsCatlog.getTagid()&&goodsCatlog.getTagid()>0){
			sql.append(" AND tagid = ?");
			paramList.add(goodsCatlog.getTagid());
		}
		if(null!=goodsCatlog.getTagextid()&&goodsCatlog.getTagextid()>0){
			sql.append(" AND tagextid = ?");
			paramList.add(goodsCatlog.getTagextid());
		}
		sum = this.getJdbcTemplate().queryForInt(sql.toString(), paramList.toArray());
		return sum;
	}
	
	/*
	 * @see com.beike.wap.dao.goods.MGoodsDao#queryGoodsInfo(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MGoods> queryGoodsInfo(String goodsIds) throws Exception {
		if(!StringUtils.validNull(goodsIds)){
			return null;
		}
		List tempList = null;
		List<MGoods> goodsList = new ArrayList<MGoods>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT bg.goodsname name,bg.goodsid id,bg.virtual_count virtualcount,bgf.sales_count salescount,bg.logo3 listlogo, ");
		sql.append("bg.logo4 logo4 ,bg.currentPrice cPrice, bg.sourcePrice sPrice, bg.discount discount ,bg.rebatePrice rPrice ");
		sql.append("FROM beiker_goods_profile bgf ");
		sql.append("LEFT JOIN beiker_goods bg ON bgf.goodsid = bg.goodsid ");
		sql.append("WHERE bg.goodsid IN(").append(goodsIds).append(") ");
		sql.append("ORDER BY FIND_IN_SET(bg.goodsid,'").append(goodsIds).append("') ");
		tempList = this.getJdbcTemplate().queryForList(sql.toString());
		if(null!=tempList && tempList.size()>0){
			MGoods mGoods = null;
			for(int i=0;i<tempList.size();i++){
				mGoods = new MGoods();
				Map result = (Map)tempList.get(i);
				if(StringUtils.validNull((String)result.get("name"))){
					mGoods.setGoodsname(result.get("name").toString());
				}
				Long id = ((Number)result.get("id")).longValue();
				if(null!=result.get("id")){
					mGoods.setGoodsId(id);
				}
				if(null!=result.get("salescount")){
					Integer virtualcount = ((Number)result.get("virtualcount")).intValue();
					Integer salescount = ((Number)result.get("salescount")).intValue();
					mGoods.setVirtualCount(virtualcount+salescount);
				}
				if(StringUtils.validNull((String)result.get("listlogo"))){
					mGoods.setLogo3(result.get("listlogo").toString());
				}
				if(StringUtils.validNull((String)result.get("logo4"))){
					mGoods.setLogo2(result.get("logo4").toString());
				}
				BigDecimal cPrice = (BigDecimal)result.get("cPrice");
				if(null!=cPrice ){
					mGoods.setCurrentPrice(cPrice.doubleValue());
				}
				BigDecimal sPrice = (BigDecimal)result.get("sPrice");
				if(null!=sPrice){
					mGoods.setSourcePrice(sPrice.doubleValue());
				}
				BigDecimal rPrice = (BigDecimal)result.get("rPrice");
				if(null!=rPrice){
					mGoods.setRebatePrice(rPrice.doubleValue());
				}
				Float discount =  ((Number)result.get("discount")).floatValue();
				if(null!=discount){
					mGoods.setDiscount(discount);
				}
				goodsList.add(mGoods);
			}
		}
		return goodsList;
	}
	
	/*
	 * @see com.beike.wap.dao.MGoodsDao#addShopItem(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean addShopItem(String goodsid, String merchantId, int buySum,String userid)
			throws Exception {
		String sql;
		ShopItem shopItem;
		// 登陆后添加产品至购物车
		if (goodsid != null && merchantId != null) {
			// 当前产品存在,增加数量
			
			Long.parseLong(goodsid);
			Long.parseLong(merchantId);
			
			shopItem = isshopItemExists(goodsid, merchantId, userid);
			if (shopItem != null) {
				sql = "update beiker_shopcart set buy_count=?,addtime=? where goodsid=? and userid=?";
				Long buyCount = shopItem.getBuy_count()+buySum;
				if(buyCount.longValue()>999){
					buyCount = 999L;
				}								
				getJdbcTemplate().update(sql, new Object[] { buyCount, new Date(), goodsid, userid });
				return true;
			} else {
				// 当前产品不在购物车,新添加商品
				sql = "insert into beiker_shopcart(merchantid,goodsid,userid,buy_count,addtime) values(?,?,?,?,?)";
				getJdbcTemplate().update(
						sql,
						new Object[] { merchantId, goodsid, userid, buySum,
								new Date() });
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public ShopItem isshopItemExists(String goodsid, String merchantID,
			String userid) {
		
		Long.parseLong(goodsid);
		Long.parseLong(merchantID);
		Long.parseLong(userid);
		
		String sql = "select * from beiker_shopcart where goodsid=? and merchantid=? and userid=?";
		
		List shopItemList = null;
		shopItemList = getSimpleJdbcTemplate().query(sql,
				ParameterizedBeanPropertyRowMapper.newInstance(ShopItem.class),
				new Object[] { goodsid, merchantID, userid });
		if (shopItemList.size() > 0) {
			return (ShopItem) shopItemList.get(0);
		}
		return null;
	}
	
	/**
	 * 将查询结果（map组成的List）转化成具体的对象列表
	 * 
	 * @param results
	 *            jdbcTemplate返回的查询结果
	 * @return 具体的对象列表
	 * @author lvjx
	 */
	@SuppressWarnings("unchecked")
	private List<MGoods> convertResultToObjectList(List results)
			throws Exception {
		List<MGoods> objList = new ArrayList<MGoods>();
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				Map result = (Map) results.get(i);
				MGoods goods = this.convertResultMapToObject(result);
				objList.add(goods);
			}
		}
		return objList;
	}

	/**
	 * 将查询结果元素（map对象）转化为具体对象
	 * 
	 * @param result
	 *            jdbcTemplate返回的查询结果元素（map对象）
	 * @return 具体的对象类型
	 * @author lvjx
	 */
	@SuppressWarnings("unchecked")
	private MGoods convertResultMapToObject(Map result) throws Exception {
		MGoods obj = new MGoods();
		if (result != null) {
			
			if (null != result.get("goodsId")) {
				Long goodsId = ((Number) result.get("goodsId")).longValue();
				obj.setGoodsId(goodsId);
			}
			if (null!=result.get("goodsname")) {
				obj.setGoodsname(result.get("goodsname").toString());
			}
			
			if (result.get("sourcePrice") != null) {
				Double sourcePrice = ((Number) result.get("sourcePrice"))
				.doubleValue();
				obj.setSourcePrice(sourcePrice);
			}
			
			if (result.get("currentPrice") != null) {
				Double currentPrice = ((Number) result.get("currentPrice"))
				.doubleValue();
				obj.setCurrentPrice(currentPrice);
			}
			
			if (result.get("discount") != null) {
				Double discount = ((Number) result.get("discount")).doubleValue();
				obj.setDiscount(discount);
			}
			
			if (result.get("rebatePrice") != null) {
				Double rebatePrice = ((Number) result.get("rebatePrice"))
				.doubleValue();
				obj.setRebatePrice(rebatePrice);
			}
			if (null!= result.get("profilevalue")) {
				obj.setGoodsCount(result.get("profilevalue").toString());
			}

			if (null!=result.get("logo2")) {
				obj.setLogo2(result.get("logo2").toString());
			}
		}
		return obj;

	}

	@Override
	public List<MGoods> queryGoodsByBrandId(String goodsIds) throws Exception {
		String sql = "SELECT * FROM beiker_goods WHERE goodsid IN ("+goodsIds+") and isavaliable = 1";
		List<MGoods> rsList = getSimpleJdbcTemplate().query(sql, new RowMapperImpl());
		if(rsList == null || rsList.size() == 0)
		{
			return null;
		}
		return rsList;
	}
	
	protected class RowMapperImpl implements ParameterizedRowMapper<MGoods> {
		public MGoods mapRow(ResultSet rs, int rowNum) throws SQLException {
			MGoods goods = new MGoods();
			goods.setGoodsId(rs.getLong("goodsId"));
			goods.setGoodsname(rs.getString("goodsname"));
			goods.setSourcePrice(rs.getDouble("sourcePrice"));
			goods.setCurrentPrice(rs.getDouble("currentPrice"));
			goods.setPayPrice(rs.getDouble("payPrice"));
			goods.setOfferPrice(rs.getDouble("offerPrice"));
			goods.setRebatePrice(rs.getDouble("rebatePrice"));
			goods.setDividePrice(rs.getDouble("dividePrice"));
			goods.setDiscount(rs.getDouble("discount"));
			goods.setMaxcount(rs.getInt("maxcount"));
			goods.setEndTime(rs.getDate("endTime"));
			goods.setCity(rs.getString("city"));
			goods.setStartTime(rs.getDate("startTime"));
			goods.setIsavaliable(rs.getInt("isavaliable"));
			goods.setLogo1(rs.getString("logo1"));
			goods.setLogo2(rs.getString("logo2"));
			goods.setLogo3(rs.getString("logo3"));
			goods.setQpsharepic(rs.getString("qpsharepic"));
			goods.setOrderLoseAbsDate(rs.getLong("order_lose_abs_date"));
			goods.setOrderLoseDate(rs.getDate("order_lose_date"));
			goods.setGuestId(rs.getLong("guest_id"));
			goods.setGoodsTitle(rs.getString("goods_title"));
			goods.setVirtualCount(rs.getInt("virtual_count"));
 			return goods;
		}
	}

	/*
	 * @see com.beike.wap.dao.MGoodsDao#findById(java.lang.Long)
	 */
	@Override
	public MGoods findById(Long id) throws Exception {
		String sql = "SELECT * FROM beiker_goods WHERE goodsid = ?";
		List<MGoods> goodsList =  getSimpleJdbcTemplate().query(sql, new RowMapperImpl(),id);
		if(goodsList == null ||goodsList.size() == 0)
		{
			return null;
		}
		return goodsList.get(0);
	}

	/*
	 * @see com.beike.wap.dao.MGoodsDao#queryMaxDate(int, int, int, java.lang.String)
	 */
	@Override
	public Date queryMaxDate(int typeType, int typeFloor, int typePage,
			String typeArea) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("select max(type_date) from beiker_wap_type_info ");
		sql.append("where type_area = ? and type_type=? and type_floor = ? and type_page= ? ");
		Object[] params = new Object[]{typeArea,typeType,typeFloor,typePage};
		int[] types = new int[]{Types.VARCHAR,Types.INTEGER,Types.INTEGER,Types.INTEGER};
		Date maxDate = (Date)this.getJdbcTemplate().queryForObject(sql.toString(), params, types, Date.class);
		return maxDate;
	}

	@Override
	public List<Long> queryGoodsIdByBrandId(Long brandId) throws Exception {
		String sql = "SELECT goodsid FROM beiker_goods_merchant WHERE merchantid = ?";
		List<Long> rsList = getSimpleJdbcTemplate().query(sql, new ParameterizedRowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet rs, int arg1) throws SQLException {
				return rs.getLong("goodsid");
			}}, brandId);
		if(rsList == null || rsList.size() == 0){
			return null;
		}
		return rsList;
	}

}
