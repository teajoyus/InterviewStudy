package com.example.interviewstudy.designpattern.state;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author:mihon
 * Time: 2019\5\29 0029.17:09
 * Description:This is Target
 */
public class Target {
    private State state;
    public void setState(State state) {
        this.state = state;
    }

    /**
     * 来到客厅
     */
    public void goLiveRoom(){
        setState(new LiveRoomState());
    }

    /**
     * 来到房间
     */
    public void goBedRoom(){
        setState(new BedroomState());
    }
    public void openLight(){
        state.openLight();
    }
}
