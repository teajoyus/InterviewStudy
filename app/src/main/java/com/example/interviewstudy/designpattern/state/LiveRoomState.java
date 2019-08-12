package com.example.interviewstudy.designpattern.state;

/**
 * Author:mihon
 * Time: 2019\5\29 0029.17:08
 * Description:This is LiveRoomState
 */
public class LiveRoomState implements State {
    @Override
    public void openLight() {
        System.out.println("客厅的灯亮了");
    }
}
