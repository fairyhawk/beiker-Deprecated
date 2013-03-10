package com.beike.dao.impl.merchant;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.beike.action.pay.PayInfoParam;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.merchant.MerchantDao;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.entity.catlog.MerchantCatlog;
import com.beike.entity.merchant.Merchant;
import com.beike.entity.merchant.MerchantEvaluation;
import com.beike.entity.merchant.MerchantProfileType;
import com.beike.form.MerchantForm;
import com.beike.util.StringUtils;

/**
 * <p>
 * Title:商户操作数据库
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
 * @date May 17, 2011
 * @author ye.tian
 * @version 1.0
 */
@Repository("merchantDao")
public class MerchantDaoImpl extends GenericDaoImpl<Merchant, Long> implements MerchantDao {

	public static int READ_BUFFER_SIZE = 1024;

	/**
	 * 补充说明：
	 * 该方法的sql语句添加了virtualcount 字段！
	 */
	@Override
	public Merchant getMerchantById(Long id) {
		String sql = " select merchantid as id,addr,latitude,merchantname,overrefound,parentId,quality,sevenrefound,tel,merchantintroduction, virtualcount " + " from beiker_merchant where merchantid=? limit 1";
		return getSimpleJdbcTemplate().queryForObject(sql, new RowMapperImpl(), id);

	}

