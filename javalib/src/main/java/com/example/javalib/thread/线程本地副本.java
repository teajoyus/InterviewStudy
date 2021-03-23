package com.example.javalib.thread;

/**
 * author : linmh
 * date : 2021/3/23 19:55
 * description :
 */
public class 线程本地副本 {
    boolean flag = false;
    String name;

    public static void main(String[] args) {
        final 线程本地副本 demo = new 线程本地副本();
        new Thread() {
            @Override
            public void run() {
                super.run();
                System.out.println("thread1 - start");
                while (!demo.flag) {
                    Object obj = new Object();
                    obj.getClass();
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
                System.out.println("thread1 - end");
            }
        }.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
//                System.out.println("thread2 start");
                demo.flag = true;
                demo.name = "123";
//                System.out.println("thread2 end");
            }
        }.start();


    }
}
