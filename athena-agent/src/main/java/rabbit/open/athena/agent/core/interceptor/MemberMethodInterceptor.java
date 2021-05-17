package rabbit.open.athena.agent.core.interceptor;

import net.bytebuddy.implementation.bind.annotation.*;
import rabbit.open.athena.agent.core.callback.MorphCallBack;
import rabbit.open.athena.plugin.common.AthenaPluginDefinition;
import rabbit.open.athena.plugin.common.context.AgentContext;

import java.lang.reflect.Method;

/**
 * 成员函数拦截器
 */
public class MemberMethodInterceptor extends MethodInterceptor {

    public MemberMethodInterceptor(AthenaPluginDefinition pluginDefinition) {
        super(pluginDefinition);
    }

    @RuntimeType
    public Object interceptor(@This Object objThis, @Origin Method method,
                              @AllArguments Object[] args,
                              @Morph MorphCallBack callBack) throws Exception {
        return doInterceptor(method, args,
                () -> new AgentContext(objThis),
                () -> callBack.call(args));
    }
}
