package rabbit.open.athena.core.interceptor;

import net.bytebuddy.implementation.bind.annotation.*;
import rabbit.open.athena.core.callback.MorphCallBack;
import rabbit.open.athena.plugin.common.AthenaPluginDefinition;
import rabbit.open.athena.plugin.common.context.EnhancedObject;

import java.lang.reflect.Method;

/**
 * 成员函数拦截器
 */
public class MemberMethodInterceptor extends AbstractInterceptor {

    public MemberMethodInterceptor(AthenaPluginDefinition pluginDefinition) {
        super(pluginDefinition);
    }

    @RuntimeType
    public Object interceptor(@This Object objThis, @Origin Method method,
                              @AllArguments Object[] args,
                              @Morph MorphCallBack callBack) throws Exception {
        return doInterceptor(method, args,
                () -> new EnhancedObject(objThis),
                () -> callBack.call(args));
    }
}
