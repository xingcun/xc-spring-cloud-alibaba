package com.xc.admin.schedule;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xc.base.BaseSchedule;
import com.xc.util.CommonUtil;

@Component
public class TestSchedule extends BaseSchedule {
	
	@Scheduled(cron = "0/5 * * * * ?" ) 
	@Override
	public void schedule() {
		start("TestSchedule");
	}
	
	@Override
	public void doRun() {
		System.out.println("执行任务:"+CommonUtil.formatLongDate(new Date()));
	}
	


}
