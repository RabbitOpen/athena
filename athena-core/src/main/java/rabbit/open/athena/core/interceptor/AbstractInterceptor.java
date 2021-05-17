package rabbit.open.athena.core.interceptor;

import rabbit.open.athena.core.exception.AthenaException;
import rabbit.open.athena.plugin.common.AthenaPluginDefinition;
import rabbit.open.athena.plugin.common.ClassEnhancer;
import rabbit.open.athena.plugin.common.context.AgentContext;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * 抽象拦截器
 */
public abstract class AbstractInterceptor {

    protected AthenaPluginDefinition pluginDefinition;

    private ClassEnhancer enhancer;

    public AbstractInterceptor(AthenaPluginDefinition pluginDefinition) {
        this.pluginDefinition = pluginDefinition;
        try {
            enhancer = pluginDefinition.enhancerClass().newInstance();
        } catch (InstantiationException e) {
            throw new AthenaException(e);
        } catch (IllegalAccessException e) {
            throw new AthenaException(e);
        }
    }

    public ClassEnhancer getEnhancer() {
        return enhancer;
    }

    /**
     * 方法拦截
     * @param method
     * @param args
     * @param contextSupplier
     * @param resultSupplier
     * @return
     */
    protected Object doInterceptor(Method method, Object[] args, Supplier<AgentContext> contextSupplier,
                                   Supplier<Object> resultSupplier) throws Exception {
        Object result = null;
        AgentContext context = contextSupplier.get();
        try {
            getEnhancer().beforeMethod(context, method, args);
            result = resultSupplier.get();
            result = getEnhancer().afterMethod(context, method, args, result);
            return result;
        } catch (Throwable t) {
            getEnhancer().onException(context, method, args, result, t);
            return result;
        }
    }
}
