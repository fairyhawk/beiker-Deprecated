/**  
* @Title: Flagship.java
* @Package com.beike.form
* @Description: TODO(用一句话描述该文件做什么)
* @author Grace Guo guoqingcun@gmail.com  
* @date 2013-1-16 下午3:00:17
* @version V1.0  
*/
package com.beike.entity.flagship;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.beike.entity.goods.Goods;
import com.beike.entity.onlineorder.OrderMenu;
import com.beike.entity.takeaway.TakeAway;
import com.beike.entity.takeaway.TakeAwayMenu;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.form.TakeAwayDetailForm;

/**
 * @ClassName: Flagship
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Grace Guo guoqingcun@gmail.com
 * @date 2013-1-16 下午3:00:17
 *
 */
public class Flagship implements Serializable{

	/**
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/
	private static final long serialVersionUID = 3729116276540904088L;
	
	public final static Integer ORDER_MENU_COUNT = 3;//从可点餐的分店中取3道菜

	public final static Integer TAKEAWAY_MENU_COUNT = 16;//从可外卖的分店中取16个菜
	
	public final static Integer HOt_PRODUCT_COUNT = 9;//取9个卖商品
	
	private Long id;//旗舰店标识
	
	private Long guestId;//商家
	
	private Long brandId;//品牌
	
	private Long city;//城市
	
	private String realmName;//二级域名
	
	private String sinaMicroBlogName;//新浪微博名字
	
	private String sinaMicroBlog;//新浪微博账号
	
	private String qqMicroBlog;//QQ微博账号
	
	private String flagshipBackgroundColor;//店面背景色
	
	private String flagshipBackgroundImg;//店面背景图片
	
	private Long mouldId;//店面模板标识
	
	private String mouldName;//店面模板名称
	
	private String mouldImg;//
	
	private String mouldUrl;//店面jsp

	private String branchs;//旗舰店下属分店
	
	private Goods recommendGoods;//推荐商品
	
	private Integer recommendSalecount;//推荐商品销售数量
	
	private List<GoodsForm> hotGoodsList;//热销商品
	
	private MerchantForm merchant;//品牌
	
	private List<String[]> album;//相册
	
	private List<OrderMenu> orderMenus;//点餐
	
	private Map<TakeAway,List<TakeAwayMenu>> takeaways;//外卖
	
	private String flagshipName;//旗舰店名称
	
	private GoodsForm topGoods;
	
	/**
	 * 优惠信息
	 */
	private List<String> offersContents;
	
	public List<String> getOffersContents() {
		return offersContents;
	}

	public void setOffersContents(List<String> offersContents) {
		this.offersContents = offersContents;
	}

	public GoodsForm getTopGoods() {
		return topGoods;
	}

	public void setTopGoods(GoodsForm topGoods) {
		this.topGoods = topGoods;
	}

	public String getFlagshipName() {
		return flagshipName;
	}

	public void setFlagshipName(String flagshipName) {
		this.flagshipName = flagshipName;
	}

	private String flagshipLogo;//旗舰店logo
	
	public List<OrderMenu> getOrderMenus() {
		return orderMenus;
	}

	public void setOrderMenus(List<OrderMenu> orderMenus) {
		this.orderMenus = orderMenus;
	}

	public Goods getRecommendGoods() {
		return recommendGoods;
	}

	public void setRecommendGoods(Goods recommendGoods) {
		this.recommendGoods = recommendGoods;
	}

	public List<GoodsForm> getHotGoodsList() {
		return hotGoodsList;
	}

	public void setHotGoodsList(List<GoodsForm> hotGoodsList) {
		this.hotGoodsList = hotGoodsList;
	}

	public MerchantForm getMerchant() {
		return merchant;
	}

	public void setMerchant(MerchantForm merchant) {
		this.merchant = merchant;
	}

	public Map<TakeAway, List<TakeAwayMenu>> getTakeaways() {
		return takeaways;
	}

	public void setTakeaways(Map<TakeAway, List<TakeAwayMenu>> takeaways) {
		this.takeaways = takeaways;
	}
	
	public String getFlagshipLogo() {
		return flagshipLogo;
	}

	public void setFlagshipLogo(String flagshipLogo) {
		this.flagshipLogo = flagshipLogo;
	}

	public List<String[]> getAlbum() {
		if(album==null)
			return getMerchant()==null ? null : getMerchant().getListMerchantbaoLogo();
		else 
		    return album;
	}

	public void setAlbum(List<String[]> album) {
		this.album = album;
	}

	public String getBranchs() {
		return branchs;
	}

	public void setBranchs(String branchs) {
		this.branchs = branchs;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGuestId() {
		return guestId;
	}

	public void setGuestId(Long guestId) {
		this.guestId = guestId;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public Long getCity() {
		return city;
	}

	public void setCity(Long city) {
		this.city = city;
	}

	public String getRealmName() {
		return realmName;
	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}

	public String getSinaMicroBlog() {
		if(StringUtils.isBlank(sinaMicroBlog))
			return null;
		if(sinaMicroBlog.startsWith("http://"))
		    return sinaMicroBlog;
		else
			return "http://"+sinaMicroBlog;
	}

	public void setSinaMicroBlog(String sinaMicroBlog) {
		this.sinaMicroBlog = sinaMicroBlog;
	}

	public String getQqMicroBlog() {
		if(StringUtils.isBlank(qqMicroBlog))
			return null;
		if(qqMicroBlog.startsWith("http://"))
		    return qqMicroBlog;
		else
			return "http://"+qqMicroBlog;
	}

	public void setQqMicroBlog(String qqMicroBlog) {
		this.qqMicroBlog = qqMicroBlog;
	}

	public String getFlagshipBackgroundColor() {
		return flagshipBackgroundColor;
	}

	public void setFlagshipBackgroundColor(String flagshipBackgroundColor) {
		this.flagshipBackgroundColor = flagshipBackgroundColor;
	}

	public String getFlagshipBackgroundImg() {
		return flagshipBackgroundImg;
	}

	public void setFlagshipBackgroundImg(String flagshipBackgroundImg) {
		this.flagshipBackgroundImg = flagshipBackgroundImg;
	}

	public Long getMouldId() {
		return mouldId;
	}

	public void setMouldId(Long mouldId) {
		this.mouldId = mouldId;
	}

	public String getMouldName() {
		return mouldName;
	}

	public void setMouldName(String mouldName) {
		this.mouldName = mouldName;
	}

	public String getMouldImg() {
		return mouldImg;
	}

	public void setMouldImg(String mouldImg) {
		this.mouldImg = mouldImg;
	}

	public String getMouldUrl() {
		return mouldUrl;
	}

	public void setMouldUrl(String mouldUrl) {
		this.mouldUrl = mouldUrl;
	}
	
	public String getSinaMicroBlogName() {
		return StringUtils.isBlank(sinaMicroBlogName) ? null : sinaMicroBlogName.trim();
	}

	public void setSinaMicroBlogName(String sinaMicroBlogName) {
		this.sinaMicroBlogName = sinaMicroBlogName;
	}
	
	public Integer getRecommendSalecount() {
		return recommendSalecount;
	}

	public void setRecommendSalecount(Integer recommendSalecount) {
		this.recommendSalecount = recommendSalecount;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
	
	/*public static void main(String[] args) {
		Flagship f = new Flagship();
		f.setSinaMicroBlog("http://dls.weibo.com");
		System.out.println(f.getSinaMicroBlog());
		f.setQqMicroBlog("http://dls.qq.weibo.com");
		System.out.println(f.getQqMicroBlog());
	}*/
}
