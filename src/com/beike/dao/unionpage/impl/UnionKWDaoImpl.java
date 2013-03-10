package com.beike.dao.unionpage.impl;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.unionpage.UnionKWDao;


@SuppressWarnings("unchecked")
@Repository("unionKWDao")
public class UnionKWDaoImpl extends GenericDaoImpl implements UnionKWDao {

	@Override
	public List<String> getUsedKW(String isused,Long begin) {
		String sql = "SELECT buk.keyword FROM beiker_unionpage_keyword buk WHERE buk.isused=? ORDER BY buk.id LIMIT ?,1000";
		return getJdbcTemplate().queryForList(sql, new Object[] { isused,begin },
				String.class);
	}

	@Override
	public Long getKWCount(String isused) {
		String sql = "SELECT COUNT(buk.id) FROM beiker_unionpage_keyword buk WHERE buk.isused=?";
		return getJdbcTemplate().queryForLong(sql,new Object[]{isused});
	}

	@Override
	public String getKWUpdateTime() {
		String sql = "SELECT buk.updatetime FROM beiker_unionpage_keyword buk ORDER BY buk.id LIMIT 1";
		return getJdbcTemplate().queryForObject(sql, String.class).toString();
	}

	@Override
	public List<String> getUnUsedKW(Long begin) {
		String sql = "SELECT buk.keyword FROM beiker_unionpage_keyword buk WHERE buk.isused=1 ORDER BY buk.id LIMIT ?,1000";
		return getJdbcTemplate().queryForList(sql, new Object[] { begin },
				String.class);
	}
	/** 
	 * @date 2012-5-17
	 * @description:通过关键词Id查询关键词
	 * @param id
	 * @return String
	 * @throws 
	 */
	public String getKeyWordById(int id){
		String sql = "SELECT keyword FROM beiker_unionpage_keyword WHERE id = ?";
		Object[] params = new Object[]{id};
		List<Map<String,String>> listKeyWord = this.getJdbcTemplate().queryForList(sql, params);
		String keyWord = null;
		if(listKeyWord != null && listKeyWord.size() > 0){
			keyWord = listKeyWord.get(0).get("keyword");
		}
		return keyWord;
	}

	/** 
	 * @date 2012-5-18
	 * @description:查询所有的关键词信息，
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getAllKeyWordMsg(){
		String sql = "SELECT buk.id,buk.keyword FROM beiker_unionpage_keyword buk WHERE buk.isused='0' ORDER BY buk.id";
		List<Map<String,Object>> listKeyWord = getJdbcTemplate().queryForList(sql);
		return listKeyWord;
	}
	/** 
	 * @date 2012-5-21
	 * @description:通过关键词查询相应的关键词信息
	 * @param keyWord
	 * @param count
	 * @return List<Map<String,String>>
	 * @throws 
	 */
	public List<Map<String,String>> getMsgByKeyWord(String keyWord,int count){
		String sql = "SELECT buk.id,buk.keyword FROM beiker_unionpage_keyword buk WHERE buk.isused = 0 " +
							"AND buk.keyword IN ("+keyWord+") ORDER BY FIND_IN_SET(buk.keyword,"+ keyWord.replaceAll("','", ",") +") LIMIT 0,?";
		return this.getJdbcTemplate().queryForList(sql,new Object[]{count});
	
	}
}
