package com.ht.project.util;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LogUtil extends ClassicConverter {
    private static final Logger LOGGER =getLogger(LogUtil.class);

    private static String ip;
    @Override
    public String convert(ILoggingEvent event) {
        try {
            if(ip==null ) {
                ip = InetAddress.getLocalHost().getHostName()+":"+InetAddress.getLocalHost().getHostAddress();
            }
            return ip;
        } catch (UnknownHostException e) {
            LOGGER.error("获取日志Ip异常", e);
        }
        return null;
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

}
