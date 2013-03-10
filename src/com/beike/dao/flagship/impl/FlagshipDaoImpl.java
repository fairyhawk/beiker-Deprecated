package com.beike.dao.flagship.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.flagship.FlagshipDao;
import com.beike.entity.flagship.Flagship;
import com.beike.form.MerchantForm;
import com.beike.mapper.FlagshipMapper;
import com.beike.page.Pager;

/**
 * @ClassName: FlagshipDaoImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Grace Guo guoqingcun@gmail.com
 * @date 2013-1-16 下午3:29:11
 *
 */
@Repository("flagshipDao")
public class FlagshipDaoImpl extends GenericDaoImpl<Flagship, Long> implements FlagshipDao {
	private final Log logger = LogFactory.getLog(FlagshipDaoImpl.class);
	
	@Override
	public Flagship getFlagshipByRealmName(String realmName,Boolean isPreview) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT(BF.id),BF.guest_id,BF.brand_id,BF.city,BF.realm_name,BF.sina_microBlog,BF.qq_microBlog,BF.flagship_name, BF.flagship_background_color,BF.flagship_background_img,BFM.id AS mould_id,BFM.mould_name,BFM.mould_img,BFM.mould_url,GROUP_CONCAT(DISTINCT(BFB.branch_id))  AS branchs,flagship_logo,sina_microBlog_name ");
		sql.append("FROM beiker_flagship BF LEFT JOIN beiker_flagship_mould BFM ON BF.mould = BFM.id LEFT JOIN beiker_flagship_branch BFB ON BF.id = BFB.flagship_id JOIN beiker_merchant BM ON BFB.branch_id = BM.merchantid ");
		if(isPreview)
			sql.append("WHERE realm_name=? ");
		else
			sql.append("WHERE realm_name=? AND BF.is_online = '1' ");
		sql.append("GROUP BY BFB.flagship_id ");		
		sql.append("limit 1");
		return this.getSimpleJdbcTemplate().queryForObject(sql.toString(), new FlagshipMapper(), new Object[]{realmName});
	}

	@Override
	public Long getLastInsertId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MerchantForm> getChildMerchnatById(String branchs, Pager pager) {
		// modify by qiaowb 2011-12-30 只查询曾经有过商品的分店地址
				String sql = "select distinct m.merchantname as merchantname,m.merchantid as id,m.addr as addr,m.latitude as latitude,m.tel as tel,m.buinesstime as buinesstime,m.city as city, m.is_support_takeaway as is_support_takeaway, m.is_support_online_meal as is_support_online_meal,m.environment,m.capacity,m.otherservice from  beiker_merchant m  where m.merchantid in("+branchs+") order by m.sort_number asc,m.merchantid desc limit "
						+ pager.getStartRow() + "," + pager.getPageSize();

				List list = this.getJdbcTemplate().queryForList(sql);
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
					String city = (String) map.get("city");
					merchantForm.setCity(city);
					merchantForm.setIs_Support_Takeaway(map.get("is_support_takeaway").toString());
					merchantForm.setIs_Support_Online_Meal(map.get("is_support_online_meal").toString());
					
					//author wenjie.mai 添加environment、capacity、otherservice三个字段
					String environment = (String) map.get("environment");
					String capacity    = (String) map.get("capacity");
					String otherservice= (String) map.get("otherservice");
					
					if(StringUtils.isNotBlank(environment)){
						if(environment.indexOf(",") !=-1){
							environment = environment.trim().replaceAll(",","、");
						}
						if(StringUtils.isNotBlank(capacity) || StringUtils.isNotBlank(otherservice))
							environment += "、";
					}else{
						environment = "";
					}
					
					if(StringUtils.isNotBlank(capacity)){
						capacity = capacity.trim();
						if(StringUtils.isNotBlank(otherservice))
							capacity += "、";
					}else{
						capacity = "";
					}
					
					if(StringUtils.isNotBlank(otherservice)){
						if(otherservice.indexOf(",") !=-1){
							otherservice = otherservice.trim().replaceAll(",","、");
						}
					}else{
						otherservice = "";
					}
					
					merchantForm.setEnvironment(environment);
					merchantForm.setCapacity(capacity);
					merchantForm.setOtherservice(otherservice);
					
					listForm.add(merchantForm);
				}
				return listForm;
	}
	@SuppressWarnings("rawtypes")
	@Override
	public int getFlagShipTotalCountForCity(Long cityId) {
		
		StringBuilder countsql = new StringBuilder();
		
		countsql.append("SELECT DISTINCT(bf.brand_id),bf.realm_name,bm.virtualcount,bmp.mc_sale_count,bmp.mc_logo2, ");
		countsql.append("bmp.mc_well_count,bmp.mc_satisfy_count,bmp.mc_poor_count,bm.merchantname FROM beiker_flagship bf ");
		countsql.append("LEFT JOIN beiker_merchant bm ON bf.brand_id =  bm.merchantid ");
		countsql.append("LEFT JOIN beiker_merchant_profile bmp ON bmp.merchantid = bm.merchantid ");
		countsql.append("WHERE bf.is_online = '1' AND bf.city = ").append(cityId);
		countsql.append(" ORDER BY bf.online_time DESC ");
		
		List countlist = this.getJdbcTemplate().queryForList(countsql.toString());
		
		if(countlist == null || countlist.size() == 0)
			return 0;
		
		return countlist.size();
	}

	@SuppressWarnings({"rawtypes"})
	@Override
	public List getFlagShipInfo(Long cityId,int start,int end) {
		
		StringBuilder flagsql = new StringBuilder();
		
		flagsql.append("SELECT DISTINCT(bf.brand_id),bf.realm_name,bm.virtualcount,bmp.mc_sale_count,bmp.mc_logo2, ");
		flagsql.append("bmp.mc_well_count,bmp.mc_satisfy_count,bmp.mc_poor_count,bm.merchantname FROM beiker_flagship bf ");
		flagsql.append("LEFT JOIN beiker_merchant bm ON bf.brand_id =  bm.merchantid ");
		flagsql.append("LEFT JOIN beiker_merchant_profile bmp ON bmp.merchantid = bm.merchantid ");
		flagsql.append("WHERE bf.is_online = '1' AND bf.city = ").append(cityId);
		flagsql.append(" ORDER BY bf.online_time DESC ");
		flagsql.append(" Limit ").append(start).append(",").append(end);
		
		List flaglist = this.getJdbcTemplate().queryForList(flagsql.toString());
		
		if(flaglist == null || flaglist.size() == 0)
			return null;
		
		return flaglist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getFlagshipByMerchantId(Long merchantId) {
		
		StringBuilder flagsql = new StringBuilder();
		
		flagsql.append("SELECT DISTINCT(BF.id),BF.guest_id,BF.brand_id,BF.city,BF.realm_name,BF.sina_microBlog,");
		flagsql.append("BF.qq_microBlog,BF.flagship_name, BF.flagship_background_color,BF.flagship_background_img,");
		flagsql.append("BFM.id AS mould_id,BFM.mould_name,BFM.mould_img,BFM.mould_url,GROUP_CONCAT(DISTINCT(BFB.branch_id))  AS branchs,");
		flagsql.append("flagship_logo,sina_microBlog_name FROM beiker_flagship BF ");
		flagsql.append("LEFT JOIN beiker_flagship_mould BFM ON BF.mould = BFM.id ");
		flagsql.append("LEFT JOIN beiker_flagship_branch BFB ON BF.id = BFB.flagship_id ");
		flagsql.append("LEFT JOIN beiker_merchant BM ON BFB.branch_id = BM.merchantid ");
		flagsql.append("WHERE BF.is_online = '1' AND BF.brand_id = ").append(merchantId);
		flagsql.append(" GROUP BY BFB.flagship_id ");
		flagsql.append(" LIMIT 1");
		
		List li = this.getSimpleJdbcTemplate().queryForList(flagsql.toString());
		
		return li;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getOfferContentByMerchantId(Long merchantId,String nowTime) {
		
		StringBuilder offer_sql = new StringBuilder();
		
		offer_sql.append("SELECT offers_id,guest_id,brand_id,begin_time,end_time,offers_contents,offers_status ");
		offer_sql.append("FROM beiker_special_offers ");
		offer_sql.append(" WHERE offers_status = 'ONLINE' AND begin_time <= '").append(nowTime).append("' ");
		offer_sql.append(" AND end_time >= '").append(nowTime).append("' AND brand_id = ").append(merchantId);
		offer_sql.append(" ORDER BY begin_time DESC ");
		
		List offerlist =  this.getJdbcTemplate().queryForList(offer_sql.toString());
		return offerlist;
	}
}
