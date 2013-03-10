/**  
* @Title: MergeWithBrandsDao.java
* @Package com.beike.dao.brand
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Aug 29, 2012 11:03:23 AM
* @version V1.0  
*/
package com.beike.dao.brand;

import java.util.List;
import java.util.Map;

import com.beike.dao.GenericDao;
import com.beike.entity.brand.MergeWithBrands;

/**
 * @ClassName: MergeWithBrandsDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Aug 29, 2012 11:03:23 AM
 *
 */
public interface MergeWithBrandsDao extends GenericDao<MergeWithBrands, Long>{

	/**
	 * 
	* @Title: getMergeWithBrands
	* @Description: 获取品牌聚合主商品
	* @param @param goodsid 商品标识
	* @param @return    设定文件
	* @return List<Map<String,String>>    返回类型
	* @throws
	 */
	public List<Map<String, Object>> getMergeWithBrands(Long goodsid)throws Exception;

}
