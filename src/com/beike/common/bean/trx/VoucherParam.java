package com.beike.common.bean.trx;

import java.util.ArrayList;
import java.util.List;

import com.beike.common.entity.trx.SendType;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.VoucherType;

/**
 * @Title: VoucherParam.java
 * @Package com.beike.common.bean.trx
 * @Description: 凭证参数聚合参数
 * @date 2 8, 2012 11:24:04 AM
 * @author wh.cheng
 * @version v1.0
 */
public class VoucherParam {

	private TrxorderGoods trxorderGoods;// 商品订单
	
	private List<TrxorderGoods> trxorderGoodsList = new ArrayList<TrxorderGoods>();//商品订单集合

	private String goodsTitle;// 商品简称

	private String voucherCode;// 凭证码

	private String[] mobile;// 手机号数组

	private String eamil;// EMail

	private String smsTemplate;// 短信模板

	private int smsVouGoodsNameCount;// 商品简称截取字数

	private int isSendMerVouInVou;// 实际是否发送的是商家码 ，用户短信模板切换。（上传到平台的商家码是否用尽的属性）

	private SendType sendType;// 发送类型
	
	private VoucherType voucherType;
	
	private String outSmsTemplate="";//商家自有模板
	
	private String subGuestName = "";// 分店名称
	

	public List<TrxorderGoods> getTrxorderGoodsList() {
		return trxorderGoodsList;
	}

	public void setTrxorderGoodsList(List<TrxorderGoods> trxorderGoodsList) {
		this.trxorderGoodsList = trxorderGoodsList;
	}

	public String getSubGuestName() {
		return subGuestName;
	}

	public void setSubGuestName(String subGuestName) {
		this.subGuestName = subGuestName;
	}

	public int getIsSendMerVouInVou() {
		return isSendMerVouInVou;
	}

	public void setIsSendMerVouInVou(int isSendMerVouInVou) {
		this.isSendMerVouInVou = isSendMerVouInVou;
	}

	public String getOutSmsTemplate() {
		return outSmsTemplate;
	}

	public void setOutSmsTemplate(String outSmsTemplate) {
		this.outSmsTemplate = outSmsTemplate;
	}

	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}

	public VoucherParam() {
		super();
	}

	public VoucherParam(TrxorderGoods trxorderGoods, String goodsTitle,
			String voucherCode, String[] mobile) {
		super();
		this.trxorderGoods = trxorderGoods;
		this.goodsTitle = goodsTitle;
		this.voucherCode = voucherCode;
		this.mobile = mobile;
	}

	public VoucherParam(TrxorderGoods trxorderGoods, String goodsTitle,
			String voucherCode, String[] mobile, String email, SendType sendType) {
		super();
		this.trxorderGoods = trxorderGoods;
		this.goodsTitle = goodsTitle;
		this.voucherCode = voucherCode;
		this.mobile = mobile;
		this.eamil = email;
		this.sendType = sendType;
	}

	public VoucherParam(TrxorderGoods trxorderGoods, String goodsTitle,
			String voucherCode, String[] mobile, int isSendMerVouInVou,
			SendType sendType) {
		super();
		this.trxorderGoods = trxorderGoods;
		this.goodsTitle = goodsTitle;
		this.voucherCode = voucherCode;
		this.mobile = mobile;
		this.isSendMerVouInVou = isSendMerVouInVou;
		this.sendType = sendType;
	}

	public VoucherParam(TrxorderGoods trxorderGoods, String goodsTitle,
			String voucherCode, String[] mobile, String smsTemplate,
			int smsVouGoodsNameCount, int isSendMerVouInVou) {
		super();
		this.trxorderGoods = trxorderGoods;
		this.goodsTitle = goodsTitle;
		this.voucherCode = voucherCode;
		this.mobile = mobile;
		this.smsTemplate = smsTemplate;
		this.smsVouGoodsNameCount = smsVouGoodsNameCount;
		this.isSendMerVouInVou = isSendMerVouInVou;
	}

	public TrxorderGoods getTrxorderGoods() {
		return trxorderGoods;
	}

	public void setTrxorderGoods(TrxorderGoods trxorderGoods) {
		this.trxorderGoods = trxorderGoods;
	}

	public String getGoodsTitle() {
		return goodsTitle;
	}

	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}

	public String getVoucherCode() {
		return voucherCode;
	}

	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}

	public String[] getMobile() {
		return mobile;
	}

	public void setMobile(String[] mobile) {
		this.mobile = mobile;
	}

	public String getSmsTemplate() {
		return smsTemplate;
	}

	public void setSmsTemplate(String smsTemplate) {
		this.smsTemplate = smsTemplate;
	}

	public int getSmsVouGoodsNameCount() {
		return smsVouGoodsNameCount;
	}

	public void setSmsVouGoodsNameCount(int smsVouGoodsNameCount) {
		this.smsVouGoodsNameCount = smsVouGoodsNameCount;
	}

	public int isSendMerVouInVou() {
		return isSendMerVouInVou;
	}

	public void setSendMerVouInVou(int isSendMerVouInVou) {
		this.isSendMerVouInVou = isSendMerVouInVou;
	}

	public SendType getSendType() {
		return sendType;
	}

	public void setSendType(SendType sendType) {
		this.sendType = sendType;
	}

	public String getEamil() {
		return eamil;
	}

	public void setEamil(String eamil) {
		this.eamil = eamil;
	}

}
