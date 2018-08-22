package com.yutao.netutils;

import com.yutao.netutils.listener.OnRequestListener;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 线程池工具
 */
public class ThreadPoolUtils {
    private static ThreadPoolUtils instance;

    private ExecutorService executorService;

    private int maxThread = 4;//最大线程数

    private String THREAD_TAG_DEFAULT = "ThreadPoolUtils";

    private WeakReference<HashMap<String,ExecutorService>> mapWeakReference;

    public synchronized static ThreadPoolUtils getInstance() {
        synchronized (ThreadPoolUtils.class){
            if (instance == null)
                instance = new ThreadPoolUtils();
        }
        return instance;
    }

    private ThreadPoolUtils(){
        init();
    }

    public void init(){
        addDefaultExS();
    }

    /**
     * 初始化一个线程池
     */
    private void addDefaultExS(){
        executorService = Executors.newFixedThreadPool(maxThread);
        HashMap<String,ExecutorService> serviceHashMap = new HashMap<>();
        serviceHashMap.put(THREAD_TAG_DEFAULT,executorService);
        mapWeakReference = new WeakReference<>(serviceHashMap);
    }

    /**
     * 获得线程池
     * @return
     */
    private HashMap<String,ExecutorService> getExecutorServiceMap(){
        if (mapWeakReference == null)
            addDefaultExS();
        HashMap<String,ExecutorService> serviceHashMap = mapWeakReference.get();
        if (serviceHashMap == null)
            addDefaultExS();
        return mapWeakReference.get();
    }

    /**
     * 添加并执行线程
     * @param thread
     * @param threadPoolTag 线程池的标记,可以添加多个线程池，比如一个页面一个线程池，并且，当页面关闭后，将对应线程池的线程全部关闭
     */
    public Future addThread(Thread thread,String threadPoolTag,OnRequestListener onRequestListener){
        if (thread==null)
            return null;
        if(threadPoolTag == null){
            return addThread(thread,onRequestListener);
        }

        HashMap<String,ExecutorService> serviceHashMap = getExecutorServiceMap();
        if (!serviceHashMap.containsKey(threadPoolTag)){
            serviceHashMap.put(threadPoolTag,Executors.newFixedThreadPool(maxThread));
        }
        Future future = serviceHashMap.get(threadPoolTag).submit(thread);
        return future;
    }

    /**
     * 添加并执行一个线程
     * @param thread
     */
    public Future addThread(Thread thread,OnRequestListener onRequestListener){
        return addThread(thread,THREAD_TAG_DEFAULT,onRequestListener);
    }

    /**
     * 停止线程池中的线程
     * @param threadPoolTag
     */
    public void stopThreadPool(String threadPoolTag){
        if (threadPoolTag == null) {
            stopThreadPool();
            return;
        }
        HashMap<String,ExecutorService> serviceHashMap = getExecutorServiceMap();
        if (!serviceHashMap.containsKey(threadPoolTag)){
            return;
        }
        ExecutorService executorService = serviceHashMap.get(threadPoolTag);
        executorService.shutdownNow();
    }

    /**
     * 停止线程中的线程
     */
    public void stopThreadPool(){
        stopThreadPool(THREAD_TAG_DEFAULT);
    }

}
