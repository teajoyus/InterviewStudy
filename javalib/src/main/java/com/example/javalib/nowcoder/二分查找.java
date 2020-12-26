package com.example.javalib.nowcoder;

import javax.sound.midi.Soundbank;

/**
 * 输出在数组中第一个大于等于查找值的位置，如果数组中不存在这样的数，则输出数组长度加一。
 */
public class 二分查找 {
    public static void main(String[] args) {
        int[] data = new int[]{1, 2, 4, 5, 7, 8, 12, 15, 26, 67};
//        System.out.println(new 二分查找().search(data, 0, data.length - 1, 113));
        int[] data2 = new int[]{1, 2, 4, 4, 5};
        System.out.println(new 二分查找().upper_bound_(5, 3, data2));
        int[] data3 = new int[]{3, 3, 4, 4, 4, 5, 6, 6, 6, 7, 8, 8, 12, 13, 15, 16, 21, 21, 22, 24, 24, 27, 28, 32, 34, 35, 35, 36, 36, 39, 40, 41, 41, 42, 44, 44, 45, 45, 47, 47, 47, 47, 48, 48, 50, 51, 51, 53, 53, 53, 54, 54, 54, 56, 56, 57, 59, 60, 60, 60, 60, 61, 62, 63, 65, 65, 65, 65, 67, 67, 68, 70, 71, 71, 74, 75, 75, 79, 81, 84, 84, 86, 86, 87, 90, 90, 90, 90, 91, 92, 93, 94, 94, 94, 95, 97, 97, 98, 98, 99};
        System.out.println(new 二分查找().upper_bound_(100, 97, data3));
    }

    /**
     * 二分查找
     *
     * @param n int整型 数组长度
     * @param v int整型 查找值
     * @param a int整型一维数组 有序数组
     * @return int整型
     */
    public int upper_bound_(int n, int v, int[] a) {
        // write code here
        return search2(a, 0, n - 1, v);
    }

    /**
     * 二分查找法
     *
     * @param a
     * @param left
     * @param right
     * @param value 输出索引
     * @return
     */
    public int search2(int[] a, int left, int right, int value) {
        System.out.println("left：" + left + ",right:" + right);
        int mid = (left + right) / 2;
        if (left == right || mid == left) {
            if (a[left] >= value) {
                return left + 1;
            } else {
                if (left + 1 < a.length) {
                    return left + 2;
                } else {
                    return a.length + 1;
                }
            }
        }
        if (a[mid] < value) {
            return search2(a, mid + 1, right, value);
        } else if (a[mid] > value) {
            return search2(a, left, mid - 1, value);
        } else {
            if(mid-1>0&&a[mid-1]==a[mid]){
                return search2(a, left, mid - 1, value);
            }else {
                return mid+1;
            }
        }
    }

    public int search(int[] a, int left, int right, int value) {
//        System.out.println("left：" + left + ",right:" + right);
        if (left == right) {
            if (a[left] == value) {
                return left;
            } else {
                return -1;
            }
        }
        int mid = (left + right) / 2;
        if (a[mid] < value) {
            return search(a, mid + 1, right, value);
        } else if (a[mid] > value) {
            return search(a, left, mid - 1, value);
        } else {
            return mid;
        }

    }
}

