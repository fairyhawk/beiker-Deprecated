package com.beike.core.service.trx;

import java.util.List;

import com.beike.common.entity.trx.Voucher;

/**
 * @Title: VoucherPrefetchService.java
 * @Package com.beike.core.service.trx
 * @Description: 凭证预取服务接口(新起事务，独立出来使AOP生效)
 * @date 4 1, 2012 6:31:57 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface VoucherPrefetchService {

	/**
	 * 凭证预取 新起事务. 对入口事务时间没有减少，但需加快for update以及预取后的update的事务提交时间，尽快为集群其它服务器释放DB
	 * 锁资源。 变更为需要事务 mysql for update 锁区间，否则会有死锁问题
	 * 
	 * @param prefetchCount
	 * @return
	 */
	public List<Voucher> preFetchVoucher(int prefetchCount);

}
