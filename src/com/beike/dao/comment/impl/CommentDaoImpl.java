package com.beike.dao.comment.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.comment.CommentDao;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
@Repository("commentDao")
public class CommentDaoImpl extends GenericDaoImpl implements CommentDao{

	@Override
	public Long addComment(int isdefault,int score, String comment, Long merchantID,
			Long userid, Long goodsid, Long trxorderid, int ordercount) {
		String sql = "INSERT INTO beiker_order_evaluation(score,evaluation,merchantid,userid,goodsid,trxorderid,issysdefault, ADDTIME, STATUS,ordercount) VALUES(?,?,?,?,?,?,?,now(),0,?)";
		int result = getJdbcTemplate().update(sql, new Object[]{score,comment,merchantID,userid,goodsid,trxorderid,isdefault,ordercount});
		return getLastInsertId();
	}

	@Override
	public int addCommentPhoto(final List<String> photourl, final Long evaluationid) {
		String sql = "INSERT INTO beiker_evaluation_photo(evaluationid,photourl) VALUES(?,?)";
		int[] affectSize = getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public int getBatchSize() {
				return photourl.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				ps.setLong(1, evaluationid);
				ps.setString(2, photourl.get(i));
				
			}
			
		});
		return affectSize.length;
			
		
	}

	@Override
	public int updateBranchEvaluation(Long branchid, Long evaluationid,int ordercount) {
		StringBuilder be = new StringBuilder();
		//分店为0,表示使用第三方验证码
		if(branchid == 0){
			be.append("INSERT INTO beiker_branch_evaluation(branchid,evaluationid,ordercount) VALUES(0,").append(evaluationid).append(",").append(ordercount).append(")");
		}else{
			be.append("INSERT INTO beiker_branch_evaluation(branchid,evaluationid,ordercount) VALUES(").append(branchid).append(",").append(evaluationid).append(",").append(ordercount).append(")");
		}
		return getJdbcTemplate().update(be.toString());
	}

	@Override
	public int updateBranchProfile(Long merchantID, Long branchID,
			int well_count, int satisfy_count, int poor_count) {
	    String findsql = "SELECT bbp.id FROM beiker_branch_profile bbp WHERE bbp.merchantid=? AND bbp.branchid=? LIMIT 1";
	   List exists = getJdbcTemplate().queryForList(findsql,new Object[]{merchantID,branchID});
	   StringBuilder updatesql = new StringBuilder();
	   if(exists.size()>0){
		  updatesql.append("UPDATE beiker_branch_profile bbp SET bbp.well_count=bbp.well_count+").append(well_count).append(",bbp.satisfy_count=bbp.satisfy_count+").append(satisfy_count).append(",bbp.poor_count=bbp.poor_count+").append(poor_count).append(" WHERE bbp.merchantid=").append(merchantID).append(" AND bbp.branchid=").append(branchID);
	   }else{
		   updatesql.append("INSERT INTO beiker_branch_profile(merchantid,branchid,well_count,satisfy_count,poor_count) VALUES(").append(merchantID).append(",").append(branchID).append(",").append(well_count).append(",").append(satisfy_count).append(",").append(poor_count).append(")"); 
	   }
		return getJdbcTemplate().update(updatesql.toString());
	}

	@Override
	public int updateMerchanProfile(Long merchantid, int newscore,
			int mc_well_count, int mc_satisfy_count, int mc_poor_count) {
		String sql_mc_score = "SELECT bmp.mc_score FROM beiker_merchant_profile bmp WHERE bmp.merchantid=?";
		Long mc_score_now = getJdbcTemplate().queryForLong(sql_mc_score, new Object[]{merchantid});
		StringBuilder new_mc_score = new StringBuilder();
		if(mc_score_now+newscore>=0){
			new_mc_score.append("UPDATE beiker_merchant_profile bmp SET bmp.mc_score=bmp.mc_score+?,bmp.mc_well_count=bmp.mc_well_count+?,bmp.mc_satisfy_count=bmp.mc_satisfy_count+?,bmp.mc_poor_count=bmp.mc_poor_count+? WHERE bmp.merchantid=? AND bmp.mc_score=?");
			return getJdbcTemplate().update(new_mc_score.toString(),new Object[]{newscore,mc_well_count,mc_satisfy_count,mc_poor_count,merchantid,mc_score_now});
		}else{
			//店铺/商品得分不能小于0
			new_mc_score.append("UPDATE beiker_merchant_profile bmp SET bmp.mc_score=0,bmp.mc_well_count=bmp.mc_well_count+?,bmp.mc_satisfy_count=bmp.mc_satisfy_count+?,bmp.mc_poor_count=bmp.mc_poor_count+? WHERE bmp.merchantid=? AND bmp.mc_score=?");
			return getJdbcTemplate().update(new_mc_score.toString(),new Object[]{mc_well_count,mc_satisfy_count,mc_poor_count,merchantid,mc_score_now});
		}
	}
	
	@Override
	public int getEvaluateMerchantCountById(Long userId, Long merchantId, int score) {
		StringBuilder merCount = new StringBuilder();
		merCount.append("SELECT count(id) FROM beiker_order_evaluation where merchantid=? ");
		merCount.append("AND publishstatus=1 ");
		if(score >= 0 && score <= 2){
			merCount.append(" AND score=? ");
			return this.getSimpleJdbcTemplate().queryForInt(merCount.toString(), merchantId, score);
		}else{
			return this.getSimpleJdbcTemplate().queryForInt(merCount.toString(), merchantId);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getEvaluateMerchantID(Long userId, Long merchantId, int start, int end, int score) {
		StringBuilder merchantCount = new StringBuilder();
		merchantCount.append("SELECT id FROM beiker_order_evaluation where merchantid = ? ");
		merchantCount.append("AND publishstatus=1 ");
		if(score >=0 && score <= 2){
			merchantCount.append("AND score=? ");
		}
		
		if(userId == null || userId==0){
			merchantCount.append("ORDER BY addtime DESC ");
		}else{
			
			merchantCount.append("ORDER BY FIND_IN_SET(userid,'").append(userId).append("') DESC, addtime DESC ");
		}
		merchantCount.append("limit ?,?");
		
		if(score >=0 && score <= 2){
			return this.getJdbcTemplate().queryForList(merchantCount.toString(),
					new Object[]{merchantId,score,start,end}, Long.class);
		}else{
			return this.getJdbcTemplate().queryForList(merchantCount.toString(), 
					new Object[]{merchantId,start,end}, Long.class);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String,Object>> getEvaluationInfoByIds(List<Long> idList) {
		StringBuilder merBuilder = new StringBuilder();
		merBuilder.append("SELECT boe.id,boe.userid,boe.evaluation,boe.ordercount,boe.score,boe.addtime,boe.status,boe.goodsid,bg.goodsname,bg.isavaliable,bg.goods_title,boe.trxorderid ");
		merBuilder.append("From beiker_order_evaluation boe left join beiker_goods bg on bg.goodsid = boe.goodsid ");
		merBuilder.append("WHERE boe.id IN (").append(StringUtils.arrayToString(idList.toArray(), ",")).append(") ");
		merBuilder.append("ORDER BY FIND_IN_SET(boe.id,'").append(StringUtils.arrayToString(idList.toArray(), ",")).append("')");
		
		List<Map<String,Object>> lstRet = this.getSimpleJdbcTemplate().queryForList(merBuilder.toString());
		if(lstRet == null || lstRet.size() == 0){
			return null;
		}
		return lstRet;
	}
 
	@SuppressWarnings("unchecked")
	@Override
	public List getAllEvaluationForMerchant(Long merchangId) {
		
		String sql = "select mc_well_count,mc_satisfy_count,mc_poor_count from beiker_merchant_profile where merchantid = "+merchangId;
		List evalist = this.getJdbcTemplate().queryForList(sql);
		
		if(evalist == null || evalist.size() == 0)
			return null;
		
		return evalist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getRecommendGoodsidBySec(Long goodsid) {
		String sql = "SELECT bcg.tagextid FROM beiker_catlog_good bcg WHERE bcg.goodid=" +goodsid + " GROUP BY bcg.tagextid";
		List<Long> tagextid = getJdbcTemplate().queryForList(sql, Long.class);
		Long area_id = getAreaIDByGoodsid(goodsid);
		StringBuilder findsql = new StringBuilder();
		findsql.append("SELECT bcg.goodid FROM beiker_goods_profile bgp JOIN beiker_catlog_good bcg ON bgp.goodsid=bcg.goodid WHERE bcg.area_id=").append(area_id).append(" AND bcg.tagextid IN(").append(StringUtils.arrayToString(tagextid.toArray(), ",")).append(")").append(" AND bcg.isavaliable=1 AND bcg.goodid !=").append(goodsid).append(" AND bgp.well_count>0 GROUP BY bcg.goodid ORDER BY bgp.well_count DESC LIMIT 4");
		return getJdbcTemplate().queryForList(findsql.toString(),Long.class);
	}

	@Override
	public List<Long> getRecommendGoodsidByFir(Long goodsid,List<Long> notingoodsid, int rest,Long area_id) {
		String sql = "SELECT bcg.tagid FROM beiker_catlog_good bcg WHERE bcg.goodid=" + goodsid + " GROUP BY bcg.tagid";
		List<Long> tagid = getJdbcTemplate().queryForList(sql, Long.class);
		//Long area_id = getAreaIDByGoodsid(goodsid);
		
		StringBuilder findsql = new StringBuilder();
		findsql.append("SELECT bcg.goodid FROM beiker_goods_profile bgp JOIN beiker_catlog_good bcg ON bgp.goodsid=bcg.goodid WHERE bcg.area_id=")
		.append(area_id).append(" AND bcg.tagid IN(").append(StringUtils.arrayToString(tagid.toArray(), ",")).append(") AND bcg.isavaliable=1 AND bcg.goodid NOT IN(").append(goodsid);
		if(notingoodsid != null && notingoodsid.size()>0){
			findsql.append(",").append(StringUtils.arrayToString(notingoodsid.toArray(),","));
		}
		
		findsql.append(")").append(" GROUP BY bcg.goodid ORDER BY bgp.well_count DESC LIMIT ").append(rest);
		return getJdbcTemplate().queryForList(findsql.toString(), Long.class);
	}

	@Override
	public int updateGoodsProfile(Long goodsid,int well_count, int satisfy_count,
			int poor_count) {
		String updatesql = "UPDATE beiker_goods_profile bgp SET bgp.well_count=bgp.well_count+?,bgp.satisfy_count=bgp.satisfy_count+?,bgp.poor_count=bgp.poor_count+? WHERE bgp.goodsid=?";
		
		return  getJdbcTemplate().update(updatesql, new Object[]{well_count,satisfy_count,poor_count,goodsid});
	}

	@Override
	public List getTrxOrderInfo(boolean batch,Long goodsid, Long id,Long trx_order_id) {
		String sql = null;
		if(batch){
			sql = "SELECT btg.goods_id,btg.sub_guest_id,btg.id FROM beiker_trxorder_goods btg WHERE btg.goods_id=? AND btg.trxorder_id=? AND btg.trx_status='USED'";
			
			return getJdbcTemplate().queryForList(sql, new Object[]{goodsid,trx_order_id});
		}else{
			sql = "SELECT btg.sub_guest_id,btg.goods_id,btg.id FROM beiker_trxorder_goods btg WHERE btg.goods_id=? AND btg.id=? AND btg.trx_status='USED' LIMIT 1";
			return getJdbcTemplate().queryForList(sql, new Object[]{goodsid,id});
		}
	}

	@Override
	public List<Map<String, Object>> queryExpiredNoComment(int expiredDay) {
		String dateBefore = DateUtils.getTimeBeforeORAfter(expiredDay,"yyyy-MM-dd 00:00:00");
		
		StringBuilder selSql = new StringBuilder("select trxorder.trxorder_id,trxorder.id,trxorder.goods_id,trxorder.sub_guest_id,trx.user_id ");
		selSql.append("from beiker_trxorder as trx ")
			.append("inner join beiker_trxorder_goods as trxorder on trxorder.trxorder_id = trx.id ")
			.append("inner join beiker_voucher as voucher on voucher.voucher_id = trxorder.voucher_id ")
			.append("where trxorder.trx_status = 'USED' and trxorder.comment_id=0 and voucher.voucher_status = 'USED' and voucher.confirm_date < ? ")
			.append("limit 1000 ");
		
		return this.getSimpleJdbcTemplate().queryForList(selSql.toString(), dateBefore);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getEvaluateBrandID(Long userId, Long brandid, int start,int end,int score) {
		StringBuilder brandCount = new StringBuilder();
		brandCount.append("SELECT count(boe.id) FROM beiker_order_evaluation boe ");
		brandCount.append("LEFT JOIN beiker_branch_evaluation bbe ON boe.id = bbe.evaluationid where bbe.branchid = ? ");
		brandCount.append("AND boe.publishstatus=1 ");
		if(score >= 0 && score <= 2){
			brandCount.append("AND score = ? ");
		}
		
		if(userId==null || userId==0){
			brandCount.append("ORDER BY boe.addtime DESC");
		}else{
			brandCount.append("ORDER BY FIND_IN_SET(boe.userid,'").append(userId).append("') DESC, boe.addtime DESC ");
		}
		
		brandCount.append("limit ?,?");
		
		if(score >=0 && score <= 2){
			return this.getJdbcTemplate().queryForList(brandCount.toString(),
					new Object[]{brandid,score,start,end}, Long.class);
		}else{
			return this.getJdbcTemplate().queryForList(brandCount.toString(), 
					new Object[]{brandid,start,end}, Long.class);
		}
	}
	
	@Override
	public int getEvaluateBrandCountById(Long userId, Long brandid, int score) {
		StringBuilder brandCount = new StringBuilder();
		brandCount.append("SELECT boe.id FROM beiker_order_evaluation boe ");
		brandCount.append("LEFT JOIN beiker_branch_evaluation bbe ON boe.id = bbe.evaluationid ");
		brandCount.append("WHERE bbe.branchid=? ");
		brandCount.append("AND boe.publishstatus=1 ");
		if(score >= 0 && score  <= 2){
			brandCount.append("AND score=? ");
			return this.getSimpleJdbcTemplate().queryForInt(brandCount.toString(), brandid, score);
		}else{
			return this.getSimpleJdbcTemplate().queryForInt(brandCount.toString(), brandid);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getEvaluateGoodNameAndMerName(List<Long> ids) {
		StringBuilder evaName_sql = new StringBuilder();
		evaName_sql.append("SELECT bbe.evaluationid,bm.merchantname,bbe.branchid FROM beiker_branch_evaluation bbe ");
		evaName_sql.append("LEFT JOIN beiker_merchant bm ON bm.merchantid = bbe.branchid ");
		evaName_sql.append("where bbe.evaluationid in (").append(StringUtils.arrayToString(ids.toArray(), ",")).append(") ");
		List<Map<String, Object>> namelist = this.getSimpleJdbcTemplate().queryForList(evaName_sql.toString());
		return namelist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getAllEvaluateForBrand(Long brandid) {
		
		String sql = "SELECT well_count,satisfy_count,poor_count FROM beiker_branch_profile where branchid = "+brandid;
		
		List brandlist = this.getJdbcTemplate().queryForList(sql);
		return brandlist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String,Object>> getEvaluatePhoto(List<Long> ids) {
		StringBuilder photo_sql = new StringBuilder();
		photo_sql.append("SELECT evaluationid , photourl FROM beiker_evaluation_photo ");
		photo_sql.append("where evaluationid in (").append(StringUtils.arrayToString(ids.toArray(), ",")).append(") order by id");
		List photolist = this.getSimpleJdbcTemplate().queryForList(photo_sql.toString());
		return photolist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> gotoCommentPage(boolean batch,
			Long trx_order_id, Long id, Long goodsid) {
		StringBuilder sql = new StringBuilder();
		if(batch){
			sql.append("SELECT DISTINCT btg.id,btg.sub_guest_id AS guestid FROM beiker_trxorder_goods btg WHERE btg.trx_status='USED' AND btg.goods_id=").append(goodsid).append(" AND btg.trxorder_id=").append(trx_order_id);
		}else{
			sql.append("SELECT DISTINCT btg.id,btg.sub_guest_id AS guestid FROM beiker_trxorder_goods btg WHERE btg.trx_status='USED' AND btg.goods_id=").append(goodsid).append(" AND btg.id=").append(id).append(" AND btg.trxorder_id=").append(trx_order_id);
		}
		Map<String,Object> info = new HashMap<String, Object>();
		List ret = getJdbcTemplate().queryForList(sql.toString());
		if(ret == null || ret.size()<1){
			return null;
		}
		Map<Object,Object> temp = null;
		List<Long> branchidList = new ArrayList<Long>();
		for(int i=0;i<ret.size();i++){
			 temp = (Map) ret.get(i);
			 if(temp.get("guestid") != null){
				 branchidList.add((Long) temp.get("guestid"));
			 }
			 
		}
		//此次评论订单数
		 info.put("comment_total", ret.size());
		//查询分店
		sql.delete(0, sql.toString().length());
		sql.append("SELECT DISTINCT bm.merchantname FROM beiker_merchant bm WHERE bm.merchantid IN(").append(StringUtils.arrayToString(branchidList.toArray(), ",")).append(") AND bm.parentId!=0");
		List<String> branchNameList = getJdbcTemplate().queryForList(sql.toString(), String.class);
		info.put("branches",branchNameList);
		sql.delete(0, sql.toString().length());
		sql.append("SELECT COUNT(btg.id) AS t FROM beiker_trxorder_goods btg WHERE btg.goods_id=").append(goodsid).append(" AND btg.trxorder_id=").append(trx_order_id);
		Long total_order = getJdbcTemplate().queryForLong(sql.toString());
		//总订单数
		info.put("total_order", total_order);
		return info;
	}

	@Override
	public int findTrxOrder(Long trx_order_id, Long goodsid) {
		String sql = "SELECT COUNT(btg.id) FROM beiker_trxorder_goods btg WHERE btg.goods_id=? AND btg.trxorder_id=? AND btg.trx_status='USED'";
		return getJdbcTemplate().queryForInt(sql, new Object[]{goodsid,trx_order_id});
	}

	@Override
	public int findUserid(Long trx_order_id, Long userid) {
		String sql = "SELECT COUNT(bt.id) FROM beiker_trxorder bt WHERE bt.user_id=? AND bt.id=?";
		return getJdbcTemplate().queryForInt(sql,new Object[]{userid,trx_order_id});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getCatlogGoodByBrandid(Long merchantid) {
		
		StringBuilder query_catlog = new StringBuilder();
		
		query_catlog.append("SELECT btp.tag_name as tagname,btx.tag_name as tagextname,btp.id,btx.id,bgm.merchantid FROM beiker_goods_merchant bgm ");
		query_catlog.append("LEFT JOIN beiker_catlog_good  bcg ON bgm.merchantid=bcg.brandid ");
		query_catlog.append("LEFT JOIN beiker_tag_property btp ON bcg.tagid = btp.id ");
		query_catlog.append("LEFT JOIN beiker_tag_property btx ON bcg.tagextid=btx.id ");
		query_catlog.append("WHERE btp.parentid=0 AND btx.parentid=btp.id  AND bgm.goodsid=bcg.goodid AND bgm.merchantid = ? ");
		query_catlog.append("GROUP BY btx.id ");
		
		List catloglist = this.getJdbcTemplate().queryForList(query_catlog.toString(),new Object[]{merchantid});
		
		return catloglist;
	}

	@Override
	public Long getAreaIDByGoodsid(Long goodsid) {
		String sql = "SELECT DISTINCT bcg.area_id FROM beiker_catlog_good bcg WHERE bcg.goodid=?";
		return (Long) getJdbcTemplate().queryForObject(sql,new Object[]{goodsid},Long.class);
	}

	@Override
	public List<Long> getGoodsidByAreaid(Long area_id, List<Long> notin) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT bcg.goodid FROM beiker_catlog_good bcg WHERE bcg.area_id=").append(area_id).append(" AND bcg.isavaliable=1");
		if(notin != null && notin.size()>0){
			sql.append(" AND bcg.goodid NOT IN(")
			.append(StringUtils.arrayToString(notin.toArray(), ",")).append(")");
		}
		sql.append(" GROUP BY bcg.goodid");
		return getJdbcTemplate().queryForList(sql.toString(), Long.class);
	}

	@Override
	public List<Long> getTopSalesGoodsID(List<Long> availableGoodsid) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT btg.goods_id FROM beiker_trxorder_goods btg WHERE btg.create_date BETWEEN ? AND ? AND btg.trx_status !='INIT'")
		.append(" AND btg.goods_id IN(").append(StringUtils.arrayToString(availableGoodsid.toArray(), ",")).append(")")
		.append("GROUP BY btg.goods_id ORDER BY COUNT(btg.goods_id) DESC LIMIT 4");
		return getJdbcTemplate().queryForList(sql.toString(), new Object[] { DateUtils.getTimeBeforeORAfter(-1),
							DateUtils.getNowTime() }, Long.class);
	}

	@Override
	public List<Map<String, Object>> getAllEvaluationForGoodById(Long goodId) {
		String evasql = "SELECT well_count,satisfy_count,poor_count FROM beiker_goods_profile WHERE goodsid ="+goodId;
		return this.getSimpleJdbcTemplate().queryForList(evasql);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getEvaluateGoodsId(Long userId,
			Long goodId, int start, int end, int score) {
		StringBuilder query_evasql = new StringBuilder();
		query_evasql.append("SELECT id FROM beiker_order_evaluation where goodsid = ? ");
		query_evasql.append(" AND publishstatus=1 ");
		if(score>=0 && score <=2){
			query_evasql.append(" AND SCORE = ? ");
		}
		if(userId==null || userId==0){
			query_evasql.append(" ORDER BY addtime DESC ");
		}else{
			query_evasql.append(" ORDER BY FIND_IN_SET(userid,'").append(userId).append("') DESC, ADDTIME DESC ");
		}
		query_evasql.append(" limit ?,? ");
		
		if(score >=0 && score <= 2){
			return this.getJdbcTemplate().queryForList(query_evasql.toString(),
					new Object[]{goodId,score,start,end}, Long.class);
		}else{
			return this.getJdbcTemplate().queryForList(query_evasql.toString(), 
					new Object[]{goodId,start,end}, Long.class);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getOtherEvaluateGoodsId(String ids) {
		StringBuilder query_othersql = new StringBuilder();
		query_othersql.append("SELECT t1.id FROM (SELECT boe.id,boe.userid AS userid,boe.addtime,boe.goodsid FROM beiker_order_evaluation boe ");
		query_othersql.append("LEFT JOIN beiker_evaluation_photo bep ON bep.evaluationid = boe.id ");
		query_othersql.append("WHERE evaluation IS NOT NULL AND bep.photourl IS NOT NULL AND boe.score = 0 AND boe.publishstatus=1");
		query_othersql.append("AND boe.goodsid IN (").append(ids).append(") ");
		query_othersql.append("ORDER BY boe.addtime DESC) t1 GROUP BY t1.userid LIMIT 3");
		return this.getJdbcTemplate().queryForList(query_othersql.toString(), Long.class);
	}

	@Override
	public int getEvaluateGoodCountById(Long userId, Long goodId, int score) {
		StringBuilder query_evasql = new StringBuilder();
		query_evasql.append("SELECT count(id) FROM beiker_order_evaluation where goodsid=? ");
		query_evasql.append(" AND publishstatus=1 ");
		if(score>=0 && score <=2){
			query_evasql.append(" AND SCORE=? ");
			return this.getSimpleJdbcTemplate().queryForInt(query_evasql.toString(),goodId,score);
		}else{
			return this.getSimpleJdbcTemplate().queryForInt(query_evasql.toString(),goodId);
		}
	}
	@Override
	public Integer getEvaluateGoodCountById(Long goodId) {
		StringBuilder query_evasql = new StringBuilder();
		query_evasql.append("SELECT count(id) FROM beiker_order_evaluation where goodsid=? ");
		query_evasql.append(" AND publishstatus=1 ");
		return this.getSimpleJdbcTemplate().queryForInt(query_evasql.toString(),goodId);
	}

	@Override
	public List<Map<String,Object>> getEvaluationIdByMerchantId(List<Long> merchantIdList,int score,int count){
		if(null != merchantIdList && merchantIdList.size() > 0){
			String idStr = StringUtils.arrayToString(merchantIdList.toArray(), ",");
			StringBuilder sql = new StringBuilder("SELECT boe.id,boe.addtime FROM beiker_order_evaluation boe");
			sql.append(" LEFT JOIN beiker_branch_evaluation bbe ON bbe.evaluationid = boe.id")
			.append(" WHERE bbe.branchid IN(").append(idStr).append(")  AND boe.issysdefault = 0 AND boe.`status` = 0 ");
			if(score != 2){
				sql.append(" AND boe.score <> 2");
			}else{
				sql.append(" AND boe.score = 2 LIMIT ").append(count);
			}
			return this.getSimpleJdbcTemplate().queryForList(sql.toString());
		}else{
			return null;
		}
	}
	public int getOrderCountByTrxId(Long trxorder_id,Long goodsId){
		StringBuilder sql = new StringBuilder("SELECT COUNT(btg.id) AS t FROM beiker_trxorder_goods btg WHERE btg.goods_id=")
		.append(goodsId).append(" AND btg.trxorder_id=").append(trxorder_id);
		return getJdbcTemplate().queryForInt(sql.toString());
	}
}
