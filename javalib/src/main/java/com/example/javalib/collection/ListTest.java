package com.example.javalib.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author linmh
 * @email 625321521@qq.com
 * @date 2020-09-17 10:54
 * @desc
 */
class ListTest {
    public static void main(String[] args) {
        List<Person> list = new ArrayList<>();
        list.add(new Worker1());
        list.add(new Worker2());
        printPersonList(list);

        List<Worker1> work1List = new ArrayList<>();
        work1List.add(new Worker1());
        //会报错，接收参数是List<Person>，而实际上是List<Worker1>， 所以需要方法改成协变,让List<Worker1>变成是List<Person>的子类
//        printPersonList(work1List);
        //这样就可以了
        printPersonList2(work1List);


        printWorker1List(work1List);
        //会报错，因为接收的是List<Worker1>，并不是List<Person>。但是如果一定要这么做，那么可以用逆变
//        printWorker1List(list);
        //这样就不错报错了，用了逆变就可以
        printWorker1List2(list);
    }

    public static void printPersonList(List<Person> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).doWork();
        }
    }

    /**
     * 如果List中的泛型对象类型不一致，即使对象有继承关系也不行，需要用协变 把对象变成？ extends SuperClass 的方式
     * <p>
     * 这样做的好处是只知道父类的List，但是同时要兼容子类List的情况
     *
     * @param list
     */
    public static void printPersonList2(List<? extends Person> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).doWork();
        }
        //当是协变后的list是不能add的，因为不知道具体是什么类型，连add Person也不行
        //所以以下两行都会报错
//        list.add(new Worker1());
//        list.add(new Person());

    }

    public static void printWorker1List(List<Worker1> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).doWork();
        }
    }

    /**
     * 通过逆变 把泛型类的Worker1变成 ? super Worker1， 表示能接受那些Worker1的基类的List
     * <p>
     * 这样做的好处是只知道子类List，为了能兼容父类List的情况
     *
     * @param list
     */
    public static void printWorker1List2(List<? super Worker1> list) {
        for (int i = 0; i < list.size(); i++) {
            //会报错 用了逆变之后，get只能获取Object对象，因为Object是一切类的基类
//            list.get(i).doWork();
            if (list.get(i) instanceof Worker1) {
                ((Worker1) list.get(i)).doWork();
            }
            if (list.get(i) instanceof Person) {
                ((Person) list.get(i)).doWork();
            }
        }
        //Person不可以接收，因为编译期就变成了Worker1了
//            list.add(new Person());
        //Worker1一定是某个类型的子类，所以可以接收
        list.add(new Worker1());
    }


    public static class Person {
        public void doWork() {
            System.out.println("Person doWork");
        }
    }

    public static class Worker1 extends Person {
        @Override
        public void doWork() {
            System.out.println("Worker1 doWork");
        }
    }

    public static class Worker2 extends Person {
        @Override
        public void doWork() {
            System.out.println("Worker2 doWork");
        }
    }
}
