package com.example.javalib.高频算法题;

import com.example.javalib.ArrayParser;

/**
 * author : linmh
 * date : 2021/3/18 19:21
 * description :
 */
public class 二分查找 {
    public static void main(String[] args) {
        二分查找 demo = new 二分查找();
        System.out.println(demo.binarySearch(ArrayParser.parseArray("[0,1,2,3,4,5,6,7,8,9]"),7));
    }

    int binarySearch(int[] nums, int target) {
        int start = 0;
        int end = nums.length - 1;
        while (start != end) {
            int middle = (start + end) / 2;
            if (nums[middle] > target) {
                end = middle - 1;
            } else if (nums[middle] < target) {
                start = middle + 1;
            } else {
                return middle;
            }
        }
        return -1;
    }
}
