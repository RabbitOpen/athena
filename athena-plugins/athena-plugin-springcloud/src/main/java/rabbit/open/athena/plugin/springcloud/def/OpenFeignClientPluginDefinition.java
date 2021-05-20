package rabbit.open.athena.plugin.springcloud.def;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.open.athena.plugin.common.AbstractEnhancer;
import rabbit.open.athena.plugin.springcloud.SpringCloudPluginGroup;
import rabbit.open.athena.plugin.springcloud.enhance.OpenFeignClientEnhancer;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * open feign client 插件
 */
public class OpenFeignClientPluginDefinition implements SpringCloudPluginGroup {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return named("org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient");
    }

    @Override
    public Class<? extends AbstractEnhancer> enhancerClass() {
        return OpenFeignClientEnhancer.class;
    }

    @Override
    public ElementMatcher.Junction methodMatcher() {
        return named("execute");
    }
}
