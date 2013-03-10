package com.beike.core.service.trx.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.bean.trx.VmAccountParamInfo;
import com.beike.common.bean.trx.VoucherInfo;
import com.beike.common.bean.trx.VoucherParam;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.common.entity.trx.SendType;
import com.beike.common.entity.trx.TrxLog;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.AuthStatus;
import com.beike.common.enums.trx.CreditStatus;
import com.beike.common.enums.trx.MerSettleStatus;
import com.beike.common.enums.trx.PartnerApiType;
import com.beike.common.enums.trx.TrxLogType;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.enums.trx.VoucherStatus;
import com.beike.common.enums.trx.VoucherType;
import com.beike.common.enums.trx.VoucherVerifySource;
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
import com.beike.common.guid.GuidGenerator;
import com.beike.core.service.trx.VoucherPrefetchService;
import com.beike.core.service.trx.VoucherSendFactory;
import com.beike.core.service.trx.VoucherSendService;
import com.beike.core.service.trx.VoucherService;
import com.beike.core.service.trx.partner.PartnerCommonService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.core.service.trx.vm.VmAccountService;
import com.beike.dao.trx.MerVoucherDao;
import com.beike.dao.trx.TrxLogDao;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.dao.trx.VoucherDao;
import com.beike.dao.user.UserDao;
import com.beike.entity.common.Sms;
import com.beike.entity.user.User;
import com.beike.form.SmsInfo;
import com.beike.service.common.EmailService;
import com.beike.service.common.SmsService;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.EnumUtil;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;
import com.beike.util.img.JsonUtil;

/**
 * @Title: VoucherServiceImpl.java
 * @Package com.beike.core.service.trx.impl
 * @Description: TODO
 * @date May 28, 2011 11:24:04 AM
 * @author wh.cheng
 * @version v1.0
 */
@Service("voucherService")
public class VoucherServiceImpl implements VoucherService {

	private final Log logger = LogFactory.getLog(VoucherServiceImpl.class);
	PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	public String vouOverAlertEmail = propertyUtil.getProperty("mer_vouhcher_over_alert_email");
	private final String VACCOUNTID = "1";
	@Resource(name = "smsService")
	private SmsService smsService;
	private final String OPERATORID = "0";
	@Autowired
	private VoucherDao voucherDao;

	@Resource(name = "guidGeneratorService")
	private GuidGenerator guidGenerator;

	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;

	@Autowired
	private TrxSoaService trxSoaService;

	@Autowired
	private TrxOrderDao trxOrderDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private EmailService emailService;

	@Resource(name = "voucherSendFactory")
	private VoucherSendFactory voucherSendFactory;

	@Autowired
	private TrxLogDao trxLogDao;

	@Autowired
	private MerVoucherDao merVoucherDao;
	@Autowired
	private VmAccountService vmAccountService;

	@Autowired
	private VoucherPrefetchService voucherPrefetchService;
	@Autowired
	private PartnerCommonService partnerCommonService;

	// 异常报警邮件
	public String merchant_api_voucher_email = propertyUtil.getProperty("merchant_api_voucher_email");

	// 扣款报警邮件模板
	public static final String MERCHANT_TRX_ERROR = "MERCHANT_TRX_ERROR";

