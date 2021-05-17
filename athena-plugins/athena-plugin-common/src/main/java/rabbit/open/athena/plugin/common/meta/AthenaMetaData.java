package rabbit.open.athena.plugin.common.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代理配置
 */
public class AthenaMetaData {

    Logger logger = LoggerFactory.getLogger(getClass());

    // 应用端配置的有效插件
    @Property("agent.plugin.enabledPlugins")
    private List<String> enabledPlugins = new ArrayList<>();

    // 应用端配置的有效插件组
    @Property("agent.plugin.enabledPluginGroups")
    private List<String> enabledPluginGroups = new ArrayList<>();

    // 接入应用名
    @Property("agent.application.name")
    private String applicationName = "app";

    public List<String> getEnabledPlugins() {
        return enabledPlugins;
    }

    public List<String> getEnabledPluginGroups() {
        return enabledPluginGroups;
    }

    /**
     * 根据配置文件解析配置数据
     * @param yml
     * @return
     */
    public static AthenaMetaData readBy(String yml) {
        Yaml yaml = new Yaml();
        Iterable<Object> all = yaml.loadAll(AthenaMetaData.class.getResourceAsStream("/" + yml));
        if (!all.iterator().hasNext()) {
            return new AthenaMetaData();
        }
        return convert((Map<String, Object>) all.iterator().next());
    }

    private static AthenaMetaData convert(Map<String, Object> config) {
        AthenaMetaData athenaMetaData = new AthenaMetaData();
        Map<Field, List<String>> fieldMapping = getFieldMapping();
        for (Map.Entry<Field, List<String>> entry : fieldMapping.entrySet()) {
            Map<String, Object> target = config;
            for (int i = 0; i < entry.getValue().size(); i++) {
                String nodeName = entry.getValue().get(i);
                Object value = target.get(nodeName);
                if (!target.containsKey(nodeName)) {
                    continue;
                }
                if (i == entry.getValue().size() - 1) {
                    Field field = entry.getKey();
                    field.setAccessible(true);
                    try {
                        field.set(athenaMetaData, value);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    target = (Map<String, Object>) value;
                }
            }
        }
        return athenaMetaData;
    }

    /**
     * 获取字段和属性的映射关系
     * @return
     */
    private static Map<Field, List<String>> getFieldMapping() {
        Map<Field, List<String>> mapping = new HashMap<>();
        for (Field field : AthenaMetaData.class.getDeclaredFields()) {
            ArrayList<String> list = new ArrayList<>();
            Property property = field.getAnnotation(Property.class);
            if (null == property || "".equals(property.value().trim())) {
                continue;
            } else {
                for (String s : property.value().trim().split("\\.")) {
                    list.add(s);
                }
            }
            mapping.put(field, list);
        }
        return mapping;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder("[{");
        for (int i = 0; i < AthenaMetaData.class.getDeclaredFields().length; i++) {
            Field field = AthenaMetaData.class.getDeclaredFields()[i];
            try {
                Object value = field.get(this);
                if (null == value || null == field.getAnnotation(Property.class)) {
                    continue;
                }
                sb.append("\n\t" + field.getName() + " : " + asText(value));
                if (i != AthenaMetaData.class.getDeclaredFields().length - 1) {
                    sb.append(", ");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        sb.append("\n}]");
        return sb.toString();
    }

    private Object asText(Object value) {
        if (value instanceof List) {
            StringBuilder sb = new StringBuilder("[");
            List<Object> list = (List<Object>) value;
            for (int i = 0; i < list.size(); i++) {
                sb.append(asText(list.get(i)));
                if (i != list.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        } else if (value instanceof Class) {
            return ((Class<?>) value).getName();
        } else {
            return value.toString();
        }
    }
}
