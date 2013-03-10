/**  
* @Title: GoodsActivityServiceImpl.java
* @Package com.beike.service.activity.impl
* @Description: TODO(用一句话描述该文件做什么)
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Sep 11, 2012 7:31:53 PM
* @version V1.0  
*/
package com.beike.service.activity.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.activity.GoodsActivityDao;
import com.beike.entity.goods.GoodsActivity;
import com.beike.service.activity.GoodsActivityService;

/**
 * @ClassName: GoodsActivityServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Sep 11, 2012 7:31:53 PM
 *
 */
@Service("goodsActivityService")
public class GoodsActivityServiceImpl implements GoodsActivityService {

	@Autowired
	private GoodsActivityDao goodsActivityDao;
	
	public void setGoodsActivityDao(GoodsActivityDao goodsActivityDao) {
		this.goodsActivityDao = goodsActivityDao;
	}
	@Override
	public GoodsActivity findActivityByGoodsId(Long goodsId) throws Exception{
		return goodsActivityDao.findActivityByGoodsId(goodsId);
	}
}
