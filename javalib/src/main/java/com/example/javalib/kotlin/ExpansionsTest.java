package com.example.javalib.kotlin;

import com.example.kotlinlib.extension.ExtensiionsDemoKt;

/**
 * author : linmh
 * date : 2021/1/25 16:57
 * description :
 */
public class ExpansionsTest {
    public static void main(String[] args) {
        String str = "222";
        int length = ExtensiionsDemoKt.moniLength(str);
        System.out.println(length);

    }
}
