package rabbit.open.athena.test;

import com.org.test.bean.User;
import com.org.test.bean.User1;
import junit.framework.TestCase;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import rabbit.open.athena.agent.core.AthenaAgent;
import rabbit.open.athena.plugin.common.PluginDefinition;
import rabbit.open.athena.plugin.common.context.PluginContext;

import java.util.List;

@RunWith(JUnit4.class)
public class PluginContextTest {

    @Test
    public void pluginContextTest() {
        PluginContext context = PluginContext.initPluginContext("athena-config.yml");
        List<PluginDefinition> enabledPlugins = context.getEnabledPlugins();
        TestCase.assertEquals(5, enabledPlugins.size());

        context = PluginContext.initPluginContext("athena-config-1.yml");
        TestCase.assertEquals(1, context.getMetaData().getEnabledPlugins().size());

        TestCase.assertEquals(1, context.getEnabledPlugins().size());

        // 和"com.org.test.bean.User"匹配的插件有两个
        TypeDescription typeDescription = TypePool.Default.ofSystemLoader()
                .describe("com.org.test.bean.User").resolve();
        List<PluginDefinition> plugins = context.getMatchedPlugins(typeDescription);
        TestCase.assertEquals(1, plugins.size());

        // 只增强了的成员方法
        AthenaAgent.premain("athena-config-1.yml", ByteBuddyAgent.install());
        TestCase.assertNull(new User().getName());
        TestCase.assertEquals("User", User.type());
    }

    /**
     * 多重增强测试
     */
    @Test
    public void multiEnhanceTest() {
        AthenaAgent.premain("athena-config-2.yml", ByteBuddyAgent.install());
        TestCase.assertEquals("lili-hello-world", new User1().getName());
        TestCase.assertEquals("User1-static", User1.type());
    }

    @Test
    public void matcherTest() {
        PluginContext context = PluginContext.initPluginContext("athena-config.yml");
        String name = PluginContext.class.getName();
        TypeDescription typeDescription = TypePool.Default.ofSystemLoader().describe(name).resolve();
        TestCase.assertTrue(!context.getExcludesMatcher().matches(typeDescription));

        name = "com.org.test.bean.User";
        typeDescription = TypePool.Default.ofSystemLoader().describe(name).resolve();
        TestCase.assertTrue(context.getExcludesMatcher().matches(typeDescription));

        TestCase.assertTrue(ElementMatchers.named(name).and(context.getExcludesMatcher()).matches(typeDescription));
        TestCase.assertTrue(context.getExcludesMatcher().and(ElementMatchers.named(name)).matches(typeDescription));


    }

}
