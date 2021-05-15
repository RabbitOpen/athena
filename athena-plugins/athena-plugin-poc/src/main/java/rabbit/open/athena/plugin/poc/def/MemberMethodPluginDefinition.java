package rabbit.open.athena.plugin.poc.def;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.open.athena.plugin.common.ClassEnhancer;
import rabbit.open.athena.plugin.poc.SimplePluginDefinition;
import rabbit.open.athena.plugin.poc.agent.SimpleClassEnhancer;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 简单成员方法插件
 */
public class MemberMethodPluginDefinition implements SimplePluginDefinition {

    @Override
    public ElementMatcher.Junction<TypeDescription> generateClassMatcher() {
        return named("com.org.test.bean.User").or(named("com.org.test.bean.Role"));
    }

    @Override
    public Class<? extends ClassEnhancer> getEnhancerClass() {
        return SimpleClassEnhancer.class;
    }
}
