/**  
* @Title: MergeWithBrandsServiceImpl.java
* @Package com.beike.service.brand.impl
* @Description: 品牌聚合业务逻辑
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Aug 29, 2012 11:01:58 AM
* @version V1.0  
*/
package com.beike.service.brand.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.brand.MergeWithBrandsDao;
import com.beike.dao.goods.GoodsDao;
import com.beike.entity.brand.MergeWithBrands;
import com.beike.form.GoodsForm;
import com.beike.service.brand.MergeWithBrandsService;

/**
 * @ClassName: MergeWithBrandsServiceImpl
 * @Description: 品牌聚合业务逻辑
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Aug 29, 2012 11:01:58 AM
 *
 */
@Service("mbService")
public class MergeWithBrandsServiceImpl implements MergeWithBrandsService {

	@Autowired
	private MergeWithBrandsDao mbDao;//品牌聚合持久Dao
	@Autowired
	private GoodsDao goodsDao;
	
	public void setMbDao(MergeWithBrandsDao mbDao) {
		this.mbDao = mbDao;
	}
	public void setGoodsDao(GoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}
	/**
	 * 
	* @Title: getMergeWithBrands
	* @Description: 根据商品标识获取品牌聚合数据
	* @param @param goodsid 商品标识
	* @param @return    设定文件
	* @return MergeWithBrands    返回类型
	* @throws
	 */
	@Override
	public MergeWithBrands getMergeWithBrands(Long goodsid) throws Exception{
		//获取主商品
		List<Map<String,Object>> mbList = mbDao.getMergeWithBrands(goodsid);
		//配置聚合商品
		MergeWithBrands mb = null;
		if(mbList.size()>0){
			mb = new MergeWithBrands();
			Map<String,Object> mbMap = mbList.get(0);
			Long id = (Long)mbMap.get("id");
			String serialnum = mbMap.get("serialnum")+"";
			Long gsid = (Long)mbMap.get("goodsid");
			String tpbbk = mbMap.get("tpbbk")+"";
			String zbqgg = mbMap.get("zbqgg")+"";

//			List<GoodsForm> tpGoodsList = goodsDao.getGoodsByIds(tpbbk);
//			List<GoodsForm> zbGoodsList = goodsDao.getGoodsByIds(zbqgg);
			mb.setId(id);
			mb.setSerialnum(serialnum);
			mb.setGoodsid(gsid);
//			mb.setTpbbkList(tpbbk);
//			mb.setZbqggList(zbqgg);
			mb.setTpbbk(tpbbk);
			mb.setZbqgg(zbqgg);
		}
		return mb;
	}


	/**
	 * 
	* @Title: getMergeWithBrands
	* @Description: 根据商品标识获取品牌聚合数据---预览模式
	* @param @param goodsid 商品标识
	* @param @return    设定文件
	* @return MergeWithBrands    返回类型
	* @throws
	 */
	@Override
	public MergeWithBrands getMergeWithBrands(Long gsid, Long serialnum,String tpbbk, String zbqgg) throws Exception{
		
		//配置聚合商品
		MergeWithBrands mb = new MergeWithBrands();
//		List<GoodsForm> tpGoodsList = goodsDao.getGoodsByIds(tpbbk);
//		List<GoodsForm> zbGoodsList = goodsDao.getGoodsByIds(zbqgg);
		mb.setSerialnum(serialnum+"");
		mb.setGoodsid(gsid);
//		mb.setTpbbkList(tpGoodsList);
//		mb.setZbqggList(zbGoodsList);
		return mb;
	}

}
