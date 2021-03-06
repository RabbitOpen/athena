package rabbit.open.athena.agent.core.transformer;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import rabbit.open.athena.agent.core.callback.MorphCallBack;
import rabbit.open.athena.agent.core.interceptor.ConstructorInterceptor;
import rabbit.open.athena.agent.core.interceptor.MemberMethodInterceptor;
import rabbit.open.athena.agent.core.interceptor.StaticMethodInterceptor;
import rabbit.open.athena.plugin.common.PluginDefinition;

public class DefaultTransformer implements AgentBuilder.Transformer {

    private PluginDefinition plugin;

    public DefaultTransformer(PluginDefinition plugin) {
        this.plugin = plugin;
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
        if (!plugin.classMatcher().matches(typeDescription)) {
            return builder;
        }
        if (plugin.isConstructor()) {
            return builder.constructor(plugin.methodMatcher())
                    .intercept(SuperMethodCall.INSTANCE.andThen(
                            MethodDelegation.withDefaultConfiguration()
                                .to(new ConstructorInterceptor(plugin))
                    ));
        }
        if (plugin.isStaticMethod()) {
            // 增强静态方法
            return builder.method(ElementMatchers.isStatic().and(plugin.methodMatcher()))
                    .intercept(MethodDelegation.withDefaultConfiguration()
                            .withBinders(Morph.Binder.install(MorphCallBack.class))
                            .to(new StaticMethodInterceptor(plugin)));
        } else {
            // 增强成员方法
            return builder.method(plugin.methodMatcher())
                    .intercept(MethodDelegation.withDefaultConfiguration()
                            .withBinders(Morph.Binder.install(MorphCallBack.class))
                            .to(new MemberMethodInterceptor(plugin)));
        }
    }
}
