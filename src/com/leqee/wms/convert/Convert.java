package com.leqee.wms.convert;

import java.util.List;

/**
 * 将源、目标对象互相转化器 
 * @author qyyao
 * @param <S>
 * @param <T>
 */
public interface Convert<S, T > {
	/**
	 * 将源对象转化为目标对象
	 * @param s
	 * @return
	 */
	T covertToTargetEntity(S s);
	
	/**
	 * 批量将源对象转化为目标对象
	 * @param ses
	 * @return
	 */
	List<T> covertToTargetEntity(List<S> ses);
	
	
	/**
	 * 将目标对象转化为源对象
	 * @param t
	 * @return
	 */
	S covertToSourceEntity(T t);

	/**
	 * 批量将目标对象转化为源对象
	 * @param ts
	 * @return
	 */
	List<S> covertToSourceEntity(List<T> ts);
}
