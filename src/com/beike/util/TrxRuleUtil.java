package com.beike.util;

import com.beike.common.exception.BaseException;
import com.beike.common.exception.RuleException;

/**
 * @Title: TrxRuleUtil
 * @Package com.beike.dao.trx
 * @Description:支付表达式处理工具类
 * @author wh.cheng@sinobogroup.com
 * @date May 5, 2011 4:38:03 PM
 * @version V1.0
 */
public class TrxRuleUtil {

	public static final String ACTHIS = "ACTHIS"; // 帐务历史是否在前端显示KEY

	public static final String VIEWRFD = "VIEWRFD"; // 退款链接是否在前端显示KEY

	public static final String AUTORFD = "AUTORFD"; // 是否自动退款KEY

	public static final String INTER_ITEM_PARTTION = ";";// 表达式间的分隔符

	public static final String INSIDE_ITEM_PARTTION = ":";// 表达式内的分隔符

	/**
	 * 解析账户历史是否显示
	 * 
	 * @param sourceRule
	 * @return
	 */
	public static boolean resolveIsActHis(String sourceRule) {
		boolean result = false;
		try {
			// String[] ruleAry = sourceRule.split(INTER_ITEM_PARTTION);
			// ACTHIS:1;VIEWRFD:1;AUTORFD:1;
			// String[] actHisItem = ruleAry[0].split(INSIDE_ITEM_PARTTION);
			String[] actHisItem = sourceRule.split(INSIDE_ITEM_PARTTION);

			String actHisValue = actHisItem[1];

			if ("1".equals(actHisValue)) {
				result = true;

			}
			if ("".equals(actHisValue) || actHisValue == null) {

				throw new RuleException(BaseException.TRXRULE_RESOLVE_ERROR);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();

		}

		return result;

	}

	/**
	 * 解析我的退款按钮是否显示
	 * 
	 * @param sourceRule
	 * @return
	 */
	public static boolean resolveIsViewRfd(String sourceRule) {
		boolean result = false;
		try {
			String[] ruleAry = sourceRule.split(INTER_ITEM_PARTTION);
			// ACTHIS:1;VIEWRFD:1;AUTORFD:1;

			String[] viewRfdItem = ruleAry[1].split(INSIDE_ITEM_PARTTION);

			String viewRfdValue = viewRfdItem[1];

			if ("1".equals(viewRfdValue)) {
				result = true;

			}
			if ("".equals(viewRfdValue) || viewRfdValue == null) {

				throw new RuleException(BaseException.TRXRULE_RESOLVE_ERROR);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();

		}

		return result;

	}

	/**
	 * 解析自动退款是否需要执行
	 * 
	 * @param sourceRule
	 * @return
	 */
	public static boolean resolveIsAutofd(String sourceRule) {
		boolean result = false;
		try {
			String[] ruleAry = sourceRule.split(INTER_ITEM_PARTTION);
			// ACTHIS:1;VIEWRFD:1;AUTORFD:1;

			String[] autoRfdItem = ruleAry[2].split(INSIDE_ITEM_PARTTION);

			String autoRfdValue = autoRfdItem[1];

			if ("1".equals(autoRfdValue)) {
				result = true;

			}
			if ("".equals(autoRfdValue) || autoRfdValue == null) {

				throw new RuleException(BaseException.TRXRULE_RESOLVE_ERROR);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();

		}

		return result;

	}

}
