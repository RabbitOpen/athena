package com.org.test.agent;

import rabbit.open.athena.plugin.common.ClassEnhancer;
import rabbit.open.athena.plugin.common.context.EnhancedObject;

import java.lang.reflect.Method;

public class AppendHello implements ClassEnhancer {

    @Override
    public void beforeMethod(EnhancedObject enhancedObj, Method targetMethod, Object[] args) {

    }

    @Override
    public Object afterMethod(EnhancedObject enhancedObj, Method targetMethod, Object[] args, Object result) {
        return result + "-hello";
    }

    @Override
    public void onException(EnhancedObject enhancedObj, Method targetMethod, Object[] args, Object result) {

    }
}
