package rabbit.open.athena.plugin.springcloud.enhance;

import feign.Request;
import rabbit.open.athena.client.context.ContextManager;
import rabbit.open.athena.client.trace.SpringCloudTraceInfo;
import rabbit.open.athena.client.trace.TraceInfo;
import rabbit.open.athena.plugin.common.SafeRunner;
import rabbit.open.athena.plugin.common.context.AbstractMethodEnhancer;
import rabbit.open.athena.plugin.springcloud.TraceInfoHelper;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OpenFeignClientEnhancer extends AbstractMethodEnhancer<SpringCloudTraceInfo> {

    @Override
    public void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args) {
        SafeRunner.handle(() -> {
            if (!ContextManager.isOpen()) {
                return;
            }
            TraceInfo traceInfo = ContextManager.getTraceInfo();
            Request request = (Request) args[0];
            Map<String, Collection<String>> headers = new HashMap<>();
            headers.putAll(request.headers());
            headers.put(TraceInfoHelper.ATHENA_TRACE_INFO, TraceInfoHelper.traceInfo2List(traceInfo));
            Request newRequest = Request.create(request.httpMethod(), request.url(), headers, request.requestBody());
            args[0] = newRequest;
        });
    }

    @Override
    public Object afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result) {
        // DO nothing
        return result;
    }

    @Override
    public void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t) {
        // DO nothing
    }
}
