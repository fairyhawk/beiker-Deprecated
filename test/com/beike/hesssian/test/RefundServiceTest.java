package com.beike.hesssian.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.beike.biz.service.hessian.TrxHessianServiceGateWay;
import com.beike.common.entity.vm.VmTrxExtend;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.RefundException;
import com.beike.core.service.trx.RefundService;
import com.beike.util.Amount;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
/*
 * @Title: RefundServiceTest.java
 * @Package com.beike.demo.test
 * @Description: TODO
 * @date May 25, 2011 6:58:13 PM
 * @author wh.cheng
 * @version v1.0
 */
 @RunWith(SpringJUnit4ClassRunner.class)
 @ContextConfiguration(locations = { "classpath:/applicationContext.xml",
 		"classpath:/springcontexttrx/trx-applicationContext.xml" })
 @TestExecutionListeners( { DependencyInjectionTestExecutionListener.class })
public class RefundServiceTest {
     @Autowired
    private TrxHessianServiceGateWay  trxHessianServiceGateWay;
	@Autowired
	private RefundService refundService;
    public void testsettle(){

        Map<String, String> sourceMap = new HashMap<String,String>();
        System.out.println(" +++++ test begin ");
        sourceMap.put("reqChannel", "BOSS");
        sourceMap.put("guestId", "1010742");
        sourceMap.put("voucherVerifySource", "SELFSERVICE");
        sourceMap.put("subGuestId", "1010742");
        try {
           String ss ="37402447";
           String [] sarr= ss.split(",");
           for(String vid:sarr){
               sourceMap.put("voucherCode", vid);
               try {
                   Map<String, String> map=  trxHessianServiceGateWay.validateVoucher(sourceMap);
                   System.out.println("++++ "+vid+" :"+map);
                } catch (Exception e) {
                    System.out.println("++++ error111  "+vid);
                }
               
           }
            
           System.out.println(" +++++ test end ++++  "); 
           
        //System.out.println(map.get("rspCode"));
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    
    }
	public RefundService getRefundService() {
		return refundService;
	}

	public void setRefundService(RefundService refundService) {
		this.refundService = refundService;
	}
	
	public void testRefundService(){/*
		
	    String userId="101585158";
	    int i=30;
	    Map<String, String> sourceMap = new HashMap<String, String>();
	    sourceMap.put("reqChannel", "BOSS");
	    sourceMap.put("vmAccountId", "97");//97优惠券 98正常
	    sourceMap.put("amount", "30");
	    sourceMap.put("requestId", "11111"+i);
	    sourceMap.put("userId", userId);
	    sourceMap.put("description", "description111");
	    sourceMap.put("bizType", "CARDLOAD");
	    
	    sourceMap.put("operatorId", "1");
	    
        
        trxHessianServiceGateWay.dispatchVm(sourceMap);
        i++;
        Map<String, String> sourceMap2 = new HashMap<String, String>();
        sourceMap2.put("reqChannel", "BOSS");
        sourceMap2.put("vmAccountId", "98");//97优惠券 98正常
        sourceMap2.put("amount", "15");
        sourceMap2.put("requestId", "11111"+i);
        sourceMap2.put("userId", userId);
        sourceMap2.put("description", "description111");
        sourceMap2.put("bizType", "CARDLOAD");
        
        sourceMap2.put("operatorId", "1");
        
        
        trxHessianServiceGateWay.dispatchVm(sourceMap2);
        
        
        
//		try {
//			//refundService.processToAct(227L);
//		} catch (RefundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (AccountException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		
//		try {
//			refundService.processToBank(16L);
//		} catch (RefundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (AccountException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	*/}
	
	
	public  void main1(String[] args) {
	    /******************/
	    List<VmTrxExtend> trxVmTrxtendList = new ArrayList<VmTrxExtend>();
	    VmTrxExtend extend1 = new VmTrxExtend();
        extend1.setAccountId(1L);
        extend1.setSubAccountId(11L);
        extend1.setAmount(10);
        trxVmTrxtendList.add(extend1);
        
        VmTrxExtend extend2 = new VmTrxExtend();
        extend2.setAccountId(1L);
        extend2.setSubAccountId(22L);
        extend2.setAmount(10);
        trxVmTrxtendList.add(extend2);
        
        
        VmTrxExtend extend3 = new VmTrxExtend();
        extend3.setAccountId(1L);
        extend3.setSubAccountId(33L);
        extend3.setAmount(10);
        trxVmTrxtendList.add(extend3);
        ///
	    List<VmTrxExtend> refundVmTrxtendList = new ArrayList<VmTrxExtend>();
	    
	    VmTrxExtend extend6 = new VmTrxExtend();
        extend6.setAccountId(1L);
        extend6.setSubAccountId(33L);
        extend6.setAmount(10);
        trxVmTrxtendList.add(extend6);
        
        
        VmTrxExtend extend7 = new VmTrxExtend();
        extend7.setAccountId(1L);
        extend7.setSubAccountId(44L);
        extend7.setAmount(10);
        trxVmTrxtendList.add(extend7);
        
	    VmTrxExtend extend4 = new VmTrxExtend();
        extend4.setAccountId(1L);
        extend4.setSubAccountId(11L);
        extend4.setAmount(5);
        refundVmTrxtendList.add(extend4);
        
        VmTrxExtend extend5 = new VmTrxExtend();
        extend5.setAccountId(1L);
        extend5.setSubAccountId(11L);
        extend5.setAmount(5);
        refundVmTrxtendList.add(extend5);
        
	   
	    /******************/
	    
	    refundVmTrxtendList = mergeList(refundVmTrxtendList);
	    int rudedVmtTrxEtdCount = refundVmTrxtendList.size();
	    System.out.println("refund size:"+rudedVmtTrxEtdCount);
        // 遍历得到已经退到了哪个交易关联记录了
        VmTrxExtend refundedVmTrxExtend = refundVmTrxtendList
                .get(rudedVmtTrxEtdCount - 1);// 最后一个退款发生的关联记录
        System.out.println(refundedVmTrxExtend.getSubAccountId());
	   int trxVmTtdIndex = trxVmTrxtendList.indexOf(refundedVmTrxExtend);
	   System.out.println("trxVmTtdIndex:"+trxVmTtdIndex);
    }
	
	public static List<VmTrxExtend> mergeList(List<VmTrxExtend> list) {

        HashMap<Long, VmTrxExtend> map = new HashMap<Long, VmTrxExtend>();
        for (VmTrxExtend item : list) {
            Long subActId = item.getSubAccountId();// 个人子账户ID
            if (map.containsKey(subActId)) {
                item.setAmount(Amount.add(map.get(subActId).getAmount(), item
                        .getAmount()));
            }
            map.put(item.getSubAccountId(), item);
            // VmTrxExtendList.add(item);

        }
        list.clear();
        list.addAll(map.values());

        return list;
    }
	@SuppressWarnings("unchecked")
    public void testqueryTrxGoodsByIds(){
        try {
            Map<String, String> sourceMap = new HashMap<String,String>();
            System.out.println(" +++++ test begin ");
            sourceMap.put("trxGoodsIds", "100");
            Map<String, Object> map= trxHessianServiceGateWay.queryTrxGoodsByIds(sourceMap);
            System.out.println("rspCode:"+map.get("rspCode"));
            List<Map<String, Object>> list = ( List<Map<String, Object>>)map.get("data");
            for(Map<String, Object> goods:list){
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("订单编号 ："+goods.get("trxOrderId"));

                System.out.println("订单状态 ："+goods.get("trxStatus"));
                
                System.out.println("消费时间 ："+goods.get("confirmDate"));
                
                System.out.println("商品编号 ："+goods.get("goodsId"));
                
                System.out.println("商家编号 ："+goods.get("guestId"));
                
                System.out.println("购买时间 ："+goods.get("createDate"));
                
                System.out.println("商品名称 ："+goods.get("goodsName"));
                
                System.out.println("payPrice ："+goods.get("payPrice"));
                
                System.out.println("dividePrice ："+goods.get("dividePrice"));
                
                System.out.println("mer_settle_status ："+goods.get("merSettleStatus"));
                
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
            }
            
            
            
            
        } catch (Exception e) {
           e.printStackTrace();
        }
        
    }
	@Test
	public void testTuanCount(){
	    try {
            
    	    Map<String, String> sourceMap = new HashMap<String,String>();
    	    String gg ="1010742";
            sourceMap.put("guestIdForSales",gg);
            sourceMap.put("subGuestIdForUsed", "1010742,1010743");
             MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
//            memCacheService.remove("countGuestIdForSales"+ gg);
//            memCacheService.remove("countSubGuestIdForUsed"+ gg);
           
            
            
            Map<String, String> map =trxHessianServiceGateWay.queryTrxGoodsCountForGuest(sourceMap);
            
           System.out.println("+++ tuanBuyCount:"+ map.get("tuanBuyCount").toString());
           System.out.println("+++ shopBuyCount:"+ map.get("shopBuyCount").toString());
            
           System.out.println("+++ tuanUsedCount:"+ map.get("tuanUsedCount").toString());
           System.out.println("+++ shopUsedCount:"+ map.get("shopUsedCount").toString());
       
	    } catch (Exception e) {
            // TODO: handle exception
        }
        
        
	}
	public static void main(String[] args) {
	    Date date = new Date();
        String startDate=DateUtils.dateToStr(date, "yyyy-MM-dd HH:mm:ss");
        String endDate= DateUtils.dateToStr(DateUtils.getFirstDateOfMonth(date), "yyyy-MM-dd HH:mm:ss"); 
        System.out.println(startDate);
        System.out.println(endDate);
    }
}
