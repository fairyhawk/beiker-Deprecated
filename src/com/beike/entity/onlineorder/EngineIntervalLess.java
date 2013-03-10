package com.beike.entity.onlineorder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.beike.util.DateUtils;
import com.beike.util.json.JSONException;
import com.beike.util.json.JSONObject;

public class EngineIntervalLess extends AbstractEngine implements Serializable{

	private List<Interval> intervals;

	public List<Interval> getIntervals() {
		return intervals;
	}

	public void setIntervals(List<Interval> intervals) {
		this.intervals = intervals;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append("]");
		return sb.toString();
	}

	@Override
	public double caculatePay(double subAmount) {
		for (int i = 0; i < this.intervals.size(); i++) {
			Interval interval = intervals.get(i);
			//TODO janwen
			if (subAmount >= interval.getInterval_amount()
				) {
				return subAmount;
			}
		}
		return subAmount;
	}

	@Override
	public String getPromotionInfo() {

		String promotion = "";
		Collections.sort(intervals);
		for (int i = 0; i < this.intervals.size(); i++) {
			Interval interval = intervals.get(i);
			promotion = promotion + "全店单笔订单<em>满" + interval.getInterval_amount() + "减" + interval.getLess_amount() + "</em><br />";
		}
		promotion ="<span>"+ promotion + "</span><cite>优惠时间：["+DateUtils.dateToStr(super.getStarttime()) + "——" + DateUtils.dateToStr(super.getEndtime()) + "]</cite>";

		return promotion;
	}

	
	@Override
	public String formatJson() {
		
		List<JSONObject> json_arrs = new ArrayList<JSONObject>();
		for(int i=0;i<this.intervals.size();i++){
			Interval inter = intervals.get(i);
			JSONObject jo = new JSONObject();
			try {
				jo.put(json_key_discount_type, "INTERVALLESS");
				jo.put(json_key_discount, inter.getLess_amount());
				jo.put(json_key_price, inter.getInterval_amount());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			json_arrs.add(jo);
		}
		logger.info(json_arrs.toString());
		return json_arrs.toString();
	}
}