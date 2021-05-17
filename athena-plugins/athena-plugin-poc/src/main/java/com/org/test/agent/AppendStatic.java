package com.org.test.agent;

import rabbit.open.athena.plugin.common.ClassEnhancer;

import java.lang.reflect.Method;

public class AppendStatic implements ClassEnhancer {

    @Override
    public void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args) {

    }

    @Override
    public Object afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result) {
        return result + "-static";
    }

    @Override
    public void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t) {

    }
}
