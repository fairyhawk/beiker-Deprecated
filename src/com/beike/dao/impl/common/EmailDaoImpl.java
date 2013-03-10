package com.beike.dao.impl.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.exception.BaseException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.common.EmailDao;
import com.beike.entity.common.Email;

/**
 * <p>
 * Title:Email模板操作类
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
 * @date May 5, 2011
 * @author ye.tian modify by wenhua.cheng
 * @version 1.0
 */
@Repository("emailDao")
public class EmailDaoImpl extends GenericDaoImpl<Email, Integer> implements
		EmailDao {

	public static int READ_BUFFER_SIZE = 1024;

	protected class RowMapperImpl implements ParameterizedRowMapper<Email> {
		public Email mapRow(ResultSet rs, int rowNum) throws SQLException {
			Email email = new Email();

			email.setId(rs.getInt("id"));
			email.setTemplatecode(rs.getString("templatecode"));
			Blob blob = rs.getBlob("templatecontent");
			StringBuilder sb = new StringBuilder();
			BufferedInputStream bi = null;
			try {
				bi = new BufferedInputStream(blob
						.getBinaryStream());
				byte[] data = new byte[READ_BUFFER_SIZE];
				for (int len = 0; (len = bi.read(data)) != -1;) {
					String content = new String(data, 0, len, "UTF-8");
					// System.out.println(content);
					sb.append(content);					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(bi!=null){
					try {
						bi.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			email.setTemplatecontent(sb.toString());

			// String emailSubject = "";
			String emailSubject = rs.getString("templatesubject");
			// modify by wenhua.cheng
			// /*try {
			// String temSubject = rs.getString("templatesubject");
			// if (temSubject != null) {
			// emailSubject = new String(temSubject.getBytes(), "UTF-8");
			// }
			// } catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			// }*/
			email.setTemplatesubject(emailSubject);
			return email;
		}
	}

	public Email findEmailTemplate(String templateCode) throws BaseException {
		String sql = "select * from beiker_emailtemplate where templatecode=?";
		return getSimpleJdbcTemplate().queryForObject(sql, new RowMapperImpl(),
				templateCode);
	}

}
