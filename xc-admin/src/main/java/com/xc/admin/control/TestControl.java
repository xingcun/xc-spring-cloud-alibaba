package com.xc.admin.control;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import com.alibaba.fastjson.JSON;
import com.xc.admin.feign.TestFeignFactory;
import com.xc.control.BaseQuartzControl;
import com.xc.event.XcLocalEvent;
import com.xc.event.XcRemoteEvent;
import com.xc.vo.BaseModelVo;
import com.xc.vo.MessageVo;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.apache.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.xc.admin.feign.TestFeign;
import com.xc.service.TestService;
import com.xc.util.CacheFactory;
import com.xc.util.LockUtil;
import com.xc.vo.ModelVo;

@RestController
public class TestControl extends BaseQuartzControl {

	private static final Logger log = LoggerFactory.getLogger(TestControl.class);
	@Reference
	private TestService testService;


	@Autowired
	private TestFeign testFeign;

	@Autowired
	private TestFeignFactory testFeignFactory;

	@Autowired
	private ThreadPoolTaskExecutor executor;

	/**
	 * 测试dubbo连接
	 * @param msg
	 * @return
	 */
	@RequestMapping(value = "/testDubbo")
	public ModelVo testDubbo(String msg){
		ModelVo vo = new ModelVo();
		vo.setCode(BaseModelVo.Code.SUCCESS);
		vo.getResult().put("msg",msg);
		testService.testDubbo(vo.getResult());
		return vo;
	}

	/**
	 * 检测ignite cache
	 * @param msg
	 * @return
	 */
	@RequestMapping(value = "/testCache")
	public JSONObject testCache(String msg) {
		CacheFactory.getInstance().getTestCache().put("msg", msg);
		JSONObject obj = new JSONObject();
		obj.put("status", 1);
		testService.getServiceCache("msg");
		obj.put("cache",CacheFactory.getInstance().getTestCache().get("msg"));
		System.out.println("admin cache :" + obj.getString("cache"));

		return obj;
	}

	/**
	 * 测试线程池执行
	 * @param msg
	 * @return
	 */
	@RequestMapping(value = "/testExecutor")
	public ModelVo testExecutor(String msg) {
		executor.execute(() -> {
			System.out.println("-----------------------------+========================executor+++++++++++++++++++++++++++++++++++++++++++"+msg);
		});
		ModelVo vo = new ModelVo();
		vo.setCode(BaseModelVo.Code.SUCCESS);
		return vo;
	}

	@RequestMapping(value = "/testFeign")
	public JSONObject testFeign(String msg) {
		System.out.println("admin testFeign msg:" +msg);
		return testFeign.testHystrix(msg);
	}

	@RequestMapping(value = "/testFeignFactory")
	public JSONObject testFeignFactory(String msg) {
		System.out.println("admin testFeignFactory msg:" +msg);
		return testFeignFactory.testHystrixFactory(msg);
	}



	@RequestMapping(value = "/testLock")
	public ModelVo testLock(Long time) {
		ModelVo vo = new ModelVo();


		LockUtil util = new LockUtil("testLock");
		boolean flag = util.lock(3L, TimeUnit.SECONDS);

		if (flag) {

			try {
				if (time != null) {
					Thread.sleep(time);
				}
			} catch (InterruptedException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
			util.unlock();
		}



		vo.getResult().put("tLock", flag);

		return vo;

	}

	@RequestMapping(value = "/testLock2")
	public ModelVo testLock2(Long time) {
		ModelVo vo = new ModelVo();


		LockUtil util = new LockUtil("testLock2");
		boolean flag = util.lock(3L, TimeUnit.SECONDS);

		if (flag) {

			try {
				if (time != null) {
					Thread.sleep(time);
				}
			} catch (InterruptedException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
			util.unlock();
		}



		vo.getResult().put("tLock2", flag);

		return vo;
	}


	@StreamListener(value = Sink.INPUT)
	public void receiveAll(Message message,
						   @Header(value = "type", required = false) String type,
						   @Header(value = "test", required = false) String test) {
		log.info(JSON.toJSONString(message.getHeaders()));
		log.info("receiveAll msgs, payload: {}, type header: {}, test header: {}",
				message.getPayload(), type, test);
	}

	@EventListener(classes = XcRemoteEvent.class)
	public void receiveEvent(XcRemoteEvent xcRemoteEvent) {
		MessageVo message = xcRemoteEvent.getMessageVo();
		log.info("{}- xc-admin -- get remoteEvent: {}", message.getSubject(), message.getContent());
	}


	@EventListener(classes = XcLocalEvent.class)
	public void receiveLocalEvent(XcLocalEvent xcLocalEvent) {
		log.info(" xc-admin -- get localEvent: {}",xcLocalEvent.getEchoMessage());
	}


	@RequestMapping(value = "/testRemoteEvent")
	public JSONObject testRemoteEvent() {
		System.out.println("admin-------------+++++++++++++++++++++-----------------testRemoteEvent");
		JSONObject obj = new JSONObject();
		testService.testRemoteEvent();
		obj.put("status", 1);
		return obj;
	}

	@RequestMapping(value = "/testLocalEvent")
	public JSONObject testLocalEvent(String msg) {
		System.out.println("admin-------------+++++++++++++++++++++-----------------testLocalEvent");
		JSONObject obj = new JSONObject();
		testService.testLocalEvent(msg);
		obj.put("status", 1);
		return obj;
	}

	@RequestMapping(value = "/testMqEvent")
	public JSONObject testMqEvent(String name, Integer age) {
		System.out.println("admin-------------+++++++++++++++++++++-----------------testMqEvent");
		JSONObject obj = new JSONObject();
		obj.put("message", testService.testMqEvent(name, age));
		obj.put("status", 1);
		return obj;
	}

	@RequestMapping(value = "/getCache")
	public JSONObject getCache() {
		JSONObject dbObj = new JSONObject();
		List<Ignite> ignites = Ignition.allGrids();
		for(Ignite ignite : ignites){
			JSONObject cacheObj = new JSONObject();

			ignite.cacheNames().forEach((key)->{

				cacheObj.put(key, JSON.toJSONString(ignite.cache(key)));
				ignite.cache(key).forEach((entry)->{
					System.out.println(entry.getKey()+"================"+ JSON.toJSONString(entry.getValue()));
				});
			});
			dbObj.put(ignite.name()+"",cacheObj);

		}
		return dbObj;
	}

	public void setTestService(TestService testService) {
		this.testService = testService;
	}

	public void setTestFeign(TestFeign testFeign) {
		this.testFeign = testFeign;
	}

	public void setTestFeignFactory(TestFeignFactory testFeignFactory) {
		this.testFeignFactory = testFeignFactory;
	}

	public void setExecutor(ThreadPoolTaskExecutor executor) {
		this.executor = executor;
	}
}
