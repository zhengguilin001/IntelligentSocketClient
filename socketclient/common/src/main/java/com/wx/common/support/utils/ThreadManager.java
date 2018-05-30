package com.wx.common.support.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理的工具类，封装类
 *
 * @author ThinkPad
 *         线程池的管理 ，通过java 中的api实现管理
 *         采用conncurrent框架： 非常成熟的并发框架 ，特别在匿名线程管理非常优秀的
 */
public class ThreadManager {
    /**
     * //通过ThreadpollProxy的代理类来对线程池管理
     */
    private static ThreadPollProxy mThreadPollProxy;

    public static ThreadPollProxy getThreadPollProxy() {
        synchronized (ThreadPollProxy.class) {
            if (mThreadPollProxy == null) {
                mThreadPollProxy =
                        new ThreadPollProxy(1, 1, 1000);
            }
        }
        return mThreadPollProxy;
    }

    /**
     * //通过ThreadPoolExecutor的代理类来对线程池的管理
     */
    public static class ThreadPollProxy {
        private ThreadPoolExecutor poolExecutor;//线程池执行者 ，java内部通过该api实现对线程池管理
        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime;

        public ThreadPollProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
            poolExecutor = new ThreadPoolExecutor(this.corePoolSize,
                    this.maximumPoolSize, this.keepAliveTime,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    Executors.defaultThreadFactory());
        }

        /**
         * 对外提供一个执行任务的方法
         *
         * @param runnable
         */
        public void execute(Runnable runnable) {
            if (poolExecutor == null || poolExecutor.isShutdown()) {
                poolExecutor = new ThreadPoolExecutor(
                        corePoolSize,
                        maximumPoolSize,
                        keepAliveTime,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        Executors.defaultThreadFactory()
                );
            }
            poolExecutor.execute(runnable);
        }
    }


}
