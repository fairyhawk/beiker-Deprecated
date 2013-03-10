package com.beike.dao.impl.merchant;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.merchant.ShopsBaoDao;
import com.beike.entity.merchant.Merchant;
import com.beike.form.CouponForm;
import com.beike.form.MerchantForm;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;

/**      
 * project:beiker  
 * Title:
 * Description:商铺宝DAOImpl
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Oct 31, 2011 5:24:42 PM     
 * @version 1.0
 */
@Repository("shopsBaoDao")
public class ShopsBaoDaoImpl  extends GenericDaoImpl<Merchant, Long> implements ShopsBaoDao {

	/* (non-Javadoc)
	 * @see com.beike.dao.merchant.ShopsBaoDao#getMerchantDetailById(java.lang.Long)
	 */
	public MerchantForm getMerchantDetailById(Long merchantId) 
	{
		StringBuffer bufSql = new StringBuffer("select bm.merchantid as merchantid, bm.virtualcount,")
				.append(" bmp.mc_logo1,bmp.mc_logo2,bmp.mc_logo3,bmp.mc_logo4,bmp.mc_avg_scores,bmp.mc_evaliation_count,bmp.mc_sale_count,bmp.mc_fix_tel,")
				.append(" bm.sevenrefound,bm.overrefound,bm.quality,bm.merchantintroduction,bm.merchantdesc,bm.merchantname,")
				.append(" bm.salescountent,bm.ownercontent,bm.csstemplatename,bm.city,bmp.mc_score,bmp.mc_well_count,bmp.mc_satisfy_count,bmp.mc_poor_count,bm.isvipbrand,bm.is_support_online_meal,bm.is_support_takeaway ")
				.append(" from beiker_merchant_profile bmp left join beiker_merchant bm on bm.merchantid=bmp.merchantid ")
				.append(" where bm.parentid=0 and bm.merchantid=? ");
		List list = this.getJdbcTemplate().queryForList(bufSql.toString(),new Object[] { merchantId });
		if (list == null || list.size() == 0){
			return null;
		}
		MerchantForm merchantForm = new MerchantForm();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			// 品牌的ID
			Long merchantid = (Long) map.get("merchantid");
			merchantForm.setId(String.valueOf(merchantid));
			/**
			 * 品牌下商品的虚拟购买次数
			 */
			int merVirtualCount = (Integer)map.get("virtualcount");
			merchantForm.setVirtualCount(merVirtualCount);

			Long sevenrefound = (Long) map.get("sevenrefound");
			merchantForm.setSevenrefound(sevenrefound);
			Long overrefound = (Long) map.get("overrefound");
			merchantForm.setOverrefound(overrefound);
			Long quality = (Long) map.get("quality");
			merchantForm.setQuality(quality);
			String merchantname = (String) map.get("merchantname");
			merchantForm.setMerchantname(merchantname);
			String merchantintroduction = (String) map.get("merchantintroduction");
			merchantForm.setMerchantintroduction(merchantintroduction);
			String merchantdesc = (String) map.get("merchantdesc");
			merchantForm.setMerchantdesc(merchantdesc);
			merchantForm.setCity((String) map.get("city"));
			merchantForm.setSalescountent((String)map.get("salescountent"));
			merchantForm.setOwnercontent((String)map.get("ownercontent"));
			merchantForm.setCsstemplatename((String)map.get("csstemplatename"));
			
			merchantForm.setLogo1((String) map.get("mc_logo1"));
			merchantForm.setLogoTitle((String) map.get("mc_logo4"));
			merchantForm.setAvgscores(String.valueOf((Float)map.get("mc_avg_scores")));
			merchantForm.setEvaluation_count(String.valueOf((Integer) map.get("mc_evaliation_count")));
			merchantForm.setSalescount(String.valueOf((Integer) map.get("mc_sale_count")));
			merchantForm.setTel((String) map.get("mc_fix_tel"));
			merchantForm.setLogo2((String) map.get("mc_logo2"));
			
			//add by qiaowb 2012-03-15 新评价系统评分
			merchantForm.setMcScore((Long) map.get("mc_score"));
			merchantForm.setWellCount((Long) map.get("mc_well_count"));
			merchantForm.setSatisfyCount((Long) map.get("mc_satisfy_count"));
			merchantForm.setPoorCount((Long) map.get("mc_poor_count"));
			merchantForm.setIsVip((Integer)map.get("isvipbrand"));
			
			merchantForm.setIs_Support_Online_Meal(String.valueOf(map.get("is_support_online_meal")));
			merchantForm.setIs_Support_Takeaway(String.valueOf(map.get("is_support_takeaway")));
		}
		return merchantForm;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public String getCashCouponByGoods(Long merchantId, Long money) {
		StringBuilder mer = new StringBuilder();
		StringBuilder str = new StringBuilder();
		mer.append("SELECT goodsid FROM beiker_goods_merchant bgm ");
		mer.append("where bgm.merchantid=?");
		List merchantList = this.getJdbcTemplate().queryForList(mer.toString(),new Object[]{merchantId});
		
		if(null == merchantList || merchantList.size() <= 0){
		    	return null; // 该品牌不存在
		}
		
		for(int i=0;i<merchantList.size();i++){
	    	Map map = (Map) merchantList.get(i);
	    	Long goodsid    = (Long) map.get("goodsid");
	    	str.append(goodsid);
	    	str.append(",");
	    }
		
		String ids = str.substring(0,str.length()-1);
		return ids;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List getCashCouponByIDAndMoney(String ids,Long money) {
		
		StringBuilder gods = new StringBuilder();
		gods.append("SELECT bg.goodsid,bg.rebatePrice,bg.currentPrice ,bg.goodsname FROM beiker_goods bg ");
		gods.append("WHERE bg.couponcash = '1' AND bg.isavaliable='1' AND bg.sourcePrice = ? AND bg.goodsid in ( ");
		gods.append(ids+") ");
		gods.append("ORDER BY bg.startTime DESC ");
		gods.append("LIMIT 2");
		List rs = this.getJdbcTemplate().queryForList(gods.toString(),new Object[]{money});
		return rs;
	}

	@Override
	public List<MerchantForm> getChildMerchnatById(Long merchantId) {
		
		StringBuilder queryMerchant = new StringBuilder();
		queryMerchant.append("select m.merchantname as merchantname,m.merchantid as id,m.addr as addr,m.latitude as latitude,m.tel as tel,m.buinesstime as buinesstime from  beiker_merchant m where m.parentid=?");
		
		List list = this.getJdbcTemplate().queryForList(queryMerchant.toString(),new Object[] { merchantId });
		if (list == null || list.size() == 0)
			return null;
		
		List<MerchantForm> listForm = new ArrayList<MerchantForm>();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			MerchantForm merchantForm = new MerchantForm();
			String merchantname = (String) map.get("merchantname");
			merchantForm.setMerchantname(merchantname);
			Long mid = (Long) map.get("id");
			merchantForm.setId(String.valueOf(mid));
			String addr = (String) map.get("addr");
			merchantForm.setAddr(addr);
			String latitude = (String) map.get("latitude");
			merchantForm.setLatitude(latitude);
			String tel = (String) map.get("tel");
			merchantForm.setTel(tel);
			String buinesstime = (String) map.get("buinesstime");
			merchantForm.setBuinesstime(buinesstime);
			listForm.add(merchantForm);
		}
		return listForm;
	}

