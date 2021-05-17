package rabbit.open.athena.plugin.common.context;

import java.lang.reflect.Method;

public class ContextMetric {

    // 被增强的对象
    private Object enhancedObj;

    // 被增强的方法
    private Method method;

    // 调用入参
    private Object[] args;

    public ContextMetric(Object enhancedObj, Method method, Object[] args) {
        this.enhancedObj = enhancedObj;
        this.method = method;
        this.args = args;
    }

    public Object getEnhancedObj() {
        return enhancedObj;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }
}
