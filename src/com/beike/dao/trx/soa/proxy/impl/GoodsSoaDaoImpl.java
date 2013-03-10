package com.beike.dao.trx.soa.proxy.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.soa.proxy.GoodsSoaDao;

/**
 * @ClassName: GoodsSoaDaoImpl
 * @Description: TODO
 * @author yurenli
 * @date 2012-3-24 下午04:54:29
 * @version V1.0
 */
@Repository("goodsSoaDao")
public class GoodsSoaDaoImpl extends GenericDaoImpl<Object, Long> implements
		GoodsSoaDao {
	//FIXME

	/**
	 * 
	 * 补充说明：by zx.liu
	 * 
	 * 
	 * 此处的sql 语句添加了一个属性 virtual_count（商品的虚拟购买数量）
	 * 
	 */
	@Override
	public Map<String, Object> getmaxCountById(Long id) {
		String sql = "select maxcount,isavaliable from beiker_goods where goodsId=? ";

		List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
				.queryForList(sql, id);

		if (list != null && list.size() > 0) {

			return list.get(0);

		}

		return null;

	}

	@Override
	public List<Map<String, Object>> getMaxcountByIdlist(String id) {
		String sql = "select maxcount,isavaliable from beiker_goods where goodsId in ( "
				+ id + ")";

		List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
				.queryForList(sql);

		return list;

	}

	@Override
	public Map<String, Object> getGoodsProfile(Long goodsId) {
		String sql = "select bgp.goodsid as goodsId,bgp.sales_count as salesCount,bgp.detailpageurl as detailpageurl from beiker_goods_profile bgp where bgp.goodsid=?";
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
				.queryForList(sql, goodsId);

		if (list == null || list.size() == 0) {
			return null;
		}

		Map<String, Object> map = list.get(0);

		return map;

	}

	@Override
	public List<Map<String, Object>> getGoodsProfileByGoodsid(String goodsId) {
		String sql = "select bgp.goodsid as goodsId,bgp.sales_count as salesCount,bgp.detailpageurl as detailpageurl from beiker_goods_profile bgp where bgp.goodsid in"
				+ "(" + goodsId + ")";
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
				.queryForList(sql);

		return list;

	}

	/**
	 * 查询商品个人限购信息
	 * 
	 * @param goodsId
	 *            add by wenhua.cheng
	 * 
	 * @return
	 */
	@Override
	public Map<String, Object> getSingleCount(Long goodsId) {

		String sql = "select goods_single_count as singleCount from beiker_goods where  goodsid=?";

		List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sql, goodsId);

		if (list != null && list.size() > 0) {

			return list.get(0);

		}

		return null;

	}

	/**
	 * 根据商品表主键查询商品简称
	 * 
	 * @param goodsId
	 * @return
	 */

	@Override
	public Map<String, Object> findGoodsTitleById(Long goodsId) {

		String sql = "select goodsId,goods_title as goodsTitle,is_scheduled as isScheduled  from beiker_goods where goodsId=?";

		List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
				.queryForList(sql, goodsId);

		if (list != null && list.size() > 0) {

			return list.get(0);

		}

		return null;

	}

	
	/**
	 * 查询品牌
	 * 
	 * @param goodsIdList
	 * @return
	 */
	public Map<String, Object> findMerchantName(List<Long> goodsIdList) {
		StringBuilder sb = new StringBuilder();
		if (goodsIdList != null && goodsIdList.size() > 0) {

			for (Long goodsId : goodsIdList) {
				sb.append(goodsId);
				sb.append(",");

			}
			sb.deleteCharAt(sb.length() - 1);

			StringBuilder sqlSb = new StringBuilder();

			sqlSb
					.append("select m.merchantid as merchantId,m.merchantname as merchantName  from beiker_merchant m left join beiker_goods_merchant mg on mg.merchantid=m.merchantid where  m.parentid='0'  and mg.goodsid in (");

			sqlSb.append(sb.toString());
			sqlSb.append(")");
			List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
					.queryForList(sqlSb.toString());

			if (list != null && list.size() > 0) {

				return list.get(0);

			}
		}

		return null;

	}
	
	
	/**
	 * 查询品类
	 * 
	 * @param goodsIdList
	 * @return
	 */
	public Map<String, Object> findTagByIdName(Long goodsId) {
		if (goodsId != null) {
			StringBuilder sqlSb = new StringBuilder();

			sqlSb.append("select tp.id as tagId,tp.tag_name as tagName from beiker_tag_property tp left join beiker_catlog_good cg on tp.id=cg.tagextid where cg.goodid=");
			sqlSb.append(goodsId);
			List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
					.queryForList(sqlSb.toString());

			if (list != null && list.size() > 0) {

				return list.get(0);

			}
		}

		return null;

	}
	
	/**
	 * 查询分店名称
	 * 
	 * @param subGuestId
	 * @return
	 */
	public Map<String, Object> getMerchantById(Long subGuestId) {
		if (subGuestId != null ) {


			StringBuilder sqlSb = new StringBuilder();

			sqlSb
					.append("select m.merchantid as merchantId,m.merchantname as merchantName,m.parentId  as parentId from beiker_merchant m WHERE m.merchantid ="+subGuestId);

			List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
					.queryForList(sqlSb.toString());

			if (list != null && list.size() > 0) {

				return list.get(0);

			}
		}

		return null;

	}

	/**
	 * 根据商品表主键查询logo4
	 * 
	 * @param goodsId
	 * @return
	 */

	@Override
	public Map<String, Object> findGoodsPicUrlById(Long goodsId) {

		String sql = "select logo3 as logo   from beiker_goods where goodsId=?";

		List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
				.queryForList(sql, goodsId);

		if (list != null && list.size() > 0) {

			return list.get(0);

		}

		return null;

	}

	/**
	 * 根据goodsId批量查询商品详情
	 * 
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> findBatchGoodsInfoByIdStr(String goodsIdStr) {
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append("select goodsid as goodsId,guest_id as guestId,goodsName,sourcePrice,payPrice,rebatePrice,dividePrice,order_lose_abs_date as orderLoseAbsDate,");
		sqlSb.append("order_lose_date as orderLoseDate,isRefund  as isRefund,send_rules as isSendMerVou,isadvance as isAdvance,endTime as  endTime,is_menu as isMenu  from beiker_goods ");
		sqlSb.append(" where goodsId in (");
		sqlSb.append(goodsIdStr);
		sqlSb.append(")");

		List<Map<String, Object>> list = this.getSimpleJdbcTemplate()
				.queryForList(sqlSb.toString());

		if (list != null && list.size() > 0) {

			return list;

		}
		return null;

	}
	
	/**
	 * 商品销售量
	 */
	@Override
	public void updateSalesCount(Long goodId, String salesCountStr) {
		if (goodId == null || salesCountStr == null || "".equals(salesCountStr)) {
			throw new IllegalArgumentException(
					"goodId and salesCountStr not null");
		}
		String upSql = "update beiker_goods_profile set sales_count=sales_count+"
				+ salesCountStr + " where goodsid=" + goodId;
		int rows = this.getSimpleJdbcTemplate().update(upSql);
		if (rows == 0) {
			String insertSql = "insert into beiker_goods_profile(sales_count,goodsid) values("
					+ salesCountStr + "," + goodId + ")";
			this.getSimpleJdbcTemplate().update(insertSql);
		}
	}


	/**
	 *根据主键查询秒杀表信息
	 * @param id
	 * @return
	 */
	@Override
	public Map<String,Object> findMiaoSha(Long id) {
		String querySql = "SELECT id id,goods_id goodsId,m_title mTitle,m_short_title mShortTitle,m_pay_price mPayPrice,m_maxcount mMaxCount,m_settle_price mSetlePrice,m_single_count mSingleCount,m_start_time mStartTime,m_end_time mEndTime,m_show_start_time mShowStartTime,m_show_end_time mShowEndTime,m_banner mBanner,is_used isUsed,is_need_virtual isNeedVirtual,m_virtual_count mVirtualCount,m_sale_count mSaleCount,createucid createUcId,createtime createTime,updateucid updateUcId,updatetime updateTime FROM beiker_miaosha WHERE id = ?";
		return this.getSimpleJdbcTemplate().queryForMap(querySql,id);
	}

	/**
	 * 根据主键查询秒杀表实际销量(带悲观锁)
	 * @param id
	 * @return
	 */
	@Override
	public int findMiaoShaSaleCountForUpdate(Long id) {
		String querySql = "SELECT m_sale_count FROM beiker_miaosha WHERE id = ? FOR UPDATE";
		return this.getSimpleJdbcTemplate().queryForInt(querySql,id);
	}

	/**
	 * 根据主键更新秒杀表实际销量
	 * @param saleCount
	 * @param id
	 * @return
	 */
	@Override
	public int updateMiaoShaSaleCountById(int saleCount, Long id) {
		String updateSql = "UPDATE beiker_miaosha SET m_sale_count=? WHERE id=?";
		return this.getSimpleJdbcTemplate().update(updateSql, saleCount,id);
	}

	@Override
	public Map<String, Object> findBytrxgoodsId(Long trxgoodsId) {
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append("SELECT bsaf.id,bsaf.message,bsaf.status,bsaf.createtime,bsaf.createucid FROM beiker_scheduled_application_form bsaf ");
		sqlSb.append("WHERE bsaf.trx_id =? ");
		sqlSb.append(" ORDER BY bsaf.id desc limit 1");
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sqlSb.toString(), trxgoodsId);
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	/** 
	 * @description:获取商品的品牌Id
	 * @param goodsId
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getGoodsBrandId(String goodsId){
		StringBuilder sql = new StringBuilder("SELECT bgm.goodsid,bgm.merchantid");
		sql.append(" FROM beiker_merchant bm")
			.append(" LEFT JOIN beiker_goods_merchant bgm ON bgm.merchantid = bm.merchantid")
			.append(" WHERE bgm.goodsid IN (").append(goodsId).append(") AND bm.parentId = 0")
			.append(" ORDER BY FIND_IN_SET(bgm.goodsid,'").append(goodsId).append("')");
		return this.getJdbcTemplate().queryForList(sql.toString());
	}
	
	/**
	 * 查找所有父种类（商品类别）
	 * @return java.util.List<Tag>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String,String> findParentTags(){
		String sql = "SELECT id,tag_name FROM beiker_tag_property WHERE parentid = 0 ";
		List<Map<String,Object>> tagList = getJdbcTemplate().queryForList(sql);
		if(null != tagList && tagList.size()>0){
			Map<String,String> map = new HashMap<String,String>();
			for(Map<String,Object> tagMap : tagList){
				map.put(String.valueOf(tagMap.get("id")), (String)tagMap.get("tag_name"));
			}
			return map;
		}
		return null;
	}

	/**
	 * 根据goodsId获得商品所属种类（品类catlog_goods表的tagids)
	 * @param goodsId
	 * @return
	 */
	public List<Map<String,Object>> getCatalogGoods(Long goodsId){
		String sql = "SELECT goodid,regionid,tagid,regionextid,tagextid num FROM beiker_catlog_good  WHERE goodid=?";
		List<Map<String,Object>> list = this.getSimpleJdbcTemplate().queryForList(sql, goodsId);
		return list;
	}

	@Override
	public Map<String, Object> getFilmShowByShowIndex(Long showIndex) {
		String querySql ="SELECT id, cinema_id, hall_id, hall_name, show_index, show_time, sale_end_time, film_id, film_name, language, status, c_price, u_price, v_price, city_id, uw_price, sp_type, sp_price, is_imax, dimensional, seat_count, is_available, upd_time FROM beiker_film_show WHERE show_index = ?";
		return this.getSimpleJdbcTemplate().queryForMap(querySql, new Object[]{showIndex});
	}

	@Override
	public Map<String, Object> getCinemaByCinemaId(Long Cinemaid) {
		String querySql ="SELECT id, cinema_id, type, name, city_id, dist_id, hall_count, address, bus_line, des, photo, url, tel, is_phone_pay, special_des, coord, is_cooperation, update_time, is_available FROM beiker_cinema WHERE cinema_id = ?";
		return this.getSimpleJdbcTemplate().queryForMap(querySql, new Object[]{Cinemaid});
	}

	@Override
	public Map<String, Object> getCinemaIdByTrxGoodsId(Long trxGoodsId) {
		String querySql = "select a.cinema_id  from beiker_film_show a  LEFT JOIN beiker_film_goods_order b ON a.id=b.film_show_id where b.trx_goods_id= ?";
		return this.getSimpleJdbcTemplate().queryForMap(querySql, new Object[]{trxGoodsId});
	}
	
	/**
	 * 根据大桥网票网影院ID查询千品平台影院ID
	 * @param cinemaId
	 * @return
	 */
	@Override
	public Long queryQianpinCinemaByWpId(Long cinemaId) {
		String sql = "select ci.cinema_id from beiker_cinema_info ci left join beiker_wpw_cinema_map bwcm on ci.cinema_id = bwcm.cinema_id left join beiker_cinema c on bwcm.cinema_wpw_id = c.cinema_id where c.cinema_id = " + cinemaId + " limit 1";
		
		Long qianpin_cinemaId=this.getJdbcTemplate().queryForLong(sql);
		
		return qianpin_cinemaId;
	}
	
	
}
