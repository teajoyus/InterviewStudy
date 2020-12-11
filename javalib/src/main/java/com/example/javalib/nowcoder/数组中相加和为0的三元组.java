package com.example.javalib.nowcoder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 给出一个有n个元素的数组S，S中是否有元素a,b,c满足a+b+c=0？找出数组S中所有满足条件的三元组。
 * 注意：
 * 三元组（a、b、c）中的元素必须按非降序排列。（即a≤b≤c）
 * 解集中不能包含重复的三元组。
 * 例如，给定的数组 S = {-10 0 10 20 -10 -40},解集为(-10, 0, 10) (-10, -10, 20)
 *
 *
 * 解题思路：
 * 1、先排序好 因为排序好之后才可以左右夹逼，找出符合条件的临界点坐标
 * 2、固定第一个值，取num[i]，然后第二个值从left=i+1开始，第三个值取right=lenght-1 最大值
 * 3、固定left，从right开始向左遍历，如果找到三者之合大于0 那么right就继续往左（因为right太大了，往左的话right对应的值就会小一点。）
 * （那为什么是判断大于0不是小于0呢，因为我们是固定left的做法，如果是固定right移动left的话那就是判断小于0）
 * 4、当前面right，一直往左发现第一个三者之和小于等于0的right。判断是不是等于0，如果是那么入List.
 * 5、如果等于0入了List，那么要判断left的下一个数是不是等于left，防止重复的问题。
 * 6、如果不是等于0，那么left++，下一趟了（因为是有序的，遍历最大的right三者之和都小于0了，那么right左边的三值之和就一定小于0不需要判断了）
 * （第6步直接是left++而不用判断重复，因为都找不到left对应的集合了 判断不判断其实无所谓，要判断也可以）
 */
public class 数组中相加和为0的三元组 {
    public static void main(String[] args) {
//        ArrayList<ArrayList<Integer>> lists = new 数组中相加和为0的三元组().threeSum(new int[]{0, 0, 0, 0});
        ArrayList<ArrayList<Integer>> lists = new 数组中相加和为0的三元组().threeSum(new int[]{1,2,-2,-1});
//        ArrayList<ArrayList<Integer>> lists = new 数组中相加和为0的三元组().threeSum(new int[]{-10, 0, 10, 20, -10, -40});
        System.out.println(lists);
//        ArrayList<ArrayList<Integer>> lists = new 数组中相加和为0的三元组().threeSum(new int[]{-10, 0, 10 ,20 ,-10, -40});
    }

    //-40 -10 -10 0 10 20
    //-2 -1 1 2
    public ArrayList<ArrayList<Integer>> threeSum(int[] num) {
        Arrays.sort(num);
        ArrayList<ArrayList<Integer>> lists = new ArrayList<>();
        for (int i = 0; i < num.length; i++) {
            //防止重复
            if(i!=0&&num[i]==num[i-1])continue;
            int left = i + 1;
            int right = num.length - 1;
            while (left < right) {
//                System.out.println("left:"+left+",right:"+right+",i:"+i);
                //从左到右  从小到大排序了，固定left之后，在right那边找一个最大值满足>0的，
                while (left < right && num[i] + num[left] + num[right] > 0) {
                    right--;
                }
                if (left<right&&num[i] + num[left] + num[right] == 0) {
                    ArrayList<Integer> list = new ArrayList<>(3);
                    list.add(num[i]);
                    list.add(num[left]);
                    list.add(num[right]);
                    lists.add(list);
                    int temp = num[left];
                    //找到的话则要去除重复
                    while (left < right && num[left] == temp) left++;
                } else {
                    left++;
                }
            }
        }
        return lists;
    }
}
