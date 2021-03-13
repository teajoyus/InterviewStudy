package com.example.javalib.leetcode.回溯算法;

import java.util.LinkedList;

/**
 * author : linmh
 * date : 2021/3/10 17:02
 * description :
 * 给定一个非负整数 n，计算各位数字都不同的数字 x 的个数，其中 0 ≤ x < 10n 。
 * <p>
 * 自己理解：这种题目不是要让你去遍历10的n次方，而是排列组合。因为要求每一位数字都不同
 * <p>
 * 结果：虽然做出来了AC了，但是明显还是DP更好
 */
public class Num357_计算各个位数不同的数字个数 {
    public static void main(String[] args) {
        Num357_计算各个位数不同的数字个数 demo = new Num357_计算各个位数不同的数字个数();
        System.out.println(demo.countNumbersWithUniqueDigits(2));
    }

    public int countNumbersWithUniqueDigits(int n) {
        int[] result = new int[1];
        backTrack(n, new LinkedList<>(), result);
        return result[0];
    }

    private void backTrack(int n, LinkedList<Integer> list, int[] arr) {
        arr[0]++;
        System.out.println(list);
        if (n == list.size()) {
            return;
        }
        for (int i = 0; i <= 9; i++) {
            if (list.isEmpty() && i == 0) {
                continue;
            }
            if (!list.contains(i)) {
                list.add(i);
                backTrack(n, list, arr);
                list.removeLast();
            }
        }
    }
}
