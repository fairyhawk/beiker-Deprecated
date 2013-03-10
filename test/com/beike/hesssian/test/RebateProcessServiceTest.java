package com.beike.hesssian.test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.biz.service.trx.daemon.AccountNotifyDaemon;
import com.beike.biz.service.trx.daemon.TrxOrderNotifyDaemon;

/**
 * @Title: RebateProcessServiceTest.java
 * @Package com.beike.demo.test
 * @Description: TODO
 * @date May 10, 2011 1:35:17 PM
 * @author wh.cheng
 * @version v1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml",
		"classpath:/springcontexttrx/trx-applicationContext.xml" })
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class })
public class RebateProcessServiceTest {

	// @Test
	// public void testRebateByTrxGoods() {
	//
	// Map<String, String> map = new HashMap<String, String>();
	// map.put("trxGoodsId", "95");
	// // map.put("goodsName", "123|1212|121");
	//
	// Map rspMap=trxHessianService.rebatebyTrxGoodsId(map);
	// System.out.println(rspMap.get("STATUS"));
	// System.out.println(rspMap.get("RSPCODE"));
	// }
	@Autowired
	private TrxOrderNotifyDaemon trxOrderNotifyDaemon;
	//@Test
	public void testTrxCreate() throws UnsupportedEncodingException {

		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", "1020");
		map.put("providerType", "YEEPAY");
		map.put("providerChannel", "BOC");
		map.put("goodsName", URLEncoder.encode("阿斯顿第三方", "utf-8"));
		map.put("goodsId", "12496");
		map.put("sourcePrice", "20");
		map.put("payPrice", "0");
		map.put("rebatePrice", "5");
		map.put("dividePrice", "10");
		map.put("guestId", "123456");
		map.put("orderLoseAbsDate", "60");
		map.put("orderLoseDate", "null");

		map.put("trxType", "NORMAL");
		map.put("payMp", "5" + "-" + "13683334717" + "-" + 12496);
		map.put("prizeId", "100000000");
		map.put("des", "123.0");
		map.put("reqChannel", "BOSS");
		map.put("goodsCount", "10");
		Map rspMap = null;

		System.out.println(rspMap.get("status"));
		System.out.println(rspMap.get("rspCode"));
	}

	@Autowired
	private AccountNotifyDaemon accountNotifyDaemon;
	
	@Test
	public void testTrxCreateInMC1() throws UnsupportedEncodingException {

		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", "1117051");
		map.put("pageOffset", "0");
		map.put("isCachePage", "0");
		map.put("uuid", "111111111111111111111122");
		map.put("pageSize", "10");
		map.put("reqChannel", "BOSS");
	
		
		Map<String,String> rspMap = trxHessianServiceGateWay.queryPurse(map);
		
		System.out.println(rspMap.get("rspCode"));
		System.out.println("**************************"+rspMap);
	}
	/*
	 * @Test public void testRefundToBank() { Map<String, String> map = new
	 * HashMap<String, String>();
	 * 
	 * map.put("trxGoodsId", "10227");
	 * 
	 * map.put("operator", "test");
	 * 
	 * Map rspMap = trxHessianService.refundToBank(map);
	 * 
	 * System.out.println(rspMap.get("STATUS"));
	 * System.out.println(rspMap.get("RSPCODE")); }
	 */
	/*
	 * @Test public void testComplateTrx() {
	 * 
	 * Map<String, String> map = new HashMap<String, String>();
	 * map.put("payRequestId", "Requestf80b70ad0ea445fbb6b");
	 * map.put("proExternallId", "test11011955s4"); map.put("sucTrxAmount",
	 * "80");
	 * 
	 * Map rspMap = trxHessianService.complateTrx(map);
	 * System.out.println(rspMap.get("STATUS"));
	 * System.out.println(rspMap.get("RSPCODE")); }
	 */
	/*
	 * @Test public void testHessianService() { OrderInfo orderInfo = new
	 * OrderInfo(); Map<String,String> map=new HashMap<String,String>();
	 * map.put("userId", "20"); map.put("trxAmount", "0.01");
	 * map.put("bizType","test"); //Map
	 * rspMap=trxHessianService.createAccount(map); //Map
	 * rspMap=trxHessianService.getActByUserId(map); Map rspMap;
	 * 
	 * 
	 * rspMap = trxHessianService.rebate(map);
	 * System.out.println(rspMap.get("STATUS"));
	 * System.out.println(rspMap.get("RSPCODE")); }
	 */
	/*
	 * @Test public void testBizProcessFactory() { OrderInfo orderInfo = new
	 * OrderInfo();
	 * 
	 * orderInfo.setBizType("rebate-biz"); orderInfo.setUserId(120L);
	 * orderInfo.setExtendInfo("extendInfo"); orderInfo.setTrxAmount(105D);
	 * orderInfo.setBizProcessType(BizProcessType.REBATE); try { try {
	 * bizProcessFactory.getProcessService(orderInfo); } catch (RebateException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (AccountException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (TrxOrderException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } catch (PaymentException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (TrxorderGoodsException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } } catch (ProcessServiceException e) { // TODO
	 * Auto-generated catch // block e.printStackTrace(); } } /* @Test public
	 * void testAddUserEmailRegist(){ OrderInfo orderInfo=new OrderInfo();
	 * 
	 * orderInfo.setBizType("rebate-biz"); orderInfo.setUserId(123L);
	 * orderInfo.setExtendInfo("extendInfo");
	 * orderInfo.setTrxAmount(105.676767D);
	 * 
	 * try { rebateProcessService.process(orderInfo); } catch
	 * (ProcessServiceException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */
	/*
	 * @Test public void testGuidGenerator(){
	 * 
	 * String iSpecId=guidGenerator.getCode("Trx"); System.out.print(iSpecId); }
	 */
	/*
	 * @Test public void testRebRecordService(){
	 * 
	 * RebRecord rebRecord=new RebRecord();
	 * 
	 * 
	 * rebRecord.setCreateDate(new Date()); rebRecord.setExternalId("22");
	 * rebRecord.setRequestId("dsdsds"); rebRecord.setTrxAmount(100L);
	 * rebRecord.setTrxStatus(TrxStatus.INIT); rebRecord.setUserId(5L);
	 * rebRecord.setOrderType(OrderType.REBATE); try {
	 * rebRecordService.create(rebRecord); } catch (RebateException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 */
	/*
	 * @Test public void testAccountService(){
	 * 
	 * try { accountService.create(121L); } catch (AccountException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 */
	/*
	 * @Test public void testAccountHistoryDao(){
	 * 
	 * AccountHistory actHistory=new AccountHistory();
	 * 
	 * actHistory.setAccountId(100L);
	 * actHistory.setActHistoryType(ActHistoryType.SALES);
	 * actHistory.setBalance(100D); actHistory.setBizType("bizType");
	 * actHistory.setCreateDate(new Date()); actHistory.setTrxId(100L);
	 * actHistory.setDispaly(true); actHistory.setDescription("desc");
	 * accountHistoryDao.addAccountHistory(actHistory); }
	 */
	
	//@Test
	public void testTrxCreateInMC() throws UnsupportedEncodingException {

	Map<String, String> map = new HashMap<String, String>();
	map.put("reqChannel", "PARTNER");
	map.put("guestId", "914003183");
	map.put("voucherCode", "70784119");
	map.put("voucherVerifySource", "SMS");
	map.put("subGuestId", "1");
	map.put("description","111111");

	//List<Map<String, Object>>  list = trxorderGoodsDao.findTrxorderGoodsByGoodsIdTrxorderId("1818874","38062");

	//trxorderGoodsService.findTrxorderGoodsByUserIdStatus(1020L, 0, 5, "USEDINPFVOU|COMMENTEDINPFVOU|EXPIRED|REFUNDTOACT|RECHECK|REFUNDTOBANK");
	/*Map<String, Object> togMap = list.get(0);
	String lastUpdateDate = DateUtils.dateToStrLong( togMap.get("lastUpdateDate")==null?new Date():(Date)togMap.get("lastUpdateDate"));
	System.out.println(lastUpdateDate);*/
	//Map<String,String> rspMap = trxHessianServiceGateWay.createTrxOrder(map);
	Map<String,String> rspMap = trxHessianServiceGateWay.validateVoucher(map);

	System.out.println(rspMap.get("status"));
	System.out.println(rspMap.get("rspCode"));
	System.out.println("**************************"+rspMap);
	}
	
	
	
	
	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;
	


}
