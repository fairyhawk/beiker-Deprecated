package com.beike.page;
/**
 * Title : 	PagerHelper
 * <p/>
 * Description	: 保存分页信息类
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : Sinobo
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-5-23   lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-23  
 */
public class PagerHelper {

	public static Pager getPager(int currentPage, int totalRows, int pageSize) {
		Pager pager = new Pager(totalRows, pageSize);
		if (currentPage==0) {
			pager.setStart(1);
		} else {
				pager.setStart(currentPage);
		}

		return pager;
	}
}
