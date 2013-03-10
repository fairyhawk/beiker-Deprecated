/**  
* @Title: GoodsActivityDao.java
* @Package com.beike.dao.activity
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Sep 11, 2012 7:32:44 PM
* @version V1.0  
*/
package com.beike.dao.activity;

import com.beike.dao.GenericDao;
import com.beike.entity.goods.GoodsActivity;

/**
 * @ClassName: GoodsActivityDao
 * @Description: 活动商品DAO
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Sep 11, 2012 7:32:44 PM
 *
 */
public interface GoodsActivityDao extends GenericDao<GoodsActivity, Long>{

	public GoodsActivity findActivityByGoodsId(Long goodsId) throws Exception;

}
