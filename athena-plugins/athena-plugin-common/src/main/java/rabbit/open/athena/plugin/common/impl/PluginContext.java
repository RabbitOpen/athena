package rabbit.open.athena.plugin.common.impl;

import net.bytebuddy.description.type.TypeDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.plugin.common.AthenaPluginDefinition;
import rabbit.open.athena.plugin.common.PluginService;
import rabbit.open.athena.plugin.common.meta.AthenaMetaData;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 插件上下文
 */
public class PluginContext implements PluginService {

    static Logger logger = LoggerFactory.getLogger(PluginContext.class);

    // 默认客户端代理配置文件名
    private static final String DEFAULT_ATHENA_CONFIG_FILE_NAME = "athena.yml";

    private PluginContext() {}

    private static PluginContext context;

    private AthenaMetaData metaData;

    /**
     * 已知的插件
     */
    private List<AthenaPluginDefinition> pluginDefinitions = new ArrayList<>();

    /**
     * 初始化插件上下文
     * @param  athenaConfigFileName
     * @return
     */
    public static PluginContext initPluginContext(String athenaConfigFileName) {
        PluginContext context = new PluginContext();
        String fileName = (null == athenaConfigFileName ? DEFAULT_ATHENA_CONFIG_FILE_NAME : athenaConfigFileName);
        URL resource = PluginContext.class.getResource("/" + fileName);
        if (null == resource) {
            logger.info("config file[{}] is not existed!", fileName);
        } else {
            context.metaData = AthenaMetaData.readBy("/" + fileName);
            logger.info("load config from file[{}] : {}", fileName, context.metaData);
        }
        return context;
    }

    @Override
    public void loadPlugins(Class<? extends AthenaPluginDefinition>[] definitions) {
        for (Class<? extends AthenaPluginDefinition> defClz : definitions) {
            ServiceLoader<? extends AthenaPluginDefinition> services = ServiceLoader.load(defClz);
            for (AthenaPluginDefinition definition : services) {
                logger.info("load plugin definition: {} --> {}", defClz.getSimpleName(), definition.getClass().getName());
                pluginDefinitions.add(definition);
            }
        }
    }

    @Override
    public List<AthenaPluginDefinition> getPlugins(TypeDescription type) {
        List<AthenaPluginDefinition> plugins = new ArrayList<>();
        for (AthenaPluginDefinition definition : pluginDefinitions) {
            if (!definition.generateClassMatcher().matches(type)) {
                continue;
            }
            plugins.add(definition);
        }
        return plugins;
    }

    public AthenaMetaData getMetaData() {
        return metaData;
    }

    @Override
    public List<AthenaPluginDefinition> getEnabledPlugins() {
        List<Class<? extends AthenaPluginDefinition>> enabledPlugins = getMetaData().getEnabledPlugins();
        if (enabledPlugins.isEmpty()) {
            return pluginDefinitions;
        } else {
            List<AthenaPluginDefinition> list = new ArrayList<>();
            for (Class<? extends AthenaPluginDefinition> enabledPlugin : enabledPlugins) {
                for (AthenaPluginDefinition existedPlugin : pluginDefinitions) {
                    if (enabledPlugin.equals(existedPlugin.getClass())) {
                        list.add(existedPlugin);
                    }
                }
            }
            return list;
        }
    }
}
