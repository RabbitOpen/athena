package rabbit.open.athena.plugin.customize.def;

import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;

public class StaticFunctionTracePluginDefinition extends MemberFunctionTracePluginDefinition {

    @Override
    public ElementMatcher.Junction methodMatcher() {
        return super.methodMatcher().and(isStatic());
    }

    @Override
    public boolean isStaticMethod() {
        return true;
    }
}
