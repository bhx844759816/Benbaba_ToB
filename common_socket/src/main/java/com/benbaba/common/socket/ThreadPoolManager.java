package com.benbaba.common.socket;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工厂
 */
public class ThreadPoolManager {
    private static final int CPU_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_SIZE * 2 + 1;//核心线程数
    private static final int MAX_THREAD_SIZE = 100; //线程池最大线程数量
    private static final long KEEP_ALIVE_TIME = 1;//存活时间
    private TimeUnit unit = TimeUnit.HOURS;
    private ThreadPoolExecutor sPoolExecutor;
    private static ThreadPoolManager INSTANCE;

    public static ThreadPoolManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ThreadPoolManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new ThreadPoolManager();
            }
        }
        return INSTANCE;
    }

    private ThreadPoolManager() {
        sPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,//核心线程数
                MAX_THREAD_SIZE,//最大线程数
                KEEP_ALIVE_TIME,//线程存活时间
                unit,//单位
                new LinkedBlockingDeque<Runnable>(),//任务执行队列
                Executors.defaultThreadFactory(), //创建线程的工厂
                new ThreadPoolExecutor.AbortPolicy() //用来对超出maximumPoolSize的任务的处理策略
        );
    }

    /**
     * 执行任务
     */
    public void execute(Runnable runnable) {
        if (runnable != null)
            sPoolExecutor.execute(runnable);


    }

    /**
     * 从线程池中移除任务
     */
    public void remove(Runnable runnable) {
        if (runnable != null)
            sPoolExecutor.remove(runnable);
    }

    /**
     * 执行callable并返回执行结果
     *
     * @param callable
     * @return
     */
    public <T> Future<T> submit(Callable<T> callable) {

        return sPoolExecutor.submit(callable);
    }

    public <T> Future<T> submit(Runnable runnable, T t) {
        return sPoolExecutor.submit(runnable, t);
    }


}