	protected class RowMapperImpl implements ParameterizedRowMapper<Merchant> {
		@Override
		public Merchant mapRow(ResultSet rs, int rowNum) throws SQLException {
			Merchant merchant = new Merchant();

			merchant.setId(rs.getLong("id"));
			merchant.setAddr(rs.getString("addr"));
			merchant.setLatitude(rs.getString("latitude"));

			merchant.setMerchantname(rs.getString("merchantname"));
			merchant.setOverrefound(rs.getInt("overrefound"));
			merchant.setParentId(rs.getLong("parentId"));
			merchant.setQuality(rs.getInt("quality"));
			merchant.setSevenrefound(rs.getInt("sevenrefound"));
			merchant.setTel(rs.getString("tel"));

			/**
			 * 补充说明： 该方法添加了virtualcount 字段！
			 */
			merchant.setVirtualCount(rs.getInt("virtualcount"));

			Blob blob = rs.getBlob("merchantintroduction");

			StringBuilder sb = new StringBuilder();
			BufferedInputStream bi = null;
			try {
				bi = new BufferedInputStream(blob.getBinaryStream());
				byte[] data = new byte[READ_BUFFER_SIZE];
				for (int len = 0; (len = bi.read(data)) != -1;) {
					sb.append(new String(data, "UTF-8"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (bi != null) {
					try {
						bi.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			merchant.setMerchantintroduction(sb.toString());
			return merchant;
		}
	}

	@Override
	public void addMerchant(MerchantForm form) {
		String sql = "insert into beiker_merchant (addr,merchantname,tel,parentId,latitude,sevenrefound,overrefound,quality,merchantintroduction,buinesstime) " + "values (?,?,?,?,?,?,?,?,?,?)";
		this.getSimpleJdbcTemplate().update(sql, form.getAddr(), form.getMerchantname(), form.getTel(), form.getParentId(), form.getLatitude(), form.getSevenrefound(), form.getOverrefound(), form.getQuality(), form.getMerchantintroduction(), form.getBuinesstime());
	}

	@Override
	public Map<String, String> getEvaluationAvgScoreByMerchantId(Long merchantId) {
		String sql = "select avg(m.evaluationscore) as evaluationscore,count(m.id) as count   from beiker_merchantevaluation  m where m.merchantid=?";
		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { merchantId });
		Double evaluationscore = null;
		Long count = null;
		if (list != null && list.size() > 0) {
			Map map = (Map) list.get(0);
			evaluationscore = (Double) map.get("evaluationscore");
			count = (Long) map.get("count");
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("evaluationscore", evaluationscore + "");
		map.put("count", count + "");

		return map;

	}

	@SuppressWarnings("unchecked")
	@Override
	public MerchantProfileType getMerchantProfileTypeByMerchantId(Long merchantId, String propertyname) {
		// author wenjie.mai sql优化
		String sql = "select id,merchantid,mc_logo1,mc_logo2,mc_logo3,mc_logo4,mc_avg_scores,mc_evaliation_count,mc_sale_count,mc_fix_tel " + "from beiker_merchant_profile where merchantid = ? ";
		logger.info("sql...." + sql);
		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { merchantId });
		if (list == null || list.size() == 0)
			return null;
		MerchantProfileType pt = new MerchantProfileType();
		for (Object object : list) {
			Map map = (Map) object;
			String pvalue = String.valueOf(map.get(propertyname));
			pt.setPropertyname(propertyname);
			pt.setPropertyvalue(pvalue);
			Long id = (Long) map.get("id");
			pt.setId(id);
		}
		return pt;
	}

	public Map<Long, Map<String, Object>> getMerchantLogo(List<Long> merchantidList) {
		Map<Long, Map<String, Object>> returnMap = new HashMap<Long, Map<String, Object>>();
		if (merchantidList == null || merchantidList.size() == 0) {
			return returnMap;
		}
		// author wenjie.mai sql 优化
		String query_logo = "select bmc.merchantid,bmc.mc_logo1,bmc.mc_logo2,bmc.mc_logo3,bmc.mc_logo4 from beiker_merchant_profile bmc " + "left join beiker_merchant bm on bmc.merchantid=bm.merchantid where bm.parentid=0 and " + "bm.merchantid in (" + StringUtils.arrayToString(merchantidList.toArray(new Long[] {}), ",") + ")";
		logger.info("query_logo......" + query_logo);
		List<Map<String, Object>> queryMap = this.getSimpleJdbcTemplate().queryForList(query_logo);
		for (Map<String, Object> map : queryMap) {
			Long merchantid = ((Number) map.get("merchantid")).longValue();
			String mc_logo1 = (String) map.get("mc_logo1");
			String mc_logo2 = (String) map.get("mc_logo2");
			String mc_logo3 = (String) map.get("mc_logo3");
			String mc_logo4 = (String) map.get("mc_logo4");
			Map<String, Object> logoMap = returnMap.get(merchantid);
			if (logoMap == null) {
				logoMap = new HashMap<String, Object>();
				returnMap.put(merchantid, logoMap);
			}
			if (StringUtils.validNull(mc_logo1)) {
				logoMap.put("mc_logo1", mc_logo1);
			}
			if (StringUtils.validNull(mc_logo2)) {
				logoMap.put("mc_logo2", mc_logo2);
			}
			if (StringUtils.validNull(mc_logo3)) {
				logoMap.put("mc_logo3", mc_logo3);
			}
			if (StringUtils.validNull(mc_logo4)) {
				logoMap.put("mc_logo4", mc_logo4);
			}
		}
		return returnMap;
	}

	/**
	 * 补充说明：
	 * 该方法的sql 添加了 bm.virtualcount 字段, 用于显示品牌下的商品的虚拟购买次数
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<MerchantForm> getMerchantByIds(String idsCourse) {
		// author wenjie.mai sql优化
		String sql = "select bm.merchantname,bm.virtualcount,bmc.id,bmc.merchantid,bmc.mc_logo1,bmc.mc_logo2,bmc.mc_logo3," + "bmc.mc_logo4,bmc.mc_avg_scores,bmc.mc_evaliation_count,bmc.mc_sale_count,bmc.mc_fix_tel " + "from beiker_merchant_profile bmc left join beiker_merchant bm on bmc.merchantid=bm.merchantid " + "where bm.merchantid in (" + idsCourse + ")  and bm.parentid=0 "
				+ "order by find_in_set(bmc.merchantid,'" + idsCourse + "')";
		List list = this.getJdbcTemplate().queryForList(sql);
		if (list == null || list.size() == 0)
			return null;
		List<MerchantForm> listForm = new LinkedList<MerchantForm>();

		List<Long> merchantidList = new ArrayList<Long>();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			Long merchantid = (Long) map.get("merchantid");
			merchantidList.add(merchantid);
		}
		Map<Long, Map<String, Object>> logoMap = getMerchantLogo(merchantidList);

		for (int i = 0; i < list.size(); i++) {
			MerchantForm mf = new MerchantForm();
			Map map = (Map) list.get(i);
			Long merchantid = (Long) map.get("merchantid");
			if (merchantid != null) {
				mf.setId(String.valueOf(merchantid));
			}

			if (listForm.indexOf(mf) != -1) {
				mf = listForm.get(listForm.indexOf(mf));
			}
			// 品牌的名称
			String merchantname = (String) map.get("merchantname");
			mf.setMerchantname(merchantname);
			/**
			 * 品牌下商品的虚拟购买次数
			 */
			int merVirtualCount = (Integer) map.get("virtualcount");
			mf.setVirtualCount(merVirtualCount);
			Map tempMap = logoMap.get(merchantid);
			if (tempMap.get("mc_logo1") != null) {
				mf.setLogo1(String.valueOf(tempMap.get("mc_logo1")));
			}
			if (tempMap.get("mc_logo2") != null) {
				mf.setLogo2(String.valueOf(tempMap.get("mc_logo2")));
			}
			if (tempMap.get("mc_logo3") != null) {
				mf.setLogoDetail(String.valueOf(tempMap.get("mc_logo3")));
			}
			Integer saleCount = (Integer) map.get("mc_sale_count");
			if (saleCount == null || "".equals(saleCount)) {
				saleCount = 0;
			}
			mf.setSalescount(String.valueOf(saleCount));
			if (listForm.indexOf(mf) == -1) {
				listForm.add(mf);
			}
		}
		return listForm;
	}

	/**
	 * 补充说明：
	 * 该方法的sql 添加了 bm.virtualcount 字段, 用于显示品牌下的商品的虚拟购买次数
	 */
	public List<MerchantForm> getGoodsByIds(String idsCourse, int start, int end) {
		// author wenjie.mai sql优化
		String sql = "select bm.merchantname,bm.virtualcount,bmc.merchantid,bmc.mc_logo1,bmc.mc_logo2,bmc.mc_logo3,bmc.mc_logo4" + " from beiker_merchant_profile bmc  left join beiker_merchant bm on bmc.merchantid=bm.merchantid " + "where bm.parentid=0 and bmc.merchantid in (" + idsCourse + ") " + "order by find_in_set(bmc.id,'" + idsCourse + "') limit " + start + "," + end;
		logger.info("sql..." + sql);
		List list = this.getJdbcTemplate().queryForList(sql);
		if (list == null || list.size() == 0) {
			return null;
		}
		List<MerchantForm> listForm = new ArrayList<MerchantForm>();

		for (int i = 0; i < list.size(); i++) {
			MerchantForm mf = new MerchantForm();
			Map map = (Map) list.get(i);
			Long merchantid = (Long) map.get("merchantid");
			if (merchantid != null) {
				mf.setId(String.valueOf(merchantid));
			}
			if (listForm.indexOf(mf) != -1) {
				mf = listForm.get(listForm.indexOf(mf));
			}
			// 品牌的名称
			String merchantname = (String) map.get("merchantname");
			mf.setMerchantname(merchantname);
			/**
			 * 品牌下商品的虚拟购买次数
			 */
			int merVirtualCount = (Integer) map.get("virtualcount");
			mf.setVirtualCount(merVirtualCount);
			String logo2 = (String) map.get("mc_logo2");
			String logo3 = (String) map.get("mc_logo3");
			if (StringUtils.validNull(logo2)) {
				mf.setLogo2(logo2);
			}
			if (StringUtils.validNull(logo3)) {
				mf.setLogoDetail(logo3);
			}
			listForm.add(mf);
		}

		return listForm;
	}

	/**
	 * 补充说明：该方法涉及到页面中的”品牌购买次数“的显示
	 * 该方法中的sql 语句添加了 bm.virtualcount 字段,用于记录品牌下商品的虚拟购买次数
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MerchantForm getMerchantDetailById(Long merchantId) {
		// author wenjie.mai sql 优化
		String sql = "select bm.merchantid as merchantid, bm.virtualcount,bm.sevenrefound,bm.overrefound,bm.quality," + "bm.merchantintroduction,bm.merchantdesc,bm.merchantname,bmc.mc_logo1,bmc.mc_logo2,bmc.mc_logo3," + "bmc.mc_logo4,bmc.mc_avg_scores,bmc.mc_evaliation_count,bmc.mc_sale_count,bmc.mc_fix_tel "
				+ "from beiker_merchant_profile bmc left join  beiker_merchant bm on bm.merchantid=bmc.merchantid " + "where bm.parentid=0 and bm.merchantid=?";
		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { merchantId });
		if (list == null || list.size() == 0)
			return null;
		MerchantForm merchantForm = new MerchantForm();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			// 品牌的ID
			Long merchantid = (Long) map.get("merchantid");
			merchantForm.setId(String.valueOf(merchantid));
			/**
			 * 品牌下商品的虚拟购买次数
			 */
			int merVirtualCount = (Integer) map.get("virtualcount");
			merchantForm.setVirtualCount(merVirtualCount);

			String logo1 = (String) map.get("mc_logo1");
			String logo2 = (String) map.get("mc_logo2");
			String logo4 = (String) map.get("mc_logo4");
			Float avgscore = (Float) map.get("mc_avg_scores");
			Integer evaliati = (Integer) map.get("mc_evaliation_count");
			Integer saleCoun = (Integer) map.get("mc_sale_count");
			String fixtel = (String) map.get("mc_fix_tel");

			merchantForm.setLogo1(logo1);
			merchantForm.setLogo2(logo2);
			merchantForm.setLogoTitle(logo4);
			merchantForm.setAvgscores(String.valueOf(avgscore));
			merchantForm.setEvaluation_count(String.valueOf(evaliati));
			merchantForm.setSalescount(String.valueOf(saleCoun));
			merchantForm.setTel(fixtel);

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
			String city = (String) map.get("city");
			merchantForm.setCity(city);
		}
		return merchantForm;
	}

