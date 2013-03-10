package com.beike.service.goods.ad.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.goods.ad.ADGoodsDao;
import com.beike.service.goods.ad.ADGoodsService;


@Service("ADGoodsService")
public class ADGoodsServiceImpl implements ADGoodsService {

	@Autowired
	private ADGoodsDao adGoodsDao;
	@Override
	public List<Long> getSameCategoryGoods(int cityid, int categoryid,
			String type, String notinGoodsid, int limit) {
		return adGoodsDao.getSameCategoryGoods(cityid, categoryid, type, notinGoodsid, limit);
	}

}
