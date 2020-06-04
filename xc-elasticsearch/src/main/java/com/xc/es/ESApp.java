package com.xc.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ESApp {
    public static void main(String[] args) throws Exception{
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(ESApp.class,args);
    }
}
