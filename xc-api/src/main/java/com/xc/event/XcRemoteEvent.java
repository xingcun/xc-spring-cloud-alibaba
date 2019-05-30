package com.xc.event;

import com.alibaba.fastjson.JSONObject;
import com.xc.vo.MessageVo;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

public class XcRemoteEvent extends RemoteApplicationEvent {

    private MessageVo messageVo;



    public XcRemoteEvent(){
        super();
    }

    public XcRemoteEvent(Object source, String originService, String destinationService) {
        super(source, originService,destinationService);
    }

    public MessageVo getMessageVo() {
        return messageVo;
    }

    public void setMessageVo(MessageVo messageVo) {
        this.messageVo = messageVo;
    }
}
