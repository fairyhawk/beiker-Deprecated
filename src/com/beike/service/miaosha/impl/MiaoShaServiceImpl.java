package com.beike.service.miaosha.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.GenericDao;
import com.beike.dao.miaosha.MiaoShaDao;
import com.beike.entity.miaosha.MiaoSha;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;
import com.beike.service.goods.GoodsService;
import com.beike.service.impl.GenericServiceImpl;
import com.beike.service.miaosha.MiaoShaService;

/**      
 * project:beiker  
 * Title:秒杀Service实现
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Jul 31, 2012 3:25:39 PM     
 * @version 1.0
 */
@Service("miaoShaService")
public class MiaoShaServiceImpl extends GenericServiceImpl<MiaoSha, Long> implements MiaoShaService {
	
	@Autowired
	private MiaoShaDao miaoShaDao;
	
	@Autowired
	private GoodsService goodsService;
	
	/* (non-Javadoc)
	 * @see com.beike.service.GenericService#findById(java.io.Serializable)
	 */
	@Override
	public MiaoSha findById(Long id) {
		return miaoShaDao.getMiaoShaById(id);
	}

	@Override
	public GenericDao<MiaoSha, Long> getDao() {
		return miaoShaDao;
	}

	@Override
	public MiaoSha getMiaoShaById(Long msId) {
		return miaoShaDao.getMiaoShaById(msId);
	}

	@Override
	public List<MiaoSha> getMiaoShaListByAreaId(Long areaId, int count) {
		List<MiaoSha> lstMiaoSha = miaoShaDao.getMiaoShaListByAreaId(areaId, count);
		if(lstMiaoSha!=null && lstMiaoSha.size()>0){
			List<Long> lstGoodsIds = new ArrayList<Long>();
			for(MiaoSha tmpMs : lstMiaoSha){
				lstGoodsIds.add(tmpMs.getGoodsId());
			}
			
			List<GoodsForm> lstGoods = goodsService.getGoodsFormByChildId(lstGoodsIds);
			if(lstGoods!=null && lstGoods.size()>0){
				HashMap<Long,GoodsForm> hsGoodsMap = new HashMap<Long,GoodsForm>();
				for(GoodsForm goods : lstGoods){
					hsGoodsMap.put(goods.getGoodsId(), goods);
				}
				if(!hsGoodsMap.isEmpty()){
					for(MiaoSha tmpMs : lstMiaoSha){
						GoodsForm tmpGoods = hsGoodsMap.get(tmpMs.getGoodsId());
						tmpMs.setGoodsCurrentPrice(tmpGoods.getCurrentPrice());
						tmpMs.setGoodsLogo(tmpGoods.getLogo4());
					}
				}
			}
		}
		return lstMiaoSha;
	}

	@Override
	public int getMiaoShaCount(Long areaId, int status) {
		return miaoShaDao.getMiaoShaCount(areaId, status);
	}

	@Override
	public List<Long> getMiaoShaIdsByPage(Long areaId, int status, Pager pager) {
		return miaoShaDao.getMiaoShaIdsByPage(areaId, status, pager);
	}

	@Override
	public List<MiaoSha> getMiaoShaListByIds(List<Long> lstMsIds) {
		return miaoShaDao.getMiaoShaListByIds(lstMsIds);
	}

	@Override
	public List<Long> getIndexMiaoShaByCityId(Long areaId) {
		return miaoShaDao.getIndexMiaoShaByCityId(areaId);
	}

	@Override
	public List<Map<String, Object>> getNextBeginMiaoShaIDs(String timeS,
			String timeE) {
		return miaoShaDao.getNextBeginMiaoShaIDs(timeS, timeE);
	}
}
