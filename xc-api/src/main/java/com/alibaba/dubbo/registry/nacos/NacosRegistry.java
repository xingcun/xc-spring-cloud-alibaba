/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.registry.nacos;

import static java.lang.Long.getLong;
import static java.lang.System.getProperty;
import static org.apache.dubbo.common.Constants.CONFIGURATORS_CATEGORY;
import static org.apache.dubbo.common.Constants.CONSUMERS_CATEGORY;
import static org.apache.dubbo.common.Constants.PROVIDERS_CATEGORY;
import static org.apache.dubbo.common.Constants.ROUTERS_CATEGORY;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.common.utils.UrlUtils;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.support.FailbackRegistry;
import org.apache.ignite.internal.util.typedef.internal.S;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;

/**
 * Nacos {@link Registry}
 *
 * @since 2.6.6
 */
public class NacosRegistry extends FailbackRegistry {
	/**
	 * The interval in second of lookup service names(only for Dubbo-OPS)
	 */
	private static final long LOOKUP_INTERVAL = getLong("dubbo.service.names.lookup.interval", 30);
	/**
	 * All supported categories
	 */
	private static final String[] ALL_SUPPORTED_CATEGORIES = of(PROVIDERS_CATEGORY, CONSUMERS_CATEGORY,
			ROUTERS_CATEGORY, CONFIGURATORS_CATEGORY);
	/**
	 * The pagination size of query for Nacos service names(only for Dubbo-OPS)
	 */
	private static final int PAGINATION_SIZE = Integer.getInteger("nacos.service.names.pagination.size", 100);

	private final NamingService namingService;

	private final ConcurrentMap<String, EventListener> nacosListeners;

	/**
	 * The separator for service name
	 */
	private static final String SERVICE_NAME_SEPARATOR = getProperty("dubbo.service.name.separator", ":");

	/**
	 * {@link ScheduledExecutorService} lookup service names(only for Dubbo-OPS)
	 */
	private volatile ScheduledExecutorService serviceNamesScheduler;

	private static final int CATEGORY_INDEX = 0;

	private static final int SERVICE_INTERFACE_INDEX = CATEGORY_INDEX + 1;

	private static final int SERVICE_VERSION_INDEX = SERVICE_INTERFACE_INDEX + 1;

	private static final int SERVICE_GROUP_INDEX = SERVICE_VERSION_INDEX + 1;

	private static final String WILDCARD = "*";

	public NacosRegistry(URL url, NamingService namingService) {
		super(url);
		this.namingService = namingService;
		this.nacosListeners = new ConcurrentHashMap<String, EventListener>();
	}

	@Override
	public void doRegister(URL url) {
		Instance instance = toServiceInstance(url);

		execute(new NamingServiceCallback() {
			@Override
			public void callback(NamingService namingService) throws NacosException {
				namingService.registerInstance(instance.getServiceName(), instance);
			}
		});

	}

	@Override
	public void doUnregister(URL url) {
		Instance instance = toServiceInstance(url);
		execute(new NamingServiceCallback() {
			@Override
			public void callback(NamingService namingService) throws NacosException {
				namingService.deregisterInstance(instance.getServiceName(), instance.getIp(), instance.getPort());
			}
		});

	}

	@Override
	public void doSubscribe(URL url, NotifyListener listener) {
		Set<String> serviceNames = getServiceNames(url, listener);
		doSubscribe(url, listener, serviceNames);
	}

	@Override
	public void doUnsubscribe(URL url, NotifyListener listener) {
		if (isAdminProtocol(url)) {
			shutdownServiceNamesLookup();
		}

	}

	@Override
	public boolean isAvailable() {
		return "UP".equals(namingService.getServerStatus());
	}

	private void shutdownServiceNamesLookup() {
		if (serviceNamesScheduler != null) {
			serviceNamesScheduler.shutdown();
		}
	}

	protected Instance toServiceInstance(URL url) {
		Instance instance = new Instance();
		instance.setServiceName(getServiceName(url));

		// Append default category if absent
		String category = url.getParameter(Constants.CATEGORY_KEY, Constants.DEFAULT_CATEGORY);
		URL newURL = url.addParameter(Constants.CATEGORY_KEY, category);
		newURL = newURL.addParameter(Constants.PROTOCOL_KEY, url.getProtocol());
		String ip = url.getHost();
		int port = url.getPort();

		instance.setIp(ip);
		instance.setPort(port);
		instance.setMetadata(new LinkedHashMap<String, String>(newURL.getParameters()));
		return instance;
	}

	/**
	 * Get the service name
	 *
	 * @param url {@link URL}
	 * @return non-null
	 */
	public static String getServiceName(URL url) {
		String category = url.getParameter(Constants.CATEGORY_KEY, Constants.DEFAULT_CATEGORY);
		return getServiceName(url, category);
	}

