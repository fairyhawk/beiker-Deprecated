package com.beike.dao.impl.coupon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.coupon.CouponDao;
import com.beike.entity.catlog.CouponCatlog;
import com.beike.form.CouponForm;
import com.beike.util.DateUtils;

/**
 * <p>Title: 优惠券数据库操作</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 26, 2011
 * @author ye.tian
 * @version 1.0
 */
@Repository("couponDao")
public class CouponDaoImpl extends GenericDaoImpl<CouponCatlog, Integer> implements CouponDao {


	public List<CouponForm> getCouponByPage(String idsCourse, int start, int end) {
		String sql="select id,couponname,enddate,downcount,couponlogo from beiker_coupon where id in ("+idsCourse+")  order by find_in_set(id,'"+idsCourse+"') limit "+start+","+end;
		List list=this.getJdbcTemplate().queryForList(sql, new Object[]{idsCourse,start,end});
		
		if(list==null||list.size()==0)return null;
		List<CouponForm> listCouponForm=new ArrayList<CouponForm>();
		for(int i=0;i<list.size();i++){
			Map map=(Map) list.get(i);
			CouponForm couponForm=new CouponForm();
			Long couponid=(Long) map.get("id");
			String couponName=(String) map.get("couponname");
			Date endDate=(Date) map.get("enddate");
			Long downcount=(Long) map.get("downcount");
			String couponlogo=(String) map.get("couponlogo");
			
			couponForm.setCouponid(couponid);
			couponForm.setCouponName(couponName);
			couponForm.setEndDate(endDate);
			couponForm.setCouponlogo(couponlogo);
			couponForm.setDowncount(downcount);
			listCouponForm.add(couponForm);
		}
		
		return listCouponForm;
		
	}

