package com.beike.dao.qianpincard.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.qianpincard.QianPinCardDao;
import com.beike.entity.goods.Goods;
import com.beike.util.StringUtils;

/**  
* @Title:  千品卡Dao实现
* @Package com.beike.dao.qianpincard.impl
* @Description: TODO
* @author wenjie.mai  
* @date Feb 29, 2012 10:28:56 AM
* @version V1.0  
*/
@Repository("qianPinCardDao")
public class QianPinCardDaoImpl extends GenericDaoImpl<Goods,Long> implements QianPinCardDao {
	/* (non-Javadoc)
	 * @see com.beike.dao.qianpincard.QianPinCardDao#getCardGoodsIdsByCityId(java.lang.String)
	 */
	@Override
	public List<Long> getCardGoodsIdsByCityId(String areaId) {
		StringBuilder sql = new StringBuilder();
			sql.append("SELECT DISTINCT bcg.goodid as goodsid ")
				.append("FROM beiker_catlog_good bcg ")
				.append("JOIN beiker_goods bg ON bg.goodsid=bcg.goodid ")
				.append("JOIN beiker_goods_profile bgp ON bg.goodsid=bgp.goodsid ")
				.append("WHERE bcg.area_id=? AND bcg.isavaliable=1 AND bg.iscard='1' AND bg.maxcount>bgp.sales_count ");
		List<Long> goodsidList = getJdbcTemplate().queryForList(sql.toString(),
				new Object[]{areaId}, Long.class);
		return goodsidList;
	}

