/**  
* @Title: MergeWithBrandsService.java
* @Package com.beike.service.brand
* @Description: 品牌聚合业务逻辑接口
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Aug 29, 2012 10:59:26 AM
* @version V1.0  
*/
package com.beike.service.brand;

import com.beike.entity.brand.MergeWithBrands;

/**
 * @ClassName: MergeWithBrandsService
 * @Description: 品牌聚合业务逻辑接口
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Aug 29, 2012 10:59:26 AM
 *
 */
public interface MergeWithBrandsService {

	/**
	 * 
	* @Title: getMergeWithBrands
	* @Description: 根据商品标识获取品牌聚合数据
	* @param @param goodsid 商品标识
	* @param @return    设定文件
	* @return MergeWithBrands    返回类型
	* @throws
	 */
	public MergeWithBrands getMergeWithBrands(Long goodsid)throws Exception;

	public MergeWithBrands getMergeWithBrands(Long gsid, Long serialnum,String tpbbk, String zbqgg)throws Exception;

}
