package com.example.javalib;

public class MyClass {
    public static void main(String[] args) {
        System.out.println("@22222222222");
        AA aa = new AA();
        new Thread(){
            @Override
            public void run() {
                aa.haha();
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                aa.hehe();
            }
        }.start();

    }
    public static class AA{
        public synchronized void haha(){
            System.out.println("haha");
            System.out.println("haha:"+System.currentTimeMillis());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        public synchronized void hehe(){
            System.out.println("hehe");
            System.out.println("hehe:"+System.currentTimeMillis());
        }
    }
}
