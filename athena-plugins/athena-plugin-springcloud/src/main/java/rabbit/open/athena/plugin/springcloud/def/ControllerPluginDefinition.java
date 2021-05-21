package rabbit.open.athena.plugin.springcloud.def;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        return isAnnotatedWith(RestController.class)
                .or(isAnnotatedWith(Controller.class))
                .and(not(isInterface()))
                .and(not(isAbstract()));
    }

    @Override
    public Class<? extends AbstractEnhancer> enhancerClass() {
        return ControllerEnhancer.class;
    }

    @Override
    public ElementMatcher.Junction methodMatcher() {
        return isAnnotatedWith(PostMapping.class).or(isAnnotatedWith(GetMapping.class))
                .or(isAnnotatedWith(RequestMapping.class))
                .and(isPublic()).and(not(isAbstract()));
    }

}
