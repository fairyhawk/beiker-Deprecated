package com.beike.dao.operation.wish;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.entity.user.User;
import com.beiker.model.operation.wish.InvitePrizeBean;
import com.beiker.model.operation.wish.InviteRecord;
import com.beiker.model.operation.wish.PrizePeopleInfo;
import com.beiker.model.operation.wish.WeiboInfo;
import com.beiker.model.operation.wish.InviteRecordBean;
import com.beiker.model.operation.wish.UserProfileBean;
import com.beiker.model.operation.wish.WishUser;
import com.beiker.model.operation.wish.WishUserRank;

@Repository
public class WishActivityDaoImpl extends GenericDaoImpl implements
		WishActivityDao {

	@Override
	public List<InviteRecord> getInviteRecordByUserID(String userid) {

		String sql = "select email,mobile,temp.fromweb,temp.registtime,temp.weiboid,temp.nickname,temp.awardno from (select awardno,targetid,registtime,fromweb,weiboid,nickname from beiker_inviteprize join beiker_inviterecord  on beiker_inviterecord.sourceid=beiker_inviteprize.userid where userid=? and inviterecord_id != 0) as temp join beiker_user bu on temp.targetid=bu.user_id";
		// String sql = "select
		// email,mobile,temp.fromweb,temp.registtime,temp.weiboid,profile.name,profile.value,profile.userid
		// from (select awardno,targetid,registtime,fromweb,weiboid from
		// beiker_inviteprize join beiker_inviterecord on userid=? and
		// inviterecord_id != 0) as temp join beiker_user bu on
		// temp.targetid=bu.user_id join beiker_userprofile profile on
		// profile.userid=temp.targetid";

		// String sql = "select
		// email,mobile,fromweb,bi.registtime,bi.weiboid,bp.name,bp.value,awardno
		// from beiker_inviterecord bi join beiker_user bu on
		// bi.sourceid=bu.user_id join beiker_userprofile bp on
		// bp.userid=bi.sourceid join beiker_inviteprize inviteprize on
		// inviteprize.userid in(select sourceid from beiker_inviterecord where
		// sourceid=?) and bp.userid=? and inviteprize.inventrecord_id != 0";
		List<InviteRecord> list = getJdbcTemplate().query(sql,
				new Object[] { userid }, new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						InviteRecord inviteRecord = new InviteRecord();
						inviteRecord.setAwardNo(rs.getString("awardno"));
						inviteRecord.setEmail(rs.getString("email"));
						inviteRecord.setRegisterTime(rs
								.getTimestamp("registtime"));
						inviteRecord.setSourceType(rs.getString("fromweb"));
						inviteRecord.setScreenName(rs.getString("nickname"));
						inviteRecord.setWeiboid(rs.getLong("weiboid"));
						inviteRecord.setMobile(rs.getString("mobile"));
						return inviteRecord;
					}
				});
		if (list.size() > 0) {
			return list;
		}
		return null;
	}

	public String formatAwardNo(String currentNo) {
		while (currentNo.length() < 6) {
			currentNo = "0" + currentNo;
		}
		return currentNo;
	}

	@Override
	public WishUser getWishUser(String userid) {

		String sql = "  select userid,awardno,inviterecord_id,registtime,fromweb from beiker_inviteprize prize join beiker_inviterecord record on prize.userid=record.sourceid  where prize.userid=? and prize.inviterecord_id=0";
		List<WishUser> userList = getJdbcTemplate().query(sql,
				new Object[] { userid }, new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						WishUser wishUser = new WishUser();
						wishUser.setAwardNo(rs.getString("awardno"));
						wishUser.setRegisterTime(rs.getTimestamp("registtime"));
						wishUser.setUserid(rs.getLong("userid"));
						wishUser.setFromweb(rs.getString("fromweb"));
						return wishUser;
					}
				});

		if (userList.size() > 0) {
			return userList.get(0);
		}
		return null;
	}

	@Override
	public List<WishUserRank> getWishUserRank() {
		String sql = "select distinct name,value,fromweb,weiboid,count_table.sourceid,count_table.total from (select sourceid,count(sourceid) as total from beiker_inviterecord group by sourceid order by sourceid desc) as count_table join beiker_userprofile bu on bu.userid=count_table.sourceid join beiker_inviterecord bi on bi.sourceid=bu.userid where name='sina_screenName' or name='tencent_scrrenName' limit 10";
		List<WishUserRank> userRankList = getJdbcTemplate().query(sql,
				new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						WishUserRank wishUserRank = new WishUserRank();
						// String name = rs.getString("name");
						wishUserRank.setScreenName(rs.getString("value"));
						wishUserRank.setInviteUsers(rs.getInt("total"));
						wishUserRank.setSourceType(rs.getString("fromweb"));
						wishUserRank.setWeiboid(rs.getInt("weiboid"));
						return wishUserRank;
					}
				});
		return userRankList;
	}

	@Override
	public Long getAttendNo() {
		String sql = "select count(distinct userid) as total from beiker_inviteprize";
		List<Long> total = getJdbcTemplate().query(sql, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("total");
			}
		});
		if (total == null || total.size() == 0)
			return 0L;
		Long attendNo = total.get(0);
		if (attendNo == null)
			return 0L;

		return attendNo;
	}

	private Long getInviteCount() {
		String sql = "select count(distinct sourceid) as total from beiker_inviterecord";
		List<Long> total = getJdbcTemplate().query(sql, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("total");
			}
		});
		if (total == null || total.size() == 0)
			return 0L;
		Long attendNo = total.get(0);
		if (attendNo == null)
			return 0L;

		return attendNo;
	}

	@Override
	public Long getInviteRank(String userid) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT  COUNT(temp2.total) rank FROM (SELECT COUNT(userid) AS total FROM beiker_inviteprize WHERE userid = ? GROUP BY userid ORDER BY total DESC) temp1 JOIN (SELECT COUNT(userid) AS total FROM beiker_inviteprize GROUP BY userid ORDER BY total DESC) temp2 ON temp2.total >= temp1.total");
		List<Long> rank = getJdbcTemplate().query(sb.toString(),
				new Object[] { userid }, new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getLong("rank");
					}
				});
		if (rank.size() > 0) {
			Long rowCount = rank.get(0);
			return rowCount;
		}
		return getInviteCount();
	}

	@Override
	public Long saveRecord(String userid, String targetid, String fromweb,
			String weiboid, String nickname,String mobile) {
		if (fromweb == null || "".equals(fromweb) || "null".equals(fromweb)) {
			fromweb = "EMAILCONFIG";
		}
		if (nickname == null) {
			nickname = "";
		}
		if (weiboid == null || "".equals(weiboid) || "".equals(weiboid)) {
			weiboid = "";
		}
		StringBuilder sb = new StringBuilder();
		if (userid == null || userid.trim().equals("")) {
			sb.append("insert into beiker_inviterecord(targetid,fromweb,weiboid,registtime,nickname,mobile) values(?,?,?,?,?,?)");
			getSimpleJdbcTemplate().update(sb.toString(), targetid, fromweb,
					weiboid, new Date(), nickname,mobile);
		} else {
			sb.append("insert into beiker_inviterecord(targetid,sourceid,fromweb,weiboid,registtime,nickname,mobile) values(?,?,?,?,?,?,?)");
			getSimpleJdbcTemplate().update(sb.toString(), targetid, userid,
					fromweb, weiboid, new Date(), nickname,mobile);
		}
		return super.getLastInsertId();
	}

	@Override
	public void saveNewAward(String userid, Long inviterecord_id, String number) {
		StringBuilder sb = new StringBuilder();
		if (inviterecord_id == null) {
			inviterecord_id = 0L;
		}
		sb.append(
				"insert into beiker_inviteprize(userid,awardno,inviterecord_id) values(")
				.append(userid).append(",'" + number + "',")
				.append(inviterecord_id).append(")");
		System.out.println("sb=" + sb.toString());
		getJdbcTemplate().execute(sb.toString());
	}

	@Override
	public Long getFalseCount() {
		String sql = "select sum(count) as sumlogs  from beiker_prizecountlog";
		Long sumlogs = null;
		try {
			sumlogs = this.getJdbcTemplate().queryForLong(sql);
		} catch (Exception e) {

		}
		if (sumlogs == null)
			return 0L;
		return sumlogs;

	}

	@Override
	public void saveFalseCount(int falseCount) {
		String sql = "insert into beiker_prizecountlog (count,inserdate) values(?,?)";
		this.getSimpleJdbcTemplate().update(sql, falseCount, new Date());
	}

	@Override
	public WeiboInfo getWeiboInfo(Long userid) {
		String sql = "select count(bi.targetid) as invitecount ,bu.value,bi.fromweb,bi.weiboid from beiker_userprofile bu left join beiker_inviterecord bi  on bu.userid=sourceid  where bi.fromweb=bu.profiletype and (bu.name='tencent_screenName' or bu.name='sina_screenName') and bu.userid=? group by bu.userid";

		List list = this.getSimpleJdbcTemplate().queryForList(sql, userid);
		if (list == null || list.size() == 0)
			return null;
		WeiboInfo weiboInfo = new WeiboInfo();
		Map map = (Map) list.get(0);
		String screenName = (String) map.get("value");
		String fromweb = (String) map.get("fromweb");
		String weiboid = (String) map.get("weiboid");
		Long invitecount = (Long) map.get("invitecount");
		weiboInfo.setScreenName(screenName);
		weiboInfo.setWeiboid(weiboid);
		weiboInfo.setInvitecount(invitecount);
		weiboInfo.setFromweb(fromweb);
		String url = "";
		if ("SINACONFIG".equals(fromweb)) {
			url = "http://weibo.com/" + weiboid;
		} else if ("TENCENTCONFIG".equals(fromweb)) {
			url = "http://t.qq.com/" + screenName;
		}
		weiboInfo.setUrl(url);

		return weiboInfo;

	}

	@Override
	public List<InviteRecordBean> findInviteRecord() {
		String sql = "SELECT  targetid, fromweb, weiboid,total,nickname FROM beiker_inviterecord t1 ,(SELECT userid,COUNT(*) AS 'total' FROM beiker_inviteprize GROUP BY userid LIMIT 10) t2 WHERE (t1.targetid=t2.userid) ORDER BY total desc";
		List<InviteRecordBean> list = getJdbcTemplate().query(sql,
				new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						InviteRecordBean ir = new InviteRecordBean();
						ir.setTargetId(rs.getInt("targetid"));
						ir.setFromWeb(rs.getString("fromweb"));
						ir.setWeiboId(rs.getString("weiboid"));
						ir.setNickName(rs.getString("nickname"));
						ir.setTotal(rs.getLong("total"));
						return ir;
					}
				});
		if (list == null || list.size() == 0) {
			return null;
		}
		return list;
	}

	public int findCountByUserId(String userId) {
		String sql = "SELECT count(*) from beiker_inviterecord where targetid =  "
				+ userId;
		return getSimpleJdbcTemplate().queryForInt(sql);
	}

	@Override
	public List<InviteRecordBean> findInviteRecordByUserId(int sourceId) {
		String sql = "SELECT  id, sourceid, targetid, fromweb, weiboid, registtime, nickname,mobile FROM beiker_inviterecord WHERE sourceid=?";
		List<InviteRecordBean> list = getJdbcTemplate().query(sql,
				new Object[] { sourceId }, new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						InviteRecordBean ir = new InviteRecordBean();
						ir.setId(rs.getLong("id"));
						ir.setSourceId(rs.getInt("sourceid"));
						ir.setTargetId(rs.getInt("targetid"));
						ir.setFromWeb(rs.getString("fromweb"));
						ir.setWeiboId(rs.getString("weiboid"));
						ir.setMobile(rs.getString("mobile"));
						Timestamp ts = rs.getTimestamp("registtime");
						
						if (ts != null) {
							ir.setRegistTime(String.format("%1$tF", ts));
						}
						ir.setNickName(rs.getString("nickname"));
						return ir;
					}
				});
		if (list == null || list.size() == 0) {
			return null;
		}
		return list;
	}

	@Override
	public List<InvitePrizeBean> findInvitePrizeByInventrecordId(
			long inventrecordId, int userId) {
		String sql = "SELECT id, userid, awardno, inviterecord_id FROM beiker_inviteprize WHERE inviterecord_id=? and userid="
				+ userId;
		List<InvitePrizeBean> list = getJdbcTemplate().query(sql,
				new Object[] { inventrecordId }, new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						InvitePrizeBean ipb = new InvitePrizeBean();
						ipb.setId(rs.getLong("id"));
						ipb.setAwardNo(rs.getString("awardno"));
						ipb.setUserId(rs.getInt("userid"));
						ipb.setInviterecord_id(rs.getLong("inviterecord_id"));
						return ipb;
					}
				});
		if (list == null || list.size() == 0) {
			return null;
		}
		return list;
	}

	@Override
	public List<UserProfileBean> findUserProfile(int sourceId,
			String profiletype) {
		String sql = "SELECT id, name, value, profiletype, userid, profiledate FROM beiker_userprofile WHERE userid=? AND profiletype=? GROUP BY userid, profiletype";
		List<UserProfileBean> list = getJdbcTemplate().query(sql,
				new Object[] { sourceId, profiletype }, new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						UserProfileBean up = new UserProfileBean();
						up.setId(rs.getLong("id"));
						up.setName(rs.getString("name"));
						up.setValue(rs.getString("value"));
						up.setProfileType("profiletype");
						up.setUserId(rs.getInt("userid"));
						return up;
					}
				});
		if (list == null || list.size() == 0) {
			return null;
		}
		return list;
	}

	public List<User> findUserByUserId(int sourceId) {
		String sql = "SELECT email, mobile FROM beiker_user WHERE user_id=?";
		List<User> list = getJdbcTemplate().query(sql,
				new Object[] { sourceId }, new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						User user = new User();
						user.setEmail(rs.getString("email"));
						user.setMobile(rs.getString("mobile"));
						return user;
					}
				});
		if (list == null || list.size() == 0) {
			return null;
		}
		return list;
	}

	@Override
	public Long getUserPrizeCount(Long userid) {
		String sql = "select count(userid) as total from beiker_inviteprize bi where bi.userid=?";
		List<Long> list = getJdbcTemplate().query(sql, new Object[] { userid },
				new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						Long total = (Long) rs.getLong("total");

						return total;
					}
				});
		if (list == null || list.size() == 0) {
			return 0L;
		}

		return list.get(0);

	}

	@Override
	public String getRegistUserPrizeNo(Long userid) {
		String sql = "select sourceid from beiker_inviterecord where targetid="+userid;
		Long sourceid =0L;
		try {
			sourceid = getSimpleJdbcTemplate().queryForObject(sql, Long.class);
		} catch (DataAccessException e1) {
			e1.printStackTrace();
		}
		if(sourceid==null||sourceid==0){
			sql = "select bi.awardno  from beiker_inviteprize bi where bi.userid=? and bi.inviterecord_id=0 limit 1";
		}else{
			sql = "select bi.awardno  from beiker_inviteprize bi where bi.userid=? and bi.inviterecord_id>0 order by id limit 1";
		}
		String awardno = null;
		try {
			awardno = (String) this.getJdbcTemplate().queryForObject(sql,
					new Object[] { userid }, String.class);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return awardno;
	}

	@Override
	public List<InviteRecordBean> createInviteRecordExtend() {
		String sql = "SELECT  targetid, fromweb, weiboid,total,nickname FROM beiker_inviterecord t1 ,(SELECT userid,COUNT(*) AS 'total' FROM beiker_inviteprize GROUP BY userid) t2 WHERE (t1.targetid=t2.userid) ORDER BY total desc,targetid desc LIMIT 10";
		List<InviteRecordBean> list = getJdbcTemplate().query(sql,
				new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						InviteRecordBean ir = new InviteRecordBean();
						ir.setTargetId(rs.getInt("targetid"));
						ir.setFromWeb(rs.getString("fromweb"));
						ir.setWeiboId(rs.getString("weiboid"));
						ir.setNickName(rs.getString("nickname"));
						ir.setTotal(rs.getLong("total"));
						return ir;
					}
				});
		if (list == null || list.size() == 0) {
			return null;
		}
		return list;
	}

	@Override
	public Long createInviteRankExtend(String userid) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(temp2.total)+1 rank FROM (SELECT COUNT(userid) AS total FROM beiker_inviteprize WHERE userid = ?) temp1 JOIN (SELECT COUNT(userid) AS total FROM beiker_inviteprize GROUP BY userid ORDER BY total DESC) temp2 ON temp2.total > temp1.total");
		List<Long> rank = getJdbcTemplate().query(sb.toString(),
				new Object[] { userid }, new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getLong("rank");
					}
				});
		if (rank.size() > 0) {
			Long rowCount = rank.get(0);
				return rowCount;
		}
		return getInviteCount();
	}

	@Override
	public PrizePeopleInfo getPeopleInfo() {
		String sql="select bi.title,bi.content from beiker_inviteprizepeople bi where bi.isavaliable=1 limit 1";
		List list=this.getJdbcTemplate().queryForList(sql);
		if(list==null||list.size()==0)
			return new PrizePeopleInfo();
		
		Map map=(Map) list.get(0);
		PrizePeopleInfo ppi=new PrizePeopleInfo();
		String title=(String) map.get("title");
		ppi.setTitle(title);
		String content=(String) map.get("content");
		ppi.setPrizeContent(content);
		return ppi;
		
	}

	@Override
	public boolean isActiveMobileExist(String mobile) {
		String sql="select bi.id,bi.sourceid,bi.targetid,bi.fromweb,bi.weiboid,bi.registtime,bi.nickname,bi.mobile from beiker_inviterecord bi where bi.mobile=?";
		List list=this.getJdbcTemplate().queryForList(sql, new Object[]{mobile});
		if(list==null||list.size()==0)return false;
		return true;
		
	}
}
