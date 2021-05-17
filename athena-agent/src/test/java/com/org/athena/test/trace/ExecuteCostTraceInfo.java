package com.org.athena.test.trace;

import rabbit.open.athena.plugin.common.TraceInfo;

/**
 * 执行耗时
 */
public class ExecuteCostTraceInfo extends TraceInfo {

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
}
