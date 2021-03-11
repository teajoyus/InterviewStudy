package com.example.javalib.经典算法;

/**
 * author : linmh
 * date : 2021/3/9 17:46
 * description :
 */
public class 背包问题 {
    public static void main(String[] args) {
        System.out.println(new 背包问题().knapsack(4,3,new int[]{2,1,3},new int[]{4,2,3}));
//        System.out.println(new 背包问题().knapsack(5,3,new int[]{3,5,2},new int[]{5,8,1}));
    }

    /**
     * @param W   背包能承载的重量
     * @param N   物品的个数
     * @param wt  第i个物品的重量
     * @param val 第i个物品的价值
     * @return
     *
     * 对于前i个物品、当前背包剩余的容量为w，这时候可以容纳的价值是dp[i][w]
     * 对于dp[i][w]来说需要择优：
     *
     */
    private int knapsack(int W, int N, int[] wt, int[] val) {
        //dp表示，对于前i个物品、当前背包剩余的容量为w，这时候可以容纳的价值是dp[i][w]
        int[][] dp = new int[N+1][W + 1];
        for (int i = 1; i <= N; i++) {
            for (int w = 1; w <= W; w++) {
                //装不下了
                if (w - wt[i - 1] < 0) {
                    //那就只能选择不装这个物品
                    dp[i][w] = dp[i - 1][w];
                } else {
                    //装入或者不装入背包 需要择优
                    dp[i][w] = Math.max(dp[i - 1][w - wt[i - 1]] + val[i - 1], dp[i - 1][w]);
                }
                System.out.println("i="+i+"w="+w+"dp:"+dp[i][w]);
            }

        }
        return dp[N][W];
    }
}
