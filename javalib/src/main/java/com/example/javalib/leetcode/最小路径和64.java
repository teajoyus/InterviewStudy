package com.example.javalib.leetcode;

/**
 * 给定一个包含非负整数的 m x n 网格 grid ，请找出一条从左上角到右下角的路径，使得路径上的数字总和为最小。
 * <p>
 * 说明：每次只能向下或者向右移动一步。
 * https://leetcode-cn.com/problems/minimum-path-sum/
 * <p>
 * 这个题目是自己解出来的。
 * 自己通过动态规划的思想。
 * 首先自己发现了个规律就是：每个点的上一步要么是上面要么是左边（因为每次只能向下或者向右移动一步
 * 那么就好办了，这个点（i,j）的最短路径要么就是上面的点的最短路径+自身，要么就是左边的点的最短路径+自身。
 * 也就是f(i,j) = min(f(i-1,j),f(i,j-1)+g[i,j];
 * 也就是一个点的最短路径（最优解）变分解成了顶部和左部的最优解。
 * 这就求出来了状态转移方程。
 *
 * 这类问题最难的就是求出桩体转移方程 所以自己要先画一画，从最小结构看起慢慢找他们的规律
 *
 *
 * 思考：
 * 2、求出最短路径与第二短的路径的差距
 * 1、要求的打印出完整的最短路径来，而不是求和的结果。算法该如何设计（注意：最短路径并不一定只有一条）
 *
 *
 */
public class 最小路径和64 {
    public static void main(String[] args) {
        int[][] grid = new int[][]{{9, 1, 4, 8}};
//        int[][] grid = new int[][]{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}};
        System.out.println(new 最小路径和64().minPathSum(grid));
    }

    public int minPathSum(int[][] grid) {
        if (grid == null) {
            return 0;
        }
        int[][] result = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (i == 0) {
                    result[i][j] = j > 0 ? result[i][j - 1] : 0;
                } else if (j == 0) {
                    result[i][j] = result[i - 1][j];
                } else {
                    result[i][j] = Math.min(result[i - 1][j], result[i][j - 1]);
                }
                result[i][j] += grid[i][j];
//                System.out.println(result[i][j]);
            }
        }
        return result[result.length - 1][result[0].length - 1];
    }
}
