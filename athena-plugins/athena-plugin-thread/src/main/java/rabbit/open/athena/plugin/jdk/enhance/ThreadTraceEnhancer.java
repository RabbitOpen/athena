package rabbit.open.athena.plugin.jdk.enhance;

import rabbit.open.athena.client.context.ContextManager;
import rabbit.open.athena.client.trace.TraceInfo;
import rabbit.open.athena.client.wrapper.AbstractWrapper;
import rabbit.open.athena.client.wrapper.RunnableWrapper;
import rabbit.open.athena.plugin.common.AbstractEnhancer;
import rabbit.open.athena.plugin.common.SafeRunner;
import rabbit.open.athena.plugin.common.context.PluginContext;

import java.lang.reflect.Method;

public class ThreadTraceEnhancer implements AbstractEnhancer {

    @Override
    public void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args) {
        // TO DO: ignore
    }

    @Override
    public Object afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result) {
        AbstractWrapper wrapper = (AbstractWrapper) objectEnhanced;
        wrapper.setRealObject(args[0]);
        setPreHandler(objectEnhanced, wrapper);
        setPostHandler(wrapper);
        setErrorHandler(wrapper);
        return null;
    }

    /**
     * 设置run/call前置处理
     * @param objectEnhanced
     * @param wrapper
     */
    private void setPreHandler(Object objectEnhanced, AbstractWrapper wrapper) {
        boolean opened = ContextManager.isOpen();
        TraceInfo parent = ContextManager.getTraceInfo();
        wrapper.setBefore(() -> SafeRunner.handle(() -> {
            TraceInfo traceInfo = new TraceInfo() {};
            traceInfo.setAppName(PluginContext.getContext().getMetaData().getApplicationName());
            traceInfo.setThreadName(Thread.currentThread().getName());
            String methodName = wrapper instanceof RunnableWrapper ? "run" : "call";
            traceInfo.setFullMethodName(methodName);
            traceInfo.setName(methodName);
            traceInfo.setTargetClzName(objectEnhanced.getClass().getName());
            traceInfo.setStart(System.currentTimeMillis());
            if (opened) {
                traceInfo.setTraceId(parent.getTraceId());
                traceInfo.setRoot(true);
                traceInfo.setDepth(parent.getDepth() + 1);
                traceInfo.setExecuteOrder(0);
            }
            ContextManager.open(traceInfo);
            ContextManager.setTraceInfo(traceInfo);
        }));
    }

    /**
     * 设置run/call后置处理
     * @param wrapper
     */
    private void setPostHandler(AbstractWrapper wrapper) {
        wrapper.setAfter(() -> SafeRunner.handle(() -> {
            TraceInfo traceInfo = ContextManager.getTraceInfo();
            try {
                traceInfo.setEnd(System.currentTimeMillis());
                ContextManager.setTraceInfo(traceInfo.getParent());
                PluginContext.getContext().getOrInitCollector().doCollection(traceInfo);
            } finally {
                ContextManager.close(traceInfo);
            }
        }));
    }

    /**
     * 设置run/call异常处理
     * @param wrapper
     */
    private void setErrorHandler(AbstractWrapper wrapper) {
        wrapper.setError(e -> SafeRunner.handle(() -> {
            TraceInfo traceInfo = ContextManager.getTraceInfo();
            try {
                traceInfo.setEnd(System.currentTimeMillis());
                traceInfo.setExceptionOccurred(true);
                traceInfo.setErrMsg(e.getMessage());
                ContextManager.setTraceInfo(traceInfo.getParent());
                PluginContext.getContext().getOrInitCollector().doCollection(traceInfo);
            } finally {
                ContextManager.close(traceInfo);
            }
        }));
    }

    @Override
    public void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t) {
        // to do : ignore
    }

}
