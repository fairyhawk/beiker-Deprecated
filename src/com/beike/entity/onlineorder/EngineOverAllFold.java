package com.beike.entity.onlineorder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.beike.util.DateUtils;
import com.beike.util.json.JSONException;
import com.beike.util.json.JSONObject;


public class EngineOverAllFold extends AbstractEngine implements Serializable{

    private double discount;

    public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", discount=").append(discount);
        sb.append("]");
        return sb.toString();
    }

	@Override
	public double caculatePay(double subAmount) {
		return subAmount * discount;
	}

	@Override
	public String getPromotionInfo() {
		return "<span>全单可享受<em>" + discount + "</em>折优惠</span><cite>优惠时间：[" + DateUtils.dateToStr(super.getStarttime()) + "——" + DateUtils.dateToStr(super.getEndtime()) + "]</cite>";
	}

	@Override
	public String formatJson() {
		JSONObject jo = new JSONObject();
		List<JSONObject> json_arrs = new ArrayList<JSONObject>();
			try {
				jo.put(json_key_discount,discount);
				jo.put(AbstractEngine.json_key_discount_type, "OVERALLFOLD");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			json_arrs.add(jo);
			logger.info(json_arrs.toString());
			return json_arrs.toString();
	}
}