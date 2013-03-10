package com.beike.entity.operation.ouzhoubei;

import java.sql.Timestamp;

public class MatchInfo {

	private Long id;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private String matchteams;
	

	public String getMatchteams() {
		return matchteams;
	}

	public void setMatchteams(String matchteams) {
		this.matchteams = matchteams;
	}

	public Timestamp getMatchtime() {
		return matchtime;
	}

	public void setMatchtime(Timestamp matchtime) {
		this.matchtime = matchtime;
	}

	public String getMatchscore() {
		return matchscore;
	}

	public void setMatchscore(String matchscore) {
		this.matchscore = matchscore;
	}

	private  Timestamp matchtime;
	
	private String matchscore;
}
