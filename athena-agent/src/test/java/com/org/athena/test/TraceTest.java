package com.org.athena.test;

import com.org.athena.test.bean.SimpleService;
import com.org.athena.test.service.UserService;
import com.org.athena.test.trace.SampleTraceInfoCollector;
import junit.framework.TestCase;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.agent.core.AthenaAgent;
import rabbit.open.athena.plugin.common.TraceInfo;
import rabbit.open.athena.plugin.common.TraceInfoCollector;
import rabbit.open.athena.plugin.common.context.PluginContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class TraceTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    ArrayBlockingQueue<TraceInfo> traceInfoList = new ArrayBlockingQueue<>(128);

    @Test
    public void traceTest() throws InterruptedException {

        // 多线程访问，用线程安全的队列
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

        // 第一个调用的速度会慢点，因为需要增强字节码
        new UserService().doSomething(10, "hello");
        int threadCount = 12;
        CountDownLatch cdl = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                new UserService().doSomething(10, "hello");
                cdl.countDown();
            }).start();
        }
        cdl.await();
        threadCount++;
        logger.info("{} size: {}", Thread.currentThread().getName(), traceInfoList.size());
        TestCase.assertEquals(4 * threadCount, traceInfoList.size());
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

            for (TraceInfo info : list) {
                logger.info("traceInfo: {}-{}, method: {}-{}, cost: {}", info.getThreadName(), info.getTraceId(), info.getTargetClzName(), info.getFullMethodName(), (info.getEnd() - info.getStart()));
            }
        }

        traceInfoList = new ArrayBlockingQueue<>(128);
        SimpleService simpleService = new SimpleService();
        simpleService.doSomething();
        TestCase.assertEquals(5, traceInfoList.size());
        map = traceInfoList.stream().collect(Collectors.groupingBy(TraceInfo::getTraceId, Collectors.toList()));
        TestCase.assertEquals(2, map.size());
    }
}
