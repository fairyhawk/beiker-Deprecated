package com.beike.biz.service.trx.daemon;

import org.springframework.stereotype.Service;

/**
 * @Package com.beike.biz.service.trx
 * @Description: 定时补发返现金额（该功能废弃且findByDis()查询数据过多，若重新启用需优化）
 * @date 2011-12-19 13:49:27
 * @author renli.yu
 * @version v1.0
 */
@Service("reRebateDaemon")
public class ReRebateDaemon {

	//private final Log logger = LogFactory.getLog(ReRebateDaemon.class);

	//@Autowired
	//private TrxorderGoodsService trxorderGoodsService;

	public void executeReturnCancer() {
		/*

		List<TrxorderGoods> trxList = trxorderGoodsService.findByDis();
		if (trxList == null || trxList.size() == 0) {
			return;
		}
		int listCount = trxList.size();
		logger
				.info("+++++++++"
						+ "++++++++start++++++++++reissueReturnAmountDaemon+++++++++size="
						+ listCount + "++++++++++");
		int k = 0;

		for (TrxorderGoods trxorderGoods : trxList) {
			Long trxorderGoodsId = trxorderGoods.getId();
			try {
				trxorderGoodsService.rebateDaemon(trxorderGoods);
				k++;
				logger
						.info("+++++++++reissueReturnAmountDaemon->trxorderGoodsId"
								+ trxorderGoodsId + "+++++++++++++");

			} catch (BaseException e) {
				logger.debug("+++++++++" + e + "+++++++++++++");
				e.printStackTrace();
			} catch (Exception e1) {
				logger.debug("+++++++++" + e1 + "+++++++++++++");
				e1.printStackTrace();
			}

		}
		logger
				.info("+++++++++++++++++end++++++++++reissueReturnAmountDaemon+++++++++SUCCESS="
						+ k + "++++++++++");
	*/}
}
