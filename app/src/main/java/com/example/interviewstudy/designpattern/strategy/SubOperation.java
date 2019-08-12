package com.example.interviewstudy.designpattern.strategy;

/**
 * Author:mihon
 * Time: 2019\5\29 0029.16:09
 * Description:This is AddOperation
 */
public class SubOperation implements Strategy {
    @Override
    public int doOpt(int num1, int num2) {
        return num1 - num2;
    }
}
