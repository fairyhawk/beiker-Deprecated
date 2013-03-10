package com.beike.biz.service.trx.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.beike.biz.service.trx.OrderFilmService;
import com.beike.common.bean.trx.FilmApiOrderParam;
import com.beike.common.entity.trx.FilmGoodsOrder;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.core.service.trx.FilmApiGoodsOrderService;
import com.beike.dao.goods.GoodsDao;
import com.beike.dao.trx.FilmGoodsOrderDao;
import com.beike.dao.trx.soa.proxy.GoodsSoaDao;
import com.beike.entity.goods.Goods;
import com.beike.util.Amount;
import com.beike.util.StringUtils;
/**
 * @desc 网上选坐电影票的处理服务类
 * @author ljp
 *
 */
@Repository("orderFilmService")
public class OrderFilmServiceImpl implements OrderFilmService{
	@Autowired
	private GoodsSoaDao goodsSoaDao;
	@Autowired
	private GoodsDao goodsDao;
	
	private final Log logger = LogFactory.getLog(OrderFilmServiceImpl.class);
	@Autowired
	private FilmGoodsOrderDao filmGoodsOrderDao;
	@Autowired
	private FilmApiGoodsOrderService filmApiGoodsOrderService;
	@Override
	public Goods createFilmGoods(String seatInfo, Long id) {
		//seatInfo 格式格式：11:12。其中11 表示11 排，12 表示12 座， 多个座位用|分隔。如：	11:12|11:11|11:13
		Map<String, Object> filmShow = goodsSoaDao.getFilmShowByShowIndex(id);
		int filmCount = seatInfo.split("\\|").length;
		Goods goods = goodsDao.getGoodsDaoById(Long.valueOf("2000001"));
		goods.setSourcePrice(Amount.mul(((BigDecimal)filmShow.get("c_price")).doubleValue(),filmCount));
		goods.setPayPrice(Amount.mul(((BigDecimal)filmShow.get("v_price")).doubleValue(),filmCount));
		goods.setRebatePrice(0);
		goods.setDividePrice(0);
		goods.setMerchantname("网票网");
		return goods;
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> addFilmGoodsOrder(FilmGoodsOrder filmGoodsOrder, String sid, String mobile)
			throws Exception {
		if(filmGoodsOrder != null){
			filmGoodsOrderDao.addFilmGoodsOrder(filmGoodsOrder);
			Long filmId = filmGoodsOrderDao.getLastInsertId();
			FilmApiOrderParam filmApiOrderParam = new FilmApiOrderParam();
			filmApiOrderParam.setFilmSid(sid);//锁坐唯一号
			filmApiOrderParam.setPayType("9990");//下单支付方式
			filmApiOrderParam.setMobile(mobile);//下单手机号
			filmApiOrderParam.setMsgType("1");//验票码发送方式1需要系统发送验票码； 2不需要系统发送验票码 
			filmApiOrderParam.setAmount(Amount.mul(filmGoodsOrder.getFilmPrice().doubleValue(),filmGoodsOrder.getFilmCount()));//下单金额
			filmApiOrderParam.setGoodsType("1");//商品类型，默认为1
			String result = filmApiGoodsOrderService.createFilmOrder(filmApiOrderParam);
			logger.info("----------this is wpw applyTicket------output---"+result);
			if(!StringUtils.validNull(result)){
				logger.info("----------this is wpw applyTicket------output---is null;");
				throw new Exception();
			}
			Map<String, String> res = (Map<String, String>)new JSONParser().parse(result);
			if(!StringUtils.validNull(res.get("PayNo"))||!StringUtils.validNull(res.get("SID"))){
				logger.info("----------this is wpw applyTicket------output---is not value--------;");
				throw new Exception();
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("filmId", filmId+"");//网票网下单记录表主键ID
			map.put("filmPayno", res.get("PayNo"));//网票网下单成功回传订单号
			map.put("filmSid", sid);
			return map;
		}else{
			throw new IllegalArgumentException("filmGoodsOrder not null");
		}
			
			
	}



	@Override
	public void updateFilmGoodsOrderById(Long filmId, String filmPayNo,
			Long trxOrderId, Long trxGoodsId) throws StaleObjectStateException{
		FilmGoodsOrder filmGoodsOrder = filmGoodsOrderDao.queryFilmGoodsOrderById(filmId);
		if(filmGoodsOrder != null){
			filmGoodsOrder.setFilmPayNo(filmPayNo);
			filmGoodsOrder.setTrxGoodsId(trxGoodsId);
			filmGoodsOrder.setTrxOrderId(trxOrderId);
		}
		filmGoodsOrderDao.updateFilmGoodsOrderById(filmGoodsOrder);
	}



	@Override
	public Map<String, Object> queryFilmShowByShowIndex(Long showIndex) throws Exception {
		return goodsSoaDao.getFilmShowByShowIndex(showIndex);
	}



	@Override
	public Map<String, Object> queryCinemaInfoByCinemaId(Long cinemaId) throws Exception {
		return goodsSoaDao.getCinemaByCinemaId(cinemaId) ;
	}
	
	
	public void updateStatusByTrxGoodsId(Long trxGoodsId){
		filmGoodsOrderDao.updateStatusByTrxGoodsId(trxGoodsId);
	}



	@Override
	public Long queryCinemaIdByTrxGoodsId(Long trxGoodsId) throws Exception {
		Long cinemaId=(Long)goodsSoaDao.getCinemaIdByTrxGoodsId(trxGoodsId).get("cinema_id");
		if(cinemaId==null){ 
			return null;
		}else{
			return goodsSoaDao.queryQianpinCinemaByWpId(cinemaId);
		}
	}
	
	
	
}
