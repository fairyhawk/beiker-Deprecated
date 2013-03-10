package com.beike.service.comment.impl;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.Account;
import com.beike.common.entity.vm.SubAccount;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.dao.comment.CommentDao;
import com.beike.dao.goods.GoodsDao;
import com.beike.dao.trx.AccountDao;
import com.beike.dao.user.UserExpandDao;
import com.beike.dao.vm.SubAccountDao;
import com.beike.entity.merchant.BranchProfile;
import com.beike.form.MerchantForm;
import com.beike.form.OrderEvaluationForm;
import com.beike.page.Pager;
import com.beike.service.comment.CommentService;
import com.beike.service.sensitiveword.SensitivewordFilterService;
import com.beike.util.Amount;
import com.beike.util.DateUtils;
import com.beike.util.NullDigitalCheck;
import com.beike.util.StaticDomain;
import com.beike.util.StringUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**
 * Action直接调用该Service,其它事务Service包含到该Service
 * @author janwen
 *
 */
@Service("commentService")
public class CommentServiceImpl implements CommentService{
	@Autowired
	private SubAccountDao subAccountDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private CommentDao commentDao;

	@Autowired
	private GoodsDao goodsDao;
	
	@Autowired
	@Qualifier("trxorderGoodsService")
	private TrxorderGoodsService trxorderGoodsService;
	
	@Autowired
	private UserExpandDao userExpandDao;

   private static final String TOP_SALED_GOODS_KEY = "COMMENT_RECOMMEND_GOODS";
   
   
   private static MemCacheService cacheService = MemCacheServiceImpl.getInstance();
	
	/* (non-Javadoc)
	 * @see com.beike.service.comment.CommentService#getRemindAccountBalance(java.lang.Long, java.lang.String)
	 */
	@Override
	public double getRemindAccountBalance(Long userId, String dayCount) {
		double remindBalance = 0.0;
		
		// 获取 vc 类型的 account 对象
		Account account = accountDao.findByUserIdAndType(userId, AccountType.VC);
		List<SubAccount> remindList = new ArrayList<SubAccount>();
		if (account != null && account.getBalance() > 0) {
			long accountId = account.getId();
			// 查询到期的余额
			String currentDate = DateUtils.getStringDate();
			Date beginDate = DateUtils.toDate(currentDate,"yyyy-MM-dd HH:mm:ss");
			Date currentDateFormat = DateUtils.toDate(currentDate, "yyyy-MM-dd");// //当前时间（精确到day）
			String endDateStr = DateUtils.getNextDay(currentDate, dayCount);
			Date endDate = DateUtils.toDate(endDateStr, "yyyy-MM-dd HH:mm:ss");
			Long subAccountSuffix = StringUtils.getDeliveryIdBase(accountId);
			remindList = subAccountDao.findRemindListByActId(accountId,
					subAccountSuffix + "", beginDate, endDate);
			if (null != remindList && remindList.size() > 0) {
				// remindList元素中过期时间处理
				int index = remindList.size() - 1;
				for (int i = index; i >= 0; i--) {
					SubAccount subAccount = remindList.get(i);
					Date loseDate = subAccount.getLoseDate();
					// 如果过期时间点为时分秒为00:00:00，则提示用户时，过期时间前移一天
					String loseDateStr = DateUtils.dateToStrLong(loseDate);
					loseDate = loseDateStr.indexOf("00:00:00") > 0 ? DateUtils
							.toDate(DateUtils.getNextDay(loseDateStr, "-1"),
									"yyyy-MM-dd HH:mm:ss") : loseDate;
					Date loseDateFormat = DateUtils.toDate(DateUtils.toString(
							loseDate, "yyyy-MM-dd"), "yyyy-MM-dd");// 过期时间（精确到day）
					subAccount.setLoseDate(loseDateFormat);
					if (loseDateFormat.before(currentDateFormat)) {
						// 如果过期时间在当前 时间 之前则删除元素，防止余额过期时报乐观锁过期顺延造成提示时过期时间在当前时间之前
						remindList.remove(i);
					}
				}
				remindList = mergeList(remindList);
			}
		}

		if(remindList!=null && remindList.size()>0){
			for(SubAccount tmpAccount : remindList){
				remindBalance = remindBalance + tmpAccount.getBalance();
			}
		}
		return remindBalance;
	}
	
