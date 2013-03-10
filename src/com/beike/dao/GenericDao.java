package com.beike.dao;

import java.io.Serializable;

public interface GenericDao<T, ID extends Serializable> {
	public Long getLastInsertId();
}
