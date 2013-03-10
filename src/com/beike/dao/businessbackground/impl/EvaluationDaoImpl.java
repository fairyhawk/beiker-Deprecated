package com.beike.dao.businessbackground.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.businessbackground.EvaluationDao;

/**
 * @Title：EvaluationDaoImpl.java
 * @Package com.beike.dao.businessbackground.impl
 * @Description：
 * @date：2013-1-28 - 下午12:30:38
 * @author：zhaojinglong@qianpin.com
 * @version
 */
@SuppressWarnings("unchecked")
@Repository("evaluationDao")
public class EvaluationDaoImpl extends GenericDaoImpl implements EvaluationDao {

	@Override
	public int getEvaluateCount(Map<String, Object> queryMap) {
		StringBuilder evaCountSql = new StringBuilder();
		evaCountSql.append("SELECT COUNT(ordereva.id) ");
		evaCountSql.append("FROM beiker_order_evaluation ordereva ");
		evaCountSql.append("JOIN beiker_trxorder_goods ordergoods ON ordereva.id = ordergoods.comment_id ");
		evaCountSql.append("JOIN beiker_user user ON ordereva.userid = user.user_id ");
		evaCountSql.append("WHERE ordereva.merchantid = ? ");
		String goodssn = (String)queryMap.get("goodssn");
		Long goodsid = (Long)queryMap.get("goodsid");
		String email = (String)queryMap.get("email");
		Long userid = (Long)queryMap.get("userid");
		Long subguestid = (Long)queryMap.get("subguestid");
		Integer score = (Integer)queryMap.get("score");
		List<Object> lstArgs = new ArrayList<Object>();
		lstArgs.add((Long)queryMap.get("merchantid"));  //品牌ID
		if(StringUtils.isNotEmpty(goodssn)){ //订单编号
			evaCountSql.append("AND ordergoods.trx_goods_sn = ? ");
			lstArgs.add(goodssn);
		}
		if(goodsid != null && goodsid > 0){ //商品编号－ID
			evaCountSql.append("AND ordereva.goodsid = ? ");
			lstArgs.add(goodsid);
		}
		if(StringUtils.isNotEmpty(email)){ //登录用户－email
			evaCountSql.append("AND user.email LIKE ? ");
			lstArgs.add(email + "%");
		}
		if(userid != null && userid > 0){ //会员ID
			evaCountSql.append("AND ordereva.userid = ? ");
			lstArgs.add(userid);
		}
		if(subguestid != null){ //所消费分店
			evaCountSql.append("AND ordergoods.sub_guest_id = ? ");
			lstArgs.add(subguestid);
		}
		if(score != null && score >= 0 && score <= 2){ //评价级别
			evaCountSql.append("AND ordereva.score = ? ");
			lstArgs.add(score);
		}
		return this.getSimpleJdbcTemplate().queryForInt(evaCountSql.toString(), lstArgs.toArray());
	}

	@Override
	public List<Map<String, Object>> getScrollEvaluate(Map<String, Object> queryMap, int curPage, int pageSize) {
		//可以查询出来的：订单编号，商品ID，登录用户，评价级别，评价时间，评价内容
		//缺少的：商品名称，商品类型，所消费分店
		StringBuilder evaSql = new StringBuilder();
		evaSql.append("SELECT ordereva.id, ordergoods.trx_goods_sn AS goodssn, ordereva.goodsid, user.email, ordereva.score, ");
		evaSql.append("ordereva.addtime AS addtime, ordereva.evaluation AS evaluation, ordergoods.sub_guest_id AS subguestid, ordergoods.biz_type AS biztype ");
		evaSql.append("FROM beiker_order_evaluation ordereva ");
		evaSql.append("JOIN beiker_trxorder_goods ordergoods ON ordereva.id = ordergoods.comment_id ");
		evaSql.append("JOIN beiker_user user ON ordereva.userid = user.user_id ");
		evaSql.append("WHERE ordereva.merchantid = ? ");
		String goodssn = (String)queryMap.get("goodssn");
		Long goodsid = (Long)queryMap.get("goodsid");
		String email = (String)queryMap.get("email");
		Long userid = (Long)queryMap.get("userid");
		Long subguestid = (Long)queryMap.get("subguestid");
		Integer score = (Integer)queryMap.get("score");
		List<Object> lstArgs = new ArrayList<Object>();
		lstArgs.add((Long)queryMap.get("merchantid"));  //品牌ID
		if(StringUtils.isNotEmpty(goodssn)){ //订单编号
			evaSql.append("AND ordergoods.trx_goods_sn = ? ");
			lstArgs.add(goodssn);
		}
		if(goodsid != null && goodsid > 0){ //商品编号－ID
			evaSql.append("AND ordereva.goodsid = ? ");
			lstArgs.add(goodsid);
		}
		if(StringUtils.isNotEmpty(email)){ //登录用户－email
			evaSql.append("AND user.email LIKE ? ");
			lstArgs.add(email + "%");
		}
		if(userid != null && userid > 0){ //会员ID
			evaSql.append("AND ordereva.userid = ? ");
			lstArgs.add(userid);
		}
		if(subguestid != null){ //所消费分店
			evaSql.append("AND ordergoods.sub_guest_id = ? ");
			lstArgs.add(subguestid);
		}
		if(score != null && score >= 0 && score <= 2){ //评价级别
			evaSql.append("AND ordereva.score = ? ");
			lstArgs.add(score);
		}
		evaSql.append("ORDER BY ordereva.addtime DESC ");
		evaSql.append("LIMIT ?, ?");
		lstArgs.add((curPage - 1) * pageSize);
		lstArgs.add(pageSize);
		return this.getJdbcTemplate().queryForList(evaSql.toString(), lstArgs.toArray());
	}
	
	public List<Map<String, Object>>  getGoodsByIds(Set<Long> goodsidsSet){
		String goodsids = StringUtils.join(goodsidsSet, ",");
		if(StringUtils.isNotEmpty(goodsids)){
			String sql = "SELECT goods.goodsid, goods.goodsname, goods.couponcash FROM beiker_goods goods WHERE goods.goodsid IN(" + goodsids + ")";
			return this.getJdbcTemplate().queryForList(sql);
		}
		return null;
	}
	
	public List<Map<String, Object>> getSubGuests(Set<Long> subguestidsSet){
		String subguestids = StringUtils.join(subguestidsSet, ",");
		if(StringUtils.isNotEmpty(subguestids)){
			String sql = "SELECT merchant.merchantid, merchant.merchantname FROM beiker_merchant merchant WHERE merchant.merchantid IN(" + subguestids + ")";
			return this.getJdbcTemplate().queryForList(sql);
		}
		return null;
	}

}
