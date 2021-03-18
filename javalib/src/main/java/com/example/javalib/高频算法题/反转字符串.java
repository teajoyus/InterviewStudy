package com.example.javalib.高频算法题;

/**
 * author : linmh
 * date : 2021/3/18 15:01
 * description :
 */
public class 反转字符串 {
    public static void main(String[] args) {
        反转字符串 demo = new 反转字符串();
    }
    public String solve (String str) {
        if(str==null||"".equals(str)){
            return str;
        }
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length/2; i++) {
            char t = arr[i];
            arr[i] = arr[arr.length - 1 - i];
            arr[arr.length - 1 - i] = t;
        }
        return new String(arr);
    }
}
