/**
 * 
 */
package com.beike.service.impl.lottery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.lottery.LotteryDao;
import com.beike.entity.lottery.LotteryInfo;
import com.beike.entity.lottery.PrizeGoods;
import com.beike.entity.lottery.PrizeInfo;
import com.beike.service.common.EmailService;
import com.beike.service.lottery.LotteryService;
import com.beike.util.PropertiesReader;
import com.beike.util.StringUtils;

/**
 * @author janwen
 * 
 */
@Service("lotteryService")
public class LotteryServiceImpl implements LotteryService {

	@Autowired
	private LotteryDao lotteryDao;
	@Resource(name = "emailService")
	private EmailService emailService;

	@Override
	public LotteryInfo getLotteryInfo(String prizeid) {
		return lotteryDao.getLotteryInfo(prizeid);
	}

	@Override
	public PrizeInfo getPrizeInfo(String prizeid) {
		return lotteryDao.getPrizeInfo(prizeid);
	}

	@Override
	public LotteryInfo isJoined(String prizeid, String userid) {
		return lotteryDao.isJoined(prizeid, userid);
	}

	@Override
	public LotteryInfo saveLotteryInfo(String prizeid, String userid) {
		return lotteryDao.saveLotteryInfo(prizeid, userid);
	}

	@Override
	public List<String> getLotteryWinnersNo(String prizeid) {
		return lotteryDao.getLotteryWinnersNo(prizeid);
	}

