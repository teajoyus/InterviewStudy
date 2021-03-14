package com.example.javalib;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class MyClass {
    public static void main(String[] args) {
        System.out.println("@22222222222");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(null, null);
        hashMap.put(null, null);
        System.out.println("hashmap:" + hashMap);
        HashMap<String,String> hashMap1 = new HashMap<>(65);
        hashMap1.put("1","2");
        for (int i = 0; i < 128; i++) {
            hashMap1.put(String.valueOf(i+1000),"2");

        }
        try {
            Field field = HashMap.class.getDeclaredField("table");
            field.setAccessible(true);
            Object[] objects = (Object[]) field.get(hashMap1);
            System.out.println("长度："+objects.length);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000000; i++) {
            int a = i % 16;
        }
        System.out.println(start - System.currentTimeMillis()+"ms");
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000000000; i++) {
            int a = i & 15;
        }
        System.out.println(start - System.currentTimeMillis()+"ms");
        list.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {

            }
        });
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
            } catch (Exception e) {
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