	/* (non-Javadoc)
	 * @see com.beike.dao.qianpincard.QianPinCardDao#getTopSaleCardGoods(java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public List getTopSaleGoods(String beginTime, String endTime,
			int count, List<Long> lstInGoods) {
		if(lstInGoods==null || lstInGoods.size()==0){
			return new ArrayList<Long>();
		}
		//查询指定时间范围内销售量最好的N款
		StringBuilder sqlCount = new StringBuilder();
		sqlCount.append("SELECT btg.goods_id as goodsid FROM beiker_trxorder_goods btg WHERE btg.trx_status!='INIT' AND btg.create_date BETWEEN ? AND ? ")
			.append("AND btg.goods_id IN(").append(StringUtils.arrayToString(lstInGoods.toArray(), ",")).append(") ")
			.append("GROUP BY btg.goods_id ORDER BY COUNT(btg.goods_id) DESC");
		List<Long> goodsidList = null;
		if(count > 0){
			sqlCount.append(" LIMIT ?");
			goodsidList = getJdbcTemplate().queryForList(
					sqlCount.toString(),
					new Object[] { beginTime, endTime, count }, Long.class);
		}else{
			goodsidList = getJdbcTemplate().queryForList(
					sqlCount.toString(),
					new Object[] { beginTime, endTime }, Long.class);
		}

		if (goodsidList == null) {
			goodsidList = new ArrayList<Long>();
		}
		
		//指定时间范围内不足N款,则取最后上线的商品
		if(count==0 || goodsidList.size()<count){
			//排除第一步销售量最好的N款
			if(goodsidList.size()>0){
				for(Long goodsId : goodsidList){
					lstInGoods.remove(goodsId);
				}
			}
			if(count>0){
				count = count - goodsidList.size();
			}
			if(lstInGoods!=null && lstInGoods.size()>0){
				StringBuilder sqlTime = new StringBuilder();
				sqlTime.append("SELECT goods_id as goodsid FROM beiker_goods_on_end_time ")
					.append("where goods_id IN(").append(StringUtils.arrayToString(lstInGoods.toArray(), ",")).append(") ")
					.append("order by on_time desc");
				
				List<Long> lstTimeGoodsIds = null;
				if(count > 0){
					sqlTime.append(" LIMIT ?");
					lstTimeGoodsIds = getJdbcTemplate().queryForList(
							sqlTime.toString(), new Object[] { count }, Long.class);
				}else{
					lstTimeGoodsIds = getJdbcTemplate().queryForList(sqlTime.toString(),Long.class);
				}
				
				if (lstTimeGoodsIds != null) {
					goodsidList.addAll(lstTimeGoodsIds);
				}
			}
		}
		return goodsidList;
	}


	@Override
	public List getGoodsByCategoryID(int cityid) {
		String sql = "SELECT bcg.goodid,bcg.tagid FROM beiker_catlog_good bcg JOIN beiker_goods bg ON bcg.goodid=bg.goodsid WHERE bg.couponcash='0' AND bcg.isavaliable=1 AND bcg.area_id=? GROUP BY bcg.goodid";
		List goodsidMap = getJdbcTemplate().queryForList(sql, new Object[]{cityid});
		return goodsidMap;
	}

	@Override
	public List getSecondCategoryRank(int tagid,int cityid) {
		String sql = "SELECT COUNT(temp.goodid),temp.regionextid,brp.region_name,brp2.region_enname AS parent_en_name FROM(SELECT DISTINCT bcg.goodid,bcg.regionextid FROM beiker_catlog_good bcg WHERE bcg.isavaliable=1 AND bcg.tagid=? AND bcg.area_id=?) temp JOIN beiker_region_property brp ON brp.id=temp.regionextid JOIN beiker_region_property brp2 ON brp2.id=brp.parentid GROUP BY temp.regionextid ORDER BY COUNT(temp.goodid) DESC LIMIT 5";
		List regionRank = getJdbcTemplate().queryForList(sql,new Object[]{tagid,cityid});
		return regionRank;
	}

	@Override
	public Long getGoodsCountByCity(int tagid, int cityid) {
		String sql = "SELECT count(DISTINCT bcg.goodid) FROM beiker_catlog_good bcg WHERE bcg.tagid=? AND bcg.area_id=? AND bcg.isavaliable=1";
		Long count = getJdbcTemplate().queryForLong(sql, new Object[]{tagid,cityid});
		return count;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getCouponCashForCityId(Long cityId) {
		
		StringBuilder cashBuilder = new StringBuilder();
		cashBuilder.append("SELECT  DISTINCT bg.goodsid FROM beiker_goods bg ");
		cashBuilder.append("LEFT JOIN beiker_catlog_good bcg   ON bg.goodsid = bcg.goodid ");
		cashBuilder.append("LEFT JOIN beiker_goods_profile bgp ON bgp.goodsid = bcg.goodid ");
		cashBuilder.append("WHERE  bg.couponcash = '1' AND bg.isavaliable = 1 AND bg.startTime < NOW() AND bg.endTime >= NOW() ");
		cashBuilder.append("AND bgp.sales_count < bg.maxcount AND bcg.area_id = ? ");
		List cashList = this.getJdbcTemplate().queryForList(cashBuilder.toString(),new Object[]{cityId},Long.class);
		
		if(cashList != null && cashList.size()>0){
			return cashList;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getCheckGoodIdOneMerchant(String ids) {
		
		StringBuilder checkGood = new StringBuilder();
		checkGood.append("SELECT bgm.merchantid,bgm.goodsid,bgp.sales_count FROM beiker_goods_merchant bgm ");
		checkGood.append("LEFT JOIN beiker_merchant bg ON bgm.merchantid = bg.merchantid ");
		checkGood.append("LEFT JOIN beiker_goods_profile bgp ON bgm.goodsid = bgp.goodsid ");
		checkGood.append("where bg.parentid = 0 AND bgp.goodsid in (").append(ids).append(") ");
		checkGood.append("ORDER BY FIND_IN_SET(bgm.goodsid,'"+ids+"')");
		
		List checkList = this.getJdbcTemplate().queryForList(checkGood.toString());
		
		if(checkList != null && checkList.size()>0){
			return checkList;
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List getCouponCashById(Set<Long> idsList) {
		
		StringBuilder cashBuiler = new StringBuilder();
		cashBuiler.append("SELECT bg.goodsid,bg.sourcePrice,bg.currentPrice,bg.logo4 as listlogo FROM beiker_goods bg ");
		cashBuiler.append("where bg.goodsid in (").append(StringUtils.arrayToString(idsList.toArray(), ",")).append(") ");
		cashBuiler.append("ORDER BY FIND_IN_SET(bg.goodsid,'"+StringUtils.arrayToString(idsList.toArray(), ",")+"'").append(") ");
		List cashlist = this.getJdbcTemplate().queryForList(cashBuiler.toString());
		
		if(cashlist != null && cashlist.size()>0){
			return cashlist;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.beike.dao.qianpincard.QianPinCardDao#getGoodsIdsOrderOntime(java.util.List)
	 */
	@Override
	public List<Long> getGoodsIdsOrderOntime(List<Long> lstInGoods) {
		if(lstInGoods==null || lstInGoods.size()==0){
			return lstInGoods;
		}
		StringBuilder sqlTime = new StringBuilder();
		sqlTime.append("SELECT goods_id as goodsid FROM beiker_goods_on_end_time ")
			.append("where goods_id IN(").append(StringUtils.arrayToString(lstInGoods.toArray(), ",")).append(") ")
			.append("order by on_time desc");
		
		List<Long> lstTimeGoodsIds = getJdbcTemplate().queryForList(sqlTime.toString(),Long.class);
		
		if (lstTimeGoodsIds == null) {
			lstTimeGoodsIds = lstInGoods;
		}
		return lstTimeGoodsIds;
	}
}