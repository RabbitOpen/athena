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
    List<AthenaPluginDefinition> getMatchedPlugins(TypeDescription type);

    /**
     * 获取有效的插件配置
     * @return
     */
    List<AthenaPluginDefinition> getEnabledPlugins();

    /**
     * 加载指定的插件
     * @param definitions
     */
    void loadPlugins(List<Class<? extends AthenaPluginDefinition>> definitions);

}
