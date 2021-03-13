package com.example.javalib;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * author : linmh
 * date : 2021/3/9 11:13
 * description :
 */
public class MultiThreadDemo {
    public static void main(String[] args) {
//       printAllThreadInfo();
        priorityThread();
    }

    /**
     * 输出所有线程
     */
    private static void printAllThreadInfo(){
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false,false);
        for (int i = 0; i < threadInfos.length; i++) {
            System.out.println("thread id:"+threadInfos[i].getThreadId()+",thread name:"+threadInfos[i].getThreadName()+",state:"+threadInfos[i].getThreadState());

        }
    }
    private static void priorityThread(){
        Thread thread = new Thread("thread-1"){
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 2000000000; i++) {
                    if(i%100000000==0) {
                        System.out.println(Thread.currentThread().getName() + "正在打印：" + i);
                    }
                }
                System.out.println(Thread.currentThread().getName()+"打印完毕"+System.currentTimeMillis());
            }
        };
        Thread thread2 = new Thread("thread-2"){
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 2000000000; i++) {
                    if(i%100000000==0) {
                        System.out.println(Thread.currentThread().getName() + "正在打印：" + i);
                    }
                }
                System.out.println(Thread.currentThread().getName()+"打印完毕"+System.currentTimeMillis());
            }
        };
        thread.setPriority(10);
        thread2.setPriority(10);
        thread.start();
        thread2.start();
    }
}
