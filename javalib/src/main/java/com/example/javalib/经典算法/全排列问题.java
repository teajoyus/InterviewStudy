package com.example.javalib.经典算法;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * author : linmh
 * date : 2021/3/9 19:07
 * description :
 */
public class 全排列问题 {
    public static void main(String[] args) {
        全排列问题 qu = new 全排列问题();
        qu.permute(new int[]{1, 2, 3, 4});
//        for (int i = 0; i < qu.res.size(); i++) {
//            System.out.println(qu.res.get(i));
//        }
//        System.out.println(qu.res.size());
    }

    private List<List<Integer>> res = new ArrayList<>();

    private List<List<Integer>> permute(int[] nums) {

        backtrack(nums, new LinkedList<Integer>());
        return res;
    }

    private void backtrack(int[] nums, LinkedList<Integer> track) {
        //结束触发条件
        if (nums.length == track.size()) {
            res.add(new ArrayList<>(track));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            //排除不合法的选择（也就是说，每一轮又面对全部元素的选择，但是元素是不能重复选的，那么就得去除掉）
            if (track.contains(nums[i])) {
                continue;
            }
            //做选择
            track.add(nums[i]);
            //进入下一层决策树
//            System.out.println("track before:"+track);
            backtrack(nums, track);
//            System.out.println("track after:"+track);
//            取消选择（怎么理解呢，就是做选择完毕之后，需要撤销掉，然后做另外一个种选择。比如全排列[1 2 3 4]，那么撤销掉4，然后4的这轮for也完了，推到前一个选择是3，又是撤销掉3，变成4 所以就变成[1,2,4]）
            track.removeLast();
        }
    }
}
