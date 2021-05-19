package rabbit.open.athena.agent.core.interceptor;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import rabbit.open.athena.plugin.common.PluginDefinition;

public class ConstructorInterceptor extends MethodInterceptor {

    public ConstructorInterceptor(PluginDefinition pluginDefinition) {
        super(pluginDefinition);
    }

    @RuntimeType
    public void doInterceptor(@AllArguments Object[] args, @This Object objectEnhanced) {
        enhancer.afterMethod(objectEnhanced, null, args, null);
    }
}
