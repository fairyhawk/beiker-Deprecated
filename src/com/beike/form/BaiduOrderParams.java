/**
 * 
 */
package com.beike.form;

/**
 * @author a
 *
 */
public class BaiduOrderParams {
	////订单号，在提交方系统中唯一
	private String order_id;
	////团购商品短标题 <255 bytes
	private String title;
	//商品描述，例如： 价值186元的简单爱蛋糕（南瓜无糖） <2048bytes
	private String summary;
	//团购商品图片（海报）url<255bytes
	private String logo;
	//团购商品url（需要和提交给百度导航的xml api中的商品地址完全一致）<255bytes
	private String url;
	//商品单价 单位：分 如2100表示rmb21.00
	private long price;
	//购买数量
	private long goods_num;
	//总价 单位：分 例如：300000
	private long sum_price;
	//消费券过期时间，自Jan 1 1970 00:00:00 GMT的秒数; 0为不限制
	private long expire;
	//商家地址，例如：朝阳区建国路178号汇通时代广场; <1024bytes
	private String addr;
	//百度uid，如无tn参数，则此参数必填
	private String uid;
	//用户手机号
	private String mobile;
	//从tuan.baidu.com过来的链接中获取（建议保持在cookie，下单时保存在数据库，便于付款成功后回传）
	private String tn;
	//从tuan.baidu.com过来的链接中获取（建议保持在cookie，下单时保存在数据库，便于付款成功后回传）
	private String baiduid;
	//百度分成金额（单位：分），值为订单总价*分成比例
	private long bonus;
	//用户下单时间，即订单号码生成的时间（非用户支付完成时间），自Jan 1 1970 00:00:00 GMT的秒数
	private long order_time;
	//团购商品在百度团购API中对应的城市名称
	private String order_city;
	
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		if(title!=null && title.length()>127){
			title = title.substring(0, 127);
		}
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getPrice() {
		return price;
	}
	public void setPrice(long price) {
		this.price = price;
	}
	public long getGoods_num() {
		return goods_num;
	}
	public void setGoods_num(long goods_num) {
		this.goods_num = goods_num;
	}
	public long getSum_price() {
		return sum_price;
	}
	public void setSum_price(long sum_price) {
		this.sum_price = sum_price;
	}
	public long getExpire() {
		return expire;
	}
	public void setExpire(long expire) {
		this.expire = expire;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getTn() {
		return tn;
	}
	public void setTn(String tn) {
		this.tn = tn;
	}
	public String getBaiduid() {
		return baiduid;
	}
	public void setBaiduid(String baiduid) {
		this.baiduid = baiduid;
	}
	public long getBonus() {
		return bonus;
	}
	public void setBonus(long bonus) {
		this.bonus = bonus;
	}
	public long getOrder_time() {
		return order_time;
	}
	public void setOrder_time(long order_time) {
		this.order_time = order_time;
	}
	public String getOrder_city() {
		return order_city;
	}
	public void setOrder_city(String order_city) {
		this.order_city = order_city;
	}
}