	@Override
	public List<Long> getGoodsCountIds(String idsCourse, int start, int end) {
		if(idsCourse==null || "".equals(idsCourse)){
			return new ArrayList<Long>();
		}
		String sql = "select bg.goodsId as goodsid  from beiker_goods_profile bgf left join  beiker_goods bg on bgf.goodsid=bg.goodsId left join beiker_goods_merchant bgm on bgf.goodsid=bgm.goodsid  where bgm.merchantid in("
			+ idsCourse
			+ ") AND bg.isavaliable = '1' AND bgf.sales_count < bg.maxcount and bg.endTime >=? and bg.startTime<=?  group by  bg.goodsId ORDER BY  bg.startTime DESC limit "
			+ start + "," + end;
		String curDate = DateUtils.getStringDateShort();
		List list = this.getJdbcTemplate().queryForList(sql,new Object[] {curDate,curDate});
		List<Long> listids = new LinkedList<Long>();
		if (list == null || list.size() == 0)
			return new ArrayList<Long>();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			Long goodsId = (Long) map.get("goodsid");
			listids.add(goodsId);
		}
		return listids;
	}
	
	/* (non-Javadoc)
	 * @see com.beike.dao.merchant.ShopsBaoDao#getMerchantRegionById(java.lang.Long)
	 */
	public String getMerchantRegionById(Long merchantId) {
		String regionname = "";
		String sql = "SELECT DISTINCT regionextid FROM beiker_catlog_good WHERE brandid=" + merchantId + " LIMIT 5";
		List<Map<String,Object>> listId = this.getSimpleJdbcTemplate().queryForList(sql);
		if(listId!=null && listId.size()>0){
			StringBuilder ids = new StringBuilder("");
			for (Map<String,Object> mapId : listId) {
				ids.append(mapId.get("regionextid"));
				ids.append(",");
			}
			
			if (ids.length() == 0) {
				return regionname;
			}
			
			String id = ids.substring(0, ids.lastIndexOf(","));
			
			sql = "SELECT GROUP_CONCAT(region_name) as regionname FROM beiker_region_property WHERE id IN (" + id + ")";
			
			List<Map<String,Object>> list = this.getSimpleJdbcTemplate().queryForList(sql);
			if (list == null || list.size() == 0 || list.size() > 1){
				return regionname;
			}
			Map<String,Object> map = list.get(0);
			regionname = (String) map.get("regionname");
		}
		
		return regionname;
	}

	@Override
	public List<CouponForm> getCouponForShopBaoByMerchantId(Long merchantId,int top) {
		String sqlCatlog = "select couponid from beiker_catlog_coupon where enddate>=? and createdate<=? and isavaliable=1";
		List<Map<String, Object>> lstCatlogIds = null;
		String curDate = DateUtils.getStringDateShort();
		lstCatlogIds = this.getSimpleJdbcTemplate().queryForList(
				sqlCatlog.toString(),curDate,curDate);
		if (lstCatlogIds != null && lstCatlogIds.size() > 0) {
			StringBuffer bufCatlogIds = new StringBuffer();
			for (Map<String, Object> mapId : lstCatlogIds) {
				bufCatlogIds = bufCatlogIds
						.append((Long) mapId.get("couponid")).append(",");
			}

			String sql = "select bc.id,bc.couponname,bc.enddate,bc.downcount from beiker_coupon bc left join beiker_merchant bm on bc.merchantid=bm.merchantid where bc.merchantid= "
					+ merchantId + " and bm.parentid=0 ";
			sql = sql + " and bc.id in ("
					+ bufCatlogIds.substring(0, bufCatlogIds.length() - 1)
					+ ") ORDER BY bc.createdate DESC  limit 0," + top;
			List list = this.getJdbcTemplate().queryForList(sql);
			if (list == null || list.size() == 0)
				return null;
			List<CouponForm> listForm = new ArrayList<CouponForm>();
			for (int i = 0; i < list.size(); i++) {
				CouponForm couponForm = new CouponForm();
				Map map = (Map) list.get(i);
				Long couponid = (Long) map.get("id");
				String couponname = (String) map.get("couponname");
				Date endDate = (Date) map.get("enddate");
				couponForm.setCouponid(couponid);
				couponForm.setCouponName(couponname);
				couponForm.setEndDate(endDate);
				couponForm.setDowncount((Long) map.get("downcount"));
				listForm.add(couponForm);
			}
			return listForm;
		} else {
			return new ArrayList<CouponForm>();
		}
	}


	@Override
	public MerchantForm getShangpubaoDetailById(Long merchantId) {
		StringBuffer bufSql = new StringBuffer("select bsp.shb_title_logo,bsp.shb_logo1,bsp.shb_logo2,bsp.shb_logo3,bsp.shb_logo4,")
				.append(" bsp.shb_logo5,bsp.shb_logo6,bsp.shb_logo7,bsp.shb_logo8 ")
				.append(" from beiker_shanghubao_profile bsp")
				.append(" where bsp.merchantid=? ");
		List list = this.getJdbcTemplate().queryForList(bufSql.toString(),new Object[] { merchantId });
		if (list == null || list.size() == 0){
			return null;
		}
		MerchantForm merchantForm = new MerchantForm();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			merchantForm.setBaoTitleLogo((String) map.get("shb_title_logo"));
			merchantForm.setMerchantbaoLogo1((String) map.get("shb_logo1"));
			merchantForm.setMerchantbaoLogo2((String) map.get("shb_logo2"));
			merchantForm.setMerchantbaoLogo3((String) map.get("shb_logo3"));
			merchantForm.setMerchantbaoLogo4((String) map.get("shb_logo4"));
			merchantForm.setMerchantbaoLogo5((String) map.get("shb_logo5"));
			merchantForm.setMerchantbaoLogo6((String) map.get("shb_logo6"));
			merchantForm.setMerchantbaoLogo7((String) map.get("shb_logo7"));
			merchantForm.setMerchantbaoLogo8((String) map.get("shb_logo8"));
		}
		return merchantForm;
	}


	@Override
	public List<MerchantForm> getBrandReview(List<String> brandids) {
		StringBuffer bufSql = new StringBuffer("select bmp.merchantid as merchantid, bm.isvipbrand isvip,")
		.append(" bmp.mc_score,bmp.mc_well_count,bmp.mc_satisfy_count,bmp.mc_poor_count")
		.append(" from beiker_merchant_profile bmp left join beiker_merchant bm on bm.merchantid=bmp.merchantid  where bmp.merchantid IN(").append(StringUtils.arrayToString(brandids.toArray(), ",")).append(")");
		List list = this.getJdbcTemplate().queryForList(bufSql.toString());
		
		
		List<MerchantForm> brands = new ArrayList<MerchantForm>();
		for (int i = 0; i < list.size(); i++) {
			MerchantForm merchantForm = new MerchantForm();
			Map map = (Map) list.get(i);
			// 品牌的ID
			Long merchantid = (Long) map.get("merchantid");
			merchantForm.setId(String.valueOf(merchantid));

			//add by qiaowb 2012-03-15 新评价系统评分
			merchantForm.setMcScore((Long) map.get("mc_score"));
			merchantForm.setWellCount((Long) map.get("mc_well_count"));
			merchantForm.setSatisfyCount((Long) map.get("mc_satisfy_count"));
			merchantForm.setPoorCount((Long) map.get("mc_poor_count"));
			merchantForm.setIsVip((Integer)map.get("isvip"));
			brands.add(merchantForm);
		}
		return brands;
	}


	@SuppressWarnings("rawtypes")
	@Override
	public int getGoodsIdTotalCount(String idsCourse) {
		
		if(org.apache.commons.lang.StringUtils.isBlank(idsCourse))
			   return 0;
		
		StringBuilder countsql = new StringBuilder();
		
		countsql.append("select bg.goodsId as goodsid  from beiker_goods_profile bgf ");
		countsql.append("left join  beiker_goods bg on bgf.goodsid=bg.goodsId ");
		countsql.append("left join beiker_goods_merchant bgm on bgf.goodsid=bgm.goodsid ");
		countsql.append("where bgm.merchantid in(").append(idsCourse).append(") ");
		countsql.append("AND bg.isavaliable = '1' AND bgf.sales_count < bg.maxcount and ");
		countsql.append("bg.endTime >=NOW() and bg.startTime<=NOW() ");
		countsql.append("group by  bg.goodsId ORDER BY  bg.startTime ");
		
		List countlist = this.getJdbcTemplate().queryForList(countsql.toString());
		
		if(countlist == null || countlist.size() ==0)
			return 0;
		
		return countlist.size();
	}
}