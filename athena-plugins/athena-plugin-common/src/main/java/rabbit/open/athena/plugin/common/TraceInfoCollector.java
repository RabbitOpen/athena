package rabbit.open.athena.plugin.common;

import rabbit.open.athena.client.trace.TraceInfo;

import java.util.List;

public interface TraceInfoCollector {

    /**
     * 开启消息收集
     * @param traceInfo
     */
    void doCollection(TraceInfo traceInfo);

    void doCollection(List<TraceInfo> traceInfoList);

    /**
     * 初始化收集器
     * @param host
     * @param port
     */
    void init(String host, int port);

}
