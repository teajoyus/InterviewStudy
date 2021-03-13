package com.example.javalib.leetcode.回溯算法;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * author : linmh
 * date : 2021/3/10 11:37
 * description :
 * <p>
 * 给定一个字符串S，通过将字符串S中的每个字母转变大小写，我们可以获得一个新的字符串。返回所有可能得到的字符串集合。
 * <p>
 * 题解：
 * 自己利用了回溯思想，一次性过了。
 * 这道题目主要是之前学的回缩思想的一种变形，
 * 第一个是选择列表，因为每一轮的选择列表不再是某个元素，而是某个元素可能可以做出两种选择（大小写）
 * 所以就需要判断如果能转换的话，那就多做一种转换元素后的选择，
 * 然后自身也要做一次选择。
 * <p>
 * 亮点：因为这次选择是不能向之前那种判断是否包含元素，因为是重复的
 * 所以自己想到可以加个索引，因为每次做出选择后，下个选择肯定是从index+1开始了（也就是选择列表逐渐会缩减）
 */
public class Num784_字母大小写全排列 {
    public static void main(String[] args) {
        Num784_字母大小写全排列 demo = new Num784_字母大小写全排列();
        System.out.println(demo.letterCasePermutation("a1b2"));
    }

    public List<String> letterCasePermutation(String S) {
        List<String> list = new ArrayList<>();
        backTrack(0, S, new LinkedList<>(), list);
        return list;
    }

    public void backTrack(int index, String s, LinkedList<Character> list, List<String> result) {
        if (list.size() == s.length()) {
            StringBuilder sb = new StringBuilder(list.size());
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i));
            }
            result.add(sb.toString());
            return;
        }
        for (int i = index; i < s.length(); i++) {
            char ch = s.charAt(i);
            char newCh = transfer(ch);
//            System.out.println("ch；"+ch+",newCh:"+newCh);
            //选择的条件
            if (ch != newCh) {
                //做出了选择
                list.add(ch);
                //下一轮选择
                backTrack(i + 1, s, list, result);
                //撤销
                list.removeLast();
            }
            //做出了选择
            list.add(newCh);
            //下一轮选择
            backTrack(i + 1, s, list, result);
            //撤销
            list.removeLast();
//            System.out.println("list:"+list);
        }
    }

    public char transfer(char ch) {
        if (ch >= 'A' && ch <= 'Z') {
            return Character.toLowerCase(ch);
        } else if (ch >= 'a' && ch <= 'z') {
            return Character.toUpperCase(ch);
        }
        return ch;
    }
}
