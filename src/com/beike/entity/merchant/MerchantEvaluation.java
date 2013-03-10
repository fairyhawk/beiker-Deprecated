package com.beike.entity.merchant;

import java.io.Serializable;

/**
 * <p>
 * Title:商家留言，打分
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date May 17, 2011
 * @author ye.tian
 * @version 1.0
 */

@SuppressWarnings("serial")
public class MerchantEvaluation implements Serializable{

	private Long id;

	private double evaluationscore;

	private Long merchantid;

	private String evaluationcontent;

	private Long userId;

	public MerchantEvaluation() {
	}

	public MerchantEvaluation(Long id, double evaluationscore, Long merchantid,
			String evaluationcontent, Long userId) {
		this.id = id;
		this.evaluationscore = evaluationscore;
		this.merchantid = merchantid;
		this.evaluationcontent = evaluationcontent;
		this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getEvaluationscore() {
		return evaluationscore;
	}

	public void setEvaluationscore(double evaluationscore) {
		this.evaluationscore = evaluationscore;
	}

	public Long getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(Long merchantid) {
		this.merchantid = merchantid;
	}

	public String getEvaluationcontent() {
		return evaluationcontent;
	}

	public void setEvaluationcontent(String evaluationcontent) {
		this.evaluationcontent = evaluationcontent;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
