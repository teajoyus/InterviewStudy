package com.example.javalib.高频算法题;

import java.util.ArrayList;

/**
 * 给定一个数组，找出其中最小的K个数。例如数组元素是4,5,1,6,2,7,3,8这8个数字，则最小的4个数字是1,2,3,4。如果K>数组的长度，那么返回一个空的数组
 * https://www.nowcoder.com/practice/6a296eb82cf844ca8539b57c23e6e9bf
 */
class 最小的K个数 {
    public static void main(String[] args) {
        最小的K个数 demo = new 最小的K个数();
//        demo.GetLeastNumbers_Solution()
    }

    public ArrayList<Integer> GetLeastNumbers_Solution(int[] input, int k) {
        ArrayList<Integer> list = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            list.add(input[i]);
        }
        for (int i = k; i < input.length; i++) {
            replaceMax(list, input[i]);
        }
        return list;
    }

    public void replaceMax(ArrayList<Integer> list, int val) {
        int maxIndex = 0;
        int max = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (max < list.get(i)) {
                maxIndex = i;
                max = list.get(i);
            }
        }
        if (list.get(maxIndex) > val) {
            list.set(maxIndex, val);
        }

    }

}
