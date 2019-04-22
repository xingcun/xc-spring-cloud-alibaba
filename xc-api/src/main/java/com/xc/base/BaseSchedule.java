package com.xc.base;

import com.xc.util.CacheFactory;
import com.xc.util.LockUtil;

public abstract class BaseSchedule {

	/**
	 * 用于启动任务,调用start
	 */
	public abstract void schedule();
	
	public void start(String name) {
		Long time = (Long) CacheFactory.getInstance().getLockCache().get(name);
		if(time!=null && System.currentTimeMillis()/1000-time<2) {
			/**
			 * 此处保证运行时间以秒为单位
			 */
			return;
		}
		LockUtil util = new LockUtil(name);
		boolean flag = util.lock();

		if (flag) {
			 CacheFactory.getInstance().getLockCache().put(name, System.currentTimeMillis()/1000);
			try {
				doRun();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				util.unlock();
			}
		}
	}

	/**
	 * 用于执行任务内容
	 */
	public abstract void doRun();
}
