package com.example.javalib.leetcode.dp;

import java.lang.reflect.Array;
import java.util.Arrays;

public class 分发饼干455 {
    public static void main(String[] args) {

    }
    public int findContentChildren(int[] g, int[] s) {
        int childrens = 0,cookies=0;
        Arrays.sort(g);
        Arrays.sort(s);
        for (int i = 0,j=0; i < g.length&&j<s.length; j++) {
            //如果第i个饼干能满足第i个孩子，那么孩子数+1，移动下一个孩子
            if(g[i]<=s[j]){
                childrens++;
                i++;
            }
        }
        return childrens;
    }
}
