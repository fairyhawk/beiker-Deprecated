/**  
* @Title: MergeWithBrandsDaoImpl.java
* @Package com.beike.dao.brand.impl
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Aug 29, 2012 11:14:58 AM
* @version V1.0  
*/
package com.beike.dao.brand.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.brand.MergeWithBrandsDao;
import com.beike.entity.brand.MergeWithBrands;

/**
 * @ClassName: MergeWithBrandsDaoImpl
 * @Description: 品牌聚合持久化
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Aug 29, 2012 11:14:58 AM
 *
 */
@Repository("mbDao")
public class MergeWithBrandsDaoImpl extends GenericDaoImpl<MergeWithBrands, Long> implements MergeWithBrandsDao {

	/**
	 * 
	* @Title: getMergeWithBrands
	* @Description: 获取品牌聚合主商品
	* @param @param goodsid 商品标识
	* @param @return    设定文件
	* @return List<Map<String,String>>    返回类型
	* @throws
	 */
	@Override
	public List<Map<String, Object>> getMergeWithBrands(Long goodsid) {
		String sql = "SELECT * FROM beiker_brand_unionpage WHERE GOODSID = ?";
		List<Map<String, Object>> mblist = getJdbcTemplate().queryForList(sql,	new Object[] { goodsid });
		return mblist;
	}
}
