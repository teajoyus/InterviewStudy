package com.example.javalib.nowcoder;

/**
 * 牛客上是用dp去解题
 *
 * 但是自己不是用dp解题的 而是用自己的算法来做标记
 * 用一个buy和sale变量来表示买入的最低价格和卖出的最高价格
 * 用一个canSales变量来表示是否允许卖出，
 * 假如当前更新了buy，那么就就不允许卖出了
 * 假如当前更新了sale，那么就可以卖出了
 * 如果是允许卖出的话，就把结果值取最大
 */
public class 买卖股票的最好时机 {
    public static void main(String[] args) {
        System.out.println(new 买卖股票的最好时机().maxProfit(new int[]{8,2,4,9,5,1,2,8}));
//        System.out.println(new 买卖股票的最好时机().maxProfit(new int[]{2, 3, 5, 1, 4, 6, 7, 5}));
    }

    public int maxProfit(int[] prices) {
        // write code here
        if(prices==null||prices.length==0){
            return 0;
        }
//        int dp[] = new int[prices.length];
        int buy = prices[0], sale = Integer.MIN_VALUE;
        int max = 0;
        boolean canSales = false;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] < buy) {
                buy = prices[i];
                canSales = false;
            }
            if (prices[i] > sale) {
                sale = prices[i];
                canSales = true;
            }
//            System.out.println("buy:"+buy+",sale:"+sale);
            if(canSales) {
                max = Math.max(max, sale - buy);
            }
        }
        return max;
    }
}

