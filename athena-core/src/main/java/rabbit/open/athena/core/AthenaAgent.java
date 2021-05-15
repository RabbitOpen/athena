package rabbit.open.athena.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.plugin.common.AthenaPluginDefinition;
import rabbit.open.athena.plugin.common.impl.PluginContext;
import rabbit.open.athena.plugin.poc.SimplePluginDefinition;

import java.lang.instrument.Instrumentation;

/**
 * 代理入口
 */
public class AthenaAgent {

    static Logger logger = LoggerFactory.getLogger(AthenaAgent.class);

    static Class<? extends AthenaPluginDefinition>[] PLUGIN_DEFINITIONS = new Class[] {
            SimplePluginDefinition.class
    };

    public static void premain(String configFileName, Instrumentation inst) {
        logger.info("athena agent is started!, config file is {}", null == configFileName ? "not specified!" : configFileName);
        PluginContext context = PluginContext.initPluginContext(configFileName);
        context.loadPlugins(PLUGIN_DEFINITIONS);
        context.getEnabledPlugins();

    }
}
