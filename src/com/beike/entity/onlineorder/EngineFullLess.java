package com.beike.entity.onlineorder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.beike.util.DateUtils;
import com.beike.util.json.JSONException;
import com.beike.util.json.JSONObject;


public class EngineFullLess extends AbstractEngine implements Serializable{
    

    private double fullAmount;

    private double lessAmount;

   



    public double getFullAmount() {
		return fullAmount;
	}

	public void setFullAmount(double fullAmount) {
		this.fullAmount = fullAmount;
	}

	public double getLessAmount() {
		return lessAmount;
	}

	public void setLessAmount(double lessAmount) {
		this.lessAmount = lessAmount;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", fullAmount=").append(fullAmount);
        sb.append(", lessAmount=").append(lessAmount);
        sb.append("]");
        return sb.toString();
    }

	@Override
	public double caculatePay(double subAmount) {
		if(subAmount >= fullAmount){
			return subAmount - (subAmount/fullAmount) * lessAmount;
		}
		return subAmount;
	}

	@Override
	public String getPromotionInfo() {
		return "<span>每满<em>" + this.fullAmount + "</em>元 减<em>" + this.lessAmount + "</em>元</span>" + "</span><cite>优惠时间：["  + DateUtils.dateToStr(super.getStarttime()) +  "——" + DateUtils.dateToStr(super.getEndtime()) + "]</cite>";
	}
	
	

	@Override
	public String formatJson() {
		JSONObject jo = new JSONObject();
		List<JSONObject> json_arrs = new ArrayList<JSONObject>();
			try {
				jo.put(AbstractEngine.json_key_price, fullAmount);
				jo.put(json_key_discount,lessAmount);
				jo.put(AbstractEngine.json_key_discount_type, "FULLLESS");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			json_arrs.add(jo);
			logger.info(json_arrs.toString());
			return json_arrs.toString();
	}
}