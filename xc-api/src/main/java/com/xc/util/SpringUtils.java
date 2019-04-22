package com.xc.util;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public final class SpringUtils implements BeanFactoryPostProcessor {

    private static ConfigurableListableBeanFactory beanFactory; // Spring应用上下文环境
    
    private static Environment parent = new StandardEnvironment() {
	};
	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	@Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws org.springframework.beans.BeansException
     *
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 获取类型为requiredType的对象
     *
     * @param clz
     * @return
     * @throws org.springframework.beans.BeansException
     *
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        @SuppressWarnings("unchecked")
        T result = (T) beanFactory.getBean(clz);
        return result;
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @param name
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @param name
     * @return boolean
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     *
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }

    /**
     * @param name
     * @return Class 注册对象的类型
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     *
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     * @param name
     * @return
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     *
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);
    }

    
	/**
	 * 根据属性文件生成环境
	 * 
	 * @param propertiesFiles
	 * @return
	 */
	public static Environment getEnvironment(final String... propertiesFiles) {

		if (propertiesFiles == null) {
			throw new IllegalStateException(
					"propertiesFiles should not be null");
		}

		return new StandardEnvironment() {
			@Override
			public void customizePropertySources(
					MutablePropertySources propertySources) {
				for (int i = 0; i < propertiesFiles.length; i++) {
					String propertiesFile = propertiesFiles[i];
					Assert.notNull(propertiesFile,
							"propertiesFiles should not be contain a null value");
					propertiesFile = parent
							.resolveRequiredPlaceholders(propertiesFile);
					Resource resource = resourceLoader
							.getResource(propertiesFile);
					if (!resource.exists()) {
						throw new IllegalArgumentException(String.format(
								"The resource %s is not exist", propertiesFile));
					}
					try {
						propertySources.addLast(new ResourcePropertySource(
								resource.getFilename(), resource));
					} catch (IOException e) {
						System.out.println(e);
						//throw e;
					}
				}
				super.customizePropertySources(propertySources);
			}
		};
	}
}
