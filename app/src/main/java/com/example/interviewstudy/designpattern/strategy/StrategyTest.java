package com.example.interviewstudy.designpattern.strategy;

/**
 * Author:mihon
 * Time: 2019\5\29 0029.16:10
 * Description:This is StrategyTest
 */
public class StrategyTest {
    public static void main(String[] args){
        Traget traget = new Traget(new AddOperation());
        System.out.println(traget.executeOpt(20,30));
        //传入的不同策略，这些策略的算法不同
        traget = new Traget(new SubOperation());
        //导致了不同结果
        System.out.println(traget.executeOpt(20,30));
    }
}
