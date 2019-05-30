package com.xc.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogIpConfig extends ClassicConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogIpConfig.class);

	private static String ip;
	@Override
	public String convert(ILoggingEvent event) {
		try {
			if(ip==null ) {
				ip =InetAddress.getLocalHost().getHostName()+":"+InetAddress.getLocalHost().getHostAddress();
			}
			return ip;
		} catch (UnknownHostException e) {
			LOGGER.error("获取日志Ip异常", e);
		}
		return null;
	}
}