	/**
	 * 获取凭证
	 */
	@Override
	public Voucher activeVoucher(Long guestId, Long goodsId, Date activeDate, int isSendMerVoucher, String trxGoodsSn, String merVoucherEmailTem, boolean isAllowBuyPayLimit) throws VoucherException, StaleObjectStateException {

		// Voucher voucher
		// =voucherDao.findInit(VoucherStatus.INIT);//此行代码不能删，如果预取策略出异常，直接用此句替换

		Voucher voucher = findInitVoucher();// 预取策略取出凭证

		if (voucher == null) {
			// TODO 事件报警
			throw new VoucherException(BaseException.VOUCHER_NOT_ENOUGH);
		}
		Long voucherId = voucher.getId();
		if (isSendMerVoucher == 1 && isAllowBuyPayLimit) {// (使用商家码+没有个人超限)如果是需要取商家自有的凭证

			Map<String, Object> merVoucherMap = merVoucherDao.findByGtIdAndGdId(guestId, goodsId);
			if (merVoucherMap == null) {// 如果商家凭证码不足，则直接取平台凭证。并发送邮件
				// TODO 发送内部报警邮件
				String alertEmailParams[] = { trxGoodsSn, guestId.toString(), goodsId.toString() };
				if (vouOverAlertEmail != null && vouOverAlertEmail.length() > 0) {
					String[] vouOverAlertEmailAry = vouOverAlertEmail.split(",");
					int alertEmailCount = vouOverAlertEmailAry.length;

					try {
						for (int i = 0; i < alertEmailCount; i++) {
							emailService.send(null, null, null, null, null, null, new String[] { vouOverAlertEmailAry[i] }, null, null, new Date(), alertEmailParams, merVoucherEmailTem);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else {
				Long merVoucherId = (Long) (merVoucherMap.get("id"));// 商家凭证ID
				String merVoucherCode = merVoucherMap.get("merVoucherCode").toString();

				// 根据主键更新商家凭证，置为已获取并关联Voucher_id
				merVoucherDao.updateMerVoucher(merVoucherId, voucherId);
				// 覆盖平台凭证码
				voucher.setVoucherCode(merVoucherCode);
				// 使用商家上传的校验码。置标志为便于外部方法调短信模板
				voucher.setSendMerVou(true);
			}
		}

		voucher.setActiveDate(new Date());
		voucher.setVoucherStatus(VoucherStatus.ACTIVE);
		voucher.setGuestId(guestId);
		voucherDao.update(voucher);
		return voucher;
	}

	/**
	 * 单个获取voucher并作list 线程处理以及方法同步，删除拿完后立即删除
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized Voucher findInitVoucher() {
		List<Voucher> voucherListInMem = (List<Voucher>) TrxConstant.voucherPrefetchVouList;

		if (voucherListInMem == null || voucherListInMem.size() == 0) {// 如果内存里已经用尽，再次到库中取
			List<Voucher> voucherListInDB = voucherPrefetchService.preFetchVoucher(TrxConstant.voucherPrefetchCount);

			TrxConstant.conVerSynchronizedList(voucherListInDB);// 放入到内存中，并将List做线程安全处理
		}
		// 从内存中取第一个（此处能保证是此voucher status 为init，无需进行判断）
		List<Voucher> voucherListInMemNew = (List<Voucher>) TrxConstant.voucherPrefetchVouList;
		Voucher voucher = voucherListInMemNew.get(0);
		logger.info("+++vouPrefetch ->vounPrefetch in Mem size:" + voucherListInMemNew.size() + "+++++getTop1Voucher->id:" + voucher.getId() + "++voucherStatus:" + voucher.getVoucherStatus() + "+++");
		// 拿完之后直接删掉。
		TrxConstant.voucherPrefetchVouList.remove(voucher);
		logger.info("+++vouPrefetch ->remove->id:" + voucher.getId() + "++voucherStatus:" + voucher.getVoucherStatus() + "+++");

		return voucher;
	}

	/**
	 * 商家上传到凭证码到平台发送验证码购买成功后凭证自校验
	 * 
	 * @param guestId
	 * @param voucherId
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
	@Override
	public TrxorderGoods checkVoucherSelf(TrxorderGoods trxorderGoods, String voucherVrifySource, String subGuestId) throws VoucherException, ProcessServiceException, RebateException, AccountException, TrxOrderException, PaymentException, TrxorderGoodsException, RuleException,
			StaleObjectStateException {

		Voucher voucher = voucherDao.findById(trxorderGoods.getVoucherId());
		TrxorderGoods tg = validateVoucher(voucher, trxorderGoods, voucherVrifySource, subGuestId);
		return tg;
	}

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
	@Override
	public TrxorderGoods checkVoucherMerVouApi(TrxorderGoods trxorderGoods, String voucherVrifySource, String subGuestId, String merVouCodeInApi) throws VoucherException, ProcessServiceException, RebateException, AccountException, TrxOrderException, PaymentException, TrxorderGoodsException,
			RuleException, StaleObjectStateException {

		Voucher voucher = voucherDao.findById(trxorderGoods.getVoucherId());
		if (merVouCodeInApi != null && merVouCodeInApi.length() > 0) {

			voucher.setVoucherCode(merVouCodeInApi);// 重置验证码，经商家API响应的对方流水号保存下来。便于在我们平台下次请求时调用向对方发起请求

		}
		TrxorderGoods trxOrderGoods = validateVoucher(voucher, trxorderGoods, voucherVrifySource, subGuestId);

		return trxOrderGoods;
	}

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
	@Override
	public TrxorderGoods validateVoucher(Voucher voucher, TrxorderGoods trxorderGoods, String voucherVrifySource, String subGuestId) throws VoucherException, ProcessServiceException, RebateException, AccountException, TrxOrderException, PaymentException, TrxorderGoodsException, RuleException,
			StaleObjectStateException {
	    Long guestId=trxorderGoods.getGuestId();
		if (!((VoucherStatus.ACTIVE.equals(voucher.getVoucherStatus()) && TrxStatus.SUCCESS.equals(trxorderGoods.getTrxStatus())) || (VoucherStatus.DESTORY.equals(voucher.getVoucherStatus()) && TrxStatus.EXPIRED.equals(trxorderGoods.getTrxStatus())))) {
			throw new VoucherException(BaseException.VOUCHER_STATUS_INVALID);// 不可回收
		}

		voucher.setConfirmDate(new Date());
		voucher.setVoucherStatus(VoucherStatus.USED);
		voucher.setVoucherVerifySource(EnumUtil.transStringToEnum(VoucherVerifySource.class, voucherVrifySource));
		voucherDao.update(voucher);

		/**
		 * 新增同步更新TrxOrderGoods authStatus 状态
		 */
		// 加入同步更新trx_order_goods中的凭证状态 //正常回收
		trxorderGoods.setAuthStatus(AuthStatus.RECOVERY);
		trxorderGoods.setTrxStatus(TrxStatus.USED);
		if (trxorderGoods.isIsadvance()) {
			trxorderGoods.setMerSettleStatus(MerSettleStatus.NOSETTLE);// 无需结算（不结算）
		} else {
			trxorderGoods.setMerSettleStatus(MerSettleStatus.UNSETTLE);// 可结算（未结算）
			if(guestId.toString().length()==7 && subGuestId.length()==7){//招财宝商家
                //将商品订单入账状态改为CREDITING（入账中，新清结算逻辑）
                trxorderGoods.setCreditStatus(CreditStatus.CREDITING.toString());
            } 
		}
		trxorderGoods.setSubGuestId((new Long(subGuestId))); // 分店ID

		TrxOrder trxOrder = trxOrderDao.findById(trxorderGoods.getTrxorderId());

		try {
			TrxLog trxLogUsed = new TrxLog(trxorderGoods.getTrxGoodsSn(), new Date(), TrxLogType.USED, "使用", "");
			trxLogDao.addTrxLog(trxLogUsed);

			if (trxorderGoods.getCreateDate().before(TrxConstant.rebateEndDate)) {

				String vmAccountId = VACCOUNTID;//
				String amount = trxorderGoods.getRebatePrice() + "";
				String requestId = guidGenerator.gainCode("DIS");
				String userId = trxOrder.getUserId().toString();
				String operatorId = OPERATORID;
				VmAccountParamInfo vmAccountParamInfo = new VmAccountParamInfo();
				vmAccountParamInfo.setVmAccountId(vmAccountId);
				vmAccountParamInfo.setAmount(amount);
				vmAccountParamInfo.setRequestId(requestId);
				vmAccountParamInfo.setUserId(userId);
				vmAccountParamInfo.setOperatorId(operatorId);
				vmAccountParamInfo.setActHistoryType(ActHistoryType.RABATE);
				vmAccountParamInfo.setDescription(trxorderGoods.getId().toString());

				int result = vmAccountService.dispatchVmForVou(vmAccountParamInfo);
				logger.info("++++++rebate  dis++trxorderGoods:" + trxorderGoods.getId() + "++dis result:" + result + "+++++++++++++++");
				if (result == 1) {
					trxorderGoods.setDis(true);
					TrxLog trxLogReabte = new TrxLog(trxorderGoods.getTrxGoodsSn(), new Date(), TrxLogType.TRXORDERGOODS, "返现成功", "返现金额：￥" + trxorderGoods.getRebatePrice());
					trxLogDao.addTrxLog(trxLogReabte);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e);
		}

		trxorderGoodsDao.updateTrxGoods(trxorderGoods);
		return trxorderGoods;
	}
	
	
	

	@Override
	public int checkVoucherCount(VoucherStatus voucherStatus, Date curDate) {

		return voucherDao.findByDateAndStatus(curDate, voucherStatus);

	}

	@Override
	public Long createVoucher() {

		// 新增创建时耗时日志(由此确定判重查询和随机数冲突谁最耗时)
		logger.info("++++++++start create item voucher +++++at date:" + DateUtils.getStringTodayto());
		String voucherCode = getRandomCode8();

		logger.info("++++++++start create item voucher++++ qry repeat voucher +++++at date:" + DateUtils.getStringTodayto());
		Voucher voucher = voucherDao.findByVoucherCode(voucherCode);

		if (voucher != null) {
			logger.info("++++++++ create item voucher++++ qry repeat voucher is true +++++at date:" + DateUtils.getStringTodayto());
			return 0L;
		}

		if (voucherCode.length() != 8) {
			logger.info("++++++++ create item voucher++++ qry repeat voucher !=8 +++++at date:" + DateUtils.getStringTodayto());
			return 0L;

		}
		logger.info("++++++++finished create item voucher++++ qry repeat voucher +++++at date:" + DateUtils.getStringTodayto());

		Voucher voucherNew = new Voucher(new Date(), voucherCode, VoucherStatus.INIT);

		Long voucherId = voucherDao.addVoucher(voucherNew);
		logger.info("++++++++finished create item voucher+++voucherId=" + voucherId + "+++voucherCode:******+++++at date:" + DateUtils.getStringTodayto());
		return voucherId;

	}

	public static String getRandomCode8() {
		double d = Math.random() * 100000000;
		int i = (int) d;
		if (i < 10000000) {
			i = i + 10000000;
		}
		return String.valueOf(i);
	}

	@Override
	public void destoryVoucher(Long voucherId) throws VoucherException, StaleObjectStateException {
		Voucher voucher = voucherDao.findById(voucherId);
		if (voucher == null) {

			throw new VoucherException(BaseException.VOUCHER_NOT_FOUND);
		}
		if (!voucher.getVoucherStatus().isDestory()) {
			throw new VoucherException(BaseException.VOUCHER_STATUS_INVALID);// 不可销毁

		}
		voucherDao.updateStatusByIdAndDate(voucherId, VoucherStatus.DESTORY, new Date(), voucher.getVersion());

		// 加入同步更新trx_order_goods中的凭证状态 //退款销毁
		TrxorderGoods trxorderGoods = trxorderGoodsDao.findByVoucherId(voucher.getId());
		trxorderGoods.setAuthStatus(AuthStatus.DESTROY);
		trxorderGoodsDao.updateTrxGoods(trxorderGoods);
	}

	@Override
	public void destoryExpiredVoucher(Long voucherId) throws VoucherException, StaleObjectStateException {

		Voucher voucher = voucherDao.findById(voucherId);
		if (voucher == null) {

			throw new VoucherException(BaseException.VOUCHER_NOT_FOUND);
		}
		if (!voucher.getVoucherStatus().isDestory()) {
			throw new VoucherException(BaseException.VOUCHER_STATUS_INVALID);// 不可销毁

		}
		voucherDao.updateStatusByIdAndDate(voucherId, VoucherStatus.DESTORY, new Date(), voucher.getVersion());

		// 加入同步更新trx_order_goods中的凭证状态 //过期
		TrxorderGoods trxorderGoods = trxorderGoodsDao.findByVoucherId(voucher.getId());
		trxorderGoods.setAuthStatus(AuthStatus.TIMEOUT);
		trxorderGoodsDao.updateTrxGoods(trxorderGoods);
	}

	@Override
	public void reSendVoucher(Long trxOrderGoodsId, String changeMobile, String email, String sendType, String outSmsTemplate) throws TrxorderGoodsException, VoucherException {

		// 判断trxordergoods是否存在以及状态是否符合// 取出凭证号
		TrxorderGoods tg = trxorderGoodsDao.findById(trxOrderGoodsId);
		Long goodsId = tg.getGoodsId();
		List<Long> goodsIdList = new ArrayList<Long>();
		goodsIdList.add(goodsId);

		if (tg == null) {

			throw new TrxorderGoodsException(BaseException.TRXORDERGOODS_NOT_FOUND);
		}

		// 是否支持重发
		if (!tg.isSupVouReSend()) {
			throw new VoucherException(BaseException.VOUCHER_SEND_TRXORDER_GOODS_STATUS_INVALID);

		}
		Voucher voucher = voucherDao.findById(tg.getVoucherId());
		Map<Long, String> goodsTitleMap = trxSoaService.findGoodsTitle(goodsIdList);
		String goodsTitle = goodsTitleMap.get(goodsId).toString();// 取商品简称
		String voucherCode = voucher.getVoucherCode();

		// 根据trxordergoods找userId取用户手机号和邮箱

		Long trxOrderId = tg.getTrxorderId();
		TrxOrder trxorder = trxOrderDao.findById(trxOrderId);
		Long userId = trxorder.getUserId();
		String mobileInTrxorder = trxorder.getMobile();// 订单里保存的手机号
		User user = userDao.findById(userId);
		String mobile = user.getMobile();

		// 分销商逻辑分支
		PartnerInfo parInfo = partnerCommonService.qryParterByUserIdInMem(userId);// 获取分销商
		// 如果是分销商订单且该分销商支持支重发，则取trxorder预存的手机号进行发送
		//分销商 只限制团800 和京东，其他都能重发
		if (parInfo != null ) {
			if(StringUtils.validNull(mobileInTrxorder)){
				mobile = mobileInTrxorder;
			}
		}

		email = email != null && email.length() > 0 ? email : user.getEmail();
		changeMobile = changeMobile != null && changeMobile.length() > 0 ? changeMobile : mobile;
		String[] mobileAry = new String[] { mobile, changeMobile };

		// 封装短信参数
		VoucherParam voucherParam = new VoucherParam(tg, goodsTitle, voucherCode, mobileAry, email, EnumUtil.transStringToEnum(SendType.class, sendType));

		int isSendMerVouInTg = tg.isSendMerVou();

		String smsTemplate = "";

		VoucherType voucherType = null;

		if (parInfo != null) {
			if (PartnerApiType.BUY360.name().equals(parInfo.getApiType())) {// 京东来的订单我们不发短信

				return;
			}
			voucherType = VoucherType.PLATFORM;
			String smsExpress = parInfo.getSmsExpress();
			// String subName =
			// parInfo.getSubName();//分销商名称，根据此名称发送给不同分销商信息{sub_name}&千品合作团购
			Map<String, Object> mapJson = JsonUtil.getMapFromJsonString(smsExpress);
			smsTemplate = (String) mapJson.get("resend_sms_temlate");
			VoucherSendService voucherSendService = voucherSendFactory.getVoucherSendService(voucherType);// 调用相关服务实现类
			voucherParam.setSmsTemplate(smsTemplate);// 重置短信模板
			voucherParam.setOutSmsTemplate(outSmsTemplate);// 分销商外部模板
			voucherSendService.sendVoucher(voucherParam);
		} else {
			// 凭证短信发送类型分发
			// if (isMerchantApi && isMerchantApiAndMapGoods) {
				if (tg.isSendMerVou() == 2 ) {
				// 商家API
				voucherType = VoucherType.MERCHANT_API;
				smsTemplate = Constant.MER_VOUCHERDISPATCH_API;
				voucherParam.setSmsTemplate(smsTemplate);// 重置短信模板
				VoucherSendService voucherSendService = voucherSendFactory.getVoucherSendService(voucherType);// 调用相关服务实现类
				if (mobile.equals(changeMobile)) {
					voucherSendService.reSendVoucher(voucherParam); // 调用重发
				} else if (!mobile.equals(changeMobile)) {
					voucherSendService.transSendVoucher(voucherParam);// 调用换发
				}

			} else if (tg.isSendMerVou() == 3 ){
				
				voucherType = VoucherType.FILM_API;
				smsTemplate = Constant.MER_VOUCHERDISPATCH_API;
				voucherParam.setSmsTemplate(smsTemplate);// 重置短信模板
				voucherParam.setMobile(new String[1]);
				VoucherSendService voucherSendService = voucherSendFactory.getVoucherSendService(voucherType);// 调用相关服务实现类
				voucherSendService.reSendVoucher(voucherParam); // 调用重发
			}else {// 平台凭证码
				voucherType = VoucherType.PLATFORM;
				if (isSendMerVouInTg == 0) {
					smsTemplate = Constant.SMS_VOUCHER_DISPATCH;
					if(tg.getBizType()==1){
						smsTemplate = Constant.SMS_MENU_VOUCHER_DISPATCH;
						Long subGuestId = tg.getSubGuestId();
						Map<String, Object> mapMerchant = trxSoaService.getMerchantById(subGuestId);
						String merchantName = mapMerchant.get("merchantName").toString();
						voucherParam.setSubGuestName(merchantName);
					}
				} else if (isSendMerVouInTg == 1) {
					smsTemplate = Constant.MER_VOUCHERDISPATCH;
				}
				voucherParam.setSmsTemplate(smsTemplate);// 重置短信模板
				VoucherSendService voucherSendService = voucherSendFactory.getVoucherSendService(voucherType);// 调用相关服务实现类
				voucherSendService.sendVoucher(voucherParam);

			}
		}
	}

	/**
	 * 根据凭证码查询订单信息
	 * @param voucherCode
	 * @param guestId
	 * @return
	 * @throws TrxorderGoodsException
	 */
	@Override
	public Voucher findByGuestIdAndCode(String voucherCode,Long guestId){
		return voucherDao.findByGuestIdAndCode(guestId, voucherCode);
	}
	
	
	
	@Override
	public TrxResponseData queryVoucher(TrxRequestData requestData) throws TrxorderGoodsException {

		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(requestData.getTrxorderGoodsId(), requestData.getUserId());

		if (trxorderGoods == null || !trxorderGoods.isSupVouReSend()) {// 不存在或者不满足查看条件
			throw new TrxorderGoodsException(BaseException.TRXORDER_TRXSTATUS_INVALID);
		}
		Voucher voucher = voucherDao.findById(trxorderGoods.getVoucherId());

		if (voucher == null) {
			throw new TrxorderGoodsException(BaseException.VOUCHER_NOT_FOUND);
		}
		String userId = String.valueOf(requestData.getUserId());
		String trxorderGoodsId = String.valueOf(trxorderGoods.getId());
		String trxorderGoodsSn = trxorderGoods.getTrxGoodsSn();
		String voucherCode = voucher.getVoucherCode();
		String voucherType = String.valueOf(trxorderGoods.isSendMerVou());
		TrxResponseData responseData = new TrxResponseData(userId, trxorderGoodsId, trxorderGoodsSn, voucherCode, voucherType, "");

		return responseData;
	}

	/**
	 * 支付成功发送短信实现
	 * 
	 * @param tgList
	 */
	@Override
	public void sendVoucherPostPay(List<TrxorderGoods> tgList, Long userId, String outSmsTemplate) {

		if (tgList == null || tgList.size() == 0) {
			return;
		}
		PartnerInfo parInfo = partnerCommonService.qryParterByUserIdInMem(userId);
		//如果是分销商的订单
		if (parInfo != null) {
			//如果是京东和团800订单我们不发短信
			if (PartnerApiType.BUY360.name().equals(parInfo.getApiType()) || PartnerApiType.TUAN800.name().equals(parInfo.getApiType())) {// 京东和团800来的订单我们不发短信
				return;
			}else{
				//如果是其它分销商的订单就按原来的发送信息
				for (TrxorderGoods trxorderGoods : tgList) {
					// 封装短信参数
					VoucherParam smsVoucherParam = new VoucherParam(trxorderGoods, trxorderGoods.getGoodsTitle(), trxorderGoods.getVoucherCode(), new String[] { trxorderGoods.getMobile() }, trxorderGoods.isSendMerVou(), SendType.SMS);
					String smsTemplate = "";// 短信模板
					String smsExpress = parInfo.getSmsExpress();
					Map<String, Object> mapJson = JsonUtil.getMapFromJsonString(smsExpress);
					smsTemplate = (String) mapJson.get("send_sms_temlate");
					smsVoucherParam.setVoucherType(VoucherType.PLATFORM);
					smsVoucherParam.setSmsTemplate(smsTemplate);
					smsVoucherParam.setOutSmsTemplate(outSmsTemplate);// 分销商外部模板
					
					 sendVoucher(smsVoucherParam);
				}
			}
		}else{
			//对trxOrderGoods根据id进行分组
			Map<Long, List<TrxorderGoods>> goodsIdMap = new HashMap<Long, List<TrxorderGoods>>(); 
			for(TrxorderGoods trxOrderGoods : tgList){
				if(goodsIdMap.containsKey(trxOrderGoods.getGoodsId())){
					List<TrxorderGoods> temp = goodsIdMap.get(trxOrderGoods.getGoodsId());
					temp.add(trxOrderGoods);
					goodsIdMap.put(trxOrderGoods.getGoodsId(), temp);
				}else{
					List<TrxorderGoods> temp = new ArrayList<TrxorderGoods>();
					temp.add(trxOrderGoods);
					goodsIdMap.put(trxOrderGoods.getGoodsId(), temp);
				}
			}
			//对根据id分组后的订单进行发送短信
			Set<Entry<Long, List<TrxorderGoods>>> entrys = goodsIdMap.entrySet();
			for(Entry<Long, List<TrxorderGoods>> entry : entrys){
				List<TrxorderGoods> orderGoods = entry.getValue();
				TrxorderGoods trxorderGoods = orderGoods.get(0);
				int isSendMerVouInVou = trxorderGoods.isSendMerVou();
				boolean isMerchantApiAndMapGoods = trxorderGoods.isSendMerVou() == 2 ? true : false; // 是否是通过第三方发码
				VoucherParam smsVoucherParam = new VoucherParam(trxorderGoods, trxorderGoods.getGoodsTitle(), trxorderGoods.getVoucherCode(), new String[] { trxorderGoods.getMobile() }, trxorderGoods.isSendMerVou(), SendType.SMS);
				// 凭证短信发送类型分发
				if (isSendMerVouInVou == 2) {// 阳光绿洲商家API
					smsVoucherParam.setVoucherType(VoucherType.MERCHANT_API);
					smsVoucherParam.setTrxorderGoodsList(orderGoods);
				} else if (isSendMerVouInVou == 3) {// 网票网商家API
					smsVoucherParam.setVoucherType(VoucherType.FILM_API);
					smsVoucherParam.setSmsTemplate(Constant.MER_VOUCHERDISPATCH_API);

				} else {// 平台凭证码
					smsVoucherParam.setVoucherType(VoucherType.PLATFORM);
					//此商品在此订单中的数量
					int orderGoodsSize = orderGoods.size();
					if( orderGoodsSize == 1){ //当购买此商品数量为1时
						smsVoucherParam.setSmsTemplate(Constant.VOUCHERDISPATCHMIN);
						if(trxorderGoods.getBizType()==1){//当下单商品为点餐商品时
							smsVoucherParam.setSmsTemplate(Constant.SMS_MENU_VOUCHER_DISPATCH);
							Long subGuestId = trxorderGoods.getSubGuestId();
							Map<String, Object> mapMerchant = trxSoaService.getMerchantById(subGuestId);
							String merchantName = mapMerchant.get("merchantName").toString();
							smsVoucherParam.setSubGuestName(merchantName);
						}
					}
					if( orderGoodsSize > 1 && orderGoodsSize <= 5){//当购买此商品数量为大于1小等5
						smsVoucherParam.setSmsTemplate(Constant.VOUCHERDISPATCHOTHER);
						smsVoucherParam.setTrxorderGoodsList(orderGoods);
					}
					if(orderGoodsSize > 5){//当购买此商品数量为大5
						smsVoucherParam.setSmsTemplate(Constant.VOUCHERDISPATCHMAX);
						smsVoucherParam.setTrxorderGoodsList(orderGoods);
					}
				}
				//如果是阳光绿洲 则需要多次通知阳光绿洲
				if(isSendMerVouInVou == 2 && smsVoucherParam.getTrxorderGoodsList().size()>=1){
					String trxGoodsSn = "";
					for(int i=0 ;i < smsVoucherParam.getTrxorderGoodsList().size() ; i++ ){
						TrxorderGoods temp = smsVoucherParam.getTrxorderGoodsList().get(i);
						smsVoucherParam.setTrxorderGoods(temp);
						trxGoodsSn += smsVoucherParam.getTrxorderGoodsList().get(i).getTrxGoodsSn()+ ";";
						send(smsVoucherParam,isMerchantApiAndMapGoods,  temp);
					}
					String goodsName = StringUtils.cutffStr(trxorderGoods.getGoodsTitle(),
							TrxConstant.smsVouGoodsNameCount, "");// 商品简称
					String ordLoseDate = DateUtils.toString(trxorderGoods.getOrderLoseDate(),
							"yyyy-MM-dd");// 过期时间
					// 短信参数
					Object[] smsParamMin = new Object[] { goodsName,smsVoucherParam.getTrxorderGoodsList().size() ,trxGoodsSn,
							ordLoseDate };
					Object[] smsParamMax = new Object[] { goodsName,smsVoucherParam.getTrxorderGoodsList().size()};
					Sms sms;
					try {
						
						String contentResult = "";
						if(smsVoucherParam.getTrxorderGoodsList().size() >= 6){
							sms = smsService.getSmsByTitle(Constant.MER_VOUCHERDISPATCH_API_SMS_MAX);
							String template = sms.getSmscontent(); // 获取短信模板
							contentResult = MessageFormat.format(template,
									smsParamMax);
						}else{
							sms = smsService.getSmsByTitle(Constant.MER_VOUCHERDISPATCH_API_SMS_MIN);
							String template = sms.getSmscontent(); // 获取短信模板
							contentResult = MessageFormat.format(template,
									smsParamMin);
						}
						

						SmsInfo sourceBean = new SmsInfo(trxorderGoods.getMobile(), contentResult,
								"15", "1");

						logger.info("+++++++++++smsVoucher:mobile:" + trxorderGoods.getMobile()
								+ "+++trxgoodsSn:" + trxGoodsSn
								+ "->voucherCode:*****+++++++");

						smsService.sendSms(sourceBean);
					} catch (BaseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else{
					send(smsVoucherParam,isMerchantApiAndMapGoods,  trxorderGoods);
				}
				
					
			}
		}
		
		
		
	}
	
	private void send(VoucherParam smsVoucherParam, boolean isMerchantApiAndMapGoods, TrxorderGoods  trxorderGoods){
		// 发送短信
		VoucherParam voucherParamRtn = sendVoucher(smsVoucherParam);

		// 阳光绿洲更新凭证码替换平台码
		if (isMerchantApiAndMapGoods) {
			if (voucherParamRtn != null) {// marchantApiVoucherServiceImpl里
				// 若没有找到第三方发码的产品编码配置,则voucherParamRtn==null
				// 更新通过商家在线API拿到的凭证码，入千品库
				Voucher voucher = findVoucherByid(trxorderGoods.getVoucherId());
				String voucherCode = voucherParamRtn.getVoucherCode();
				voucher.setVoucherCode(voucherCode);
				try {
					updateVoucherCode(voucher);
				} catch (Exception e) {
					logger.debug("++++++++++++++++++++++" + e);
					e.printStackTrace();
				}
		    }

		}
	}

	/**
	 * 发送凭证
	 * 
	 * @param voucherParam
	 * @return
	 */
	@Override
	public VoucherParam sendVoucher(VoucherParam voucherParam) {

		String mobile = voucherParam.getMobile()[0];
		String[] mobileAry = { mobile, mobile };
		voucherParam.setMobile(mobileAry);
		VoucherSendService voucherSendService = voucherSendFactory.getVoucherSendService(voucherParam.getVoucherType());// 调用相关服务实现类
		VoucherParam rtnvoucherParam = voucherSendService.sendVoucher(voucherParam);
		return rtnvoucherParam;

	}

	/**
	 * 更新凭证码
	 * 
	 * @param voucher
	 * @throws StaleObjectStateException
	 */
	@Override
	public void updateVoucherCode(Voucher voucher) throws StaleObjectStateException {
		voucherDao.update(voucher);
	}

	/**
	 * 根据主键ID查询凭证信息
	 * 
	 * @param voucherId
	 * @return
	 */
	@Override
	public Voucher findVoucherByid(Long voucherId) {
		return voucherDao.findById(voucherId);
	}

	@Override
	public Voucher preQryInWtDBVoucherByid(Long voucherId) {
		return voucherDao.findById(voucherId);
	}

	@Override
	public VoucherInfo preCheckVoucher(Long guestId, String voucherCode, String subGuestId) throws VoucherException {

		Voucher voucher = voucherDao.findByGuestIdAndCode(guestId, voucherCode);
		if (voucher == null) {
			throw new VoucherException(BaseException.VOUCHER_NOT_FOUND);
		}

		TrxorderGoods trxorderGoods = trxorderGoodsDao.findByVoucherId(voucher.getId());
		if (!((VoucherStatus.ACTIVE.equals(voucher.getVoucherStatus()) && TrxStatus.SUCCESS.equals(trxorderGoods.getTrxStatus())) || (VoucherStatus.DESTORY.equals(voucher.getVoucherStatus()) && TrxStatus.EXPIRED.equals(trxorderGoods.getTrxStatus())))) {
			throw new VoucherException(BaseException.VOUCHER_STATUS_INVALID);// 不可回收
		}

		TrxOrder trxOrder = trxOrderDao.findById(trxorderGoods.getTrxorderId());

		VoucherInfo voucherInfo = new VoucherInfo(voucher, trxorderGoods, trxOrder);

		return voucherInfo;
	}

	public VoucherDao getVoucherDao() {
		return voucherDao;
	}

	public void setVoucherDao(VoucherDao voucherDao) {
		this.voucherDao = voucherDao;
	}

	public GuidGenerator getGuidGenerator() {
		return guidGenerator;
	}

	public void setGuidGenerator(GuidGenerator guidGenerator) {
		this.guidGenerator = guidGenerator;
	}

}
