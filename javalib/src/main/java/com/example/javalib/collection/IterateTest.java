package com.example.javalib.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author linmh
 * @email 625321521@qq.com
 * @date 2020-09-17 15:02
 * @desc
 */
class IterateTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(String.valueOf(i));
        }
        Iterator<String> iterator = list.iterator();
    }

}
