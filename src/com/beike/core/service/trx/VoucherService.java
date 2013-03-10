package com.beike.core.service.trx;

import java.util.Date;
import java.util.List;

import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.bean.trx.VoucherInfo;
import com.beike.common.bean.trx.VoucherParam;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.enums.trx.VoucherStatus;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.RuleException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.exception.VoucherException;

/**
 * @Title: VoucherService.java
 * @Package com.beike.core.service.trx
 * @Description: 凭证Service
 * @date May 27, 2011 4:53:09 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface VoucherService {

	public Long createVoucher();

	/**
	 * 获取凭证
	 * 
	 * @param guestId
	 * @param goodsId
	 * @param activeDate
	 * @param isSendMerVoucher
	 * @param trxGoodsSn
	 * @return
	 * @throws StaleObjectStateException
	 */
	public Voucher activeVoucher(Long guestId, Long goodsId, Date activeDate,
			int isSendMerVoucher, String trxGoodsSn,
			String merVoucherEmailTem, boolean isAllowBuyPayLimit)
			throws VoucherException, StaleObjectStateException;

	/**
	 * 销毁凭证
	 * 
	 * @param voucher
	 * @throws VoucherException
	 * @throws StaleObjectStateException
	 */
	public void destoryVoucher(Long voucher) throws VoucherException,
			StaleObjectStateException;

	
	/**
	 * 凭证校验公用方法
	 * 
	 * @param voucher
	 * @param trxorderGoods
	 * @param voucherVrifySource
	 * @param subGuestId
	 * @throws VoucherException
	 * @throws RuleException
	 * @throws TrxorderGoodsException
	 * @throws PaymentException
	 * @throws TrxOrderException
	 * @throws AccountException
	 * @throws RebateException
	 * @throws ProcessServiceException
	 * @throws StaleObjectStateException
	 */
	public TrxorderGoods validateVoucher(Voucher voucher,
			TrxorderGoods trxorderGoods, String voucherVrifySource,
			String subGuestId) throws VoucherException,
			ProcessServiceException, RebateException, AccountException,
			TrxOrderException, PaymentException, TrxorderGoodsException,
			RuleException, StaleObjectStateException;

	/**
	 * 商家上传到平台发送验证码购买成功后凭证自校验
	 * 
	 * @param trxorderGoods
	 * @param voucherVrifySource
	 * @param subGuestId
	 * @throws VoucherException
	 * @throws ProcessServiceException
	 * @throws RebateException
	 * @throws AccountException
	 * @throws TrxOrderException
	 * @throws PaymentException
	 * @throws TrxorderGoodsException
	 * @throws RuleException
	 * @throws StaleObjectStateException
	 */
	public TrxorderGoods checkVoucherSelf(TrxorderGoods trxorderGoods,
			String voucherVrifySource, String subGuestId)
			throws VoucherException, ProcessServiceException, RebateException,
			AccountException, TrxOrderException, PaymentException,
			TrxorderGoodsException, RuleException, StaleObjectStateException;

	/**
	 * 通过商家API发送验证码购买成功后凭证自校验
	 * 
	 * @param trxorderGoods
	 * @param voucherVrifySource
	 * @param subGuestId
	 * @throws VoucherException
	 * @throws ProcessServiceException
	 * @throws RebateException
	 * @throws AccountException
	 * @throws TrxOrderException
	 * @throws PaymentException
	 * @throws TrxorderGoodsException
	 * @throws RuleException
	 * @throws StaleObjectStateException
	 */
	public TrxorderGoods checkVoucherMerVouApi(TrxorderGoods trxorderGoods,
			String voucherVrifySource, String subGuestId, String merVouCodeInApi)
			throws VoucherException, ProcessServiceException, RebateException,
			AccountException, TrxOrderException, PaymentException,
			TrxorderGoodsException, RuleException, StaleObjectStateException;

	/**
	 * 将凭证置为过期（等同于销毁，凭证不可用，只不过还可以退款）
	 * 
	 * @param voucherStatus
	 * @param startDate
	 * @return
	 * @throws StaleObjectStateException
	 */
	public void destoryExpiredVoucher(Long voucherId) throws VoucherException,
			StaleObjectStateException;

	public int checkVoucherCount(VoucherStatus voucherStatus, Date startDate);

	/**
	 * 凭证重发
	 * 
	 * @param trxOrderGoodsId
	 * @param sendType
	 * @throws TrxorderGoodsException
	 * @throws VoucherException
	 */

	public void reSendVoucher(Long trxOrderGoodsId, String changeMobile,
			String email, String sendType,String outSmsTemplate) throws TrxorderGoodsException,VoucherException;

	/**
	 * 凭证短信发送
	 * 
	 * @param mobile
	 * @param trxgoodsSn
	 * @param smsParam
	 * @param smsTemTitle
	 * @throws BaseException
	 */

	public VoucherParam sendVoucher(VoucherParam smsVoucherParam);
	
	/**
	 * 支付成功发送短信实现
	 * @param tgList
	 */
	public void sendVoucherPostPay(List<TrxorderGoods> tgList,Long userId,String  outSmsTemplate);

	/**
	 * 更新凭证码
	 * 
	 * @param voucher
	 * @throws StaleObjectStateException
	 */
	public void updateVoucherCode(Voucher voucher)
			throws StaleObjectStateException;

	/**
	 * 根据主键ID查询凭证信息接口
	 * 
	 * @param voucherId
	 * @return
	 */
	public Voucher findVoucherByid(Long voucherId);
	
	/**
	 * 根据凭证码和分店编号查询
	 * @param requestData
	 * @return
	 * @throws TrxorderGoodsException 
	 * @throws NumberFormatException 
	 */
	public Voucher findByGuestIdAndCode(String voucherCode,Long guestId);
	
	/**
	 * 查看凭证
	 * @param requestData
	 * @return
	 */
	public TrxResponseData queryVoucher(TrxRequestData requestData) throws TrxorderGoodsException;
	
	
	/**
	 * 预查询凭证相关信息
	 * @param guestId
	 * @param voucherCode
	 * @param voucherVerifySource
	 * @param subGuestId
	 * @return
	 * @throws VoucherException
	 */
	public VoucherInfo preCheckVoucher (Long guestId, String voucherCode, String subGuestId)throws VoucherException ;
	
	/**
	 * 主库查询
	 * @param voucherId
	 * @return
	 */
	public Voucher preQryInWtDBVoucherByid(Long voucherId);
	
	
}
