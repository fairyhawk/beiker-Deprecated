package com.beike.common.enums.trx;

/**
 * @ClassName: RegChannel
 * @Description: 交易对外接口请求类型
 * @author yurenli
 * @date 2012-3-20 下午06:55:38
 * @version V1.0
 */
public enum ReqChannel {

	/**
	 * 运营后台
	 */
	BOSS,
	/**
	 * 手机客户端
	 */
	MC,
	/**
	 * WAP客户端
	 */
	WAP,
	/**
	 * 千品前台
	 */
	WEB,
	
	/**
	 * 分销商
	 */
	PARTNER,

	/**
	 * 开发平台
	 */
	OPEN, 
	
	/**
	 * 财务
	 */
	FINANCE;
	/**
	 * 
	 * @return
	 */
	public boolean isReqChannel() {
		switch (this) {
		case BOSS:
			return true;
		case MC:
			return true;
		case WAP:
			return true;
		case WEB:
			return true;
		case OPEN:
			return true;
		case FINANCE:
			return true;
		default:
			return false;
		}
	}

}
