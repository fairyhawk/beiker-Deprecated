package com.beike.action.pay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.biz.service.trx.OrderFilmService;
import com.beike.biz.service.trx.OrderFoodService;
import com.beike.common.bean.trx.FilmApiOrderParam;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.TrxOrderException;
import com.beike.core.service.trx.FilmApiGoodsOrderService;
import com.beike.entity.goods.Goods;
import com.beike.util.Constant;
import com.beike.util.StringUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.hao3604j.org.json.JSONArray;

/**
 * @desc 点餐支付类
 * @author ljp
 * @date 20121128
 *
 */
@Controller
public class PayOrderFoodAction {
	private final Log logger = LogFactory.getLog(PayOrderFoodAction.class);
	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();
	@Autowired
	private OrderFoodService orderFoodService;
	@Autowired
	private OrderFilmService orderFilmService;
	@Autowired
	private FilmApiGoodsOrderService filmApiGoodsOrderService;
	/**
	 * @desc 点餐支付处理
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @author ljp
	 * @date 20121128
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/pay/payBillForFood.do")
	public ModelAndView payBillForFood(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			//格式为LIST<Map<String, integer>  menuid---count
			String buyFoodInfo = request.getParameter("buyFoodInfo");
			//活动id
			String orderId = request.getParameter("orderId");
			
			//分店id
			String guestId = request.getParameter("guestId");
			logger.info("--------------payBillForFood---------buyFoodInfo="+buyFoodInfo+"     --orderId="+orderId+"-----guestId--"+guestId);
			//检查活动和分店是否为合法信息
			boolean flag = orderFoodService.checkInputParameter(orderId, guestId);
			//如果不合法返回
			if( !flag ){
				logger.info("--------------pay order food guestid and orderid is illegal--------");
				throw new TrxOrderException(BaseException.MENU_ILLEALL_DATA);
			}
			JSONParser jsonParser = new JSONParser();
			//把购买的菜单信息解析成对象
			List<Map<String, Integer>>	listFoods = (List<Map<String, Integer>>)jsonParser.parse(buyFoodInfo);
			//把购买的菜单信息josn格式
			String menuJsonGoods = createMenuJsonGoods( listFoods );
			logger.info("-------------createMenuJsonSortGoods    is -----"+menuJsonGoods);
			Goods goods = orderFoodService.queryGoodsByBuyFoodInfo(listFoods, orderId,guestId);
			StringBuffer goodsDetailInfo = new StringBuffer();// 将一些商品信息放入memcache
			try {
				goodsDetailInfo.append(URLEncoder.encode(goods.getGoodsname(),
						"utf-8"));// 商品名字--1
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getGoodsId());// 商品ID---2
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getSourcePrice());// 商品原价格--3
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getPayPrice());// 商品购买价格--4
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getRebatePrice());// 商品返现价格--5
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getDividePrice());// 商品分成价格--6
			// TODO加入过期时间等
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getGuestId());// guestID--7
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getOrderLoseAbsDate());// 订单过期时间段--8
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getOrderLoseDate());// 订单过期时间点--9
			goodsDetailInfo.append("&");
			// 加入品牌名称
			try {
				goodsDetailInfo.append(URLEncoder.encode(
						goods.getMerchantname(), "utf-8"));// 品牌名称--10
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			goodsDetailInfo.append("&");
			
			goodsDetailInfo.append("1");// 商品数量--11
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getMerchantid());// 品牌id--12
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getGoodsSingleCount());// 商品个人购买上限--13
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getIsRefund());// 是否自动退款--14
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getSendRules());// 是否发送商家校验码--15
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getIsadvance());// 是否预付款 0：否 1：是--16
			goodsDetailInfo.append("&");
			goodsDetailInfo.append("0");// 秒杀ID--17
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getIsMenu());//是否为点菜单0否1 是18
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(menuJsonGoods);//购买菜单19
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(guestId);//分店id--20
			String goodsDetailInfoStr = goodsDetailInfo.toString();
			logger.info("----diancan----goodsDetailinfoStr-------------"+goodsDetailInfoStr);
			String goodsDetailKey = StringUtils.createUUID();
			memCacheService.set(goodsDetailKey, goodsDetailInfoStr, 18000);// 商品信息放入memcache
			return new ModelAndView("redirect:" + Constant.USER_TRX_LOGIN_REGISTER
					+ "?goodsDetailKey=" + goodsDetailKey);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("++++++++++++Trx-Exception:" + e.getStackTrace());
			throw new Exception();
			
		}
	}
	
	/**
	 * 转换格式把menuId = 1 menucount = 2 转成  map<key menu_id, value = count>
	 * @param listFoods
	 * @return
	 * @throws Exception
	 */
	private String createMenuJsonGoods(List<Map<String, Integer>> listFoods)throws Exception{
		Map<Integer, Integer> idCount = new HashMap<Integer,Integer>();
		List<Integer> listKeyValue = new ArrayList<Integer>();
		for(Map<String, Integer> m : listFoods){
			listKeyValue.add(Integer.parseInt(m.get("menuId")+""));
			Integer count = Integer.parseInt(m.get("menuCount")+"");
			//对前台输入校验
			if(count < 1 ){
				count = 1;
			}
			if(count > 100){
				count = 100;
			}
			idCount.put(Integer.parseInt(m.get("menuId")+""),count);
		}
		
		
		List<Map<String, Integer>> result  = new ArrayList<Map<String,Integer>>();
		for(int i = 0 ; i < listKeyValue.size() ;i ++){
			Map<String,Integer> temp = new HashMap<String, Integer>();
			
			temp.put(listKeyValue.get(i)+"", idCount.get(Integer.parseInt(listKeyValue.get(i)+"")));
			result.add(temp);
		}
		JSONArray array = new JSONArray(result);
		return array.toString();
	}
	
