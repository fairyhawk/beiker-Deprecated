/**
 * 
 */
package com.beike.service.impl;

import java.io.Serializable;

import com.beike.dao.GenericDao;
import com.beike.service.GenericService;

public abstract class GenericServiceImpl<T, ID extends Serializable> implements
		GenericService<T, ID> {

	public abstract GenericDao<T, ID> getDao();

}
