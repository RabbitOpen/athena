package com.org.athena.test.trace;

import rabbit.open.athena.client.trace.TraceInfo;
import rabbit.open.athena.plugin.common.TraceInfoCollector;

import java.util.List;

public class SampleTraceInfoCollector implements TraceInfoCollector {

    TraceInfoCollector proxy;

    public void setProxy(TraceInfoCollector proxy) {
        this.proxy = proxy;
    }

    @Override
    public void doCollection(TraceInfo traceInfo) {
        this.proxy.doCollection(traceInfo);
    }

    @Override
    public void doCollection(List<TraceInfo> traceInfoList) {
        this.proxy.doCollection(traceInfoList);
    }

    @Override
    public void init(String host, int port) {

    }
}
