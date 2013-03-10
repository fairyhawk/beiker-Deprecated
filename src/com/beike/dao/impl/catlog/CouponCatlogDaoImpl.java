package com.beike.dao.impl.catlog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.catlog.CatlogDao;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.entity.catlog.CouponCatlog;
import com.beike.page.Pager;
import com.beike.util.DateUtils;

/**
 * <p>
 * Title:优惠券 地域、属性数据库操作
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
@Repository("couponCatlogDao")
public class CouponCatlogDaoImpl extends GenericDaoImpl<CouponCatlog, Long>
		implements CatlogDao {

	private final Log log = LogFactory.getLog(this.getClass());

	public List<Long> searchCatlog(AbstractCatlog abstractLog) {
		CouponCatlog couponCatlog = (CouponCatlog) abstractLog;
		String whereCourse = couponCatlog.getSearchCourse();
		String sql = "select goodid from beiker_catlog_coupon ";
		if (!couponCatlog.isNull()) {
			sql += " where " + whereCourse;
		} else if (!couponCatlog.isOrderByNull()) {
			sql += whereCourse;
		} else {
			sql += whereCourse;
		}
		List list = this.getJdbcTemplate().queryForList(sql);
		if (list == null || list.size() == 0)
			return null;
		List<Long> couponIdList = new ArrayList<Long>();
		for (Object object : list) {
			Map map = (Map) object;
			Long couponid = (Long) map.get("couponid");
			couponIdList.add(couponid);
		}
		return couponIdList;
	}

	public List<Long> searchCatlog(AbstractCatlog abstractLog, int start,
			int end) {
		CouponCatlog couponCatlog = (CouponCatlog) abstractLog;
		Long cityid=couponCatlog.getCityid();
		String whereCourse = couponCatlog.getSearchCourse();
		String sql = "select couponid from beiker_catlog_coupon";
		String curDate = DateUtils.getStringDateShort();
		if (!couponCatlog.isNull()) {
			sql += " where  enddate>="+curDate+" and createdate<="+curDate+" and isavaliable='1' and area_id="+cityid+" and ";
			sql += whereCourse;
		} else if (!couponCatlog.isOrderByNull()) {
			sql += " where  enddate>="+curDate+" and createdate<="+curDate+" and isavaliable='1' and area_id="+cityid+" ";
			sql += whereCourse;
		} else {
			sql += " where  enddate>="+curDate+" and createdate<="+curDate+" and isavaliable='1' and area_id="+cityid+" ";
			sql += whereCourse;
		}
		sql += " limit " + start + "," + end;
		List list = this.getJdbcTemplate().queryForList(sql);
		if (list == null || list.size() == 0)
			return null;
		List<Long> couponIdList = new LinkedList<Long>();
		for (Object object : list) {
			Map map = (Map) object;
			Long couponid = (Long) map.get("couponid");
			couponIdList.add(couponid);
		}
		return couponIdList;

	}

	public int searchCatlogCount(AbstractCatlog abstractLog) {
		CouponCatlog couponCatlog = (CouponCatlog) abstractLog;
		Long cityid=couponCatlog.getCityid();
		String whereCourse = couponCatlog.getSearchCourse();
		String sql = "select count(distinct couponid) as count from beiker_catlog_coupon ";
		if (!couponCatlog.isNull()) {
			whereCourse = whereCourse.substring(0,
					whereCourse.indexOf(" group by couponid "));
			sql += " where " + whereCourse;
			sql += "  and enddate>=date_format(curdate(),'%Y%m%d') and createdate<=date_format(curdate(),'%Y%m%d') and area_id="+cityid+" ";
		} else {
			sql += " where enddate>=date_format(curdate(),'%Y%m%d') and createdate<=date_format(curdate(),'%Y%m%d') and area_id="+cityid+" ";
		}
		List list = this.getJdbcTemplate().queryForList(sql);
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
		String sql = "select tag_name from beiker_tag_property where id in(select tagid from beiker_catlog_coupon where couponid=" + id + ") and parentid=0";
		String cat = "";
		List<String> tagParent = getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("tag_name");
			}
		});
		if(tagParent.size()>0){
			cat = tagParent.get(0);
		}
		
		//FIXME
		String sqlTagext = "select tag_name from beiker_tag_property where id in(select tagextid from beiker_catlog_coupon where couponid=" + id + ")";
		List<String> tagExt = getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("tag_name");
			}
		});
		if(tagExt.size() > 0){
			cat = cat + tagExt.get(0);
		}
		return cat;
	}*/
	@Override
	public List<Long> getCatlogRank(AbstractCatlog abstractCatlog, Pager pager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getGoodsOnTime(AbstractCatlog abstractLog) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getTopSaled() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> searchDefaultGoodsId(AbstractCatlog abstractLog) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getGoodsSaled() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getGoodsCatlogList(Long cityid, int curPage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getDefaultGoodsIdBySortWeight(AbstractCatlog abstractlog,
			Pager pager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getGoodsBiaoqianList(String goodsIds) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Map> getFeaturedGoods(List<Long> goodsids,
			AbstractCatlog query, Pager pager) {
		// TODO Auto-generated method stub
		return null;
	}
}
