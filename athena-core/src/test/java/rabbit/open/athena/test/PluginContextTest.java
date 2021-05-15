package rabbit.open.athena.test;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import rabbit.open.athena.plugin.common.AthenaPluginDefinition;
import rabbit.open.athena.plugin.common.impl.PluginContext;
import rabbit.open.athena.plugin.poc.SimplePluginDefinition;

import java.util.List;

@RunWith(JUnit4.class)
public class PluginContextTest {

    @Test
    public void loadTest() {
        PluginContext context = PluginContext.initPluginContext("athena-config.yml");
        context.loadPlugins(new Class[] {
                SimplePluginDefinition.class
        });
        List<AthenaPluginDefinition> enabledPlugins = context.getEnabledPlugins();
        TestCase.assertEquals(2, enabledPlugins.size());

        context = PluginContext.initPluginContext("athena-config-1.yml");
        context.loadPlugins(new Class[] {
                SimplePluginDefinition.class
        });
        enabledPlugins = context.getEnabledPlugins();
        TestCase.assertEquals(1, enabledPlugins.size());
    }
}
