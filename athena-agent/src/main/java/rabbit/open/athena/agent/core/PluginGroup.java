package rabbit.open.athena.agent.core;

import rabbit.open.athena.plugin.common.PluginDefinition;
import rabbit.open.athena.plugin.jdk.JDKPluginGroup;

public final class PluginGroup {

    public static final Class<? extends PluginDefinition>[] DEFAULT_PLUGIN_GROUPS = new Class[] {
            JDKPluginGroup.class
    };
}
