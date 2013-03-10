/* @(#) TreadPool.java
 */
package com.beike.util.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 线程池
 * @author wangweijie
 * @version 1.0
 *
 */
public class ThreadPool extends ThreadPoolExecutor{
	private static Log log = LogFactory.getLog(ThreadPool.class);
	private static ThreadPool _instance = null;
	private static final int maximumPoolSize = 10;   //线程池维护线程的最大数量 
		
	private static final int corePoolSize = 5;		//线程池维护线程的最少数量 

	private final static long keepAliveTime = 3;// 3s 
	
	private final static TimeUnit timeUnit = TimeUnit.SECONDS;   //时间单位 秒
	
	@SuppressWarnings("unchecked")
	private static BlockingQueue workQueue;   //线程池所使用的缓冲队列 
	
	private static RejectedExecutionHandler handler;

    private static final int cacheQueueSize = 10000;    //缓存队列大小
    
    @SuppressWarnings("unchecked")
	private ThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue workQueue,
			 RejectedExecutionHandler handler){
    	super(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue, handler);
    }

    @SuppressWarnings("unchecked")
	public synchronized static ThreadPool getInstance(){
    	if(null == _instance){
        	log.info("Thread pool init.....Cache queue size is ["+ cacheQueueSize
        			+ "] The thread pool max number is  [" + maximumPoolSize 
        			+ "] The thread pool min number is [ " + corePoolSize + "]");
        	workQueue = new ArrayBlockingQueue(cacheQueueSize);    //缓冲队列
        	handler = new ThreadPoolExecutor.CallerRunsPolicy(); // 处理方式 重试添加当前的任务，他会自动重复调用execute()方法 
        	_instance = new ThreadPool(corePoolSize, maximumPoolSize, 
        			keepAliveTime, timeUnit, workQueue, handler);
    	}
    	return _instance;
    }
    
    
    @Override
	protected void afterExecute(Runnable r, Throwable t) {
    	
    	log.debug("Thread[" + r.hashCode() + "] completed, There are "
				+ super.getPoolSize() + " threads in thread pool, "
				+ super.getActiveCount() + " threads is executing, "
				+ workQueue.size() + " threads in cache");
		super.afterExecute(r, t);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
    	log.debug("Thread [" + r.hashCode() + "] begin to execute, There are "
				+ super.getPoolSize() + " threads in thread pool, "
				+ super.getActiveCount() + " threads is executing, "
				+ workQueue.size() + " threads in cache");
		super.beforeExecute(t, r);
	}	
	
}