package com.org.athena.test;

import com.org.athena.test.bean.SimpleService;
import com.org.athena.test.service.HomeService;
import com.org.athena.test.service.RoleService;
import com.org.athena.test.service.UserService;
import com.org.athena.test.trace.SampleTraceInfoCollector;
import junit.framework.TestCase;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.pool.TypePool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.agent.core.AthenaAgent;
import rabbit.open.athena.client.Traceable;
import rabbit.open.athena.client.trace.TraceInfo;
import rabbit.open.athena.client.wrapper.CallableWrapper;
import rabbit.open.athena.client.wrapper.RunnableWrapper;
import rabbit.open.athena.plugin.common.TraceInfoCollector;
import rabbit.open.athena.plugin.common.context.PluginContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class TraceTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    Semaphore semaphore;

    ArrayBlockingQueue<TraceInfo> traceInfoList;

    @Test
    public void traceTest() throws InterruptedException {
        semaphore = new Semaphore(0);
        traceInfoList = new ArrayBlockingQueue<>(128);
        // 多线程访问，用线程安全的队列
        AthenaAgent.premain("trace.yml", ByteBuddyAgent.install());
        SampleTraceInfoCollector collector = (SampleTraceInfoCollector) PluginContext.getContext().getOrInitCollector();
        collector.setProxy(new TraceInfoCollector() {

            @Override
            public void doCollection(TraceInfo traceInfo) {
                semaphore.release();
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
        int threadCount = 8;
        CountDownLatch cdl = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                new UserService().doSomething(10, "hello");
                cdl.countDown();
            }).start();
        }
        cdl.await();
        threadCount++;
        TestCase.assertEquals(5 * threadCount, traceInfoList.size());
        Map<String, List<TraceInfo>> map = traceInfoList.stream().collect(Collectors.groupingBy(TraceInfo::getTraceId, Collectors.toList()));
        TestCase.assertEquals(threadCount, map.size());
        for (List<TraceInfo> list : map.values()) {
            TestCase.assertEquals(5, list.size());
            list.sort((o1, o2) -> {
                if (o1.getDepth() == o2.getDepth()) {
                    return Integer.compare(o1.getExecuteOrder(), o1.getExecuteOrder());
                }
                return Integer.compare(o1.getDepth(), o2.getDepth());
            });
            TestCase.assertEquals(0, list.get(0).getDepth());
            TestCase.assertEquals(1, list.get(1).getDepth());
            TestCase.assertEquals(2, list.get(2).getDepth());
            TestCase.assertEquals(2, list.get(3).getDepth());
            TestCase.assertEquals(3, list.get(4).getDepth());

            TestCase.assertEquals(0, list.get(0).getExecuteOrder());
            TestCase.assertEquals(0, list.get(1).getExecuteOrder());
            TestCase.assertEquals(0, list.get(2).getExecuteOrder());
            TestCase.assertEquals(1, list.get(3).getExecuteOrder());
            TestCase.assertEquals(0, list.get(4).getExecuteOrder());

        }

        traceInfoList = new ArrayBlockingQueue<>(128);
        SimpleService simpleService = new SimpleService();
        simpleService.doSomething();
        TestCase.assertEquals(6, traceInfoList.size());
        map = traceInfoList.stream().collect(Collectors.groupingBy(TraceInfo::getTraceId, Collectors.toList()));
        TestCase.assertEquals(2, map.size());

        // runnable 异步调用
        asyncRunnableInvokeTest();

        // callable 异步调用
        asyncCallableInvokeTest();
    }

    private void asyncCallableInvokeTest() throws InterruptedException {
        Map<String, List<TraceInfo>> map;
        traceInfoList = new ArrayBlockingQueue<>(128);
        semaphore = new Semaphore(0) ;
        new UserService().asyncRun(() -> {
            new RoleService().doSomething();
            new HomeService().doSomething();
            return 100;
        });
        semaphore.acquire(5);
        map = traceInfoList.stream().collect(Collectors.groupingBy(TraceInfo::getTraceId,
                Collectors.toList()));
        TestCase.assertEquals(1, map.size());
        List<TraceInfo> next = map.values().iterator().next();
        next.sort((o1, o2) -> {
            if (o1.getDepth() == o2.getDepth()) {
                return Integer.compare(o1.getExecuteOrder(), o1.getExecuteOrder());
            }
            return Integer.compare(o1.getDepth(), o2.getDepth());
        });
        TestCase.assertEquals(5, next.size());

        TestCase.assertEquals("asyncRun", next.get(0).getName());
        TestCase.assertEquals(UserService.class.getName(), next.get(0).getTargetClzName());
        TestCase.assertEquals(0, next.get(0).getDepth());

        TestCase.assertEquals("call", next.get(1).getName());
        TestCase.assertEquals(CallableWrapper.class.getName(), next.get(1).getTargetClzName());
        TestCase.assertEquals(1, next.get(1).getDepth());
        TestCase.assertEquals(0, next.get(1).getExecuteOrder());

        TestCase.assertEquals("doSomething", next.get(2).getName());
        TestCase.assertEquals(RoleService.class.getName(), next.get(2).getTargetClzName());
        TestCase.assertEquals(2, next.get(2).getDepth());
        TestCase.assertEquals(0, next.get(2).getExecuteOrder());

        TestCase.assertEquals("doSomething", next.get(3).getName());
        TestCase.assertEquals(HomeService.class.getName(), next.get(3).getTargetClzName());
        TestCase.assertEquals(2, next.get(3).getDepth());
        TestCase.assertEquals(1, next.get(3).getExecuteOrder());

        TestCase.assertEquals("doSomething", next.get(4).getName());
        TestCase.assertEquals(RoleService.class.getName(), next.get(4).getTargetClzName());
        TestCase.assertEquals(3, next.get(4).getDepth());
        TestCase.assertEquals(0, next.get(4).getExecuteOrder());
    }

    private void asyncRunnableInvokeTest() throws InterruptedException {
        Map<String, List<TraceInfo>> map;
        traceInfoList = new ArrayBlockingQueue<>(128);
        semaphore = new Semaphore(0) ;
        new UserService().asyncRun(() -> {
            new RoleService().doSomething();
            new HomeService().doSomething();
        });
        semaphore.acquire(5);
        map = traceInfoList.stream().collect(Collectors.groupingBy(TraceInfo::getTraceId,
                Collectors.toList()));
        TestCase.assertEquals(1, map.size());
        List<TraceInfo> next = map.values().iterator().next();
        next.sort((o1, o2) -> {
            if (o1.getDepth() == o2.getDepth()) {
                return Integer.compare(o1.getExecuteOrder(), o1.getExecuteOrder());
            }
            return Integer.compare(o1.getDepth(), o2.getDepth());
        });
        TestCase.assertEquals(5, next.size());

        TestCase.assertEquals("asyncRun", next.get(0).getName());
        TestCase.assertEquals(UserService.class.getName(), next.get(0).getTargetClzName());
        TestCase.assertEquals(0, next.get(0).getDepth());

        TestCase.assertEquals("run", next.get(1).getName());
        TestCase.assertEquals(RunnableWrapper.class.getName(), next.get(1).getTargetClzName());
        TestCase.assertEquals(1, next.get(1).getDepth());
        TestCase.assertEquals(0, next.get(1).getExecuteOrder());

        TestCase.assertEquals("doSomething", next.get(2).getName());
        TestCase.assertEquals(RoleService.class.getName(), next.get(2).getTargetClzName());
        TestCase.assertEquals(2, next.get(2).getDepth());
        TestCase.assertEquals(0, next.get(2).getExecuteOrder());

        TestCase.assertEquals("doSomething", next.get(3).getName());
        TestCase.assertEquals(HomeService.class.getName(), next.get(3).getTargetClzName());
        TestCase.assertEquals(2, next.get(3).getDepth());
        TestCase.assertEquals(1, next.get(3).getExecuteOrder());

        TestCase.assertEquals("doSomething", next.get(4).getName());
        TestCase.assertEquals(RoleService.class.getName(), next.get(4).getTargetClzName());
        TestCase.assertEquals(3, next.get(4).getDepth());
        TestCase.assertEquals(0, next.get(4).getExecuteOrder());
    }

    private static TypeDescription typeDescriptionOf(Class<?> beanClass) {
        return TypePool.Default.ofSystemLoader().describe(beanClass.getName()).resolve();
    }

    @Test
    public void traceableAnnotationTest() throws InterruptedException {
        AthenaAgent.premain("traceable.yml", ByteBuddyAgent.install());
        semaphore = new Semaphore(0);
        traceInfoList = new ArrayBlockingQueue<>(128);
        // 多线程访问，用线程安全的队列
        SampleTraceInfoCollector collector = (SampleTraceInfoCollector) PluginContext.getContext().getOrInitCollector();
        collector.setProxy(new TraceInfoCollector() {

            @Override
            public void doCollection(TraceInfo traceInfo) {
                traceInfoList.add(traceInfo);
                semaphore.release();
            }

            @Override
            public void doCollection(List<TraceInfo> traceInfoList) {

            }

            @Override
            public void init(String host, int port) {

            }
        });
        new Customer().getUserName();
        Customer.getClassName();
        semaphore.acquire(2);
        TestCase.assertEquals(2, traceInfoList.size());
        TestCase.assertEquals("Customer.getUserName", traceInfoList.poll().getName());
        TestCase.assertEquals("Customer.getClassName", traceInfoList.poll().getName());
    }

    public static class Customer {

        @Traceable(name = "Customer.getUserName")
        public void getUserName() {

        }

        @Traceable(name = "Customer.getClassName")
        public static void getClassName() {

        }
    }
}
