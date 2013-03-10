/**
 * 
 */
package com.beike.util.hao3604j;

/**
 * @author a
 *
 */
public class Tuan360OrderParams {
	//qid INT 360用户ID，360用户唯一标识
	private String qid;
	//order_id INT 订单号
	private String order_id;
	//order_time int 订单时间，格式：20100714090000
	private String order_time;
	//pid string 商品标识
	private String pid;
	//price string 商品单价 单位:元 例如：19.20元
	private String price;
	//number int 购买数量
	private int number;
	//total_price int 总价 单位:元 例如：300.50
	private String total_price;
	//goods_url string 网站商品url，例：http://www.sitename.com/######
	private String goods_url;
	//title string 商品短标题,例：云南原生态云南火锅。20汉字以内，用于团购提醒
	private String title;
	//desc string 商品描述，例：仅售78元！价值186元的简单爱蛋糕（黑森林蛋糕/南瓜无糖慕斯），两款任选其一
	private String desc;
	//spend_close_time int 消费截止时间，例：20100714090000
	private String spend_close_time;
	//merchant_addr string 商家地址，例：朝阳区建国路178号汇通时代广场
	private String merchant_addr;
	
	public String getQid() {
		return qid;
	}
	public void setQid(String qid) {
		this.qid = qid;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getOrder_time() {
		return order_time;
	}
	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getTotal_price() {
		return total_price;
	}
	public void setTotal_price(String total_price) {
		this.total_price = total_price;
	}
	public String getGoods_url() {
		return goods_url;
	}
	public void setGoods_url(String goods_url) {
		this.goods_url = goods_url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getSpend_close_time() {
		return spend_close_time;
	}
	public void setSpend_close_time(String spend_close_time) {
		this.spend_close_time = spend_close_time;
	}
	public String getMerchant_addr() {
		return merchant_addr;
	}
	public void setMerchant_addr(String merchant_addr) {
		this.merchant_addr = merchant_addr;
	}
}
