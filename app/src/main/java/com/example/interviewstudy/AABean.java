package com.example.interviewstudy;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * author : linmh
 * date : 2021/3/5 18:28
 * description :
 */
public class AABean implements Serializable {
    @SerializedName(value = "haha", alternate = "userName")
    public String haha;
    @SerializedName(value = "nn", alternate = "userNum")
    public int nn;
}
