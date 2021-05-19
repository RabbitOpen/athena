package com.org.athena.test.trace;

import rabbit.open.athena.plugin.common.context.AbstractMethodEnhancer;

import java.lang.reflect.Method;

/**
 * 执行耗时增强(MetricClassEnhancer 自带耗时统计增强，这里只是为了做个测试)
 */
public class ExecuteCostMethodEnhancer extends AbstractMethodEnhancer<ExecuteCostTraceInfo> {

    @Override
    protected ExecuteCostTraceInfo newTraceInfo() {
        return new ExecuteCostTraceInfo();
    }

    @Override
    protected void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args, ExecuteCostTraceInfo traceInfo) {
    }

    @Override
    protected void afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result, ExecuteCostTraceInfo traceInfo) {
    }

    @Override
    protected void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t, ExecuteCostTraceInfo traceInfo) {
    }
}
