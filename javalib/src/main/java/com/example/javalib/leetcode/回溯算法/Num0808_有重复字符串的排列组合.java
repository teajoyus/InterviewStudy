package com.example.javalib.leetcode.回溯算法;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * author : linmh
 * date : 2021/3/10 16:05
 * description :
 * 有重复字符串的排列组合。编写一种方法，计算某字符串的所有排列组合。
 */
public class Num0808_有重复字符串的排列组合 {
    public static void main(String[] args) {
        Num0808_有重复字符串的排列组合 demo = new Num0808_有重复字符串的排列组合();
        String[] strArr = demo.permutation("ppe");
        for (int i = 0; i < strArr.length; i++) {
            System.out.println(strArr[i]);
        }
    }

    private List<String> result = new ArrayList<>();

    public String[] permutation(String S) {
        dfs(S, new boolean[S.length()], new StringBuilder());
        return result.toArray(new String[0]);
    }

    public void dfs(String s,boolean[] visit, StringBuilder sb) {
        if (s.length() == sb.length()) {
            String str = sb.toString();
            if (!result.contains(str)) {
                result.add(str);
            }
            return;
        }
        for (int i = 0; i < s.length(); i++) {
            if(!visit[i]) {
                sb.append(s.charAt(i));
                visit[i] = true;
                dfs(s, visit, sb);
                sb.deleteCharAt(sb.length() - 1);
                visit[i] = false;
            }
        }

    }
}
