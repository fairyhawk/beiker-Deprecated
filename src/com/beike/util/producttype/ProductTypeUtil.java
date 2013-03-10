package com.beike.util.producttype;

public class ProductTypeUtil {
	/**
	 *  查询商品类型
	 * @param beiker_trxorder_goods表的biztype,beiker_goods的couponcash
	 * @return
	 */
	public static String getProductType(String biztype,String couponcash){
		String result = "";
		if(biztype.equals("1")){
			result = "网上商店";
		}else {
			if(couponcash.equals("0")){
				result = "团购套餐";
			}else if(couponcash.equals("1")){
				result = "现金券";
			}else {
				result = "代金券";
			}
		}
		return result;
	}
}
