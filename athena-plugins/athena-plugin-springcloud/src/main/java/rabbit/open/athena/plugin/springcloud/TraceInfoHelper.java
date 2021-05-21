package rabbit.open.athena.plugin.springcloud;

import rabbit.open.athena.client.trace.TraceInfo;

import java.util.ArrayList;
import java.util.List;

public class TraceInfoHelper {

    // spring cloud传递 traceinfo 信息时使用的key
    public static final String ATHENA_TRACE_INFO = "$athena-trace-info-header-key";

    /**
     * trace info转list
     * @param traceInfo
     * @return
     */
    public static List<String> traceInfo2List(TraceInfo traceInfo) {
        List<String> list = new ArrayList<>();
        list.add(traceInfo.getTraceId() + "," + new Integer(traceInfo.getDepth()).toString()
            + "," + new Integer(traceInfo.incrementChild()).toString());
        return list;
    }

    /**
     * string转trace info
     * @param info
     * @return
     */
    public static TraceInfo string2TraceInfo(String info) {
        TraceInfo traceInfo = new TraceInfo();
        String[] fields = info.split(",");
        traceInfo.setTraceId(fields[0]);
        traceInfo.setDepth(Integer.parseInt(fields[1]));
        traceInfo.setExecuteOrder(Integer.parseInt(fields[2]));
        return traceInfo;
    }
}
