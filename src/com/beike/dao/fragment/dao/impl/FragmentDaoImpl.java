package com.beike.dao.fragment.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.fragment.dao.FragmentDao;
import com.beike.entity.common.Fragment;


/**
 * <p>Title:碎片管理 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Jul 4, 2011
 * @author ye.tian
 * @version 1.0
 */
@Repository("fragmentDao")
public class FragmentDaoImpl extends GenericDaoImpl<Fragment, Long> implements FragmentDao {

	public Fragment getFragment(String city, String page, String name) {
		String sql="select bf.fragmentid,bf.content from beiker_fragment bf where bf.city=? and bf.name=? and bf.page=? and ispublish=0";
		
		List list=this.getSimpleJdbcTemplate().queryForList(sql, city,name,page);
		Fragment fragment=new Fragment();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Map map=(Map) list.get(i);
				
				Long fragmentid=(Long) map.get("fragmentid");
				String content=(String) map.get("content");
				fragment.setFragmentid(fragmentid);
				fragment.setContent(content);
			}
		}
		return fragment;
	}

	public void insertFragment(Fragment fragment) {
		String sql="insert into beiker_fragment  (city,name,title,page,content,version,ispublish) values(?,?,?,?,?,?,?)";
		final Fragment ft=fragment;
		int flag = this.getJdbcTemplate().update(sql,new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, ft.getCity());
				ps.setString(2, ft.getName());
				ps.setString(3, ft.getTitle());
				ps.setString(4, ft.getPage());
				ps.setString(5, ft.getContent());
				ps.setLong(6, ft.getVersion());
				ps.setLong(7, ft.getIspublish());
			}
		});
	}

	public void updateFragment(Fragment fragment) {
		String sql="update beiker_fragment bf set bf.city=? and bf.name=? and bf.title=? and bf.page=? and bf.content=? and bf.version=? and bf.ispublish=? where bf.fragmentid=?";
		final Fragment ft=fragment;
		int flag = this.getJdbcTemplate().update(sql,new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, ft.getCity());
				ps.setString(2, ft.getName());
				ps.setString(3, ft.getTitle());
				ps.setString(4, ft.getPage());
				ps.setString(5, ft.getContent());
				ps.setLong(6, ft.getVersion());
				ps.setLong(7, ft.getIspublish());
				ps.setLong(8, ft.getFragmentid());
			}
		});
	}

	public List<Fragment> getFragment(String city, String page) {
		
		String sql="select bf.title,bf.fragmentid,bf.name,bf.type,bf.count from beiker_fragment bf where bf.city=?  and bf.page=? and ispublish='0'";
		List list=this.getJdbcTemplate().queryForList(sql, new Object[]{city,page});
		if(list==null||list.size()==0)return new ArrayList<Fragment>();
		List<Fragment> listFragment=new ArrayList<Fragment>();
		for(int i=0;i<list.size();i++){
			Map map=(Map) list.get(i);
			Fragment fragment=new Fragment();
			Long fragmentid=(Long) map.get("fragmentid");
			String fragmentName=(String) map.get("name");
			String fragmentType=(String) map.get("type");
			Long fragmentCount=(Long) map.get("count");
			String title=(String) map.get("title");
			
			fragment.setFragmentid(fragmentid);
			fragment.setName(fragmentName);
			fragment.setType(fragmentType);
			fragment.setCount(fragmentCount);
			fragment.setTitle(title);
			
			listFragment.add(fragment);
		}
		
		return listFragment;
	}

	public void insertTestData(String city) {
		String sql="select * from beiker_fragment bf where bf.city='beijing'";
		
		List list=this.getSimpleJdbcTemplate().queryForList(sql, city);
		
		
		
	}

}