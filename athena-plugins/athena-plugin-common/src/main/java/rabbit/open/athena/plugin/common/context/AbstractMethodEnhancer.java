package rabbit.open.athena.plugin.common.context;

import rabbit.open.athena.client.context.ContextManager;
import rabbit.open.athena.client.trace.TraceInfo;
import rabbit.open.athena.plugin.common.AbstractEnhancer;
import rabbit.open.athena.plugin.common.SafeRunner;
import rabbit.open.athena.plugin.common.exception.AthenaException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.UUID;

/**
 * 抽象增强实现，记录trace info信息
 */
public abstract class AbstractMethodEnhancer<T extends TraceInfo> implements AbstractEnhancer {

    protected Class<T> clz;

    public AbstractMethodEnhancer() {
        Class<?> cls = getClass();
        while (!(cls.getGenericSuperclass() instanceof ParameterizedType)) {
            cls = cls.getSuperclass();
        }
        clz = (Class<T>) ((ParameterizedType) (cls.getGenericSuperclass())).getActualTypeArguments()[0];
    }

    @Override
    public void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args) {
        SafeRunner.handle(() -> {
            T traceInfo = initTraceInfo();
            traceInfo.setAppName(PluginContext.getContext().getMetaData().getApplicationName());
            traceInfo.setThreadName(Thread.currentThread().getName());
            traceInfo.setFullMethodName(targetMethod.getName() + "(" + type2Str(targetMethod.getParameterTypes()) + ")");
            traceInfo.setName(targetMethod.getName());
            traceInfo.setTargetClzName(Modifier.isStatic(targetMethod.getModifiers()) ? targetMethod.getDeclaringClass().getName() : objectEnhanced.getClass().getName());
            traceInfo.setStart(System.currentTimeMillis());
            if (!ContextManager.isOpen()) {
                traceInfo.setTraceId(UUID.randomUUID().toString().replace("-", ""));
                traceInfo.setRoot(true);
                ContextManager.open(traceInfo);
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

    @Override
    public Object afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result) {
        SafeRunner.handle(() -> {
            TraceInfo traceInfo = ContextManager.getTraceInfo();
            try {
                traceInfo.setEnd(System.currentTimeMillis());
                afterMethod(objectEnhanced, targetMethod, args, result, (T) traceInfo);
                ContextManager.setTraceInfo(traceInfo.getParent());
                PluginContext.getContext().getOrInitCollector().doCollection(traceInfo);
            } finally {
                ContextManager.close(traceInfo);
            }
        });
        return result;
    }

    @Override
    public void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t) {
        SafeRunner.handle(() -> {
            TraceInfo traceInfo = ContextManager.getTraceInfo();
            try {
                traceInfo.setEnd(System.currentTimeMillis());
                traceInfo.setExceptionOccurred(true);
                traceInfo.setErrMsg(t.getMessage());
                onException(objectEnhanced, targetMethod, args, result, t, (T) traceInfo);
                ContextManager.setTraceInfo(traceInfo.getParent());
                PluginContext.getContext().getOrInitCollector().doCollection(traceInfo);
            } finally {
                ContextManager.close(traceInfo);
            }
        });
    }

    /**
     * 新建一个trace info
     * @return
     */
    protected T initTraceInfo() {
        try {
            return clz.newInstance();
        } catch (Exception e) {
            throw new AthenaException(e);
        }
    };

    /**
     * 增强前置处理
     * @param objectEnhanced
     * @param targetMethod
     * @param args
     * @param traceInfo
     */
    protected void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args, T traceInfo) {}

    /**
     * 增强后置处理
     * @param objectEnhanced
     * @param targetMethod
     * @param args
     * @param traceInfo
     */
    protected void afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result, T traceInfo) {}

    /**
     * 增强异常处理
     * @param objectEnhanced
     * @param targetMethod
     * @param args
     * @param traceInfo
     */
    protected void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t, T traceInfo) {}

}
