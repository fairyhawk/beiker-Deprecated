package com.beike.wap.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.wap.dao.MMerchantDao;
import com.beike.wap.entity.MMerchant;
import com.beike.wap.entity.MMerchantProfileType;

@Repository("mMerchantDao")
public class MMerchantDaoImpl extends GenericDaoImpl<MMerchant, Long> implements MMerchantDao {
	/** 日志记录 */
	private static Log log = LogFactory.getLog(MMerchantDaoImpl.class);
	@Override
	public Long getLastInsertId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MMerchant getBrandById(long brandId) {
		
		String sql = "select DISTINCT bm.merchantid as merchantid, bm.virtualcount, bmp.mc_logo1 mc_logo1,bmp.mc_logo4 mc_logo4, "+
		" bmp.mc_avg_scores mc_avg_scores,bmp.mc_evaliation_count mc_evaliation_count,bmp.mc_sale_count mc_sale_count,bmp.mc_fix_tel mc_fix_tel,bm.sevenrefound, " +
		" bm.overrefound,bm.quality,bm.merchantintroduction,bm.merchantdesc,bm.merchantname " +
		" from  beiker_merchant_profile bmp left join  beiker_merchant bm on bm.merchantid=bmp.merchantid  " +
		" where bm.parentid=0 and bm.merchantid=? ";
		
		log.info("get brand sql : \n" + sql + "\n id = " + brandId);
		List list = getJdbcTemplate().queryForList(sql,
				new Object[] { brandId });
		if (list == null || list.size() == 0)
			return null;
		MMerchant brand = new MMerchant();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			// 品牌的ID
			Long merchantid = (Long) map.get("merchantid");
			brand.setMerchantid(merchantid);
			/**
			 * 品牌下商品的虚拟购买次数
			 */
			int merVirtualCount = (Integer)map.get("virtualcount");
			brand.setVirtualcount(merVirtualCount);
			brand.setLogo1((String)map.get("mc_logo1"));
			brand.setLogoTitle((String)map.get("mc_logo4"));
			Float mcAvgScores = ((Number)map.get("mc_avg_scores")).floatValue();
			brand.setAvgscores(String.valueOf(mcAvgScores));
			Integer mcEvaliationCount = ((Number)map.get("mc_evaliation_count")).intValue();
			brand.setEvaluation_count(String.valueOf(mcEvaliationCount));
			Integer mcSaleCount = ((Number)map.get("mc_sale_count")).intValue();
			brand.setSalescount(String.valueOf(mcSaleCount));
			brand.setTel((String)map.get("mc_fix_tel"));
		
			Long sevenrefound = (Long) map.get("sevenrefound");
			brand.setSevenrefound(sevenrefound);
			Long overrefound = (Long) map.get("overrefound");
			brand.setOverrefound(overrefound);
			Long quality = (Long) map.get("quality");
			brand.setQuality(quality);
			String merchantname = (String) map.get("merchantname");
			brand.setMerchantname(merchantname);
			String merchantintroduction = (String) map
					.get("merchantintroduction");
			brand.setMerchantintroduction(merchantintroduction);
			String merchantdesc = (String) map.get("merchantdesc");
			brand.setMerchantdesc(merchantdesc);
		
		}
		
