package com.beike.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.beike.entity.takeaway.TakeAway;
import com.beike.entity.takeaway.TakeAwayMenu;

public class TakeAwayDetailForm {
	private TakeAway takeAwayInfo;
	private List<TakeAwayMenu> menu;

	public TakeAway getTakeAwayInfo() {
		return takeAwayInfo;
	}

	public void setTakeAwayInfo(TakeAway takeAwayInfo) {
		this.takeAwayInfo = takeAwayInfo;
	}

	public List<TakeAwayMenu> getMenu() {
		return menu;
	}

	public void setMenu(List<TakeAwayMenu> menu) {
		this.menu = menu;
	}

	//获取根据菜品类别分类的菜单表
	public Map<String, List<TakeAwayMenu>> getClassifiedMenuTable() {
		Map<String, List<TakeAwayMenu>> mapedMenu = new HashMap<String, List<TakeAwayMenu>>();
		if (takeAwayInfo == null || menu == null) {
			return mapedMenu;
		}

		for (TakeAwayMenu menuItem : menu) {
			List<TakeAwayMenu> sameCategoryMenuItemList = mapedMenu.get(StringUtils.trimToEmpty(menuItem.getMenuCategory()));
			if (sameCategoryMenuItemList == null) {
				sameCategoryMenuItemList = new ArrayList<TakeAwayMenu>();
				mapedMenu.put(StringUtils.trimToEmpty(menuItem.getMenuCategory()), sameCategoryMenuItemList);
			}
			sameCategoryMenuItemList.add(menuItem);
		}
		
		//菜品排序
		for (List<TakeAwayMenu> menuItemList :mapedMenu.values()) {
			Collections.sort(menuItemList, new Comparator<TakeAwayMenu>() {
				@Override
				public int compare(TakeAwayMenu o1, TakeAwayMenu o2) {
					return o1.getMenuSort().compareTo(o2.getMenuSort());
				}});
		}
		
		return mapedMenu;
	}
}
