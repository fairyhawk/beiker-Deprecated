package com.beike.core.service.trx;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.common.enums.trx.VoucherType;
import com.beike.util.EnumUtil;

/**
 * @Title: VoucherSendFactory.java
 * @Package com.beike.core.service.trx
 * @Description: 发送凭证码服务类工厂（生产不同发送渠道的凭证发送服务实现类）
 * @date 3 8, 2012 11:24:04 AM
 * @author wh.cheng
 * @version v1.0
 */
@Service("voucherSendFactory")
public class VoucherSendFactory {

	private static Map<String, VoucherSendService> serviceMap = null;

	@Resource(name = "platformVoucherSendService")
	private VoucherSendService platformVoucherSendService;

	@Resource(name = "merchantApiVoucherSendService")
	private VoucherSendService merchantApiVoucherSendService;

	@Resource(name = "twoDimVoucherSendService")
	private VoucherSendService twoDimVoucherSendService;
	
	@Resource(name = "filmApiVoucherSendService")
	private VoucherSendService filmApiVoucherSendService;

	public VoucherSendService getVoucherSendService(VoucherType voucherType) {

		if (serviceMap == null || serviceMap.isEmpty()) {
			serviceMap = new HashMap<String, VoucherSendService>();
			serviceMap.put(EnumUtil.transEnumToString(VoucherType.PLATFORM), // 千品平台自有凭证码（含商户上传到千品平台的凭证码）
					platformVoucherSendService);
			serviceMap.put(
					EnumUtil.transEnumToString(VoucherType.MERCHANT_API), // 通过商家API，请求商家发送的凭证码
					merchantApiVoucherSendService);

			serviceMap.put(EnumUtil.transEnumToString(VoucherType.TWO_DIM), // 二维码
					twoDimVoucherSendService);
			serviceMap.put(EnumUtil.transEnumToString(VoucherType.FILM_API), // 网票网发码
					filmApiVoucherSendService);

		}

		return serviceMap.get(EnumUtil.transEnumToString(voucherType));
	}


}
