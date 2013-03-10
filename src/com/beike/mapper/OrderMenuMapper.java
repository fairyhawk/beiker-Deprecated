/**  
* @Title: OrderMenuMapper.java
* @Package com.beike.mapper
* @Description: TODO(用一句话描述该文件做什么)
* @author Grace Guo guoqingcun@gmail.com  
* @date 2013-1-16 下午6:01:19
* @version V1.0  
*/
package com.beike.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.beike.entity.onlineorder.OrderMenu;

/**
 * @ClassName: OrderMenuMapper
 * @Description: 点餐
 * @author Grace Guo guoqingcun@gmail.com
 * @date 2013-1-16 下午6:01:19
 *
 */
public class OrderMenuMapper implements ParameterizedRowMapper<OrderMenu> {

	@Override
	public OrderMenu mapRow(ResultSet rs, int rowNum) throws SQLException {
		OrderMenu om = new OrderMenu();
		om.setBranchId(rs.getLong("guest_id"));
		om.setMenuName(rs.getString("menu_name"));
		om.setMenuPrice(rs.getDouble("menu_price"));
		om.setMenuLogo(rs.getString("menu_logo"));
		return om;
	}

}
