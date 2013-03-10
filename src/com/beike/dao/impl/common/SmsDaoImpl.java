package com.beike.dao.impl.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.common.SmsDao;
import com.beike.entity.common.Sms;
import com.beike.entity.common.SmsQuene;
import com.beike.form.SmsInfo;

/**
 * <p>
 * Title: 鐭俊妯℃澘
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
 * 
 * @date May 6, 2011
 * @author ye.tian
 * @version 1.0
 */
@Repository("smsDao")
public class SmsDaoImpl extends GenericDaoImpl<Sms, Integer> implements SmsDao {
	private final Log logger = LogFactory.getLog(SmsDaoImpl.class);

	protected class RowMapperImpl implements ParameterizedRowMapper<Sms> {
		@Override
		public Sms mapRow(ResultSet rs, int rowNum) throws SQLException {
			Sms sms = new Sms();
			sms.setSmscontent(rs.getString("smscontent"));
			sms.setSmstitle("smstitle");
			sms.setId(rs.getInt("id"));
			sms.setSmstype(rs.getString("smstype"));
			return sms;
		}

	}

	@Override
	public Sms getSmsByTitle(String title) {
		String sql = "select * from beiker_smstemplate where smstitle=?";
		Sms sms = getSimpleJdbcTemplate().queryForObject(sql,
				new RowMapperImpl(), title);
		return sms;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.beike.dao.common.SmsDao#saveSmsInfo(com.beike.form.SmsInfo)
	 */
	@Override
	public String saveSmsInfo(SmsInfo sourceBean) {
		final String addSql = "insert into beiker_smsqueue(mobile,smscontent,smstype,createdate,status) values(?,?,?,now(),'0')";
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = getSelfConnection();
			pstm = con.prepareStatement(addSql);
			pstm.setString(1, sourceBean.getDesMobile());
			pstm.setString(2, sourceBean.getContent());
			pstm.setString(3, sourceBean.getSmsType());
			boolean bool = pstm.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (pstm != null) {
				try {
					pstm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	@Override
	public List<SmsQuene> getSmsInfoList(String sendCount) {
		String selSql = "select id,mobile,smscontent from beiker_smsqueue where status='0' and length(mobile)=11 order by id desc limit "
				+ sendCount;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<SmsQuene> listSms = new LinkedList<SmsQuene>();
		try {
			con = getSelfConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(selSql);
			while (rs.next()) {
				SmsQuene sms = new SmsQuene();
				Long id = rs.getLong("id");
				String mobile = rs.getString("mobile");
				String smscontent = rs.getString("smscontent");
				sms.setId(id);
				sms.setMobile(mobile);
				sms.setSmscontent(smscontent);
				listSms.add(sms);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return listSms;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.beike.dao.common.SmsDao#sendSmsInfo(java.lang.String,
	 * java.lang.String, java.lang.String, int)
	 */
	@Override
	public String updateSmsInfo(String sendResult, String operId,
			String operPass, String sendUrl, SmsQuene smsQuene) {

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String updSql = "update beiker_smsqueue set senddate=?,status=? where id=?";
			con = getSelfConnection();
			pstm = con.prepareStatement(updSql);
			pstm.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			pstm.setString(2, sendResult);
			pstm.setLong(3, smsQuene.getId());
			pstm.addBatch();
			pstm.executeBatch();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstm != null) {
				try {
					pstm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 鑾峰彇鏁版嵁搴撹繛鎺�
	 * 
	 * @return
	 */
	public static Connection getSelfConnection() {
		Connection con = null;
		ResourceBundle rb = ResourceBundle.getBundle("smsconfig");
		String url = rb.getString("smsdburl");
		String username = rb.getString("smsdbuser");
		String password = rb.getString("smsdbpass");
		try {
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

}
