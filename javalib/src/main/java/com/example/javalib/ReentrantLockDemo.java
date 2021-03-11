package com.example.javalib;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author linmh
 * @email 625321521@qq.com
 * @date 2020-07-09 16:53
 * @desc
 */
public class ReentrantLockDemo {
    final static ReentrantLock lock = new ReentrantLock(false);
    public static void main(String[] args) {
        for (int i = 1; i < 6; i++) {
            new Thread("线程" + i) {
                @Override
                public void run() {
                    test();
                }
            }.start();
        }
    }


    public static void test() {
        System.out.println("test");
        for (int i = 0; i < 2; i++) {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + "获得了锁");
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}
