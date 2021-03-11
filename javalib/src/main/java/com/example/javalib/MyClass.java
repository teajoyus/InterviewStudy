package com.example.javalib;

import java.util.ArrayList;

public class MyClass {
    public static void main(String[] args) {
        System.out.println("@22222222222");
        AA aa = new AA();
        new Thread() {
            @Override
            public void run() {
                aa.haha();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                aa.hehe();
            }
        }.start();
        CatchDemo.aa();

    }

    public static class AA {
        public synchronized void haha() {
            System.out.println("haha");
            System.out.println("haha:" + System.currentTimeMillis());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized void hehe() {
            System.out.println("hehe");
            System.out.println("hehe:" + System.currentTimeMillis());
        }
    }

    private static class CatchDemo {
        public static void aa() {
            try {

                bb();
            }catch (Exception e){
                System.out.println("捕获了");
            }
        }

        private static void bb() {
            cc();
        }

        private static void cc() {
            String str = new ArrayList<String>(1).get(10);
        }
    }
}
