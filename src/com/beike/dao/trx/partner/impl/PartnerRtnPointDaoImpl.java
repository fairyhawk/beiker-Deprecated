package com.beike.dao.trx.partner.impl;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.FilmGoodsOrder;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.partner.PartnerRtnPointDao;
import com.beike.entity.partner.PartnerRtnPoint;
import com.beike.util.EnumUtil;
import com.beike.util.StringUtils;
@Repository("partnerRtnPointDao")
public class PartnerRtnPointDaoImpl extends GenericDaoImpl<FilmGoodsOrder, Long> implements PartnerRtnPointDao{

	@Override
	public void addPartnerRtnPoint(PartnerRtnPoint partnerRtnsPoint)
			throws Exception {
		String istSql = "INSERT INTO beiker_partner_rtnpoint (trxorder_id,trx_goods_id,voucher_id,tag_id,rtn_point_type,partner_no,out_request_id,trx_goods_sn,rtn_point_rule,description,tag_name,create_date,modify_date ) "+
			"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		
		getSimpleJdbcTemplate().update(istSql, 
				partnerRtnsPoint.getTrxOrderId(),
				partnerRtnsPoint.getTrxGoodsId(),
				partnerRtnsPoint.getVoucherId(),
				partnerRtnsPoint.getTagId(),
				partnerRtnsPoint.getRtnPointType(),
				partnerRtnsPoint.getPartnerNo(),
				partnerRtnsPoint.getOutRequestId(),
				partnerRtnsPoint.getTrxGoodsSn(),
				EnumUtil.transEnumToString(partnerRtnsPoint.getRtnPointRule()),
				partnerRtnsPoint.getDescription(),
				partnerRtnsPoint.getTagName(),
				partnerRtnsPoint.getCreateDate(),
				partnerRtnsPoint.getModifyDate());
		
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> queryPartnerRtnPointByCondition(
			Map<String, String> condition) throws Exception {
		StringBuilder querySql = new StringBuilder("SELECT id,trxorder_id,trx_goods_id,voucher_id,tag_id,rtn_point_type,partner_no,out_request_id,trx_goods_sn,rtn_point_rule,description," +
				"tag_name,create_date,modify_date FROM beiker_partner_rtnpoint WHERE 1=1 ");
		
		//查询参数数组长度设定
		int size = condition.size();

		//查询参数数组
		Object[] params = new Object[size ];
		//查询参数类型数组
		int[] types = new int[params.length];
		//参数数组索引
		int index = 0;
		
		//查询条件封装
		if(StringUtils.validNull(condition.get("id"))) {
			querySql.append(" AND id= ?");
			params[index] = condition.get("id");
			types[index] = Types.INTEGER;
			index++;
		}
		if(StringUtils.validNull(condition.get("trxOrderId"))) {
			querySql.append(" AND trxorder_id = ?");
			params[index] = condition.get("trxOrderId");
			types[index] = Types.INTEGER;
			index++;
		}
		if(StringUtils.validNull(condition.get("trxGoodsId"))) {
			querySql.append(" AND trx_goods_id = ?");
			params[index] = condition.get("trxGoodsId");
			types[index] = Types.INTEGER;
			index++;
		}
		if(StringUtils.validNull(condition.get("voucherId"))) {
			querySql.append(" AND voucher_id = ?");
			params[index] = condition.get("voucherId");
			types[index] = Types.INTEGER;
			index++;
		}if(StringUtils.validNull(condition.get("tagId"))) {
			querySql.append(" AND tag_id = ?");
			params[index] = condition.get("tagId");
			types[index] = Types.INTEGER;
			index++;
		}if(StringUtils.validNull(condition.get("rtnPointType"))) {
			querySql.append(" AND rtn_point_type = ?");
			params[index] = condition.get("rtnPointType");
			types[index] = Types.INTEGER;
			index++;
		}
		if(StringUtils.validNull(condition.get("partnerNo"))) {
			querySql.append(" AND partner_no = ?");
			params[index] = condition.get("partnerNo");
			types[index] = Types.VARCHAR;
			index++;
		}
		if(StringUtils.validNull(condition.get("outRequestId"))) {
			querySql.append(" AND out_request_id = ?");
			params[index] = condition.get("outRequestId");
			types[index] = Types.VARCHAR;
			index++;
		}
		if(StringUtils.validNull(condition.get("trxGoodsSn"))) {
			querySql.append(" AND trx_goods_sn = ?");
			params[index] = condition.get("trxGoodsSn");
			types[index] = Types.VARCHAR;
			index++;
		}
		if(StringUtils.validNull(condition.get("rtnPointRule"))) {
			querySql.append(" AND rtn_point_rule = ?");
			params[index] = condition.get("rtnPointRule");
			types[index] = Types.VARCHAR;
			index++;
		}
		querySql.append(" ORDER BY  id, create_date   ");
		
		//结束
		
		
		List list = getJdbcTemplate().queryForList(querySql.toString(), params, types);
		
		 
		return list;
	}
	
}
