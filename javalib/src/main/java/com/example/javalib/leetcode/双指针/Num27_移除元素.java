package com.example.javalib.leetcode.双指针;

/**
 * author : linmh
 * date : 2021/3/11 11:21
 * description :
 * https://leetcode-cn.com/problems/remove-element/
 */
public class Num27_移除元素 {
    public static void main(String[] args) {
        Num27_移除元素 demo = new Num27_移除元素();
        int[] nums = new int[]{0, 1, 2, 2, 3, 0, 4, 2};
        int len = demo.removeElement(nums, 2);
        for (int i = 0; i < len; i++) {
            System.out.print(nums[i] + "\t");
        }
    }

    public int removeElement(int[] nums, int val) {
        int size = nums.length;
        for (int i = 0; i < size; i++) {
            if (nums[i] == val) {
                nums[i] = nums[size - 1];
                size--;
                i--;
            }
        }
        return size;
    }
}
