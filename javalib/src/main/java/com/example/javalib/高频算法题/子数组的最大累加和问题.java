package com.example.javalib.高频算法题;

/**
 * 这道题目就是自己解出来的
 * <p>
 * 知道了规律 知道了dp状态，跟那个最大乘积和是同个道理
 * 要判断下一个数如果大于0，那么就是f(i) = f(i-1) + arr[i]
 * 但是如果是小于0，那么我得知道加上这个位置组合成一个以这个i位置为右节点的最长子数组
 * 这样的话如果小于0，那么这个位置肯定不适合累加，但是我也就知道了这个位置之前的最长子数组了，当然还要跟arr[i]取最大值
 * 所以需要定义两个dp状态 两个dp数组，一个用来累加当前的最优子结构
 * 一个用来累加前一个最优子结构+自身节点后的数值
 * 需要判断的是。
 * 如果arr[i]>0，则
 * dp[i] = Math.max(dp2[i - 1] + arr[i], arr[i]);
 * dp[i]需要依赖的前一个最优子数组应该用dp2，因为dp2才发现以i-1为右节点的最优子数组，同时也要判断是否大于arr[i]
 * 另一方面dp2(i)也等同于dp(i)就可以了，因为arr[i]大于0，肯定是要累计上来的
 * <p>
 * 如果arr[i]<0,则
 * dp2[i] = Math.max(arr[i], dp2[i - 1] + arr[i]);
 * 也就是dp2[i]依旧要表示的是以i为右节点的最长子数组，所以arr[i]是一定要加上去的
 * <p>
 * 而dp[i] = Math.max(dp[i - 1], Math.max(arr[i], dp2[i - 1]));
 * 也就是dp[i]不应该再加上了，而是继续保持dp[i-1],当然也要判断arr[i]是否大于dp[i-1]
 *
 *
 * 第二次看：
 * 主要是dp状态。
 * 到第i个，你需要知道到第i个的最大累计和是多少，然后连续和是多少。
 * 这样就可以用dp来解题
 */
public class 子数组的最大累加和问题 {
    public static void main(String[] args) {
        System.out.println(new 子数组的最大累加和问题().maxsumofSubarray(new int[]{-6, 1, -2, 3, -2, -1, 5, -2, 6, -1, -5, -1, 8, -1}));
    }

    /**
     * max sum of the subarray
     *
     * @param arr int整型一维数组 the array
     * @return int整型
     */
    public int maxsumofSubarray(int[] arr) {
        // write code here
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int[] dp = new int[arr.length];
        int[] dp2 = new int[arr.length];
        dp[0] = dp2[0] = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > 0) {
                dp2[i] = dp[i] = Math.max(dp2[i - 1] + arr[i], arr[i]);
            } else {
                dp2[i] = Math.max(arr[i], dp2[i - 1] + arr[i]);
                dp[i] = Math.max(dp[i - 1], arr[i]);
            }
//            System.out.println("arr[i]:" + arr[i] + ",dp[i]:" + dp[i] + ",dp2[i]:" + dp2[i]);
        }
        return dp[dp.length-1];
    }
}
