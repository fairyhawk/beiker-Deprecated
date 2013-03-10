package com.beike.core.service.trx.lottery.full.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.common.entity.trx.lottery.full.FullLottery;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.FullLotteryException;
import com.beike.core.service.trx.lottery.full.FullLotteryService;
import com.beike.core.service.trx.lottery.reg.impl.LotteryRegServiceImpl;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.lottery.full.FullLotteryDao;
import com.beike.entity.goods.Goods;
import com.beike.service.goods.GoodsService;
import com.beike.util.DateUtils;
import com.beike.util.LotteryConstant;
import com.beike.util.StringUtils;

@Service("fullLotteryService")
public class FullLotteryServiceImpl implements FullLotteryService
{

	@Autowired
	private FullLotteryDao fullLotteryDao;

	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;

	@Autowired
	private GoodsService goodsService;

	private final Log logger = LogFactory.getLog(LotteryRegServiceImpl.class);

	private static int payPriceSumBase = 51;
	@Autowired
	private TrxSoaService trxSoaService;

	@Override
	public List<FullLottery> findFullLotteryList(int recordNumber)
	{
		List<FullLottery> fullLotteryList = fullLotteryDao.findFullLotteryList(recordNumber);

		if (fullLotteryList != null && fullLotteryList.size() > 0)
		{
			for (FullLottery full : fullLotteryList)
			{
				Map<String, Object> useMap = trxSoaService.findUserInfoById(full.getUserId());

				if (null != useMap && null != useMap.get("email") && StringUtils.validNull(useMap.get("email").toString()))
				{
					String lryContentId = full.getLotteryContent();// 中奖奖品id
					List<Long> goodsIdList = new ArrayList<Long>();
					goodsIdList.add(Long.parseLong(lryContentId));
					Map<Long, String> goodsNameMap = trxSoaService.findGoodsTitle(goodsIdList);
					String prizeName = goodsNameMap.get(Long.parseLong(lryContentId));
					String email = useMap.get("email").toString();
					String[] arrayEmail = email.split("@");
					String emailName = arrayEmail[0];
					if (emailName.length() > 5)
					{
						emailName = emailName.substring(0, 5) + "****";
					} else
					{
						emailName = emailName + "****";
					}
					full.setUserEmail(emailName);
					full.setPrizeName(StringUtils.getLength(prizeName, 6));
				}
			}
		}
		return fullLotteryList;
	}

