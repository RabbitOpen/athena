package rabbit.open.athena.plugin.common.meta;

import org.yaml.snakeyaml.Yaml;
import rabbit.open.athena.plugin.common.exception.AthenaException;
import rabbit.open.athena.plugin.common.trace.MemoryTraceCollector;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 代理配置
 */
public class AthenaMetaData {

    // 应用端配置的有效插件
    @Property("agent.plugin.enabledPlugins")
    private List<String> enabledPlugins = new ArrayList<>();

    // 应用端配置的有效插件组
    @Property("agent.plugin.enabledPluginGroups")
    private List<String> enabledPluginGroups = new ArrayList<>();

    // 接入应用名
    @Property("spring.application.name")
    private String applicationName;

    // 收集器名字
    @Property("agent.trace.collector.clzName")
    private String traceCollectorClz = MemoryTraceCollector.class.getName();

    // 收集器服务端地址
    @Property("agent.trace.collector.server.host")
    private String collectorServerHost = "localhost";

    // 收集器服务端端口
    @Property("agent.trace.collector.server.port")
    private int collectorServerPort = 8899;

    public List<String> getEnabledPlugins() {
        return enabledPlugins;
    }

    public List<String> getEnabledPluginGroups() {
        return enabledPluginGroups;
    }

    public String getApplicationName() {
        return applicationName;
    }

    /**
     * 根据配置文件解析配置数据
     * @param ymlFile
     * @return
     */
    public static AthenaMetaData initByFile(String ymlFile) {
        return initByFile(ymlFile, fileName -> {
            if (!fileName.startsWith("/")) {
                fileName = "/" + fileName;
            }
            return AthenaMetaData.class.getResourceAsStream(fileName);
        });
    }

    /**
     * 加载文件
     * @param ymlFile
     * @param streamLoader
     * @return
     */
    public static AthenaMetaData initByFile(String ymlFile, Function<String, InputStream> streamLoader) {
        InputStream stream = streamLoader.apply(ymlFile);
        if (null == stream) {
            return new AthenaMetaData();
        }
        Yaml yaml = new Yaml();
        Iterable<Object> all = yaml.loadAll(stream);
        if (!all.iterator().hasNext()) {
            close(stream);
            return new AthenaMetaData();
        }
        AthenaMetaData data = convert((Map<String, Object>) all.iterator().next());
        close(stream);
        return data;
    }

    private static void close(InputStream stream) {
        try {
            stream.close();
        } catch (IOException e) {
            throw new AthenaException(e);
        }
    }

    /**
     * 读取｛ymlFile｝中的值来填充当前对象中的空字段。
     * @param ymlFile
     */
    public void completeEmptyFieldByFile(String ymlFile) {
        AthenaMetaData data = initByFile(ymlFile);
        completeEmptyFieldByMetaData(data);
    }

    public void completeEmptyFieldByMetaData(AthenaMetaData data) {
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if (null != value) {
                    continue;
                }
                field.set(this, field.get(data));
            } catch (Exception e) {
                throw new AthenaException(e);
            }
        }
    }


    private static AthenaMetaData convert(Map<String, Object> config) {
        AthenaMetaData athenaMetaData = new AthenaMetaData();
        Map<Field, List<String>> fieldMapping = getFieldMapping();
        for (Map.Entry<Field, List<String>> entry : fieldMapping.entrySet()) {
            Map<String, Object> target = config;
            for (int i = 0; i < entry.getValue().size(); i++) {
                String nodeName = entry.getValue().get(i);
                if (null == target || !target.containsKey(nodeName)) {
                    continue;
                }
                Object value = target.get(nodeName);
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

    public String getTraceCollectorClz() {
        return traceCollectorClz;
    }

    public String getCollectorServerHost() {
        return collectorServerHost;
    }

    public int getCollectorServerPort() {
        return collectorServerPort;
    }
}
