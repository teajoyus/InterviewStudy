package com.example.javalib.leetcode;

import java.util.List;

/**
 * 这个题目要学学习
 * <p>
 * <p>
 * 给定不同面额的硬币 coins 和一个总金额 amount。编写一个函数来计算可以凑成总金额所需的最少的硬币个数。如果没有任何一种硬币组合能组成总金额，返回 -1。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/coin-change
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * <p>
 * <p>
 * 解题思路：
 * 其实就是一颗N叉树的遍历，比如[1,2,5],100 一开始跟节点是100，它有三个节点分别是99、98、95，然后再继续分下去。
 * 到最后结点为0的刚好就是可以兑换的，它的层级就是个数。而结点小于0的就是不能兑换的。
 * <p>
 * 但是这样的话时间复杂度是指数级别的。
 * 而这类题目可以发现应该用动态规划的思想，因为它有局部最优子结构。
 * 比如说100元 我已经知道99元是需要多少数量的话 ，那么100元就可以直接在99元的基础上+1了。
 * <p>
 * 所以除了要有树的思想，还要有dp的思想来存储最优子结构 避免重复递归
 */
public class Num322 {
//    static int dp[] = new int[100];

    public static void main(String[] args) {
        System.out.println(new Num322().coinChange(new int[]{1, 2, 5}, 100));

    }

    public int coinChange(int[] coins, int amount) {
        return coinChange(new int[amount],coins,amount);
    }

    public int coinChange(int[] count, int[] coins, int amount) {
        if (amount == 0) {
            return 0;
        } else if (amount < 0) {
            return -1;
        }
        if (count[amount - 1] != 0) {
            return count[amount - 1];
        }
        int min = Integer.MAX_VALUE;
        for (int coin : coins) {
            int subProblem = coinChange(count, coins, amount - coin);
            if (subProblem == -1) {
                continue;
            }
            min = Math.min(min, subProblem + 1);
        }
        count[amount - 1] = min == Integer.MAX_VALUE ? -1 : min;
        return count[amount - 1];
    }

}

