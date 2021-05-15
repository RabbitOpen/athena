package com.org.test.agent;

import rabbit.open.athena.plugin.common.ClassEnhancer;
import rabbit.open.athena.plugin.common.context.AgentContext;

import java.lang.reflect.Method;

public class SimpleClassEnhancer implements ClassEnhancer {

    @Override
    public void beforeMethod(AgentContext context, Method targetMethod, Object[] args) {

    }

    @Override
    public Object afterMethod(AgentContext context, Method targetMethod, Object[] args, Object result) {
        return null;
    }

    @Override
    public void onException(AgentContext context, Method targetMethod, Object[] args, Object result) {

    }
}
