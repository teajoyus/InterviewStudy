package com.example.javalib.高频算法题;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 给定一个数组，找出其中最小的K个数。例如数组元素是4,5,1,6,2,7,3,8这8个数字，则最小的4个数字是1,2,3,4。如果K>数组的长度，那么返回一个空的数组
 * https://www.nowcoder.com/practice/6a296eb82cf844ca8539b57c23e6e9bf
 * <p>
 * <p>
 * 第一个解法也是自己想到的就是暴力破解：
 * 要求k个最小的，那我就假定前面k个就是最小的。
 * 遍历k~n个，每次发现数组中存在比列表中小的元素，那么久放进列表来
 * 时间复杂度O(k*n)
 * <p>
 * 第二种就是直接排序。
 * <p>
 * 第三种 快排的思想.
 * 每次都用基数来把左右的数对调，当base放到中间索引是k-1是，前面的值就是k个最小值
 *
 *
 * <p>
 * 第三种就是借助缓冲区、堆排序。
 * 利用优先队列来创建一个大根堆，大根堆能帮你确保根节点都是最大值
 */
class 最小的K个数 {
    public static void main(String[] args) {
        最小的K个数 demo = new 最小的K个数();
        System.out.println(demo.GetLeastNumbers_Solution(new int[]{4, 5, 1, 6, 2, 7, 3, 8}, 10));
        System.out.println(demo.GetLeastNumbers_Solution2(new int[]{4, 5, 1, 6, 2, 7, 3, 8}, 0));
        System.out.println(demo.GetLeastNumbers_Solution3(new int[]{4, 5, 1, 6, 2, 7, 3, 8}, 4));
    }

    /**
     * 利用堆排序思想
     * 维护只有k个数组的最大堆
     * 大根堆的一个性质就是它的根始终是最大值
     * 时间复杂度：O(nlogk)
     * @param input
     * @param k
     * @return
     */
    public ArrayList<Integer> GetLeastNumbers_Solution3(int[] input, int k) {
        if (k == 0 || input.length < k) {
            return new ArrayList<>();
        }
        PriorityQueue<Integer> queue = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
        for (int i = 0; i < input.length; i++) {
            if (i < k) {
                queue.offer(input[i]);
            }else{
                if(queue.peek()>input[i]){
                    queue.poll();
                    queue.offer(input[i]);
                }
            }
        }
        ArrayList<Integer> list = new ArrayList<>();
        while(!queue.isEmpty()) {
            list.add(queue.poll());
        }
        return list;
    }

    /**
     * 利用快排思想
     *
     * @param input
     * @param k
     * @return
     */
    public ArrayList<Integer> GetLeastNumbers_Solution(int[] input, int k) {
        if (k == 0 || input.length < k) {
            return new ArrayList<>();
        }
        return quickSort(input, 0, input.length - 1, k);
    }

    private ArrayList<Integer> quickSort(int[] input, int left, int right, int k) {
        int base = input[left];
        int i = left;
        int j = right;
        while (i != j) {
            while (input[j] >= base && j > i) {
                j--;
            }
            while (input[i] <= base && i < j) {
                i++;
            }
            if (i != j) {
                int t = input[i];
                input[i] = input[j];
                input[j] = t;
            }
        }
        input[left] = input[i];
        input[i] = base;
        ArrayList<Integer> list = null;
        if (i == k - 1) {
            list = new ArrayList<>(k);
            for (int l = 0; l < k; l++) {
                list.add(input[l]);
            }
            return list;
        }
        if (left == right) {
            return null;
        }

        if (i - 1 >= left && (list = quickSort(input, left, i - 1, k)) != null) {
            return list;
        }
        if (right >= i + 1 && (list = quickSort(input, i + 1, right, k)) != null) {
            return list;
        }
        return null;

    }

    /**
     * 暴力法
     *
     * @param input
     * @param k
     * @return
     */
    public ArrayList<Integer> GetLeastNumbers_Solution2(int[] input, int k) {
        ArrayList<Integer> list = new ArrayList<>(k);
        for (int i = 0; i < k && i < input.length; i++) {
            list.add(input[i]);
        }
        if (k == 0 || input.length < k) {
            return new ArrayList<>();
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
