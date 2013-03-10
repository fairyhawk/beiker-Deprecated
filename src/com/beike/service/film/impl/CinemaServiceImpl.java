package com.beike.service.film.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.beike.dao.catlog.RegionCatlogDao;
import com.beike.dao.film.CinemaDao;
import com.beike.dao.film.FilmDao;
import com.beike.dao.film.GoodsCinemaDao;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.film.CinemaInfo;
import com.beike.entity.film.FilmRelease;
import com.beike.entity.film.FilmShow;
import com.beike.form.CinemaDetailForm;
import com.beike.page.Pager;
import com.beike.service.film.CinemaService;

@Service("cinemaService")
public class CinemaServiceImpl implements CinemaService {

	@Autowired
	private CinemaDao cinemaDao;
	@Autowired
	@Qualifier("regionCatlogDao")
	private RegionCatlogDao regionCatlogDao;
	@Autowired
	private FilmDao filmDao;
	@Autowired
	private GoodsCinemaDao goodsCinemaDao;

	@Override
	public List<RegionCatlog> getCinemaAreasByCityId(Long cityId) {
		List<Long> areaIdList = cinemaDao.queryCinemaAreaIdsByCity(cityId);
		List<RegionCatlog> areaList = regionCatlogDao.queryByIds(areaIdList);
		return areaList;
	}

	@Override
	public CinemaDetailForm queryCinemaDetail(Long cinemaId) {
		//查询影院详情
		CinemaInfo cinemaInfo = cinemaDao.queryCinemaDetail(cinemaId);
		if (cinemaInfo == null) {
			return null;
		}

		CinemaDetailForm cinemaDetailForm = new CinemaDetailForm();
		cinemaDetailForm.setCinemaInfo(cinemaInfo);
		
		//查询是否支持在线订座、是否支持团购
		cinemaDetailForm.setSupportOLBooking(cinemaInfo.getType() == 1 || cinemaInfo.getType() == 3);
		cinemaDetailForm.setSupportGroupon(goodsCinemaDao.queryExsitCinemaGoods(cinemaId));
		//查询最低团购价
		if (cinemaDetailForm.isSupportGroupon()) {
			cinemaDetailForm.setLowestGrouponPrice(goodsCinemaDao.queryLowestGrouponPrice(cinemaId));
		}
		
		return cinemaDetailForm;
	}

	@Override
	public int queryCinemaCount(Long cityId, Long areaId) {
		return cinemaDao.queryCinemaCount(cityId, areaId);
	}

	@Override
	public List<CinemaInfo> queryCinema(Pager pager, Long cityId, Long areaId) {
		return cinemaDao.queryCinema(pager, cityId, areaId);
	}

	@Override
	public List<CinemaInfo> queryCinema(Long cityId, Long areaId) {
		return cinemaDao.queryCinema( cityId, areaId);
	}

	@Override
	public int queryFilmReleaseCountByCinema(Long cityId, Long cinemaId) {
		return filmDao.queryFilmReleaseCountByCinema(cityId, cinemaId);
	}

	@Override
	public List<FilmRelease> queryFilmReleaseByCinema(Pager pager, Long cityId, Long cinemaId) {
		List<FilmRelease> result = filmDao.queryFilmReleaseByCinema(pager, cityId, cinemaId);
		return result;
	}

	@Override
	public int queryFilmReleaseCountByCityId(Long cityId) {
		return filmDao.queryFilmReleaseCountCityId(cityId);
	}

	@Override
	public List<FilmRelease> queryFilmReleaseCityId(Pager pager, Long cityId) {
		return filmDao.queryFilmReleaseCityId(pager, cityId);
	}

	@Override
	public List<FilmShow> queryFilmShowPlainByCinema(Long cinemaId, Long filmId) {
		return cinemaDao.queryFilmShowPlainByCinema(cinemaId, filmId);
	}

	@Override
	public boolean queryIsQianpinCinema(Long cinemaId) {
		return cinemaDao.queryIsQianpinCinema(cinemaId);
	}

}
