package com.beike.common.exception;


/**
 * @Title: BaseException.java
 * @Package com.beike.common.exception
 * @Description: 基础异常.
 * @author wh.cheng@sinobogroup.com
 * @date May 4, 2011 3:02:07 PM
 * @version V1.0
 */
public class BaseException extends Exception {
	
	
	private static final long serialVersionUID = 8562170304258482768L;
	//private static Log log = LogFactory.getLog(BaseException.class);

	/**
	 * 错误码命名规则：四位数字。 交易、资金清结算模块相关以1开头，第二位由相关RD根据子模块定义。
	 * 每个子模块最大定义数为100个异常码。耗尽时再进行扩充 其余模块以此类推
	 */



	/**
	 * 基础部分错误码（比如交易表达式的解析）
	 */
	public static final int TRXRULE_RESOLVE_ERROR = 9001;// 交易表达式解析出错

	/**
	 * 乐观锁异常（分出此异常。便于加重试机制。）
	 */
	public static final int OPTIMISTIC_LOCK_ERROR = 9002; // 乐观锁异常

	/**
	 * 交易模块中账户部分：10打头
	 */
	public static final int ACCOUNT_STATUS_INVALID = 1001;// 账户状态无效

	public static final int ACCOUNT_NOT_FOUND = 1002;// 帐户未发现

	public static final int AMOUNT_INVALID = 1003;// 金额无效

	public static final int ACCOUNT_TYPE_INVALID = 1004; // 账户类型状态无效

	public static final int ACCOUNT_NOT_ENOUGH = 1005; // 账户余额不足

	public static final int ACCOUNT_HASED_EXIST = 1006; // 账户已存在

	public static final int USER_NOT_FOUND = 1007; // 用户不存在

	/**
	 * 交易模块中订单、返现部分：11打头
	 */
	public static final int REBRECORD_EXTERNALID_DUCPLIT = 1101; // 返现记录交易流水不能重复

	public static final int TRXORDER_EXTERNALID_DUCPLIT = 1102; // 交易订单交易流水不能重复

	public static final int REBRECORD_TRXSTATUS_INVALID = 1103; // 返现记录状态无效
	public static final int PAYMENT_SN_DUCPLIT = 1104; // 支付信息序列好不能重复
	public static final int TRXGOODS_SN_DUCPLIT = 1105; // 订单明细序列号不能重复
	public static final int PAY_REQUEST_DUCPLIT = 1107; // 支付请求号不能重复
	public static final int PAYMENT_TRXAMOUNT_TOO_SMALL = 1108; // 往支付结构请求的金额必须>=0.01元

	public static final int PAYMENTAMOUNT_PROVIDERAMOUNT_NOT_EQUALS = 1109; // 支付结构回调金额和下单金额不一致
	public static final int TRXORDER_NOT_FOUND = 1110;// 交易订单未发现

	public static final int PAYMENTAMOUNT_MORE_THAN_TRXAMOUNT = 1111; // payment金额大于交易订单金额。如果出现此错误码，则为大BUG
	public static final int VCACTPAYMENT_OR_AND_CASACTHPAYMENT_NOT_FOUND = 1112; // VCACTpayment
	// 和CASH均不存在
	public static final int PAYMENTAMOUNT_TRXAMOUNT_NOT_EQUALS = 1113; //PAYMENT总金额和交易金额不一致。如果出现此错误码，则为大BUG

	public static final int TRXORDERGOODS_NOT_FOUND = 1114; // 订单商品明细不存在
	public static final int TRXORDERGOODS_TRXSTATUS_INVALID = 1115; // 订单商品明细状态无效

	public static final int TRXORDER_TRXSTATUS_INVALID = 1116; // 交易订单状态无效

	public static final int REBATE_REQUESTID_DUCPLIT = 1117; // 返现订单号重复

	public static final int PAYLIMIT_LIST_NOT_EQULAS = 1118; // 个人购买限制:LIST比对不等（内部系统错误，放在这里便于排错）

	public static final int PAYLIMIT_OVER_ALLOWCOUNT = 1119;// 个人购买限制:超出系统约定数量

	public static final int PAYMENT_NOT_FOUND = 1120;//支付记录未发现

	public static final int CHANNEL_STATUS_INVALID = 1121; // 不支持此通道或通道未开通

	public static final int PAYLIMIT_OVER_TOTAL_COUNT = 1122;// 总量限制:超出系统约定数量

	public static final int TRXORDERGOODS_VERIFY_BELONG_FAILED = 1123;// 商品订单归属鉴权失败

