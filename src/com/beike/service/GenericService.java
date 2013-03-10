/**
 * 
 */
package com.beike.service;

import java.io.Serializable;

public interface GenericService<T, ID extends Serializable> {

	T findById(ID id);

}
