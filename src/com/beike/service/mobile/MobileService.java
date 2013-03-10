package com.beike.service.mobile;

import java.util.List;
import java.util.Map;

import com.beike.entity.mobile.LbsGoodsInfo;
import com.beike.entity.mobile.LbsMerchantInfo;




/**
 * hessian app service:所有入参(input)参照接口设计图设定
 * 
 * 约定:
 * 所有参数小写命名,参数顺序按照设计图顺序 	
 * 入参(input)全部为Map<String,Object>
 * 结果返回(output):Map<String,Object>
 * 状态码(暂定):"1"(成功),"0"(失败),字符串类型,其它需要布尔判断类型(比如是否....)(1代表yes,0代表no)
 * @author janwen
 * Mar 20, 2012
 * 
 * 参数解释:
 * 
 * 入参
 * areaid:城市id
 * tagid:一级分类
 * tagextid:二级分类
 * lat:纬度
 * lng:经度
 * distance(km):距离
 * st:排序条件
 * start:记录起始数
 * keyword:关键词
 * type:搜索类型(1:商品,2:品牌)
 * reqeustno:请求数量
 * 
 * 返回参数:
 * input:查询条件
 * goodsid
 * storeid:分店列表
 * storename:分店名称
 * storeaddr:分店地址
 * storeregionid:分店一级商圈id
 * coord(经度-纬度)
 * distance(km)
 * branchregionfir:分店一级商圈
 * salescount:销售量
 * brandid:品牌id
 * brandname:品牌名称
 * imgurl
 * branchcatfir:分店一级分类
 * branchcatsec:分店二级分类
 * updatetime:更新时间
 * brandcatfir:品牌一级分类
 * brandcatsec(格式:100010,100020):品牌二级分类
 * title:商品短标题
 * currentprice:现价
 * sourceprice:原价
 * salescount:销售量
 * tel:电话
 * opentime:营业时间
 * isavailable(1,0)
 * 
 * 
 */
public interface  MobileService{

	/**
	 * 
	 * janwen
	 * @param query
	 * @return 商品统计信息(tags,keys)
	 *
	 */
	public Map<String,Object>	getCatStats(SearchParamV2 query);
	
	
	public Map<String,Object> searchGoods(SearchParamV2 param);
	
	
	
	public Map<String,Object> searchBranch(SearchParamV2 param);
	/**
	 * 
	 * @param tagid 
	 * @param tagextid
	 * @param lat
	 * @param lng
	 * @param distance(km)
	 * @param st(dorder:asc,starorder:asc/desc,salesorder:asc/desc)
	 * @param start
	 * 
	 * @return 分店列表code,n,rs{[storeid,storename,storeaddr,storeregionid,storeregionextid,coord(经度-纬度),distance(km),salescount,brandid,brandname,imgurl,branchcatfir,branchcatsec,updatetime,star(好评数),reputation(好评率),isvip(0/1),totalBranches(总分店数)]}
	 * 5:52:34 PM
	 * janwen
	 *
	 */
	public Map<String,Object> getListBranch(Map<String,Object> params);
	
	
	/**
	 * 
	 * janwen
	 * 根据分店id获取分店信息 v2
	 * @param params{querybranchid:[id1,id2]}
	 * @return 分店列表code,rs{[storeid,storename,storeaddr,storeregionid,storeregionextid,coord(经度-纬度),opentime,branchtel,sellgoods[id1,id2,],brandname,brandtel(optional)]}
	 *
	 */
	public Map<String,Object> getListBranchByID(Map<String, Object> params,Double lat,Double lng);
	