	/**
	 * List 部分属性相同的元素合并 .
	 * 
	 * @param list
	 */
	public List<SubAccount> mergeList(List<SubAccount> list) {

		LinkedHashMap<Date, SubAccount> map = new LinkedHashMap<Date, SubAccount>();
		for (SubAccount item : list) {
			Date loseDate = item.getLoseDate();
			if (map.containsKey(loseDate)) {
				item.setBalance(Amount.cutOff(Amount.add(map.get(loseDate)
						.getBalance(), item.getBalance()), 2));
			}
			map.put(loseDate, item);
		}
		list.clear();
		list.addAll(map.values());

		return list;
	}
	
	
	@Override
	public int getEvaluateMerchantCount(Long userId, Long merchantId,int score) {
		return commentDao.getEvaluateMerchantCountById(userId, merchantId,score);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getEvaluateMerchantID(Long userId, Long merchantId,Pager pager,int score) {
		if(pager == null){
			return null;
		}
		return commentDao.getEvaluateMerchantID(userId, merchantId, 
				pager.getStartRow(), pager.getPageSize(),score);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderEvaluationForm> getEvaluationInfoByIds(List<Long> idList) {
		List<OrderEvaluationForm> lstCommentForm = null;
		//评价信息
		List<Map<String,Object>> lstComment = commentDao.getEvaluationInfoByIds(idList);
		if(lstComment !=null && lstComment.size()>0){
			lstCommentForm = new ArrayList<OrderEvaluationForm>();
			//评价图片
			List<Map<String,Object>> photolist = commentDao.getEvaluatePhoto(idList);
			Map<Long,List<String>> photoMap = null;
			if(photolist != null && photolist.size()>0){
				photoMap = new HashMap<Long,List<String>>();
				for(Map<String,Object> tmpMap : photolist){
					Long evaid = (Long) tmpMap.get("evaluationid");
					String photourl = (String) tmpMap.get("photourl");
					List<String> tmpLst = photoMap.get(evaid);
					if(tmpLst == null){
						tmpLst = new ArrayList<String>();
					}
					tmpLst.add(photourl);
					photoMap.put(evaid, tmpLst);
				}
			}
			
			//评价对应的分店信息
			List<Map<String, Object>> namelist = commentDao.getEvaluateGoodNameAndMerName(idList);
			Map<Long,List<String[]>> nameMap = null;
			if(namelist != null && namelist.size()>0){
				nameMap = new HashMap<Long,List<String[]>>();
				for(Map<String, Object> tmpMap : namelist){
					Long evaid = (Long) tmpMap.get("evaluationid");
					String branchname = (String) tmpMap.get("merchantname");
					Long branchid = (Long) tmpMap.get("branchid");
					List<String[]> branchLst = nameMap.get(evaid);
					if(branchLst==null){
						branchLst = new ArrayList<String[]>();
					}
					branchLst.add(new String[]{branchname, String.valueOf(branchid)});
					nameMap.put(evaid,branchLst);
				}
			}
			
			StringBuilder bufCommentIds = new StringBuilder();
			//生成评价Form
			for(Map<String,Object> commentMap : lstComment){
				OrderEvaluationForm  commentForm = new OrderEvaluationForm();
				Long commentId = (Long)commentMap.get("id");
				Long goodsid = (Long)commentMap.get("goodsid");
				commentForm.setId(commentId);
				commentForm.setUserid((Long)commentMap.get("userid"));
				//评价内容敏感词在此过滤
				//commentForm.setEvaluation(SensitivewordFilterService.getFilterResult((String)commentMap.get("evaluation")));
				commentForm.setEvaluation((String)commentMap.get("evaluation"));
				commentForm.setOrdercount((Integer)commentMap.get("ordercount"));
				commentForm.setScore((Integer)commentMap.get("score"));			
				commentForm.setAddtime((Timestamp)commentMap.get("addtime"));
				commentForm.setStatus((Integer)commentMap.get("status"));
				
				commentForm.setGoodsid(goodsid);
				commentForm.setGoodsName((String)commentMap.get("goodsname"));
				commentForm.setIsavaliable((Integer)commentMap.get("isavaliable"));
				commentForm.setGoodsTitle((String)commentMap.get("goods_title"));
				
				Long trxOrderId = (Long)commentMap.get("trxorderid");
				int byCount = commentDao.getOrderCountByTrxId(trxOrderId, goodsid);
				commentForm.setBuyCount(byCount);
				//评价附图
				if(photoMap!=null && !photoMap.isEmpty()){
					commentForm.setPhotoLst(photoMap.get(commentId));
				}
				//评价分店
				if(nameMap!=null && !nameMap.isEmpty()){
					commentForm.setBranchLst(nameMap.get(commentId));
				}
				
				bufCommentIds.append((Long)commentMap.get("userid")).append(",");
				lstCommentForm.add(commentForm);
			}
			
			//评价用户
			if(bufCommentIds.toString().endsWith(",")){
				List<Map<String,Object>> lstUserInfo = userExpandDao.getUserInfoByIds(bufCommentIds.substring(0, bufCommentIds.length()-1));
				if(lstUserInfo!=null && lstUserInfo.size()>0){
					Map<Long,String[]> userMap = new HashMap<Long,String[]>();
					for(Map<String,Object> tmpMap : lstUserInfo){
						Long userId = (Long)tmpMap.get("userid");
						//头像地址
						String headIcon = (String)tmpMap.get("avatar");
						if(org.apache.commons.lang.StringUtils.isNotEmpty(headIcon)){
							if(!headIcon.startsWith("http://")){
								headIcon = StaticDomain.getDomain("") + "/" + headIcon;
							}
						}else{
							headIcon = "";
						}
						//昵称
						String nickname = (String)tmpMap.get("nickname");
						if(org.apache.commons.lang.StringUtils.isEmpty(nickname)){
							nickname = (String)tmpMap.get("email");
							if(org.apache.commons.lang.StringUtils.isNotEmpty(nickname)){
								nickname = StringUtils.handleEmail(nickname);
							}
						}
						userMap.put(userId, new String[]{nickname,headIcon});
					}
					//用户昵称、头像
					if(lstCommentForm!=null && lstCommentForm.size()>0){
						for(OrderEvaluationForm  commentForm : lstCommentForm){
							String[] aryUserInfo = userMap.get(commentForm.getUserid());
							if(aryUserInfo!=null){
								commentForm.setNickname(aryUserInfo[0]);
								commentForm.setAvatar(aryUserInfo[1]);
							}
						}
					}
				}
			}
		}
		return lstCommentForm;
	}
	@Override
	public boolean addComment(int isdefault,Long id,boolean batch,int score,String comment,Long merchantID,Long userid,Long goodsid,Long trxorderid,List<String> photourl,int well_count,int satisfy_count,int poor_count) throws Exception{
		//过滤HTML标签
		comment = StringUtils.getTxtWithoutHTMLElement(comment);
		//订单状态更新需要的数据
		List list = commentDao.getTrxOrderInfo(batch, goodsid, id, trxorderid);
		if(list == null || list.size()<1){
			throw new Exception("订单已经评论" + trxorderid + ":" + goodsid +  ":" + id);
		}
		Set<Long> idset = new HashSet<Long>();
		Map<Long,Integer> branchCountMap = new HashMap<Long, Integer>();
	    for(int i=0;i<list.size();i++){
	    	Map map = (Map) list.get(i);
	    	idset.add((Long)map.get("id"));
	    	if(branchCountMap.get(map.get("sub_guest_id")) == null){
	    		branchCountMap.put((Long)map.get("sub_guest_id"), 1);
	    	}else{
	    		branchCountMap.put((Long)map.get("sub_guest_id"), branchCountMap.get((Long)map.get("sub_guest_id"))+1);
	    	}
	    }
		Long evaluationid = commentDao.addComment(isdefault,score, comment, merchantID, userid, goodsid, trxorderid, idset.size());
		try {
			if(photourl !=null && photourl.size()>0){
				commentDao.addCommentPhoto(photourl, evaluationid);
			}
			for(Map.Entry<Long, Integer> entry:branchCountMap.entrySet()){
				commentDao.updateGoodsProfile(goodsid,well_count*entry.getValue(), satisfy_count*entry.getValue(), poor_count*entry.getValue());
				if(commentDao.updateMerchanProfile(merchantID, new Long(well_count*NullDigitalCheck.rate_score_map.get(NullDigitalCheck.RATE_BEST_ENUM)+satisfy_count*NullDigitalCheck.rate_score_map.get(NullDigitalCheck.RATE_BETTER_ENUM)+poor_count*NullDigitalCheck.rate_score_map.get(NullDigitalCheck.RATE_BAD_ENUM)).intValue()*entry.getValue(), well_count*entry.getValue(), satisfy_count*entry.getValue(), poor_count*entry.getValue())>0){
					//店铺id为0只更新beiker_branch_evaluation,不更新beiker_branch_profile
					if(entry.getKey() !=0){
						if(commentDao.updateBranchEvaluation(entry.getKey(), evaluationid, entry.getValue())>0){
							commentDao.updateBranchProfile(merchantID, entry.getKey(),well_count*entry.getValue(), satisfy_count*entry.getValue(), poor_count*entry.getValue());
						}
					}
				}
			}
			trxorderGoodsService.commentByTgId(idset,evaluationid);
			return true;
		} catch (StaleObjectStateException e) {
			e.printStackTrace();
			throw new Exception("评论保存失败,数据库异常");
		}
	}
	private static final Logger logger =Logger.getLogger(CommentServiceImpl.class);

	@Override
	public List<Long> getRecGoodsid(Long goodsid) {
		Long area_id = commentDao.getAreaIDByGoodsid(goodsid);
		List<Long> goodsidList = commentDao.getRecommendGoodsidBySec(goodsid);
		if(goodsidList != null && goodsidList.size()<4){
			goodsidList.addAll(commentDao.getRecommendGoodsidByFir(goodsid,goodsidList, 4-goodsidList.size(),area_id));
		}
		
		logger.info("getRecGoodsid goodsids " + goodsidList);
		//推荐商品还小于4推荐当前城市24小时销量最好的商品
		if(goodsidList == null || goodsidList.size()<4){
			List<Long> topsaledgoodsid = getTopSaledGoodsid(area_id, goodsidList);
			if(goodsidList != null){
				for(int i=0;i<4-goodsidList.size();i++){
					goodsidList.add(topsaledgoodsid.get(i));
				}
			}else{
				for(int i=0;i<4;i++){
					goodsidList.add(topsaledgoodsid.get(i));
				}
			}
		}
		logger.info("return recommend goodsids " + goodsidList);
		return goodsidList;
	}

	@Override
	public int getEvaluateBrandCount(Long userId, Long brandid,int score) {
		return commentDao.getEvaluateBrandCountById(userId, brandid, score);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getEvaluateBrandID(Long userId, Long brandid, Pager pager, int score) {
		if(pager == null){
			return null;
		}
		return commentDao.getEvaluateBrandID(userId, brandid,
				pager.getStartRow(),pager.getPageSize(),score);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BranchProfile getAllEvaluateForBrand(Long brandid) {
		
		List li = commentDao.getAllEvaluateForBrand(brandid);
		
		if(li == null)
			return null;
		
		Map mx = (Map) li.get(0);
		Long well = (Long)mx.get("well_count");
		Long sati = (Long)mx.get("satisfy_count");
		Long poor = (Long)mx.get("poor_count");
		
		Long evalCount = well+sati+poor;
		
		BranchProfile profile = new BranchProfile();
		
		if(evalCount>0){
			profile.setSatisfyRate(new BigDecimal(
					(float) (well + sati) * 100 / evalCount).setScale(0,RoundingMode.HALF_UP).intValue());
			
			profile.setPartWellRate(new BigDecimal((float) well * 100 / evalCount).setScale(0,RoundingMode.HALF_UP).intValue());
			
			profile.setPartSatisfyRate(new BigDecimal((float) sati * 100 / evalCount).setScale(0,RoundingMode.HALF_UP).intValue());
					
			profile.setPartPoorRate(new BigDecimal((float) poor * 100 / evalCount).setScale(0,RoundingMode.HALF_UP).intValue());
		}else{
			profile.setSatisfyRate(0);
			profile.setPartWellRate(0);
			profile.setPartSatisfyRate(0);
			profile.setPartPoorRate(0);
		}
		profile.setBranchId(brandid);
		profile.setWellCount(well);
		profile.setSatisfyCount(sati);
		profile.setPoorCount(poor);
		
		return profile;
	}
	
	/**
	 * 查询expiredDay天前消费为评价的订单
	 * @param expiredDay
	 * @return
	 */
	public List<Map<String,Object>> queryExpiredNoComment(int expiredDay){
		return commentDao.queryExpiredNoComment(expiredDay);
	}

	@Override
	public Map<String, Object> gotoCommentPage(boolean batch,
			Long trx_order_id, Long id, Long goodsid) {
		return commentDao.gotoCommentPage(batch, trx_order_id, id, goodsid);
	}

	@Override
	public boolean isvalid(Long trx_order_id, Long goodsid, Long userid) {
		if(commentDao.findTrxOrder(trx_order_id, goodsid)>0 && commentDao.findUserid(trx_order_id, userid)>0){
			return true;
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<MerchantForm> getMerchantProfile(Long merchantid) {
		
		List catloglist = commentDao.getCatlogGoodByBrandid(merchantid);
		
		if(catloglist == null || catloglist.size() == 0)
				return null;
		
		Set<String>  catlog = new LinkedHashSet<String>();
		
		//存一级、二级分类
		Map<String,Set<String>> catlogMap = new LinkedHashMap<String,Set<String>>();
		
		for(Object ox : catloglist){
			Map mx = (Map) ox;
			String tagname = (String) mx.get("tagname");
			String tagextname = (String) mx.get("tagextname");
			
			Set<String> secondCatlog = catlogMap.get(tagname);
			if(secondCatlog == null || secondCatlog.size()==0)
				secondCatlog = new LinkedHashSet<String>();
			
			secondCatlog.add(tagextname);
			catlogMap.put(tagname, secondCatlog);
		}
		
		if(catlogMap == null || catlogMap.size() ==0 || catlogMap.isEmpty())
				return null;
		
		Set<Entry<String, Set<String>>> catalogKey = catlogMap.entrySet();
		for(Entry<String, Set<String>> entry : catalogKey){
			catlog.add(entry.getKey());
			catlog.add(entry.getKey());
		}
		
		List li = commentDao.getAllEvaluationForMerchant(merchantid);
		if(li == null)
			return null;
				
		Map mx = (Map) li.get(0);
		Integer well  = (Integer)mx.get("mc_well_count");
		Integer sati  = (Integer)mx.get("mc_satisfy_count");
		Integer poor  = (Integer)mx.get("mc_poor_count");
		Integer score = (Integer)mx.get("mc_score");
		
		Long wellCount = Long.parseLong(well.toString());
		Long satiCount = Long.parseLong(sati.toString());
		Long poorCount = Long.parseLong(poor.toString());
		
		//商家信息
		List<MerchantForm> formlist = new ArrayList<MerchantForm>();	
		MerchantForm  form = new MerchantForm();
		form.setMainBusiness(catlog);
		form.setWellCount(wellCount);
		form.setSatisfyCount(satiCount);
		form.setPoorCount(poorCount);
		form.setMcScore(Long.parseLong(score.toString()));
		form.calculateScore();
		formlist.add(form);
		return formlist;
	}

	@Override
	public int getEvaluateGoodCount(Long userId,Long goodId, int score) {
		return commentDao.getEvaluateGoodCountById(userId, goodId, score);
	}

	@Override
	public Integer getEvaluateGoodCount(Long goodId) {
		// TODO Auto-generated method stub
		return commentDao.getEvaluateGoodCountById(goodId);
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getEvaluateGoodID(Long userId, Long goodId,Pager pager,int score) {
		int startRow = pager.getStartRow();		
		return commentDao.getEvaluateGoodsId(userId, goodId, startRow, pager.getPageSize(), score);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getEvaluateGoodOtherID(Long merchantId) {
		List<Long> idlist = goodsDao.getGoodIdWithMerchant(merchantId);
		if(idlist == null || idlist.size() == 0){
			return  null;
		}else{
			return commentDao.getOtherEvaluateGoodsId(StringUtils.arrayToString(idlist.toArray(), ","));
		}
	}

	@Override
	public List<Long> getTopSaledGoodsid(Long area_id,List<Long> notin) {
		List<Long> top4goodsid = (List<Long>) cacheService.get(area_id + "_" + TOP_SALED_GOODS_KEY);
		if(top4goodsid == null){
			List<Long> availableGoodsid = commentDao.getGoodsidByAreaid(area_id, notin);
			top4goodsid = commentDao.getTopSalesGoodsID(availableGoodsid);
			cacheService.set(area_id + "_" + TOP_SALED_GOODS_KEY, top4goodsid);
		}
		return top4goodsid;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Integer> getAllEvaluationForGood(Long goodId) {
		List<Map<String, Object>> li = commentDao.getAllEvaluationForGoodById(goodId);
		Map<String,Integer> resuMap = new HashMap<String,Integer>();
		if(li == null || li.size() ==0){
			resuMap.put("well_count", 0);
			resuMap.put("satisfy_count", 0);
			resuMap.put("poor_count", 0);
		}else{
			Map<String, Object> mx = li.get(0);
			Long well = (Long)mx.get("well_count");
			Long sati = (Long)mx.get("satisfy_count");
			Long poor = (Long)mx.get("poor_count");
			resuMap.put("well_count",    well!=null?well.intValue():0);
			resuMap.put("satisfy_count", sati!=null?sati.intValue():0);
			resuMap.put("poor_count",    poor!=null?poor.intValue():0);
		}
		return resuMap;
	}
	public List<Long> getEvaluationIdByMerchantId(List<Long> merchantIdList){
		//很好满意的评价
		List<Map<String,Object>> goodList = commentDao.getEvaluationIdByMerchantId(merchantIdList,0,0);
		List<Long> resList = new ArrayList<Long>();
		if(null != goodList && goodList.size() > 0){
			int badCount = goodList.size()/20;
			List<Map<String,Object>> badList = null;
			if(badCount > 0){
				badList = commentDao.getEvaluationIdByMerchantId(merchantIdList, 2, badCount);
				if(null != badList){
					goodList.addAll(badList);
				}
			}			
			List<OrderEvaluationForm> formList = new ArrayList<OrderEvaluationForm>();
			for(Map<String,Object> map : goodList){
				OrderEvaluationForm form = new OrderEvaluationForm();
				form.setAddtime(Timestamp.valueOf(map.get("addtime").toString()));
				form.setId(Long.parseLong(map.get("id").toString()));
				formList.add(form);
			}
			Collections.sort(formList);
			for (OrderEvaluationForm orderEvaluationForm : formList) {
				resList.add(orderEvaluationForm.getId());
			}
		}
		return resList;
	}
}
