package com.beike.dao.impl.catlog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.catlog.GoodsCatlogDao;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.page.Pager;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**
 * <p>
 * Title:商品的地域、属性数据库操作
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
 * @date May 21, 2011
 * @author ye.tian
 * @version 1.0
 */
@Repository("goodsCatlogDao")
public class GoodsCatlogDaoImpl extends GenericDaoImpl<GoodsCatlog, Long>
		implements GoodsCatlogDao {
	private final Log log = LogFactory.getLog(this.getClass());
	
	//今日新品N天内上线
	private final int NEW_GOODS_BEFORE = -3;
	/**
	 * 
	 * @author janwen
	 * @time Dec 26, 2011 4:41:12 PM
	 * 
	 * @param abstractLog
	 * @param start
	 * @param end
	 * @return 默认排序所有符合条件的goodsid
	 */
	@Override
	public List searchDefaultGoodsId(AbstractCatlog abstractLog) {
		GoodsCatlog goodsCatlog = (GoodsCatlog) abstractLog;
		String whereCourse = goodsCatlog.getSearchDefaultGoodCourse();
		Long cityid = goodsCatlog.getCityid();
		String before3days = DateUtils.getTimeBeforeORAfter(NEW_GOODS_BEFORE, "yyyy-MM-dd 00:00:00");
		StringBuilder sqlBuf = new StringBuilder();
		if (!goodsCatlog.isNull()) {
			// 今日新品：需要关联beiker_goods_on_end_time
			if (goodsCatlog.getIsNew()) {
				if(goodsCatlog.getFeaturetagid() != null){
					sqlBuf.append(
							"SELECT bcg.goodid,bg.currentPrice FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bget.goods_id = bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_biaoqian_goods bbg ON bbg.goodsid=bcg.goodid WHERE bbg.isavailable='y' AND  bcg.enddate>= ? AND  bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount ")
							.append(" AND bbg.biaoqianid=").append(goodsCatlog.getFeaturetagid())
							.append(" AND bget.on_time>='").append(before3days)
							.append("' AND ").append(whereCourse);
				}else{
					sqlBuf.append(
							"SELECT bcg.goodid,bg.currentPrice FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bget.goods_id = bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= ? AND  bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount ")
							.append(" AND bget.on_time>='").append(before3days)
							.append("' AND ").append(whereCourse);
				}
			
			}else if(goodsCatlog.getFeaturetagid() != null){
				sqlBuf.append(
						"SELECT bcg.goodid,bg.currentPrice FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_biaoqian_goods bbg ON bbg.goodsid=bcg.goodid WHERE bbg.isavailable='y' AND bcg.enddate>= ? AND bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount")
						.append(" AND bbg.biaoqianid=").append(goodsCatlog.getFeaturetagid()).append(" AND ")
						.append(whereCourse);
			}else {
				sqlBuf.append(
						"SELECT bcg.goodid,bg.currentPrice FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= ? AND bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount AND ")
						.append(whereCourse);
			}
			sqlBuf.append(" group by bcg.goodid ");
		} else {
			// 今日新品：需要关联beiker_goods_on_end_time
			if (goodsCatlog.getIsNew()) {
				sqlBuf.append(
						"SELECT bcg.goodid,bg.currentPrice FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bget.goods_id = bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= ? AND  bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount ")
						.append(" AND bget.on_time>='").append(before3days)
						.append("' ");
			} else {
				sqlBuf.append("SELECT bcg.goodid,bg.currentPrice FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= ? AND  bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount ");
			}
			// 排除商超卡
			sqlBuf.append(" AND bg.iscard='0' ");
			//现金券 或商品代金券
			if(goodsCatlog.getCashSelected() && goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and (bg.couponcash='1' or bg.couponcash='2' ) ");
			}else if(goodsCatlog.getCashSelected()){
				sqlBuf.append(" and bg.couponcash='1' ");
			}else if(goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and bg.couponcash='2' ");
			}
			sqlBuf.append(" GROUP BY bcg.goodid");
		}
		String curDate = DateUtils.getStringDateShort();
		List list = getJdbcTemplate().queryForList(sqlBuf.toString(),
				new Object[] { curDate, curDate, cityid });
		return list;
	}

	/** 
	 * @date Apr 5, 2012
	 * @param abstractlog 
	 * @return List 通过权重值默认排序符合条件的map{goodid}
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getDefaultGoodsIdBySortWeight(AbstractCatlog abstractlog,Pager pager){
		GoodsCatlog goodsCatlog = (GoodsCatlog)abstractlog;
		String whereCourse = goodsCatlog.getSearchDefaultGoodCourse();
		Long cityid = goodsCatlog.getCityid();
		String before3days = DateUtils.getTimeBeforeORAfter(NEW_GOODS_BEFORE, "yyyy-MM-dd 00:00:00");
		StringBuilder sqlBuf = new StringBuilder();
		if (!goodsCatlog.isNull()) {
			// 今日新品：需要关联beiker_goods_on_end_time
			if (goodsCatlog.getIsNew()) {
				if (goodsCatlog.getFeaturetagid() != null) {
					sqlBuf.append(
							"SELECT bcg.goodid FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bget.goods_id = bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_biaoqian_goods bbg ON bbg.goodsid=bcg.goodid WHERE bbg.isavailable='y' AND bcg.enddate>= ? AND  bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount ")
							.append(" AND bbg.biaoqianid=")
							.append(goodsCatlog.getFeaturetagid())
							.append(" AND bget.on_time >='")
							.append(before3days).append("' AND ")
							.append(whereCourse);
				} else {
					sqlBuf.append(
							"SELECT bcg.goodid FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bget.goods_id = bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= ? AND  bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount ")
							.append(" AND bget.on_time >='")
							.append(before3days).append("' AND ")
							.append(whereCourse);
				}

			} else if (goodsCatlog.getFeaturetagid() != null) {
				sqlBuf.append(
						"SELECT bcg.goodid  FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_biaoqian_goods bbg ON bbg.goodsid=bcg.goodid  WHERE bbg.isavailable='y' AND bcg.enddate>= ? AND bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount ").append(" AND bbg.biaoqianid=").append(goodsCatlog.getFeaturetagid()) 
						.append(" AND ")
						.append(whereCourse);
			} else {
				sqlBuf.append(
						"SELECT bcg.goodid  FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= ? AND bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount AND ")
						.append(whereCourse);
			}
			
			sqlBuf.append(" group by bcg.goodid ");
		} else {
			// 今日新品：需要关联beiker_goods_on_end_time
			if (goodsCatlog.getIsNew()) {
				sqlBuf.append(
						"SELECT bcg.goodid FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bget.goods_id = bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= ? AND  bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount ")
						.append(" AND bget.on_time>='").append(before3days)
						.append("' ");
			} else {
				sqlBuf.append("SELECT bcg.goodid  FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= ? AND  bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount ");
			}
			//排除商超卡
			sqlBuf.append(" AND bg.iscard='0' ");
			//现金券 或商品代金券
			if(goodsCatlog.getCashSelected() && goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and (bg.couponcash='1' or bg.couponcash='2' ) ");
			}else if(goodsCatlog.getCashSelected()){
				sqlBuf.append(" and bg.couponcash='1' ");
			}else if(goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and bg.couponcash='2' ");
			}	
			sqlBuf.append(" GROUP BY bcg.goodid");
		}
		//按权重值默认降序排列
		if(goodsCatlog.isNewVersion()){
			sqlBuf.append(" ORDER BY bcg.sortweights_b DESC ");
		}else{
			sqlBuf.append(" ORDER BY bcg.sortweights DESC ");
		}
		
		sqlBuf.append("LIMIT ").append(pager.getStartRow()).append(" , ").append(pager.getPageSize());
		
		String curDate = DateUtils.getStringDateShort(); 
		List list = getJdbcTemplate().queryForList(sqlBuf.toString(), new Object[] { curDate, curDate, cityid });
		if (list == null || list.size() == 0){
			return null;
		}
		List<Long> goodsIdList = new LinkedList<Long>();
		for (Object object : list) {
			Map<String,Object> map = (Map<String, Object>) object;
			Long goodId = (Long) map.get("goodid");
			goodsIdList.add(goodId);
		}
		return goodsIdList;
	}
	/**
	 * 
	 * @author janwen
	 * @time Dec 26, 2011 4:55:42 PM
	 * 
	 * @return 24小时销售排行榜 map{goods_id,top(24小时销售量)}
	 */
	@Override
	public List getTopSaled() {
		int exp = 5 * 60;
		String key = "GoodsCatlog_getTopSaled";
		MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
		List list = (List) memCacheService.get(key);
		if (list == null || list.size() == 0) {
			String sql = "SELECT btg.goods_id, COUNT(btg.goods_id) top FROM beiker_trxorder_goods btg WHERE btg.create_date BETWEEN ? AND ? AND btg.trx_status !='INIT' GROUP BY btg.goods_id";
			list = getJdbcTemplate().queryForList(
					sql,
					new Object[] { DateUtils.getTimeBeforeORAfter(-1),
							DateUtils.getNowTime() });
			memCacheService.set(key, list, exp);
		}
		return list;
	}

	/**
	 * 
	 * @author janwen
	 * @time Dec 26, 2011 5:11:07 PM
	 * 
	 * @param abstractLog
	 * @return map{goods_id,on_time}
	 */
	@Override
	public List getGoodsOnTime(AbstractCatlog abstractLog) {
		GoodsCatlog goodsCatlog = (GoodsCatlog) abstractLog;
		String whereCourse = goodsCatlog.getSearchDefaultGoodCourse();
		String before3days = DateUtils.getTimeBeforeORAfter(NEW_GOODS_BEFORE,
				"yyyy-MM-dd 00:00:00");
		StringBuilder sqlBuf = new StringBuilder();

		sqlBuf.append("SELECT bget.goods_id,bget.on_time FROM beiker_goods_on_end_time bget LEFT JOIN beiker_catlog_good bcg ON bget.goods_id=bcg.goodid JOIN beiker_goods bg ON bg.goodsid=bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid");
		if(goodsCatlog.getFeaturetagid() != null){
			sqlBuf.append(" JOIN beiker_biaoqian_goods bbg ON bbg.goodsid = bget.goods_id ")
			.append(" WHERE bcg.enddate>= ? AND bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount AND bbg.isavailable='y' AND bbg.biaoqianid=").append(goodsCatlog.getFeaturetagid());
		}else{
			sqlBuf.append(" WHERE bcg.enddate>= ? AND bcg.createdate<= ? AND bcg.isavaliable=1 AND bcg.area_id=? AND bgprofile.sales_count<bg.maxcount ");
		}
		if (!goodsCatlog.isNull()) {
			// 今日新品
			if (goodsCatlog.getIsNew()) {
				if(goodsCatlog.getFeaturetagid() != null){
					
				}
				sqlBuf.append(" AND bget.on_time>='").append(before3days)
						.append("' ");
			}
			sqlBuf.append(" AND ").append(whereCourse);
			sqlBuf.append(" group by bcg.goodid ");
		} else {
			//排除商超卡
			sqlBuf.append("AND bg.iscard='0' ");
			//今日新品
			if(goodsCatlog.getIsNew()){
				sqlBuf.append(" AND bget.on_time>='").append(before3days).append("' ");
			}
			
			//现金券 或商品代金券
			if(goodsCatlog.getCashSelected() && goodsCatlog.getTokenSelected()){
				sqlBuf.append(" AND (bg.couponcash='1' OR bg.couponcash='2' ) ");
			}else if(goodsCatlog.getCashSelected()){
				sqlBuf.append(" AND bg.couponcash='1' ");
			}else if(goodsCatlog.getTokenSelected()){
				sqlBuf.append(" AND bg.couponcash='2' ");
			}
			
			sqlBuf.append(" GROUP BY bcg.goodid");
		}
		String curDate = DateUtils.getStringDateShort();
		List list = getJdbcTemplate().queryForList(sqlBuf.toString(),
				new Object[] { curDate, curDate, goodsCatlog.getCityid() });
		return list;
	}

	@Override
	public List<Long> searchCatlog(AbstractCatlog abstractLog, int start,
			int end) {
		GoodsCatlog goodsCatlog = (GoodsCatlog) abstractLog;
		Long cityid = goodsCatlog.getCityid();
		String whereCourse = goodsCatlog.getSearchGoodCourse();
		String before3days = DateUtils.getTimeBeforeORAfter(NEW_GOODS_BEFORE, "yyyy-MM-dd 00:00:00");
		StringBuilder sqlBuf = new StringBuilder();
		// 日期排序,价格排序,现金券排序,折扣率,好评排序
		sqlBuf.append("select goodid from beiker_catlog_good bcg JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_goods_on_end_time bget ON bcg.goodid=bget.goods_id JOIN beiker_goods bg ON bg.goodsid=bcg.goodid");
		 if(abstractLog.getFeaturetagid() != null){
			 sqlBuf.append(" JOIN beiker_biaoqian_goods bbg ON bbg.goodsid=bcg.goodid ");
		 }
		sqlBuf.append(
				" where bcg.enddate>='" + DateUtils.getStringDateShort()  + "' and bcg.createdate<='" + DateUtils.getStringDateShort() + "' and bcg.isavaliable=1 and bgprofile.sales_count<bg.maxcount AND area_id=")
				.append(cityid);
		
		if(goodsCatlog.getFeaturetagid() != null){
			sqlBuf.append(" AND bbg.isavailable='y' AND bbg.biaoqianid=").append(abstractLog.getFeaturetagid());
		}
		sqlBuf.append(" and ");
		if (abstractLog.getIsNew()) {
			sqlBuf.append(" bget.on_time>='").append(before3days)
					.append("' and ");
		}
		sqlBuf.append(whereCourse);
		sqlBuf.append(" limit ").append(start).append(",").append(end);
		
		List list = this.getJdbcTemplate().queryForList(sqlBuf.toString());
		if (list == null || list.size() == 0)
			return null;
		List<Long> goodIdList = new LinkedList<Long>();
		for (Object object : list) {
			Map map = (Map) object;
			Long goodId = (Long) map.get("goodid");
			goodIdList.add(goodId);
		}
		return goodIdList;
	}

	@Override
	public int searchCatlogCount(AbstractCatlog abstractLog) {
		GoodsCatlog goodsCatlog = (GoodsCatlog) abstractLog;
		Long cityid = goodsCatlog.getCityid();
		String whereCourse = goodsCatlog.getFeatureSearchGoodCourse();
		String curDate = DateUtils.getStringDateShort();
		String before3days = DateUtils.getTimeBeforeORAfter(NEW_GOODS_BEFORE, "yyyy-MM-dd 00:00:00");
		
		StringBuilder sqlBuf = new StringBuilder();
		if (abstractLog.getIsNew()) {
			if (abstractLog.getFeaturetagid() != null) {
				sqlBuf.append("select count(distinct goodid) as count from beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bcg.goodid=bget.goods_id JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_biaoqian_goods bbg on bbg.goodsid=bcg.goodid  ");
			} else {
				sqlBuf.append("select count(distinct goodid) as count from beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bcg.goodid=bget.goods_id JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid ");
			}
		} else {
			// 查询带有标签的商品
			if (abstractLog.getFeaturetagid() != null) {
				sqlBuf.append("select count(distinct goodid) as count from beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_biaoqian_goods bbg on bbg.goodsid=bcg.goodid ");
			} else {
				sqlBuf.append("select count(distinct goodid) as count from beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid ");
			}
		}
		if (!goodsCatlog.isNull()) {
			whereCourse = whereCourse.substring(0,
					whereCourse.indexOf(" group by bcg.goodid "));
			sqlBuf.append(" where ").append(whereCourse);
			sqlBuf.append(" and bcg.enddate>='").append(curDate)
					.append("' and bcg.createdate<='");
			sqlBuf.append(curDate)
					.append("' and bcg.isavaliable=1 and bcg.area_id=")
					.append(cityid);
			sqlBuf.append(" and bgprofile.sales_count<bg.maxcount ");
			//排除商超卡
			sqlBuf.append(" and bg.iscard='0' ");
			//今日新品
			if(abstractLog.getIsNew()){
				sqlBuf.append(" and bget.on_time>='").append(before3days).append("'");
			}
		}else {
			//全部商品
			sqlBuf.append(" where bcg.enddate>='").append(curDate).append("' and bcg.createdate<='");
			sqlBuf.append(curDate).append("' and bcg.isavaliable=1 and bcg.area_id=").append(cityid);
			sqlBuf.append(" and bgprofile.sales_count<bg.maxcount ");
			//排除商超卡
			sqlBuf.append(" and bg.iscard='0' ");
			//今日新品
			if(abstractLog.getIsNew()){
				sqlBuf.append(" and bget.on_time>='").append(before3days).append("'");
			}
			//现金券 或商品代金券
			if(goodsCatlog.getCashSelected() && goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and (bg.couponcash='1' or bg.couponcash='2' ) ");
			}else if(goodsCatlog.getCashSelected()){
				sqlBuf.append(" and bg.couponcash='1' ");
			}else if(goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and bg.couponcash='2' ");
			}
		}

		log.debug("searchCatlogCount search count sql: " + sqlBuf.toString());
		List list = this.getJdbcTemplate().queryForList(sqlBuf.toString());
		if (list == null || list.size() == 0)
			return 0;

		Long count = 0L;

		for (Object object : list) {
			Map map = (Map) object;
			count = (Long) map.get("count");
		}

		return Integer.parseInt(count + "");

	}

	/*@Override
	public String getCatByID(Long id) {
		String sql = "select tag_name from beiker_tag_property where id in(select tagid from beiker_catlog_good where goodid="
				+ id + ") and parentid=0";
		String cat = "";
		List<String> tagParent = getJdbcTemplate().query(sql, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("tag_name");
			}
		});
		if (tagParent.size() > 0) {
			cat = tagParent.get(0);
		}

		// FIXME
		String sqlTagext = "select tag_name from beiker_tag_property where id in(select tagextid from beiker_catlog_good where goodid="
				+ id + ")";
		List<String> tagExt = getJdbcTemplate().query(sql, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("tag_name");
			}
		});
		if (tagExt.size() > 0) {
			cat = cat + tagExt.get(0);
		}
		return cat;
	}
*/
	@Override
	public List<Long> getCatlogRank(AbstractCatlog abstractCatlog, Pager pager) {

	//	String sql = "SELECT goods_id,COUNT(goods_id) c FROM (SELECT btg.goods_id FROM beiker_trxorder_goods btg LEFT JOIN beiker_catlog_good bcg ON bcg.goodid = btg.goods_id WHERE btg.trx_status != 'INIT' AND btg.create_date BETWEEN ? AND ? AND bcg.area_id =? AND bcg.isavaliable =1 GROUP BY btg.id) tbl GROUP BY goods_id ORDER BY c DESC LIMIT 0,9";

		String sql = "SELECT btg.goods_id,count(distinct btg.goods_id, btg.id) c FROM beiker_trxorder_goods btg LEFT JOIN beiker_catlog_good bcg ON bcg.goodid = btg.goods_id WHERE btg.trx_status != 'INIT' AND btg.create_date BETWEEN ? AND ? AND bcg.area_id =? AND bcg.isavaliable =1 GROUP BY btg.goods_id ORDER BY c DESC LIMIT 0,9";
		GoodsCatlog goodsCatlog = (GoodsCatlog) abstractCatlog;
		List results = getJdbcTemplate().queryForList(
				sql,
				new Object[] { DateUtils.getTimeBeforeORAfter(-1),
						DateUtils.getNowTime(), goodsCatlog.getCityid() });

		List<Long> goodIdList = new ArrayList<Long>();

		for (Object object : results) {
			Map map = (Map) object;
			Long goodId = (Long) map.get("goods_id");
			goodIdList.add(goodId);
		}
		return goodIdList;
	}

	@Override
	public List getGoodsSaled() {
		String sql = "SELECT (bgp.sales_count+ bg.virtual_count) saled,bg.goodsid FROM beiker_goods bg JOIN beiker_goods_profile bgp ON bg.goodsid=bgp.goodsid";

		List list = getJdbcTemplate().queryForList(sql);
		return list;
	}

	@Override
	public List<Map<String, Object>> getGoodsCatlogList(Long cityid, int curPage) {
		String curDate = DateUtils.getStringDateShort();
		StringBuilder bufSel = new StringBuilder(
				"select distinct bcg.goodid,bcg.regionid,bcg.regionextid, ");
		bufSel.append(
				"bcg.tagid,bcg.tagextid,bget.on_time,bg.currentPrice,bg.couponcash ")
				.append("from beiker_catlog_good bcg ")
				.append("JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid ")
				.append("JOIN beiker_goods_on_end_time bget ON bcg.goodid=bget.goods_id ")
				.append("JOIN beiker_goods bg ON bg.goodsid=bcg.goodid ")
				.append("where enddate>=? and createdate<=? ")
				.append("and bcg.isavaliable=1 and bgprofile.sales_count<bg.maxcount and bcg.area_id=? and bg.iscard='0' order by bcg.goodid ")
				.append("limit ?,1000 ");
		return this.getSimpleJdbcTemplate().queryForList(bufSel.toString(),curDate,curDate,
				cityid, curPage * 1000);
	}

	@Override
	public List<Map<String, Object>> getGoodsBiaoqianList(String goodsIds) {
		StringBuilder bufSel = new StringBuilder(
				"select distinct goodsid,biaoqianid ");
		bufSel.append("from beiker_biaoqian_goods ")
				.append("where isavailable='y' and goodsid in (").append(goodsIds).append(") ")
				.append("order by goodsid");
		return this.getSimpleJdbcTemplate().queryForList(bufSel.toString());
	}

	@Override
	public List<Map> getFeaturedGoods(List<Long> goodsids,
			AbstractCatlog queryCriteria, Pager pager) {
		if (goodsids != null && goodsids.size() > 0) {
			StringBuilder sql = new StringBuilder(
					"SELECT DISTINCT bbg.goodsid FROM beiker_biaoqian_goods bbg WHERE bbg.goodsid IN(");
			sql.append(StringUtils.arrayToString(goodsids.toArray(), ","))
					.append(")").append(" AND bbg.biaoqianid=")
					.append(queryCriteria.getFeaturetagid())
					.append(" AND bbg.isavailable='y' LIMIT ")
					.append(pager.getStartRow()).append(",")
					.append(pager.getPageSize());
			return getJdbcTemplate().queryForList(sql.toString());
		}

		return null;
	}

	
	/**
	 * 从指定的商品id范围内查询商品
	 * @param validGoodsIdList 商品idlist
	 * @param abstractLog
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public List<Long> searchCatlog(List<Long> validGoodsIdList, AbstractCatlog abstractLog, int start, int end) {
		GoodsCatlog goodsCatlog = (GoodsCatlog) abstractLog;
		Long cityid = goodsCatlog.getCityid();
		String whereCourse = goodsCatlog.getSearchGoodCourse();
		String before3days = DateUtils.getTimeBeforeORAfter(NEW_GOODS_BEFORE, "yyyy-MM-dd 00:00:00");
		StringBuilder sqlBuf = new StringBuilder();
		// 日期排序,价格排序,现金券排序,折扣率,好评排序
		sqlBuf.append("select goodid from beiker_catlog_good bcg JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_goods_on_end_time bget ON bcg.goodid=bget.goods_id JOIN beiker_goods bg ON bg.goodsid=bcg.goodid");
		if (abstractLog.getFeaturetagid() != null) {
			sqlBuf.append(" JOIN beiker_biaoqian_goods bbg ON bbg.goodsid=bcg.goodid ");
		}
		sqlBuf.append(" where bcg.enddate>='" + DateUtils.getStringDateShort() + "' and bcg.createdate<='" + DateUtils.getStringDateShort() + "' and bcg.isavaliable=1 and bgprofile.sales_count<bg.maxcount AND area_id=").append(cityid);
		
		//商品id范围
		sqlBuf.append(" and bg.goodsid in (:validGoodsIdList)");

		if (goodsCatlog.getFeaturetagid() != null) {
			sqlBuf.append(" AND bbg.isavailable='y' AND bbg.biaoqianid=").append(abstractLog.getFeaturetagid());
		}
		sqlBuf.append(" and ");
		if (abstractLog.getIsNew()) {
			sqlBuf.append(" bget.on_time>='").append(before3days).append("' and ");
		}
		sqlBuf.append(whereCourse);
		sqlBuf.append(" limit ").append(start).append(",").append(end);
		
		
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("validGoodsIdList", validGoodsIdList);
		List list = this.getSimpleJdbcTemplate().queryForList(sqlBuf.toString(),parameterSource);
		
		
		if (list == null || list.size() == 0)
			return null;
		List<Long> goodIdList = new LinkedList<Long>();
		for (Object object : list) {
			Map map = (Map) object;
			Long goodId = (Long) map.get("goodid");
			goodIdList.add(goodId);
		}
		return goodIdList;
	}

	@Override
	public List searchDefaultGoodsId(List<Long> validGoodsIdList, AbstractCatlog abstractLog) {
		GoodsCatlog goodsCatlog = (GoodsCatlog) abstractLog;
		String whereCourse = goodsCatlog.getSearchDefaultGoodCourse();
		Long cityid = goodsCatlog.getCityid();
		String before3days = DateUtils.getTimeBeforeORAfter(NEW_GOODS_BEFORE, "yyyy-MM-dd 00:00:00");
		StringBuilder sqlBuf = new StringBuilder();
		if (!goodsCatlog.isNull()) {
			// 今日新品：需要关联beiker_goods_on_end_time
			if (goodsCatlog.getIsNew()) {
				if (goodsCatlog.getFeaturetagid() != null) {
					sqlBuf.append(
							"SELECT bcg.goodid,bg.currentPrice FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bget.goods_id = bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_biaoqian_goods bbg ON bbg.goodsid=bcg.goodid WHERE bbg.isavailable='y' AND  bcg.enddate>= :endDate AND  bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount ")
							.append(" AND bbg.biaoqianid=").append(goodsCatlog.getFeaturetagid()).append(" AND bget.on_time>='").append(before3days).append("' AND ").append(whereCourse);
				} else {
					sqlBuf.append(
							"SELECT bcg.goodid,bg.currentPrice FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bget.goods_id = bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= :endDate AND  bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount ")
							.append(" AND bget.on_time>='").append(before3days).append("' AND ").append(whereCourse);
				}

			} else if (goodsCatlog.getFeaturetagid() != null) {
				sqlBuf.append(
						"SELECT bcg.goodid,bg.currentPrice FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_biaoqian_goods bbg ON bbg.goodsid=bcg.goodid WHERE bbg.isavailable='y' AND bcg.enddate>= :endDate AND bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount")
						.append(" AND bbg.biaoqianid=").append(goodsCatlog.getFeaturetagid()).append(" AND ").append(whereCourse);
			} else {
				sqlBuf.append("SELECT bcg.goodid,bg.currentPrice FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= :endDate AND bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount AND ").append(whereCourse);
			}
			
			//商品id范围
			sqlBuf.append(" and bg.goodsid in (:validGoodsIdList)");
			
			sqlBuf.append(" group by bcg.goodid ");
		} else {
			// 今日新品：需要关联beiker_goods_on_end_time
			if (goodsCatlog.getIsNew()) {
				sqlBuf.append("SELECT bcg.goodid,bg.currentPrice FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bget.goods_id = bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= :endDate AND  bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount ")
						.append(" AND bget.on_time>='").append(before3days).append("' ");
			} else {
				sqlBuf.append("SELECT bcg.goodid,bg.currentPrice FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= :endDate AND  bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount ");
			}
			// 排除商超卡
			sqlBuf.append(" AND bg.iscard='0' ");
			//现金券 或商品代金券
			if(goodsCatlog.getCashSelected() && goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and (bg.couponcash='1' or bg.couponcash='2' ) ");
			}else if(goodsCatlog.getCashSelected()){
				sqlBuf.append(" and bg.couponcash='1' ");
			}else if(goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and bg.couponcash='2' ");
			}
			
			//商品id范围
			sqlBuf.append(" and bg.goodsid in (:validGoodsIdList)");
			
			sqlBuf.append(" GROUP BY bcg.goodid");
		}
		
		String curDate = DateUtils.getStringDateShort();

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("endDate", curDate);
		parameterSource.addValue("createDate", curDate);
		parameterSource.addValue("areaId", cityid);
		parameterSource.addValue("validGoodsIdList", validGoodsIdList);
		
		List<Map<String,Object>> list = getSimpleJdbcTemplate().queryForList(sqlBuf.toString(), parameterSource);
		return list;
	}
	/**
	 * 从指定的商品id范围内查询商品
	 * @param validGoodsIdList 商品idlist
	 * @param abstractlog
	 * @param pager
	 * @return
	 */
	@Override
	public List<Long> getDefaultGoodsIdBySortWeight(List<Long> validGoodsIdList, AbstractCatlog abstractlog, Pager pager) {
		GoodsCatlog goodsCatlog = (GoodsCatlog) abstractlog;
		String whereCourse = goodsCatlog.getSearchDefaultGoodCourse();
		Long cityid = goodsCatlog.getCityid();
		String before3days = DateUtils.getTimeBeforeORAfter(NEW_GOODS_BEFORE, "yyyy-MM-dd 00:00:00");
		StringBuilder sqlBuf = new StringBuilder();
		if (!goodsCatlog.isNull()) {
			// 今日新品：需要关联beiker_goods_on_end_time
			if (goodsCatlog.getIsNew()) {
				if (goodsCatlog.getFeaturetagid() != null) {
					sqlBuf.append(
							"SELECT bcg.goodid FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bget.goods_id = bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_biaoqian_goods bbg ON bbg.goodsid=bcg.goodid WHERE bbg.isavailable='y' AND bcg.enddate>= :endDate AND  bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount ")
							.append(" AND bbg.biaoqianid=").append(goodsCatlog.getFeaturetagid()).append(" AND bget.on_time >='").append(before3days).append("' AND ").append(whereCourse);
				} else {
					sqlBuf.append("SELECT bcg.goodid FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bget.goods_id = bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= :endDate AND  bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount ")
							.append(" AND bget.on_time >='").append(before3days).append("' AND ").append(whereCourse);
				}

			} else if (goodsCatlog.getFeaturetagid() != null) {
				sqlBuf.append("SELECT bcg.goodid  FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_biaoqian_goods bbg ON bbg.goodsid=bcg.goodid  WHERE bbg.isavailable='y' AND bcg.enddate>= :endDate AND bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount ")
						.append(" AND bbg.biaoqianid=").append(goodsCatlog.getFeaturetagid()).append(" AND ").append(whereCourse);
			} else {
				sqlBuf.append("SELECT bcg.goodid  FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= :endDate AND bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount AND ").append(whereCourse);
			}
			
			//商品id范围
			sqlBuf.append(" and bg.goodsid in (:validGoodsIdList)");
			
			sqlBuf.append(" group by bcg.goodid ");
		} else {
			// 今日新品：需要关联beiker_goods_on_end_time
			if (goodsCatlog.getIsNew()) {
				sqlBuf.append("SELECT bcg.goodid FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bget.goods_id = bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= :endDate AND  bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount ")
						.append(" AND bget.on_time>='").append(before3days).append("' ");
			} else {
				sqlBuf.append("SELECT bcg.goodid  FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid WHERE bcg.enddate>= :endDate AND  bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount ");
			}
			//排除商超卡
			sqlBuf.append(" AND bg.iscard='0' ");
			//现金券 或商品代金券
			if(goodsCatlog.getCashSelected() && goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and (bg.couponcash='1' or bg.couponcash='2' ) ");
			}else if(goodsCatlog.getCashSelected()){
				sqlBuf.append(" and bg.couponcash='1' ");
			}else if(goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and bg.couponcash='2' ");
			}
			
			//商品id范围
			sqlBuf.append(" and bg.goodsid in (:validGoodsIdList)");
			
			sqlBuf.append(" GROUP BY bcg.goodid");
		}
		//按权重值默认降序排列
		if (goodsCatlog.isNewVersion()) {
			sqlBuf.append(" ORDER BY bcg.sortweights_b DESC ");
		} else {
			sqlBuf.append(" ORDER BY bcg.sortweights DESC ");
		}

		sqlBuf.append("LIMIT ").append(pager.getStartRow()).append(" , ").append(pager.getPageSize());

		String curDate = DateUtils.getStringDateShort();
		
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("endDate", curDate);
		parameterSource.addValue("createDate", curDate);
		parameterSource.addValue("areaId", cityid);
		parameterSource.addValue("validGoodsIdList", validGoodsIdList);
		
		List<Long> result = getSimpleJdbcTemplate().query(sqlBuf.toString(), new ParameterizedRowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("bcg.goodid");
			}
		}, parameterSource);
		
		if (result == null || result.size() == 0) {
			return null;
		}
		return result;
	}
	
	@Override
	public int searchCatlogCount(List<Long> validGoodsIdList, AbstractCatlog abstractLog) {
		GoodsCatlog goodsCatlog = (GoodsCatlog) abstractLog;
		Long cityid = goodsCatlog.getCityid();
		String whereCourse = goodsCatlog.getFeatureSearchGoodCourse();
		String curDate = DateUtils.getStringDateShort();
		String before3days = DateUtils.getTimeBeforeORAfter(NEW_GOODS_BEFORE, "yyyy-MM-dd 00:00:00");

		StringBuilder sqlBuf = new StringBuilder();
		if (abstractLog.getIsNew()) {
			if (abstractLog.getFeaturetagid() != null) {
				sqlBuf.append("select count(distinct goodid) as count from beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bcg.goodid=bget.goods_id JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_biaoqian_goods bbg on bbg.goodsid=bcg.goodid  ");
			} else {
				sqlBuf.append("select count(distinct goodid) as count from beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_on_end_time bget ON bcg.goodid=bget.goods_id JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid ");
			}
		} else {
			// 查询带有标签的商品
			if (abstractLog.getFeaturetagid() != null) {
				sqlBuf.append("select count(distinct goodid) as count from beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid JOIN beiker_biaoqian_goods bbg on bbg.goodsid=bcg.goodid ");
			} else {
				sqlBuf.append("select count(distinct goodid) as count from beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid ");
			}
		}
		if (!goodsCatlog.isNull()) {
			whereCourse = whereCourse.substring(0, whereCourse.indexOf(" group by bcg.goodid "));
			sqlBuf.append(" where ").append(whereCourse);
			sqlBuf.append(" and bcg.enddate>='").append(curDate).append("' and bcg.createdate<='");
			sqlBuf.append(curDate).append("' and bcg.isavaliable=1 and bcg.area_id=").append(cityid);
			sqlBuf.append(" and bgprofile.sales_count<bg.maxcount ");
			//排除商超卡
			sqlBuf.append(" and bg.iscard='0' ");
			//今日新品
			if (abstractLog.getIsNew()) {
				sqlBuf.append(" and bget.on_time>='").append(before3days).append("'");
			}
		} else {
			//全部商品
			sqlBuf.append(" where bcg.enddate>='").append(curDate).append("' and bcg.createdate<='");
			sqlBuf.append(curDate).append("' and bcg.isavaliable=1 and bcg.area_id=").append(cityid);
			sqlBuf.append(" and bgprofile.sales_count<bg.maxcount ");
			//排除商超卡
			sqlBuf.append(" and bg.iscard='0' ");
			//今日新品
			if (abstractLog.getIsNew()) {
				sqlBuf.append(" and bget.on_time>='").append(before3days).append("'");
			}
			//现金券 或商品代金券
			if(goodsCatlog.getCashSelected() && goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and (bg.couponcash='1' or bg.couponcash='2' ) ");
			}else if(goodsCatlog.getCashSelected()){
				sqlBuf.append(" and bg.couponcash='1' ");
			}else if(goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and bg.couponcash='2' ");
			}
		}

		//商品id范围
		sqlBuf.append(" and bg.goodsid in (:validGoodsIdList)");

		log.debug("searchCatlogCount search count sql: " + sqlBuf.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("validGoodsIdList", validGoodsIdList);

		List list = this.getSimpleJdbcTemplate().queryForList(sqlBuf.toString(), parameterSource);
		if (list == null || list.size() == 0) {
			return 0;
		}
		Long count = 0L;

		for (Object object : list) {
			Map map = (Map) object;
			count = (Long) map.get("count");
		}

		return Integer.parseInt(count + "");

	}
	
	
	
	@Override
	public List getGoodsOnTime(List<Long> validGoodsIdList, AbstractCatlog abstractLog) {
		GoodsCatlog goodsCatlog = (GoodsCatlog) abstractLog;
		String whereCourse = goodsCatlog.getSearchDefaultGoodCourse();
		String before3days = DateUtils.getTimeBeforeORAfter(NEW_GOODS_BEFORE, "yyyy-MM-dd 00:00:00");
		StringBuilder sqlBuf = new StringBuilder();

		sqlBuf.append("SELECT bget.goods_id,bget.on_time FROM beiker_goods_on_end_time bget LEFT JOIN beiker_catlog_good bcg ON bget.goods_id=bcg.goodid JOIN beiker_goods bg ON bg.goodsid=bcg.goodid JOIN beiker_goods_profile bgprofile ON bcg.goodid=bgprofile.goodsid");
		if (goodsCatlog.getFeaturetagid() != null) {
			sqlBuf.append(" JOIN beiker_biaoqian_goods bbg ON bbg.goodsid = bget.goods_id ").append(" WHERE bcg.enddate>= :endDate AND bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount AND bbg.isavailable='y' AND bbg.biaoqianid=").append(goodsCatlog.getFeaturetagid());
		} else {
			sqlBuf.append(" WHERE bcg.enddate>= :endDate AND bcg.createdate<= :createDate AND bcg.isavaliable=1 AND bcg.area_id=:areaId AND bgprofile.sales_count<bg.maxcount ");
		}
		if (!goodsCatlog.isNull()) {
			// 今日新品
			if (goodsCatlog.getIsNew()) {
				if (goodsCatlog.getFeaturetagid() != null) {

				}
				sqlBuf.append(" AND bget.on_time>='").append(before3days).append("' ");
			}
			sqlBuf.append(" AND ").append(whereCourse);
			//商品id范围
			sqlBuf.append(" and bg.goodsid in (:validGoodsIdList)");
			
			sqlBuf.append(" group by bcg.goodid ");
		} else {
			//排除商超卡
			sqlBuf.append("AND bg.iscard='0' ");
			//今日新品
			if (goodsCatlog.getIsNew()) {
				sqlBuf.append(" AND bget.on_time>='").append(before3days).append("' ");
			}

			//现金券 或商品代金券
			if(goodsCatlog.getCashSelected() && goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and (bg.couponcash='1' or bg.couponcash='2' ) ");
			}else if(goodsCatlog.getCashSelected()){
				sqlBuf.append(" and bg.couponcash='1' ");
			}else if(goodsCatlog.getTokenSelected()){
				sqlBuf.append(" and bg.couponcash='2' ");
			}

			//商品id范围
			sqlBuf.append(" and bg.goodsid in (:validGoodsIdList)");
			
			sqlBuf.append(" GROUP BY bcg.goodid");
		}
		String curDate = DateUtils.getStringDateShort();
		
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("endDate", curDate);
		parameterSource.addValue("createDate", curDate);
		parameterSource.addValue("areaId", goodsCatlog.getCityid());
		parameterSource.addValue("validGoodsIdList", validGoodsIdList);
		
		List list = getSimpleJdbcTemplate().queryForList(sqlBuf.toString(),parameterSource);
		return list;
	}
}
