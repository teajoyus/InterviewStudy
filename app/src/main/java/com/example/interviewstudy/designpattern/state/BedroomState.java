package com.example.interviewstudy.designpattern.state;

/**
 * Author:mihon
 * Time: 2019\5\29 0029.17:09
 * Description:This is BedroomState
 */
public class BedroomState implements State {
    @Override
    public void openLight() {
        System.out.println("卧室的灯亮了");
    }
}
