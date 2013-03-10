package com.beike.dao.impl.CodeOperator;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.CodeOperator.CodeOperatorConfigureDao;
import com.beike.entity.goods.Goods;

/**
 * 发码商品质信息接口实现
 * @author yurenli
 *
 */
@Repository("codeOperatorConfigureDao")
public class CodeOperatorConfigureDaoImpl extends GenericDaoImpl<Goods, Long> implements CodeOperatorConfigureDao{

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> findProductNumByGoodsId(Long goodsId,String apiType) {
		String sql = "select product_num from beiker_codeoperator_configure where goods_id=? and api_type=?";
		List<Map<String, String>> list = getJdbcTemplate().queryForList(sql, new Object[] { goodsId,apiType });
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}

}
