package rabbit.open.athena.plugin.common;

import rabbit.open.athena.plugin.common.context.AgentContext;

import java.lang.reflect.Method;

/**
 * 类文件增强接口
 */
public interface ClassEnhancer {

    void beforeMethod(AgentContext context, Method targetMethod, Object[] args);

    Object afterMethod(AgentContext context, Method targetMethod, Object[] args, Object result);

    void onException(AgentContext context, Method targetMethod, Object[] args, Object result, Throwable t);
}
