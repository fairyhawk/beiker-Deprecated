package com.beike.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * List 求差，求并，求交
 * 
 * @author wenhua.cheng
 * 
 */
@SuppressWarnings("unchecked")
public class ListUtils {
	/**
	 * 求交集
	 * 
	 * @param ls
	 * @param ls2
	 * @return
	 */
	public static List intersect(List ls, List ls2) {
		List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
		Collections.copy(list, ls);
		list.retainAll(ls2);
		return list;
	}

	/**
	 * 求并
	 * 
	 * @param ls
	 * @param ls2
	 * @return
	 */

	public static List union(List ls, List ls2) {
		List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
		Collections.copy(list, ls);
		list.addAll(ls2);
		return list;
	}

	/**
	 * 求差
	 * 
	 * @param ls
	 * @param ls2
	 * @return
	 */
	public static List diff(List ls, List ls2) {
		List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
		Collections.copy(list, ls);
		list.removeAll(ls2);
		return list;
	}
	
	
	public static String listSplitPrefix(List list,String prefix){
		if(list==null||list.size()==0)
		return "";
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<list.size();i++){
			Object obj=list.get(i);
			sb.append(obj);
			sb.append(prefix);
		}
		if(sb.indexOf(prefix)!=-1){
			return sb.substring(0, sb.lastIndexOf(prefix));
		}
		return sb.toString();
	}


}
