package com.example.javalib.高频算法题;

import javafx.scene.control.Alert;

/**
 * https://www.nowcoder.com/practice/e016ad9b7f0b45048c58a9f27ba618bf
 * <p>
 * 还是一样利用快排算法
 */
class 寻找第k大的数 {
    public static void main(String[] args) {
        寻找第k大的数 demo = new 寻找第k大的数();
        System.out.println(demo.findKth(new int[]{1, 3, 5, 2, 2}, 5, 3));
    }

    public int findKth(int[] a, int n, int K) {
        // write code here
        int[] result = new int[1];
        quickSort(a, 0, n - 1, K, result);
        return result[0];
    }

    public boolean quickSort(int[] a, int left, int right, int K, int[] result) {
        if (left > right) {
            return false;
        }
        int base = a[left];
        int i = left;
        int j = right;
        while (i != j) {
            while (a[j] >= base && j > i) {
                j--;
            }
            while (a[i] <= base && i < j) {
                i++;
            }
            if (i != j) {
                int t = a[i];
                a[i] = a[j];
                a[j] = t;
            }
        }
        a[left] = a[i];
        a[i] = base;
        if (i == a.length - K) {
            result[0] = a[i];
            return true;
        }
        if (left == right) {
            return false;
        }
        return quickSort(a, left, i - 1, K, result) || quickSort(a, i + 1, right, K, result);
    }
}
