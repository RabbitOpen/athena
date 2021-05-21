package rabbit.open.athena.plugin.springcloud.enhance;

import rabbit.open.athena.client.context.ContextManager;
import rabbit.open.athena.client.trace.SpringCloudTraceInfo;
import rabbit.open.athena.client.trace.TraceInfo;
import rabbit.open.athena.plugin.common.SafeRunner;
import rabbit.open.athena.plugin.common.context.AbstractMethodEnhancer;
import rabbit.open.athena.plugin.springcloud.TraceInfoHelper;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class ServletDispatcherEnhancer extends AbstractMethodEnhancer<SpringCloudTraceInfo> {

    @Override
    public void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args) {
        HttpServletRequest request = (HttpServletRequest) args[0];
        String traceInfoStr = request.getHeader(TraceInfoHelper.ATHENA_TRACE_INFO);
        if (null != traceInfoStr) {
            TraceInfo parent = TraceInfoHelper.string2TraceInfo(traceInfoStr);
            ContextManager.open(parent);
        }
    }

    @Override
    public Object afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result) {
        SafeRunner.handle(() -> {
            TraceInfo traceInfo = ContextManager.getTraceInfo();
            ContextManager.close(traceInfo);
        });
        return result;
    }

    @Override
    public void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t) {
        // DO NOTHING
        SafeRunner.handle(() -> {
            TraceInfo traceInfo = ContextManager.getTraceInfo();
            ContextManager.close(traceInfo);
        });
    }
}
