package rabbit.open.athena.plugin.customize.def;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.open.athena.client.Traceable;
import rabbit.open.athena.plugin.common.AbstractEnhancer;
import rabbit.open.athena.plugin.customize.CustomizePluginGroup;
import rabbit.open.athena.plugin.customize.enhance.TraceableMethodEnhancer;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class MemberFunctionTracePluginDefinition implements CustomizePluginGroup {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return declaresMethod(isAnnotatedWith(Traceable.class)).and(not(isInterface()));
    }

    @Override
    public ElementMatcher.Junction methodMatcher() {
        return isAnnotatedWith(Traceable.class);
    }

    @Override
    public Class<? extends AbstractEnhancer> enhancerClass() {
        return TraceableMethodEnhancer.class;
    }
}
