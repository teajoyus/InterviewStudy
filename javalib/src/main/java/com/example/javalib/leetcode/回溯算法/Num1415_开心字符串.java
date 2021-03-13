package com.example.javalib.leetcode.回溯算法;

import java.util.LinkedList;

/**
 * author : linmh
 * date : 2021/3/10 15:14
 * description :
 * 比方说，字符串 "abc"，"ac"，"b" 和 "abcbabcbcb" 都是开心字符串，但是 "aa"，"baa" 和 "ababbc" 都不是开心字符串。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/the-k-th-lexicographical-string-of-all-happy-strings-of-length-n
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * <p>
 * 题解： 第一次就直接AC了
 */
public class Num1415_开心字符串 {
    public static void main(String[] args) {
        Num1415_开心字符串 demo = new Num1415_开心字符串();
        System.out.println(demo.getHappyString(3, 9));
    }

    private String k_str = "";

    public String getHappyString(int n, int k) {
        backTrack(n, new StringBuilder(), k, new int[1]);
        return k_str;
    }

    public void backTrack(int n, StringBuilder sb, int k, int[] k_arr) {
        if (sb.length() == n) {
//            System.out.println(list);
            k_arr[0]++;
            if (k_arr[0] == k) {
                k_str = sb.toString();
            }
            return;
        }
        if (k_arr[0] >= k) {
            return;
        }
        for (char ch = 'a'; ch <= 'c'; ch++) {
            if (sb.length() == 0 || sb.charAt(sb.length() - 1) != ch) {
                sb.append(ch);
                backTrack(n, sb, k, k_arr);
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }
}
