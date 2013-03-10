package com.beike.service.impl.lottery;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.lottery.LotteryDaoNew;
import com.beike.entity.lottery.LotteryInfoNew;
import com.beike.entity.lottery.LotteryTicket;
import com.beike.entity.lottery.PrizeInfoNew;
import com.beike.service.lottery.LotteryServiceNew;

@Service("lotteryNewService")
public class LotteryServiceNewImpl implements LotteryServiceNew {

	@Autowired
	private LotteryDaoNew lotteryDaoNew;

	@Override
	public LotteryInfoNew getLotteryInfoNew(String newprize_id) {
		Integer winnersNum=lotteryDaoNew.getWinnersNumber(newprize_id);
		LotteryInfoNew lotteryInfoNew=null;
		try {
			lotteryInfoNew = lotteryDaoNew.getLotteryInfoNew(newprize_id);
			lotteryInfoNew.setWinners(winnersNum);
			changeLotteryInfoJointNumber(newprize_id, lotteryInfoNew);
		} catch (Exception e) {
		}
		return lotteryInfoNew;
	}
	
	
	
	private void changeLotteryInfoJointNumber(String newprize_id,
			LotteryInfoNew lotteryInfoNew) {
		List<PrizeInfoNew> listPrize=lotteryDaoNew.getPrizeInfoNew(newprize_id);
		PrizeInfoNew pin=new PrizeInfoNew();
		pin.setStartprize_id(lotteryInfoNew.getStartprize_id());
		
		int i=listPrize.indexOf(pin);
		if(i!=-1){
			//假如不是 第一次开奖信息
			if(i>0&&i<listPrize.size()){
				//其余 判断前一次是否已经开奖 假如未开奖 显示人数应该为前一次开奖人数加当前开奖人数
				//假如前一次开奖时间已经过了
				PrizeInfoNew prePrizeInfo=listPrize.get(i-1);
				Timestamp ts=prePrizeInfo.getStartprize_seedtime();
				if(ts.before(new Date())&&"1".equals(prePrizeInfo.getStrartprize_status())){
					int pj=Integer.parseInt(prePrizeInfo.getStartprize_jointnumber()+"");
					lotteryInfoNew.setTotal(lotteryInfoNew.getTotal()+pj);
				}
				
			}
		}
	}



	public void changeLotteryInfoJointNumber(String newprize_id,PrizeInfoNew lotteryInfoNew){
		List<PrizeInfoNew> listPrize=lotteryDaoNew.getPrizeInfoNew(newprize_id);
		PrizeInfoNew pin=new PrizeInfoNew();
		pin.setStartprize_id(lotteryInfoNew.getStartprize_id());
		
		int i=listPrize.indexOf(pin);
		if(i!=-1){
			//假如不是 第一次开奖信息
			if(i>0&&i<listPrize.size()){
				//其余 判断前一次是否已经开奖 假如未开奖 显示人数应该为前一次开奖人数加当前开奖人数
				//假如前一次开奖时间已经过了
				PrizeInfoNew prePrizeInfo=listPrize.get(i-1);
				Timestamp ts=prePrizeInfo.getStartprize_seedtime();
				if(ts.before(new Date())&&"1".equals(prePrizeInfo.getStrartprize_status())){
					int pj=Integer.parseInt(prePrizeInfo.getStartprize_jointnumber()+"");
					lotteryInfoNew.setStartprize_jointnumber(lotteryInfoNew.getStartprize_jointnumber()+pj);
				}
				
			}
		}
	}

	@Override
	public Long isJoined(String newprize_id, String user_id) {
		return lotteryDaoNew.isJoined(newprize_id, user_id);
	}

	@Override
	public List<Long> getRecommendGoodsID(String area_id) {
		List listMap=lotteryDaoNew.getRecommendGoodsID(area_id);
		List<Long> listLong=new ArrayList<Long>();
		if(listMap!=null&&listMap.size()>0){
			for(int i=0;i<listMap.size();i++){
				Map map=(Map) listMap.get(i);
				Long goodId=(Long) map.get("goodid");
				listLong.add(goodId);
			}
		}
		
		return listLong;
	}

	@Override
	public List<LotteryTicket> getLotteryTicketInfo(String newprize_id,
			String user_id) {
		return lotteryDaoNew.getLotteryTicketInfo(newprize_id, user_id);
	}

	@Override
	public List<PrizeInfoNew> getPrizeInfoNew(String prize_id) {
		List<PrizeInfoNew> listPrizeInfo=lotteryDaoNew.getPrizeInfoNew(prize_id);
		if(listPrizeInfo!=null&&listPrizeInfo.size()>0){
			for (PrizeInfoNew prizeInfoNew : listPrizeInfo) {
				changeLotteryInfoJointNumber(prize_id, prizeInfoNew);
			}
		}
		return listPrizeInfo;
	}

	@Override
	public boolean saveLotteryTicketInfo(String newprize_id,String numbersource,String getlorrystatus,String user_id, String startprize_id) {
		lotteryDaoNew.updateParticipants(startprize_id);
		Long lastId=lotteryDaoNew.saveLotteryTicketInfo(newprize_id, numbersource, getlorrystatus, user_id);
		if(lastId!=null)return true;
		return false;
	}

	@Override
	public Timestamp getRemainderInviteTime(String userid, String prize_id) {
		return lotteryDaoNew.getRemainderInviteTime(userid, prize_id);
	}

	@Override
	public int getLotteryStatus(String prizeid) {
		return lotteryDaoNew.getLotteryStatus(prizeid);
	}

	@Override
	public int getLotteryInfoStatus(String prizeid) {
		return lotteryDaoNew.getLotteryInfoStatus(prizeid);
	}

	@Override
	public List<LotteryTicket> getLotteryTicketInfo(Long userId) {
		return lotteryDaoNew.getLotteryTicketList(userId);
		
	}

	@Override
	public LotteryInfoNew getFinalLotteryResult(String prizeid) {
		Integer winnersNum=lotteryDaoNew.getWinnersNumber(prizeid);
		LotteryInfoNew lotteryInfoNew=lotteryDaoNew.getFinalLotteryResult(prizeid);
		lotteryInfoNew.setWinners(winnersNum);
		return lotteryInfoNew;
	}
}
