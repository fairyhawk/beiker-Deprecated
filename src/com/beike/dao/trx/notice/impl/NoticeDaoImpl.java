package com.beike.dao.trx.notice.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.notice.Notice;
import com.beike.common.enums.trx.NoticeStatus;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.notice.NoticeDao;
import com.beike.util.EnumUtil;

/**   
 * @title: NoticeDaoImpl.java
 * @package com.beike.dao.trx.notice.impl
 * @description: NoticeDao实现
 * @author wangweijie  
 * @date 2012-6-13 上午10:40:04
 * @version v1.0   
 */

@Repository("noticeDao")
public class NoticeDaoImpl extends GenericDaoImpl<Notice,Long> implements NoticeDao {

	@Override
	public Long addNotice(Notice notice) {
		if (null == notice){
			throw new IllegalArgumentException("notice not null!");
		}
		StringBuilder insertSqlSb=new StringBuilder();
		insertSqlSb.append("insert beiker_notice(host_no,notice_type,request_id,content,count,random_count,method_type,");
		insertSqlSb.append("status,rsp_msg,create_date,modify_date,token) values (?,?,?,?,?,?,?,?,?,?,?,?)");
		
		getSimpleJdbcTemplate().update(insertSqlSb.toString(),notice.getHostNo(),notice.getNoticeType(),notice.getRequestId(),
				notice.getContent(),notice.getCount(),notice.getRandomCount(),notice.getMethodType(),EnumUtil.transEnumToString(notice.getStatus()),notice.getRspMsg(),
				new Timestamp(notice.getCreateDate().getTime()),new Timestamp(notice.getModifyDate().getTime()),notice.getToken());
		
		return getLastInsertId();
	}

	@Override
	public Notice findById(Long id) {
		if (id == null  || id.intValue()==0) {
			
			throw new IllegalArgumentException("notice not null or 0!");
		}
		StringBuilder sqlSb=new StringBuilder();
		sqlSb.append("select id,host_no,notice_type,request_id,content,count,random_count,method_type,status,rsp_msg,");
		sqlSb.append("create_date,modify_date,version,token from beiker_notice where id = ?");
		
		List<Notice> noticeList = getSimpleJdbcTemplate().query(sqlSb.toString(),new RowMapperImpl(), id);
		
		if (noticeList!=null && noticeList.size() > 0) {
			return noticeList.get(0);
		}
		return null;
	}

	@Override
	public List<Notice> findNoticeListByStatus(NoticeStatus status) {
		if (status== null) {
			
			throw new IllegalArgumentException("notice not null!");
		}
		StringBuilder sqlSb=new StringBuilder();
		sqlSb.append("select id,host_no,notice_type,request_id,content,count,random_count,method_type,status,rsp_msg,");
		sqlSb.append("create_date,modify_date,version,token from beiker_notice where status = ?");
	
		return getSimpleJdbcTemplate().query(sqlSb.toString(),new RowMapperImpl(), EnumUtil.transEnumToString(status));
	
	}

	@Override
	public void updateNotice(Notice notice) throws StaleObjectStateException{
		if (null == notice) {
			throw new IllegalArgumentException("notice not null");
		}
		String updateSql = "update beiker_notice set count=?,random_count=?,status=?,rsp_msg=?,modify_date=?,version=version+1 where id=? and version=? ";
		
		int result = getSimpleJdbcTemplate().update(updateSql,notice.getCount(),notice.getRandomCount(),EnumUtil.transEnumToString(notice.getStatus()),
				notice.getRspMsg(),new Timestamp(notice.getModifyDate().getTime()),notice.getId(), notice.getVersion());
		
		if (result == 0) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}
	
	
	@Override
	public void updateNoticeStatusById(NoticeStatus status,Long id,Long version)throws StaleObjectStateException {
		if (null == status ||  id==null ||  id.intValue()==0 || version==null  ) {
			throw new IllegalArgumentException("status  and id and  version  not null");
		}
		String updateSql = "update beiker_notice set status=?,modify_date=?,version=version+1 where id=? and version=? ";
		
		int result = getSimpleJdbcTemplate().update(updateSql,EnumUtil.transEnumToString(status),
				new Timestamp(new Date().getTime()),id,version);
		
		if (result == 0) {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}
	}
	
	public Notice findTokenByHostNoNoticeTypeRequestId(String hostNo,String methodType,String requestId){

		if (hostNo== null||methodType==null||requestId==null) {
			
			throw new IllegalArgumentException("notice not null!");
		}
		StringBuilder sqlSb=new StringBuilder();
		sqlSb.append("select id,host_no,notice_type,request_id,content,count,random_count,method_type,status,rsp_msg,");
		sqlSb.append("create_date,modify_date,version,token from beiker_notice where host_no = ? and method_type = ? and request_id = ?");
	
		return getSimpleJdbcTemplate().query(sqlSb.toString(),new RowMapperImpl(), hostNo,methodType,requestId).get(0);
	
	
	}
	
	protected class RowMapperImpl implements ParameterizedRowMapper<Notice> {
		public Notice mapRow(ResultSet rs, int rowNum) throws SQLException {
			Notice notice = new Notice();
			notice.setId(rs.getLong("id"));
			notice.setHostNo(rs.getString("host_no"));
			notice.setNoticeType(rs.getString("notice_type"));
			notice.setRequestId(rs.getString("request_id"));
			notice.setContent(rs.getString("content"));
			notice.setRandomCount(rs.getInt("random_count"));
			notice.setCount(rs.getInt("count"));
			notice.setMethodType(rs.getString("method_type"));
			notice.setStatus(EnumUtil.transStringToEnum(NoticeStatus.class,rs.getString("status")));
			notice.setRspMsg(rs.getString("rsp_msg"));
			notice.setCreateDate(rs.getTimestamp("create_date"));
			notice.setModifyDate(rs.getTimestamp("modify_date"));
			notice.setVersion(rs.getLong("version"));
			notice.setToken(rs.getString("token"));
			return  notice;
		}
	}

	
	
}
