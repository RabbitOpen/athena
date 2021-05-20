package rabbit.open.athena.agent.core;

import rabbit.open.athena.plugin.common.PluginDefinition;
import rabbit.open.athena.plugin.customize.CustomizePluginGroup;
import rabbit.open.athena.plugin.jdk.ThreadTracePluginGroup;
import rabbit.open.athena.plugin.springcloud.SpringCloudPluginGroup;

public final class PluginGroup {

    public static final Class<? extends PluginDefinition>[] DEFAULT_PLUGIN_GROUPS = new Class[] {
            ThreadTracePluginGroup.class,            //jdk相关插件组

            SpringCloudPluginGroup.class,            //jdk相关插件组

            CustomizePluginGroup.class,              //自定义插件
    };
}
