package rabbit.open.athena.plugin.common;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * 插件定义
 */
public interface AthenaPluginDefinition {

    /**
     * 定义哪些class可以被增强
     */
    ElementMatcher.Junction<TypeDescription> generateClassMatcher();

    /**
     * 声明哪些方法可以被增强
     */
    default ElementMatcher.Junction<TypeDescription> generateMethodMatcher() {
        return isPublic().and(not(named("toString")))
                .and(not(named("hashCode")));
    }

    /**
     * 用来增强的class对象
     */
    Class<? extends ClassEnhancer> getEnhancerClass();

    /**
     * 标识被增强的方法是不是静态方法
     * @return
     */
    default boolean isStaticMethod() {
        return false;
    }
}