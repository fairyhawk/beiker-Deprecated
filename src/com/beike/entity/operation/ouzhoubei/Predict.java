package com.beike.entity.operation.ouzhoubei;

public class Predict {

	
	@Override
	public String toString() {
		return "欧洲杯活动竞猜用户日志:Predict [userid=" + userid + ", match_id=" + match_id
				+ ", predict_score=" + predict_score + ", matchteams="
				+ matchteams + "]";
	}
	private Long userid;
	private Long match_id;
	private String predict_score;
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public Long getMatch_id() {
		return match_id;
	}
	public void setMatch_id(Long match_id) {
		this.match_id = match_id;
	}
	public String getPredict_score() {
		return predict_score;
	}
	public void setPredict_score(String predict_score) {
		this.predict_score = predict_score;
	}
	public String getMatchteams() {
		return matchteams;
	}
	public void setMatchteams(String matchteams) {
		this.matchteams = matchteams;
	}
	private String matchteams;
	
	
}
