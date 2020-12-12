package com.example.javalib.leetcode;

public class 三步问题 {
    public static void main(String[] args) {
        System.out.println(new 三步问题().waysToStep(61));
    }

    public int waysToStep(int n) {
        if (n < 3) {
            return n;
        }
        int[] result = new int[n];
        result[0] = 1;
        result[1] = 2;
        result[2] = result[1] + result[0] + 1;
        for (int i = 3; i < n; i++) {
            int temp = (result[i - 1] + result[i - 2])%1000000007;
            result[i] = (temp + result[i - 3])%1000000007;
        }
        return result[n - 1];
    }
}
