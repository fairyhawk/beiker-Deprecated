/**  
* @Title: GoodsActivityService.java
* @Package com.beike.service.activity
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Sep 11, 2012 7:31:07 PM
* @version V1.0  
*/
package com.beike.service.activity;

import com.beike.entity.goods.GoodsActivity;

/**
 * @ClassName: GoodsActivityService
 * @Description: 活动商品service
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Sep 11, 2012 7:31:07 PM
 *
 */
public interface GoodsActivityService {

	public GoodsActivity findActivityByGoodsId(Long goodsId) throws Exception;

}
