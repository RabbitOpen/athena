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
     * @param traceInfo
     * @param method
     */
    public static void open(TraceInfo traceInfo, Method method) {
        if (!isOpen()) {
            metricContext.set(new ContextMetric(traceInfo, method));
        }
    }

    /**
     * 设置traceInfo
     * @param info
     */
    public static void setTraceInfo(TraceInfo info) {
        traceInfoContext.set(info);
    }

    public static TraceInfo getTraceInfo() {
        return traceInfoContext.get();
    }

    /**
     * 关闭增强
     * @param traceInfo
     * @param method
     */
    public static void close(TraceInfo traceInfo, Method method) {
        if (!isOpen()) {
            return;
        }
        ContextMetric contextMetric = metricContext.get();
        if (contextMetric.getMethod() == method && contextMetric.getTraceInfo() == traceInfo) {
            metricContext.remove();
            traceInfoContext.remove();
        }
    }
}
