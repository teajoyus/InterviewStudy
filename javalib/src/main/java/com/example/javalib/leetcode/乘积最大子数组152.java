package com.example.javalib.leetcode;

/**
 * 这个是动态规划的进阶版本 值得学习。
 * 在这里新学习到了一个DP状态的概念
 * 如果按照简单版本的DP来规划的话，会发现如果f(i-1)乘以一个负数的话会让数据更小
 * 而这时候的连续乘积子数组就结束了，然后遇到一个数据，这时候无论是f(i)还是f(i-1)都不是当前的最优连续乘积子数组
 * 因为在这里断开后，有可能有以i为右端点的新数组与当前数据组成最大乘积数
 * 比如[2,3,-2,-5]，、可以看出f(0) = 2 f(1) = 6,但是当计算到f(2)的时候发现-2是负数不行，所以f(2)就还是为6
 * 但是后面的f(3)的时候发现并不能拿f(2)来计算，因为f(3)我们也可能看出它的最大成绩和应该是数组本身
 * <p>
 * 所以很明显用f(i)并表示不了最优子结构
 *
 * <p>
 * 所以首先我们要有DP状态的概念，然后分DP情况来表示状态转移方程。
 * 切入点就是当num[i]>0时，那么连续成绩和就可以是f(i-1)*num[i]，同时也要判断这个结果跟num[i]两者取最大值
 * 但是如果num[i]<0的话，我们其实是需要前面有一个连续的最小的乘积，因为这样成上去后才会反应出一个更大的乘积，
 * 当然还是要再判断这个结果跟num[i]最大值，
 * 因为如果之前的最小乘积还是大于0的话，这时候的乘积是要比num[i]这个数要小的
 * <p>
 * 所以我们除了要用f(i)来表示最大乘积的同时，也需要用另外一个fmin(i)来表示最小的连续乘积和
 * 这样DP状态就有两个，一个是求最大连续乘积的，一个是求最小连续乘积的
 * <p>
 * 所以当num[i]>0时，有
 * f(i) = max(num[i],f(i-1)*num[i])
 * fmin(i) = min(num[i],fmin(i-1)*num[i])
 * <p>
 * 当num[i]<=0时，有
 * f(i) = max(num[i],fmin(i-1)*num[i])
 * fmin(i) = min(num[i],f(n-1)*num[i])
 * <p>
 * 这样就有了状态转移方程，由于这里引入了2个DP状态的概念，所以对应有2个状态转换方程
 */
public class 乘积最大子数组152 {
    public static void main(String[] args) {
        System.out.println(new 乘积最大子数组152().maxProduct(new int[]{2, 3, -2, 4}));
    }

    public int maxProduct(int[] nums) {
        int[] result = new int[nums.length];
        int[] resultMin = new int[nums.length];
        result[0] = nums[0];
        resultMin[0] = nums[0];
        int ans = nums[0];
        for (int i = 1; i < nums.length; i++) {

            if (nums[i] > 0) {
                result[i] = Math.max(nums[i], result[i - 1] * nums[i]);
                //resuleMin[i]记录以i为最右端的最小的连续乘积和，那么乘以nums[i]就可以得到一个更小的乘积和
                resultMin[i] = Math.min(nums[i], resultMin[i - 1] * nums[i]);
            } else {
                result[i] = Math.max(nums[i], resultMin[i - 1] * nums[i]);
                //只有一个是result[i-1]*nums[i] 不是resultMin[i-1]*nums[i]
                //那是因为num[i]已经是非正数了，那么必须乘以前面最大乘积才能得到一个最小的乘积和
                resultMin[i] = Math.min(nums[i], result[i - 1] * nums[i]);

            }
            //完了再匹配一下新加的是否大于上一个连续乘积和
            //如果得到的连续乘积比上次的小，那就不要了 还是保留上次的
            ans = Math.max(ans, result[i]);
        }
        return ans;
    }
}

