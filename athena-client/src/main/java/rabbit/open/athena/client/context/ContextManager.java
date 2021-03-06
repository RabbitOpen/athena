package rabbit.open.athena.client.context;

import rabbit.open.athena.client.trace.TraceInfo;

public class ContextManager {

    private static ThreadLocal<TraceInfo> traceInfoContext = new ThreadLocal<>();

    // 字节增强上下文
    private static ThreadLocal<TraceInfo> enhanceContext = new ThreadLocal<>();

    /**
     * 判断是否开启了增强
     * @return
     */
    public static boolean isOpen() {
        return null != enhanceContext.get();
    }

    /**
     * 开启增强
     * @param context
     */
    public static void open(TraceInfo context) {
        if (!isOpen()) {
            enhanceContext.set(context);
            setTraceInfo(context);
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
     * @param context
     */
    public static void close(TraceInfo context) {
        if (!isOpen()) {
            return;
        }
        TraceInfo openContext = enhanceContext.get();
        if (openContext == context) {
            enhanceContext.remove();
            traceInfoContext.remove();
        }
    }
}
