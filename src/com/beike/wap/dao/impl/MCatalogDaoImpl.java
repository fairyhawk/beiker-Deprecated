package com.beike.wap.dao.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.wap.dao.MCatalogDao;
import com.beike.wap.entity.MCouponCatlog;
import com.beike.wap.entity.MGoodsCatlog;
import com.beike.wap.entity.MerchantCatlog;

@Repository("mCatalogDao")
public class MCatalogDaoImpl extends GenericDaoImpl<MCouponCatlog, Long> implements MCatalogDao{

	private static Log log = LogFactory.getLog(MCatalogDaoImpl.class);
	
	@Override
	public Long getLastInsertId() {
		return null;
	}

	@Override
	public MCouponCatlog findCouponCatalogById(long couponId) {
		String sql="select bcg.regionextid, bcg.area_id, brp.id as tagid,brp.tag_name as tagname from beiker_tag_property brp left join  beiker_catlog_coupon  bcg on bcg.tagid=brp.id where bcg.couponid=?";
		log.info(sql + "\ncouponId : " + couponId);
		List list = getJdbcTemplate().queryForList(sql, new Object[]{couponId});
		if(list==null||list.size()==0)return null;
		MCouponCatlog couponCatlog=new MCouponCatlog();
		for(int i=0;i<list.size();i++){
			Map map=(Map) list.get(i);
			Long tagId=(Long) map.get("tagid");
			String tagname=(String) map.get("tagname");
			Long regionextId = (Long) map.get("regionextid");
			Long areaId = (Long)map.get("area_id");
			
			couponCatlog.setTagid(tagId);
			couponCatlog.setTagName(tagname);
			couponCatlog.setRegionextid(regionextId);
			couponCatlog.setCityid(areaId);
		}
		return couponCatlog;
	}

	@Override
	public int getCouponCatalogSum(MCouponCatlog couponCatlog) throws Exception{
		int sum = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(DISTINCT couponid) FROM beiker_catlog_coupon WHERE enddate >= DATE_FORMAT(CURDATE(),'%Y%m%d') ");
		sql.append("AND createdate <= DATE_FORMAT(CURDATE(),'%Y%m%d') AND isavaliable = '1' AND area_id = ? ");
		if(null!=couponCatlog.getRegionid()&&couponCatlog.getRegionid()>0){
			sql.append(" AND regionid =  ").append(couponCatlog.getRegionid());
		}
		if(null!=couponCatlog.getRegionextid()&&couponCatlog.getRegionextid()>0){
			sql.append(" AND regionextid =  ").append(couponCatlog.getRegionextid());
		}
		if(null!=couponCatlog.getTagid()&&couponCatlog.getTagid()>0){
			sql.append(" AND tagid = ").append(couponCatlog.getTagid());
		}
		if(null!=couponCatlog.getTagextid()&&couponCatlog.getTagextid()>0){
			sql.append(" AND tagextid = ").append(couponCatlog.getTagextid());
		}
		int[] types = new int[]{Types.INTEGER};
		log.info(sql.toString() + "\narea_id : " + couponCatlog.getCityid());
		Object[] params = new Object[]{couponCatlog.getCityid()};
		sum = this.getJdbcTemplate().queryForInt(sql.toString(), params, types);
		return sum;
	}

