package com.beike.action.pay.hessianclient;

import java.util.Map;

/**
 * 
 * @ClassName  AppHessianService
 * @package com.beike.service.hessian
 * @description
 * @author  liuqinggang
 * @Create Date: 2013-1-30 下午06:26:18
 *
 */
public interface FinanceHessianServiceGateway {
    /**
     * 
     * @param sourceMap
     * @return
     */
    public Map<String,String> guestCredit(Map<String,String> sourceMap);
    
    /**
     * 商家账户创建
     * @param inputMap    guestId
     * @return  msg 的值为success成功...其它都是失败
     * @author ljp
     * @date :　20130131
     */
    public Map<String, String> createGuestAccount(Map<String, String> inputMap);
    
    /**
	 * 冻结订单对商家账户的操作
	 * @param inputMap
	 * @return
	 * @author ljp
	 */
	public Map<String, String> freezeTrxOrderGoods(Map<String, String> inputMap);
	
	/**
	 * 解冻订单对商家账户的操作
	 * @param inputMap
	 * @return
	 */
	public Map<String, String> unFreezeTrxOrderGoods(Map<String, String> inputMap);

	
	 /**
     * 验证是否入账成功，查询账户历史
     * @param sourceMap
     * @return
     */
    public Map<String,String> getGuestCreditInfo(Map<String,String> sourceMap);
    

	
	/**
	 * 改变打款单状态，例如：将待打款单搁置，将搁置的打款单恢复及打款成功，失败等的状态改变
	 * @author Wenzhong Gu
	 * @param sourceMap
	 * @return
	 */
	public Map<String,String> modifyRemitRecordStatus(Map<String,String> sourceMap);

}
