/**  
* @Title: GoodsActivityDaoImpl.java
* @Package com.beike.dao.activity.impl
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Sep 11, 2012 7:37:11 PM
* @version V1.0  
*/
package com.beike.dao.activity.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.activity.GoodsActivityDao;
import com.beike.entity.goods.GoodsActivity;

/**
 * @ClassName: GoodsActivityDaoImpl
 * @Description: 活动商品DAO
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Sep 11, 2012 7:37:11 PM
 *
 */
@Repository("goodsActivityDao")
public class GoodsActivityDaoImpl  extends GenericDaoImpl<GoodsActivity,Long> implements GoodsActivityDao {

	@Override
	public GoodsActivity findActivityByGoodsId(Long goodsId) throws Exception {
		String sql="SELECT act_id,goods_id,create_date FROM beiker_goods_activity WHERE goods_id = "+goodsId;
		List<Map<String,Object>> result = getSimpleJdbcTemplate().queryForList(sql);
		GoodsActivity  goodsActivity = null;
		if(result.size()>0){
			goodsActivity = new GoodsActivity();
			goodsActivity.setActId(Long.parseLong(result.get(0).get("act_id").toString()));
			goodsActivity.setGoodsId(Long.parseLong(result.get(0).get("goods_id").toString()));
			goodsActivity.setCreateDate(Timestamp.valueOf(result.get(0).get("create_date").toString()));
		}
		return goodsActivity;
	}
	

}