	/**
	 * 补充方法：当单个用户参与抽奖时, 调用该接口向参与用户发送邮件（奖号） Add by zx.liu
	 * 
	 * @param prizeId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Override
	public void participantEmail(Long prizeId, Long userId) throws Exception {
		// 根据抽奖ID获取抽奖信息
		PrizeGoods prizeGoods = (PrizeGoods) lotteryDao
				.findPrizeGoodsById(prizeId);
		// 根据抽奖活动ID 和用户ID 来获取用户的奖号
		Map<String, Object> map = lotteryDao.findLotteryUserById(prizeId,
				userId);
		// 根据用户ID 获取 Lottery 的User 的Email 信息
		// String userEmail = lotteryDao.findUserEmailById(userId);

		if (null != map) {
			/**
			 * 邮件模板路径前缀
			 */
			String url = PropertiesReader.getValue("lottery", "host");
			// 获取推荐商品的相关信息, 用于邮件中商品推荐位的显示
			Map<String, Object> feature1 = (Map<String, Object>) lotteryDao
					.findGoodsInfo(prizeGoods.getFeaturedId1());
			Map<String, Object> feature2 = (Map<String, Object>) lotteryDao
					.findGoodsInfo(prizeGoods.getFeaturedId2());
			Map<String, Object> feature3 = (Map<String, Object>) lotteryDao
					.findGoodsInfo(prizeGoods.getFeaturedId3());
			// 抽奖商品的信息
			Map<String, Object> prizeGoodsInfo = (Map<String, Object>) lotteryDao
					.findGoodsInfo(prizeGoods.getGoodsId());
			// 用于格式化日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// 组建填充邮件模板的字符串数组
			String[] emailParams = new String[] {
					url,
					url,
					url,
					url,
					url,
					url
							+ getURLGoods(Long
									.toString(prizeGoods.getGoodsId())
									.toString()), // {5}
					url + "jsp/uploadimages/" + prizeGoodsInfo.get("logo4"), // {6}
					sdf.format(prizeGoods.getEndTime()), // 开奖时间{7}
					prizeGoods.getWinners().toString(), // 预订中奖人数{8}
					url
							+ getURLGoods(
									Long.toString(prizeGoods.getGoodsId()))
									.toString(), // {9}
					prizeGoodsInfo.get("goodsname").toString(), // 抽奖商品名称 {10}
					map.get("winnumber").toString(), // 奖号{11}
					prizeGoods.getSeedDescription(), // {12}
					url + getURLPrize(prizeGoods.getPrizeId().toString()), // {13}
					// 推荐商品01
					url + getURLGoods(prizeGoods.getFeaturedId1().toString()), // {14}
					feature1.get("goodsname").toString(),
					url + getURLGoods(prizeGoods.getFeaturedId1().toString()),
					url + "jsp/uploadimages/" + feature1.get("logo4"),
					feature1.get("currentPrice").toString(),
					feature1.get("rebatePrice").toString(),
					url + getURLGoods(prizeGoods.getFeaturedId1().toString()),
					url, // {21}
					// 推荐商品02
					url + getURLGoods(prizeGoods.getFeaturedId2().toString()), // {22}
					feature2.get("goodsname").toString(),
					url + getURLGoods(prizeGoods.getFeaturedId2().toString()),
					url + "jsp/uploadimages/" + feature2.get("logo4"),
					feature2.get("currentPrice").toString(),
					feature2.get("rebatePrice").toString(),
					url + getURLGoods(prizeGoods.getFeaturedId2().toString()),
					url, // {29}
					// 推荐商品03
					url + getURLGoods(prizeGoods.getFeaturedId3().toString()), // {30}
					feature3.get("goodsname").toString(),
					url + getURLGoods(prizeGoods.getFeaturedId3().toString()),
					url + "jsp/uploadimages/" + feature3.get("logo4"),
					feature3.get("currentPrice").toString(),
					feature3.get("rebatePrice").toString(),
					url + getURLGoods(prizeGoods.getFeaturedId3().toString()),
					url // {37}

			}; // End String[] emailParams
			/**
			 * 以下方法实现：为抽奖参与者发送邮件的功能！
			 */
			emailService.send(null, null, null, null, null, "千品网0元抽奖",
					new String[] { map.get("email").toString() }, null, null,
					new Date(), emailParams, "LOTTERY_PARTICIPANT");
		}
	}

	/**
	 * 生成抽奖商品伪静态或动态链接
	 * 
	 * @param prizeid
	 * @return
	 */
	public static String getURLPrize(String prizeid) {
		if (StringUtils.validNull(prizeid)) {
			String static_url = PropertiesReader.getValue("project",
					"STATIC_URL");
			if ("false".equals(static_url)) {
				return "lottery/lotteryAction.do?command=getAwardResult&prizeid="
						+ prizeid;
			} else {
				return "lottery/" + prizeid + ".html";
			}
		}
		return "";
	}

	/**
	 * 生成商品伪静态或动态链接
	 * 
	 * @param goodsid
	 * @return
	 */
	public static String getURLGoods(String goodsid) {
		if (StringUtils.validNull(goodsid)) {
			String static_url = PropertiesReader.getValue("project",
					"STATIC_URL");
			if ("false".equals(static_url)) {
				return "goods/showGoodDetail.do?goodId=" + goodsid;
			} else {
				return "goods/" + goodsid + ".html";
			}
		}
		return "";
	}

	@Override
	public List<Map<String, Object>> getRecommendGoods(Long prizeId)
			throws Exception {
		// 根据抽奖ID获取抽奖信息
		PrizeGoods prizeGoods = (PrizeGoods) lotteryDao
				.findPrizeGoodsById(prizeId);
		// 获取推荐商品的相关信息, 用于邮件中商品推荐位的显示
		Map<String, Object> feature1 = (Map<String, Object>) lotteryDao
				.findGoodsInfo(prizeGoods.getFeaturedId1());
		feature1.put("goodsurl", getURLGoods(Long.toString(prizeGoods.getFeaturedId1())));
		Map<String, Object> feature2 = (Map<String, Object>) lotteryDao
				.findGoodsInfo(prizeGoods.getFeaturedId2());
		feature2.put("goodsurl", getURLGoods(Long.toString(prizeGoods.getFeaturedId2())));
		Map<String, Object> feature3 = (Map<String, Object>) lotteryDao
				.findGoodsInfo(prizeGoods.getFeaturedId3());
		feature3.put("goodsurl", getURLGoods(Long.toString(prizeGoods.getFeaturedId3())));
		Map<String, Object> feature4 = (Map<String, Object>) lotteryDao
				.findGoodsInfo(prizeGoods.getFeaturedId4());
		feature4.put("goodsurl", getURLGoods(Long.toString(prizeGoods.getFeaturedId4())));
		List<Map<String, Object>> recommendGoods = new ArrayList<Map<String, Object>>();
		recommendGoods.add(feature1);
		recommendGoods.add(feature2);
		recommendGoods.add(feature3);
		recommendGoods.add(feature4);
		return recommendGoods;
	}

}