	@Override
	public FullLottery processFullLottery(Long userId, String localCity, Long lotteryType) throws FullLotteryException, Exception
	{
		// 判断 是否 抽奖时间范围内
		Date beginDate = DateUtils.toDate(LotteryConstant.fullBeginDate, "yyyy-MM-dd HH:mm:ss");
		Date endDate = DateUtils.toDate(LotteryConstant.fullEndDate, "yyyy-MM-dd HH:mm:ss");
		if (!DateUtils.betweenBeginAndEnd(new Date(), beginDate, endDate))
		{
			logger.debug("+++processFullLottery:++userId:" + userId + "+++localCity:" + localCity + "+lotteryType:" + lotteryType + "++Exception:" + BaseException.LOTTERY_NO_PRIZE + "+++ ");
			throw new FullLotteryException(BaseException.LOTTERY_NO_PRIZE);
		}

		// 判断 用户剩余的可抽奖次数
		int userLotteryCount = findTrxorderGoodsAndFullLottery(userId, LotteryConstant.fullBeginDate, LotteryConstant.fullEndDate);
		if (userLotteryCount <= 0)
		{
			logger.debug("+++processFullLottery:++userId:" + userId + "+++localCity:" + localCity + "+lotteryType:" + lotteryType + "++Exception:" + BaseException.LOTTERY_REMAIN_COUNT_NOT_ENOUGH + "+++ ");
			throw new FullLotteryException(BaseException.LOTTERY_REMAIN_COUNT_NOT_ENOUGH);
		}

		String lotteryContent = "";// 奖项初始值
		long isLottery = 0;// 是否中奖初始值
		FullLottery fullLottery = null;
		int random = (int) (Math.random() * 100);

		if ("".equals(localCity) || !LotteryConstant.lotteryFullMap.containsKey(localCity))
		{
			localCity = "beijing";
		}

		String lotteryInfo = LotteryConstant.lotteryFullMap.get(localCity);
		String[] goodsIdLottery = lotteryInfo.split(";");

		for (String goods : goodsIdLottery)
		{
			String goodsId = goods.split(":")[0];
			String goodsItem = goods.split(":")[1];
			int startNum = Integer.parseInt(goodsItem.split("-")[0]);
			int endNum = Integer.parseInt(goodsItem.split("-")[1]);

			if (startNum <= random && random <= endNum)
			{
				// 如果下线商品被抽中，就在剩余商品里找第一个概率不为0的， 且在线的商品， 如果没找到： 提示奖品已被抽完
				Long goodsIdChecked = getGoodsIdForLottery(localCity, Long.parseLong(goodsId));
				if (null == goodsIdChecked)
				{
					logger.debug("+++processFullLottery:++userId:" + userId + "+++localCity:" + localCity + "+lotteryType:" + lotteryType + "++Exception:" + BaseException.LOTTERY_NO_PRIZE + "+++ ");
					throw new FullLotteryException(BaseException.LOTTERY_NO_PRIZE);
				}

				goodsId = String.valueOf(goodsIdChecked);
				isLottery = 1;
				lotteryContent = goodsId;// 中奖后赋予中奖值
				break;
			}
		}

		// 拼装抽奖信息
		fullLottery = new FullLottery();
		fullLottery.setUserId(userId);
		fullLottery.setIsLottery(isLottery);
		fullLottery.setLotteryContent(lotteryContent);
		fullLottery.setCityName(LotteryConstant.lotteryFullCityMap.get(localCity).toString());
		fullLottery.setLotteryType(lotteryType);
		fullLottery.setDescription("51满额抽奖");

		if (1 == isLottery)
		{
			// 线上商品抽奖
			if (0 == lotteryType.longValue())
			{
				// 调用下单
				Map<String, String> rspMap = processCreateTrxOrder(userId, Long.parseLong(lotteryContent));
				String rspCode = rspMap.get("rspCode");
				String rspStatus = rspMap.get("status");
				if ("SUCCESS".equals(rspStatus) && "1".equals(rspCode))
				{
					// 如果下单返回1，则插入中奖信息
					logger.debug("+++havePrize:++userId:" + userId + "+++localCity:" + localCity + "+lotteryType:" + lotteryType + "++++lotteryContent:" + lotteryContent + "++++ ");
					fullLotteryDao.addFullLottery(fullLottery);

				} else
				{
					logger.debug("+++processFullLottery:++userId:" + userId + "+++localCity:" + localCity + "+lotteryType:" + lotteryType + "++Exception:" + BaseException.LOTTERY_PRIZE_DISPCHER_FAILED + "+++ ");
					throw new FullLotteryException(BaseException.LOTTERY_PRIZE_DISPCHER_FAILED);
				}
			}
			// 线下商品抽奖
			else if (1 == lotteryType.longValue())
			{

			}
			// 虚拟币商品抽奖
			else if (2 == lotteryType.longValue())
			{

			}

		} else
		{
			// 如果没中奖，则插入没中奖信息
			logger.debug("+++noPrize:++userId:" + userId + "+++localCity:" + localCity + "+lotteryType:" + lotteryType + "++++ ");
			fullLotteryDao.addFullLottery(fullLottery);
		}

		return fullLottery;
	}

	@Override
	public int findTrxorderGoodsAndFullLottery(Long userId, String startDate, String endDate)
	{
		logger.info("++++++findTrxorderGoodsAndFullLottery+++++userId=" + userId + "++++++++++");
		double price = 0.0;
		Map<String, Object> trxorderGoodsPriceMap = fullLotteryDao.findTrxorderGoods(userId, startDate, endDate);
		if (null != trxorderGoodsPriceMap.get("price"))
		{
			price = Double.parseDouble(trxorderGoodsPriceMap.get("price").toString());
		}
		Map<String, Object> fullLotteryPriceMap = fullLotteryDao.findFullLotteryByUserId(userId);
		Long count = (Long) fullLotteryPriceMap.get("count");
		logger.info("++++++findTrxorderGoodsAndFullLottery+++++userId=" + userId + "++++price=" + price + "++++fullLotteryCount=" + count);
		int prizeCount = (int) price / payPriceSumBase;
		int maxCount = prizeCount - count.intValue();// 最多可抽奖次数
		if (maxCount < 0)
		{
			maxCount = 0;
		}
		return maxCount;
	}

