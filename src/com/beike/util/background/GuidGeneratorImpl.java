package com.beike.util.background;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.beike.common.entity.trx.GuidItem;
import com.beike.dao.GenericDaoImpl;

/**
 * @Title: GuidGeneratorImpl.java
 * @Package com.beike.common.guid
 * @Description: TODO
 * @author wh.cheng@sinobogroup.com
 * @date May 6, 2011 8:29:31 PM
 * @version V1.0
 */
public class GuidGeneratorImpl extends GenericDaoImpl<GuidItem, Long> implements
		GuidGenerator {
	/** Logger for this class */
	private static final Log logger = LogFactory
			.getLog(GuidGeneratorImpl.class);

	private static String localAddress;
	private static String spit = "";
	private static Map<String, Long> map = new HashMap<String, Long>();
	private static long maxAutoId;
	private static String randomString;
	private static String jvmId;
	private static boolean initFlag = false;

	public GuidGeneratorImpl() {
		init();
	}

	public String getGuid(String type) {
		String id = "";
		if (type == null || type.trim().length() == 0)
			type = "NULL";
		else if (type.length() > 4)
			type = type.substring(0, 4);
		id += type;
		id += spit;
		id += getLocalAddress(true);
		id += spit;
		id += getNowTime();
		id += spit;
		id += getAutoId(type);
		id += spit;
		id += getRandomString(2);

		return id;
	}

	public String getGuid() {
		return getGuid("GUID");
	}

	private synchronized void init() {
		if (initFlag == true)
			return;
		localAddress = getLocalAddress(true);
		initFlag = true;
		maxAutoId = this.getMaxNumber(8);
		logger.info("IP:" + localAddress + ",MaxAutoId:" + maxAutoId
				+ ",RandomString:" + getRandomString(2));
	}

	private String getNowTime() {
		DateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sf.format(new Date());
	}

	private synchronized String getAutoId(String type) {
		if (map.containsKey(type)) {
			Long no = map.get(type);
			String nos = getFixString(no.intValue(), 8);
			Long ti = no + 1;
			if (ti > maxAutoId)
				ti = 1L;
			map.put(type, ti);
			return nos;
		} else {
			String nos = getFixString(1, 8);
			map.put(type, 2L);
			return nos;
		}
	}

	private long getMaxNumber(int len) {
		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			sb.append("9");
		}
		return Long.parseLong(sb.toString());
	}

	private String getRandomString(int len) {
		if (randomString != null)
			return randomString;

		byte[] bs = new byte[len];
		Random rand = new SecureRandom();
		for (int i = 0; i < len; i++) {
			byte b = (byte) (65 + rand.nextInt(26));
			bs[i] = b;
		}
		randomString = new String(bs);
		return randomString;
	}

	private String getNewRandomString(int len, String exclude) {
		byte[] bs = new byte[len];
		Random rand = new SecureRandom();
		for (int i = 0; i < len; i++) {
			byte b = (byte) (65 + rand.nextInt(26));
			bs[i] = b;
		}
		randomString = new String(bs);
		if (exclude != null && exclude.indexOf(randomString) >= 0)
			return getNewRandomString(len, exclude);
		else
			return randomString;
	}

	private String getFixString(long num, int len) {
		String tp = "" + num;
		if (tp.length() == len)
			return tp;
		if (tp.length() > len)
			return tp.substring(0, len);
		for (int i = 0; i <= len / 4 + 1; i++) {
			tp = "00000" + tp;
		}
		return tp.substring(tp.length() - len);
	}

	public String getLocalAddress() {
		return getLocalAddress(false);
	}

	private String getLocalAddress(boolean onlyNumber) {
		if (localAddress != null)
			return localAddress;
		String ip = "";
		try {
			InetAddress ad = InetAddress.getLocalHost();
			ip = ad.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			if (onlyNumber)
				ip = "255255255255";
			else
				ip = "255.255.255.255";
		}
		if (!onlyNumber)
			return ip;
		StringBuffer sb = new StringBuffer(30);
		int begin = 0, size = ip.length();
		while (begin < size) {
			int t = ip.indexOf(".", begin);
			if (t < 0)
				t = size;
			String p = "000" + ip.substring(begin, t);
			sb.append(p.substring(p.length() - 3));
			begin = t + 1;
		}
		localAddress = sb.toString();
		try {
			if (localAddress.length() > 12)
				localAddress = localAddress
						.substring(localAddress.length() - 12);
		} catch (Exception e) {
			e.printStackTrace();
			localAddress = "255255255255";
		}
		return localAddress;
	}

	public String getJvmId() {
		if (jvmId != null && jvmId.trim().length() > 0) {
			// System.out.println("================JVM Id pro:" +
			// jvmId+"=============");
			return jvmId;
		} else {
			jvmId = System.getenv().get("JVMID");
			if (jvmId == null || jvmId.trim().length() == 0)
				jvmId = getNewRandomString(1, "A,B,C,D,E");
			logger.info("JVM Id:" + jvmId);
			// System.out.println("================JVM Id after:" +
			// jvmId+"=============");
			return jvmId;
		}
	}

	// @Transactional(propagation = Propagation.REQUIRES_NEW)
	public synchronized String getSpecialId(final String project, int length) {
		return this.getSpecialId(project, length, true);
	}

	public synchronized String getSpecialId(final String project, int length,
			boolean appendJvmId) {
		String iJvmId = getJvmId();
		String iSpecId;
		String qrySql = "select * from beiker_guiditem  where project=?";
		String updateSql = "update beiker_guiditem set auto_id=?  where project=?";
		String istSql = "insert into beiker_guiditem(project,auto_id) values(?,?)";

		List<GuidItem> guidItemlist = getSimpleJdbcTemplate().query(qrySql,
				new RowMapperImpl(), project);
		if (guidItemlist == null || guidItemlist.size() == 0) {
			// not exist item, use 1 and create new

			// 若根据业务查询为空，则写入一条
			getSimpleJdbcTemplate().update(istSql, project, 1L);
			if (appendJvmId)
				iSpecId = this.getFixString(1, length) + iJvmId;
			else
				iSpecId = this.getFixString(1, length);
		} else {
			// exist item , use item and update item
			GuidItem guidItem2 = (GuidItem) guidItemlist.get(0);
			long naid = guidItem2.getAutoId() + 1L;

			getSimpleJdbcTemplate().update(updateSql, naid, project);

			if (appendJvmId)
				iSpecId = this.getFixString(naid, length) + iJvmId;
			else
				iSpecId = this.getFixString(naid, length);
		}
		return iSpecId;
	}

	public String gainCode(String prefix,int length) {
		String no = getSpecialId(prefix, length);
		return prefix + no;
		// return no;
	}

	protected class RowMapperImpl implements ParameterizedRowMapper<GuidItem> {
		public GuidItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			GuidItem guidItem = new GuidItem();
			guidItem.setId(rs.getLong("id"));
			guidItem.setAutoId(rs.getLong("auto_id"));
			guidItem.setProject(rs.getString("project"));
			guidItem.setDescription(rs.getString("description"));
			return guidItem;
		}
	}

}
