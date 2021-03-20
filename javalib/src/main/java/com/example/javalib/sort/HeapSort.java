package com.example.javalib.sort;

import com.example.javalib.ArrayParser;

/**
 * author : linmh
 * date : 2021/3/20 19:13
 * description :
 * 堆排序
 *
 */
public class HeapSort {
    public static void main(String[] args) {
        HeapSort demo = new HeapSort();
        int[] arr =  HeapSort.sort(ArrayParser.parseArray("[2,1,5,7,3,9,8,6]"));
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]+"\t");
        }
        System.out.println("");
    }

    public static int[] sort(int[] arr) {
        //1.构建大顶堆
        //从第一个非叶子结点  i = arr.length / 2 - 1
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            //从第一个非叶子结点从下至上，从右至左调整结构
            adjustHeap(arr, i, arr.length);
        }
//        上面走完了一轮就可以确定第一个是最大值。
//        然后将最大值与末尾的对换，继续再缩小范围继续去做调整
        //2.调整堆结构+交换堆顶元素与末尾元素
        for (int j = arr.length - 1; j > 0; j--) {
            swap(arr, 0, j);//将堆顶元素与末尾元素进行交换
            adjustHeap(arr, 0, j);//重新对堆进行调整
        }
        return arr;
    }

    /**
     * 调整大顶堆（仅是调整过程，建立在大顶堆已构建的基础上）
     *
     *  第i个结点的左孩子是2 * i + 1
     *  第i个结点的左孩子是2 * i + 2
     *  那么首先构造k = i * 2 + 1 来 拿到左节点
     *  然后再判断右节点也就是k+1是否比较大，大的话就k++指向右节点
     *  然后父节点和子节点进行交换。
     *  然后进入下一轮。
     *  注意下一轮进入的是第i个结点的左孩子，所以k = k * 2 + 1的步伐
     *
     * @param arr
     * @param i
     * @param length
     */
    public static void adjustHeap(int[] arr, int i, int length) {
        int temp = arr[i];//先取出当前元素i
        for (int k = i * 2 + 1; k < length; k = k * 2 + 1) {//从i结点的左子结点开始，也就是2i+1处开始
            if (k + 1 < length && arr[k] < arr[k + 1]) {//如果左子结点小于右子结点，k指向右子结点
                k++;
            }
            if (arr[k] > temp) {//如果子节点大于父节点，将子节点值赋给父节点（不用进行交换）
                arr[i] = arr[k];
                i = k;
            } else {
                break;
            }
        }
        arr[i] = temp;//将temp值放到最终的位置
    }

    /**
     * 交换元素
     *
     * @param arr
     * @param a
     * @param b
     */
    public static void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
}