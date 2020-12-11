package com.example.javalib.sort;

public class MergeSort {
    public static void main(String[] args) {
        final int LENGTH = 100;
//        int arr[] = new int[LENGTH];
//        Random random = new Random();
//        for (int i = 0; i < LENGTH; i++) {
//            arr[i] = random.nextInt(LENGTH);
//        }
//        int arr[] = {1, 2, 3, 4, 5,6,7,8};
        int arr[] = {8, 7, 6, 5, 4, 3, 2, 1};
        String str1 = tongji(arr);

        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + "\t");
        }
        System.out.println("");
        int[] newArr = mergeSort(arr, 0, arr.length - 1);
        for (int i = 0; i < newArr.length; i++) {
            System.out.print(newArr[i] + "\t");
        }

        String str2 = tongji(newArr);
        System.out.println("\n" + str1.equals(str2));
    }

    public static int[] mergeSort(int[] arr, int left, int right) {
        if (left == right) {
            return new int[]{arr[left]};
        }
        int mid = (left + right) / 2;
        int[] leftArr = mergeSort(arr, left, mid);//左有序数组
        int[] rightArr = mergeSort(arr, mid + 1, right);//右有序数组
        int[] newArr = new int[leftArr.length + rightArr.length];//归并合并后的数组
        int i = 0, j = 0, k = 0;
        //接下来是要合并两个有序数组到新数组上。 通过判断大小来依次把左右数组放到新数组上
        while (i < leftArr.length && j < rightArr.length) {
            if (leftArr[i] < rightArr[j]) {
                newArr[k++] = leftArr[i++];
            } else {
                newArr[k++] = rightArr[j++];
            }
        }
        //别忘了可能左数组并没有遍历完（这时候肯定是右数组遍历完退出来了），所以左数组还要继续追加
        while (i < leftArr.length) {
            newArr[k++] = leftArr[i++];
        }
        //右数组同理追加
        while (j < rightArr.length) {
            newArr[k++] = rightArr[j++];
        }
        return newArr;

    }

    public static String tongji(int arr[]) {
        StringBuilder sb = new StringBuilder();
        int[] nums = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            nums[arr[i] - 1]++;
        }
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0) {
                sb.append(i + ":" + nums[i]);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
