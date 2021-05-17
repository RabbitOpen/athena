package com.org.athena.test.trace;

import rabbit.open.athena.plugin.common.context.MetricClassEnhancer;

import java.lang.reflect.Method;

/**
 * 执行耗时增强
 */
public class ExecuteCostEnhancer extends MetricClassEnhancer<ExecuteCostTraceInfo> {

    @Override
    protected ExecuteCostTraceInfo newTraceInfo() {
        return new ExecuteCostTraceInfo();
    }

    @Override
    protected void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args, ExecuteCostTraceInfo traceInfo) {
        traceInfo.setStart(System.currentTimeMillis());
    }

    @Override
    protected void afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result, ExecuteCostTraceInfo traceInfo) {
        traceInfo.setEnd(System.currentTimeMillis());
    }

    @Override
    protected void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t, ExecuteCostTraceInfo traceInfo) {
        traceInfo.setEnd(System.currentTimeMillis());
    }
}