	public static final int GOODS_COUNT_ITEM_LENGTH_NOT_EQULAS = 1124; // 下单时商品ID和数量ID元素长度不一致或需唯一的元素有重复
	
	public static final int PAYMENT_TRXSTATUS_INVALID = 1125;//支付记录状态无效
	
	public static final int KEY_VALUE_NOT_FOUND = 1126;//密钥未发现
	
	public static final int TRX_TURE_FALSE__INVALID = 1127;//是否预付、是否自动退款判断错误
	
	public static final int GOODS_DATE_TRANSFORM_ERROR = 1128; // 点菜单商品只可通过WEB端支付

	public static final int MENU_ILLEGALL_ORDERID =1129;//在点菜支付时活动编号与menu所在的活动编号不一致
	
	public static final int MENU_ILLEALL_GOODID=1130;//根据活动id没有找到对应数据
	
	public static final int MENU_ILLEALL_DATA = 1131;//不合法数据 例如负数或者是数据库中没有的数据
	
	public static final int MOBILE_ERROR = 1132;//不合法数据 例如负数或者是数据库中没有的数据
	/**
	 * 交易模块中退款部分：12打头
	 * 
	 */

	public static final int REFUND_STATUS_INVALID = 1201; // 退款申请条件状态不满足

	public static final int REFUND_SUCCESS_HAVED = 1202; // 已经退款成功过

	public static final int REFUND_TRXORDER_PAYMENT_AMOUNT_EQUALS = 1203; // 内部金额核对不平

	public static final int REFUND_DETAIL_NOT_FOUND = 1204; // 退款明细不存在

	public static final int REFUND_RECORD_NOT_FOUND = 1205; // 退款记录不存在

	public static final int REFUND_RECORD_APPLY_HAVED = 1206; // 已经申请过账户退款。运营处理中。

	public static final int REFUND_APPLY_FROBACK_NOT_PAYCASH = 1207; // 该笔商品订单没有现金支付

	public static final int REFUND_TO_BANK_SUC_OR_FAILED = 1208; // 退款到银行卡疑似成功，有资金风险，请联系技术处理

	public static final int REFUND_FAILED_HAVED = 1209; // 已经退款失败过
	
	public static final int REFUND_TO_BANK_AUTO_REFUSE= 1210; // 个人账户现金余额不足，系统自动拒绝

	/**
	 * 凭证模块：13打头
	 */
	public static final int VOUCHER_NOT_ENOUGH = 1301; // 库存凭证不足
	public static final int VOUCHER_STATUS_INVALID = 1302; // 凭证状态不足
	public static final int VOUCHER_NOT_FOUND = 1303; // 凭证未发现

	public static final int VOUCHER_SEND_TRXORDER_GOODS_STATUS_INVALID = 1304;// 凭证发放商品订单状态不足

	/**
	 * 活动类异常：14打头
	 */
	public static final int LOTTERY_OVER_ALLOWCOUNT = 1400; // 一次抽奖活动下不能超过一次

	public static final int SMSTEMPLATE_NOT_FOUNT = 9010;// 短信模板没有找到

	/**
	 * 过期异常:15打头
	 */
	public static final int EXPIRED_STATUS_INVALID = 1500; // 过期条件状态不满足

	/**
	 * 虚拟款项业务异常：16打头
	 */

	public static final int VM_ACCOUNT_NOT_FOUND = 1600;// 虚拟款项帐户未发现

	public static final int VM_ACCOUNT_COUNT_OVER_IN_ONE_SORT = 1601;// 虚拟款项帐在同一类别下只能有一个

	public static final int VM_DIS_REQUESTID_DUCPLIT = 1602;// 虚拟款项帐下发订单号重复
	public static final int VM_DIS_REQUESTID_NOT_NULL = 1603;// 虚拟款项帐下发订单号不能为空
	public static final int VM_ACCOUNT_DATE = 1604;// 过期时间小于当前时间
	public static final int VM_ACCOUNT_NOT_ENOUGH = 1605; // 虚拟款项账户余额不足

	public static final int VM_TRXVMEXTEND_NOT_FOUND = 1606; // 虚拟款项交易关联记录不存在

	public static final int VM_TRXEXTEND_RUDEXTEND_ERROR = 1607; // 虚拟款项交易关联记录和退款关联数据错误

