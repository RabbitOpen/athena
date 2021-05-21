package com.org.athena.test.trace;

import rabbit.open.athena.plugin.common.context.AbstractMethodEnhancer;

import java.lang.reflect.Method;

/**
 * 执行耗时增强(MetricClassEnhancer 自带耗时统计增强，这里只是为了做个测试)
 */
public class ExecuteCostMethodEnhancer extends AbstractMethodEnhancer<ExecuteCostTraceInfo> {

    @Override
    protected void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args, ExecuteCostTraceInfo traceInfo) {
    }

}
