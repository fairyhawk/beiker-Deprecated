package com.beike.dao.impl.goods;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.goods.GoodsDao;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.goods.Goods;
import com.beike.entity.goods.GoodsProfile;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;

/**
 * <p>
 * Title:商品数据库实现
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
 * @date May 16, 2011
 * @author ye.tian
 * @version 1.0
 */
@Repository("goodsDao")
public class GoodsDaoImpl extends GenericDaoImpl<Goods, Long> implements GoodsDao {
	private final Log logger = LogFactory.getLog(GoodsDaoImpl.class);

	/**
	 * @param id
	 *        0:下线 1:可用
	 * @return 返回商品可用状态
	 */
	@Override
	public int getLuceneGoodsById(Long id) {
		String sql = "select isavaliable from beiker_goods where goodsId=?";
		int status = 0;
		try {
			status = getSimpleJdbcTemplate().queryForInt(sql, id);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return status;

	}

	@Override
	public void addGoodsProfile(GoodsProfile goodsProfile) {
		String sql = "insert into  beiker_goods_profile (sales_count,goodsid) values(?,?) ";
		this.getSimpleJdbcTemplate().update(sql, goodsProfile.getProfileValue(), goodsProfile.getGoodsId());
	}

	@Override
	public List<Map<String, String>> getTopGoods(int count) {
		String sql = "select * from beiker_goods  limit 0,?";
		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { count });
		if (list == null || list.size() == 0) {
			return null;
		}

		List<Map<String, String>> listParam = new ArrayList<Map<String, String>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, String> mapValue = new HashMap<String, String>();
			Map map = (Map) list.get(i);
			Long goodsId = (Long) map.get("goodsid");
			String goodsName = (String) map.get("goodsname");
			BigDecimal rebatePrice = (BigDecimal) map.get("rebatePrice");
			BigDecimal currentPrice = (BigDecimal) map.get("currentPrice");
			String logo2 = (String) map.get("logo2");

			mapValue.put("goodsid", goodsId + "");
			mapValue.put("goodsname", goodsName);
			mapValue.put("rebatePrice", rebatePrice + "");
			mapValue.put("currentPrice", currentPrice + "");
			mapValue.put("logo2", logo2);
			listParam.add(mapValue);
		}
		return listParam;

	}

	@Override
	public Long addGood(final GoodsForm form) {
		final String sql = "insert into beiker_goods " + "(goodsname,sourcePrice,currentPrice,payPrice,offerPrice,rebatePrice,dividePrice,discount,maxcount,endTime,city,startTime,isavaliable,logo1,logo2,logo3)" + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		// this.getSimpleJdbcTemplate().update(sql,
		// form.getGoodsname(),form.getSourcePrice(),form.getCurrentPrice(),form.getPayPrice(),form.getOfferPrice(),form.getRebatePrice(),form.getDividePrice(),form.getDiscount(),form.getMaxcount(),form.getEndTime(),form.getCity(),form.getStartTime(),form.getIsavaliable());

		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, new String[] { "goodsname", "sourcePrice", "currentPrice", "payPrice", "offerPrice", "rebatePrice", "dividePrice", "discount", "maxcount", "endTime", "city", "startTime", "isavaliable", "logo1", "logo2", "logo3" });
				ps.setString(1, form.getGoodsname());
				ps.setDouble(2, form.getSourcePrice());
				ps.setDouble(3, form.getCurrentPrice());

				ps.setDouble(4, form.getPayPrice());

				ps.setDouble(5, form.getOfferPrice());
				ps.setDouble(6, form.getRebatePrice());
				ps.setDouble(7, form.getDividePrice());
				ps.setDouble(8, form.getDiscount());
				ps.setInt(9, form.getMaxcount());

				ps.setDate(10, new java.sql.Date(form.getEndTime().getTime()));
				ps.setString(11, form.getCity());
				ps.setDate(12, new java.sql.Date(form.getStartTime().getTime()));
				ps.setInt(13, form.getIsavaliable());
				ps.setString(14, form.getLogo1());
				ps.setString(15, form.getLogo2());
				ps.setString(16, form.getLogo3());

				return ps;
			}

		}, keyHolder);

		// 获得 插入 id
		Long goodId = keyHolder.getKey().longValue();
		return goodId;
	}

	/**
	 * 补充说明：by zx.liu
	 * 此处的sql 语句添加了一个属性 virtual_count（商品的虚拟购买数量）
	 */
	@Override
	public Goods getGoodsDaoById(Long id) {
		String sql = " select goodsId,goodsname,is_scheduled,city,sourcePrice,currentPrice,payPrice,offerPrice,rebatePrice,dividePrice, " + " discount,maxcount,endTime,startTime,isavaliable,logo1,logo2,logo3,goods_title,qpsharepic, " + " guest_id,order_lose_abs_date,order_lose_date, virtual_count,goods_single_count,isRefund,kindlywarnings,send_rules,isadvance,iscard, is_menu "
				+ " from beiker_goods where goodsId=? ";
		Goods goods = null;

		try {
			logger.debug("getGoodsDaoById,SQL=" + sql + id);
			goods = this.getSimpleJdbcTemplate().queryForObject(sql, new RowMapperImpl(), id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return goods;

	}

	protected class RowMapperImpl implements ParameterizedRowMapper<Goods> {
		@Override
		public Goods mapRow(ResultSet rs, int rowNum) throws SQLException {
			Goods goods = new Goods();
			Long goodsId = rs.getLong("goodsId");
			goods.setGoodsId(goodsId);

			String goodsName = rs.getString("goodsname");
			goods.setGoodsname(goodsName);

			String city = rs.getString("city");
			goods.setCity(city);

			Double sourcePrice = rs.getDouble("sourcePrice");
			goods.setSourcePrice(sourcePrice);

			Double currentPrice = rs.getDouble("currentPrice");
			goods.setCurrentPrice(currentPrice);

			Double payPrice = rs.getDouble("payPrice");
			goods.setPayPrice(payPrice);

			Double offerPrice = rs.getDouble("offerPrice");
			goods.setOfferPrice(offerPrice);

			Double rebatePrice = rs.getDouble("rebatePrice");
			goods.setRebatePrice(rebatePrice);

			Double dividePrice = rs.getDouble("dividePrice");
			goods.setDividePrice(dividePrice);

			Double discount = rs.getDouble("discount");
			goods.setDiscount(discount);

			int maxcount = rs.getInt("maxcount");
			goods.setMaxcount(maxcount);

			Date endTime = rs.getDate("endTime");
			goods.setEndTime(endTime);

			Date startTime = rs.getDate("startTime");
			goods.setStartTime(startTime);

			int isavaliable = rs.getInt("isavaliable");
			goods.setIsavaliable(isavaliable);

			goods.setLogo1(rs.getString("logo1"));
			goods.setLogo2(rs.getString("logo2"));
			goods.setLogo3(rs.getString("logo3"));
			goods.setGoodsTitle(rs.getString("goods_title"));
			goods.setQpsharepic(rs.getString("qpsharepic"));
			// add by wenhua.cheng
			goods.setGuestId(rs.getLong("guest_id"));
			goods.setOrderLoseAbsDate(rs.getLong("order_lose_abs_date"));
			goods.setOrderLoseDate(rs.getTimestamp("order_lose_date"));

			/**
			 * 补充语句：
			 * 此处的语句添加了一个属性 virtualCount（商品的虚拟购买数量）
			 */
			goods.setVirtualCount(rs.getInt("virtual_count"));

			goods.setGoodsSingleCount(rs.getInt("goods_single_count"));
			// 是否自动退款 0:不自动退款 1:自动退款
			goods.setIsRefund(rs.getInt("isRefund"));

			goods.setSendRules(rs.getInt("send_rules"));

			// 温馨提示
			String kindlywarnings = rs.getString("kindlywarnings");
			goods.setKindlywarnings(kindlywarnings);
			goods.setIsadvance(rs.getInt("isadvance"));
			goods.setIsCard(rs.getString("iscard"));
			//是否支持酒店预订
			if (rs.getString("is_scheduled") != null) {
				goods.setIs_scheduled(rs.getString("is_scheduled"));
			}
			goods.setIsMenu(rs.getInt("is_menu"));

			return goods;
		}
	}

	@Override
	public Map<String, String> getMerchantByGoodId(Long goodId) {
		String sql = "select m.merchantname as merchantname,m.merchantid as id,m.sevenrefound as sevenrefound,m.overrefound as overrefound,m.quality as quality from  beiker_merchant m  left join beiker_goods_merchant bm on m.merchantid = bm.merchantid where bm.goodsid=? and m.parentid=0";
		List list = getJdbcTemplate().queryForList(sql, new Object[] { goodId });
		Map<String, String> map = new HashMap<String, String>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map valueMap = (Map) list.get(i);
				Long merchantId = (Long) valueMap.get("id");
				String merchantname = (String) valueMap.get("merchantname");
				Long sevenrefound = (Long) valueMap.get("sevenrefound");
				Long overrefound = (Long) valueMap.get("overrefound");
				Long quality = (Long) valueMap.get("quality");
				map.put("id", merchantId + "");
				map.put("merchantname", merchantname);
				map.put("sevenrefound", sevenrefound + "");
				map.put("overrefound", overrefound + "");
				map.put("quality", quality + "");
			}
		}
		return map;

	}

	/**
	 * 查询商品个人限购信息（ 交易相关，已挪到GoodsSoaDao下.代码解耦）若此字段相关有改动，请同步通知交易组。
	 * @param goodsId
	 *        add by wenhua.cheng
	 * @return
	 */
	@Override
	public Map<Long, Integer> getSingleCount(Long goodsId) {

		String sql = "select goods_single_count as singleCount from beiker_goods where  goodsid=?";

		List list = getJdbcTemplate().queryForList(sql, new Object[] { goodsId });

		Map<Long, Integer> map = new HashMap<Long, Integer>();
		if (list != null && list.size() > 0) {

			Map valueMap = (Map) list.get(0);
			Integer singleCount = (Integer) valueMap.get("singleCount");

			map.put(goodsId, singleCount);
		}

		return map;

	}

	@Override
	public int getAllGoodsMerchantCount(Long goodId) {
		String sql = "select count(m.merchantid) as count from  beiker_merchant m  left join beiker_goods_merchant bm on m.merchantid = bm.merchantid left join  beiker_goods b on bm.goodsid=b.goodsid where b.goodsid=? and m.parentid !=0";

		List list = getJdbcTemplate().queryForList(sql, new Object[] { goodId });

		if (list == null || list.size() == 0)
			return 0;

		Map map = (Map) list.get(0);

		Long count = (Long) map.get("count");

		return Integer.parseInt(count + "");
	}

	@Override
	public List<Map<String, String>> getAllGoodsMerchant(Long goodId, int start, int end) {
		String sql = "select m.merchantname as merchantname,m.merchantid as id,m.addr as addr,m.latitude as latitude,m.tel as tel,m.buinesstime as buinesstime,m.city as city,m.is_support_takeaway as is_support_takeaway, m.is_support_online_meal as is_support_online_meal,m.environment,m.capacity,m.otherservice from  beiker_merchant m  left join beiker_goods_merchant bm on m.merchantid = bm.merchantid left join  beiker_goods b on bm.goodsid=b.goodsid where b.goodsid=? and m.parentid !=0 order by m.sort_number asc,m.merchantid desc limit ?,?";
		List list = getJdbcTemplate().queryForList(sql, new Object[] { goodId, start, end });
		List<Map<String, String>> listvalue = new ArrayList<Map<String, String>>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map valueMap = (Map) list.get(i);
				Map<String, String> map = new HashMap<String, String>();
				Long merchantId = (Long) valueMap.get("id");
				String merchantname = (String) valueMap.get("merchantname");
				String addr = (String) valueMap.get("addr");
				String tel = (String) valueMap.get("tel");
				String buinesstime = (String) valueMap.get("buinesstime");
				String latitude = (String) valueMap.get("latitude");
				String city = (String) valueMap.get("city");

				map.put("id", merchantId + "");
				map.put("merchantname", merchantname);
				map.put("latitude", latitude);
				map.put("tel", tel);
				map.put("addr", addr);
				map.put("buinesstime", buinesstime);
				map.put("city", city);

				map.put("is_support_takeaway", valueMap.get("is_support_takeaway").toString());
				map.put("is_support_online_meal", valueMap.get("is_support_online_meal").toString());
				
				String environment = (String) valueMap.get("environment");
				String capacity    = (String) valueMap.get("capacity");
				String otherservice= (String) valueMap.get("otherservice");
				
				map.put("environment",environment);
				map.put("capacity",capacity);
				map.put("otherservice",otherservice);

				listvalue.add(map);
			}
		}

		return listvalue;
	}

	@Override
	public GoodsProfile getGoodsProfile(Long goodsId, String profileName) {
		String sql = "select bgp.goodsid,bgp.sales_count,bgp.detailpageurl from beiker_goods_profile bgp where bgp.goodsid=?";
		List list = this.getJdbcTemplate().queryForList(sql, new Object[] { goodsId });
		if (list == null || list.size() == 0) {
			return null;
		}
		GoodsProfile goodProfile = new GoodsProfile();
		Map map = (Map) list.get(0);
		if ("detailpageurl".equals(profileName)) {
			String profilevalue = (String) map.get("detailpageurl");
			goodProfile.setProfileName(profileName);
			goodProfile.setProfileValue(profilevalue);
			return goodProfile;
		} else {
			Integer profilevalue = (Integer) map.get("sales_count");
			goodProfile.setProfileName(profileName);
			goodProfile.setProfileValue(profilevalue + "");
			return goodProfile;
		}

	}

	/**
	 * 补充声明：by zx.liu
	 * 此方法中的的sql 语句添加了bg.virtual_count 字段, 来自beiker_goods 表
	 */
	@Override
	public List<GoodsForm> getGoodsByIds(String idsCourse) {

		List<GoodsForm> listForm = new LinkedList<GoodsForm>();

		// listlogo改为logo4 modify by qiaowb 2011-12-12
		String sql = "select bg.goodsname,bg.is_scheduled, bg.goodsid, bg.virtual_count as virtualcount , bgf.sales_count as salescount, bg.logo4 as listlogo,bg.logo4,bg.logo1,bg.logo2,bg.currentPrice,bg.sourcePrice,bg.discount,bg.rebatePrice,bg.goods_title,boe.on_time,bg.city,bg.isRefund,bg.endTime "
				+ " from beiker_goods_profile bgf left join beiker_goods bg on bgf.goodsid=bg.goodsid left join beiker_goods_on_end_time boe on bgf.goodsid=boe.goods_id " + " where bg.goodsid in(" + idsCourse + ")" + " order by find_in_set(bg.goodsid,'" + idsCourse + "')";

		String sqlRegion = "select  bcg.goodid as goodsId,brg.region_name as region_name,brg2.region_name as region_ext_name from beiker_catlog_good bcg , beiker_region_property  brg ,beiker_region_property brg2 where   bcg.regionid=brg.id  and  bcg.regionextid=brg2.id and bcg.goodid in(" + idsCourse + ")  order by find_in_set(bcg.goodid,'" + idsCourse + "')";

		logger.debug("商品搜索sql" + sql);
		logger.debug("商品搜索区域sql" + sqlRegion);

		List list = this.getJdbcTemplate().queryForList(sql);
		if (list == null || list.size() == 0) {
			return null;
		}
		List listRegion = this.getJdbcTemplate().queryForList(sqlRegion);

		Map<Long, GoodsForm> listGoodsForm = new LinkedHashMap<Long, GoodsForm>();

		//3天前日期
		Date tmpDate = DateUtils.toDate(DateUtils.getTimeBeforeORAfter(-3, "yyyy-MM-dd 00:00:00"), "yyyy-MM-dd HH:mm:ss");

		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			Long goodsId = (Long) map.get("goodsid");
			GoodsForm goodsForm = listGoodsForm.get(goodsId);
			if (goodsForm == null) {
				goodsForm = new GoodsForm();
			}
			Integer isRefund = (Integer) map.get("isRefund");
			goodsForm.setRefund(isRefund);
			//酒店预订
			if (map.get("is_scheduled") != null) {
				goodsForm.setIs_scheduled(map.get("is_scheduled").toString());
			}

			goodsForm.setGoodsId(goodsId);

			/**
			 * 此处为商品的真实销售数量
			 */
			String realSalesCount = String.valueOf(map.get("salescount"));
			/**
			 * 补充声明：
			 * 获取商品销售的虚拟数量
			 */
			Integer virtualCount = (Integer) map.get("virtualcount");
			;
			if (null != realSalesCount && realSalesCount.trim().length() > 0) {
				virtualCount = Integer.parseInt(realSalesCount) + (Integer) map.get("virtualcount");
			}
			/**
			 * 补充说明：
			 * 此处的商品数量为： 商品的真实数量 + 商品的虚拟数量
			 */
			String salesCount = virtualCount.toString();
			String listlogo = (String) map.get("listlogo");
			BigDecimal currentPrice = (BigDecimal) map.get("currentPrice");
			BigDecimal sourcePrice = (BigDecimal) map.get("sourcePrice");
			Float discount = (Float) map.get("discount");
			BigDecimal rebatePrice = (BigDecimal) map.get("rebatePrice");
			String goodsname = (String) map.get("goodsname");
			String logo4 = (String) map.get("logo4");
			goodsForm.setLogo4(logo4);
			goodsForm.setGoodsname(goodsname);
			goodsForm.setGoodsId(goodsId);
			goodsForm.setLogo1((String) map.get("logo1"));
			goodsForm.setLogo2((String) map.get("logo2"));
			/**
			 * 补充说明：
			 * 此处的商品数量用于页面显示
			 */
			goodsForm.setSalescount(salesCount);

			goodsForm.setListlogo(listlogo);
			goodsForm.setCurrentPrice(currentPrice.doubleValue());
			goodsForm.setSourcePrice(sourcePrice.doubleValue());
			goodsForm.setDiscount(discount);
			goodsForm.setRebatePrice(rebatePrice.doubleValue());
			goodsForm.setGoodsTitle((String) map.get("goods_title"));
			goodsForm.setCity((String) map.get("city"));
			goodsForm.setEndTime((Date)map.get("endTime"));

			//商品性质：新品
			Timestamp tsOnTime = (Timestamp) map.get("on_time");
			if (tsOnTime.compareTo(tmpDate) >= 0) {
				goodsForm.setCharacter("1");
			} else {
				goodsForm.setCharacter("0");
			}

			listGoodsForm.put(goodsId, goodsForm);
		}

		if (listRegion != null && listRegion.size() > 0) {
			for (int j = 0; j < listRegion.size(); j++) {
				Map mapRegionx = (Map) listRegion.get(j);
				Long goodsId = (Long) mapRegionx.get("goodsId");
				GoodsForm goodsForm = listGoodsForm.get(goodsId);
				if (goodsForm == null) {
					goodsForm = new GoodsForm();
				}
				Map<Long, Set<String>> mapRegion = goodsForm.getMapRegion();
				if (mapRegion == null) {
					mapRegion = new HashMap<Long, Set<String>>();
				}

				Set<String> lr = mapRegion.get(goodsId);
				if (lr == null || lr.size() == 0) {
					lr = new HashSet<String>();
				}
				String region_name = (String) mapRegionx.get("region_name");
				String region_ext_name = (String) mapRegionx.get("region_ext_name");
				// 二级区域放在括号内 modify by qiaowb 2011-12-17
				lr.add(createRegionDisplay(region_name, region_ext_name));
				mapRegion.put(goodsId, lr);
				goodsForm.setMapRegion(mapRegion);
				listGoodsForm.put(goodsId, goodsForm);
			}

		}

		if (listGoodsForm != null && listGoodsForm.size() > 0) {
			Set<Long> setForm = listGoodsForm.keySet();
			for (Long setLong : setForm) {
				GoodsForm goodsForm = listGoodsForm.get(setLong);
				// 只有一个商圈特殊处理 modify by qiaowb 2011-12-17
				if (goodsForm.getMapRegion() != null) {
					goodsForm.getMapRegion().put(goodsForm.getGoodsId(), correctRegionDisplay(goodsForm.getMapRegion().get(goodsForm.getGoodsId())));
				}
				listForm.add(goodsForm);
			}
		}

		return listForm;

	}

	@Override
	public int getGoodsCount(String idsCourse) {
		String sql = "select count(distinct bg.goodsId) as count from beiker_goods_profile bgf left join  beiker_goods bg   on bgf.goodsid=bg.goodsId left join beiker_goods_merchant bgm on bgf.goodsid=bgm.goodsid  where bgm.merchantid in(" + idsCourse + ")AND bgf.sales_count < bg.maxcount AND bg.endTime >=? AND bg.startTime<=? AND bg.isavaliable = '1'";
		List list = this.getSimpleJdbcTemplate().queryForList(sql, DateUtils.getStringDateShort(), DateUtils.getStringDateShort());
		if (list == null || list.size() == 0 || list.size() > 1)
			return 0;
		Map map = (Map) list.get(0);
		Long count = (Long) map.get("count");

		return Integer.parseInt(count + "");
	}

	@Override
	public List<Long> getGoodsCountIds(String idsCourse, int start, int end) {
		String sql = "select bg.goodsId as goodsid  from beiker_goods_profile bgf left join  beiker_goods bg   on bgf.goodsid=bg.goodsId left join beiker_goods_merchant bgm on bgf.goodsid=bgm.goodsid  where bgm.merchantid in(" + idsCourse + ")group by  bg.goodsId order by bgf.sales_count desc limit " + start + "," + end;
		List list = this.getSimpleJdbcTemplate().queryForList(sql);
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

	@Override
	public List<GoodsForm> getBrandGoodsByIds(String idsCourse) {
		List<GoodsForm> listForm = new LinkedList<GoodsForm>();
		String sql = "select bg.goodsname,bg.goodsId,bgf.sales_count as salescount ,bg.logo4 as listlogo,bg.currentPrice,bg.sourcePrice,bg.discount,bg.rebatePrice from beiker_goods_profile bgf left join  beiker_goods bg   on bgf.goodsid=bg.goodsId   where bg.goodsId in(" + idsCourse + ")order by bgf.sales_count";
		// 商品列表不显示品牌logo modify by qiaowb 2012-01-06
		/*
		 * String sqlMerchant =
		 * "select bgm.goodsid as goodsId,bmp.mc_logo2 as merchantlogo from beiker_goods_merchant bgm  left join beiker_merchant bm on bgm.merchantid=bm.id left join beiker_merchant_profile bmp on bmp.merchantid=bm.id where bm.parentid=0 and bgm.goodsid in ("
		 * + idsCourse + ")";
		 */

		String sqlRegion = "select  bcg.goodid as goodsId,brg.region_name as region_name,brg2.region_name as region_ext_name from beiker_catlog_good bcg , beiker_region_property  brg ,beiker_region_property brg2 where   bcg.regionid=brg.id  and  bcg.regionextid=brg2.id and bcg.goodid in(" + idsCourse + ")  ";

		logger.info("商品搜索sql" + sql);
		// logger.info("商品搜索商家sql" + sqlMerchant);
		logger.info("商品搜索区域sql" + sqlRegion);

		List list = this.getJdbcTemplate().queryForList(sql);
		if (list == null || list.size() == 0)
			return null;

		// List merchantLogo = this.getJdbcTemplate().queryForList(sqlMerchant);
		List listRegion = this.getJdbcTemplate().queryForList(sqlRegion);

		Map<Long, GoodsForm> listGoodsForm = new LinkedHashMap<Long, GoodsForm>();

		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			Long goodsId = (Long) map.get("goodsId");
			GoodsForm goodsForm = listGoodsForm.get(goodsId);
			if (goodsForm == null) {
				goodsForm = new GoodsForm();
			}
			goodsForm.setGoodsId(goodsId);

			String salesCount = (String) map.get("salescount");
			// String merchantlogo=(String) map.get("merchantlogo");
			String listlogo = (String) map.get("listlogo");
			BigDecimal currentPrice = (BigDecimal) map.get("currentPrice");
			BigDecimal sourcePrice = (BigDecimal) map.get("sourcePrice");
			Float discount = (Float) map.get("discount");
			BigDecimal rebatePrice = (BigDecimal) map.get("rebatePrice");
			String goodsname = (String) map.get("goodsname");

			goodsForm.setGoodsname(goodsname);
			goodsForm.setGoodsId(goodsId);
			goodsForm.setSalescount(salesCount);
			// gf.setMerchantlogo(merchantlogo);
			goodsForm.setListlogo(listlogo);
			goodsForm.setCurrentPrice(currentPrice.doubleValue());
			goodsForm.setSourcePrice(sourcePrice.doubleValue());
			goodsForm.setDiscount(discount);
			goodsForm.setRebatePrice(rebatePrice.doubleValue());
			// goodsForm.setMapRegion(mapRegion);
			// listForm.add(goodsForm);
			listGoodsForm.put(goodsId, goodsForm);
		}
		/*
		 * for (int j = 0; j < merchantLogo.size(); j++) { Map map = (Map)
		 * merchantLogo.get(j); Long goodsId = (Long) map.get("goodsId");
		 * GoodsForm goodsForm = listGoodsForm.get(goodsId); if (goodsForm ==
		 * null) { goodsForm = new GoodsForm(); }
		 * 
		 * String merchantlogo = (String) map.get("merchantlogo");
		 * goodsForm.setMerchantlogo(merchantlogo);
		 * 
		 * listGoodsForm.put(goodsId, goodsForm); }
		 */

		if (listRegion != null && listRegion.size() > 0) {
			for (int j = 0; j < listRegion.size(); j++) {
				Map mapRegionx = (Map) listRegion.get(j);
				Long goodsId = (Long) mapRegionx.get("goodsId");
				GoodsForm goodsForm = listGoodsForm.get(goodsId);
				if (goodsForm == null) {
					goodsForm = new GoodsForm();
				}
				Map<Long, Set<String>> mapRegion = goodsForm.getMapRegion();
				if (mapRegion == null) {
					mapRegion = new HashMap<Long, Set<String>>();
				}

				Set<String> lr = mapRegion.get(goodsId);
				if (lr == null || lr.size() == 0) {
					lr = new HashSet<String>();
				}
				String region_name = (String) mapRegionx.get("region_name");
				String region_ext_name = (String) mapRegionx.get("region_ext_name");
				// 二级区域放在括号内 modify by qiaowb 2011-12-17
				lr.add(createRegionDisplay(region_name, region_ext_name));
				mapRegion.put(goodsId, lr);
				goodsForm.setMapRegion(mapRegion);
				listGoodsForm.put(goodsId, goodsForm);
			}

		}

		if (listGoodsForm != null && listGoodsForm.size() > 0) {
			Set<Long> setForm = listGoodsForm.keySet();
			for (Long setLong : setForm) {
				GoodsForm goodsForm = listGoodsForm.get(setLong);
				// 只有一个商圈特殊处理 modify by qiaowb 2011-12-17
				if (goodsForm.getMapRegion() != null) {
					goodsForm.getMapRegion().put(goodsForm.getGoodsId(), correctRegionDisplay(goodsForm.getMapRegion().get(goodsForm.getGoodsId())));
				}
				listForm.add(goodsForm);
			}
		}
		return listForm;

	}

	@Override
	public Goods getTopGoodsByBrandId(Long merchantId) {
		String sql = "select bg.* from beiker_goods bg left join beiker_goods_merchant bgm on bg.goodsid=bgm.goodsid left join beiker_merchant m on bgm.merchantid=m.merchantid where m.parentid=0 and m.merchantid=?  order by  bg.isTop desc  limit 1";
		Goods goods = null;
		try {
			goods = this.getSimpleJdbcTemplate().queryForObject(sql, new RowMapperImpl(), merchantId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return goods;
	}

	@Override
	public Goods getOneGoodsByBrandId(Long merchantId) {
		String sql = "select bg.* from beiker_goods bg left join beiker_goods_merchant bgm on bg.goodsid=bgm.goodsid left join beiker_merchant m on bgm.merchantid=m.merchantid where m.parentid=?  group by bg.goodsid limit 1";
		Goods goods = null;
		try {
			goods = this.getSimpleJdbcTemplate().queryForObject(sql, new RowMapperImpl(), merchantId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return goods;
	}

	@Override
	public GoodsCatlog searchGoodsRegionById(Long goodsId) {
		String sql = "select brp.id as tagid,brp.tag_name as tagname from beiker_tag_property brp left join  beiker_catlog_good  bcg on bcg.tagid=brp.id where bcg.goodid=?";

		List list = this.getSimpleJdbcTemplate().queryForList(sql, goodsId);
		if (list == null || list.size() == 0)
			return null;
		GoodsCatlog goodsCatlog = new GoodsCatlog();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			Long tagId = (Long) map.get("tagid");
			String tagname = (String) map.get("tagname");
			goodsCatlog.setTagid(tagId);
			goodsCatlog.setTagName(tagname);
		}

		return goodsCatlog;
	}

	@Override
	public Map<String, String> findLogo3AndMerNameBygoodId(Long goodsId) {
		// Map<String,String> rspMap=null;
		if (goodsId == null) {
			throw new IllegalArgumentException("goodsId not null");
		}

		String sql = "select m.merchantid as id,m.merchantname as merchantName,b.logo3 as logo3,b.isRefund as isrefund,b.is_scheduled as isScheduled,b.goods_title as goodsTitle from  beiker_merchant m  left join beiker_goods_merchant bm on m.merchantid = bm.merchantid left join  beiker_goods b on bm.goodsid=b.goodsid where m.parentId=0 AND b.goodsid=?";
		List list = getJdbcTemplate().queryForList(sql, new Object[] { goodsId });
		Map<String, String> map = new HashMap<String, String>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map valueMap = (Map) list.get(i);
				Long merchantId = (Long) valueMap.get("id");
				String merchantName = (String) valueMap.get("merchantName");
				String log3 = (String) valueMap.get("logo3");
				// String isRefund = (String)valueMap.get("isrefund"); 
				Integer isRefund = (Integer) valueMap.get("isrefund");
				String isScheduled = (String) valueMap.get("isScheduled");
				String goodsTitle = (String) valueMap.get("goodsTitle");
				map.put("isRefund", isRefund.toString());
				map.put("merchantName", merchantName);
				map.put("logo3", log3);
				map.put("merchantId", merchantId.toString());
				map.put("isScheduled", isScheduled.toString());
				map.put("goodsTitle", goodsTitle.toString());
			}
		}
		return map;

	}

	/**
	 * 商品销售量
	 */
	@Override
	public void updateSalesCount(Long goodId, String salesCountStr) {
		if (goodId == null || salesCountStr == null || "".equals(salesCountStr)) {
			throw new IllegalArgumentException("goodId and salesCountStr not null");
		}
		String upSql = "update beiker_goods_profile set sales_count=sales_count+" + salesCountStr + " where goodsid=" + goodId;
		int rows = this.getSimpleJdbcTemplate().update(upSql);
		if (rows == 0) {
			String insertSql = "insert into beiker_goods_profile(sales_count,goodsid) values(" + salesCountStr + "," + goodId + ")";
			this.getSimpleJdbcTemplate().update(insertSql);
		}
	}

	//更新商品销售数量(交易调用已挪到goodsSoaDao数据代理下，便于后期代码拆分和解耦)
	@Override
	public void updateSalesCount(Map<Long, Integer> map) {
		if (map == null || map.size() == 0) {
			throw new IllegalArgumentException("goodId and salesCountStr not null");
		}
		for (Map.Entry<Long, Integer> mapEntry : map.entrySet()) {
			updateSalesCount(mapEntry.getKey(), mapEntry.getValue() + "");
		}

	}

	/**
	 * 补充说明：by zx.liu
	 * 此方法中的sql 语句添加了一个属性 bg.virtual_count（商品的虚拟数量）
	 */
	@Override
	public List<GoodsForm> getSalesCountByIds(String ids) {
		String sql = "select bg.goodsId, bg.virtual_count as virtualcount, bgf.sales_count as salescount " + " from beiker_goods_profile bgf left join  beiker_goods bg   on bgf.goodsid=bg.goodsId   " + " where bg.goodsId in(" + ids + ")order by bgf.sales_count ";

		List list = this.getJdbcTemplate().queryForList(sql);
		List<GoodsForm> listForm = new ArrayList<GoodsForm>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				GoodsForm gf = new GoodsForm();
				Long goodsId = (Long) map.get("goodsId");

				// String salescount=(String) map.get("salescount");
				/**
				 * 此处为商品的真实销售数量
				 */
				Integer realSalesCount = (Integer) map.get("salescount");
				/**
				 * 补充声明：
				 * 获取商品销售的虚拟数量
				 */
				Integer virtualCount = (Integer) map.get("virtualcount");
				if (null != realSalesCount) {
					virtualCount = realSalesCount + (Integer) map.get("virtualcount");
				}
				/**
				 * 补充说明：
				 * 此处的商品数量为 真实销售数量 加上 虚拟的销售数量！
				 */
				String salesCount = virtualCount.toString();

				gf.setGoodsId(goodsId);
				gf.setSalescount(salesCount);
				listForm.add(gf);
			}
		}

		return listForm;
	}

	@Override
	public List<Map<String, String>> getGoodsMerchant(String goodsid) {
		StringBuffer sbff = new StringBuffer();
		sbff.append("select merchantid,goodsid from beiker_goods_merchant where goodsid in(");
		sbff.append(goodsid);
		sbff.append(")");
		List<Map<String, String>> listGoodsMerchant = this.getJdbcTemplate().queryForList(sbff.toString());
		return listGoodsMerchant;
	}

	@Override
	public List<Map<String, String>> getMerchant(String merchantid) {
		StringBuffer sbff = new StringBuffer();
		sbff.append("select merchantid,merchantname from beiker_merchant where merchantid=");
		sbff.append(merchantid);
		sbff.append(" and parentid='0'");
		List<Map<String, String>> listMerchant = this.getJdbcTemplate().queryForList(sbff.toString());
		return listMerchant;
	}

	@Override
	public List<Goods> getGoodsById(String goodsId) {
		List<Goods> listGoods = new ArrayList<Goods>();
		String sql = "select b.goodsid,b.is_scheduled,b.goodsname,b.sourcePrice,b.currentPrice,b.payPrice,b.offerPrice,b.rebatePrice,b.dividePrice,b.discount,b.maxcount,b.endTime,b.city,"
				+ "b.startTime,b.isavaliable,b.logo1,b.logo2,b.logo3,b.logo4,b.qpsharepic,b.isTop,b.order_lose_abs_date,b.order_lose_date,b.guest_id,b.goods_title, b.virtual_count,b.goods_single_count,b.isRefund,b.kindlywarnings,b.send_rules,b.isadvance,b.iscard , b.is_menu " + " from beiker_goods b where b.goodsId in(" + goodsId + ") order by b.goodsid";
		listGoods = this.getSimpleJdbcTemplate().query(sql, new RowMapperImpl());
		return listGoods;

	}

	/**
	 * 补充说明：by zx.liu
	 * 此方法中的sql 语句添加了一个属性 b.virtual_count（商品的虚拟数量）
	 */
	@Override
	public List<Goods> getGoodsDaoByIdList(String goodsid) {
		List<Goods> listGoods = new ArrayList<Goods>();

		String sql = "select b.goodsid,b.goodsname,b.sourcePrice,b.is_scheduled,b.currentPrice,b.payPrice,b.offerPrice,b.rebatePrice,b.dividePrice,b.discount,b.maxcount,b.endTime,b.city,"
				+ "b.startTime,b.isavaliable,b.logo1,b.logo2,b.logo3,b.logo4,b.qpsharepic,b.isTop,b.order_lose_abs_date,b.order_lose_date,b.guest_id,b.goods_title, b.virtual_count,b.goods_single_count,b.isRefund,b.kindlywarnings,b.send_rules,b.isadvance,b.iscard , b.is_menu  "
				+ " from beiker_goods b left join beiker_goods_merchant t on b.goodsid=t.goodsid left join beiker_merchant m on m.merchantid=t.merchantid where m.parentid='0' and b.goodsId in(" + goodsid + ") order by t.merchantid,t.goodsid";
		Goods goods = null;

		// 品牌信息查询
		String sql1 = "select m.merchantid,m.merchantname from beiker_merchant m right join beiker_goods_merchant gm on m.merchantid=gm.merchantid " + "where m.parentid='0' and gm.goodsid in (" + goodsid + ") order by gm.merchantid,gm.goodsid";

		try {
			logger.info("getGoodsDaoByIdList,SQL=" + sql);
			listGoods = this.getSimpleJdbcTemplate().query(sql, new RowMapperImpl());

			List listMerchant = this.getJdbcTemplate().queryForList(sql1);
			for (int i = 0; i < listMerchant.size(); i++) {
				Map map = (Map) listMerchant.get(i);
				listGoods.get(i).setMerchantname((String) map.get("merchantname"));
				listGoods.get(i).setMerchantid(map.get("merchantid") + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return listGoods;

	}

	/**
	 * 根据商品ID, 来获取该 商品的虚拟购买数量
	 * Add by zx.liu
	 */
	@Override
	public Long getGoodsVirtualCountById(Long goodsId) {
		String sql = " SELECT virtual_count FROM beiker_goods WHERE goodsid=? ";
		Long goodsVirtualCount = this.getSimpleJdbcTemplate().queryForLong(sql, goodsId);
		if (null == goodsVirtualCount) {
			return 0L;
		}
		return goodsVirtualCount;
	}

	@Override
	public List<Map<String, Object>> getMiaoshaInfoByGoodsIds(Long... miaoshaid) {
		String ids = StringUtils.arrayToString(miaoshaid, ",");
		String sql1 = "SELECT bm.id  ,bg.goodsid,bm.m_pay_price,bg.logo2,bg.sourcePrice,bg.discount,bm.m_short_title FROM beiker_miaosha bm LEFT JOIN beiker_goods bg ON bm.goods_id=bg.goodsid WHERE bm.id in (" + ids + ") order by find_in_set(bm.id,'" + ids + "') ";
		List<Map<String, Object>> goodsMapList = getSimpleJdbcTemplate().queryForList(sql1);
		StringBuilder sb = new StringBuilder();
		if (goodsMapList != null && goodsMapList.size() > 0) {
			for (Map<String, Object> map : goodsMapList) {
				Long goodsId = ((Number) map.get("goodsid")).longValue();
				if (goodsId == null)
					return null;
				sb.append(goodsId);
				sb.append(",");
			}
		}
		String goodsidstr = "";
		if (sb.indexOf(",") != -1) {
			goodsidstr = sb.substring(0, sb.lastIndexOf(","));
		}
		if ("".equals(goodsidstr))
			return new ArrayList<Map<String, Object>>();

		String sql2 = "SELECT goodsid,a.merchantid FROM beiker_goods_merchant a,beiker_merchant b WHERE a.merchantid=b.merchantid AND b.parentId=0 AND a.goodsid IN (" + goodsidstr + ")";
		List<Map<String, Object>> merchantidMapList = getSimpleJdbcTemplate().queryForList(sql2);
		List<Long> merchantidList = new ArrayList<Long>();
		for (Map<String, Object> goodsMap : goodsMapList) {
			Long goodsid = ((Number) goodsMap.get("goodsid")).longValue();
			for (Map<String, Object> merchantidMap : merchantidMapList) {
				Long goodsidTemp = ((Number) merchantidMap.get("goodsid")).longValue();
				if (goodsid.equals(goodsidTemp)) {
					Long merchantid = ((Number) merchantidMap.get("merchantid")).longValue();
					goodsMap.put("merchantid", merchantid);
					merchantidList.add(merchantid);
					break;
				}
			}
		}
		// 添加地域,分类属性
		String sql4 = "SELECT goodid,tagid,tagextid,regionid,regionextid FROM beiker_catlog_good  WHERE goodid IN(" + goodsidstr + ")";
		Map<Long, String> mapRegion = getRegionProperty();
		Map<Long, String> mapTag = getTagProperty();
		List<Map<String, Object>> catlogMapList = getSimpleJdbcTemplate().queryForList(sql4);
		for (Map<String, Object> goodsMap : goodsMapList) {
			Long goodsid = ((Number) goodsMap.get("goodsid")).longValue();
			for (Map<String, Object> catlogMap : catlogMapList) {
				Long goodid = ((Number) catlogMap.get("goodid")).longValue();
				if (goodsid.equals(goodid)) {
					List<Map<String, Object>> catlogGoodMapList = (List<Map<String, Object>>) goodsMap.get("catlog");
					// 添加地域,分类属性
					catlogMap.put("tag_name", mapTag.get((catlogMap.get("tagid"))));
					catlogMap.put("tagextname", mapTag.get(catlogMap.get("tagextid")));
					catlogMap.put("regionname", mapRegion.get(catlogMap.get("regionid")));
					catlogMap.put("regionextname", mapRegion.get(catlogMap.get("regionextid")));
					if (catlogGoodMapList == null) {
						catlogGoodMapList = new ArrayList<Map<String, Object>>();
						goodsMap.put("catlog", catlogGoodMapList);
					}
					catlogGoodMapList.add(catlogMap);
					break;
				}
			}
		}
		String sql5 = "SELECT * FROM beiker_goods_profile where goodsid IN (" + goodsidstr + ")";
		List<Map<String, Object>> saleCountList = getSimpleJdbcTemplate().queryForList(sql5);

		for (Map<String, Object> goodsMap : goodsMapList) {
			Long goodsid = ((Number) goodsMap.get("goodsid")).longValue();
			for (Map<String, Object> saleCountMap : saleCountList) {
				Long goodid = ((Number) saleCountMap.get("goodsid")).longValue();
				if (goodsid.equals(goodid)) {
					goodsMap.put("saleCount", saleCountMap.get("sales_count"));
					break;
				}
			}
		}
		for (Map<String, Object> goodsMap : goodsMapList) {
			Object count = goodsMap.get("saleCount");
			if (count == null) {
				goodsMap.put("saleCount", 0);
			}
		}
		return goodsMapList;
	}

	@Override
	public List<Map<String, Object>> getGoodsInfoByGoodsIds(Long... goodsids) {
		String ids = StringUtils.arrayToString(goodsids, ",");
		String sql1 = "SELECT * from beiker_goods where goodsid in (" + ids + ") order by find_in_set(goodsid,'" + ids + "')";
		List<Map<String, Object>> goodsMapList = getSimpleJdbcTemplate().queryForList(sql1);
		String sql2 = "SELECT goodsid,a.merchantid,b.merchantintroduction FROM beiker_goods_merchant a,beiker_merchant b WHERE a.merchantid=b.merchantid AND b.parentId=0 AND a.goodsid IN (" + ids + ")";
		List<Map<String, Object>> merchantidMapList = getSimpleJdbcTemplate().queryForList(sql2);
		List<Long> merchantidList = new ArrayList<Long>();
		for (Map<String, Object> goodsMap : goodsMapList) {
			Long goodsid = ((Number) goodsMap.get("goodsid")).longValue();
			for (Map<String, Object> merchantidMap : merchantidMapList) {
				Long goodsidTemp = ((Number) merchantidMap.get("goodsid")).longValue();
				if (goodsid.equals(goodsidTemp)) {
					Long merchantid = ((Number) merchantidMap.get("merchantid")).longValue();
					String merchantintroduction = String.valueOf(merchantidMap.get("merchantintroduction"));
					goodsMap.put("merchantid", merchantid);
					goodsMap.put("merchantintroduction", merchantintroduction);
					merchantidList.add(merchantid);
					break;
				}
			}
		}
		// 不需要查询商家属性 qiaowb 2012-01-06
		/*
		 * String sql3 =
		 * "SELECT * FROM beiker_merchant_profile WHERE merchantid in (" +
		 * StringUtils.arrayToString(merchantidList .toArray(new Long[] {}),
		 * ",") + ")"; List<Map<String, Object>> merchantMapList =
		 * getSimpleJdbcTemplate() .queryForList(sql3); for (Map<String, Object>
		 * goodsMap : goodsMapList) { Long merchantid = ((Number)
		 * goodsMap.get("merchantid")).longValue(); for (Map<String, Object>
		 * merchantMap : merchantMapList) { Long merchantidTemp = ((Number)
		 * merchantMap.get("merchantid")) .longValue(); if
		 * (merchantid.equals(merchantidTemp)) {
		 * goodsMap.put(merchantMap.get("propertyname").toString(),
		 * merchantMap.get("propertyvalue")); } } }
		 */
		// 添加地域,分类属性
		String sql4 = "SELECT goodid,tagid,tagextid,regionid,regionextid FROM beiker_catlog_good  WHERE goodid IN(" + ids + ")";
		Map<Long, String> mapRegion = getRegionProperty();
		Map<Long, String> mapTag = getTagProperty();
		List<Map<String, Object>> catlogMapList = getSimpleJdbcTemplate().queryForList(sql4);
		for (Map<String, Object> goodsMap : goodsMapList) {
			Long goodsid = ((Number) goodsMap.get("goodsid")).longValue();
			for (Map<String, Object> catlogMap : catlogMapList) {
				Long goodid = ((Number) catlogMap.get("goodid")).longValue();
				if (goodsid.equals(goodid)) {
					List<Map<String, Object>> catlogGoodMapList = (List<Map<String, Object>>) goodsMap.get("catlog");
					// 添加地域,分类属性
					catlogMap.put("tag_name", mapTag.get((catlogMap.get("tagid"))));
					catlogMap.put("tagextname", mapTag.get(catlogMap.get("tagextid")));
					catlogMap.put("regionname", mapRegion.get(catlogMap.get("regionid")));
					catlogMap.put("regionextname", mapRegion.get(catlogMap.get("regionextid")));
					if (catlogGoodMapList == null) {
						catlogGoodMapList = new ArrayList<Map<String, Object>>();
						goodsMap.put("catlog", catlogGoodMapList);
					}
					catlogGoodMapList.add(catlogMap);
				}
			}
		}
		String sql5 = "SELECT * FROM beiker_goods_profile where goodsid IN (" + ids + ")";
		List<Map<String, Object>> saleCountList = getSimpleJdbcTemplate().queryForList(sql5);

		for (Map<String, Object> goodsMap : goodsMapList) {
			Long goodsid = ((Number) goodsMap.get("goodsid")).longValue();
			for (Map<String, Object> saleCountMap : saleCountList) {
				Long goodid = ((Number) saleCountMap.get("goodsid")).longValue();
				if (goodsid.equals(goodid)) {
					goodsMap.put("saleCount", saleCountMap.get("sales_count"));
					break;
				}
			}
		}
		for (Map<String, Object> goodsMap : goodsMapList) {
			Object count = goodsMap.get("saleCount");
			if (count == null) {
				goodsMap.put("saleCount", 0);
			}
		}
		return goodsMapList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.beike.dao.goods.GoodsDao#getGoodsCountIds(java.lang.String,
	 * java.lang.String, int, int)
	 */
	@Override
	public List<Long> getShopsBaoGoodsCountIds(String idsCourse, String filterFlag, int start, int end) {
		StringBuffer bufSql = new StringBuffer("select bg.goodsId as goodsid  from beiker_goods_profile bgf left join  beiker_goods bg on bgf.goodsid=bg.goodsId left join beiker_goods_merchant bgm on bgf.goodsid=bgm.goodsid  where bgm.merchantid in(").append(idsCourse).append(")and bg.isavaliable=1 and bg.startTime<=NOW() and bg.endTime>=NOW() and bgf.sales_count<bg.maxcount");
		if (org.apache.commons.lang.StringUtils.isNotEmpty(filterFlag)) {
			bufSql.append(" and ").append(filterFlag);
		}
		bufSql.append(" group by bg.goodsId order by bg.virtual_count + bgf.sales_count desc limit ").append(start).append(",").append(end);
		List list = this.getSimpleJdbcTemplate().queryForList(bufSql.toString());
		List<Long> listids = new LinkedList<Long>();
		if (list == null || list.size() == 0) {
			return new ArrayList<Long>();
		}
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			Long goodsId = (Long) map.get("goodsid");
			listids.add(goodsId);
		}
		return listids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.beike.dao.goods.GoodsDao#getShopsBaoGoodsCount(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public int getShopsBaoGoodsCount(String idsCourse, String filterFlag) {
		StringBuffer bufSql = new StringBuffer("select count(gid) as count from (select bg.goodsId as gid from beiker_goods_profile bgf left join beiker_goods bg on bgf.goodsid=bg.goodsId left join beiker_goods_merchant bgm on bgf.goodsid=bgm.goodsid  where bgm.merchantid in(").append(idsCourse).append(
				")and bg.isavaliable=1 and bg.startTime<=NOW() and bg.endTime>=NOW() and  bgf.sales_count<bg.maxcount");
		if (org.apache.commons.lang.StringUtils.isNotEmpty(filterFlag)) {
			bufSql.append(" and ").append(filterFlag);
		}
		bufSql.append(" group by bg.goodsId) aa");
		List list = this.getSimpleJdbcTemplate().queryForList(bufSql.toString());
		if (list == null || list.size() == 0 || list.size() > 1) {
			return 0;
		}
		Map map = (Map) list.get(0);
		Long count = (Long) map.get("count");
		return Integer.parseInt(count + "");
	}

	@Override
	public Goods getTopGoodsForShopBao(Long merchantId) {

		StringBuilder queryTop = new StringBuilder();
		queryTop.append("SELECT bg.* FROM beiker_goods bg LEFT JOIN beiker_goods_merchant bgm ON bg.goodsid=bgm.goodsid ");
		queryTop.append("LEFT JOIN beiker_merchant m ON bgm.merchantid=m.merchantid LEFT JOIN beiker_goods_profile bgp ON bgp.goodsid = bg.goodsid ");
		queryTop.append("WHERE m.parentid=0 AND m.merchantid=? AND bg.isTop ='1' AND bg.isavaliable = '1' AND couponcash != '1'");
		queryTop.append("AND bgp.sales_count < bg.maxcount AND bg.endTime>=DATE_FORMAT(CURDATE(),'%Y%m%d') AND bg.startTime<=DATE_FORMAT(CURDATE(),'%Y%m%d') ");
		queryTop.append("ORDER BY  bg.isTop DESC  LIMIT 1");
		Goods goods = null;

		try {
			goods = this.getSimpleJdbcTemplate().queryForObject(queryTop.toString(), new RowMapperImpl(), merchantId);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.info("没有查到相关记录:getOneGoodsByForShopBao() merchantId:" + merchantId);
			return null;
		}
		return goods;
	}

	@Override
	public Goods getOneGoodsByForShopBao(Long merchantId) {
		StringBuilder queryTop = new StringBuilder();
		queryTop.append("SELECT bg.* FROM beiker_goods bg LEFT JOIN beiker_goods_merchant bgm ON bg.goodsid=bgm.goodsid  ");
		queryTop.append("LEFT JOIN beiker_merchant m  ON bgm.merchantid=m.merchantid ");
		queryTop.append("LEFT JOIN beiker_goods_profile bgf ON bgf.goodsid=bg.goodsid ");
		queryTop.append("WHERE m.parentid=? AND bg.isavaliable = '1' AND couponcash != '1' AND bgf.sales_count< bg.maxcount ");
		queryTop.append("AND bg.endTime>=DATE_FORMAT(CURDATE(),'%Y%m%d') AND bg.startTime<=DATE_FORMAT(CURDATE(),'%Y%m%d') ORDER BY bg.virtual_count + bgf.sales_count DESC,bg.startTime DESC  LIMIT 1 ");
		Goods goods = null;
		try {
			goods = this.getSimpleJdbcTemplate().queryForObject(queryTop.toString(), new RowMapperImpl(), merchantId);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.info("没有查到相关记录:getOneGoodsByForShopBao() merchantId:" + merchantId);
			return null;
		}
		return goods;
	}

	@Override
	public String getIsTopForShopBao(Long goodsid) {

		String query_sql = "select isTop from beiker_goods where goodsid = " + goodsid;
		List rs = this.getJdbcTemplate().queryForList(query_sql);
		if (null != rs && rs.size() > 0) {
			Map m = (Map) rs.get(0);
			return (String) m.get("isTop");
		}
		return null;
	}

	@Override
	public Set<String> getGoodsRegion(Long goodsid) {

		String sqlRegion = "select  bcg.goodid as goodsId,brg.region_name as region_name,brg2.region_name as region_ext_name from beiker_catlog_good bcg ," + " beiker_region_property  brg ,beiker_region_property brg2 where   bcg.regionid=brg.id  and  bcg.regionextid=brg2.id and " + "bcg.goodid =" + goodsid + " order by find_in_set(bcg.goodid,'" + goodsid + "')";
		List rs = this.getJdbcTemplate().queryForList(sqlRegion);
		Set<String> list = new HashSet<String>();
		if (null != rs && rs.size() > 0) {
			for (int i = 0; i < rs.size(); i++) {
				Map mapRegionx = (Map) rs.get(i);
				String region_name = (String) mapRegionx.get("region_name");
				String region_ext_name = (String) mapRegionx.get("region_ext_name");
				// 二级区域放在括号内 modify by qiaowb 2011-12-17
				list.add(createRegionDisplay(region_name, region_ext_name));
			}
			// 只有一个商圈特殊处理 modify by qiaowb 2011-12-17
			return correctRegionDisplay(list);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.beike.dao.goods.GoodsDao#getRecommendGoods(java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.lang.String)
	 */
	@Override
	public List<Long> getRecommendGoods(Long regionid, String regionextids, Long tagid, Long cityid, Long count, String excludeIds) {
		// 原子查询修改先查询beiker_catlog_good找出商品ID，再通过in商品ID获取最终结果
		// 根据二级商圈、一级分类查询商品ID
		StringBuffer bufSelId = new StringBuffer("SELECT DISTINCT tmp_bc.goodid,tmp_bc.brandid FROM beiker_catlog_good tmp_bc WHERE tmp_bc.isavaliable=1 ");
		if (org.apache.commons.lang.StringUtils.isNotEmpty(excludeIds)) {
			bufSelId = bufSelId.append(" AND tmp_bc.goodid NOT IN (" + excludeIds + ")");
		}

		if (regionextids != null) {
			bufSelId.append(" AND tmp_bc.regionextid in (").append(regionextids).append(")").append(" AND tmp_bc.area_id = ").append(cityid);
		} else if (regionid != null) {
			bufSelId.append(" AND tmp_bc.regionid = ").append(regionid).append(" AND tmp_bc.area_id = ").append(cityid);
		} else if (tagid != null) {
			bufSelId.append(" AND tmp_bc.tagid = ").append(tagid).append(" AND tmp_bc.area_id = ").append(cityid);
		}
		bufSelId.append(" order by tmp_bc.currentprice asc");

		List<Map<String, Object>> lstCatlogIds = null;
		lstCatlogIds = this.getSimpleJdbcTemplate().queryForList(bufSelId.toString());
		if (lstCatlogIds != null && lstCatlogIds.size() > 0) {
			StringBuffer bufCatlogIds = new StringBuffer();
			StringBuilder bufBrand = new StringBuilder(",");
			for (Map<String, Object> mapId : lstCatlogIds) {
				//同一品牌只取价格低的商品
				if (bufBrand.indexOf("," + mapId.get("brandid") + ",") < 0) {
					bufCatlogIds = bufCatlogIds.append(mapId.get("goodid")).append(",");
					bufBrand.append(mapId.get("brandid") + ",");
				}
			}
			StringBuffer bufSel = new StringBuffer("SELECT bg.goodsid AS goodsid FROM beiker_goods bg ");
			bufSel.append("LEFT OUTER JOIN beiker_goods_profile bgf ON bgf.goodsid = bg.goodsid ").append("WHERE bg.isavaliable=1 AND bg.startTime<=NOW() AND bg.endTime>=NOW() AND bg.currentPrice > bg.dividePrice ");
			if (bufCatlogIds.length() > 0) {
				bufSel = bufSel.append(" AND bg.goodsid IN(" + bufCatlogIds.substring(0, bufCatlogIds.length() - 1) + ") ");
			}
			bufSel.append(" AND bgf.sales_count<bg.maxcount ").append("ORDER BY bg.virtual_count + bgf.sales_count DESC,bg.goodsid DESC LIMIT ?");
			List<Map<String, Object>> lstReIds = null;
			lstReIds = this.getSimpleJdbcTemplate().queryForList(bufSel.toString(), count);

			List<Long> listids = new LinkedList<Long>();
			if (lstReIds == null || lstReIds.size() == 0) {
				return new ArrayList<Long>();
			}
			for (int i = 0; i < lstReIds.size(); i++) {
				Map map = lstReIds.get(i);
				Long goodsId = (Long) map.get("goodsid");
				listids.add(goodsId);
			}
			return listids;
		} else {
			return new ArrayList<Long>();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.beike.dao.goods.GoodsDao#getGoodsRegionIds(java.lang.Long)
	 */
	@Override
	public List<Map<String, Object>> getGoodsRegionIds(Long goodsId) {
		StringBuffer bufSql = new StringBuffer("SELECT DISTINCT goodid AS goodid,regionid AS regionid,regionextid AS regionextid,tagid AS tagid,tagextid AS tagextid ").append("FROM beiker_catlog_good bcg ").append("WHERE goodid=").append(goodsId).append(" order by regionextid");
		List<Map<String, Object>> lstRegion = this.getSimpleJdbcTemplate().queryForList(bufSql.toString());
		return lstRegion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.beike.dao.goods.GoodsDao#getCouponRegionIds(java.lang.Long)
	 */
	@Override
	public List<Map<String, Object>> getCouponRegionIds(Long couponId) {
		StringBuffer bufSql = new StringBuffer("SELECT DISTINCT couponid AS couponid,regionid AS regionid,regionextid AS regionextid,tagid AS tagid,tagextid AS tagextid ").append("FROM beiker_catlog_coupon bcg ").append("WHERE couponid=").append(couponId).append(" order by regionextid");
		List<Map<String, Object>> lstRegion = this.getSimpleJdbcTemplate().queryForList(bufSql.toString());
		return lstRegion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.beike.dao.goods.GoodsDao#getGoodsFirstRegionById(java.lang.Long)
	 */
	@Override
	public List<Map<String, Object>> getGoodsFirstRegionById(Long goodsId) {
		StringBuffer bufSql = new StringBuffer("SELECT DISTINCT goodid AS goodid,regionid AS regionid ").append("FROM beiker_catlog_good bcg ")
		// .append("JOIN beiker_region_property brp1 ON brp1.id =
		// bcg.regionid ")
				.append("WHERE goodid=").append(goodsId).append(" limit 2");
		List<Map<String, Object>> lstRegion = this.getSimpleJdbcTemplate().queryForList(bufSql.toString());
		return lstRegion;
	}

	@Override
	public List<Long> getHotMerchantIDS(AbstractCatlog abstractCatlog) {
		StringBuilder sql = new StringBuilder();
		GoodsCatlog gc = (GoodsCatlog) abstractCatlog;
		Object[] params;
		sql.append("SELECT DISTINCT bm.merchantid FROM beiker_merchant_profile bm JOIN beiker_goods_merchant bgm ON bgm.merchantid=bm.merchantid JOIN beiker_catlog_good bcg ON bcg.goodid=bgm.goodsid WHERE bcg.isavaliable=1 AND bcg.area_id=? ");
		if (gc.getTagextid() != null) {
			sql.append(" AND bcg.tagextid=?");
			params = new Object[2];
			params[0] = gc.getCityid();
			params[1] = gc.getTagextid();

		} else if (gc.getTagid() != null) {
			sql.append(" AND bcg.tagid=?");
			params = new Object[2];
			params[0] = gc.getCityid();
			params[1] = gc.getTagid();
		} else {
			params = new Object[1];
			params[0] = gc.getCityid();
		}
		sql.append(" ORDER BY bm.mc_sale_count DESC LIMIT 0,5");
		logger.info("hot brand sql=	" + sql.toString());
		List<Long> list = getJdbcTemplate().query(sql.toString(), params, new RowMapper() {

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				return rs.getLong("merchantid");
			}

		});
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.beike.dao.goods.GoodsDao#getMostExpGoodsId(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getMostExpGoodsId(String goodsIds) {
		String query_sql = "select goodsid,city from beiker_goods where goodsid in (" + goodsIds + ") order by currentPrice desc limit 1";
		List<Map<String, Object>> lstGoods = this.getSimpleJdbcTemplate().queryForList(query_sql);
		return lstGoods;
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
				String[] aryRegion = org.apache.commons.lang.StringUtils.split(regionName, "(");
				if (aryRegion.length == 2) {
					regionName = aryRegion[1];
					if (regionName.endsWith(")")) {
						regionName = regionName.substring(0, regionName.length() - 1);
					}
				}
			}
			setRegion.clear();
			setRegion.add(regionName);
		}
		return setRegion;
	}

	@Override
	public Map<Long, String> getRegionProperty() {
		String sql = "SELECT brp.id,brp.region_name FROM beiker_region_property brp";
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
		Map<Long, String> map = new HashMap<Long, String>();
		for (int i = 0, size = list.size(); i < size; i++) {
			map.put((Long) list.get(i).get("id"), (String) list.get(i).get("region_name"));
		}
		return map;
	}

	@Override
	public Map<Long, String> getTagProperty() {
		String sql = "SELECT btp.tag_name,btp.id FROM beiker_tag_property btp";
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
		Map<Long, String> map = new HashMap<Long, String>();
		for (int i = 0, size = list.size(); i < size; i++) {
			map.put((Long) list.get(i).get("id"), (String) list.get(i).get("tag_name"));
		}
		return map;
	}

	@Override
	public List<Long> getTopGoodsByMerchantId(Long merchantId, int topCount, String excGoodsIds) {
		StringBuilder bufSel = new StringBuilder("SELECT bg.goodsid AS goodsid FROM beiker_goods bg ");
		bufSel.append("LEFT OUTER JOIN beiker_goods_profile bgf ON bgf.goodsid = bg.goodsid ").append("LEFT JOIN beiker_goods_merchant bgm ON bgm.goodsid=bg.goodsid ").append("WHERE bgm.merchantid=? and bg.isavaliable=1 AND bg.startTime<=NOW() AND bg.endTime>=NOW() ").append("AND bgf.sales_count < bg.maxcount ");
		if (org.apache.commons.lang.StringUtils.isNotEmpty(excGoodsIds)) {
			bufSel.append(" and bg.goodsid not in (").append(excGoodsIds).append(") ");
		}
		bufSel.append("ORDER BY bg.virtual_count + bgf.sales_count DESC,bg.currentPrice LIMIT ?");

		List<Map<String, Object>> lstReIds = null;
		lstReIds = this.getSimpleJdbcTemplate().queryForList(bufSel.toString(), merchantId, topCount);

		List<Long> listids = new LinkedList<Long>();
		if (lstReIds == null || lstReIds.size() == 0) {
			return new ArrayList<Long>();
		}
		for (int i = 0; i < lstReIds.size(); i++) {
			Map map = lstReIds.get(i);
			Long goodsId = (Long) map.get("goodsid");
			listids.add(goodsId);
		}
		return listids;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getLowestPriceByNow(Long cityId, String ids) {

		StringBuilder query_lowprice = new StringBuilder();
		List<Long> lowPriceList = null;
		query_lowprice.append("SELECT bai.goodsid FROM beiker_adgoodsinfo bai ");
		query_lowprice.append("WHERE bai.type = '3' AND bai.cityid = ? ");
		if (StringUtils.validNull(ids)) {
			query_lowprice.append("AND bai.goodsid not in (").append(ids).append(") ");
		}
		List lowList = this.getJdbcTemplate().queryForList(query_lowprice.toString(), new Object[] { cityId });

		if (lowList != null && lowList.size() > 0) {
			lowPriceList = new LinkedList<Long>();
			for (Object ox : lowList) {
				Map mx = (Map) ox;
				Long goodsid = (Long) mx.get("goodsid");
				lowPriceList.add(goodsid);
			}
		}

		return lowPriceList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> queryLowestPrice(Long cityId, Long countNum, String ids) {

		StringBuilder query_lowestGood = new StringBuilder();
		List<Long> goodsidList = new LinkedList<Long>();

		query_lowestGood.append("SELECT DISTINCT(bg.goodsid) AS goodsid FROM beiker_goods bg ");
		query_lowestGood.append("LEFT JOIN beiker_catlog_good   bcg ON bg.goodsid = bcg.goodid ");
		query_lowestGood.append("LEFT JOIN beiker_goods_profile bgp ON bg.goodsid = bgp.goodsid ");
		query_lowestGood.append("WHERE bg.isavaliable = '1' AND bg.startTime<=NOW() ");
		query_lowestGood.append("AND bg.endTime>=NOW() AND bg.currentPrice > bg.dividePrice ");
		query_lowestGood.append("AND bg.dividePrice>0 AND bgp.sales_count < bg.maxcount ");
		query_lowestGood.append("AND bcg.area_id = ? ");
		if (StringUtils.validNull(ids)) {
			query_lowestGood.append("AND bg.goodsid not in (").append(ids).append(") ");
		}
		query_lowestGood.append("ORDER BY bg.currentPrice ASC LIMIT ?");
		List goodsList = this.getJdbcTemplate().queryForList(query_lowestGood.toString(), new Object[] { cityId, countNum });

		if (goodsList != null && goodsList.size() > 0) {
			for (Object obj : goodsList) {
				Map map = (Map) obj;
				Long goodid = (Long) map.get("goodsid");
				goodsidList.add(goodid);
			}
		}
		return goodsidList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getPartLowestPrice(Long cityId, Long countNum, String ids, Long tagid) {

		StringBuilder getLowestGoods = new StringBuilder();
		List<Long> goodsidList = new ArrayList<Long>();
		getLowestGoods.append("SELECT DISTINCT(bg.goodsid) AS goodsid FROM beiker_goods bg ");
		getLowestGoods.append("LEFT JOIN beiker_catlog_good   bcg ON bg.goodsid = bcg.goodid ");
		getLowestGoods.append("LEFT JOIN beiker_goods_profile bgp ON bg.goodsid = bgp.goodsid ");
		getLowestGoods.append("WHERE bg.isavaliable = '1' AND bg.startTime<=NOW() ");
		getLowestGoods.append("AND bg.endTime>=NOW() AND bg.currentPrice > bg.dividePrice ");
		getLowestGoods.append("AND bg.dividePrice>0 AND bgp.sales_count < bg.maxcount ");
		getLowestGoods.append("AND bg.couponcash = '0' AND bcg.area_id = ? ");
		getLowestGoods.append(" AND bcg.tagid = ? ");
		if (StringUtils.validNull(ids)) {
			getLowestGoods.append("AND bg.goodsid not in (").append(ids).append(") ");
		}
		getLowestGoods.append("ORDER BY bg.currentPrice ASC,bg.goodsid DESC LIMIT ?");

		goodsidList = this.getJdbcTemplate().queryForList(getLowestGoods.toString(), new Object[] { cityId, tagid, countNum }, Long.class);
		return goodsidList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getTopRegionCatlogId(Long cityId, Long countNum) {
		List<Long> regionIdList = new ArrayList<Long>();
		StringBuilder topRegionSql = new StringBuilder();
		topRegionSql.append("SELECT regionid,COUNT(DISTINCT goodid) FROM beiker_catlog_good ");
		topRegionSql.append("WHERE isavaliable=1 AND area_id =? ");
		topRegionSql.append("GROUP BY regionid order by COUNT(DISTINCT goodid) DESC,regionid ASC  ");
		if (countNum != null && countNum > 0L) {
			topRegionSql.append("LIMIT " + countNum);
		}
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(topRegionSql.toString(), new Object[] { cityId });
		if (null != list && list.size() > 0) {
			for (Map<String, Object> map : list) {
				Long regionId = (Long) map.get("regionid");
				regionIdList.add(regionId);
			}
		}
		return regionIdList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getGoodCountByCity(String city, String type) {

		StringBuilder query_count = new StringBuilder();

		query_count.append("SELECT COUNT(DISTINCT(bg.goodsid)) as countid FROM beiker_goods bg ");
		query_count.append("LEFT JOIN beiker_catlog_good bcg ON bg.goodsid = bcg.goodid ");
		query_count.append("LEFT JOIN beiker_goods_profile bgp ON bcg.goodid = bgp.goodsid ");
		query_count.append("LEFT JOIN beiker_area ba ON bcg.area_id = ba.area_id ");
		query_count.append("WHERE bcg.isavaliable = 1 AND enddate>=date_format(curdate(),'%Y%m%d') and createdate<=date_format(curdate(),'%Y%m%d') AND bg.iscard = '0' ");
		query_count.append("AND bgp.sales_count < bg.maxcount AND ba.area_en_name = '").append(city).append("' ");
		query_count.append("AND bcg.tagid = ").append(type);

		logger.info("sql....." + query_count.toString());
		List li = this.getJdbcTemplate().queryForList(query_count.toString());

		return li;
	}

	@Override
	public List<Map<String, Object>> findGoodInfoByShopCartGoodsId(String goodsIds) {
		if (goodsIds == null || goodsIds.length() == 0) {
			throw new IllegalArgumentException("goodsId not null");
		}

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT goods.goodsid AS goodsId, goods.goodsname AS goodsName,goods.goods_title AS goodsTitle,goods.logo3 AS goodsDTPicUrl,goods.payPrice AS goodsPayPrice,goods.isavaliable AS isAvaliable,goods.goods_single_count as singleCount,maxcount as maxCount,merchant.merchantid AS merchantId, merchant.merchantname AS merchantName");
		sql.append(" FROM beiker_merchant merchant, beiker_goods_merchant bm, beiker_goods goods ");
		sql.append(" WHERE     merchant.merchantid = bm.merchantid  AND bm.goodsid = goods.goodsid AND merchant.parentId = 0   AND  goods.goodsid in ( ");
		sql.append(goodsIds);
		sql.append(" ) ");

		List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sql.toString());

		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getLowestPriceGoodById(Long catlogid, Long cityid) {

		StringBuilder lower_good = new StringBuilder();

		lower_good.append("select distinct(bcg.goodid) as goodsid from beiker_catlog_good bcg ");
		lower_good.append("left join beiker_goods_profile bgp on bcg.goodid = bgp.goodsid ");
		lower_good.append("where bcg.area_id = ? and bcg.tagid = ? ");
		lower_good.append("order by bgp.sales_count asc ");
		lower_good.append("limit 3; ");

		List li = this.getJdbcTemplate().queryForList(lower_good.toString(), new Object[] { cityid, catlogid });

		return li;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getGoodIdWithMerchant(Long merchantId) {
		StringBuilder query_mersql = new StringBuilder();
		query_mersql.append("SELECT bgm.goodsid FROM beiker_goods_merchant bgm ");
		query_mersql.append("LEFT JOIN beiker_goods bg ON bgm.goodsid = bg.goodsid ");
		query_mersql.append("LEFT JOIN beiker_goods_profile bgp ON bg.goodsid = bgp.goodsid ");
		query_mersql.append("WHERE merchantid = ? AND bg.isavaliable = 1 AND bgp.sales_count < bg.maxcount ");
		query_mersql.append("AND bg.startTime < NOW() AND bg.endTime >= NOW() ");
		return this.getJdbcTemplate().queryForList(query_mersql.toString(), new Object[] { merchantId }, Long.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getGoodKindlyByGoodId(Long goodId) {

		String kind_sql = "SELECT goods_id,kindlywarnings,high_light,create_time FROM beiker_goods_kindly WHERE goods_id = " + goodId;

		List kindlist = this.getJdbcTemplate().queryForList(kind_sql);

		return kindlist;
	}

	/**
	 * @description:通过主商品Id获得主商品所在所有分店卖的所有商品
	 * @param goodsid
	 * @return String
	 * @throws
	 */
	public String getMerchantGoodsByMainId(Long goodsid) {
		StringBuilder merchantsql = new StringBuilder("SELECT distinct bgm1.goodsid ").append(" FROM beiker_goods_merchant bgm").append(" JOIN beiker_merchant bm ON bgm.merchantid = bm.merchantid ").append(" AND bgm.goodsid = ? AND bm.parentId <> 0").append(" JOIN beiker_goods_merchant bgm1 ON bgm1.merchantid = bgm.merchantid")
				.append(" JOIN beiker_goods bg ON bg.goodsid = bgm1.goodsid AND bg.isavaliable = 1 AND bg.couponcash = '0'");
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(merchantsql.toString(), new Object[] { goodsid });
		if (list != null && list.size() > 0) {
			StringBuilder sb = new StringBuilder("");
			for (int i = 0; i < list.size(); i++) {
				if (i == list.size() - 1) {
					sb.append(list.get(i).get("goodsid"));
				} else {
					sb.append(list.get(i).get("goodsid")).append(",");
				}
			}
			return sb.toString();
		}
		return null;
	}

	public List<Map<String, Object>> getListGoodsInfo(Map<String, String> map) {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT bcg.goodid,bcg.regionid,bcg.regionextid,bcg.tagid,bcg.tagextid,bcg.currentprice").append(" FROM beiker_catlog_good bcg").append(" LEFT JOIN beiker_goods_merchant bgm ON bgm.goodsid = bcg.goodid").append(" LEFT JOIN beiker_goods bg ON bg.goodsid = bcg.goodid").append(" WHERE bcg.goodid NOT IN (").append(map.get("goodsIds"))
				.append(") AND bg.isavaliable = 1 AND bg.couponcash = '0'").append(" AND bcg.area_id = ").append(Integer.parseInt(map.get("areaid")));
		if (StringUtils.validNull(map.get("tagid"))) {
			sql.append(" AND bcg.tagid IN (").append(map.get("tagid")).append(")");
		}
		if (StringUtils.validNull(map.get("tagextid"))) {
			sql.append(" AND bcg.tagextid NOT IN (").append(map.get("tagextid")).append(")");
		}
		if (StringUtils.validNull(map.get("regionid"))) {
			sql.append(" AND bcg.regionid IN (").append(map.get("regionid")).append(")");
		}
		sql.append(" ORDER BY bcg.goodid DESC");
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString());
		if (null != list && list.size() > 0) {
			return list;
		}
		return null;
	}

	/**
	 * @description:通过商品Ids查询订单量
	 * @param goodIds
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	public List<Map<String, Object>> getOrderCount(String goodIds) {
		StringBuilder sql = new StringBuilder("SELECT btg.goods_id, COUNT(DISTINCT btg.goods_id, btg.id) cou ").append(" FROM beiker_trxorder_goods btg").append(" LEFT JOIN beiker_goods bg ON bg.goodsid = btg.goods_id").append(" WHERE btg.trx_status != 'INIT' AND btg.goods_id IN(").append(goodIds).append(") AND btg.create_date BETWEEN ? AND ? AND bg.isavaliable =1")
				.append(" GROUP BY btg.goods_id").append(" ORDER BY cou DESC");
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql.toString(), new Object[] { DateUtils.getTimeBeforeORAfter(-2), DateUtils.getNowTime() });
		return list;
	}

	/**
	 * @description:获取商品的品牌Id
	 * @param goodsId
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	public List<Map<String, Object>> getGoodsBrandId(String goodsId) {
		StringBuilder sql = new StringBuilder("SELECT bgm.goodsid,bgm.merchantid").append(" FROM beiker_merchant bm").append(" LEFT JOIN beiker_goods_merchant bgm ON bgm.merchantid = bm.merchantid").append(" WHERE bgm.goodsid IN (").append(goodsId).append(") AND bm.parentId = 0").append(" ORDER BY FIND_IN_SET(bgm.goodsid,'").append(goodsId).append("')");
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString());
		return list;
	}

	public List<Map<String, Object>> getRealSalesCount(String goodsIds, String cityId) {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT bgp.goodsid,bgp.sales_count,bg.startTime").append(" FROM beiker_goods_profile bgp").append(" LEFT JOIN beiker_goods bg ON bg.goodsid = bgp.goodsid").append(" LEFT JOIN beiker_catlog_good bcg ON bcg.goodid = bg.goodsid").append(" WHERE bcg.area_id = ").append(cityId).append(" AND bg.isavaliable = 1 AND bg.couponcash = '0' ")
				.append(" AND bgp.sales_count > 0 AND bg.startTime < NOW()").append(" AND bgp.goodsid NOT IN(").append(goodsIds).append(")");
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString());
		return list;
	}

	@Override
	public List<Map<String, Object>> getHuodongGoodsId(Long goodsId) {

		String sql = "select goodsid from beiker_huodong bh where bh.goodsid=? and bh.isjoin='1' and bh.isstart='1';";
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sql, goodsId);
		return list;
	}

	/**
	 * 根据goodsId获得商品所属种类（品类catlog_goods表的tagids)
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> getCatalogGoods(Long goodsId) {
		String sql = "SELECT goodid,regionid,tagid,regionextid,tagextid num FROM beiker_catlog_good  WHERE goodid=?";
		List<Map<String, Object>> list = this.getSimpleJdbcTemplate().queryForList(sql, goodsId);
		return list;
	}

	@Override
	public int queryGoodsCountByIds(List<Long> goodsIdList, String scope, boolean cashOnly) {
		StringBuffer sql = new StringBuffer("select * from beiker_goods where goodsid in (:idList)");
		//查询范围（所有商品:all 、最新商品<3天内发布>:new）
		if ("new".equals(scope)) {
			String threeDaysAgoStr = new SimpleDateFormat("yyyy-MM-dd").format(org.apache.commons.lang.time.DateUtils.add(new Date(), Calendar.DATE, -3));
			sql.append("");
		} else {
			sql.append("");
		}
		if (cashOnly) {
			sql.append(" and couponcash = 1");
		}
		return 0;
	}

	@Override
	public List<GoodsForm> queryGoodsByIds(Pager pager, List<Long> goodsIdList, String scope, String sort, boolean cashOnly) {
		StringBuffer sql = new StringBuffer("select * from beiker_goods g left join beiker_goods_on_end_time goet on g.goodsid = goet.goods_id  where g.goodsid in (:idList)");
		//查询范围（所有商品:all 、最新商品<3天内发布>:new）
		if ("new".equals(scope)) {
			String threeDaysAgoStr = DateUtils.getTimeBeforeORAfter(-3, "yyyy-MM-dd 00:00:00");
			sql.append(" and goet.on_time>= " + threeDaysAgoStr);
		}
		//是否只查现金券
		if (cashOnly) {
			sql.append(" and g.couponcash = 1");
		}
		//排序(默认:default、发布时间:publishTime、销量:salesCount、价格:price、好评率:review、折扣:rebate)
		if (!sort.equals("default")) {
			String[] sortRule = sort.split("_");
			String sort_term = sortRule[0];
			String sort_order = sortRule[1];
			if ("publishTime".equals(sort_term)) {
				sql.append(" order by goet.on_time " + sort_order);
			} else if ("salesCount".equals(sort_term)) {
				sql.append(" order by on_time " + sort_order);
			} else if ("price".equals(sort_term)) {
				sql.append(" order by on_time " + sort_order);
			} else if ("review".equals(sort_term)) {
				sql.append(" order by on_time " + sort_order);
			} else if ("rebate".equals(sort_term)) {
				sql.append(" order by on_time " + sort_order);
			}
		}
		return null;
	}

	public List<Map<String, Object>> getGoodsIdsByFirstCatIds(String firstCatIds, Long areaId){
		StringBuilder budSql = new StringBuilder("");
		budSql.append("SELECT DISTINCT goodid AS goodid ");
		budSql.append("FROM beiker_catlog_good bcg ");
		budSql.append("JOIN beiker_goods bg ON bg.goodsid=bcg.goodid ");
		budSql.append("JOIN beiker_goods_profile profile ON bg.goodsid=profile.goodsid ");
		budSql.append("WHERE bcg.isavaliable=1 AND bg.endTime>NOW()  AND bg.maxcount>profile.sales_count AND bcg.area_id=? AND bcg.tagid IN(").append(firstCatIds).append(") ");
		List<Map<String, Object>> lstGoodsIds = this.getSimpleJdbcTemplate().queryForList(budSql.toString(), areaId);
		return lstGoodsIds;
	}
	
	public List<Map<String, Object>> getGoodIdsOfBestSellingWithinaPeriodOfTime(String goodIds, String pointInTime, int amount){
		List<Map<String, Object>> lstGoods = new ArrayList<Map<String, Object>>();
		if(goodIds != null && !"".equals(goodIds)){
			StringBuilder budSql = new StringBuilder("");
			budSql.append("SELECT goods_id AS goodsid, COUNT(1) AS counts FROM beiker_trxorder_goods trxordergoods ");
			budSql.append("WHERE trxordergoods.trx_status != 'INIT' AND trxordergoods.create_date >= ? AND trxordergoods.goods_id IN(").append(goodIds).append(") ");
			budSql.append("GROUP BY trxordergoods.goods_id ORDER BY counts DESC LIMIT ").append(amount);
			lstGoods = this.getSimpleJdbcTemplate().queryForList(budSql.toString(), pointInTime);
		}
		return lstGoods;
	}
	public List<Map<String,Object>> getBranchIdByGoodsId(Long goodId){
		StringBuilder sql = new StringBuilder("SELECT bgm.merchantid FROM beiker_goods_merchant bgm")
		.append(" LEFT JOIN beiker_merchant bm ON bm.merchantid = bgm.merchantid")
		.append(" WHERE bgm.goodsid = ? AND bm.parentId <> 0");
		return this.getSimpleJdbcTemplate().queryForList(sql.toString(), new Object[]{goodId});
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getTopGoodsWithFlagShip(List<Long> merchantIdList) {
		
		StringBuilder topGoods = new StringBuilder();
		
		topGoods.append("SELECT DISTINCT(m.merchantid),bg.* FROM beiker_goods bg ");
		topGoods.append("LEFT JOIN beiker_goods_merchant bgm ON bg.goodsid=bgm.goodsid ");
		topGoods.append("LEFT JOIN beiker_merchant m ON bgm.merchantid=m.merchantid ");
		topGoods.append("LEFT JOIN beiker_goods_profile bgp ON bgp.goodsid = bg.goodsid ");
		topGoods.append("WHERE m.parentid=0 AND bg.isTop ='1' AND bg.isavaliable = '1' AND couponcash != '1' ");
		topGoods.append("AND bgp.sales_count < bg.maxcount AND bg.endTime>=DATE_FORMAT(CURDATE(),'%Y%m%d') ");
		topGoods.append("AND bg.startTime<=DATE_FORMAT(CURDATE(),'%Y%m%d') ");
		topGoods.append("AND m.merchantid IN (").append(StringUtils.arrayToString(merchantIdList.toArray(),",")).append(") ");
		topGoods.append("GROUP BY m.merchantid ");
		
		List goodList =  this.getJdbcTemplate().queryForList(topGoods.toString());
		
		return goodList;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getMaxSaleCountWithFlagShip(List<Long> merchantIdList) {
		
		StringBuilder scountsql = new StringBuilder();
		
		scountsql.append("SELECT m.merchantid,bg.* FROM beiker_goods bg ");
		scountsql.append("LEFT JOIN beiker_goods_merchant bgm ON bg.goodsid=bgm.goodsid ");
		scountsql.append("LEFT JOIN beiker_merchant m  ON bgm.merchantid=m.merchantid ");
		scountsql.append("LEFT JOIN beiker_goods_profile bgf ON bgf.goodsid=bg.goodsid ");
		scountsql.append("WHERE bg.isavaliable = '1' AND couponcash != '1' AND bgf.sales_count< bg.maxcount ");
		scountsql.append("AND bg.endTime>=DATE_FORMAT(CURDATE(),'%Y%m%d') AND bg.startTime<=DATE_FORMAT(CURDATE(),'%Y%m%d') ");
		scountsql.append("AND m.parentid = 0 AND m.merchantid IN (").append(StringUtils.arrayToString(merchantIdList.toArray(),",")).append(") ");
		scountsql.append("GROUP BY merchantid ");
		scountsql.append("ORDER BY bg.virtual_count + bgf.sales_count DESC,bg.startTime ");
		
		List salelist = this.getJdbcTemplate().queryForList(scountsql.toString());
		
		return salelist;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getMerchantIdByGoodId(Long goodId) {
		
		StringBuilder mersql =  new StringBuilder();
		
		mersql.append("SELECT bm.merchantid FROM beiker_merchant bm ");
		mersql.append("LEFT JOIN beiker_goods_merchant bgm ON bm.merchantid = bgm.merchantid ");
		mersql.append("WHERE bm.parentid = 0 AND bgm.goodsid = ").append(goodId);
		mersql.append(" LIMIT 1 ");
		
		List merlist = this.getJdbcTemplate().queryForList(mersql.toString());
		
		return merlist;
	}
}
