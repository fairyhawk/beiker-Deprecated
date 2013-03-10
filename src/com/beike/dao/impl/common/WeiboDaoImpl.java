package com.beike.dao.impl.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.beike.common.enums.user.ProfileType;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.WeiboDao;
import com.beike.entity.user.UserProfile;
import com.beike.util.DateUtils;

/**
 * <p>Title:微博、SNS相关属性实现</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 10, 2011
 * @author ye.tian
 * @version 1.0
 */
@Repository("weiboDao")
public class WeiboDaoImpl extends GenericDaoImpl<UserProfile, Long> implements WeiboDao {

	public void updateWeiboProType(Map<String, String> map,Long userid,ProfileType type) {
		//批量修改
		Set<String> set=map.keySet();
		String sqls[]=new String[map.size()];
		int index=0;
		for (String string : set) {
			String sql="update beiker_userprofile set value='"+map.get(string)+"' where userid="+userid+" and profiletype='"+type.toString()+"' and name='"+string+"'";
			sqls[index++]=sql;
		}
		getJdbcTemplate().batchUpdate(sqls);
	}

	public void addWeiboProType(final Map<String, String> map, Long userid,ProfileType userProfile) {
		//批量增加
		Set<String> set=map.keySet();
		String sqls[]=new String[map.size()];
		int index=0;
		for (String string : set) {
			String sql="insert into beiker_userprofile (name,value,profiletype,userid,profiledate)";
			sql+=" values('"+string+"','"+map.get(string)+"','"+userProfile+"',"+userid+",'"+DateUtils.dateToStrLong(new Date())+"')";
			sqls[index++]=sql;
		}
		getJdbcTemplate().batchUpdate(sqls);
	}
	
	

	public Map<String, String> getWeiboProType(Long userid, ProfileType type) {
		
		String sql="select * from beiker_userprofile where userid=? and profiletype=?";
		List list=getJdbcTemplate().queryForList(sql,new Object[]{userid,type.toString()});
		Map<String,String> m=new HashMap<String,String>();
		if(list!=null&&list.size()>0){
			for (Object object : list) {
				Map map=(Map) object;
				String name=(String) map.get("name");
				String value=(String) map.get("value");
				m.put(name, value);
			}
		}
		return m;
	}

	public void removeWeiboProType(Long userid, ProfileType profileType) {
		String sql="delete from beiker_userprofile where userid=? and profiletype=?";
		getSimpleJdbcTemplate().update(sql, userid,profileType.toString());
	}

	public Long getWeiboUserIdByProType(String weiboid, ProfileType type) {
		String sql="select userid from beiker_userprofile where value=? and profiletype=?";
		List list=getJdbcTemplate().queryForList(sql, new Object[]{weiboid,type.toString()});
		if(list==null||list.size()==0){
			return 0L;
		}
		Long userid=null;
		Map map=(Map) list.get(0);
		userid=(Long) map.get("userid");
		if(userid!=null){
			return userid;
		}
		return null;
	}

	public void removeBindingAccessTokenByWeiboId(String weiboid,
			ProfileType profileType) {
		Long userid=getWeiboUserIdByProType(weiboid, profileType);
		removeWeiboProType(userid, profileType);
	}

	public Map<String, String> getWeiboScreenName(Long userid) {
		
		String sql="select profiletype,value from beiker_userprofile where name like ?  and userid=?";
		List list=getSimpleJdbcTemplate().queryForList(sql, "%screenName%",userid);
		if(list==null||list.size()==0){
			return new HashMap<String,String>();
		}
		Map<String,String> map=new HashMap<String,String>();
		for(int i=0;i<list.size();i++){
			Map mapx=(Map) list.get(i);
			String profiletype=(String) mapx.get("profiletype");
			String value=(String) mapx.get("value");
			map.put(profiletype, value);
		}
		return map;
		
	}






}
