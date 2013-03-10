package com.beike.dao.trx.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.TrxLog;
import com.beike.common.enums.trx.TrxLogType;
import com.beike.common.enums.trx.TrxlogSubType;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.TrxLogDao;
import com.beike.util.EnumUtil;

/**
 * @Title: TrxLogDaoImpl.java
 * @Package com.beike.dao
 * @Description: 交易相关业务日志，运营使用
 * @date Jun 30, 2011 5:18:10 PM
 * @author wh.cheng
 * @version v1.0
 */
@Repository("trxLogDao")
public class TrxLogDaoImpl extends GenericDaoImpl<TrxLog, Long> implements
		TrxLogDao {

	public Long addTrxLog(final TrxLog trxLog) {

		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (trxLog == null) {
			throw new IllegalArgumentException("trxLog not null");

		} else {
			final StringBuffer sb = new StringBuffer();
			sb.append("insert into beiker_trxlog(trx_goods_sn,create_date,trxlog_type,log_title,log_content,trxlog_sub_type) value(?,?,?,?,?,?)");

			this.getJdbcTemplate().update(new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement ps = con.prepareStatement(sb.toString(),
							new String[] { "trx_goods_sn", "create_date",
									"trxlog_type", "log_title","log_content","trxlog_sub_type" });

					ps.setString(1, trxLog.getTrxGoodsSn());
					ps.setTimestamp(2, new Timestamp(trxLog.getCreateDate()
							.getTime()));
					ps.setString(3, EnumUtil.transEnumToString(trxLog
							.getTrxLogType()));
					ps.setString(4,trxLog.getLogTitle());
					ps.setString(5, trxLog.getLogContent());
					ps.setString(6, EnumUtil.transEnumToString(trxLog.getTrxlogSubType()));

					return ps;
				}

			}, keyHolder);

		}

		Long trxLogId = keyHolder.getKey().longValue();
		return trxLogId;

	}
	
	
	public void  updateTrxLog(Long id,String content){
		if(id==null){
			
			throw new IllegalArgumentException();
		}
		
		String upSql="update beiker_trxlog set log_content=? where id=?";
		
		this.getSimpleJdbcTemplate().update(upSql,content,id);
		
		
	} 
	
	

	public TrxLog findTtxLogById(Long id){
		if(id ==null){
			throw new IllegalArgumentException("id is not null");
			
		}
		
		
		String qrySql="select id,trx_goods_sn,create_date,trxlog_type,log_content,log_title,trxlog_sub_type from beiker_trxlog where id=?";
		
		List<TrxLog> trxLogList=this.getSimpleJdbcTemplate().query(qrySql,new RowMapperImpl(), id);
		if(trxLogList.size()>0){
			
			return trxLogList.get(0);
		}
		
		
		return  null;
	}
	
	protected class RowMapperImpl implements ParameterizedRowMapper<TrxLog> {
		public TrxLog mapRow(ResultSet rs, int rowNum) throws SQLException {
			TrxLog trxLog = new TrxLog();
			trxLog.setId(rs.getLong("id"));
			trxLog.setTrxGoodsSn(rs.getString("trx_goods_sn"));
			trxLog.setCreateDate(rs.getTimestamp("create_date"));
			trxLog.setTrxLogType(EnumUtil.transStringToEnum(TrxLogType.class,rs.getString("trxlog_type")));
			trxLog.setLogContent(rs.getString("log_content"));
			trxLog.setLogTitle(rs.getString("log_title"));
			trxLog.setTrxlogSubType(EnumUtil.transStringToEnum(TrxlogSubType.class,rs.getString("trxlog_sub_type")));
			
			
			return trxLog;
		}
	}
	

}
