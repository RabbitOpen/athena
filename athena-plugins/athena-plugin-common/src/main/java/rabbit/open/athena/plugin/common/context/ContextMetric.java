package rabbit.open.athena.plugin.common.context;

import rabbit.open.athena.plugin.common.TraceInfo;

import java.lang.reflect.Method;

public class ContextMetric {

    // trace info
    private TraceInfo traceInfo;

    // 被增强的方法
    private Method method;

    public ContextMetric(TraceInfo traceInfo, Method method) {
        this.traceInfo = traceInfo;
        this.method = method;
    }

    public Object getTraceInfo() {
        return traceInfo;
    }

    public Method getMethod() {
        return method;
    }

}
