package rabbit.open.athena.plugin.common;

import java.lang.reflect.Method;

/**
 * 类文件增强接口
 */
public interface AbstractEnhancer {

    void beforeMethod(Object objectEnhanced, Method targetMethod, Object[] args);

    Object afterMethod(Object objectEnhanced, Method targetMethod, Object[] args, Object result);

    void onException(Object objectEnhanced, Method targetMethod, Object[] args, Object result, Throwable t);

    default String type2Str(Class<?>[] types) {
        String str = "";
        for (int i = 0; i < types.length; i++) {
            str += types[i].getName();
            if (i != types.length - 1) {
                str += ", ";
            }
        }
        return str;
    }
}
