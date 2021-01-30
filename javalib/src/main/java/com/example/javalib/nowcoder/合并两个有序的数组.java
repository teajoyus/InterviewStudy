package com.example.javalib.nowcoder;

/**
 * 给出两个有序的整数数组 和 ，请将数组 合并到数组 中，变成一个有序的数组
 * 注意：
 * 可以假设 数组有足够的空间存放 数组的元素， 和 中初始的元素数目分别为 和
 * 
 * 
 * 两种思路：
 * 1、一种是归并排序的思想，空间换时间。采用一个新数组来合并两个有序数组
 * 2、另外一种是从右边开始，从AB数组中拿出最大值插入到A数组的尾端（前提是A数组足够大）
 */
public class 合并两个有序的数组 {
    public static void main(String[] args) {
        int[] A = new int[]{1};
        new 合并两个有序的数组().merge(A,1,new int[0],0);

    }
    public void merge(int A[], int m, int B[], int n) {
        int[] newArr = new int[m+n];
        int i = 0, j = 0, k = 0;
        //接下来是要合并两个有序数组到新数组上。 通过判断大小来依次把左右数组放到新数组上
        while (i < m && j < n) {
            if (A[i] < B[j]) {
                newArr[k++] = A[i++];
            } else {
                newArr[k++] = B[j++];
            }
        }
        //别忘了可能左数组并没有遍历完（这时候肯定是右数组遍历完退出来了），所以左数组还要继续追加
        while (i <m) {
            newArr[k++] = A[i++];
        }
        //右数组同理追加
        while (j < n) {
            newArr[k++] = B[j++];
        }
        k = 0;
        while (k<newArr.length){
            A[k] = newArr[k];
            k++;
        }
    }
}
