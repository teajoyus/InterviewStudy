package com.example.javalib.leetcode;


import javax.sound.midi.Soundbank;

/**
 * 1、求出综合sum
 * 2、得到平均数ave
 * 3、sum取余3不等于0那么就是false了，因为不能均等
 * 4、贪心的得到第一个最小的序列和是sum/3的序列
 * 5、只要求出第二个序列的和是sum/3那就说明满足条件，因为最后一个序列和就肯定也是sum？3了
 * <p>
 * 注：为什么要贪心的选择一个最小的i作为第一个最小序列
 * <p>
 * <p>
 * 双指针前后向中间逼近，不用考虑中间那段怎么分，只要左右两段累加和等于3等分的数值，中间剩的那段也就找到了
 */
public class Num1013 {
    public static void main(String[] args) {
        System.out.println(new Num1013().canThreePartsEqualSum(new int[]{3, 3, 6, 5, -2, 2, 5, 1, -9, 4}));
//        System.out.println(new Num1013().canThreePartsEqualSum(new int[]{1,-1,1,-1}));
//        System.out.println(new Num1013().canThreePartsEqualSum(new int[]{10, -10, 10, -10, 10, -10, 10, -10}));
//        System.out.println(new Num747().dominantIndex(new int[]{ 3,6,1,0}));
//        System.out.println(new Num747().dominantIndex(new int[]{ 1, 0}));
    }

    public boolean canThreePartsEqualSum(int[] A) {
        int sum = 0;
        for (int i = 0; i < A.length; i++) {
            sum += A[i];
        }
        if (sum % 3 != 0) {
            return false;
        }
        int ave = sum / 3;
        int firtCalc = 0;
        int lastCalc = 0;
        boolean findOne = false;
        boolean findLast = false;
        for (int i = 0, j = A.length - 1; i < j - 1; ) {
            if (!findOne) {
                firtCalc += A[i];
                if (firtCalc == ave) {
                    findOne = true;
                } else {
                    i++;
                }
            }
            if (!findLast) {
                lastCalc += A[j];
                if (lastCalc == ave) {
                    findLast = true;
                }else {
                    j--;
                }
            }
            if (findOne && findLast) {
                break;
            }
        }
        return findOne && findLast;
    }
}
