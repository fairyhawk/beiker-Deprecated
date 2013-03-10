package com.beike.core.service.trx.partner;

import java.util.Date;

import com.beike.common.bean.trx.partner.PartnerInfo;

/**
 * @Title: PartnerService.java
 * @Package com.beike.core.service.trx.parter
 * @Description: 合作分销商API交易相关Service
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface PartnerService {

	/**
	 * 检查验签、加密以及IP.
	 * 
	 * @param desStr
	 *            密文
	 * @param partnerNo
	 *            分销商编号
	 * @return
	 */
	public String checkHmacData(String desStr, PartnerInfo partnerInfo, String partnerIP);

	/**
	 * 验签，校验数据正确性
	 * 
	 * @param desStr
	 *            参数
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            签名
	 * @param partnerInfo
	 * @param partnerIP
	 * @return
	 * @author wz.gu for 团800 签名验证
	 */
	public String checkHmacData(String desStr, String publicKey, String sign, PartnerInfo partnerInfo, String partnerIP);

	/**
	 * 
	 * @param params 参数
	 * @param publicKey 公钥
	 * @param privateKey 私钥
	 * @return
	 * @author wz.gu md5 加密 签名 for tuan800 
	 */
	public String checkHmacData(String params, String publicKey, String privateKey);

	/**
	 * 转换并组装分销商请求相关参数
	 * 
	 * @param parmInfo
	 * @return
	 */

	public Object transReqInfo(String paramInfo);

	/**
	 * 转换分销商请求参数-58的券查询
	 * 
	 * @param paramInfo
	 * @return
	 */

	/**
	 * 响应的密文或者签名数据
	 * 
	 * @param source
	 * @param keyValue
	 * @return
	 */
	public String generateRspHmac(Object source, String keyValue);

	/**
	 * 根据分销商订单号查询分销商订单信息
	 * 
	 * @param userId
	 * @param outRequestId
	 * @return
	 */
	// public String findTrxorderByPartnerNo(List<Long> userIdList, String
	// outRequestId);

	public String findTrxorder(Object ptopSource, String partnerNo);

	/**
     * 查询凭证
     * @param ptop
     * @param partnerNo
     * @return
     */
    public String findVoucher(Object ptop,String partnerNo)throws Exception;
    
    /**
     * 根据凭证ID查询凭证信息
     * @param partnerNo
     * @param trxGoodsSn
     * @return
     */
    public String findVouInfoByVouId(String partnerNo,String voucherId);

	/**
	 * 根据创建时间查询凭证信息
	 * 
	 * @param partnerNo
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public String findVouInfoByActiveDate(String partnerNo, Date startTime, Date endTime, String trxStatusStr);

	/**
	 * 根据更新时间查询凭证信息
	 * 
	 * @param partnerNo
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public String findVouInfoByLastUpdateDate(String partnerNo, Date startTime, Date endTime, String trxStatus);

	/**
	 * 订单同步接口
	 * 
	 * @param ptop
	 * @return
	 * @throws Exception
	 */
	public String synchroTrxOrder(Object ptopSource, String partnerNo) throws Exception;

	/**
	 * 凭证重发接口
	 * 
	 * @param ptop
	 * @return
	 */
	public String noTscResendVoucher(Object ptop) throws Exception;

	/**
	 * 退款接口
	 * 
	 * @param ptop
	 * @return
	 */
	public String processTrxOrder(Object ptop) throws Exception;
	

}
