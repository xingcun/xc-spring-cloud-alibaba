package com.xc.admin.control;

import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.xc.admin.feign.TestFeign;
import com.xc.service.TestCacosService;
import com.xc.service.TestService;
import com.xc.util.CacheFactory;
import com.xc.util.LockUtil;
import com.xc.vo.ModelVo;

@RestController
public class TestControl {

	@Reference
	private TestService testService;

	@Reference
	private TestCacosService testCacosService;

	@Autowired
	private TestFeign testFeign;

	@Autowired
	private ThreadPoolTaskExecutor executor;

	@RequestMapping(value = "/test")
	public JSONObject test(String msg) {
		System.out.println("------------------------------");
		CacheFactory.getInstance().getCache("test").put("msg", msg);
		JSONObject obj = new JSONObject();
		testService.test();
		testCacosService.send(msg);
		obj.put("status", 1);
		executor.execute(() -> {
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(
					"-----------------------------+========================executor+++++++++++++++++++++++++++++++++++++++++++");
		});
		return obj;
	}

	@RequestMapping(value = "/testFeign")
	public JSONObject testFeign(@RequestBody JSONObject obj) {
		System.out.println("admin cache msg:" + CacheFactory.getInstance().getCache("test").get("msg"));
		return testFeign.test(obj);
	}

	@RequestMapping(value = "/testLock")
	public ModelVo testLock(Long time) {
		ModelVo vo = new ModelVo();
		/*
		 * IgniteLock lock =
		 * CacheFactory.getInstance().getIgnite().reentrantLock("scheduleTaskTwo", true,
		 * true, true); if(lock.tryLock(2L, TimeUnit.SECONDS)) {
		 * System.out.println("tryLock:成功"); lock.unlock();
		 * System.out.println("unlock    000000kkkkkk"); }else {
		 * System.out.println("tryLock:失败"); }
		 * 
		 * try { if(time!=null) { Thread.sleep(time); } }catch (Exception e) {
		 * e.printStackTrace(); // TODO: handle exception } //lock.unlock();
		 * vo.getResult().put("lock", lock);
		 */
		/*
		 * Lock tLock =
		 * CacheFactory.getInstance().getLockCache().lock("scheduleTaskLock1");
		 * System.out.println("try locking ........"); boolean flag = tLock.tryLock();
		 * System.out.println("cache try lock:"+flag); if(flag) { try {
		 * Thread.sleep(3000L); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } tLock.unlock(); }
		 */

		LockUtil util = new LockUtil("scheduleTaskLock");
		boolean flag = util.lock();

		if (flag) {

			try {
				if (time != null) {
					Thread.sleep(time);
				}
			} catch (InterruptedException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		util.unlock();

		vo.getResult().put("tLock", flag);

		return vo;

	}

	@RequestMapping(value = "/testLock2")
	public ModelVo testLock2(Long time) {
		ModelVo vo = new ModelVo();
		System.out.println("start get lock ........");
		/*
		 * GridCacheLockImpl lock =
		 * (GridCacheLockImpl)CacheFactory.getInstance().getIgnite().reentrantLock(
		 * "scheduleTaskTwo", true, true, true);
		 * System.out.println("try locking ........"); boolean flag = lock.tryLock();
		 * System.out.println("tryLock:"+flag); if(flag) { try {
		 * Thread.currentThread().sleep(5000L); } catch (InterruptedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } lock.unlock();
		 * System.out.println("unlock"); }
		 * 
		 * vo.getResult().put("lock", flag);
		 */

		Lock tLock = CacheFactory.getInstance().getLockCache().lock("scheduleTaskLock");
		System.out.println("try cache locking ........");
		boolean flag = false;
		flag = tLock.tryLock();
		System.out.println("cache try lock:" + flag);

		if (flag) {
			CacheFactory.getInstance().getLockCache().put("currentThread", Thread.currentThread());
			CacheFactory.getInstance().getLockCache().put("currentLock", tLock);
			tLock.unlock();
		} else {
			/*
			 * try { System.out.println("存在其它超时锁,针对其它的锁进行解锁。。。。。。。。。");
			 * ((Lock)CacheFactory.getInstance().getLockCache().get("currentLock")).unlock()
			 * ; }catch (Exception e) { e.printStackTrace(); }
			 * System.out.println("存在其它超时锁线程,针对其进行中断");
			 * ((Thread)CacheFactory.getInstance().getLockCache().get("currentThread")).
			 * interrupt();
			 */
		}
		vo.getResult().put("tLock", flag);

		return vo;
	}

}
