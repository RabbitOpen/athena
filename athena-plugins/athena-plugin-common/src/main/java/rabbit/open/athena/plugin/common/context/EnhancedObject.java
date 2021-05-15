package rabbit.open.athena.plugin.common.context;

/**
 * 增强对象
 */
public class EnhancedObject {

    private Object realObject;

    public EnhancedObject() {
        this(null);
    }

    public EnhancedObject(Object realObject) {
        this.realObject = realObject;
    }

    public Object getRealObject() {
        return realObject;
    }
}