	public List<CouponForm> getCouponByIds(String idsCourse) {
		String sql="select merchantid,id,couponname,enddate,downcount,couponlogo from beiker_coupon where id in ("+idsCourse+")  order by find_in_set(id,'"+idsCourse+"')";
		
		String sqlRegion="select  bcg.couponid as couponid,brg.region_name as region_name,brg2.region_name as region_ext_name from beiker_catlog_coupon bcg , beiker_region_property  brg ,beiker_region_property brg2 where   bcg.regionid=brg.id  and  bcg.regionextid=brg2.id and bcg.couponid in("+idsCourse+")  order by find_in_set(bcg.couponid,'"+idsCourse+"')";
		
		List list=this.getJdbcTemplate().queryForList(sql);
		
		List listRegion=this.getJdbcTemplate().queryForList(sqlRegion);
		
		if(list==null||list.size()==0)return null;
		List<CouponForm> listCouponForm=new LinkedList<CouponForm>();
		
		Map<Long,CouponForm> mapForm=new LinkedHashMap<Long, CouponForm>();
		
		for(int i=0;i<list.size();i++){
			Map map=(Map) list.get(i);
			
			Long couponid=(Long) map.get("id");
			
			CouponForm couponForm=mapForm.get(couponid);
			if(couponForm==null){
				couponForm=new CouponForm();
			}
			
			Long merchantid=(Long) map.get("merchantid");
			String couponName=(String) map.get("couponname");
			Date endDate=(Date) map.get("enddate");
			Long downcount=(Long) map.get("downcount");
			String couponlogo=(String) map.get("couponlogo");
			couponForm.setMerchantid(merchantid);
			couponForm.setCouponid(couponid);
			couponForm.setCouponName(couponName);
			couponForm.setEndDate(endDate);
			couponForm.setCouponlogo(couponlogo);
			couponForm.setDowncount(downcount);
//			listCouponForm.add(couponForm);
			mapForm.put(couponid, couponForm);
		}
		
		if(listRegion!=null&&listRegion.size()>0){
			for(int j=0;j<listRegion.size();j++){
				Map mapRegionx=(Map) listRegion.get(j);
				
				Long couponid=(Long) mapRegionx.get("couponid");
				
				CouponForm couponForm=mapForm.get(couponid);
				if(couponForm!=null){
					
					Map<Long,Set<String>>  mapRegion=couponForm.getMapRegion();
					if(mapRegion==null){
						mapRegion=new HashMap<Long,Set<String>>();
					}
					
					Set<String> lr=(Set<String>) mapRegion.get(couponid);
					if(lr==null||lr.size()==0){
						lr=new HashSet<String>();
					}
					String region_name=(String) mapRegionx.get("region_name");
					String region_ext_name=(String) mapRegionx.get("region_ext_name");
					// 二级区域放在括号内 modify by qiaowb 2011-12-17
					lr.add(createRegionDisplay(region_name, region_ext_name));
					mapRegion.put(couponid, lr);
					couponForm.setMapRegion(mapRegion);
					mapForm.put(couponid, couponForm);
				}
			}
		}
		
		if(mapForm!=null&&mapForm.size()>0){
			Set<Long> setForm=mapForm.keySet();
			for (Long setLong : setForm) {
				CouponForm couponForm=mapForm.get(setLong);	
				// 只有一个商圈特殊处理 modify by qiaowb 2011-12-17
				couponForm.getMapRegion().put(
						couponForm.getCouponid(),
						correctRegionDisplay(couponForm.getMapRegion().get(
								couponForm.getCouponid())));
				listCouponForm.add(couponForm);
			}
		}
		
		
		
		return listCouponForm;
	}
	/**
	 * Lucene搜索
	 * @param idsCourse
	 * @return
	 */
	public List<CouponForm> getLuceneCouponByIds(String idsCourse) {
		//先搜索正常的优惠券(未下架)
		String sql="select merchantid,id,couponname,enddate,downcount,couponlogo from beiker_coupon where id in ("+idsCourse+") where enddate>=now() order by find_in_set(id,'"+idsCourse+"')";
		
		String sqlRegion="select  bcg.couponid as couponid,brg.region_name as region_name,brg2.region_name as region_ext_name from beiker_catlog_coupon bcg , beiker_region_property  brg ,beiker_region_property brg2 where   bcg.regionid=brg.id  and  bcg.regionextid=brg2.id and bcg.couponid in("+idsCourse+")  order by find_in_set(bcg.couponid,'"+idsCourse+"')";
		//已经下架的优惠券
		String sqlvalid="select merchantid,id,couponname,enddate,downcount,couponlogo from beiker_coupon where id in ("+idsCourse+") where enddate<now() order by find_in_set(id,'"+idsCourse+"')";

		List list=this.getJdbcTemplate().queryForList(sql);
		//下架优惠券排序靠后
		List validlist = getJdbcTemplate().queryForList(sqlvalid);
		if(validlist != null && validlist.size()>0){
			list.addAll(validlist);
		}
		List listRegion=this.getJdbcTemplate().queryForList(sqlRegion);
		
		if(list==null||list.size()==0)return null;
		List<CouponForm> listCouponForm=new LinkedList<CouponForm>();
		
		Map<Long,CouponForm> mapForm=new LinkedHashMap<Long, CouponForm>();
		
		for(int i=0;i<list.size();i++){
			Map map=(Map) list.get(i);
			
			Long couponid=(Long) map.get("id");
			
			CouponForm couponForm=mapForm.get(couponid);
			if(couponForm==null){
				couponForm=new CouponForm();
			}
			
			Long merchantid=(Long) map.get("merchantid");
			String couponName=(String) map.get("couponname");
			Date endDate=(Date) map.get("enddate");
			Long downcount=(Long) map.get("downcount");
			String couponlogo=(String) map.get("couponlogo");
			couponForm.setMerchantid(merchantid);
			couponForm.setCouponid(couponid);
			couponForm.setCouponName(couponName);
			couponForm.setEndDate(endDate);
			couponForm.setCouponlogo(couponlogo);
			couponForm.setDowncount(downcount);
//			listCouponForm.add(couponForm);
			mapForm.put(couponid, couponForm);
		}
		
		if(listRegion!=null&&listRegion.size()>0){
			for(int j=0;j<listRegion.size();j++){
				Map mapRegionx=(Map) listRegion.get(j);
				
				Long couponid=(Long) mapRegionx.get("couponid");
				
				CouponForm couponForm=mapForm.get(couponid);
				if(couponForm!=null){
					
					Map<Long,Set<String>>  mapRegion=couponForm.getMapRegion();
					if(mapRegion==null){
						mapRegion=new HashMap<Long,Set<String>>();
					}
					
					Set<String> lr=(Set<String>) mapRegion.get(couponid);
					if(lr==null||lr.size()==0){
						lr=new HashSet<String>();
					}
					String region_name=(String) mapRegionx.get("region_name");
					String region_ext_name=(String) mapRegionx.get("region_ext_name");
					lr.add(region_name+":"+region_ext_name);
					mapRegion.put(couponid, lr);
					couponForm.setMapRegion(mapRegion);
					mapForm.put(couponid, couponForm);
				}
			}
		}
		
		if(mapForm!=null&&mapForm.size()>0){
			Set<Long> setForm=mapForm.keySet();
			for (Long setLong : setForm) {
				CouponForm couponForm=mapForm.get(setLong);
				listCouponForm.add(couponForm);
			}
		}
		
		
		
		return listCouponForm;
	}
	public List<CouponForm> getCouponListByMerchantId(Long merchantId, int top) {
		String sql="select bc.id,bc.couponname from  beiker_coupon bc left join beiker_merchant bm on bc.merchantid=bm.merchantid where bc.merchantid=? and bm.parentid=0 and bc.enddate>=? and bc.createdate<=?  limit 0,?";
		String curDate = DateUtils.getStringDateShort();
		List list=this.getSimpleJdbcTemplate().queryForList(sql, merchantId,curDate,curDate,top);
		if(list==null||list.size()==0)return null;
		List<CouponForm> listForm=new ArrayList<CouponForm>();
		for(int i=0;i<list.size();i++){
			CouponForm couponForm=new CouponForm();
			Map map=(Map) list.get(i);
			Long couponid=(Long) map.get("id");
			String couponname=(String) map.get("couponname");
			couponForm.setCouponid(couponid);
			couponForm.setCouponName(couponname);
			listForm.add(couponForm);
		}
		
		return listForm;
		
	}

