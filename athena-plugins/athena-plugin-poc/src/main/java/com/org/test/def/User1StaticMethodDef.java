package com.org.test.def;

import com.org.test.SimplePluginGroup;
import com.org.test.agent.AppendStatic;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.open.athena.plugin.common.AbstractEnhancer;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 静态方法插件
 */
public class User1StaticMethodDef implements SimplePluginGroup {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return named("com.org.test.bean.User1");
    }

    @Override
    public Class<? extends AbstractEnhancer> enhancerClass() {
        return AppendStatic.class;
    }

    @Override
    public ElementMatcher.Junction methodMatcher() {
        return named("type");
    }

    @Override
    public boolean isStaticMethod() {
        return true;
    }
}
