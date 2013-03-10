/**  
* @Title: FlagshipMapper.java
* @Package com.beike.dao.flagship
* @Description: TODO(用一句话描述该文件做什么)
* @author Grace Guo guoqingcun@gmail.com  
* @date 2013-1-16 下午4:04:45
* @version V1.0  
*/
package com.beike.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.config.SetFactoryBean;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.beike.entity.flagship.Flagship;

/**
 * @ClassName: FlagshipMapper
 * @Description: 旗舰店
 * @author Grace Guo guoqingcun@gmail.com
 * @date 2013-1-16 下午4:04:45
 *
 */
public class FlagshipMapper implements ParameterizedRowMapper<Flagship> {

	@Override
	public Flagship mapRow(ResultSet rs, int rowNum) throws SQLException {
		Flagship flagship = new Flagship();
		flagship.setId(rs.getLong("id"));
		flagship.setGuestId(rs.getLong("guest_id"));
		flagship.setBrandId(rs.getLong("brand_id"));
		flagship.setCity(rs.getLong("city"));
		flagship.setFlagshipBackgroundColor(rs.getString("flagship_background_color"));
		flagship.setFlagshipBackgroundImg(rs.getString("flagship_background_img"));
		flagship.setMouldId(rs.getLong("mould_id"));
		flagship.setMouldImg(rs.getString("mould_img"));
		flagship.setMouldName(rs.getString("mould_name"));
		flagship.setMouldUrl(rs.getString("mould_url"));
		flagship.setQqMicroBlog(rs.getString("qq_microBlog"));
		flagship.setRealmName(rs.getString("realm_name"));
		flagship.setSinaMicroBlog(rs.getString("sina_microBlog"));
		flagship.setBranchs(rs.getString("branchs"));
		flagship.setFlagshipLogo(rs.getString("flagship_logo"));
		flagship.setSinaMicroBlogName(rs.getString("sina_microBlog_name"));
		flagship.setFlagshipName(rs.getString("flagship_name"));
		return flagship;
	}

}