	/**
	 * 千品卡异常17打头
	 */
	public static final int CARD_HAS_USED = 1700; // 卡被使用过
	public static final int CARD_NO_PWD_INVALID = 1701; // 卡密无效
	public static final int CARD_HAS_EXPIRED = 1702; // 卡过期
	public static final int CARD_STATUS_INVALID = 1703; // 卡状态无效(除了‘已使用’，‘过期’)
	public static final int CADR_DES_KEY_NOT_FOUND = 1704;// 千品卡加密/解密密钥没有找到
	public static final int EM_EXCEPTION = 100000; // EM 短信
	public static final int GD_EXCEPTION = 100001; // GD 短信异常
	public int code;

	
	/**
	 * 千品抽奖18打头
	 */
	public static final int LOTTERY_REG_USER_EXIS = 1800; // 抽奖用户已经存在
	public static final int LOTTERY_REG_GAP_TIMEOUT = 1801; // 注册时间 和当前时间, 间隔时间无效\
	public static final int LOTTERY_REG_MOBILE_BINGING = 1802; // 未绑定手机
	public static final int LOTTERY_REMAIN_COUNT_NOT_ENOUGH = 1803; // 用户抽奖剩余次数不足
	public static final int LOTTERY_PRIZE_DISPCHER_FAILED  = 1804; // 奖品下发失败
	public static final int LOTTERY_NO_PRIZE  = 1805; // 抽奖商品已经全部抽没
	
	

	/**
	 * 购物车 19打头
	 */
	public static final int SHOPPINGCART_ADD_ITEM_FIELD = 1900; // 添加购物车失败
	public static final int SHOPCART_ITEM_NOTFOUND = 1901; // 没找到购物车
	public static final int SHOPPINGCART_LIST_NOT_EQULAS = 1902; // 购物车list不等
	public static final int SHOPPINGCART_FIRST_ADD_GOODS_EXIS = 1903; // 首次添加购物车：商品已经存在
	
	/**
	 * 支付前限购超限异常 20打头
	 */
	public static final int PAY_LIMIT_ERROR = 2000; // 限购异常
	
	
	/**
	 * 线下优惠券 以21大头
	 */
	public static final int DISCOUNTCOUPON_STATUS_INVALID = 2100;	//优惠券状态无效
	public static final int DISCOUNTCOUPON_USED = 2101;			//优惠券已使用
	public static final int DISCOUNTCOUPON_PWD_INVALID = 2102;		//优惠券密码无效（非已使用、过期）
	public static final int DISCOUNTCOUPON_EXPIRED = 2103;			//优惠券过期
	public static final int DISCOUNTCOUPON_NOT_FIRST = 2104;	//非首次使用优惠券
	//优惠券3期
	public static final int COUPON_NOT_EXIST = 2105;		//优惠券不存在
	public static final int COUPON_AMOUNT_LIMIT = 2106;		//优惠券金额限制
	public static final int COUPON_TAGID_LIMIT = 2107;		//优惠券品类限制
	public static final int COUPON_DATE_LIMIT = 2108;		//优惠券日期限制
	public static final int COUPON_NOT_AVAILABLE  = 2109;	//优惠券不可用
	public static final int COUPON_TOPON_ERROR  = 2110;		//优惠券充值失败
	public static final int COUPON_TOPON_STALESTATE_EXCEPTION  = 2111;		//优惠券充值乐观锁异常
	public static final int COUPON_BOUND = 2112;		//优惠券已经绑定

	
	/**
	 * 分销商模块
	 */
	
	public static final int PAETNER_VOUCHER_STATUS_INVALID = 3000;//分销商侧凭证状态无效
	public static final int PAETNER_DES_KEY_NOT_FOUND = 3001;//分销商获取证书异常
	public static final int PAETNER_VOUCHER_NOT_ALLOW_RESEND = 3002;//该分销商订单不允许重发凭证


	public BaseException() {
		super();
	}

	public BaseException(int code) {
		this.code = code;
	}

	public BaseException(String errorMsg) {

		super(errorMsg);
	}

	@Override
	public void printStackTrace() {
		System.err.print("error code:" + code);
		super.printStackTrace();
	}

	public static String getErrorMessage(Exception e) {
		StackTraceElement ste = e.getStackTrace()[0];
		String className = ste.getClassName();
		if (className.indexOf(".") > -1) {
			className = className.substring(className.lastIndexOf(".") + 1,
					className.length());
		}
		String methodName = ste.getMethodName();
		int line = ste.getLineNumber();
		String exMsg = "类名：" + className + ",方法:" + methodName + ",行数:" + line
				+ ",异常：" + e.toString();
		return exMsg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