	public CouponForm getCouponDetailById(Integer couponId) {
		String sql="select bc.id,bc.couponname as couponname,bc.coupondetaillogo as coupondetaillogo,bc.smstemplate as smstemplate,bc.browsecounts as browsecounts,bc.downcount as downcount,bc.couponnumber as couponnumber,bc.couponrules as couponrules,bc.merchantid as merchantid,bc.createdate as createdate,bc.enddate as enddate,bc.coupon_title as coupon_title from beiker_coupon bc where bc.id=?";
		List list=this.getSimpleJdbcTemplate().queryForList(sql, couponId);
		if(list==null||list.size()==0)return null;
		Map map=(Map) list.get(0);
		CouponForm couponForm=new CouponForm();
		Long couponid=(Long) map.get("id");
		couponForm.setCouponid(couponid);
		String couponname=(String) map.get("couponname");
		couponForm.setCouponName(couponname);
		String coupondetaillogo=(String) map.get("coupondetaillogo");
		couponForm.setCoupondetaillogo(coupondetaillogo);
		String smstemplate=(String) map.get("smstemplate");
		couponForm.setSmstemplate(smstemplate);
		Long browsecounts=(Long) map.get("browsecounts");
		couponForm.setBrowsecounts(browsecounts);
		Long downcount=(Long) map.get("downcount");
		couponForm.setDowncount(downcount);
		String couponnumber=(String) map.get("couponnumber");
		couponForm.setCouponnumber(couponnumber);
		String couponrules=(String) map.get("couponrules");
		couponForm.setCouponrules(couponrules);
		Long merchantid=(Long) map.get("merchantid");
		couponForm.setMerchantid(merchantid);
		
		Date enddate=(Date) map.get("enddate");
		Date createdate=(Date) map.get("createdate");
		String coupontitle=(String) map.get("coupon_title");
		
		
		couponForm.setEndDate(enddate);
		couponForm.setCreateDate(createdate);
		couponForm.setCoupontitle(coupontitle);
		
		return couponForm;
	}

	public CouponCatlog getCouponCatlogById(Integer couponId) {
		String sql="select brp.id as tagid,brp.tag_name as tagname from beiker_tag_property brp left join  beiker_catlog_coupon  bcg on bcg.tagid=brp.id where bcg.couponid=?";
		
		List list=this.getJdbcTemplate().queryForList(sql, new Object[]{couponId});
		if(list==null||list.size()==0)return null;
		CouponCatlog couponCatlog=new CouponCatlog();
		for(int i=0;i<list.size();i++){
			Map map=(Map) list.get(i);
			Long tagId=(Long) map.get("tagid");
			String tagname=(String) map.get("tagname");
			couponCatlog.setTagid(tagId);
			couponCatlog.setTagName(tagname);
		}
		
		
		return couponCatlog;
	}

	public List<CouponForm> getCouponDownCount(String ids) {
		
		String sql="select bc.id,bc.downcount as downcount from beiker_coupon bc where bc.id in(" +
				ids+")";
		List list=this.getJdbcTemplate().queryForList(sql);
		if(list==null||list.size()==0)return new ArrayList<CouponForm>();
		List<CouponForm> listCouponForm=new ArrayList<CouponForm>();
		for(int i=0;i<list.size();i++){
			Map map=(Map) list.get(i);
			CouponForm cf=new CouponForm();
			Long id=(Long) map.get("id");
			Long downcount=(Long) map.get("downcount");
			cf.setCouponid(id);
			cf.setDowncount(downcount);
			listCouponForm.add(cf);
		}
		
		return listCouponForm;
	}

