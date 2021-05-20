package rabbit.open.athena.plugin.customize.enhance;

import rabbit.open.athena.client.Traceable;
import rabbit.open.athena.client.trace.TraceInfo;
import rabbit.open.athena.plugin.common.context.AbstractMethodEnhancer;

import java.lang.reflect.Method;

public class TraceableMethodEnhancer extends AbstractMethodEnhancer<TraceInfo> {

    @Override
    protected void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args, TraceInfo traceInfo) {
        String name = targetMethod.getAnnotation(Traceable.class).name();
        if (null != name && !"".equals(name.trim())) {
            traceInfo.setName(name);
        }
    }

    @Override
    protected void afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result, TraceInfo traceInfo) {

    }

    @Override
    protected void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t, TraceInfo traceInfo) {

    }
}