	@Override
	public List<MerchantForm> getChildMerchnatById(Long merchantId) {
		String sql = "select m.merchantname as merchantname,m.merchantid as id,m.addr as addr,m.latitude as latitude,m.tel as tel,m.buinesstime as buinesstime from  beiker_merchant m where m.parentid=? ";

		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { merchantId });
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
			// String city=(String) map.get("city");
			// merchantForm.setCity(city);
			listForm.add(merchantForm);
		}
		return listForm;
	}

	@Override
	public List<MerchantForm> getChildMerchnatById(Long merchantId, int start, int end) {

		// modify by qiaowb 2011-12-30 只查询曾经有过商品的分店地址
		String sql = "select distinct m.merchantname as merchantname,m.merchantid as id,m.addr as addr,m.latitude as latitude,m.tel as tel,m.buinesstime as buinesstime,m.city as city, m.is_support_takeaway as is_support_takeaway, m.is_support_online_meal as is_support_online_meal,m.environment,m.capacity,m.otherservice from  beiker_merchant m  where m.parentid=? order by m.sort_number asc,m.merchantid desc limit "
				+ start + "," + end;

		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { merchantId });
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
			
			if(org.apache.commons.lang.StringUtils.isNotBlank(environment)){
				if(environment.indexOf(",") !=-1){
					environment = environment.trim().replaceAll(",","、");
				}
				if(org.apache.commons.lang.StringUtils.isNotBlank(capacity) || org.apache.commons.lang.StringUtils.isNotBlank(otherservice))
					environment += "、";
			}else{
				environment = "";
			}
			
			if(org.apache.commons.lang.StringUtils.isNotBlank(capacity)){
				capacity = capacity.trim();
				if(org.apache.commons.lang.StringUtils.isNotBlank(otherservice))
					capacity += "、";
			}else{
				capacity = "";
			}
			
			if(org.apache.commons.lang.StringUtils.isNotBlank(otherservice)){
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

	@Override
	public int getChildMerchantCount(Long merchantId) {
		// modify by qiaowb 2011-12-30 只查询曾经有过商品的分店地址
		String sql = "select count(distinct m.merchantid) as count from beiker_merchant m where m.parentid=?";
		List list = this.getSimpleJdbcTemplate().queryForList(sql, merchantId);
		if (list == null || list.size() == 0)
			return 0;
		Map map = (Map) list.get(0);
		Long count = (Long) map.get("count");
		return Integer.valueOf(count + "");

	}

	@Override
	public List<MerchantForm> getMerchantFormByCouponId(Integer couponId, int start, int end) {
		String sql = "select m.merchantname as merchantname,m.merchantid as id,m.addr as addr,m.latitude as latitude,m.tel as tel,m.buinesstime as buinesstime,m.city as city from beiker_coupon_merchant bcm left join beiker_merchant m on bcm.merchantid=m.merchantid where bcm.couponid=? limit ?,?";
		List list = this.getSimpleJdbcTemplate().queryForList(sql, couponId, start, end);
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
			listForm.add(merchantForm);
		}
		return listForm;
	}

	@Override
	public int getMerchantFormByCouponCount(Integer couponId) {
		String sql = "select count(m.merchantid) as count from beiker_coupon_merchant bcm left join beiker_merchant m on bcm.merchantid=m.merchantid where bcm.couponid=?";
		List list = this.getSimpleJdbcTemplate().queryForList(sql, couponId);
		if (list == null || list.size() == 0)
			return 0;

		Map map = (Map) list.get(0);
		Long count = (Long) map.get("count");
		return Integer.parseInt(count + "");

	}

	/**
	 * 根据商品ID查询商家 add by wenhua.cheng
	 * @param merchantId
	 * @param propertyname
	 * @return
	 */
	@Override
	public Map<String, String> getMerchantByGoodsId(Long goodsId) {
		String sql = "select m.merchantname as merchantname,m.addr as addr,m.merchantid as merchantid from beiker_merchant m  left join  beiker_goods_merchant gm on m.merchantid=gm.merchantid  where   gm.goodsid=? ";
		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { goodsId });
		if (list == null || list.size() == 0)
			return null;

		Map<String, String> rspMap = (Map<String, String>) list.get(0);

		return rspMap;

	}

	/**
	 * 根据商品ID查询商家预约电话和地址 add by renli.yu
	 * @param merchantId
	 * @param propertyname
	 * @return
	 */
	@Override
	public List<PayInfoParam> getMerchantsByGoodsId(Long goodsId) {
		String sql = "select m.merchantname as merchantname,m.addr as addr,m.merchantid as merchantid ,m.tel as tel from  beiker_merchant  m  left join  beiker_goods_merchant gm on m.merchantid=gm.merchantid  where parentId!=0 and gm.goodsid=? ";
		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { goodsId });
		if (list == null || list.size() == 0)
			return null;
		List<PayInfoParam> pipList = new ArrayList<PayInfoParam>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, String> rspMap = (Map<String, String>) list.get(i);
			PayInfoParam pip = new PayInfoParam();
			pip.setMertOrdTel(rspMap.get("tel"));
			pip.setMerchantAddr(rspMap.get("addr"));
			pip.setMerchantName(rspMap.get("merchantname"));
			pipList.add(pip);
		}
		return pipList;

	}

	/**
	 * 根据商户ID查询预约电话 add by wenhua.cheng
	 * @param merchantId
	 * @param propertyname
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MerchantProfileType getMerchantProfileTypeByGoodsId(Long goodsId, String propertyname) {
		// author wenjie.mai sql优化
		String sql = "select bmp.merchantid,bmp.mc_logo1,bmp.mc_logo2,bmp.mc_logo3,bmp.mc_logo4,bmp.mc_avg_scores," + "bmp.mc_evaliation_count,bmp.mc_sale_count,bmp.mc_fix_tel from beiker_merchant_profile bmp " + "left join  beiker_goods_merchant gm on bmp.merchantid=gm.merchantid where gm.goodsid= ? ";
		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { goodsId });
		if (list == null || list.size() == 0)
			return null;
		MerchantProfileType pt = new MerchantProfileType();
		for (Object object : list) {
			Map map = (Map) object;

			if (StringUtils.validNull(propertyname)) {// 查询参数不为空
				if (propertyname.equals("mc_logo1")) {
					pt.setPropertyvalue((String) map.get("mc_logo1"));
				} else if (propertyname.equals("mc_logo2")) {
					pt.setPropertyvalue((String) map.get("mc_logo2"));
				} else if (propertyname.equals("mc_logo3")) {
					pt.setPropertyvalue((String) map.get("mc_logo3"));
				} else if (propertyname.equals("mc_logo4")) {
					pt.setPropertyvalue((String) map.get("mc_logo4"));
				} else if (propertyname.equals("mc_avg_scores")) {
					pt.setPropertyvalue(String.valueOf(map.get("mc_avg_scores")));
				} else if (propertyname.equals("mc_evaliation_count")) {
					pt.setPropertyvalue(String.valueOf(map.get("mc_evaliation_count")));
				} else if (propertyname.equals("mc_sale_count")) {
					pt.setPropertyvalue(String.valueOf(map.get("mc_sale_count")));
				} else if (propertyname.equals("mc_fix_tel")) {
					pt.setPropertyvalue((String) map.get("mc_fix_tel"));
				}
				pt.setPropertyname(propertyname);
			}
			Long id = (Long) map.get("id");
			pt.setId(id);
		}
		return pt;
	}

	/**
	 * 
	 */

	/*
	 * public Long addCommentCount() {
	 * 
	 * }
	 */
	/**
	 * 加入单个商品评价 /没有全字段取出需求且进入此方法必须判空 add by wenhua.cheng /
	 * @param userId
	 * @param merchantid
	 * @param commentPoint
	 * @param commentContent
	 * @return
	 */
	@Override
	public Long addEvaluation(final Long userId, final Long merchantid, final double commentPoint, final String commentContent, final Long goodsId, final Long trxGoodsId) {

		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (userId == null || merchantid == null || goodsId == null) {

			throw new IllegalArgumentException("userId and merchantid and goods_id  not null");
		}

		final String istSql = "insert into beiker_merchantevaluation(user_id,merchantid,evaluationscore,evaluationcontent,goods_id,trx_goods_id) value(?,?,?,?,?,?)";

		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(istSql, new String[] { "user_id", "merchantid", "evaluationscore", "evaluationcontent", "goods_id", "trx_goods_id" });

				ps.setLong(1, userId);
				ps.setLong(2, merchantid);
				ps.setFloat(3, (float) commentPoint);
				ps.setString(4, commentContent);
				ps.setLong(5, goodsId);
				ps.setLong(6, trxGoodsId);
				return ps;
			}

		}, keyHolder);
		Long commentId = keyHolder.getKey().longValue();
		return commentId;

	}

	/**
	 * 根据goods读取评价信息
	 * @param goodsId
	 * @return
	 */
	@Override
	public Map<String, String> getEvaluationByGoodsId(Long goodsId, Long trxGoodsId) {
		String sql = "select m.evaluationscore as evaluationscore   from beiker_merchantevaluation  m where m.goods_id=? and m.trx_goods_id=?";
		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { goodsId, trxGoodsId });
		Float evaluationscore = null;

		if (list != null && list.size() > 0) {
			Map map = (Map) list.get(0);
			evaluationscore = (Float) map.get("evaluationscore");

		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("evaluationscore", evaluationscore + "");

		return map;

	}

	@Override
	public void updateMerchantSalesCount(Long merchantId) {
		MerchantProfileType merchantProfileType = getMerchantProfileTypeByMerchantId(merchantId, "mc_evaliation_count");
		String propertyvalue = merchantProfileType.getPropertyvalue();
		int count = 0;
		if (propertyvalue != null && !"".equals(propertyvalue)) {
			count = Integer.parseInt(propertyvalue) + 1;
		} else {
			count = 1;
		}
		// author wenjie.mai sql优化
		String sql = "update beiker_merchant_profile bm set bm.mc_evaliation_count = ? where bm.merchantid = ?";
		this.getJdbcTemplate().update(sql, new Object[] { count + "", merchantId });
	}

	/**
	 * 根据商户id获取平均分数对象
	 * @param merchantId
	 *        商户id
	 * @param 返回商家打分
	 */
	@Override
	public MerchantEvaluation findAvgScoreByMerchantId(Long merchantId) {
		String sql = "SELECT AVG(evaluationscore) AS score FROM beiker_merchantevaluation WHERE merchantid = ?";
		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { merchantId });
		if (list == null) {
			return null;
		}
		String score = String.valueOf(((Map) list.get(0)).get("score"));
		MerchantEvaluation mel = new MerchantEvaluation();
		mel.setEvaluationscore(Double.parseDouble(score));
		return mel;
	}

	/**
	 * 更新商户平均分
	 * @param score
	 *        分数
	 * @param profileName
	 *        平均分名称
	 * @param merchantId
	 *        商户id
	 */
	@Override
	public void updateMerchantAvgScore(Double score, String profileName, Long merchantId) {
		// author wenjie.mai sql优化
		String sql = "update beiker_merchant_profile set ";
		String Mname = "";
		if (StringUtils.validNull(profileName)) {// 查询参数不为空
			if (profileName.equals("mc_logo1")) {
				Mname = "mc_logo1";
			} else if (profileName.equals("mc_logo2")) {
				Mname = "mc_logo2";
			} else if (profileName.equals("mc_logo3")) {
				Mname = "mc_logo3";
			} else if (profileName.equals("mc_logo4")) {
				Mname = "mc_logo4";
			} else if (profileName.equals("mc_avg_scores")) {
				Mname = "mc_avg_scores";
			} else if (profileName.equals("mc_evaliation_count")) {
				Mname = "mc_evaliation_count";
			} else if (profileName.equals("mc_sale_count")) {
				Mname = "mc_sale_count";
			} else if (profileName.equals("mc_fix_tel")) {
				Mname = "mc_fix_tel";
			}
			sql = sql + Mname + " =? where merchantid = ?";
			logger.info("sql....." + sql);
			getSimpleJdbcTemplate().update(sql, new Object[] { score, merchantId });
		}
	}

	/**
	 * 插入商户平均分
	 * @param score
	 *        分数
	 * @param profileName
	 *        平均分名称
	 * @param merchantId
	 *        商户id
	 */
	@Override
	public void insertMerchantAvgScore(Double score, String profileName, Long merchantId) {
		// author wenjie.mai sql优化
		String sql = "insert into beiker_merchant_profile(merchantid,mc_avg_scores) values(?,?)";
		List<Object> list = new ArrayList<Object>();
		list.add(merchantId);
		list.add(score);
		getSimpleJdbcTemplate().update(sql, list.toArray(new Object[] {}));
	}

	/**
	 * 检查商户在beiker_merchant_profile是否存在平均分记录
	 * @param profileName
	 *        平均分名称
	 * @param merchantId
	 *        商户id
	 * @return 存在返回1，不存在返回0
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int checkMerchantAcgScore(String profileName, Long merchantId) {
		// author wenjie.mai sql优化
		String sql = "select bmp.merchantid,bmp.mc_logo1,bmp.mc_logo2,bmp.mc_logo3,bmp.mc_logo4,bmp.mc_avg_scores," + "bmp.mc_evaliation_count,bmp.mc_sale_count,bmp.mc_fix_tel from beiker_merchant_profile bmp " + "where bmp.merchantid = ?";
		List rs = this.getJdbcTemplate().queryForList(sql, new Object[] { merchantId });
		if (rs == null || rs.size() == 0) {
			return 0;
		}
		return 1;
	}

	/**
	 * 补充说明：
	 * 该方法的sql 添加了 bm.virtualcount 字段, 用于显示品牌下的商品的虚拟购买次数
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MerchantForm> getMerchantSalesCount(String idsCourse) {
		// author wenjie.mai sql优化
		String sql = "select bmc.merchantid,bmc.mc_sale_count,bm.virtualcount from beiker_merchant_profile bmc " + "left join beiker_merchant bm on bmc.merchantid=bm.merchantid where bm.merchantid in (" + idsCourse + ")";
		;
		List list = this.getJdbcTemplate().queryForList(sql);
		List<MerchantForm> listForm = new ArrayList<MerchantForm>();
		if (list == null || list.size() == 0)
			return null;
		for (int i = 0; i < list.size(); i++) {
			MerchantForm mf = new MerchantForm();
			Map map = (Map) list.get(i);
			Long merchantId = (Long) map.get("merchantid");
			mf.setId(merchantId + "");
			Integer saleCount = (Integer) map.get("mc_sale_count");
			mf.setSalescount(String.valueOf(saleCount));
			/**
			 * 补充说明： 此处为该品牌下商品的购买次数
			 */
			int merVirtualCount = (Integer) map.get("virtualcount");
			mf.setVirtualCount(merVirtualCount);
			listForm.add(mf);
		}
		return listForm;
	}

	/**
	 * 根据品牌ID, 来获取该 品牌下商品的虚拟购买次数
	 * Add by zx.liu
	 */
	@Override
	public Long getMerchantVirtualCountById(Long merchantId) {
		String sql = " SELECT virtualcount FROM beiker_merchant WHERE merchantid=? ";
		Long merVirtualCount = this.getSimpleJdbcTemplate().queryForLong(sql, merchantId);
		if (null == merVirtualCount) {
			return 0L;
		}
		return merVirtualCount;
	}

	@Override
	public List<Merchant> getAllMerchantList() {
		String sql = "select * from beiker_merchant where parentid=0";
		List<Merchant> merchantList = getJdbcTemplate().query(sql, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Merchant merchant = new Merchant();
				merchant.setMerchantname(rs.getString("merchantname"));
				merchant.setMerchantintroduction(rs.getString("merchantintroduction"));
				merchant.setId(rs.getLong("merchantid"));
				return merchant;
			}
		});
		return merchantList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int searchBrandCatlogCount(AbstractCatlog abstractLog) {
		MerchantCatlog merchantCatlog = (MerchantCatlog) abstractLog;
		Long cityid = merchantCatlog.getCityid();
		StringBuilder ids = new StringBuilder();
		Long count = 0L;
		String whereCourse = merchantCatlog.getSearchCourse();
		String sql = "select brandid,goodid from beiker_catlog_good ";
		if (!merchantCatlog.isNull()) {
			whereCourse = whereCourse.substring(0, whereCourse.indexOf("by brandid") + 10);
			sql += " where isavaliable='1' and area_id=" + cityid + " and " + whereCourse;
		} else {
			sql += " where isavaliable='1' and area_id=" + cityid + " " + whereCourse;
		}
		logger.info("查询有效品牌SQL...." + sql);
		List idsList = this.getJdbcTemplate().queryForList(sql);
		if (idsList == null || idsList.size() == 0)
			return 0;
		StringBuilder buildList = new StringBuilder(); // 品牌ID
		StringBuilder goodsList = new StringBuilder(); // 商品ID
		for (Object obj : idsList) {
			Map mx = (Map) obj;
			Long brandid = (Long) mx.get("brandid");
			buildList.append(brandid).append(",");
		}

		String brandids = buildList.substring(0, buildList.lastIndexOf(",")); // 获得所有有效品牌ID

		String query_goodsid = "select bgm.goodsid from beiker_goods_merchant bgm where bgm.merchantid in (" + brandids + ")";
		logger.info("查询品牌所属的商品SQL...." + query_goodsid);
		List merchantList = this.getJdbcTemplate().queryForList(query_goodsid);
		if (merchantList == null || merchantList.size() == 0) {
			return 0;
		}

		for (Object mer : merchantList) {
			Map mp = (Map) mer;
			Long gods = (Long) mp.get("goodsid");
			goodsList.append(gods).append(",");
		}

		String goodids = goodsList.substring(0, goodsList.lastIndexOf(",")); // 有效品牌对应的所有商品ID
		String query_goods = "select bg.couponcash,bg.goodsid  from beiker_goods bg where bg.isavaliable = '1' and bg.couponcash = '0' and bg.goodsid  in (" + goodids + ")";
		// logger.info("查询符合的商品SQL...." + query_goods);
		List goodList = this.getJdbcTemplate().queryForList(query_goods); // 查询除现金券以外的在售商品
		if (goodList == null || goodList.size() == 0) {
			return 0;
		}
		for (Object god : goodList) {
			Map gop = (Map) god;
			Long goodid = (Long) gop.get("goodsid");
			ids.append(goodid).append(",");
		}
		String goods = ids.substring(0, ids.lastIndexOf(","));
		String query_brand = "select count(distinct(bcg.brandid)) as count from beiker_catlog_good bcg  " + " where  bcg.goodid in (" + goods + ") ";
		if (!merchantCatlog.isNull()) {
			whereCourse = whereCourse.substring(0, whereCourse.indexOf("group by brandid"));
			query_brand += "  and bcg.area_id=" + cityid + " and " + whereCourse;
		} else {
			query_brand += "  and bcg.area_id=" + cityid;
		}

		logger.info("统计有效品牌数量SQL...." + query_brand);
		List brandList = this.getJdbcTemplate().queryForList(query_brand);
		if (brandList == null || brandList.size() == 0)
			return 0;

		for (Object object : brandList) {
			Map map = (Map) object;
			count = (Long) map.get("count");
		}
		return Integer.parseInt(count + "");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> searchBrandCatlog(AbstractCatlog abstractLog, int start, int end) {
		MerchantCatlog merchantCatlog = (MerchantCatlog) abstractLog;
		StringBuilder idbuilder = new StringBuilder();
		String ids = "";
		Long cityid = merchantCatlog.getCityid();
		String whereCourse = merchantCatlog.getSearchCourse();
		String query_id = "select brandid,goodid from beiker_catlog_good ";
		if (!merchantCatlog.isNull()) {
			query_id += " where isavaliable='1' and area_id=" + cityid + " and " + whereCourse;
		} else {
			query_id += " where isavaliable='1' and area_id=" + cityid + " " + whereCourse;
		}
		logger.info("***查询有效品牌SQL...." + query_id);
		List goodList = this.getJdbcTemplate().queryForList(query_id);
		if (goodList == null || goodList.size() == 0) {
			return null;
		}

		StringBuilder buildList = new StringBuilder(); // 品牌ID
		StringBuilder goodsList = new StringBuilder(); // 商品ID
		for (Object obj : goodList) {
			Map mx = (Map) obj;
			Long brandid = (Long) mx.get("brandid");
			buildList.append(brandid).append(",");
		}

		String brandids = buildList.substring(0, buildList.lastIndexOf(",")); // 获得所有有效品牌ID

		String query_goodsid = "select bgm.goodsid from beiker_goods_merchant bgm where bgm.merchantid in (" + brandids + ")";
		logger.info("****查询品牌所属的商品SQL...." + query_goodsid);
		List merchantList = this.getJdbcTemplate().queryForList(query_goodsid);
		if (merchantList == null || merchantList.size() == 0) {
			return null;
		}

		for (Object mer : merchantList) {
			Map mp = (Map) mer;
			Long gods = (Long) mp.get("goodsid");
			goodsList.append(gods).append(",");
		}

		String goodids = goodsList.substring(0, goodsList.lastIndexOf(",")); // 有效品牌对应的所有商品ID
		String query_goods = "select bg.couponcash,bg.goodsid  from beiker_goods bg where bg.isavaliable = '1' and bg.couponcash = '0' and bg.goodsid  in (" + goodids + ")";
		List effctGood = this.getJdbcTemplate().queryForList(query_goods); // 查询除现金券以外的在售商品

		if (effctGood == null || effctGood.size() == 0) {
			return null;
		}

		for (Object god : effctGood) {
			Map gop = (Map) god;
			Long goodid = (Long) gop.get("goodsid");
			idbuilder.append(goodid).append(",");
		}

		ids = idbuilder.substring(0, idbuilder.lastIndexOf(","));

		String query_brand = "select distinct(bcg.brandid)  from beiker_catlog_good bcg " + "left join beiker_merchant_profile bmp on bmp.merchantid = bcg.brandid";
		if (!merchantCatlog.isNull()) {
			query_brand += " where  bcg.goodid  in(" + ids + ")  and " + "bcg.area_id=" + cityid + " and " + whereCourse;
		} else {
			query_brand += " where  bcg.goodid  in(" + ids + ")  and " + "bcg.area_id=" + cityid;
		}
		query_brand += " ORDER BY CONVERT(bmp.mc_sale_count,UNSIGNED) DESC";
		query_brand += " limit " + start + "," + end;
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
	}

	@Override
	public Long getMerchantIdByDomainName(String domainName) {
		String sql = "SELECT merchantid FROM beiker_merchant WHERE domainname=? limit 1";
		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { domainName });
		if (list == null || list.size() == 0)
			return null;
		Map map = (Map) list.get(0);
		Long merchantId = (Long) map.get("merchantid");
		return merchantId;

	}

	@Override
	public boolean checkMerchantStatus(Long merchantid) {
		try {
			// List<Long> allid = getAllMerchantID();
			// String ids = StringUtils.arrayToString(allid.toArray(), ",");
			// String sql =
			// "SELECT count(bm.merchantid) FROM beiker_merchant bm JOIN beiker_goods_merchant bgm ON bgm.merchantid=bm.merchantid join beiker_goods bg on bg.goodsid=bgm.goodsid WHERE bm.parentId=0 AND bgm.merchantid IN("
			// + ids + ") AND bm.merchantid=? and bg.isavaliable=1";
			// String temp =
			// "SELECT count(bm.merchantid) FROM beiker_merchant bm JOIN beiker_goods_merchant bgm ON bgm.merchantid=bm.merchantid join beiker_goods bg on bg.goodsid=bgm.goodsid WHERE bm.parentId=0 AND bgm.merchantid IN("
			// + StringUtils.arrayToString(allid.toArray(), ",") +
			// ") AND bm.merchantid=" +merchantid + " and bg.isavaliable=1";
			String sql = "SELECT COUNT(bgm.id) FROM beiker_goods_merchant bgm JOIN beiker_goods bg ON bgm.goodsid=bg.goodsid WHERE bg.isavaliable=1 AND bgm.merchantid=?";
			int no = getJdbcTemplate().queryForInt(sql, new Object[] { merchantid });
			if (no > 0) {
				return true;
			}
			return false;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Long> getAllMerchantID() {
		String sql = "SELECT DISTINCT merchantid FROM beiker_goods_merchant";
		List list = getJdbcTemplate().queryForList(sql);
		List<Long> merchantIDList = new ArrayList<Long>();
		for (int i = 0, size = list.size(); i < size; i++) {
			merchantIDList.add((Long) ((Map) list.get(i)).get("merchantid"));
		}
		return merchantIDList;
	}

	@Override
	public Merchant getMerchantNameById(Long id) {

		String sql = "SELECT merchantid as id, merchantname as merchantname FROM beiker_merchant  WHERE merchantid = ?";

		Merchant Merchant = this.getSimpleJdbcTemplate().queryForObject(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(Merchant.class), id);

		return Merchant;
	}

	@Override
	public List<MerchantForm>  getDiancaiChildMerchant(Long branchid) {
		String sql = "select distinct m.merchantname as merchantname,m.merchantid as id,m.addr as addr,m.latitude as latitude,m.tel as tel,m.buinesstime as buinesstime,m.city as city, m.is_support_takeaway as is_support_takeaway, m.is_support_online_meal as is_support_online_meal from  beiker_merchant m  where m.merchantid=? order by m.sort_number asc,m.merchantid desc";

		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { branchid });
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

			listForm.add(merchantForm);
		}
		return listForm;
	}
}
