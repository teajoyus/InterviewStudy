package com.example.javalib.nowcoder;

/**
 * 跟Fibonacci是一样的
 */
public class 跳台阶 {
    public static void main(String[] args) {
        System.out.println(new 跳台阶().JumpFloor(4));
    }

    public int JumpFloor(int target) {
        if (target < 2) {
            return target;
        }
        int dp[] = new int[target];
        dp[0] = 1;
        dp[1] = 2;
        for (int i = 2; i < target; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[target-1];
    }
}
