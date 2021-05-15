package rabbit.open.athena.core.interceptor;

import rabbit.open.athena.plugin.common.AthenaPluginDefinition;

/**
 * 抽象拦截器
 */
public abstract class AbstractInterceptor {

    protected AthenaPluginDefinition pluginDefinition;

    public AbstractInterceptor(AthenaPluginDefinition pluginDefinition) {
        this.pluginDefinition = pluginDefinition;
    }

}
