package rabbit.open.athena.plugin.springcloud.enhance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rabbit.open.athena.client.trace.SpringCloudTraceInfo;
import rabbit.open.athena.plugin.common.context.AbstractMethodEnhancer;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class ControllerEnhancer extends AbstractMethodEnhancer<SpringCloudTraceInfo> {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args, SpringCloudTraceInfo traceInfo) {
        try {
            traceInfo.setHost(Inet4Address.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        setTraceInfoName(targetMethod, traceInfo);
        traceInfo.setRoot(true);
        traceInfo.setExecuteOrder(traceInfo.getParent().getExecuteOrder());
    }

    private void setTraceInfoName(Method targetMethod, SpringCloudTraceInfo traceInfo) {
        RequestMapping requestMapping = targetMethod.getAnnotation(RequestMapping.class);
        if (null != requestMapping) {
            traceInfo.setName(requestMapping.name());
        }
        PostMapping postMapping = targetMethod.getAnnotation(PostMapping.class);
        if (null != postMapping) {
            traceInfo.setName(postMapping.name());
        }
        GetMapping getMapping = targetMethod.getAnnotation(GetMapping.class);
        if (null != getMapping) {
            traceInfo.setName(getMapping.name());
        }
    }

}
