package com.beike.dao.impl.seo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.seo.SeoDao;
import com.beike.entity.seo.MerchantTag;
import com.beike.entity.seo.GoodsTag;
import com.beike.entity.seo.CouponTag;

@SuppressWarnings("unchecked")
@Repository("seoDao")
public class SeoDaoImpl extends GenericDaoImpl implements SeoDao {

	/**
	 * 根据商品标签来获取商品的ID
	 */
	@Override
	public Long findGoodsId(String tagEnname)
	{
		String sql = "SELECT goodsid FROM beiker_seo_goods WHERE tag_enname=? ";		
		/**
		 * 防止关键词的重复而导致的异常
		 */
		List<Map<String, Object>> goodsId = this.getSimpleJdbcTemplate().queryForList(sql, tagEnname);		
		if(null == goodsId  || goodsId.size()==0 ){
			return null;
		} 		
		String tempGoodsId = goodsId.get(0).get("goodsid").toString();		
		return Long.parseLong(tempGoodsId);
	
	}	


	/**
	 * 根据优惠券标签获取优惠券的ID
	 */
	@Override
	public Long findCouponId(String tagEnname)
	{
		String sql = "SELECT couponid FROM beiker_seo_coupon WHERE tag_enname=? ";		
		/**
		 * 防止关键词的重复而导致的异常
		 */
		List<Map<String, Object>> goodsId = this.getSimpleJdbcTemplate().queryForList(sql, tagEnname);		
		if(null == goodsId  || goodsId.size()==0 ){
			return null;
		} 		
		String tempCouponId = goodsId.get(0).get("couponid").toString();		
		return Long.parseLong(tempCouponId);
	
	}	
	
	
	/**
	 * 根据品牌标签来获取品牌的ID
	 */
	@Override
	public Long findMerchantId(String tagEnname)
	{
		String sql = "SELECT merchantid FROM beiker_seo_merchant WHERE tag_enname=? ";		
		/**
		 * 防止关键词的重复而导致的异常
		 */
		List<Map<String, Object>> goodsId = this.getSimpleJdbcTemplate().queryForList(sql, tagEnname);		
		if(null == goodsId  || goodsId.size()==0 ){
			return null;
		} 		
		String tempMerchantId = goodsId.get(0).get("merchantid").toString();		
		return Long.parseLong(tempMerchantId);
	
	}	

	
	@Override
	public String findTagId(String tagEnname)
	{
		String sql = "SELECT id FROM beiker_tag_property WHERE tag_enname=? ";	
		/**
		 * 防止关键词的重复而导致的异常
		 */
		List<Map<String, Object>> tagId = this.getSimpleJdbcTemplate().queryForList(sql, tagEnname);		
		if(null == tagId  || tagId.size()==0 ){
			return null;
		} 
		return tagId.get(0).get("id").toString();
	}

	
	@Override
	public String findRegionId(String tagEnname)
	{
		String sql = "SELECT id FROM beiker_region_property WHERE region_enname=? ";	
		/**
		 * 防止关键词的重复而导致的异常
		 */
		List<Map<String, Object>> regionId = this.getSimpleJdbcTemplate().queryForList(sql, tagEnname);		
		if(null == regionId  || regionId.size()==0 ){
			return null;
		} 
		return regionId.get(0).get("id").toString();
	}	
	
	
	
	/**
	 * 根据商品标签来获取商品的Tag信息
	 * @param tagEnname
	 * @return 
	 */	
	@Override
	public GoodsTag findGoodsTag(String tagEnname){
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id, tag_enname, addtime, goodsid FROM beiker_seo_goods ");
		sql.append(" WHERE tag_enname=? ");		
		/**
		 * 防止关键词的重复而导致的异常
		 */
		List<GoodsTag> list = this.getSimpleJdbcTemplate().query(sql.toString(), new RowMapGoodsImpl(), tagEnname);
		if(null ==list || list.size()==0){
			return null;
		}		
		return list.get(0);
	}

