package com.example.javalib.leetcode.回溯算法;

import java.util.LinkedList;

/**
 * author : linmh
 * date : 2021/3/10 11:07
 * description :
 * 假设有从 1 到 N 的 N 个整数，如果从这 N 个数字中成功构造出一个数组，使得数组的第 i 位 (1 <= i <= N) 满足如下两个条件中的一个，我们就称这个数组为一个优美的排列。条件：
 * <p>
 * 第 i 位的数字能被 i 整除
 * i 能被第 i 位上的数字整除
 * 现在给定一个整数 N，请问可以构造多少个优美的排列？
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/beautiful-arrangement
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * <p>
 * 题解：
 * 自己利用回溯算法的思想：
 * 1、明确选择列表（for循环）
 * 2、做选择（也就是选择arr[i]，能做的选择、一般需要考虑做筛选，不满足的要去除不能选）
 * 3、进入下一轮递归 选择
 * 4、撤销选择（好让第2步重新来做另外一个选择）
 * 5、在函数开头应该判断做出的选择后是否满足了条件，满足的话就可用。
 * <p>
 * <p>
 * 自己根据回溯算法把基本框架做了出来。
 * 提交时说时间超出，想了一下如果某个选择不满足条件，那么后续的选择都没用了，
 * 所以应该要判断不满足条件的就不选择：
 * list.isEmpty()||(i % (list.size()+1) == 0 || (list.size()+1) % i == 0)
 */
public class Num526_优美的排列 {
    public static void main(String[] args) {
        Num526_优美的排列 demo = new Num526_优美的排列();
        System.out.println(demo.countArrangement(11));
    }

    public int countArrangement(int n) {
        int[] result = new int[1];
        backtrack(n, new LinkedList<>(), result);
        return result[0];
    }

    public void backtrack(int n, LinkedList<Integer> list, int[] nums) {
        if (list.size() == n) {
//            for (int i = 1; i <= n; i++) {
//                if (i % list.get(i - 1) != 0 && list.get(i - 1) % i != 0) {
//                    return;
//                }
//            }
            nums[0]++;
            return;
        }
        //选择列表
        for (int i = 1; i <= n; i++) {
            //排除不可选的组合
            if (list.contains(i)) {
                continue;
            }
            //注意如果这个元素不满足条件，那么就没必要选择了。不加这个会超出时间限制
            if (list.isEmpty() || (i % (list.size() + 1) == 0 || (list.size() + 1) % i == 0)) {

                //做选择
                list.add(i);
                //下一轮递归选择
                backtrack(n, list, nums);
                //撤销选择
                list.removeLast();
            }
        }
    }
}
