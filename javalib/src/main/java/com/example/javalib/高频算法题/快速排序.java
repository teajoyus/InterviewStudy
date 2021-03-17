package com.example.javalib.高频算法题;

import com.example.javalib.ArrayParser;

/**
 * author : linmh
 * date : 2021/3/17 11:22
 * description :
 * 思考：
 * 1：为什么要先从右边靠左倒逼？
 * 因为我们是比大小的，等下需要去交换base值与这个结束为止
 * 先从右边的话就能找到一个确保小于基准值的，等下调换基准值才满足区间
 * 如果直接从左边开始的话，就不对了。
 */
public class 快速排序 {
    public static void main(String[] args) {
        快速排序 demo = new 快速排序();
        ArrayParser.print(demo.MySort(new int[]{5,2,3,1,4}));
    }

    public int[] MySort(int[] arr) {
        // write code here
        quickSort(arr, 0, arr.length - 1);
        return arr;
    }

    public void quickSort(int[] arr, int left, int right) {
        int base = arr[left];
        int i = left;
        int j = right;
        while (i != j) {
            while (arr[j] >= base && j > i) {
                j--;
            }
            while (arr[i] <= base && i < j) {
                i++;
            }
            if (i != j) {
                int t = arr[i];
                arr[i] = arr[j];
                arr[j] = t;
            }
        }
        arr[left] = arr[i];
        arr[i] = base;
        if (left < i - 1) {
            quickSort(arr, left, i - 1);
        }
        if (i + 1 < right) {
            quickSort(arr, i + 1, right);
        }

    }
}
