package rabbit.open.athena.core;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.core.transformer.DefaultTransformer;
import rabbit.open.athena.plugin.common.AthenaPluginDefinition;
import rabbit.open.athena.plugin.common.impl.PluginContext;

import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * 代理入口
 */
public class AthenaAgent {

    static Logger logger = LoggerFactory.getLogger(AthenaAgent.class);

    public static void premain(String configFileName, Instrumentation inst) {
        logger.info("athena agent is started!, config file is {}", null == configFileName ? "not specified!" : configFileName);
        PluginContext context = PluginContext.initPluginContext(configFileName);
        List<AthenaPluginDefinition> enabledPlugins = context.getEnabledPlugins();
        if (enabledPlugins.isEmpty()) {
            return;
        }
        for (AthenaPluginDefinition plugin : enabledPlugins) {
            new AgentBuilder.Default().type(plugin.classMatcher().and(context.getExcludesMatcher()))
                .transform(new DefaultTransformer(plugin))
                .with(getEmptyListener())
                .installOn(inst);
        }

    }

    private static AgentBuilder.Listener getEmptyListener() {
        return new AgentBuilder.Listener() {
            @Override
            public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean loaded) {
                // TO DO
//                logger.info("onDiscovery: {}", typeName);
            }

            @Override
            public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader,
                                         JavaModule javaModule, boolean loaded, DynamicType dynamicType) {
                // TO DO
                logger.info("onTransformation: {}", typeDescription.getName());
            }

            @Override
            public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule,
                                  boolean loaded) {
                // 不满足过滤条件的都被过滤掉了
//                logger.info("onIgnored: {}", typeDescription.getName());
            }

            @Override
            public void onError(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean b,
                                Throwable throwable) {
                // TO DO
                logger.error("{} enhance failed: {}", typeName, throwable);
            }

            @Override
            public void onComplete(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean loaded) {
                // TO DO
//                logger.info("onComplete: {}, loaded: {}", typeName, loaded);
            }

        };
    }
}
