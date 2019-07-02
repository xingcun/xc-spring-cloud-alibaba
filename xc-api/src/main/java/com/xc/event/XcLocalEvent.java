package com.xc.event;

import org.springframework.context.ApplicationEvent;

public class XcLocalEvent extends ApplicationEvent {
    private String echoMessage;

    public XcLocalEvent(Object source, String echoMessage) {
        super(source);
        this.echoMessage = echoMessage;
    }

    public String getEchoMessage() {
        return echoMessage;
    }

    public void setEchoMessage(String echoMessage) {
        this.echoMessage = echoMessage;
    }
}
