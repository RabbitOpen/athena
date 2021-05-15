package rabbit.open.athena.plugin.common.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import rabbit.open.athena.plugin.common.AthenaPluginDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代理配置
 */
public class AthenaMetaData {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Property("agent.plugin.enabledPlugins")
    private List<String> enabledPlugins = new ArrayList<>();

    /**
     * 获取申明的有效插件
     * @return
     */
    public List<Class<? extends AthenaPluginDefinition>> getEnabledPlugins() {
        List<Class<? extends AthenaPluginDefinition>> plugins = new ArrayList<>();
        for (String definition : enabledPlugins) {
            try {
                plugins.add((Class<? extends AthenaPluginDefinition>) Class.forName(definition));
            } catch (ClassNotFoundException e) {
                logger.error("plugin[{}] is not existed!", definition);
                continue;
            }
        }
        return plugins;
    }

    /**
     * 根据配置文件解析配置数据
     * @param yml
     * @return
     */
    public static AthenaMetaData readBy(String yml) {
        Yaml yaml = new Yaml();
        Iterable<Object> all = yaml.loadAll(AthenaMetaData.class.getResourceAsStream(yml));
        if (!all.iterator().hasNext()) {
            return null;
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
        StringBuilder sb = new StringBuilder(getClass().getSimpleName() + " : [{");
        for (int i = 0; i < AthenaMetaData.class.getDeclaredFields().length; i++) {
            Field field = AthenaMetaData.class.getDeclaredFields()[i];
            try {
                Object value = getGetter(field).invoke(this);
                if (null == value || null == field.getAnnotation(Property.class)) {
                    continue;
                }
                sb.append("\n\t" + field.getName() + " : " + asText(value));
                if (i != AthenaMetaData.class.getDeclaredFields().length - 1) {
                    sb.append(", ");
                }
            } catch (NoSuchMethodException e) {
                continue;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        sb.append("\n}]");
        return sb.toString();
    }

    private Method getGetter(Field field) throws NoSuchMethodException {
        return getClass().getDeclaredMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
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
