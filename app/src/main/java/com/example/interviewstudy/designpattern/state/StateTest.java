package com.example.interviewstudy.designpattern.state;



/**
 * Author:mihon
 * Time: 2019\5\29 0029.17:11
 * Description:This is StateTest
 */
public class StateTest {
    public static void main(String[] args){
        Target target = new Target();
        //当状态是在房间的时候，那么开的是房间的灯
        target.goBedRoom();
        target.openLight();

        //当状态是在客厅的时候，那么开的是客厅的灯
        target.goLiveRoom();
        target.openLight();

        //从实现逻辑上不需要去通过一个变量记录这个状态，然后用if else判断状态

    }
}