	private static String getServiceName(URL url, String category) {
		StringBuilder serviceNameBuilder = new StringBuilder(category);
		appendIfPresent(serviceNameBuilder, url, Constants.INTERFACE_KEY);
		appendIfPresent(serviceNameBuilder, url, Constants.VERSION_KEY);
		appendIfPresent(serviceNameBuilder, url, Constants.GROUP_KEY);
		return serviceNameBuilder.toString();
	}

	private static void appendIfPresent(StringBuilder target, URL url, String parameterName) {
		String parameterValue = url.getParameter(parameterName);
		appendIfPresent(target, parameterValue);
	}

	private static void appendIfPresent(StringBuilder target, String parameterValue) {
		if (StringUtils.isNotEmpty(parameterValue)) {
			target.append(SERVICE_NAME_SEPARATOR).append(parameterValue);
		}
	}

	/**
	 * Get the subscribed service names from the specified {@link URL url}
	 *
	 * @param url      {@link URL}
	 * @param listener {@link NotifyListener}
	 * @return non-null
	 */
	private Set<String> getServiceNames(URL url, NotifyListener listener) {
		if (isAdminProtocol(url)) {
			scheduleServiceNamesLookup(url, listener);
			return getSubscribedServiceNamesForOps(url);
		} else {
			return getServiceNames(url);
		}
	}

	private Set<String> getServiceNames(URL url) {
		String[] categories = getCategories(url);
		Set<String> serviceNames = new LinkedHashSet<String>(categories.length);
		for (String category : categories) {
			final String serviceName = getServiceName(url, category);
			serviceNames.add(serviceName);
		}
		return serviceNames;
	}

	/**
	 * Get the categories from {@link URL}
	 *
	 * @param url {@link URL}
	 * @return non-null array
	 */
	private String[] getCategories(URL url) {
		return Constants.ANY_VALUE.equals(url.getServiceInterface()) ? ALL_SUPPORTED_CATEGORIES
				: of(Constants.DEFAULT_CATEGORY);
	}

	private boolean isAdminProtocol(URL url) {
		return Constants.ADMIN_PROTOCOL.equals(url.getProtocol());
	}

