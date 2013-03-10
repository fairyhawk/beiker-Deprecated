package com.beike.entity.catlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * 默认排序比较接口,销售量排序升序
 * @author janwen
 * @time Dec 29, 2011 8:38:30 PM
 */
public class DefaultGoodsDescCompartor implements Comparator<DefaultGoods>  {

	
	
	
	
	@Override
	public int compare(DefaultGoods o1, DefaultGoods o2) {
		
		//销售量大排前
		if(o1.getSaled() > o2.getSaled()){
			return -1;
			//销售量相同,上架时间最近排前
		}else if(o1.getSaled() == o2.getSaled()){
			if(o1.getOn_time().after(o2.getOn_time())){
				return -1;
			}else if(o1.getOn_time().before(o2.getOn_time())){
				return 1;
			}else{
				return 0;
			}
		}else{
			return 1;
		}
	}

	
	
}
