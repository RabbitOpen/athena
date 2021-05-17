package rabbit.open.athena.plugin.common.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.plugin.common.ClassEnhancer;
import rabbit.open.athena.plugin.common.TraceInfo;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 抽象增强实现，记录trace info信息
 */
public abstract class MetricClassEnhancer<T extends TraceInfo> implements ClassEnhancer {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public final void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args) {
        safelyHandle(() -> {
            T traceInfo = newTraceInfo();
            traceInfo.setAppName(PluginContext.getContext().getMetaData().getApplicationName());
            traceInfo.setMethodName(targetMethod.getName() + "(" + type2Str(targetMethod.getParameterTypes()) +  ")");
            traceInfo.setSimpleMethodName(targetMethod.getName());
            traceInfo.setTargetClzName(targetMethod.getDeclaringClass().getName());
            if (!ContextManager.isOpen()) {
                traceInfo.setTraceId(Thread.currentThread().getName()  + " -- " + UUID.randomUUID().toString().replace("-", ""));
                traceInfo.setRoot(true);
                ContextManager.open(objectEnhanced, targetMethod, args);
            } else {
                TraceInfo parent = ContextManager.getTraceInfo();
                traceInfo.setParent(parent);
                if (null != parent) {
                    traceInfo.setTraceId(parent.getTraceId());
                    traceInfo.setDepth(parent.getDepth() + 1);
                    traceInfo.setExecuteOrder(parent.incrementChild());
                }
            }
            ContextManager.setTraceInfo(traceInfo);
            beforeMethod(objectEnhanced, targetMethod, args, traceInfo);
        });
    }

    private String type2Str(Class<?>[] types) {
        String str = "";
        for (int i = 0; i < types.length; i++) {
            str += types[i].getName();
            if (i != types.length - 1) {
                str += ", ";
            }
        }
        return str;
    }

    @Override
    public final Object afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result) {
        safelyHandle(() -> {
            try {
                TraceInfo traceInfo = ContextManager.getTraceInfo();
                afterMethod(objectEnhanced, targetMethod, args, result, (T) traceInfo);
                ContextManager.setTraceInfo(traceInfo.getParent());
                PluginContext.getContext().getOrInitCollector().doCollection(traceInfo);
            } finally {
                ContextManager.close(objectEnhanced, targetMethod, args);
            }
        });
        return result;
    }

    @Override
    public final void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t) {
        safelyHandle(() -> {
            try {
                TraceInfo traceInfo = ContextManager.getTraceInfo();
                traceInfo.setExceptionOccurred(true);
                onException(objectEnhanced, targetMethod, args, result, t, (T) traceInfo);
                ContextManager.setTraceInfo(traceInfo.getParent());
                PluginContext.getContext().getOrInitCollector().doCollection(traceInfo);
            } finally {
                ContextManager.close(objectEnhanced, targetMethod, args);
            }
        });
    }

    /**
     * 新建一个trace info
     * @return
     */
    protected abstract T newTraceInfo();

    /**
     * 增强前置处理
     * @param objectEnhanced
     * @param targetMethod
     * @param args
     * @param traceInfo
     */
    protected abstract void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args, T traceInfo);

    /**
     * 增强后置处理
     * @param objectEnhanced
     * @param targetMethod
     * @param args
     * @param traceInfo
     */
    protected abstract void afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result, T traceInfo);

    /**
     * 增强异常处理
     * @param objectEnhanced
     * @param targetMethod
     * @param args
     * @param traceInfo
     */
    protected abstract void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t, T traceInfo);

    /**
     * 切面错误不能影响正常业务
     * @param task
     */
    private void safelyHandle(Runnable task) {
        try {
            task.run();
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }
}
