package com.example.javalib.nowcoder;

/**
 * 这个题目也是用DP来解题的，但是自己没有解出来
 * 原因还是卡在没办法建立状态方程
 * 看了网上的答案，看起来dp存储可以用二维数组来组成所有情况，
 * i和j下标的数据则就是最长字串了
 * 也就是说 建立了二维数组表格，这样子str1的每一个字符对应str2的每一个字符就能发现它对应的最长字串了
 * 而这里的dp状态就是 str1[i]是否等于str2[j]
 * 当str[i]==str[j]时有，f(i,j) = f(i-1,j-1)+1
 * 为什么是i-1和j-1呢，因为i-1和j-1同时代表在str1和str2的前一个字符正对应的最长字串长度
 * 所以要两个都-1
 *
 * 当str1[i]!=str2[j]时，有
 * f(i,j)= 0
 * 为什么可以直接等于0，这样子岂不是前面的最长字串就不见了吗
 *
 * 其实不会的，这也就是dp的好处所在，因为dp在前面已经记录过最长字串了
 * 虽然这里匹配不到变成0了，但是我们还是可以换通过一个变量来记录最长的字串长度
 * 比如 biggest = max(f(i,j),biggest)。
 * 这样子我们一直知道最长字串是多长。
 *
 * 那么题目要的是输出字串 而不是长度
 *
 * 所以有了长度之后我们还要记录对应的起始位置或者是终点位置，这里用终点位置来表示
 * 也 就是每次biggest有更新，则一定是右位置发生了改变 这时候就更新下右位置
 *
 * 最后有了长度 有了右位置，就做下字符串截取了
 *
 */
public class 最长公共字串 {
    public static void main(String[] args) {
        System.out.println(new 最长公共字串().LCS("1AB2345CD", "12345EF"));
        System.out.println(new 最长公共字串().LCS2("1AB2345CD", "12345EF"));
    }

    /**
     * longest common substring
     *
     * @param str1 string字符串 the string
     * @param str2 string字符串 the string
     * @return string字符串
     */
    public String LCS(String str1, String str2) {
        // write code here
        int len1 = str1.length();
        int len2 = str2.length();
        int dp[][] = new int[len1][len2];
        int biggest = 0;
        int maxRightIndex = 0;
        for (int i = 0; i < len1; i++) {
            for (int j = 0; j < len2; j++) {
                boolean b = str1.charAt(i) == str2.charAt(j);
                if (i == 0 || j == 0) {
                    dp[i][j] = b ? 1 : 0;
                } else if (b) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = 0;
                }
                if(biggest<dp[i][j]){
                    biggest = dp[i][j];
                    maxRightIndex = i;
                }
            }
        }
        if(biggest==0){
            return "-1";
        }
        return str1.substring(maxRightIndex - biggest+1,maxRightIndex+1);
    }

    /**
     * 返回公共字串长度的
     * @param str1
     * @param str2
     * @return
     */
    public int LCS2(String str1, String str2) {
        // write code here
        int len1 = str1.length();
        int len2 = str2.length();
        int dp[][] = new int[len1][len2];
        int biggest = 0;
        for (int i = 0; i < len1; i++) {
            for (int j = 0; j < len2; j++) {
                boolean b = str1.charAt(i) == str2.charAt(j);
                if (i == 0 || j == 0) {
                    dp[i][j] = b ? 1 : 0;
                } else if (b) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = 0;
                }
                biggest = Math.max(biggest, dp[i][j]);
            }
        }
        for (int i = 0; i < dp.length; i++) {
            for (int j = 0; j < dp[i].length; j++) {
                System.out.print(dp[i][j]+"\t");
            }
            System.out.println("");
        }
        return biggest;
    }
}
