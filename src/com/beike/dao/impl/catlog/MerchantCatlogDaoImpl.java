package com.beike.dao.impl.catlog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.catlog.CatlogDao;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.entity.catlog.MerchantCatlog;
import com.beike.page.Pager;

/**
 * <p>
 * Title:品牌地域、属性分类 操作
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
@Repository("brandCatlogDao")
public class MerchantCatlogDaoImpl extends GenericDaoImpl<MerchantCatlog, Long>
		implements CatlogDao {

	private final Log log = LogFactory.getLog(this.getClass());

	public List<Long> searchCatlog(AbstractCatlog abstractLog) {
		MerchantCatlog merchantCatlog = (MerchantCatlog) abstractLog;
		
		String whereCourse = merchantCatlog.getSearchCourse();
		String sql = "select brandid from beiker_catlog_good ";
		if (!merchantCatlog.isNull()) {
			sql += " where " + whereCourse;
		} else if (!merchantCatlog.isOrderByNull()) {
			sql += whereCourse;
		} else {
			sql += whereCourse;
		}
		List list = this.getJdbcTemplate().queryForList(sql);
		if (list == null || list.size() == 0)
			return null;
		List<Long> goodIdList = new ArrayList<Long>();
		for (Object object : list) {
			Map map = (Map) object;
			Long goodId = (Long) map.get("brandid");
			goodIdList.add(goodId);
		}

		return goodIdList;

	}

	public List<Long> searchCatlog(AbstractCatlog abstractLog, int start,
			int end) {
		MerchantCatlog merchantCatlog = (MerchantCatlog) abstractLog;
		Long cityid=merchantCatlog.getCityid();
		String whereCourse = merchantCatlog.getSearchCourse();
		String sql = "select brandid from beiker_catlog_good ";
		if (!merchantCatlog.isNull()) {
			sql += " where isavaliable='1' and area_id="+cityid+" and " + whereCourse;
		} else if (!merchantCatlog.isOrderByNull()) {
			sql += " where isavaliable='1' and area_id="+cityid+" "+whereCourse;
		} else {
			sql += " where isavaliable='1' and area_id="+cityid+" "+whereCourse;
		}
		sql += " limit " + start + "," + end;
		List list = this.getJdbcTemplate().queryForList(sql);
		if (list == null || list.size() == 0)
			return null;
		List<Long> goodIdList = new LinkedList<Long>();
		for (Object object : list) {
			Map map = (Map) object;
			Long goodId = (Long) map.get("brandid");
			goodIdList.add(goodId);
		}
		return goodIdList;

	}

	public int searchCatlogCount(AbstractCatlog abstractLog) {
		MerchantCatlog merchantCatlog = (MerchantCatlog) abstractLog;
		Long cityid=merchantCatlog.getCityid();
		String whereCourse = merchantCatlog.getSearchCourse();
		String sql = "select count(distinct brandid) as count from beiker_catlog_good ";
		if (!merchantCatlog.isNull()) {
			whereCourse = whereCourse.substring(0,
					whereCourse.indexOf(" group by brandid "));
			sql += " where isavaliable='1' and area_id="+cityid+" and " + whereCourse;
		}else{
			sql+=" where isavaliable='1' and area_id="+cityid;
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
		// TODO Auto-generated method stub
		return null;
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
