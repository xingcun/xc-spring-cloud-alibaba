package com.xc.util;

import java.util.concurrent.TimeUnit;

import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.stereotype.Component;

@Component
public class CacheFactory {
	
	private static CacheFactory cacheFactory;
	
	public CacheFactory(){
		cacheFactory = this;
	}
	/**
	 * 使用同一个数据网格
	 */
	private  String  cacheGrid = "service-cache-grid";
	
	public IgniteCache getCache(String cacheName) {
		Ignite ignite = getIgnite();
		IgniteCache namedCache = ignite.cache(cacheName);
		if(namedCache == null){
			CacheConfiguration cacheCfg = new CacheConfiguration(cacheName);
			cacheCfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
			cacheCfg.setCacheMode(CacheMode.REPLICATED);
			cacheCfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.PRIMARY_SYNC);
			ignite.addCacheConfiguration(cacheCfg);
			namedCache = ignite.getOrCreateCache(cacheName);
		}
		namedCache.withExpiryPolicy(new AccessedExpiryPolicy(new Duration(TimeUnit.DAYS, 2)));
		return namedCache;
	}
	
	public Ignite getIgnite(){
		return Ignition.ignite(cacheGrid);
	}
	
	public synchronized static CacheFactory getInstance(){
		if(cacheFactory==null){
			cacheFactory = new CacheFactory();
		}
		return cacheFactory;
	}
	
	public IgniteCache<String,Object> getLockCache(){
		return getCache("CAHCE-LOCK-DISTRIBUTED");
	}
}
