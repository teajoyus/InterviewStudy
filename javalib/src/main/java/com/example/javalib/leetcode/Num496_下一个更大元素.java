package com.example.javalib.leetcode;

import java.util.ArrayList;
import java.util.Stack;

/**
 * author : linmh
 * date : 2021/3/11 16:55
 * description :
 * <p>
 * https://leetcode-cn.com/problems/next-greater-element-i/
 * <p>
 * 单调栈，从后面开始
 */
public class Num496_下一个更大元素 {

    private Stack<Integer> stack;

    public static void main(String[] args) {
        Num496_下一个更大元素 demo = new Num496_下一个更大元素();
        int[] num = demo.nextGreaterElement(new int[]{4, 1, 2}, new int[]{1, 3, 4, 2});
        for (int i = 0; i < num.length; i++) {
            System.out.print(num[i] + "\t");
        }
    }

    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        int[] nums = new int[nums1.length];
        Stack<Integer> stack = new Stack<>();
        for (int i = nums2.length - 1; i >= 0; i--) {
            //如果遇到数字大的，那么就替换掉栈顶那些数字小的，因为栈顶当前的数字总是最大的
            while (!stack.isEmpty() && stack.peek() < nums2[i]) {
                stack.pop();
            }
            if (i < nums1.length) {
                nums[i] = stack.isEmpty() ? -1 : stack.pop();
            }
            stack.push(nums2[i]);
        }
        return nums;
    }
}
