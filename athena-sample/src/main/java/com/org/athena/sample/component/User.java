package com.org.athena.sample.component;

import com.org.athena.sample.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rabbit.open.athena.client.Traceable;
import rabbit.open.athena.client.context.ContextManager;
import rabbit.open.athena.client.trace.TraceInfo;

import javax.annotation.PostConstruct;

@Component
public class User {

    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    UserService userService;

    @PostConstruct
    @Traceable(name = "init")
    public void init() {
        TraceInfo info = ContextManager.getTraceInfo();
        logger.info("name:{}, traceId: {}, depth: {}, appName: {}, executeOrder: {}", info.getName(),
                info.getTraceId(), info.getDepth(), info.getAppName(), info.getExecuteOrder());
        System.out.println(userService.getUser());
        System.out.println(userService.getUserName());
    }

}