	/**
	 * 获取商品/品牌列表
	 * @param keyword(关键词)
	 * @param type(1/2)(1:商品,2:品牌)
	 * @param areaid
	 * @param tagid
	 * @param tagextid
	 * @param regionid
	 * @param regionextid	
	 * @param st(star(星级):asc/desc,porder:asc/desc,torder:asc/desc,relativeorder:asc/desc,quantityorder:asc/desc,starorder:asc/desc)//升序值:asc,降序:desc, i.e(价格降序){st:'porder:desc'}
	 * @param start
	 * @param reqeustno
	 * @return 商品列表:code,n,rs{[{goodsid,listimgurl,title,currentprice,sourceprice,salescount,brandname,brandcatfir,brandcatsec,storelist[{storehname,storeaddr,storeregionid,coord,tel,opentime}],isavailable(1,0),updatetime},coord]}
	 *         品牌列表:code,n,rs{[brandid,imgurl,brandname,brandcatfir,brandcatsec(格式:100010,100020),storelist:[{coord(格式:经度-纬度),storeaddr,storeregionid(一级商圈id),reputation(好评率)}],star(好评数),reputation(好评率),isvip(0(不是)/1),salescount,updatetime]}
	 * 2:30:12 PM
	 * janwen 
	 * 
	 */
	public Map<String,Object> getListGoodsOrBrand(Map<String,Object> params);
	
	
	
	/**
	 * 根据goodsid列表获取商品详情 App V2
	 * janwen
	 * @param {querygoodsids: List<Long>}
	 * @return 商品列表:code,rs{[{goodsid,endtime,imgurl,title,goodsname,currentprice,sourceprice,salescount,discount,brandname,brandphone,brandcatfir,brandcatsec,kindlytip,endtime,storelist[id1,id2,id3]],isavailable,isRefund(是否支持退款 0:不支持退款 1:支持退款)}
	 *
	 */
	public Map<String,Object> getListGoodsByIDs(Map<String,Object> params);
	
	/**
	 * 注册 value值3DES解密
	 * @param email
	 * @param password
	 * @return code,uuid
	 * 11:09:59 AM
	 * janwen 
	 *
	 */
	public Map<String,Object> addNewUser(Map<String,Object> params);
	
	 /** 
	 * 用户注册,绑定手机时,验证邮箱或者手机号
	 * @param email
	 * @param tel
	 * @return code
	 * 11:10:40 AM
	 * janwen
	 *
	 */
	public Map<String,Object> updateRegisterMailPhone(Map<String,Object> params);
	
	/**
	 * 绑定手机获取验证码
	 * @param tel
	 * @param uuid
	 * @param reqChannel
	 * @return code
	 * 2:24:54 PM
	 * janwen 
	 *
	 */
	public Map<String,Object> getAuthCode(Map<String,Object> params);
	
	
	/**
	 * 绑定手机提交验证码
	 * @param auth
	 * @param tel
	 * @param uuid
	 * @return code
	 * 2:26:18 PM
	 * janwen 
	 *
	 */
	public Map<String,Object> checkAuthCode(Map<String,Object> params);
	
	/**
	 * 
	 * @param goodsid
	 * @return code,title,imgurl,currentprice,sourceprice,salescount,isavailable(是否可买),brandname,brandcatfir(多个商圈逗号隔开),brandcatsec(多个商圈逗号隔开),storelist:[{storeid,storename,storeregionid(一级商圈id多个逗号隔开),storeregionextid,coord(格式:经度-纬度),addr,tel,opentime,storedesc,reputation(分店好评率)}],tip,updatetime,brandid,max(限购数量),branddesc(允许返回空值)
	 * 11:26:40 AM
	 * janwen 商品详情
	 *
	 */
	public Map<String,Object> getGoodsDetail(Map<String,Object> params);
	
	
	
	/**品牌详情
	 * 
	 * @param brandid
	 * @return code,brandname,imgurl,brandstory,imgs(完整路径不包括服务器域名地址),brandcatfir,brandcatsec,star(好评数),reputation(好评率),storelist:[{storeid,storename,coord(格式:经度-纬度),storeaddr,storeregionid(一级商圈id),tel,opentime,reputation(分店好评率)},updatetime]
	 * 5:47:34 PM
	 * janwen
	 *
	 */
	public Map<String,Object> getBrandDetail(Map<String,Object> params);
	
	
	/**
	 * 品牌商品列表
	 * @param brandid
	 * @return code,rs:[{goodsid,listimgurl,title,currentprice,sourceprice,salescount,isavailable,updatetime,storeregionid,storeregionextid}]
	 * 5:49:16 PM
	 * janwen
	 *
	 */
	public Map<String,Object> getBrandGoods(Map<String,Object> params);
	