	/**
	 *  实现ParameterizedRowMapper接口
	 */
	public class RowMapGoodsImpl implements ParameterizedRowMapper<GoodsTag> {
		public GoodsTag mapRow(ResultSet rs, int num) throws SQLException {
			GoodsTag goodsTag = new GoodsTag();
			goodsTag.setId(rs.getLong("id"));			
			goodsTag.setTagEnname(rs.getString("tag_enname"));
			goodsTag.setAddTime(rs.getTimestamp("addtime"));
			goodsTag.setGoodsId(rs.getLong("goodsid"));			
			return goodsTag;
		}
	}

	
	/**
	 * 根据优惠券标签来获取优惠券的Tag信息
	 * @param tagEnname
	 * @return
	 */	
	@Override
	public CouponTag findCouponTag(String tagEnname){
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id, tag_enname, addtime, couponid FROM beiker_seo_coupon ");
		sql.append(" WHERE tag_enname=? ");		
		/**
		 * 防止关键词的重复而导致的异常
		 */
		List<CouponTag> list = this.getSimpleJdbcTemplate().query(sql.toString(), new RowMapCouponImpl(), tagEnname);
		if(null ==list || list.size()==0){
			return null;
		}		
		return list.get(0);
	}

	/**
	 *  实现ParameterizedRowMapper接口
	 */
	public class RowMapCouponImpl implements ParameterizedRowMapper<CouponTag> {
		public CouponTag mapRow(ResultSet rs, int num) throws SQLException {
			CouponTag couponTag = new CouponTag();
			couponTag.setId(rs.getLong("id"));			
			couponTag.setTagEnname(rs.getString("tag_enname"));
			couponTag.setAddTime(rs.getTimestamp("addtime"));
			couponTag.setCouponId(rs.getLong("couponid"));			
			return couponTag;
		}
	}	

	
	/**
	 * 根据品牌标签来获取品牌的Tag信息 
	 * @param tagEnname
	 * @return
	 */	
	@Override
	public MerchantTag findMerchantTag(String tagEnname){
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id, tag_enname, addtime, merchantid FROM beiker_seo_merchant ");
		sql.append(" WHERE tag_enname=? ");		
		/**
		 * 防止关键词的重复而导致的异常
		 */
		List<MerchantTag> list = this.getSimpleJdbcTemplate().query(sql.toString(), new RowMapMerchantImpl(), tagEnname);
		if(null ==list || list.size()==0){
			return null;
		}		
		return list.get(0);
	}

	/**
	 *  实现ParameterizedRowMapper接口
	 */
	public class RowMapMerchantImpl implements ParameterizedRowMapper<MerchantTag> {
		public MerchantTag mapRow(ResultSet rs, int num) throws SQLException {
			MerchantTag merchantTag = new MerchantTag();
			merchantTag.setId(rs.getLong("id"));			
			merchantTag.setTagEnname(rs.getString("tag_enname"));
			merchantTag.setAddTime(rs.getTimestamp("addtime"));
			merchantTag.setMerchantId(rs.getLong("merchantid"));			
			return merchantTag;
		}
	}


	@Override
	public Map<String,Object> getTagENName(String tagId) {
		String sql = "select * from beiker_tag_property where id="+tagId;
		List<Map<String,Object>> list = getSimpleJdbcTemplate().queryForList(sql);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}


	@Override
	public Map<String,Object> getRegionENName(String tagId) {
		String sql = "SELECT bgp.id,bgp.region_name,bgp.parentid,bgp.areaid,bgp.region_enname FROM beiker_region_property bgp WHERE bgp.id="+tagId;
		List<Map<String,Object>> list = getSimpleJdbcTemplate().queryForList(sql);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}


	@Override
	public String findRegionId(String tagEnname, Long cityid, Long parentId) {
		String sql = "SELECT id FROM beiker_region_property WHERE region_enname=? and areaid=? and parentid=? ";	
		/**
		 * 防止关键词的重复而导致的异常
		 */
		List<Map<String, Object>> regionId = this.getSimpleJdbcTemplate().queryForList(sql, tagEnname,cityid,parentId);		
		if(null == regionId  || regionId.size()==0 ){
			return null;
		} 
		return regionId.get(0).get("id").toString();
	}	

	@Override
	public List<Map> getFeatureTag(String tag_en_name, Long parentid) {
		String sql = "SELECT tag_enname,id FROM beiker_biaoqian_property bbp WHERE bbp.tag_enname=? AND bbp.parentid=?";
		return getJdbcTemplate().queryForList(sql,new Object[]{tag_en_name,parentid});
	}


}
