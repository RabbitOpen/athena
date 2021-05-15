package rabbit.open.athena.plugin.common;

/**
 * 类文件增强接口
 */
public interface ClassEnhancer {

    void beforeMethod();

    void afterMethod();

    void onException();
}
