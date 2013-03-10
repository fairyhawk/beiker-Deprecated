package com.beike.dao.trx.partner;

import java.util.List;
import java.util.Map;

import com.beike.entity.partner.PartnerRtnPoint;
/**
 * 对PartnerRtnsPoint增加 修改 查询
 * @author ljp
 * @date 20121224
 */
public interface PartnerRtnPointDao {
	/**
	 * 插入PartnerRtnsPoint表
	 * @param partnerRtnsPoint
	 * @throws Exception
	 * @author ljp
	 * @date 20121224
	 */
	public void addPartnerRtnPoint( PartnerRtnPoint partnerRtnPoint)throws Exception;
	
	
	/**
	 * 查询PartnerRtnsPoint 表
	 * @param condition
	 * @return
	 * @throws Exception
	 * @author ljp
	 * @date 20121224
	 */
	public List<Object> queryPartnerRtnPointByCondition(Map<String, String> condition) throws Exception ;
	
}
