package com.example.javalib.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * author : linmh
 * date : 2021/3/15 17:17
 * description :
 * 为什么volatile++不是原子性的？
 */
public class VolatileDemo {
    private volatile int index = 0;
    private AtomicInteger integer = new AtomicInteger(0);

    public static void main(String[] args) {
        VolatileDemo demo = new VolatileDemo();
        for (int i = 0; i < 10000; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //读index
                    //index+1
                    //写index
                    //所以不是一个原子操作，编译器也警告了
                    demo.index++;
                    demo.integer.incrementAndGet();
                }
            });
            thread.start();
        }
        try {
            Thread.sleep(2000);
            System.out.println("index = " + demo.index);
            System.out.println("index = " + demo.integer.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
