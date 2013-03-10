package com.beike.common.bean.trx;

import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.Voucher;

/**
 * @Title: VoucherInfo.java
 * @Package com.beike.common.bean.trx
 * @Description: 凭证相关信息及参数集合
 * @date May 27, 2011 5:08:49 PM
 * @author wh.cheng
 * @version v1.0
 */
public class VoucherInfo {
	
	private Voucher voucher;
	
	private TrxorderGoods   trxorderGoods;
	
	
	private TrxOrder trxorder;

	
	
	
	
	public VoucherInfo() {
		super();
	}


	public VoucherInfo(Voucher voucher, TrxorderGoods trxorderGoods,
			TrxOrder trxorder) {
		super();
		this.voucher = voucher;
		this.trxorderGoods = trxorderGoods;
		this.trxorder = trxorder;
	}


	public Voucher getVoucher() {
		return voucher;
	}


	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}


	public TrxorderGoods getTrxorderGoods() {
		return trxorderGoods;
	}


	public void setTrxorderGoods(TrxorderGoods trxorderGoods) {
		this.trxorderGoods = trxorderGoods;
	}


	public TrxOrder getTrxorder() {
		return trxorder;
	}


	public void setTrxorder(TrxOrder trxorder) {
		this.trxorder = trxorder;
	}
	
	
	
	
	
}
