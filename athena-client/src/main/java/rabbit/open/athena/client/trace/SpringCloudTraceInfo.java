package rabbit.open.athena.client.trace;

public class SpringCloudTraceInfo extends TraceInfo {

    // 请求路径
    private String requestURL;

    // 当前服务
    private String currentService;

    // 目标服务
    private String remoteService;

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getCurrentService() {
        return currentService;
    }

    public void setCurrentService(String currentService) {
        this.currentService = currentService;
    }

    public String getRemoteService() {
        return remoteService;
    }

    public void setRemoteService(String remoteService) {
        this.remoteService = remoteService;
    }
}
