package rabbit.open.athena.plugin.common;

import rabbit.open.athena.plugin.common.context.EnhancedObject;

import java.lang.reflect.Method;

/**
 * 类文件增强接口
 */
public interface ClassEnhancer {

    void beforeMethod(EnhancedObject enhancedObj, Method targetMethod, Object[] args);

    Object afterMethod(EnhancedObject enhancedObj, Method targetMethod, Object[] args, Object result);

    void onException(EnhancedObject enhancedObj, Method targetMethod, Object[] args, Object result);
}
