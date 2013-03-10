package com.beike.dao.goods.ad.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.goods.ad.ADGoodsDao;

@Repository("adGoodsDao")
public class ADGoodsDaoImpl extends GenericDaoImpl implements ADGoodsDao {

	@Override
	public List<Long> getSameCategoryGoods(int cityid, int categoryid,
			String type, String notinGoodsid, int limit) {
		StringBuilder sql = new StringBuilder();
		sql
				.append("SELECT ad.goodsid FROM beiker_adgoodsinfo ad WHERE ad.cityid=? AND ad.`type`=? ");
		if ("2".equals(type)) {
			sql.append("AND ad.goodsid NOT IN(").append(notinGoodsid).append(
					") ").append("ORDER BY ad.id ASC ").append("LIMIT ")
					.append(limit);
			List<Long> goodsidList = getJdbcTemplate().queryForList(
					sql.toString(), new Object[] { cityid, type }, Long.class);
			return goodsidList;
		} else {
			sql.append(" AND ad.catlogid=? AND ad.goodsid NOT IN(").append(
					notinGoodsid).append(") ").append("ORDER BY ad.id ASC ")
					.append("LIMIT ").append(limit);
			List<Long> goodsidList = getJdbcTemplate().queryForList(
					sql.toString(), new Object[] { cityid, type, categoryid },
					Long.class);
			return goodsidList;
		}

	}

}
