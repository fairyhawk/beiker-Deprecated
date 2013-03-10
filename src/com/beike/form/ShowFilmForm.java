package com.beike.form;

import com.beike.entity.film.FilmShow;

public class ShowFilmForm {
	private FilmShow filmShow;//上映的电影

	private Long grouponGoodsId;//团购商品ID,不支持团购是，值为null

	public FilmShow getFilmShow() {
		return filmShow;
	}

	public void setFilmShow(FilmShow filmShow) {
		this.filmShow = filmShow;
	}

	public Long getGrouponGoodsId() {
		return grouponGoodsId;
	}

	public void setGrouponGoodsId(Long grouponGoodsId) {
		this.grouponGoodsId = grouponGoodsId;
	}

}
