package rabbit.open.athena.plugin.common;

/**
 * 跟踪信息
 */
public abstract class TraceInfo {

    private String traceId;

    private TraceInfo parent;

    private String targetClzName;

    // 带参数类型
    private String methodName;

    // 函数名
    private String simpleMethodName;

    // 跟节点标识
    private boolean isRoot = false;

    // 深度
    private int depth = 0;

    // 同一深度下的执行顺序
    private int executeOrder = 0;

    // 直接子节点
    private int directChildCount = 0;

    // 所属服务
    private String appName;

    private boolean exceptionOccurred = false;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public TraceInfo getParent() {
        return parent;
    }

    public void setParent(TraceInfo parent) {
        this.parent = parent;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getExecuteOrder() {
        return executeOrder;
    }

    public void setExecuteOrder(int executeOrder) {
        this.executeOrder = executeOrder;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int incrementChild() {
        return directChildCount++;
    }

    public boolean isExceptionOccurred() {
        return exceptionOccurred;
    }

    public void setExceptionOccurred(boolean exceptionOccurred) {
        this.exceptionOccurred = exceptionOccurred;
    }

    public String getTargetClzName() {
        return targetClzName;
    }

    public void setTargetClzName(String targetClzName) {
        this.targetClzName = targetClzName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getSimpleMethodName() {
        return simpleMethodName;
    }

    public void setSimpleMethodName(String simpleMethodName) {
        this.simpleMethodName = simpleMethodName;
    }
}
