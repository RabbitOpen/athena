package rabbit.open.athena.plugin.common;

import net.bytebuddy.description.type.TypeDescription;

import java.util.List;

/**
 * 插件服务
 */
public interface PluginService {

    /**
     * 获取匹配的插件定义
     * @param type
     * @return
     */
    List<PluginDefinition> getMatchedPlugins(TypeDescription type);

    /**
     * 获取有效的插件配置
     * @return
     */
    List<PluginDefinition> getEnabledPlugins();

    /**
     * 加载配置的插件
     */
    void loadPlugins();

}