	/**
	 * 数据字典:城市
	 * @return code,rs:[{area_id,area_cn_name,area_en_name,area_parent_id}]
	 * 11:28:37 AM
	 * janwen
	 *
	 */
	public Map<String,Object> getCityDic();
	
	/**数据字典
	 * 商品分类
	 * @param tagid
	 * @param cityid
	 * @return code,rs:[{id,tag_name,parentid,boost}]
	 * 11:30:14 AM
	 * janwen
	 *
	 */
	public Map<String,Object> getGoodsCat(Map<String,Object> params);
	
	
	/**数据字典商圈
	 * @param cityid
	 * @param parentid
	 * @return code,rs:[{id,region_name,parentid}]
	 * 11:30:32 AM
	 * janwen
	 *
	 */
	public Map<String,Object> getRegionDic(Map<String,Object> params);
	
	
	/**
	 * 千品账号登陆
	 * @param email
	 * @param tel
	 * @param password
	 * @return code,uuid,email,tel
	 * 2:17:51 PM
	 * janwen
	 *
	 */
	public Map<String,Object> loginWithQP(Map<String,Object> params);
	
	
	/**
	 * 第三方登录
	 * @param openid
	 * @param pr(sina/renren/tencent/baidu/qq)
	 * @return code,uuid,email,tel
	 * 2:20:33 PM
	 * janwen
	 *
	 */
	public Map<String,Object> loginWithOpenID(Map<String,Object> params);
	
	/**
	 * 绑定千品账号
	 * @param openid
	 * @param name
	 * @param pr(sina/renren/tencent/baidu/qq)
	 * @param email
	 * @param password
	 * @param tp(1,0)
	 * @return code,uuid,tel
	 * 2:22:17 PM
	 * janwen
	 *
	 */
	public Map<String,Object> addQPAndOpenID(Map<String,Object> params);
	
	
	/**
	 * 
	 * @return code,cdnurl(s2.qianpin.com)
	 * 7:44:00 PM
	 * janwen
	 *
	 */
	public Map<String,Object> getCDN();
	
	/**
	 * 根据userid获取uuid
	 * @param uuuid
	 * @return code,userid
	 * 2:08:02 PM
	 * janwen
	 *
	 */
	public Map<String,Object> getUserIDByUUID(Map<String,Object> params);
	
	
	
	/**
	 * 获取用户信息
	 * @param uuuid
	 * @return code,email,tel
	 * @author wenjie.mai
	 */
	public Map<String,Object> getUserInfo(Map<String,Object> params);
	
	/**
	 * 邮件告警
	 * @param emails:[{emailaddress}]
	 * @param message
	 * @return code,
	 */
	public Map<String,Object> emailAlert(Map<String,Object> params);
	
	/**
	 * 短信告警
	 * @param phone:[{phonenumber}]
	 * @param message
	 * @return
	 */
	public Map<String,Object> messageAlert(Map<String,Object> params);
	
	/**
	 * 获取分店信息
	 * @param dataCount 分页数据量
	 * @param lastMaxId 上次最大分店ID，下次查询从ID>lastMaxId开始查询数据
	 * @return
	 */
	public List<LbsMerchantInfo> getLbsMerchantInfo(int dataCount, Long lastMaxId);
	
	/**
	 * 获取商品信息
	 * @param dataCount 分页数据量
	 * @param lastMaxMerchantId 上次最大分店ID
	 * @param lastMaxGoodsId 上次最大商品ID
	 * @return
	 */
	public List<LbsGoodsInfo> getLbsGoodsInfo(int dataCount, Long lastMaxMerchantId, Long lastMaxGoodsId);
	
