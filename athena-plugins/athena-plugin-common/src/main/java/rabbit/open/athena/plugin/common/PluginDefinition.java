package rabbit.open.athena.plugin.common;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * 插件定义
 */
public interface PluginDefinition {

    /**
     * 定义哪些class可以被增强
     */
    ElementMatcher.Junction<TypeDescription> classMatcher();

    /**
     * 声明哪些方法可以被增强
     */
    default ElementMatcher.Junction methodMatcher() {
        return isPublic().and(not(named("toString")))
                .and(not(named("hashCode")));
    }

    /**
     * 用来增强的class对象
     */
    Class<? extends ClassEnhancer> enhancerClass();

    /**
     * 标识被增强的方法是不是静态方法
     * @return
     */
    default boolean isStaticMethod() {
        return false;
    }
}
