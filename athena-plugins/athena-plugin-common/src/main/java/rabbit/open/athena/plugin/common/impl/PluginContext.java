package rabbit.open.athena.plugin.common.impl;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.plugin.common.AthenaPluginDefinition;
import rabbit.open.athena.plugin.common.PluginService;
import rabbit.open.athena.plugin.common.meta.AthenaMetaData;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * 插件上下文
 */
public class PluginContext implements PluginService {

    static Logger logger = LoggerFactory.getLogger(PluginContext.class);

    // 默认客户端代理配置文件名
    private static final String DEFAULT_ATHENA_CONFIG_FILE_NAME = "athena.yml";

    private AthenaMetaData metaData;

    // 默认插件列表
    private static final List<Class<? extends AthenaPluginDefinition>> DEFAULT_DEFINITIONS = Arrays.asList(

    );

    /**
     * 需要排除的包
     */
    private static String[] agentIgnorePackages = {
            "athena.net.bytebuddy",
            "rabbit.open.athena",
            "org.yaml.snakeyaml",
    };

    /**
     * private constructor
     */
    private PluginContext() {}


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
            context.metaData = new AthenaMetaData();
            logger.info("config file[{}] is not existed!", fileName);
        } else {
            context.metaData = AthenaMetaData.readBy(fileName);
            logger.info("load config from file[{}] : {}", fileName, context.metaData);
        }
        List<Class<? extends AthenaPluginDefinition>> declaredPluginDefinitions = context.getMetaData().getEnabledPluginDefinitions();
        if (declaredPluginDefinitions.isEmpty()) {
            logger.info("use default plugin definition!");
            context.loadPlugins(DEFAULT_DEFINITIONS);
        } else {
            // 如果外部客户端申明了插件，就使用外部申明的插件
            context.loadPlugins(declaredPluginDefinitions);
        }
        return context;
    }

    /**
     * 排除一些特定包
     * @return
     */
    public ElementMatcher.Junction getExcludesMatcher() {
        ElementMatcher.Junction junction = null;
        for (String ignorePackage : agentIgnorePackages) {
            if (null == junction) {
                junction = not(nameStartsWith(ignorePackage));
            } else {
                junction = junction.and(not(nameStartsWith(ignorePackage)));
            }
        }
        return junction;
    }

    @Override
    public void loadPlugins(List<Class<? extends AthenaPluginDefinition>> definitions) {
        for (Class<? extends AthenaPluginDefinition> defClz : definitions) {
            ServiceLoader<? extends AthenaPluginDefinition> services = ServiceLoader.load(defClz);
            for (AthenaPluginDefinition definition : services) {
                logger.info("load plugin definition: {} --> {}", defClz.getSimpleName(), definition.getClass().getName());
                pluginDefinitions.add(definition);
            }
        }
    }

    @Override
    public List<AthenaPluginDefinition> getMatchedPlugins(TypeDescription type) {
        List<AthenaPluginDefinition> plugins = new ArrayList<>();
        for (AthenaPluginDefinition definition : pluginDefinitions) {
            if (!definition.classMatcher().matches(type)) {
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
