/*
 * package com.beike.hesssian.test;
 * 
 * import java.util.Date;
 * 
 * import org.junit.Test; import org.junit.runner.RunWith; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.test.context.ContextConfiguration; import
 * org.springframework.test.context.TestExecutionListeners; import
 * org.springframework.test.context.junit4.SpringJUnit4ClassRunner; import
 * org.springframework
 * .test.context.support.DependencyInjectionTestExecutionListener;
 * 
 * import com.beike.common.entity.trx.lottery.reg.LotteryReg; import
 * com.beike.common.enums.trx.RefundHandleType; import
 * com.beike.common.enums.trx.RefundSourceType; import
 * com.beike.common.exception.AccountException; import
 * com.beike.common.exception.RefundException; import
 * com.beike.common.exception.RuleException; import
 * com.beike.common.exception.StaleObjectStateException; import
 * com.beike.common.exception.VmAccountException; import
 * com.beike.common.exception.VoucherException; import
 * com.beike.core.service.trx.RefundService; import
 * com.beike.core.service.trx.lottery.reg.LotteryRegService;
 * 
 * @RunWith(SpringJUnit4ClassRunner.class)
 * 
 * @ContextConfiguration(locations = { "classpath:/applicationContext.xml",
 * "classpath:/springcontexttrx/trx-applicationContext.xml" })
 * 
 * @TestExecutionListeners( { DependencyInjectionTestExecutionListener.class })
 * public class ReundTest {
 * 
 * @Autowired private LotteryRegService lotteryRegService;
 * 
 * @Test public void testRefund() {
 * 
 * LotteryReg lotteryReg = new LotteryReg();
 * 
 * lotteryReg.setUserId(11111L); lotteryReg.setCreateDate(new Date());
 * lotteryReg.setIsLottery(true); lotteryReg.setLotteryContent("20");
 * lotteryReg.setDescription("desc") ;
 * 
 * LotteryReg returnObj =lotteryRegService.getLotteryInfo(lotteryReg);
 * 
 * System.out.println(returnObj.getId());
 * 
 * } }
 */