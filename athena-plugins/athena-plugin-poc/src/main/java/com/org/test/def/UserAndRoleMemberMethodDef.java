package com.org.test.def;

import com.org.test.SimplePluginDefinition;
import com.org.test.agent.SimpleClassEnhancer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.open.athena.plugin.common.ClassEnhancer;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 简单成员方法插件
 */
public class UserAndRoleMemberMethodDef implements SimplePluginDefinition {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return named("com.org.test.bean.User").or(named("com.org.test.bean.Role"));
    }

    @Override
    public ElementMatcher.Junction methodMatcher() {
        return named("getName");
    }

    @Override
    public Class<? extends ClassEnhancer> enhancerClass() {
        return SimpleClassEnhancer.class;
    }
}
