package com.org.test.def;

import com.org.test.SimplePluginGroup;
import com.org.test.agent.AppendHello;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.open.athena.plugin.common.AbstractEnhancer;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 简单成员方法插件
 */
public class User1MemberMethodDef implements SimplePluginGroup {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return named("com.org.test.bean.User1");
    }

    @Override
    public ElementMatcher.Junction methodMatcher() {
        return named("getName");
    }

    @Override
    public Class<? extends AbstractEnhancer> enhancerClass() {
        return AppendHello.class;
    }
}
