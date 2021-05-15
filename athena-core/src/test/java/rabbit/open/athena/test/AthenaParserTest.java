package rabbit.open.athena.test;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.plugin.common.impl.PluginContext;
import rabbit.open.athena.plugin.common.meta.AthenaMetaData;
import rabbit.open.athena.plugin.poc.SimplePluginDefinition;

/**
 * 测试yml解析
 */
@RunWith(JUnit4.class)
public class AthenaParserTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void parseTest() {
        AthenaMetaData metaData = AthenaMetaData.readBy("/athena-config.yml");
        logger.info("config: {}", metaData);
        TestCase.assertEquals(0, metaData.getEnabledPlugins().size());
        metaData = AthenaMetaData.readBy("/empty.yml");
        TestCase.assertNull(metaData);
    }

    @Test
    public void pluginContextTest() {
        PluginContext context = PluginContext.initPluginContext("athena-config-1.yml");
        TestCase.assertEquals(1, context.getMetaData().getEnabledPlugins().size());

        // 因为context尚未加载插件
        TestCase.assertEquals(0, context.getEnabledPlugins().size());

        context.loadPlugins(new Class[] {
                SimplePluginDefinition.class
        });
        TestCase.assertEquals(1, context.getEnabledPlugins().size());
    }
}
