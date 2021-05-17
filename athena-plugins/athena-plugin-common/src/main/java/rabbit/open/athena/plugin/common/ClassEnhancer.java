package rabbit.open.athena.plugin.common;

import java.lang.reflect.Method;

/**
 * 类文件增强接口
 */
public interface ClassEnhancer {

    void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args);

    Object afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result);

    void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t);
}
