package rabbit.open.athena.agent.core.interceptor;

import rabbit.open.athena.plugin.common.AbstractEnhancer;
import rabbit.open.athena.plugin.common.PluginDefinition;
import rabbit.open.athena.plugin.common.exception.AthenaException;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * 抽象拦截器
 */
public abstract class MethodInterceptor {

    protected PluginDefinition pluginDefinition;

    protected AbstractEnhancer enhancer;

    public MethodInterceptor(PluginDefinition pluginDefinition) {
        this.pluginDefinition = pluginDefinition;
        try {
            enhancer = pluginDefinition.enhancerClass().newInstance();
        } catch (InstantiationException e) {
            throw new AthenaException(e);
        } catch (IllegalAccessException e) {
            throw new AthenaException(e);
        }
    }

    public AbstractEnhancer getEnhancer() {
        return enhancer;
    }

    /**
     * 方法拦截
     * @param method
     * @param args
     * @param objectEnhanced
     * @param resultSupplier
     * @return
     */
    protected Object doInterceptor(Method method, Object[] args, Object objectEnhanced,
                                   Supplier<Object> resultSupplier) {
        Object result = null;
        try {
            getEnhancer().beforeMethod(objectEnhanced, method, args);
            result = resultSupplier.get();
            result = getEnhancer().afterMethod(objectEnhanced, method, args, result);
            return result;
        } catch (Throwable t) {
            getEnhancer().onException(objectEnhanced, method, args, result, t);
            return result;
        }
    }
}
