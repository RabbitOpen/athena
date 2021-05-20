package rabbit.open.athena.plugin.springcloud.enhance;

import feign.Request;
import rabbit.open.athena.client.trace.SpringCloudTraceInfo;
import rabbit.open.athena.plugin.common.context.AbstractMethodEnhancer;

import java.lang.reflect.Method;
import java.net.URI;

public class OpenFeignClientEnhancer extends AbstractMethodEnhancer<SpringCloudTraceInfo> {

    @Override
    protected void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args, SpringCloudTraceInfo traceInfo) {
        Request request = (Request) args[0];
        URI asUri = URI.create(request.url());
        String clientName = asUri.getHost();
        traceInfo.setRemoteService(clientName);
        traceInfo.setCurrentService(traceInfo.getAppName());
        traceInfo.setRequestURL(request.url());
    }

    @Override
    protected void afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result, SpringCloudTraceInfo traceInfo) {
        // ignore
    }

    @Override
    protected void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t, SpringCloudTraceInfo traceInfo) {
        // IGNORE
    }
}
