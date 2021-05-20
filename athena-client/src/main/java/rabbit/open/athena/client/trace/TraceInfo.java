package rabbit.open.athena.client.trace;

/**
 * 跟踪信息
 */
public class TraceInfo {

    private String traceId;

    private TraceInfo parent;

    private String threadName;

    private String targetClzName;

    // 操作名
    private String name;

    // 函数名（带参数类型）
    private String fullMethodName;

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

    // 异常信息
    private String errMsg;

    private Long start;

    private Long end;

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullMethodName() {
        return fullMethodName;
    }

    public void setFullMethodName(String fullMethodName) {
        this.fullMethodName = fullMethodName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
