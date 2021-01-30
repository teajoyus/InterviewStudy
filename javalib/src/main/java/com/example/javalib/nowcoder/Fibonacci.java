package com.example.javalib.nowcoder;

/**
 * 不要小看他，不要 用递归 太低效
 * 用DP吧
 */
public class Fibonacci {
    public static void main(String[] args) {
        System.out.println(new Fibonacci().Fibonacci(20));
        System.out.println(new Fibonacci().Fibonacci2(20));
    }

    //0 1 1 2 3 5
    public int Fibonacci(int n) {
        if (n < 2) {
            return n;
        }
        int dp[] = new int[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        for (int i = 2; i < n + 1; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    public int Fibonacci2(int n) {
        if (n < 2) {
            return n;
        }
        int a = 0, b = 1, c = 0;
        for (int i = 2; i < n + 1; i++) {
            c = a + b;
            a = b;
            b = c;
        }
        return c;
    }
}
