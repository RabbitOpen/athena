package rabbit.open.athena.plugin.common.context;

/**
 * 增强对象
 */
public class AgentContext {

    private Object realObject;

    public AgentContext() {
        this(null);
    }

    public AgentContext(Object realObject) {
        this.realObject = realObject;
    }

    public Object getRealObject() {
        return realObject;
    }
}
