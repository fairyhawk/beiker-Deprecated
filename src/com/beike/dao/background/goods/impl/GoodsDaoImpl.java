package com.beike.dao.background.goods.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.background.goods.GoodsDao;
import com.beike.entity.background.goods.Goods;
import com.beike.form.background.goods.GoodsForm;
import com.beike.form.background.top.TopForm;
import com.beike.util.StringUtils;

/**
 * Title : GoodsDaoImpl
 * <p/>
 * Description : 商品关系数据访问实现
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : SinoboGroup.com </P> JDK Version Used : JDK 5.0 +
 * <p/>
 * Modification History :
 * <p/>
 * 
 * <pre>
 * NO.    Date    Modified By    Why & What is modified
 * </pre>
 * 
 * <pre>1     2011-06-03    lvjx			Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-03
 */
@Repository("bgGoodsDao")
public class GoodsDaoImpl extends GenericDaoImpl<Goods, Long> implements
		GoodsDao {

	/*
	 * @see
	 * com.beike.dao.background.goods.GoodsDao#addGoods(com.beike.form.background
	 * .goods.GoodsForm)
	 */
	public String addGoods(GoodsForm goodsForm) throws Exception {
		final GoodsForm form = goodsForm;
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO beiker_goods_info(goods_name,goods_tagid,goods_tagextid,goods_current_price,goods_source_price,goods_divide_price,");
		sql.append("goods_rebate_price,goods_max_count,goods_end_time,goods_logo,goods_logo_2,goods_logo_3,goods_logo_4,goods_introduction,");
		sql.append("goods_review,goods_story,goods_story_pic,guest_id,goods_status,goods_branch_id,goods_order_lose_abs_date,goods_order_lose_date,goods_modify_time ) ");
		sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now());");
		int flag = this.getJdbcTemplate().update(sql.toString(),
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, form.getGoodsName().trim());
						ps.setInt(2, form.getGoodsTagid());
						ps.setString(3, form.getGoodsTagextid());
						ps.setBigDecimal(4, form.getGoodsCurrentPrice());
						ps.setBigDecimal(5, form.getGoodsSourcePrice());
						ps.setBigDecimal(6, form.getGoodsDividePrice());
						ps.setBigDecimal(7, form.getGoodsRebatePrice());
						ps.setInt(8, form.getGoodsMaxCount());
						ps.setTimestamp(9, form.getGoodsEndTime());
						ps.setString(10, form.getGoodsLogo());
						ps.setString(11, form.getGoodsLogo2());
						ps.setString(12, form.getGoodsLogo3());
						ps.setString(13, form.getGoodsLogo4());
						ps.setString(14, form.getGoodsIntroduction());
						ps.setString(15, form.getGoodsReview());
						ps.setString(16, form.getGoodsStory());
						ps.setString(17, form.getGoodsStoryPic());
						ps.setInt(18, form.getGuestId());
						ps.setString(19, form.getGoodsStatus());
						ps.setString(20, form.getGoodsBranchId());
						ps.setInt(21, form.getGoodsOrderLoseAbsSate());
						ps.setTimestamp(22, form.getGoodsOrderLoseDate());
					}
				});
		return String.valueOf(flag);
	}

	/*
	 * @see
	 * com.beike.dao.background.goods.GoodsDao#queryGoods(com.beike.form.background
	 * .goods.GoodsForm)
	 */
	@SuppressWarnings("unchecked")
	public List<Goods> queryGoods(GoodsForm goodsForm, int startRow,
			int pageSize) throws Exception {
		List tempList = null;
		List<Goods> goodsList = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT k.guest_id guest_id,k.guest_cn_name guest_cn_name ,g.goods_id goods_id,g.goods_name goods_name,g.goods_tagid goods_tagid,");
		sql.append("g.goods_tagextid goods_tagextid,g.goods_current_price goods_current_price,g.goods_status goods_status,g.goods_on_time goods_on_time, k.brand_id brand_id ");
		sql.append("FROM beiker_goods_info g JOIN beiker_guest_info k ON k.guest_id = g.guest_id WHERE 1=1 ");
		if (goodsForm.getGoodsId() > 0) {
			sql.append(" AND g.goods_id = ").append(goodsForm.getGoodsId());
		}
		if (goodsForm.getGoodsTagid() > 0) {
			sql.append(" AND g.goods_tagid = ").append(
					goodsForm.getGoodsTagid());
		}
		if (goodsForm.getGuestId() > 0) {
			sql.append(" AND g.guest_id = ").append(goodsForm.getGuestId());
		}
		if (StringUtils.validNull(goodsForm.getGuestCnName())) {
			sql.append(" AND k.guest_cn_name like ").append(
					"'%" + goodsForm.getGuestCnName() + "%'");
		}
		if (StringUtils.validNull(goodsForm.getGoodsStatus())) {
			sql.append(" AND g.goods_status = ").append(
					"'" + goodsForm.getGoodsStatus() + "'");
		}
		/*
		 * if(StringUtils.validNull(goodsForm.getGoodsTagextid())){
		 * sql.append(" AND g.goods_tagextid = ").append(""); }
		 */
		if (null != goodsForm.getGoodsCurrentPriceBegin()
				&& null != goodsForm.getGoodsCurrentPriceEnd()) {
			sql.append(" AND g.goods_current_price BETWEEN ")
					.append(goodsForm.getGoodsCurrentPriceBegin())
					.append(" AND ")
					.append(goodsForm.getGoodsCurrentPriceEnd());
		}
		if (null != goodsForm.getGoodsCreateTimeBegin()
				&& null != goodsForm.getGoodsCreateTimeEnd()) {
			sql.append(" AND g.goods_on_time BETWEEN ")
					.append("'" + goodsForm.getGoodsCreateTimeBegin() + "'")
					.append(" AND ")
					.append("'" + goodsForm.getGoodsCreateTimeEnd() + "'");
		}
		if (goodsForm.getBrandId() > 0) {
			sql.append(" AND k.brand_id = ").append(goodsForm.getBrandId());
		}
		sql.append(" ORDER BY goods_id DESC LIMIT ?,? ");
		Object[] params = new Object[] { startRow, pageSize };
		int[] types = new int[] { Types.INTEGER, Types.INTEGER };
		tempList = this.getJdbcTemplate().queryForList(sql.toString(), params,
				types);
		if (null != tempList && tempList.size() > 0) {
			goodsList = this.convertResultToObjectList(tempList);
		}
		return goodsList;
	}

	/*
	 * @see
	 * com.beike.dao.background.goods.GoodsDao#queryGoodsCount(com.beike.form
	 * .background.goods.GoodsForm)
	 */
	public int queryGoodsCount(GoodsForm goodsForm) {
		int count = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) FROM beiker_goods_info g JOIN beiker_guest_info k ON k.guest_id = g.guest_id WHERE 1=1 ");
		if (goodsForm.getGoodsId() > 0) {
			sql.append(" AND g.goods_id = ").append(goodsForm.getGoodsId());
		}
		if (goodsForm.getGoodsTagid() > 0) {
			sql.append(" AND g.goods_tagid = ").append(
					goodsForm.getGoodsTagid());
		}
		if (goodsForm.getGuestId() > 0) {
			sql.append(" AND g.guest_id = ").append(goodsForm.getGuestId());
		}
		if (StringUtils.validNull(goodsForm.getGuestCnName())) {
			sql.append(" AND k.guest_cn_name like ").append(
					"'%" + goodsForm.getGuestCnName() + "%'");
		}
		if (StringUtils.validNull(goodsForm.getGoodsStatus())) {
			sql.append(" AND g.goods_status = ").append(
					"'" + goodsForm.getGoodsStatus() + "'");
		}
		/*
		 * if(StringUtils.validNull(goodsForm.getGoodsTagextid())){
		 * sql.append(" AND g.goods_tagextid = ").append(""); }
		 */
		if (null != goodsForm.getGoodsCurrentPriceBegin()
				&& null != goodsForm.getGoodsCurrentPriceEnd()) {
			sql.append(" AND g.goods_current_price BETWEEN ")
					.append(goodsForm.getGoodsCurrentPriceBegin())
					.append(" AND ")
					.append(goodsForm.getGoodsCurrentPriceEnd());
		}
		if (null != goodsForm.getGoodsCreateTimeBegin()
				&& null != goodsForm.getGoodsCreateTimeEnd()) {
			sql.append(" AND g.goods_on_time BETWEEN ")
					.append("'" + goodsForm.getGoodsCreateTimeBegin() + "'")
					.append(" AND ")
					.append("'" + goodsForm.getGoodsCreateTimeEnd() + "'");
		}
		if (goodsForm.getBrandId() > 0) {
			sql.append(" AND k.brand_id = ").append(goodsForm.getBrandId());
		}
		count = this.getJdbcTemplate().queryForInt(sql.toString());
		return count;
	}

	/*
	 * @see
	 * com.beike.dao.background.goods.GoodsDao#queryGoodsById(java.lang.String)
	 */
	public Goods queryGoodsById(String goodsId) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT goods_id,goods_name,goods_tagid,goods_tagextid,goods_current_price,goods_source_price,goods_divide_price,");
		sql.append("goods_rebate_price,goods_max_count,goods_end_time,goods_logo,goods_logo_2,goods_logo_3,goods_logo_4,goods_introduction,");
		sql.append("goods_review,goods_story,goods_story_pic,guest_id,goods_status,goods_branch_id,goods_order_lose_abs_date,");
		sql.append("goods_order_lose_date,goods_is_top FROM beiker_goods_info WHERE goods_id = ? ");
		Goods goods = this.getSimpleJdbcTemplate().queryForObject(
				sql.toString(),
				ParameterizedBeanPropertyRowMapper.newInstance(Goods.class),
				Integer.parseInt(goodsId));
		return goods;
	}

	/*
	 * @see
	 * com.beike.dao.background.goods.GoodsDao#editGoods(com.beike.form.background
	 * .goods.GoodsForm)
	 */
	public String editGoods(GoodsForm goodsForm) throws Exception {
		final GoodsForm form = goodsForm;
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE beiker_goods_info SET goods_name=?,goods_tagid=?,goods_tagextid=?,goods_current_price=?,goods_source_price=?,goods_divide_price=?,");
		sql.append("goods_rebate_price=?,goods_max_count=?,goods_end_time=?,goods_logo=?,goods_logo_2=?,goods_logo_3=?,goods_logo_4=?,goods_introduction=?,");
		sql.append("goods_review=?,goods_story=?,goods_story_pic=?,guest_id=?,goods_status=?,goods_branch_id=?,goods_order_lose_abs_date=?,");
		sql.append("goods_order_lose_date=?,goods_is_top=?,goods_modify_time=now()  WHERE goods_id = ? ");
		int flag = this.getJdbcTemplate().update(sql.toString(),
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, form.getGoodsName().trim());
						ps.setInt(2, form.getGoodsTagid());
						ps.setString(3, form.getGoodsTagextid());
						ps.setBigDecimal(4, form.getGoodsCurrentPrice());
						ps.setBigDecimal(5, form.getGoodsSourcePrice());
						ps.setBigDecimal(6, form.getGoodsDividePrice());
						ps.setBigDecimal(7, form.getGoodsRebatePrice());
						ps.setInt(8, form.getGoodsMaxCount());
						ps.setTimestamp(9, form.getGoodsEndTime());
						ps.setString(10, form.getGoodsLogo());
						ps.setString(11, form.getGoodsLogo2());
						ps.setString(12, form.getGoodsLogo3());
						ps.setString(13, form.getGoodsLogo4());
						ps.setString(14, form.getGoodsIntroduction());
						ps.setString(15, form.getGoodsReview());
						ps.setString(16, form.getGoodsStory());
						ps.setString(17, form.getGoodsStoryPic());
						ps.setInt(18, form.getGuestId());
						ps.setString(19, form.getGoodsStatus());
						ps.setString(20, form.getGoodsBranchId());
						ps.setInt(21, form.getGoodsOrderLoseAbsSate());
						ps.setTimestamp(22, form.getGoodsOrderLoseDate());
						ps.setString(23, form.getGoodsIsTop());
						ps.setInt(24, form.getGoodsId());
					}
				});
		return String.valueOf(flag);
	}

	/*
	 * @see
	 * com.beike.dao.background.goods.GoodsDao#downGoods(com.beike.form.background
	 * .goods.GoodsForm)
	 */
	public String downGoods(GoodsForm goodsForm) throws Exception {
		final GoodsForm form = goodsForm;
		String sql = "UPDATE beiker_goods_info SET goods_status = ?,goods_modify_time = now() WHERE goods_id = ? ";
		int flag = this.getJdbcTemplate().update(sql.toString(),
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, form.getGoodsStatus());
						ps.setInt(2, form.getGoodsId());
					}
				});
		return String.valueOf(flag);
	}

	/*
	 * @see
	 * com.beike.dao.background.goods.GoodsDao#isTopGoods(com.beike.form.background
	 * .top.GoodsForm)
	 */
	@SuppressWarnings("unchecked")
	public String queryGoodsTop(GoodsForm goodsForm) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT g.goods_id FROM beiker_goods_info g  ");
		sql.append("JOIN beiker_guest_info gi ON g.guest_id = gi.guest_id ");
		sql.append("WHERE gi.brand_id = ? AND g.goods_is_top = ? ");
		Object[] params = new Object[] { goodsForm.getBrandId(),
				goodsForm.getGoodsIsTop() };
		int[] types = new int[] { Types.INTEGER, Types.VARCHAR };
		int goodsId = 0;
		String goodId = "0";
		List listSize = this.getJdbcTemplate().queryForList(sql.toString(),
				params, types);
		if (null != listSize && listSize.size() > 0) {
			Map<String, Integer> obj = (Map<String, Integer>) listSize.get(0);
			if (obj != null && obj instanceof Number) {
				goodsId = ((Number) obj.get("goods_id")).intValue();
			}
		} else {
			goodsId = listSize.size();
		}
		if (goodsId > 0) {
			goodId = String.valueOf(goodsId);
		}
		return goodId;
	}

	/*
	 * @see
	 * com.beike.dao.background.goods.GoodsDao#editGoodsIsTop(com.beike.form
	 * .background.top.GoodsForm)
	 */
	public String editGoodsIsTop(GoodsForm goodsForm) throws Exception {
		final GoodsForm form = goodsForm;
		String sql = " UPDATE beiker_goods_info SET goods_is_top = ?,goods_modify_time = NOW() WHERE goods_id = ? ";
		int flag = this.getJdbcTemplate().update(sql.toString(),
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, form.getGoodsIsTop());
						ps.setInt(2, form.getGoodsId());
					}
				});
		return String.valueOf(flag);
	}

	/*
	 * @see
	 * com.beike.dao.background.goods.GoodsDao#addGoodsTop(com.beike.form.background
	 * .top.TopForm)
	 */
	public String addGoodsTop(TopForm topForm) throws Exception {
		final TopForm form = topForm;
		String sql = "INSERT INTO beiker_top(top_old_goods_id,top_new_goods_id,top_modify_time,top_status) VALUES (?,?,NOW(),?) ";
		int flag = this.getJdbcTemplate().update(sql.toString(),
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setInt(1, form.getTopOldGoodsId());
						ps.setInt(2, form.getTopNewGoodsId());
						ps.setString(3, form.getTopStatus());
					}
				});
		return String.valueOf(flag);
	}

	/*
	 * @see
	 * com.beike.dao.background.goods.GoodsDao#updateGoodsTop(com.beike.form
	 * .background.top.TopForm)
	 */
	public String updateGoodsTop(TopForm topForm) throws Exception {
		final TopForm form = topForm;
		String sql = "UPDATE beiker_top SET top_old_goods_id = ?,top_new_goods_id = ? ,top_status = ? ,top_modify_time = NOW() WHERE top_new_goods_id = ? ";
		int flag = this.getJdbcTemplate().update(sql.toString(),
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setInt(1, form.getTopOldGoodsId());
						ps.setInt(2, form.getTopNewGoodsId());
						ps.setString(3, form.getTopStatus());
						ps.setInt(4, form.getTopOldGoodsId());
					}
				});
		return String.valueOf(flag);
	}

	/*
	 * @see
	 * com.beike.dao.background.goods.GoodsDao#queryTopIsExist(com.beike.form
	 * .background.top.TopForm)
	 */
	public boolean queryTopIsExist(int topOldGoods) throws Exception {
		boolean flag = false;
		String sql = "SELECT COUNT(1) FROM beiker_top WHERE top_old_goods_id = ? AND top_status = '0' ";
		Object[] params = new Object[] { topOldGoods };
		int[] types = new int[] { Types.INTEGER };
		int size = this.getJdbcTemplate().queryForInt(sql, params, types);
		if (size > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 将查询结果（map组成的List）转化成具体的对象列表
	 * 
	 * @param results
	 *            jdbcTemplate返回的查询结果
	 * @return 具体的对象列表
	 * @author lvjx
	 */
	@SuppressWarnings("unchecked")
	private List<Goods> convertResultToObjectList(List results)
			throws Exception {
		List<Goods> objList = new ArrayList<Goods>();
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				Map result = (Map) results.get(i);
				Goods goods = this.convertResultMapToObject(result);
				objList.add(goods);
			}
		}
		return objList;
	}

	/**
	 * 将查询结果元素（map对象）转化为具体对象
	 * 
	 * @param result
	 *            jdbcTemplate返回的查询结果元素（map对象）
	 * @return 具体的对象类型
	 * @author lvjx
	 */
	@SuppressWarnings("unchecked")
	private Goods convertResultMapToObject(Map result) throws Exception {
		Goods obj = new Goods();
		if (result != null) {
			Long guestId = ((Number) result.get("guest_id")).longValue();
			if (null != guestId) {
				obj.setGuestId(guestId.intValue());
			}
			if (StringUtils.validNull((String) result.get("guest_cn_name"))) {
				obj.setGuestCnName(result.get("guest_cn_name").toString());
			}
			Long goodsId = ((Number) result.get("goods_id")).longValue();
			if (null != goodsId) {
				obj.setGoodsId(goodsId.intValue());
			}
			if (StringUtils.validNull((String) result.get("goods_name"))) {
				obj.setGoodsName(result.get("goods_name").toString());
			}
			Long goodsTagId = ((Number) result.get("goods_tagid")).longValue();
			if (null != goodsTagId) {
				obj.setGoodsTagid(goodsTagId.intValue());
			}
			if (StringUtils.validNull((String) result.get("goods_tagextid"))) {
				obj.setGoodsTagextid(result.get("goods_tagextid").toString());
			}
			BigDecimal bigDecimal = (BigDecimal) result
					.get("goods_current_price");
			if (null != bigDecimal) {
				// if(StringUtils.validNull((B)result.get("goods_current_price"))){
				BigDecimal currentPrice = (BigDecimal) result
						.get("goods_current_price");
				// bd=bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				obj.setGoodsCurrentPrice(currentPrice);
			}
			if (StringUtils.validNull((String) result.get("goods_status"))) {
				obj.setGoodsStatus(result.get("goods_status").toString());
			}
			Timestamp time = (Timestamp) result.get("goods_on_time");
			if (null != time) {
				Timestamp ts = (Timestamp) result.get("goods_on_time");
				obj.setGoodsCreateTime(ts);
			}
			Long brandId = ((Number) result.get("brand_id")).longValue();
			if (null != brandId) {
				obj.setBrandId(brandId.intValue());
			}

		}
		return obj;

	}

}