	public Map<String, String> processCreateTrxOrder(Long userId, Long goodsId) throws Exception
	{
		logger.info("++++++processCreateTrxOrder+++++userId=" + userId + "++++goodsId=" + goodsId + "++++");
		Map<String, String> hessianMap = new HashMap<String, String>();
		// 增加用户手机号的校验
		Map<String, Object> userMap = trxSoaService.findMobileUserById(userId);

		String userTel = (String) userMap.get("mobile");// 用户tel
		Goods goods = goodsService.findById(goodsId);
		hessianMap.put("userId", userId.toString());
		hessianMap.put("goodsName", goods.getGoodsname());
		hessianMap.put("goodsId", goods.getGoodsId().toString());
		hessianMap.put("sourcePrice", String.valueOf(goods.getSourcePrice()));
		hessianMap.put("payPrice", "0.00");
		hessianMap.put("rebatePrice", String.valueOf(goods.getRebatePrice()));
		hessianMap.put("dividePrice", String.valueOf(goods.getDividePrice()));
		hessianMap.put("providerType", "");
		hessianMap.put("providerChannel", "");

		// 新增
		hessianMap.put("guestId", goods.getGuestId().toString());
		hessianMap.put("orderLoseAbsDate", goods.getOrderLoseAbsDate().toString());
		hessianMap.put("orderLoseDate", goods.getOrderLoseDate() + "");

		// 新增自动退款字段
		hessianMap.put("isRefund", String.valueOf(goods.getIsRefund()));
		hessianMap.put("isSendMerVou", String.valueOf(goods.getSendRules()));// 是否发送商家自有校验码
		hessianMap.put("isadvance", String.valueOf(goods.getIsadvance()));// 是否预付款
		hessianMap.put("payMp", "0" + "-" + userTel + "-" + goodsId + "-" + userId);// 切记不能用竖杠
		// shopids是商品id删除购物车信息用
		hessianMap.put("trxType", "FULL_LOTTERY");// 0元抽奖交易
		// 新的名字----------------------------------------------
		hessianMap.put("prizeId", "2");// 对应beiker_trx_rule表主键
		Map<String, String> rspMap = trxHessianServiceGateWay.createTrxOrder(hessianMap);

		logger.info("++++++processCreateTrxOrder+++++userId=" + userId + "++++goodsId=" + goodsId + "++++" + "++++++rspMap=" + rspMap.get("RSPCODE"));
		return rspMap;
	}

	@Override
	public long getFullLotteryTotal()
	{

		long lotteryTotal = fullLotteryDao.getFullLotteryTotal();

		return lotteryTotal;
	}

	@Override
	public List<Goods> findLotteryGoodsList(String localCityName)
	{
		if ("".equals(localCityName))
		{
			localCityName = "beijing";
		}

		StringBuilder goodsIdStr = new StringBuilder("");
		StringBuilder goodsIdStrForProbability = new StringBuilder("");
		String lotteryInfo = LotteryConstant.lotteryFullMap.get(localCityName);
		String[] goodsIdLottery = lotteryInfo.split(";");

		for (String goods : goodsIdLottery)
		{
			String goodsId = goods.split(":")[0];
			String goodsItem = goods.split(":")[1];
			int startNum = Integer.parseInt(goodsItem.split("-")[0]);
			if (startNum >= 0 && startNum <= 99)
			{
				goodsIdStrForProbability.append(goodsId).append(",");
			}
			goodsIdStr.append(goodsId).append(",");
		}

		// 查询未下线的商品, 在线商品全是概率为0的, 不展示

		goodsIdStr.deleteCharAt(goodsIdStr.length() - 1);

		List<Goods> goodsIdList = fullLotteryDao.findLotteryGoodsList(goodsIdStr.toString());

		// 标记：是否存在概率>0的在线商品
		boolean flag = false;

		for (Goods goods : goodsIdList)
		{
			String goodsId = String.valueOf(goods.getGoodsId());
			if (goodsIdStrForProbability.indexOf(goodsId) != -1)
			{
				flag = true;
				break; // 存在概率>0的在线商品
			}
		}
		if (!flag)
		{
			goodsIdList = new ArrayList<Goods>();
		}

		return goodsIdList;
	}

