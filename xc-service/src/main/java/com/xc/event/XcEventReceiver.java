package com.xc.event;

import com.xc.vo.MessageVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class XcEventReceiver {

    private static final Logger log = LoggerFactory.getLogger(XcEventReceiver.class);


    @EventListener(classes =XcRemoteEvent.class)
    public void receiveEvent(XcRemoteEvent xcRemoteEvent) {
        MessageVo message = xcRemoteEvent.getMessageVo();
        log.info("{}- xc-service -- get remoteEvent: {}", message.getSubject(), message.getContent());
    }

    @EventListener(classes =XcLocalEvent.class)
    public void receiveLocalEvent(XcLocalEvent xcLocalEvent) {
        log.info(" xc-service -- get localEvent: {}",xcLocalEvent.getEchoMessage());
    }
}
