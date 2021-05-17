package rabbit.open.athena.plugin.common.context;

import rabbit.open.athena.plugin.common.TraceInfo;

import java.lang.reflect.Method;

public class ContextManager {

    private static ThreadLocal<TraceInfo> traceInfoContext = new ThreadLocal<>();

    private static ThreadLocal<ContextMetric> metricContext = new ThreadLocal<>();

    /**
     * 判断是否开启了增强
     * @return
     */
    public static boolean isOpen() {
        return null != metricContext.get();
    }

    /**
     * 开启增强
     * @return
     */
    public static void open(Object enhancedObj, Method method, Object[] args) {
        if (!isOpen()) {
            metricContext.set(new ContextMetric(enhancedObj, method, args));
        }
    }

    public static void setTraceInfo(TraceInfo info) {
        traceInfoContext.set(info);
    }

    public static TraceInfo getTraceInfo() {
        return traceInfoContext.get();
    }

    /**
     * 关闭增强
     * @param enhancedObj
     * @param method
     * @param args
     */
    public static void close(Object enhancedObj, Method method, Object[] args) {
        if (!isOpen()) {
            return;
        }
        ContextMetric contextMetric = metricContext.get();
        if (contextMetric.getMethod() == method && contextMetric.getEnhancedObj() == enhancedObj
            && contextMetric.getArgs() == args) {
            metricContext.remove();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
            traceInfoContext.remove();
        }
    }
}
