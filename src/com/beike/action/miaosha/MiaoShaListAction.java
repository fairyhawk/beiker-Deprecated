package com.beike.action.miaosha;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.entity.miaosha.MiaoSha;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.goods.GoodsService;
import com.beike.service.miaosha.MiaoShaService;
import com.beike.util.BeanUtils;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;

/**
 * <p>
 * Title:秒杀列表action
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 */
@Controller
public class MiaoShaListAction {

	private static Log log = LogFactory.getLog(MiaoShaListAction.class);
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private MiaoShaService miaoShaService;
	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	private static String CITY_CATLOG = "CITY_CATLOG";

	private static int pageSize = 36;

	@SuppressWarnings("unchecked")
	@RequestMapping("/miaosha/listMiaoSha.do")
	public Object listMiaoSha(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get(CITY_CATLOG);
			if (mapCity == null) {
				mapCity = BeanUtils.getCity(request, "regionCatlogDao");
				memCacheService.set(CITY_CATLOG, mapCity);
			}
			String city = CityUtils.getCity(request, response);
			if (city == null || "".equals(city)) {
				city = "beijing";
			}
			Long cityid = null;
			if (mapCity != null) {
				cityid = mapCity.get(city.trim());
			}
	
			String status = request.getParameter("status");
			String currentPage = request.getParameter("cpage");
			if(!"0".equals(status)){
				status = "1";
			}
			// 秒杀数量
			int msCount = miaoShaService.getMiaoShaCount(cityid, Integer.parseInt(status));
			
			if (StringUtils.isEmpty(currentPage)) {
				currentPage = "1";
			}
			
			// 分页
			Pager pager = PagerHelper.getPager(Integer.parseInt(currentPage),
					msCount, pageSize);
			
			if (pager.getCurrentPage() > pager.getTotalPages()) {
				pager.setCurrentPage(1);
				pager.setStartRow(0);
			}
			request.setAttribute("pager", pager);

			// 当前页秒杀数据
			List<Long> listMiaoShaIds = miaoShaService.getMiaoShaIdsByPage(cityid, Integer.parseInt(status), pager);
			List<MiaoSha> lstMiaoSha = miaoShaService.getMiaoShaListByIds(listMiaoShaIds);
			if(lstMiaoSha!=null && lstMiaoSha.size()>0){
				List<Long> lstMsGoodsIds = new ArrayList<Long>();
				for(MiaoSha tmpMs : lstMiaoSha){
					lstMsGoodsIds.add(tmpMs.getGoodsId());
				}
				
				List<GoodsForm> lstGoods = goodsService.getGoodsFormByChildId(lstMsGoodsIds);
				if(lstGoods!=null && lstGoods.size()>0){
					HashMap<Long,GoodsForm> hsGoodsMap = new HashMap<Long,GoodsForm>();
					for(GoodsForm goods : lstGoods){
						hsGoodsMap.put(goods.getGoodsId(), goods);
					}
					if(!hsGoodsMap.isEmpty()){
						for(MiaoSha tmpMs : lstMiaoSha){
							GoodsForm tmpGoods = hsGoodsMap.get(tmpMs.getGoodsId());
							tmpMs.setGoodsCurrentPrice(tmpGoods.getCurrentPrice());
							tmpMs.setGoodsLogo(tmpGoods.getLogo2());
							
							if(tmpMs.getMsStatus()==1){
								//未开始
								if(tmpMs.getMsStartTime().compareTo(new Timestamp(System.currentTimeMillis())) == 1){
									tmpMs.setStartSeconds(DateUtils.countDifSeconds(new Timestamp(System.currentTimeMillis()),tmpMs.getMsStartTime()));
									tmpMs.setEndSeconds(DateUtils.countDifSeconds(tmpMs.getMsEndTime(),new Timestamp(System.currentTimeMillis())));
									tmpMs.setMsStatus(2);
								}else if(tmpMs.getMsEndTime().compareTo(new Timestamp(System.currentTimeMillis())) == 1){
								//已开始，未结束
									tmpMs.setStartSeconds(0l);
									tmpMs.setEndSeconds(DateUtils.countDifSeconds(tmpMs.getMsEndTime(),new Timestamp(System.currentTimeMillis())));
									if(tmpMs.getMsSaleCount()>=tmpMs.getMsMaxCount()){
										tmpMs.setMsStatus(0);
									}else{
										tmpMs.setMsStatus(1);
									}
								}else{
									tmpMs.setStartSeconds(0l);
									tmpMs.setEndSeconds(0l);
									tmpMs.setMsStatus(0);
								}
							}else{
								tmpMs.setStartSeconds(0l);
								tmpMs.setEndSeconds(0l);
								tmpMs.setMsStatus(0);
							}
						}
					}
				}
			}
			
			request.setAttribute("pager", pager);
			request.setAttribute("status", status);
			request.setAttribute("lstMiaoSha", lstMiaoSha);
		} catch (Exception e) {
			log.info(e);
			e.printStackTrace();
			return new ModelAndView("redirect:../404.html");
		}
		return "/miaosha/listMiaoSha";
	}
}