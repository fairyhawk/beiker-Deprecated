package com.beike.common.enums.trx;    
/**   
 * @Title: CheckStatus.java
 * @Package com.beike.common.enums.trx
 * @Description: 支付结构侧对账状态
 * @date May 12, 2011 9:19:24 PM
 * @author wh.cheng
 * @version v1.0   
 */
public enum ProCheckStatus {

	MATCHED,UNMATCHED,PLATFORM_ONLY,OPPOSITE_ONLY,UNCHECK;
	/**
     * <p>
     * MATCHED核对平
     * </p>
     */

    /**
     * <p>
     * UNMATCHED核对不平
     * </p>
     */

    /**
     * <p>
     * PLATFORM_ONLY平台单边
     * </p>
     */

    /**
     * <p>
     * OPPOSITE_ONLY 对方单边
     * </p>
     */
	
	/**
	 * UNCHECK未对帐
	 */


}
 