package rabbit.open.athena.core.transformer;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.core.callback.MorphCallBack;
import rabbit.open.athena.core.interceptor.MemberMethodInterceptor;
import rabbit.open.athena.core.interceptor.StaticMethodInterceptor;
import rabbit.open.athena.plugin.common.AthenaPluginDefinition;

public class DefaultTransformer implements AgentBuilder.Transformer {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private AthenaPluginDefinition plugin;

    public DefaultTransformer(AthenaPluginDefinition plugin) {
        this.plugin = plugin;
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
        if (!plugin.classMatcher().matches(typeDescription)) {
            return builder;
        }
        logger.info("enhance: {}", typeDescription.getName());
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
