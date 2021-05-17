package com.org.athena.test.trace;

import com.org.athena.test.TracePluginGroup;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.open.athena.plugin.common.ClassEnhancer;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * 追踪com.org.athena.test.service路径下所有的类的所有方法
 */
public class TracePluginDefinition implements TracePluginGroup {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return nameStartsWith("com.org.athena.test.service");
    }

    @Override
    public ElementMatcher.Junction methodMatcher() {
        return any().and(not(isDeclaredBy(Object.class)));
    }

    @Override
    public Class<? extends ClassEnhancer> enhancerClass() {
        return ExecuteCostEnhancer.class;
    }
}