	public String getCouponCity(Integer couponId){
		String sql="select area_en_name as areaname from beiker_area ba left join beiker_catlog_coupon bcc on ba.area_id = bcc.area_id where bcc.couponid=?";
		List list=this.getJdbcTemplate().queryForList(sql, new Object[]{couponId});
		if(list==null||list.size()==0)return null;
		Map map=(Map) list.get(0);
		String areaname=(String) map.get("areaname");
		return areaname;
	}
	
		@Override
	public int getCouponStatusByID(Long couponid) {
		String sql = "SELECT bc.enddate FROM beiker_coupon bc WHERE bc.id=" + couponid;
		try{
			Date enddate = (Date) this.getJdbcTemplate().queryForObject(sql, Date.class);
			if(enddate.before(Calendar.getInstance().getTime())){
				return 0;
			}else{
				return 1;
			}
		}catch (Exception e) {
			//FIXME
			return 0;
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.beike.dao.coupon.CouponDao#getCouponCount(java.lang.Long)
	 */
	public int getCouponCount(Long merchantId) {
		String sqlCatlog = "select couponid from beiker_catlog_coupon where enddate>=? and createdate<=? and isavaliable=1";
		String curDate = DateUtils.getStringDateShort();
		List<Map<String, Object>> lstCatlogIds = null;
		lstCatlogIds = this.getSimpleJdbcTemplate().queryForList(
				sqlCatlog.toString(),curDate,curDate);
		if (lstCatlogIds != null && lstCatlogIds.size() > 0) {
			StringBuffer bufCatlogIds = new StringBuffer();
			for (Map<String, Object> mapId : lstCatlogIds) {
				bufCatlogIds = bufCatlogIds
						.append((Long) mapId.get("couponid")).append(",");
			}

			String sql = "SELECT COUNT(bc.id) AS couponcount FROM beiker_coupon bc WHERE bc.merchantid = ? ";
			sql = sql + " and bc.id in ("
					+ bufCatlogIds.substring(0, bufCatlogIds.length() - 1)
					+ ")";
			List list = this.getSimpleJdbcTemplate().queryForList(sql,
					new Object[] { merchantId });
			if (list == null || list.size() == 0 || list.size() > 1) {
				return 0;
			}
			Map map = (Map) list.get(0);
			Long count = (Long) map.get("couponcount");
			return Integer.parseInt(count + "");

		} else {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see com.beike.dao.coupon.CouponDao#getCouponCount(java.lang.Long, int, int)
	 */
	public List<Long> getCouponCountIds(Long merchantId, int start, int end) {
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

			String sql = "SELECT bc.id AS couponid FROM beiker_coupon bc WHERE bc.merchantid = ? ";
			sql = sql + " and bc.id in ("
					+ bufCatlogIds.substring(0, bufCatlogIds.length() - 1)
					+ ") ORDER BY bc.downcount desc limit ?,?";
			List list = this.getSimpleJdbcTemplate().queryForList(sql,
					merchantId, start, end);

			List<Long> listids = new LinkedList<Long>();
			if (list == null || list.size() == 0) {
				return new ArrayList<Long>();
			}
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				Long goodsId = (Long) map.get("couponid");
				listids.add(goodsId);
			}
			return listids;
		} else {
			return new ArrayList<Long>();
		}
	}
	
	/**
	 * 生成商圈显示字符串
	 * @param regionName
	 * @param regionNextName
	 * @param regionCount
	 * @return
	 */
	private String createRegionDisplay(String regionName, String regionNextName) {
		return regionName + "(" + regionNextName + ")";
	}
	
	/**
	 * 处理只有一个商圈只显示二级商圈
	 * @param setRegion
	 * @return
	 */
	private Set<String> correctRegionDisplay(Set<String> setRegion) {
		if (setRegion != null && setRegion.size() == 1) {
			String regionName = "";
			for (String region : setRegion) {
				regionName = region;
			}
			if (org.apache.commons.lang.StringUtils.isNotEmpty(regionName)) {
				String[] aryRegion = org.apache.commons.lang.StringUtils.split(
						regionName, "(");
				if (aryRegion.length == 2) {
					regionName = aryRegion[1];
					if (regionName.endsWith(")")) {
						regionName = regionName.substring(0, regionName
								.length() - 1);
					}
				}
			}
			setRegion.clear();
			setRegion.add(regionName);
		}
		return setRegion;
	}
}