	private void scheduleServiceNamesLookup(final URL url, final NotifyListener listener) {
		if (serviceNamesScheduler == null) {
			serviceNamesScheduler = Executors.newSingleThreadScheduledExecutor();
			serviceNamesScheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					Set<String> serviceNames = findAllServiceNames();
					filter(serviceNames, new Filter<String>() {
						@Override
						public boolean accept(String serviceName) {
							boolean accepted = false;
							for (String category : ALL_SUPPORTED_CATEGORIES) {
								String prefix = category + SERVICE_NAME_SEPARATOR;
								if (serviceName.startsWith(prefix)) {
									accepted = true;
									break;
								}
							}
							return accepted;
						}
					});
					doSubscribe(url, listener, serviceNames);
				}
			}, LOOKUP_INTERVAL, LOOKUP_INTERVAL, TimeUnit.SECONDS);
		}
	}

	/**
	 * Get the service names for Dubbo OPS
	 *
	 * @param url {@link URL}
	 * @return non-null
	 */
	private Set<String> getSubscribedServiceNamesForOps(URL url) {
		Set<String> serviceNames = findAllServiceNames();
		filterServiceNames(serviceNames, url);
		return serviceNames;
	}

	private <T> void filter(Collection<T> collection, Filter<T> filter) {
		Iterator<T> iterator = collection.iterator();
		while (iterator.hasNext()) {
			T data = iterator.next();
			if (!filter.accept(data)) { // remove if not accept
				iterator.remove();
			}
		}
	}

	private void filterServiceNames(Set<String> serviceNames, URL url) {

		final String[] categories = getCategories(url);

		final String targetServiceInterface = url.getServiceInterface();

		final String targetVersion = url.getParameter(Constants.VERSION_KEY);

		final String targetGroup = url.getParameter(Constants.GROUP_KEY);

		filter(serviceNames, new Filter<String>() {
			@Override
			public boolean accept(String serviceName) {
				// split service name to segments
				// (required) segments[0] = category
				// (required) segments[1] = serviceInterface
				// (required) segments[2] = version
				// (optional) segments[3] = group
				String[] segments = getServiceSegments(serviceName);
				int length = segments.length;
				if (length < 4) { // must present 4 segments or more
					return false;
				}

				String category = getCategory(segments);
				if (Arrays.binarySearch(categories, category) > -1) { // no match category
					return false;
				}

				String serviceInterface = getServiceInterface(segments);
				if (!WILDCARD.equals(targetServiceInterface)
						&& !StringUtils.isEquals(targetServiceInterface, serviceInterface)) { // no match interface
					return false;
				}

				String version = getServiceVersion(segments);
				if (!WILDCARD.equals(targetVersion) && !StringUtils.isEquals(targetVersion, version)) { // no match
																										// service
					// version
					return false;
				}

				String group = getServiceGroup(segments);
				if (group != null && !WILDCARD.equals(targetGroup) && !StringUtils.isEquals(targetGroup, group)) { // no
																													// match
																													// service
					// group
					return false;
				}

				return true;
			}
		});
	}

	public static String getServiceInterface(String[] segments) {
		return segments[SERVICE_INTERFACE_INDEX];
	}

	public static String getServiceVersion(String[] segments) {
		return segments[SERVICE_VERSION_INDEX];
	}

	public static String getServiceGroup(String[] segments) {
		return segments.length > 4 ? segments[SERVICE_GROUP_INDEX] : null;
	}

	public static String getCategory(String[] segments) {
		return segments[CATEGORY_INDEX];
	}

	public static String[] getServiceSegments(String serviceName) {
		return serviceName.split(SERVICE_NAME_SEPARATOR);
	}

	private void execute(NamingServiceCallback callback) {
		try {
			callback.callback(namingService);
		} catch (NacosException e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getErrMsg(), e);
			}
		}
	}

	/**
	 * {@link NamingService} Callback
	 */
	interface NamingServiceCallback {

		/**
		 * Callback
		 *
		 * @param namingService {@link NamingService}
		 * @throws NacosException
		 */
		void callback(NamingService namingService) throws NacosException;

	}

	/**
	 * A filter
	 */
	private interface Filter<T> {

		/**
		 * Tests whether or not the specified data should be accepted.
		 *
		 * @param data The data to be tested
		 * @return <code>true</code> if and only if <code>data</code> should be accepted
		 */
		boolean accept(T data);

	}

	private static <T> T[] of(T... values) {
		return values;
	}

	protected Set<String> findAllServiceNames() {
		final Set<String> serviceNames = new LinkedHashSet<String>();

		execute(new NamingServiceCallback() {
			@Override
			public void callback(NamingService namingService) throws NacosException {

				int pageIndex = 1;
				ListView<String> listView = namingService.getServicesOfServer(pageIndex, PAGINATION_SIZE);
				// First page data
				List<String> firstPageData = listView.getData();
				// Append first page into list
				serviceNames.addAll(firstPageData);
				// the total count
				int count = listView.getCount();
				// the number of pages
				int pageNumbers = count / PAGINATION_SIZE;
				int remainder = count % PAGINATION_SIZE;
				// remain
				if (remainder > 0) {
					pageNumbers += 1;
				}
				// If more than 1 page
				while (pageIndex < pageNumbers) {
					listView = namingService.getServicesOfServer(++pageIndex, PAGINATION_SIZE);
					serviceNames.addAll(listView.getData());
				}

			}
		});

		return serviceNames;
	}

	private void doSubscribe(final URL url, final NotifyListener listener, final Set<String> serviceNames) {
		Collection<Instance> serviceInstances = new LinkedList<Instance>();

		for (String serviceName : serviceNames) {
			serviceInstances.addAll(findServiceInstances(serviceName));
		}
		notifySubscriber(url, listener, serviceInstances);
	}

	protected Collection<Instance> findServiceInstances(final String serviceName) {
		final Collection<Instance> instances = new LinkedList<Instance>();
		execute(new NamingServiceCallback() {
			@Override
			public void callback(NamingService namingService) throws NacosException {
				instances.addAll(namingService.getAllInstances(serviceName));
			}
		});
		return instances;
	}

	/**
	 * Notify the Healthy {@link DubboRegistration service instance} to subscriber.
	 *
	 * @param url              {@link URL}
	 * @param listener         {@link NotifyListener}
	 * @param serviceInstances all {@link S registrations}
	 */
	private void notifySubscriber(URL url, NotifyListener listener, Collection<Instance> serviceInstances) {
		Set<Instance> healthyServiceInstances = new LinkedHashSet<Instance>(serviceInstances);
		// Healthy Instances
		filterHealthyInstances(healthyServiceInstances);
		List<URL> urls = buildURLs(url, healthyServiceInstances);
		this.notify(url, listener, urls);
	}

	private List<URL> buildURLs(URL consumerURL, Collection<Instance> serviceInstances) {
		if (serviceInstances.isEmpty()) {
			return Collections.emptyList();
		}
		List<URL> urls = new LinkedList<URL>();
		for (Instance serviceInstance : serviceInstances) {
			URL url = new URL(serviceInstance.getMetadata().get(Constants.PROTOCOL_KEY), serviceInstance.getIp(),
					serviceInstance.getPort(), serviceInstance.getMetadata());
			if (UrlUtils.isMatch(consumerURL, url)) {
				urls.add(url);
			}
		}
		return urls;
	}

	private void filterHealthyInstances(Collection<Instance> serviceInstances) {
		filter(serviceInstances, new Filter<Instance>() {
			@Override
			public boolean accept(Instance serviceInstance) {
				return filterHealthyRegistration(serviceInstance);
			}
		});
	}

	protected boolean filterHealthyRegistration(Instance serviceInstance) {
		return serviceInstance.isEnabled();
	}
}
