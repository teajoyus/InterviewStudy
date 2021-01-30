package com.example.javalib.leetcode;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class 不同路径62 {
    public static void main(String[] args) {
        System.out.println(new 不同路径62().uniquePaths2(51, 9));
        System.out.println(new 不同路径62().uniquePaths(51, 9));
    }

    /**
     * 这个做法其实跟斐波那契数列一样，他不是动态规划的
     * 因为每次局部计算还好的结果，下一步还是要继续计算这个局部
     * 所以在leetcode上面显示超时了
     *
     * @param m
     * @param n
     * @return
     */
    public int uniquePaths2(int m, int n) {
        if (m == 1 || n == 1) {
            return 1;
        }
        int result = 0;
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                result += uniquePaths2(i - 1, j) + uniquePaths2(i, j - 1);
            }
        }
        return result;
    }

    /**
     * 这个就在leetcode通过了，因为就利用了动态规划的思想，
     * 其实就是把之前局部计算好的结果存储起来，这样就不用每次递归继续去求局部结果
     *
     * @param m
     * @param n
     * @return
     */
    public int uniquePaths(int m, int n) {
        int[][] paths = new int[m][n];
        Arrays.fill(paths[0], 1);
        for (int i = 0; i < paths.length; i++) {
            paths[i][0] = 1;
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                paths[i][j] = paths[i - 1][j] + paths[i][j - 1];
            }
        }
        return paths[m - 1][n - 1];
    }
}
