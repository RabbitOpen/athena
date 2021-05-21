package rabbit.open.athena.plugin.springcloud.def;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.open.athena.plugin.common.AbstractEnhancer;
import rabbit.open.athena.plugin.springcloud.SpringCloudPluginGroup;
import rabbit.open.athena.plugin.springcloud.enhance.ControllerEnhancer;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * open feign client 插件
 */
public class ControllerPluginDefinition implements SpringCloudPluginGroup {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return isAnnotatedWith(named("org.springframework.stereotype.Controller"))
                .or(isAnnotatedWith(named("org.springframework.web.bind.annotation.RestController")))
                .and(not(isInterface()))
                .and(not(isAbstract()));
    }

    @Override
    public Class<? extends AbstractEnhancer> enhancerClass() {
        return ControllerEnhancer.class;
    }

    @Override
    public ElementMatcher.Junction methodMatcher() {
        return isAnnotatedWith(named("org.springframework.web.bind.annotation.PostMapping"))
                .or(isAnnotatedWith(named("org.springframework.web.bind.annotation.GetMapping")))
                .or(isAnnotatedWith(named("org.springframework.web.bind.annotation.RequestMapping")))
                .and(isPublic()).and(not(isAbstract()));
    }

}
