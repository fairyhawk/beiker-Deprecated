package com.beike.form;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;


/**  
* @Title:  评价Form
* @Package com.beike.form
* @Description: TODO
* @author wenjie.mai  
* @date Mar 14, 2012 6:07:02 PM
* @version V1.0  
*/
public class OrderEvaluationForm implements Serializable,Comparable<OrderEvaluationForm> {
	private static final long serialVersionUID = -2564625726981785660L;

	/**
	 * 昵称
	 */
	private String nickname;
	
	/**
	 * 头像地址
	 */	
	private String avatar;
	
	/**
	 * 评价ID
	 */
	private Long id;
	
	/**
	 * 评价打分：0很好；1满意；2差
	 */
	private int score;
	
	/**
	 * 评价内容
	 */
	private String evaluation;
	
	/**
	 * 品牌ID
	 */
	private Long merchantid;
	
	/**
	 * 用户ID
	 */
	private Long userid;
	
	/**
	 * 商品ID
	 */
	private Long goodsid;
	
	/**
	 * 交易ID
	 */
	private Long trxorderid;
	
	/**
	 * 是否系统默认评价：1是 0否
	 */
	private String issysdefault;
	
	/**
	 * 评价时间
	 */
	private Timestamp addtime;
	
	/**
	 * 评价附图
	 */
	private List<String> photoLst;
	
	/**
	 * 评价状态：0正常；1屏蔽
	 */
	private int status;
	
	/**
	 * 商品名称
	 */
	private String goodsName;
	
	/**
	 * 分店ID、分店名称
	 */
	private List<String[]> branchLst;
	
	/**
	 * 评价次数
	 */
	private int ordercount;
	
	/**
	 * 购买单数
	 */
	private int buyCount;
	
	/**
	 * 商品是否上线 1上线，0下线
	 */
	private int isavaliable;
	
	/**
	 * 商品短标题
	 */
	private String goodsTitle;
	
	public String getGoodsTitle() {
		return goodsTitle;
	}

	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}

	public int getIsavaliable() {
		return isavaliable;
	}

	public void setIsavaliable(int isavaliable) {
		this.isavaliable = isavaliable;
	}

	public int getOrdercount() {
		return ordercount;
	}

	public void setOrdercount(int ordercount) {
		this.ordercount = ordercount;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(String evaluation) {
		this.evaluation = evaluation;
	}

	public Long getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(Long merchantid) {
		this.merchantid = merchantid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(Long goodsid) {
		this.goodsid = goodsid;
	}

	public Long getTrxorderid() {
		return trxorderid;
	}

	public void setTrxorderid(Long trxorderid) {
		this.trxorderid = trxorderid;
	}

	public String getIssysdefault() {
		return issysdefault;
	}

	public void setIssysdefault(String issysdefault) {
		this.issysdefault = issysdefault;
	}

	public Timestamp getAddtime() {
		return addtime;
	}

	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<String> getPhotoLst() {
		return photoLst;
	}

	public void setPhotoLst(List<String> photoLst) {
		this.photoLst = photoLst;
	}

	public List<String[]> getBranchLst() {
		return branchLst;
	}

	public void setBranchLst(List<String[]> branchLst) {
		this.branchLst = branchLst;
	}

	@Override
	public int compareTo(OrderEvaluationForm o) {
		if(addtime.before(o.getAddtime())){
			return 1;
		}else{
			return -1;
		}
	}

	public int getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}
}
