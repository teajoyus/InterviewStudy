package com.example.interviewstudy.designpattern.strategy;

/**
 * Author:mihon
 * Time: 2019\5\29 0029.16:09
 * Description:This is Traget
 */
public class Traget {
    private Strategy strategy;

    public Traget(Strategy strategy) {
        this.strategy = strategy;
    }
    public int executeOpt(int num1,int num2){
        return strategy.doOpt(num1,num2);
    }
}