	/**
	 * 
	 * @Title: getGoodsAllCat
	 * @Description: 通过城市ID获得所有分类(包括一级、二级分类)
	 * @param cityid
	 * @return Map<String,Object>
	 * @author wenjie.mai
	 */
	public Map<String,Object> getGoodsAllCat(Map<String,Object> params);
	
	
	/**
	 * 
	 * @Title: getRegionAllDic
	 * @Description: 通过城市ID获得所有商圈(包括一级、二级商圈)
	 * @param  cityid
	 * @return Map<String,Object>
	 * @author wenjie.mai
	 */
	public Map<String,Object> getRegionAllDic(Map<String,Object> params);
	
	/**
	 * 
	 * @Title: oldUserloginWithOpenID
	 * @Description: 老用户第三方账户快速登录
	 * @param  openid
	 * @param  pr(sina/renren/tencent/baidu/qq)
	 * @return code,uuid
	 * @return Map<String,Object>
	 * @author wenjie.mai
	 */
	public Map<String,Object> oldUserloginWithOpenID(Map<String,Object> params);
	
	/**
	 * 
	 * @Title: oldUserBindOpenId
	 * @Description: 老用户第三方账户连接绑定
	 * @param openid
	 * @param name
	 * @param pr(sina/renren/tencent/baidu/qq)
	 * @param email
	 * @param password
	 * @return code,uuid,tel 
	 * @author wenjie.mai
	 */
	public Map<String,Object> oldUserBindOpenId(Map<String,Object> params);
	
	/**
	 * 
	 * @Title: newUserBindOpenIdReg
	 * @Description: 新用户第三方账户快速注册
	 * @param openid
	 * @param name
	 * @param pr(sina/renren/tencent/baidu/qq)
	 * @return code,uuid
	 * @author wenjie.mai
	 */
	public Map<String,Object> newUserBindOpenIdReg(Map<String,Object> params);
	
	/**
	 * 
	 * @Title: newUserVerifyMobileReg
	 * @Description: 新用户手机验证后注册
	 * @param  tel
	 * @param  password
	 * @return code,uuid
	 * @author wenjie.mai
	 */
	public Map<String,Object> newUserVerifyMobileReg(Map<String,Object> params);
	
	/**
	 * 
	 * @Title: oldUserVerifyMobileLogin
	 * @Description: 老用户手机验证后登录
	 * @param  tel
	 * @param  password
	 * @return code,uuid
	 * @author wenjie.mai
	 */
	public Map<String,Object> oldUserVerifyMobileLogin(Map<String,Object> params);
	
	/**
	 * 
	 * @Title: getMobileAuthCode
	 * @Description: 取验证码 
	 * @param tel
	 * @para  templateName
	 * @return code 
	 * @author wenjie.mai
	 */
	public Map<String,Object> getMobileAuthCode(Map<String,Object> params);
	
	/**
	 * 
	 * @Title: checkMobileAuthCode
	 * @Description: 校验验证码
	 * @param tel
	 * @param auth
	 * @return code 
	 * @author wenjie.mai
	 */
	public Map<String,Object> checkMobileAuthCode(Map<String,Object> params);
	
	/**
	 * 
	 * @Title: getAuthCodeForForgetPWD
	 * @Description: 忘记密码:获取验证码
	 * @param  tel
	 * @return code
	 * @author wenjie.mai
	 */
	public Map<String,Object> getAuthCodeForForgetPWD(Map<String,Object> params);
	
	/**
	 * 
	 * @Title: sendPWDForForgetPWD
	 * @Description: 忘记密码:发送密码
	 * @param  tel
	 * @param  auth
	 * @return code
	 * @author wenjie.mai
	 */
	public Map<String,Object> sendOldPwdForForgetPWD(Map<String,Object> params);
	
	/**
	 * 
	 * @Title: getUserByUserId
	 * @Description: 通过UserId查询User
	 * @param  userId
	 * @return email、mobile
	 * @author wenjie.mai
	 */
	public Map<String,Object> getUserByUserId(Map<String,Object> params);
	
}
