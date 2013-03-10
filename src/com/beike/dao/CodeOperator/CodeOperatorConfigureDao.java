package com.beike.dao.CodeOperator;

import java.util.Map;

/**
 * 发码商品质信息接口
 * @author yurenli
 *
 */
public interface CodeOperatorConfigureDao {

	public Map<String,String> findProductNumByGoodsId(Long goodsId,String apiType);
	
}
