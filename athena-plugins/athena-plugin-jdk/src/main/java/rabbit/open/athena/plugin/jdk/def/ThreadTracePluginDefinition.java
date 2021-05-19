package rabbit.open.athena.plugin.jdk.def;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.open.athena.plugin.common.AbstractEnhancer;
import rabbit.open.athena.plugin.jdk.JDKPluginGroup;
import rabbit.open.athena.plugin.jdk.enhancer.ThreadTraceAbstractEnhancer;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * runnable/callable执行时的拦截定义
 */
public class ThreadTracePluginDefinition implements JDKPluginGroup {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return named("rabbit.open.athena.client.wrapper.RunnableWrapper")
                .or(named("rabbit.open.athena.client.wrapper.CallableWrapper"));
    }

    @Override
    public Class<? extends AbstractEnhancer> enhancerClass() {
        return ThreadTraceAbstractEnhancer.class;
    }

    @Override
    public boolean isConstructor() {
        return true;
    }
}