		return brand;
	}

	@Override
	public List<MMerchant> getBranchIdByParentId(long brandId) {
		String sql = "SELECT * FROM beiker_merchant WHERE parentid = ?";
		MMerchant branch = null;
		List<MMerchant> branchList = null;
		log.info("getBranchIdByParentId : " + sql +" parentid = "+ brandId);
		try {
			branchList = getSimpleJdbcTemplate().query(sql.toString(),
					new RowMapperImpl(), brandId);
			
			if(branchList == null || branchList.size() == 0)
			{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return branchList;
	}
	
	protected class RowMapperImpl implements ParameterizedRowMapper<MMerchant> {
		public MMerchant mapRow(ResultSet rs, int rowNum) throws SQLException {
			MMerchant merchant = new MMerchant();
			merchant.setMerchantid(rs.getLong("merchantid"));
			merchant.setAddr(rs.getString("addr"));
			merchant.setMerchantname(rs.getString("merchantname"));
			merchant.setTel(rs.getString("tel"));
			merchant.setParentId(rs.getLong("parentId"));
			merchant.setLatitude(rs.getString("latitude"));
			merchant.setSevenrefound(rs.getLong("sevenrefound"));
			merchant.setOverrefound(rs.getLong("overrefound"));
			merchant.setQuality(rs.getLong("quality"));
			merchant.setMerchantintroduction(rs.getString("merchantintroduction"));
			merchant.setBuinesstime(rs.getString("buinesstime"));
			merchant.setDisplayname(rs.getString("displayname"));
			merchant.setMerchantdesc(rs.getString("merchantdesc"));
			merchant.setAreaid(rs.getLong("areaid"));
			merchant.setCity(rs.getString("city"));
			merchant.setVirtualcount(rs.getLong("virtualcount"));
			return merchant;
		}
	}

	public MMerchantProfileType getMerchantAvgEvationScoresByMerchantId(Long merchantId){
		String sql = "select id,mc_avg_scores from beiker_merchant_profile b where b.merchantid=? ";
		List list = this.getJdbcTemplate().queryForList(sql,
				new Object[] { merchantId });
		if (list == null || list.size() == 0)
			return null;
		MMerchantProfileType pt = new MMerchantProfileType();
		for (Object object : list) {
			Map map = (Map) object;
			Float mcAvgScores = ((Number)map.get("mc_avg_scores")).floatValue();
			Long id = ((Number)map.get("id")).longValue();
			pt.setId(id);
			pt.setPropertyvalue(String.valueOf(mcAvgScores));
		}
		return pt;
	}
	
	public MMerchantProfileType getMerchantLogoByMerchantId(Long merchantId){
		String sql = "select id,mc_logo1 from beiker_merchant_profile b where b.merchantid=? ";
		List list = this.getJdbcTemplate().queryForList(sql,
				new Object[] { merchantId });
		if (list == null || list.size() == 0)
			return null;
		MMerchantProfileType pt = new MMerchantProfileType();
		for (Object object : list) {
			Map map = (Map) object;
			String pvalue = (String) map.get("mc_logo1");
			Long id = (Long) map.get("id");
			pt.setId(id);
			pt.setPropertyvalue(pvalue);
		}
		return pt;
	}
	
	//该方法目前已废弃
	public MMerchantProfileType getMerchantProfileTypeByMerchantId(
			Long merchantId, String propertyname) {
		String sql = "select * from beiker_merchantprofile b where b.merchantid=? and b.propertyname=?";
		List list = this.getJdbcTemplate().queryForList(sql,
				new Object[] { merchantId, propertyname });
		if (list == null || list.size() == 0)
			return null;
		MMerchantProfileType pt = new MMerchantProfileType();
		for (Object object : list) {
			Map map = (Map) object;
			String pname = (String) map.get("propertyname");
			String pvalue = (String) map.get("propertyvalue");
			Long id = (Long) map.get("id");
			pt.setId(id);
			pt.setPropertyname(pname);
			pt.setPropertyvalue(pvalue);
		}
		return pt;
	}

	@Override
	public List<MMerchant> getBrandListByIds(String brandIds) {
		StringBuilder sqlSb = new StringBuilder("");
		sqlSb.append("SELECT DISTINCT bm.merchantid, bm.merchantname, bm.merchantintroduction,bmp.mc_logo1 AS logo1 ")
				.append("FROM beiker_merchant bm LEFT JOIN beiker_merchant_profile bmp ON ")
				.append("bm.merchantid=bmp.merchantid WHERE bm.merchantid IN (").append(brandIds)
				.append(")  ORDER BY bm.merchantid");
		List<MMerchant> brandList = new ArrayList<MMerchant>();
		try
		{
			List list = getJdbcTemplate().queryForList(sqlSb.toString());
			if (list == null || list.size() == 0)
				return null;
			for (int i = 0; i < list.size(); i++) {
				MMerchant brand = new MMerchant();
				Map map = (Map) list.get(i);
				// 品牌的ID
				Long merchantid = (Long) map.get("merchantid");
				String brandName = (String) map.get("bm.merchantname");
				
				String merchantintroduction = (String) map.get("merchantintroduction");
				String logo1 = (String) map.get("logo1");
				brand.setMerchantid(merchantid);
				brand.setMerchantname(brandName);
				brand.setMerchantintroduction(merchantintroduction);
				brand.setLogo1(logo1);
				
				brandList.add(brand);
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.info("getBrandListByIds have an Exception, sql is :　" + sqlSb);
			return null;
		}
		
		return brandList;
	}

	/*
	 * @see com.beike.wap.dao.MMerchantDao#getBrandByGoodId(java.lang.String)
	 */
	@Override
	public MMerchant getBrandByGoodId(String goodsId) {
		
		Long.parseLong(goodsId);
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT m.merchantid id FROM beiker_merchant m ");
		sql.append("LEFT JOIN beiker_goods_merchant gm  ON gm.merchantid = m.merchantid ");
		sql.append("WHERE parentId = 0 AND gm.goodsid = ? ");
		int[] types = new int[]{Types.INTEGER};
		Object[] params = new Object[]{goodsId};
		MMerchant merchant = (MMerchant)this.getJdbcTemplate().queryForObject(sql.toString(),params, types, new ParameterizedRowMapper<MMerchant>() {
			@Override
			public MMerchant mapRow(ResultSet rs, int i)
					throws SQLException {
				MMerchant mMerchant = new MMerchant();
				mMerchant.setMerchantid(rs.getLong("id"));
				return mMerchant;
			}
		});
		return merchant;
	}
}
