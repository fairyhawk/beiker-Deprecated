package com.beike.dao.mobile.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.mobile.AppStatsDao;
import com.beike.model.lucene.APPRegion;
import com.beike.model.lucene.APPTag;
import com.beike.model.lucene.AppSearchQuery;

@Repository("appStatsDao")
public class AppStatsDaoImpl extends GenericDaoImpl implements AppStatsDao {

	
	
	
	
	
	private boolean isSelected(Integer id){
		if(id != null && !"".equals(id.toString())){
			return true;
		}
		
		return false;
	}
	

	@Override
	public Integer getRegionStats(AppSearchQuery query) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(distinct bgm.merchantid,bcg.goodid) FROM beiker_goods_merchant bgm JOIN beiker_merchant brand ON bgm.merchantid=brand.merchantid JOIN beiker_catlog_good bcg ON bcg.goodid=bgm.goodsid WHERE brand.parentId !=0 AND bcg.isavaliable=1 AND bcg.area_id=?");
		if(isSelected(query.getTagid())){
			sql.append(" AND bcg.tagid=").append(query.getTagid());
		}
		
		if(isSelected(query.getTagextid())){
			sql.append(" AND bcg.tagextid=").append(query.getTagextid());
		}
		
		if(isSelected(query.getRegionid())){
			sql.append(" AND bcg.regionid=").append(query.getRegionid());
		}
		
		if(isSelected(query.getRegionextid())){
			sql.append(" AND bcg.regionextid=").append(query.getRegionextid());
		}
		return getJdbcTemplate().queryForInt(sql.toString(),new Object[]{query.getCityid()});
	}


	@Override
	public List<Integer> getTagextid(int parentid,int cityid) {
		String sql = "SELECT btp.id tagextid FROM beiker_tag_property btp JOIN beiker_catlog_relation bcr ON bcr.catlogid=btp.id OR bcr.catlogextid=btp.id WHERE btp.parentid=?  and bcr.areaid=?";
		return getJdbcTemplate().queryForList(sql, new Object[]{parentid,cityid}, Integer.class);
	}

	@Override
	public List<Integer> getRegionextid(int parentid,int cityid) {
		String sql = "SELECT brp.id regionextid FROM beiker_region_property brp WHERE brp.parentid=? AND brp.areaid=?";
		return getJdbcTemplate().queryForList(sql,new Object[]{parentid,cityid},Integer.class);
	}

	@Override
	public List<Integer> getOnlineCityid() {
		String sql = "SELECT ba.area_id FROM beiker_area ba WHERE ba.area_is_online='1' ORDER BY ba.area_id ASC";
		return getJdbcTemplate().queryForList(sql, Integer.class);
	}

	@Override
	public List<APPTag> getAvailableTag(Integer cityid, Integer parentid) {
		String sql = null;
		if(parentid == 0){
			sql = "SELECT btp.id,btp.tag_name,btp.parentid,btp.boost FROM beiker_tag_property btp JOIN beiker_catlog_relation bcr ON btp.id =bcr.catlogid WHERE bcr.areaid=? AND bcr.catlogisavailable=1 AND btp.parentid=? AND bcr.catlogextid=0";
		}else{
			sql = "SELECT btp.id,btp.tag_name,btp.parentid,btp.boost FROM beiker_tag_property btp JOIN beiker_catlog_relation bcr ON btp.id =bcr.catlogextid WHERE bcr.areaid=? AND bcr.catlogextisavaliable=1 AND btp.parentid =? AND bcr.catlogextid !=0";
		}
		return getJdbcTemplate().query(sql, new Object[]{cityid,parentid}, new RowMapper(){

			@Override
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				
				APPTag at = new APPTag();
				at.setId(rs.getInt("id"));
				at.setTag_name(rs.getString("tag_name"));
				at.setParentid(rs.getInt("parentid"));
				at.setBoost(rs.getInt("boost"));
				return at;
			}
		});
	}

	@Override
	public List<APPRegion> getAvailableRegion(Integer cityid,
			Integer parentid) {
        String sql = "SELECT brp.id,brp.region_name,brp.parentid FROM beiker_region_property brp WHERE brp.areaid=? AND brp.parentid=?";
		return getJdbcTemplate().query(sql, new Object[]{cityid,parentid}, new RowMapper(){

			@Override
			public Object mapRow(ResultSet rs, int index) throws SQLException {
				APPRegion ar = new APPRegion();
				ar.setId(rs.getInt("id"));
				ar.setRegion_name(rs.getString("region_name"));
				ar.setParentid(rs.getInt("parentid"));
				return ar;
			}
		});
	}


	
}

