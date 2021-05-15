package rabbit.open.athena.test;

import com.org.test.bean.User;
import com.org.test.bean.User1;
import junit.framework.TestCase;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.pool.TypePool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import rabbit.open.athena.core.AthenaAgent;
import rabbit.open.athena.plugin.common.AthenaPluginDefinition;
import rabbit.open.athena.plugin.common.impl.PluginContext;

import java.util.List;

@RunWith(JUnit4.class)
public class PluginContextTest {

    @Test
    public void pluginContextTest() {
        PluginContext context = PluginContext.initPluginContext("athena-config.yml");
        List<AthenaPluginDefinition> enabledPlugins = context.getEnabledPlugins();
        TestCase.assertEquals(5, enabledPlugins.size());

        context = PluginContext.initPluginContext("athena-config-1.yml");
        TestCase.assertEquals(1, context.getMetaData().getEnabledPlugins().size());

        TestCase.assertEquals(1, context.getEnabledPlugins().size());

        // 和"com.org.test.bean.User"匹配的插件有两个
        TypeDescription typeDescription = TypePool.Default.ofSystemLoader()
                .describe("com.org.test.bean.User").resolve();
        List<AthenaPluginDefinition> plugins = context.getMatchedPlugins(typeDescription);
        TestCase.assertEquals(2, plugins.size());

        // 只增强了的成员方法
        AthenaAgent.premain("athena-config-1.yml", ByteBuddyAgent.install());
        TestCase.assertNull(new User().getName());
        TestCase.assertEquals("User", User.type());
    }

    @Test
    public void enhanceTest() {
        AthenaAgent.premain("athena-config-2.yml", ByteBuddyAgent.install());
        TestCase.assertEquals("lili-hello-world", new User1().getName());
        TestCase.assertEquals("User1-static", User1.type());
    }

}
