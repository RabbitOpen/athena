package com.org.athena.test.service;


import rabbit.open.athena.client.wrapper.CallableWrapper;
import rabbit.open.athena.client.wrapper.RunnableWrapper;

import java.util.concurrent.*;

public class UserService extends BaseService {

    // 线程增强测试
    ThreadPoolExecutor tpe = new ThreadPoolExecutor(3, 3, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            r.run();
        }
    });


    public void asyncRun(Runnable task) {
        tpe.submit(RunnableWrapper.of(task));
    }

    public void asyncRun(Callable task) {
        tpe.submit(CallableWrapper.of(task));
    }
}
