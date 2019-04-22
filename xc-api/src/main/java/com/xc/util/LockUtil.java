package com.xc.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class LockUtil {
	ThreadLocal<Lock> lockLocal = new ThreadLocal<Lock>();

	private static Map<String, Long> map = new ConcurrentHashMap<String, Long>();

	private static Thread checkThread = null;

	private String lockName;

	public LockUtil(String lockName) {
		this.lockName = lockName;
	}

	public boolean lock(Long time,TimeUnit unit) {
		Lock lock = CacheFactory.getInstance().getLockCache().lock(lockName);
		boolean flag = false;
		if(time!=null && unit!=null) {
			try {
				flag = lock.tryLock(time, unit);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else {
			flag = lock.tryLock();
		}
		if (flag) {
			lockLocal.set(lock);
			map.put(lockName, System.currentTimeMillis() / 1000);
			if (checkThread == null) {
				checkThread = new Thread(() -> {
					do {
						try {
							Set<Entry<String, Long>> set = map.entrySet();
							set.forEach(e ->{
								Long runTime = e.getValue();
								if (runTime != null && System.currentTimeMillis() / 1000 - runTime > 10) {
									System.out.println(e.getKey() + "同步锁超时................");
								}
							});
							Thread.sleep(1000L);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} while (checkThread != null && !checkThread.isInterrupted());
				});
				if (checkThread != null) {
					checkThread.start();
				}
			}

		}
		return flag;
	}
	
	
	public boolean lock() {
		boolean flag = lock(null, null);
		return flag;
	}

	public boolean unlock() {
		try {
			lockLocal.get().unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.remove(lockName);
		return true;
	}

}