	@Override
	public List<Long> queryCouponId(int page,MCouponCatlog couponCatLog) throws Exception {
		List tempList = null;
		List<Long> goodsIdList = new ArrayList<Long>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT couponid FROM beiker_catlog_coupon WHERE enddate >= DATE_FORMAT(CURDATE(),'%Y%m%d') ");
		sql.append("AND createdate <= DATE_FORMAT(CURDATE(),'%Y%m%d') AND isavaliable = '1' AND area_id = ? ");
		if(null!=couponCatLog.getRegionid()&&couponCatLog.getRegionid()>0){
			sql.append(" AND regionid =  ").append(couponCatLog.getRegionid());
		}
		if(null!=couponCatLog.getRegionextid()&&couponCatLog.getRegionextid()>0){
			sql.append(" AND regionextid =  ").append(couponCatLog.getRegionextid());
		}
		if(null!=couponCatLog.getTagid()&&couponCatLog.getTagid()>0){
			sql.append(" AND tagid = ").append(couponCatLog.getTagid());
		}
		if(null!=couponCatLog.getTagextid()&&couponCatLog.getTagextid()>0){
			sql.append(" AND tagextid = ").append(couponCatLog.getTagextid());
		}
		sql.append(" LIMIT ?,5 ");
		log.info(sql.toString() + "\narea_id : " + couponCatLog.getCityid() + "\nlimit " + page + ",5");
		int[] types = new int[]{Types.INTEGER,Types.INTEGER};
		Object[] params = new Object[]{couponCatLog.getCityid(),page};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(), params, types);
		if(null!=tempList&&tempList.size()>0){
			for(int i=0;i<tempList.size();i++){
				Map result = (Map)tempList.get(i);
				Long goodsId = ((Number)result.get("couponid")).longValue();
				if(null!=goodsId){
					goodsIdList.add(goodsId);
				}
			}
		}
		return goodsIdList;
	}

	@Override
	public int getBrandCatalogSum(MerchantCatlog brandCatlog) throws Exception {
		int sum = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(DISTINCT(brandid)) FROM beiker_catlog_good WHERE enddate >= DATE_FORMAT(CURDATE(),'%Y%m%d') ");
		sql.append("AND createdate <= DATE_FORMAT(CURDATE(),'%Y%m%d') AND isavaliable = '1' AND area_id = ? ");
		if(null!=brandCatlog.getRegionid()&&brandCatlog.getRegionid()>0){
			sql.append(" AND regionid =  ").append(brandCatlog.getRegionid());
		}
		if(null!=brandCatlog.getRegionextid()&&brandCatlog.getRegionextid()>0){
			sql.append(" AND regionextid =  ").append(brandCatlog.getRegionextid());
		}
		if(null!=brandCatlog.getTagid()&&brandCatlog.getTagid()>0){
			sql.append(" AND tagid = ").append(brandCatlog.getTagid());
		}
		if(null!=brandCatlog.getTagextid()&&brandCatlog.getTagextid()>0){
			sql.append(" AND tagextid = ").append(brandCatlog.getTagextid());
		}
		int[] types = new int[]{Types.INTEGER};
		log.info(sql.toString() + "\narea_id : " + brandCatlog.getCityid());
		Object[] params = new Object[]{brandCatlog.getCityid()};
		sum = this.getJdbcTemplate().queryForInt(sql.toString(), params, types);
		return sum;
	}

	@Override
	public List<Long> queryBrandId(int page, MerchantCatlog brandCatLog)
			throws Exception {
		

		MerchantCatlog merchantCatlog = (MerchantCatlog) brandCatLog;
		StringBuilder idbuilder = new StringBuilder();
		String ids = "";
		Long cityid = merchantCatlog.getCityid();
		String whereCourse = merchantCatlog.getSearchCourse();
		String query_id = "select brandid,goodid from beiker_catlog_good ";
		if (!merchantCatlog.isNull()) {
			query_id += " where isavaliable='1' and area_id=" + cityid
					+ " and " + whereCourse;
		} else {
			query_id += " where isavaliable='1' and area_id=" + cityid + " "
					+ whereCourse;
		}
		logger.info("有效商品SQL。。。。。" + query_id);
		List goodList = this.getJdbcTemplate().queryForList(query_id);
		if (goodList == null || goodList.size() == 0) {
			return null;
		}

		for (Object object : goodList) {
			Map map = (Map) object;
			Long brandid = (Long) map.get("brandid");
			String brandSQL = "select bgm.merchantid,bgm.goodsid from beiker_goods_merchant bgm where bgm.merchantid = "
					+ brandid;
			List rs = this.getJdbcTemplate().queryForList(brandSQL);
			if (rs != null && rs.size() > 0) {
				if (rs.size() == 1) {
					Map ma = (Map) rs.get(0);
					Long godid = (Long) ma.get("goodsid");
					String query_coupon = "select bg.couponcash  from beiker_goods bg where bg.isavaliable = '1' and bg.couponcash = '0' and bg.goodsid ="
							+ godid;
					List co = this.getJdbcTemplate().queryForList(query_coupon);
					if (co != null && co.size() > 0) {
						idbuilder.append(godid).append(",");
					}
				} else {
					StringBuilder stt = new StringBuilder();
					for (int i = 0; i < rs.size(); i++) {
						Map mm = (Map) rs.get(i);
						Long gid = (Long) mm.get("goodsid");
						stt.append(gid).append(",");
					}
					String gods = stt.substring(0, stt.lastIndexOf(","));
					String query_coupon = "select bg.couponcash , bg.goodsid from beiker_goods bg where  bg.isavaliable = '1' and bg.couponcash = '0' and bg.goodsid in ("
							+ gods + ")";
					List li = this.getJdbcTemplate().queryForList(query_coupon);
					if (li.size() > 0 && li != null) {
						idbuilder.append(gods).append(","); // 将该品牌的所有商品ID
															// 加到商品列表中
					}
				}
			}
		}

		ids = idbuilder.substring(0, idbuilder.lastIndexOf(","));

		String query_brand = "select distinct(bcg.brandid)  from beiker_catlog_good bcg left join beiker_goods bg on bcg.goodid =  bg.goodsid "
				+ "left join beiker_goods_merchant bgm on bg.goodsid = bgm.goodsid left join beiker_merchant_profile bmp on bmp.merchantid = bgm.merchantid";
		if (!merchantCatlog.isNull()) {
			query_brand += " where  bg.goodsid in("
					+ ids
					+ ")  and area_id="
					+ cityid + " and " + whereCourse;
		} else {
			query_brand += " where bg.goodsid in("
					+ ids
					+ ")  and area_id="
					+ cityid;
		}
		query_brand += " ORDER BY bmp.mc_sale_count DESC";
		query_brand += " limit " + page + "," + 8;
		logger.info("搜索地域、属性的商品 query_brand: " + query_brand);
		List brandList = this.getJdbcTemplate().queryForList(query_brand);
		if (brandList == null || brandList.size() == 0)
			return null;

		List<Long> goodIdList = new LinkedList<Long>();
		for (Object object : brandList) {
			Map map = (Map) object;
			Long goodId = (Long) map.get("brandid");
			goodIdList.add(goodId);
		}
		return goodIdList;
	
		
//		List tempList = null;
//		List<Long> goodsIdList = new ArrayList<Long>();
//		StringBuilder sql = new StringBuilder();
//		sql.append("SELECT DISTINCT brandid FROM beiker_catlog_good WHERE enddate >= DATE_FORMAT(CURDATE(),'%Y%m%d') ");
//		sql.append("AND createdate <= DATE_FORMAT(CURDATE(),'%Y%m%d') AND isavaliable = '1' AND area_id = ? ");
//		if(null!=brandCatLog.getRegionid()&&brandCatLog.getRegionid()>0){
//			sql.append(" AND regionid =  ").append(brandCatLog.getRegionid());
//		}
//		if(null!=brandCatLog.getRegionextid()&&brandCatLog.getRegionextid()>0){
//			sql.append(" AND regionextid =  ").append(brandCatLog.getRegionextid());
//		}
//		if(null!=brandCatLog.getTagid()&&brandCatLog.getTagid()>0){
//			sql.append(" AND tagid = ").append(brandCatLog.getTagid());
//		}
//		if(null!=brandCatLog.getTagextid()&&brandCatLog.getTagextid()>0){
//			sql.append(" AND tagextid = ").append(brandCatLog.getTagextid());
//		}
//		sql.append(" LIMIT ?,8 ");
//		log.info(sql.toString() + "\narea_id : " + brandCatLog.getCityid() + "\nlimit " + page + ",8");
//		int[] types = new int[]{Types.INTEGER,Types.INTEGER};
//		Object[] params = new Object[]{brandCatLog.getCityid(),page};
//		tempList = this.getJdbcTemplate().queryForList(sql.toString(), params, types);
//		if(null!=tempList&&tempList.size()>0){
//			for(int i=0;i<tempList.size();i++){
//				Map result = (Map)tempList.get(i);
//				Long goodsId = ((Number)result.get("brandid")).longValue();
//				if(null!=goodsId){
//					goodsIdList.add(goodsId);
//				}
//			}
//		}
//		return goodsIdList;
	}
	
}
