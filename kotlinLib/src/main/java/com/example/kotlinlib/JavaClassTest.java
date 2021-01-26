package com.example.kotlinlib;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * author : linmh
 * date : 2021/1/26 10:25
 * description :
 */
public class JavaClassTest {
    public static void main(String[] args) {
        MyClass.INSTANCE.haha("222");
        new MethodParamsDemo().setOnClickListener(new Function1<MethodParamsDemo, Unit>() {
            @Override
            public Unit invoke(MethodParamsDemo methodParamsDemo) {
                return null;
            }
        });

    }
}
