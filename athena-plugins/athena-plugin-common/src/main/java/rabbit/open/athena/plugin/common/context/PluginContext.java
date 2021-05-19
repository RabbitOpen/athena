package rabbit.open.athena.plugin.common.context;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.plugin.common.PluginDefinition;
import rabbit.open.athena.plugin.common.PluginService;
import rabbit.open.athena.plugin.common.TraceInfoCollector;
import rabbit.open.athena.plugin.common.meta.AthenaMetaData;

import java.net.URL;
import java.util.ArrayList;
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

    // 默认插件列组表
    private List<Class<? extends PluginDefinition>> defaultPluginGroups;

    private static PluginContext context;

    private TraceInfoCollector collector;

    /**
     * 需要排除的包
     */
    private static String[] agentIgnorePackages = {
            "athena.net.bytebuddy",
            "rabbit.open.athena.agent",
            "rabbit.open.athena.plugin",
            "org.yaml.snakeyaml",
    };

    /**
     * private constructor
     */
    private PluginContext() {}


    /**
     * 已知的插件
     */
    private List<PluginDefinition> pluginDefinitions = new ArrayList<>();

    /**
     * 初始化插件上下文
     * @param  athenaConfigFileName
     * @return
     */
    public static PluginContext initPluginContext(String athenaConfigFileName) {
        return initPluginContext(athenaConfigFileName, new ArrayList<>());
    }

    public static PluginContext initPluginContext(String athenaConfigFileName, List<Class<? extends PluginDefinition>> pluginGroups) {
        PluginContext.context = new PluginContext();
        context.defaultPluginGroups = pluginGroups;
        String fileName = (null == athenaConfigFileName ? DEFAULT_ATHENA_CONFIG_FILE_NAME : athenaConfigFileName);
        URL resource = PluginContext.class.getResource("/" + fileName);
        if (null == resource) {
            context.metaData = new AthenaMetaData();
            logger.info("config file[{}] is not existed!", fileName);
        } else {
            context.metaData = AthenaMetaData.readBy(fileName);
            logger.info("load config from file[{}] : {}", fileName, context.metaData);
        }
        context.loadPlugins();
        context.getOrInitCollector();
        return context;
    }

    public static PluginContext getContext() {
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
    public void loadPlugins() {
        List<String> pluginGroups = getMetaData().getEnabledPluginGroups();
        List<Class<? extends PluginDefinition>> groupDefinitions;
        if (pluginGroups.isEmpty()) {
            logger.info("use default plugin groups: {}", defaultPluginGroups);
            groupDefinitions = defaultPluginGroups;
        } else {
            groupDefinitions = getDeclaredPluginGroups();
        }
        for (Class<? extends PluginDefinition> groupDefinition : groupDefinitions) {
            ServiceLoader<? extends PluginDefinition> services = ServiceLoader.load(groupDefinition);
            for (PluginDefinition definition : services) {
                logger.info("load plugin definition: {} --> {}", groupDefinition.getSimpleName(), definition.getClass().getName());
                pluginDefinitions.add(definition);
            }
        }
    }

    @Override
    public List<PluginDefinition> getMatchedPlugins(TypeDescription type) {
        List<PluginDefinition> plugins = new ArrayList<>();
        for (PluginDefinition definition : getEnabledPlugins()) {
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

    /**
     * 获取有效的插件配置，如果配置文件中没有有效的插件，那么就使用默认插件（已申明的全部插件）
     * @return
     */
    @Override
    public List<PluginDefinition> getEnabledPlugins() {
        List<Class<? extends PluginDefinition>> enabledPlugins = getDeclaredPlugins();
        if (enabledPlugins.isEmpty()) {
            return pluginDefinitions;
        } else {
            List<PluginDefinition> list = new ArrayList<>();
            for (Class<? extends PluginDefinition> enabledPlugin : enabledPlugins) {
                for (PluginDefinition existedPlugin : pluginDefinitions) {
                    if (enabledPlugin.equals(existedPlugin.getClass())) {
                        list.add(existedPlugin);
                    }
                }
            }
            return list;
        }
    }

    /**
     * 读取meta中配置的有效插件
     * @return
     */
    private List<Class<? extends PluginDefinition>> getDeclaredPlugins() {
        List<Class<? extends PluginDefinition>> plugins = new ArrayList<>();
        for (String definition : getMetaData().getEnabledPlugins()) {
            try {
                plugins.add((Class<? extends PluginDefinition>) Class.forName(definition));
            } catch (ClassNotFoundException e) {
                logger.error("plugin class[{}] is not found!", definition);
                continue;
            }
        }
        return plugins;
    }

    /**
     * 获取外部声明的插件组
     * @return
     */
    private List<Class<? extends PluginDefinition>> getDeclaredPluginGroups() {
        List<Class<? extends PluginDefinition>> definitions = new ArrayList<>();
        for (String definition : getMetaData().getEnabledPluginGroups()) {
            try {
                Class<?> clz = Class.forName(definition);
                if (PluginDefinition.class.isAssignableFrom(clz)) {
                    definitions.add((Class<? extends PluginDefinition>) clz);
                } else {
                    logger.error("[{}] is not a valid plugin group class!", definition);
                }
            } catch (ClassNotFoundException e) {
                logger.error("plugin group[{}] is not existed!", definition);
                continue;
            }
        }
        return definitions;
    }

    public TraceInfoCollector getOrInitCollector() {
        if (null == collector) {
            synchronized (this) {
                if (null == collector) {
                    try {
                        collector = (TraceInfoCollector) Class.forName(getMetaData().getTraceCollectorClz()).newInstance();
                        collector.init(getMetaData().getCollectorServerHost(), getMetaData().getCollectorServerPort());
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
        return collector;
    }
}
