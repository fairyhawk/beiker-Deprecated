package com.beike.entity.lottery;

import java.sql.Timestamp;

/**
 * 开奖信息
 * 
 * @author janwen
 * @time Dec 19, 2011 5:04:28 PM
 */
public class PrizeInfoNew implements  Comparable<Object>{

	private Timestamp startprize_seedtime;
	private Long startprize_jointnumber;
	private String startprize_seed;
	private String startprize_number;
	
	private String strartprize_status;
	
	private int startprize_id;

	private String startprize_title;
	private String startprize_desc;

	public String getStartprize_title() {
		return startprize_title;
	}

	public void setStartprize_title(String startprize_title) {
		this.startprize_title = startprize_title;
	}


	public Timestamp getStartprize_seedtime() {
		return startprize_seedtime;
	}

	public void setStartprize_seedtime(Timestamp startprize_seedtime) {
		this.startprize_seedtime = startprize_seedtime;
	}

	public Long getStartprize_jointnumber() {
		return startprize_jointnumber;
	}

	public void setStartprize_jointnumber(Long startprize_jointnumber) {
		this.startprize_jointnumber = startprize_jointnumber;
	}

	public String getStartprize_seed() {
		return startprize_seed;
	}

	public void setStartprize_seed(String startprize_seed) {
		this.startprize_seed = startprize_seed;
	}

	public String getStartprize_number() {
		return startprize_number;
	}

	public void setStartprize_number(String startprize_number) {
		this.startprize_number = startprize_number;
	}
	@Override
	public int compareTo(Object o) {
		if(o==null)return -1;
		PrizeInfoNew li=null;
		if(o instanceof PrizeInfoNew){
			li=(PrizeInfoNew) o;
		}
		if(li==null)return -1;
		
		Timestamp ts=li.getStartprize_seedtime();
		Timestamp thists=this.getStartprize_seedtime();
		if(thists.after(ts)){
			return -1;
		}
		return 1;
	}

	public int getStartprize_id() {
		return startprize_id;
	}

	public void setStartprize_id(int startprize_id) {
		this.startprize_id = startprize_id;
	}

	@Override
	public int hashCode() {
		
		return String.valueOf(this.getStartprize_id()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PrizeInfoNew other = (PrizeInfoNew) obj;
		if(other.getStartprize_id()!=this.getStartprize_id())
			return false;
		return true;
	}

	public String getStrartprize_status() {
		return strartprize_status;
	}

	public void setStrartprize_status(String strartprize_status) {
		this.strartprize_status = strartprize_status;
	}

	public String getStartprize_desc() {
		return startprize_desc;
	}

	public void setStartprize_desc(String startprize_desc) {
		this.startprize_desc = startprize_desc;
	}
}
