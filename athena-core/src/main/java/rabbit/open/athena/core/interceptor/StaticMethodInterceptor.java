package rabbit.open.athena.core.interceptor;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import rabbit.open.athena.core.callback.MorphCallBack;
import rabbit.open.athena.plugin.common.AthenaPluginDefinition;
import rabbit.open.athena.plugin.common.context.AgentContext;

import java.lang.reflect.Method;

/**
 * 静态函数拦截器
 */
public class StaticMethodInterceptor extends AbstractInterceptor {

    public StaticMethodInterceptor(AthenaPluginDefinition pluginDefinition) {
        super(pluginDefinition);
    }

    @RuntimeType
    public Object interceptor(@Origin Method method, @AllArguments Object[] args,
                              @Morph MorphCallBack callBack) throws Exception {
        return doInterceptor(method, args, () -> new AgentContext(), () -> callBack.call(args));
    };


}