	/**
	 * 如果下线商品被抽中，就在剩余商品里找第一个概率不为0的， 且在线的商品， 如果没找到： 提示奖品已被抽完
	 */
	@Override
	public Long getGoodsIdForLottery(String localCityName, Long goodsId)
	{

		Long returnGoodsId = null;

		// 检查传入的goodsId商品是否下线
		Map<String, Object> goodsMap = trxSoaService.getMaxCountAndIsAvbByIdInMem(goodsId);
		String isavaliable = goodsMap.get("isavaliable").toString();
		if ("1".equals(isavaliable))
		{
			return goodsId;

		} else
		{
			// 下线则重新抽取
			List<Goods> fullGoodsList = findLotteryGoodsList(localCityName);
			int onlineGoodsSize = fullGoodsList.size();
			if (0 == onlineGoodsSize)
			{
				return null;
			} else
			{
				String lotteryInfo = LotteryConstant.lotteryFullMap.get(localCityName);
				String[] goodsIdLottery = lotteryInfo.split(";");
				// 抽取未下线，并且概率>0的商品
				int random = (int) (Math.random() * 100);
				for (String goods : goodsIdLottery)
				{
					String goodsIdStr = goods.split(":")[0];
					String goodsItem = goods.split(":")[1];
					int startNum = Integer.parseInt(goodsItem.split("-")[0]);
					int endNum = Integer.parseInt(goodsItem.split("-")[1]);
					if (startNum <= random && random <= endNum)
					{
						Map<String, Object> returnMap = trxSoaService.getMaxCountAndIsAvbByIdInMem(Long.parseLong(goodsIdStr));
						String iable = returnMap.get("isavaliable").toString();
						if ("1".equals(iable))
						{
							returnGoodsId = Long.parseLong(goodsIdStr);
							break;
						}
					}
				}
			}
		}

		return returnGoodsId;
	}

	@Override
	public String getGoodsNameForLottery(Long goodsId)
	{

		List<Long> goodsIdList = new ArrayList<Long>();
		goodsIdList.add(goodsId);
		Map<Long, String> goodsMap = trxSoaService.findGoodsTitle(goodsIdList);
		String goodsName = goodsMap.get(goodsId);
		goodsName = StringUtils.getLength(goodsName, 10);

		return goodsName;
	}

	@Override
	public int getFlagForLotteryButton(int prizeSize)
	{
		int flag = 0;
		Date beginDate = DateUtils.strToDate(LotteryConstant.fullBeginDate, "yyyy-MM-dd HH:mm:ss");
		Date endDate = DateUtils.strToDate(LotteryConstant.fullEndDate, "yyyy-MM-dd HH:mm:ss");

		if (!DateUtils.loseDate(beginDate))
		{
			flag = 1; // 当前时间在活动开始前 ,显示活动未开始图片
		}
		if (DateUtils.loseDate(endDate))
		{
			flag = 2; // 当前时间在活动结束后,显示活动已结束图片
		}
		if (DateUtils.betweenBeginAndEnd(new Date(), beginDate, endDate))
		{
			flag = 3; // 在活动时间范围内 , 显示抽奖图片
		}
		if (0 == prizeSize)
		{
			flag = 2; // 所有奖品都下线或者只剩下概率=0的商品时，显示奖品已抽完
		}

		return flag;
	}

	@Override
	public long getNowEndGapTimeForLottery(int prizeSize)
	{

		Date endDate = DateUtils.strToDate(LotteryConstant.fullEndDate, "yyyy-MM-dd HH:mm:ss");
		long nowEndGapTime = (endDate.getTime() - new Date().getTime()) / 1000;
		if (nowEndGapTime < 0)
		{
			nowEndGapTime = 0;
		}
		if (0 == prizeSize)
		{
			nowEndGapTime = 0; // 所有奖品都下线，显示活动已结束图片
		}

		return nowEndGapTime;
	}

}
