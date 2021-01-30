package com.example.javalib.sort;

import java.util.Random;

/**
 * @Author linmh
 * @email 625321521@qq.com
 * @date 2020-07-08 17:54
 * @desc
 */
public class BubbleSort {
    public static void main(String[] args) {
        final int LENGTH = 20000;
        int arr[] = new int[LENGTH];
        Random random = new Random();
        for (int i = 0; i < LENGTH; i++) {
            arr[i] = random.nextInt(LENGTH);
//            System.out.print(arr[i] + "\t");
        }
        System.out.println("");
        sortV3(arr);
//        sortV2(arr);
//        sort(arr);

        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + "\t");
        }
    }

    /**
     * 不要小看冒泡，有时候可能回答补上来，或者和插入排序混淆
     * 冒泡就是不断的遍历比对相邻的两个数，交换他们的值，最后把最大（小）的值放到最后面来
     * 比如 3 2 5 1.来正序排序
     * 第一轮：首先比较3和2，交换他们的值变成2 3 5 1，接着比较3和5 ，不需要做交换。接着比较5 和 1 交换他们的值。最终变成2 3 1 5.第一轮结束，我们也成功的把最大值5冒泡到最后面
     * 所以每一轮都能确定一个最大（小）值放到后面去
     * <p>
     * for循环两次的含义：
     * 第一层：轮次。
     * 第二层：冒泡的次数。
     * <p>
     * 第二层for注意j<arr.length-1-i,而不是j<arr.length-1更不是j<arr.length. -i是因为i后面数是已经排序好的了，没必要再去冒泡。而-1是因为每一轮都会跟后一个数据对比。
     * <p>
     * 由于需要两层for循环，每一次只能确定一个排序位置，而确定这个排序位置又需要作出遍历。所以时间复杂度就是O(n2)
     * 这个过程没什么其它空间上的开销，空间复杂度就是O(1)
     *
     * @param arr
     */
    public static void sort(int[] arr) {
        int tmp = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
    }

    /**
     * 冒泡优化第一版
     * <p>
     * 改进版的冒泡。
     * 前面说的冒泡是每一轮都会做出遍历。
     * 但是如果在一轮冒泡中，都不需要作出数据交换，说明数组已经是有序的了，这个时候就没有必要再进行后面的轮次了，可以提前退出来
     * 所以我们在第一层for循环加个标记。如果第二层for循环里面有做出交换的话则做标记，最后在第二层遍历完成后如果标记无效那么我们就可以提前退出
     *
     * @param arr
     */
    public static void sortV2(int[] arr) {
        int tmp = 0;
        for (int i = 0; i < arr.length; i++) {
            boolean isSorted = false;
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    isSorted = true;
                }
            }
            if (!isSorted) {
                break;
            }
        }
    }

    /**
     * 冒泡优化第二版
     * 第一版中说能提前退出来结束循环的优化。
     * <p>
     * 而第二个优化就是基于最后比较的位置。在前面两个版本中，每一轮中的冒泡都需要冒泡到已排序数的前面一个位置。
     * 但在实际中，每轮冒泡不一定都需要冒泡到已排序数的前面一个位置，假如第二轮中需要冒泡9次，但是假如在遍历第5次后就不再做出比较，说明5次之后 后面的数组已经是有序的了，那么可以不需要再去做遍历了
     * <p>
     * 所以改进的方法就是，用一个变量来记录每一轮最后一个交换的位置，这个位置就可以作为下一轮遍历的终止位置。
     *
     * @param arr
     */
    public static void sortV3(int[] arr) {
        int tmp = 0;
        int lastExchangeIndex = 0;
        int sortBorder = arr.length - 1;
        for (int i = 0; i < arr.length; i++) {
            boolean isSorted = false;
            for (int j = 0; j < sortBorder; j++) {
                if (arr[j] > arr[j + 1]) {
                    tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    isSorted = true;
                    lastExchangeIndex = j;
                }
            }
            sortBorder = lastExchangeIndex;
            if (!isSorted) {
                break;
            }
        }
    }


    /**
     * 这个是插入排序，之前还搞错成冒泡
     * <p>
     * 第一轮：从下标0开始，遍历整个数组，如果有比下标0小（大）的数字则做交换
     * 这样在经过第一轮时，下标0这个位置的数就是数组中的最小（大）值
     * 第二轮就是从下标1开始，同样遍历整个数组做交换，最终确定了下标1就是第二个最小（大）的值了
     * 就这样，每一轮只能确定一个排序位置，而每一轮都需要做出遍历。所以它的算法复杂度就是O(n2)
     * 这个过程没什么其它空间上的开销，空间复杂度就是O(1)
     * <p>
     * 两次for循环，记住第一个for循环i从0开始，但是只需要到size-1就OK了，因为最后一个数已经不需要去做排序了
     * 第二个for循环j是从i+1开始的，因为i前面的数都是已经排序好了，而i+1后面的数就是用来和i这个位置做出交换的
     *
     * @param arr
     */
    public static void insertSort(int arr[]) {
        for (int i = 0; i < arr.length - 1; i++) {
            System.out.println("第" + (i + 1) + "轮：");
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
                for (int k = 0; k < arr.length; k++) {
                    System.out.print(arr[k] + "\t");
                }
                System.out.println("");
            }
        }
    }

}
