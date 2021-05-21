package rabbit.open.athena.plugin.springcloud.def;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.open.athena.plugin.common.AbstractEnhancer;
import rabbit.open.athena.plugin.springcloud.SpringCloudPluginGroup;
import rabbit.open.athena.plugin.springcloud.enhance.ServletDispatcherEnhancer;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * spring mvc servlet dispatcher 方法调用处理器拦截插件定义
 */
public class ServletDispatcherPluginDefinition implements SpringCloudPluginGroup {
    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return named("org.springframework.web.servlet.DispatcherServlet");
    }

    @Override
    public Class<? extends AbstractEnhancer> enhancerClass() {
        return ServletDispatcherEnhancer.class;
    }

    @Override
    public ElementMatcher.Junction methodMatcher() {
        return named("doDispatch").and(takesArguments(2));
    }
}