	/**
	 * @desc 接收支付后网票网给的数据
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/pay/payBillForFilmBefore.do")
	public void payBillForFilmBefore(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		
		try {
			String seatInfo = request.getParameter("SeatInfo");//座位信息。格式：11:12。其中11 表示11 排，12 表示12 座， 多个座位用|分隔。如：	11:12|11:11|11:13
			String sid = request.getParameter("SID");//订单号
			String flagId = request.getParameter("LockFlag");//锁座请求唯一标识(由渠道请求锁座时传入)
			logger.info("----payBillForFilmBefore=-----input parameter    seatInfo="+seatInfo+"  sid="+sid+"   flagId="+flagId);
			// TODO 看是否要对入参check
			
			//生成uuid
			String uuid = StringUtils.createUUID();
			//把入参存入缓存服务器
			memCacheService.set("filmInfo"+uuid, seatInfo+"&"+sid+"&"+flagId, 18000);
			logger.info("--------this is payBillForFilmBefore uuid------"+uuid);
			//响应网票网
			response.getWriter().write("http://www.qianpin.com/pay/payBillForFilm.do?uuid="+uuid);
			response.getWriter().flush();
			logger.info("----payBillForFilmBefore over-----------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * @desc 网票网回调c地址
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/pay/payBillForFilm.do")
	public ModelAndView payBillForFilm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			String flag = "";
			String uuid = request.getParameter("uuid");
			//判断此请求是否是来自己本网站还是网票网的
			flag = request.getParameter("flag");
			logger.info("-------this is flag----"+flag);
			if(flag == null || "".equals(flag)){
				request.setAttribute("uuid", uuid);
				return new ModelAndView("/shopcart/wpwRedirect");
			}else{
			
				logger.info("--------this is payBillForFilm uuid------"+uuid);
				String filmInfo = (String)memCacheService.get("filmInfo"+uuid);
				String[] temps = filmInfo.split("&");
				String seatInfo= temps[0];//(String)request.getParameter("SeatInfo");//座位信息。格式：11:12。其中11 表示11 排，12 表示12 座， 多个座位用|分隔。如：	11:12|11:11|11:13
				String sid =temps[1];//(String) request.getParameter("SID");//订单号
				String flagId = temps[2];//request.getParameter("FlagID");//锁座请求唯一标识(由渠道请求锁座时传入)
				
				if(!StringUtils.validNull(uuid) || !StringUtils.validNull(seatInfo) ||!StringUtils.validNull(sid) || !StringUtils.validNull(flagId) ){
					throw new IllegalArgumentException("uuid is illegal");
				}
				String filmShowId = (String)memCacheService.get(flagId);//放映流水号
				//根据选座信息封装goods
				Goods goods = orderFilmService.createFilmGoods(seatInfo, Long.parseLong(filmShowId));
				StringBuffer goodsDetailInfo = new StringBuffer();// 将一些商品信息放入memcache
				try {
					goodsDetailInfo.append(URLEncoder.encode(goods.getGoodsname(),
							"utf-8"));// 商品名字--1
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getGoodsId());// 商品ID---2
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getSourcePrice());// 商品原价格--3
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getPayPrice());// 商品购买价格--4
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getRebatePrice());// 商品返现价格--5
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getDividePrice());// 商品分成价格--6
				// TODO加入过期时间等
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getGuestId());// guestID--7
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getOrderLoseAbsDate());// 订单过期时间段--8
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getOrderLoseDate());// 订单过期时间点--9
				goodsDetailInfo.append("&");
				// 加入品牌名称
				try {
					goodsDetailInfo.append(URLEncoder.encode(
							goods.getMerchantname(), "utf-8"));// 品牌名称--10
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				goodsDetailInfo.append("&");
				
				goodsDetailInfo.append("1");// 商品数量--11
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getMerchantid());// 品牌id--12
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getGoodsSingleCount());// 商品个人购买上限--13
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getIsRefund());// 是否自动退款--14
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getSendRules());// 是否发送商家校验码--15
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(goods.getIsadvance());// 是否预付款 0：否 1：是--16
				goodsDetailInfo.append("&");
				goodsDetailInfo.append("0");// 秒杀ID--17
				goodsDetailInfo.append("&");
				goodsDetailInfo.append("2");//是否为点菜单0否1 是2是电影票18
				goodsDetailInfo.append("&");
				goodsDetailInfo.append(uuid);//uuid    19
				goodsDetailInfo.append("&");
				goodsDetailInfo.append("0");//分店id--20
				String goodsDetailInfoStr = goodsDetailInfo.toString();
				logger.info("----diancan----goodsDetailinfoStr-------------"+goodsDetailInfoStr);
				String goodsDetailKey = StringUtils.createUUID();
				memCacheService.set(goodsDetailKey, goodsDetailInfoStr, 18000);// 商品信息放入memcache
				return new ModelAndView("redirect:" + Constant.USER_TRX_LOGIN_REGISTER
						+ "?goodsDetailKey=" + goodsDetailKey);
			
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("++++++++++++Trx-Exception:" + e.getStackTrace());
			throw new Exception();
			
		}
		
	}
	
	/**
	 * @desc 选完坐后去付款
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/pay/selectSeat.do")
	public String selectSeat(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//获得场次号 也是放映流水号
		String seqNo = request.getParameter("seqNo");
		
		FilmApiOrderParam filmApiOrderParam = new FilmApiOrderParam();
		filmApiOrderParam.setSeqNo(seqNo);
		String lockFlag =  StringUtils.createUUID(); //锁坐请求唯一标识
		logger.info("------lockFlag--------"+lockFlag);
		//把放映流水号放入缓存中
		memCacheService.set(lockFlag, seqNo, 18000);
		
		filmApiOrderParam.setLockFlag(lockFlag);
		String reuslt = filmApiGoodsOrderService.selectFilmSeat(filmApiOrderParam);
		request.setAttribute("address", reuslt);
		
		return "/shopcart/wpwSelectSeat";
	}
	
}
