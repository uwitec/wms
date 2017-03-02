package com.leqee.wms.util;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 获取排他锁和读写锁的公共方法
 * @author qyyao
 * @date 2016-3-15
 * @version 1.0
 */
public class LockUtil {

	// 创建读写锁和排它锁时的同步对象
	private final static byte[] createReadWriteSyncLock = new byte[0];
	private final static byte[] createReentrantSyncLock = new byte[0];
	// 创建读写锁和排他锁的Map
	private final static HashMap<String, ReentrantReadWriteLock> readWriteLocks = new HashMap<String, ReentrantReadWriteLock>();
	private final static HashMap<String, ReentrantLock> reentrantLocks = new HashMap<String, ReentrantLock>();

	
	/**
	 * 获取lockKey对应的排它锁，没有时会创建一个
	 * @param lockKey
	 * @return
	 */
	public static ReentrantLock getReentrantLock(String lockKey ) {
		String key = lockKey ;
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(key.getBytes());
			byte[] result = md.digest();
			key = new String(result, 0, 16);
		} catch (java.security.NoSuchAlgorithmException e) {
			key = lockKey ;
		}

		ReentrantLock lock = reentrantLocks.get(key);
		if (lock == null) {
			//创建锁时需要加锁
			synchronized (createReentrantSyncLock) {
				lock = reentrantLocks.get(key);
				if (lock == null) {
					lock = new ReentrantLock();
					reentrantLocks.put(key, lock);
				}
			}
		}
		return lock;
	}
	
	
	/**
	 * 获取lockKey对应的读写锁，没有时会创建一个
	 * @param lockKey
	 * @return
	 */
	public static ReentrantReadWriteLock getReadWriteLock(String lockKey ) {
		String key = lockKey ;
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(key.getBytes());
			byte[] result = md.digest();
			key = new String(result, 0, 16);
		} catch (java.security.NoSuchAlgorithmException e) {
			key = lockKey ;
		}
		
		ReentrantReadWriteLock lock = readWriteLocks.get(key);
		if (lock == null) {
			//创建锁时需要加锁
			synchronized (createReadWriteSyncLock) {
				lock = readWriteLocks.get(key);
				if (lock == null) {
					lock = new ReentrantReadWriteLock();
					readWriteLocks.put(key, lock);
				}
			}
		}
		return lock;
	}
	
	
	
	/**
	 * 调用锁[适用于group customer + method]
	 * @param groupId
	 * @param customerId
	 * @param methodName
	 */
	public static void lock(Integer groupId, Integer customerId,
			String methodName) {
		ReentrantReadWriteLock groupLock = LockUtil.getReadWriteLock("G" + groupId + methodName ); 
		ReentrantReadWriteLock customerLock = LockUtil.getReadWriteLock("C" + customerId + methodName); 
		
		if( customerId > 0 ){
			groupLock.readLock().lock();
			customerLock.writeLock().lock();
		}else {
			groupLock.writeLock().lock();
		}
	}
	
	
	/**
	 * 释放锁[适用于group customer + method]
	 * @param groupId
	 * @param customerId
	 * @param methodName
	 */
	public static void unlock(Integer groupId, Integer customerId,
			String methodName) {
		ReentrantReadWriteLock groupLock = LockUtil.getReadWriteLock("G" + groupId + methodName ); 
		ReentrantReadWriteLock customerLock = LockUtil.getReadWriteLock("C" + customerId + methodName); 
		
		if( customerId > 0 ){
			if( groupLock != null ){
				groupLock.readLock().unlock();
			}
			if( customerLock != null ){
				customerLock.writeLock().unlock();
			}
		}else {
			if( groupLock != null ){
				groupLock.writeLock().unlock();
			}
		}
	}
	
	
	/**
	 * 调用锁[适用于shippingAppId + method]
	 * @param shippingAppId
	 * @param methodName
	 */
	public static void lock(Integer shippingAppId,String methodName) {
		ReentrantReadWriteLock shippingAppLock = LockUtil.getReadWriteLock("G" + shippingAppId + methodName ); 
		
		if( shippingAppId > 0 ){
			shippingAppLock.readLock().lock();
		}else {
			shippingAppLock.writeLock().lock();
		}
	}
	
	
	/**
	 * 释放锁[适用于shippingAppId + method]
	 * @param shippingAppId
	 * @param methodName
	 */
	public static void unlock(Integer shippingAppId,String methodName) {
		ReentrantReadWriteLock shippingAppLock = LockUtil.getReadWriteLock("G" + shippingAppId + methodName ); 
		
		if( shippingAppId > 0 ){
			if( shippingAppLock != null ){
				shippingAppLock.readLock().unlock();
			}
		}else {
			if( shippingAppLock != null ){
				shippingAppLock.writeLock().unlock();
			}
		}
	}


	/**
	 * 调用锁[适用于physicalWarehouseId batchPickGroupIds + method]
	 * @param batchPickGroupIds
	 * @param customerId
	 * @param methodName
	 */
	public static void lock(Integer physicalWarehouseId, String batchPickGroupIds, String methodName) {
		ReentrantReadWriteLock batchPickGroupIdsLock = LockUtil.getReadWriteLock("G" + physicalWarehouseId + batchPickGroupIds + methodName ); 
		batchPickGroupIdsLock.writeLock().lock();
	}

	/**
	 * 释放锁[适用于physicalWarehouseId batchPickGroupIds + method]
	 * @param batchPickGroupIds
	 * @param customerId
	 * @param methodName
	 */
	public static void unlock(Integer physicalWarehouseId, String batchPickGroupIds, String methodName) {
		ReentrantReadWriteLock batchPickGroupIdsLock = LockUtil.getReadWriteLock("G" + physicalWarehouseId + batchPickGroupIds + methodName ); 
		batchPickGroupIdsLock.writeLock().unlock();
	}
	
	
}
