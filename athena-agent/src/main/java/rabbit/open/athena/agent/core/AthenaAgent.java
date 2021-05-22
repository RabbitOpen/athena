package rabbit.open.athena.agent.core;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.agent.core.transformer.DefaultTransformer;
import rabbit.open.athena.plugin.common.PluginDefinition;
import rabbit.open.athena.plugin.common.context.PluginContext;
import rabbit.open.athena.plugin.common.exception.AthenaException;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * 代理入口
 */
public class AthenaAgent {

    static Logger logger = LoggerFactory.getLogger(AthenaAgent.class);

    public static void premain(String configFileName, Instrumentation inst) {
        logger.info("athena agent is started!, config file is {}", null == configFileName ? "not specified!" : configFileName);
        PluginContext context = PluginContext.initPluginContext(configFileName, getAgentFileDirectory(), Arrays.asList(PluginGroup.DEFAULT_PLUGIN_GROUPS));
        List<PluginDefinition> enabledPlugins = context.getEnabledPlugins();
        if (enabledPlugins.isEmpty()) {
            return;
        }
        for (PluginDefinition plugin : enabledPlugins) {
            new AgentBuilder.Default().type(context.getExcludesMatcher().and(plugin.classMatcher()))
                .transform(new DefaultTransformer(plugin))
                .with(getEmptyListener())
                .installOn(inst);
        }

    }

    /**
     * 获取agent file 所在文件目录
     * @return
     */
    private static String getAgentFileDirectory() {
        String classResourcePath = AthenaAgent.class.getName().replaceAll("\\.", "/") + ".class";
        String url = ClassLoader.getSystemClassLoader().getResource(classResourcePath).toString();
        int insidePathIndex = url.indexOf('!');
        boolean isInJar = insidePathIndex > -1;
        if (isInJar) {
            url = url.substring(url.indexOf("file:"), insidePathIndex);
            File agentJarFile;
            try {
                agentJarFile = new File(new URL(url).toURI());
            } catch (Exception e) {
                throw new AthenaException(e);
            }
            if (null != agentJarFile && agentJarFile.exists()) {
                return agentJarFile.getParentFile().getPath();
            } else {
                throw new AthenaException(String.format("agent file[%s] is not found!", url));
            }
        } else {
            int prefixLength = "file:".length();
            String classLocation = url.substring(
                    prefixLength, url.length() - classResourcePath.length());
            return new File(classLocation).getPath();
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
