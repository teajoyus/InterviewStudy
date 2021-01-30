package com.example.javalib.leetcode;


public class Num747 {
    public static void main(String[] args) {
        System.out.println(new Num747().dominantIndex(new int[]{ 1,1,0}));
//        System.out.println(new Num747().dominantIndex(new int[]{ 3,6,1,0}));
//        System.out.println(new Num747().dominantIndex(new int[]{ 1, 0}));
    }

    public int dominantIndex(int[] nums) {
        if(nums.length<2){
            return 0;
        }
        int maxIndex = 0;
        int secondMaxIndex  = 1;
        for (int i = 1; i < nums.length; i++) {
            if(nums[i]>nums[maxIndex]){
                secondMaxIndex = maxIndex;
                maxIndex = i;
            }else if(nums[i]>nums[secondMaxIndex]){
                secondMaxIndex = i;
            }
        }
        if(maxIndex!=secondMaxIndex&&nums[secondMaxIndex]*2<=nums[maxIndex]){
            return maxIndex;
        } else{
            return -1;
        }
    }
}
