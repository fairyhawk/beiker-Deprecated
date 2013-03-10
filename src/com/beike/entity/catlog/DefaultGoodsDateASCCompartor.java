package com.beike.entity.catlog;

import java.util.Comparator;

/**
 * 销售量排序逆序接口
 * @author janwen
 * @time Dec 29, 2011 8:39:05 PM
 */
public class DefaultGoodsDateASCCompartor implements Comparator<DefaultGoods>  {

	
	
	@Override
	public int compare(DefaultGoods o1, DefaultGoods o2) {
		//销售量小排前
		if(o1.getSaled() > o2.getSaled()){
			return 1;
			//销售量相同,上架时间最近排后
		}else if(o1.getSaled() == o2.getSaled()){
			if(o1.getOn_time().after(o2.getOn_time())){
				return 1;
			}else if(o1.getOn_time().before(o2.getOn_time())){
				return 1;
			}else{
				return 0;
			}
		}else{
			return -1;
		}
	}

	
	
}
