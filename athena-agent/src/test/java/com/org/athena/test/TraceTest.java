package com.org.athena.test;

import com.org.athena.test.service.UserService;
import com.org.athena.test.trace.ExecuteCostTraceInfo;
import com.org.athena.test.trace.SampleTraceInfoCollector;
import com.org.test.bean.User;
import junit.framework.TestCase;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.agent.core.AthenaAgent;
import rabbit.open.athena.plugin.common.TraceInfo;
import rabbit.open.athena.plugin.common.TraceInfoCollector;
import rabbit.open.athena.plugin.common.context.PluginContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class TraceTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void traceTest() throws InterruptedException {
        List<TraceInfo> traceInfoList = new ArrayList<>();
        AthenaAgent.premain("trace.yml", ByteBuddyAgent.install());
        SampleTraceInfoCollector collector = (SampleTraceInfoCollector) PluginContext.getContext().getOrInitCollector();
        collector.setProxy(new TraceInfoCollector() {

            @Override
            public void doCollection(TraceInfo traceInfo) {
                traceInfoList.add(traceInfo);
            }

            @Override
            public void doCollection(List<TraceInfo> traceInfoList) {

            }

            @Override
            public void init(String host, int port) {

            }
        });

//        new UserService().doSomething(10, "hello");
//        new UserService().doSomething(10, "hello");
        int threadCount = 12;
        CountDownLatch cdl = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                new UserService().doSomething(10, "hello");
                cdl.countDown();
            }).start();
        }
        cdl.await();

        logger.info("size: {}", traceInfoList.size());
        TestCase.assertEquals(4 * 12, traceInfoList.size());
        Map<String, List<TraceInfo>> map = traceInfoList.stream().collect(Collectors.groupingBy(TraceInfo::getTraceId, Collectors.toList()));
        TestCase.assertEquals(threadCount, map.size());
        for (List<TraceInfo> list : map.values()) {
            TestCase.assertEquals(4, list.size());
            list.sort((o1, o2) -> {
                if (o1.getDepth() == o2.getDepth()) {
                    return Integer.compare(o1.getExecuteOrder(), o1.getExecuteOrder());
                }
                return Integer.compare(o1.getDepth(), o2.getDepth());
            });
            TestCase.assertEquals(0, list.get(0).getDepth());
            TestCase.assertEquals(1, list.get(1).getDepth());
            TestCase.assertEquals(1, list.get(2).getDepth());
            TestCase.assertEquals(2, list.get(3).getDepth());


            TestCase.assertEquals(0, list.get(0).getExecuteOrder());
            TestCase.assertEquals(0, list.get(1).getExecuteOrder());
            TestCase.assertEquals(1, list.get(2).getExecuteOrder());
            TestCase.assertEquals(0, list.get(3).getExecuteOrder());
        }

    }
}
