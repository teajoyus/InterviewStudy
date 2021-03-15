package com.example.javalib.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * author : linmh
 * date : 2021/3/15 13:47
 * description :
 * 自定义一个锁。能同时允许两个线程来访问资源
 * 那么就可以利用AQS，实现它的tryAcquireShared和tryReleaseShared方法。
 */
public class TwinsLockDemo {
    public static void main(String[] args) {
        TwinsLockDemo demo = new TwinsLockDemo();
        TwinsLock twinsLock = new TwinsLock();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new MyRunnable(twinsLock));
            thread.start();
        }

    }

    public static class MyRunnable implements Runnable {
        TwinsLock twinsLock;

        public MyRunnable(TwinsLock twinsLock) {
            this.twinsLock = twinsLock;
        }

        @Override
        public void run() {
            twinsLock.lock();
            System.out.println(Thread.currentThread().getName() + "拿到了锁");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            twinsLock.unlock();
            System.out.println(Thread.currentThread().getName() + "释放了锁");

        }
    }


    //写一个最多允许两个线程同时访问的同步锁

    public static class TwinsLock implements Lock {
        private Sync sync = new Sync(2);

        public static class Sync extends AbstractQueuedSynchronizer {
            public Sync(int count) {
                setState(count);
            }

            @Override
            protected int tryAcquireShared(int i) {
                while (true) {
                    int state = getState();
                    int newState = state - i;
                    if (newState<0||compareAndSetState(state, newState)) {
                        return newState;
                    }
                }
            }

            @Override
            protected boolean tryReleaseShared(int i) {
                while (true) {
                    int state = getState();
                    int newState = state + i;
                    if (compareAndSetState(state, newState)) {
                        return true;
                    }
                }
            }
        }

        @Override
        public void lock() {
            sync.acquireShared(1);
        }

        @Override
        public void unlock() {
            sync.releaseShared(1);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
            return false;
        }

        @Override
        public Condition newCondition() {
            return null;
        }
    }
}
