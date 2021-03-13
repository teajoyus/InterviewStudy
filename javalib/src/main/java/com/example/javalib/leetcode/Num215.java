package com.example.javalib.leetcode;

/**
 * author : linmh
 * date : 2021/3/9 15:31
 * description :
 * 在未排序的数组中找到第 k 个最大的元素。请注意，你需要找的是数组排序后的第 k 个最大的元素，而不是第 k 个不同的元素。
 * <p>
 * https://leetcode-cn.com/problems/kth-largest-element-in-an-array/
 *
 * 解法：利用快速排序的思想，每次总能把基数放到一个对的位置，这个位置在哪里刚好就可以知道是第k大
 */
public class Num215 {
    public static void main(String[] args) {
        System.out.println(new Num215().findKthLargest(new int[]{7,6,5,4,3,2,1}, 2));
//        System.out.println(new Num215().findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2));
//        System.out.println(new Num215().findKthLargest(new int[]{2, 1}, 2));
//        new Num215().sort(new int[]{3, 2, 1, 5, 6, 4});
    }

    public int findKthLargest(int[] nums, int k) {
        return quickSort(k, nums, 0, nums.length - 1);
    }

    public int quickSort(int k, int[] nums, int left, int right) {
        int i = left, j = right;
        int base = nums[left];
        while (i != j) {
            while (nums[j] >= base && i < j) {
                j--;
            }
            while (nums[i] <= base && i < j) {
                i++;
            }
            if (i != j) {
                int t = nums[i];
                nums[i] = nums[j];
                nums[j] = t;
            }
        }
        if (k == (nums.length - i)) {
            return nums[left];
        }
        if(left==right){
            return Integer.MIN_VALUE;
        }
        nums[left] = nums[i];
        nums[i] = base;
        if(left<i) {
            int ansL = quickSort(k, nums, left, i );
            if (ansL != Integer.MIN_VALUE) {
                return ansL;
            }
        }
        if(right>i) {
            int ansR = quickSort(k, nums, i + 1, right);
            if (ansR != Integer.MIN_VALUE) {
                return ansR;
            }
        }
        return Integer.MIN_VALUE;

    }

    /**
     * 重写快速排序
     * 第一个卡点：第一趟确定基点后，也知道for循环是 i != j;，不知道怎么左右交换，二分为两个子数组
     * 第二个卡点是：记错了，快排的基数不是说去找mid，而是随便确定一个数作为基数，比如第一个，最后再把这个基数放到中间来切分两个
     * <p>
     * 最后一个卡点：在进入递归时的区间可以是[left,i-1]、[i+1,right]，没必要去包含i，因为i已经是基数确定位置了
     *
     * @param nums
     */
    public void sort(int[] nums) {
        quickSort(nums, 0, nums.length - 1);
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + "\t");
        }
    }

    public void quickSort(int[] nums, int left, int right) {
        if (left == right) {
            return;
        }
        int base = nums[left];
        int i = left, j = right;
        while (i != j) {
            //寻找右边第一个小于基数的元素
            while (nums[j] < base && i < j) {
                j--;
            }
            //寻找左边第一个大于等于基数的元素
            while (nums[i] >= base && i < j) {
                i++;
            }
            if (i < j) {
                int t = nums[i];
                nums[i] = nums[j];
                nums[j] = t;
            }
        }
        nums[left] = nums[i];
        nums[i] = base;
        if (left < i) {
            quickSort(nums, left, i);
        }
        if (i + 1 < right) {
            quickSort(nums, i + 1, right);
        }

    }
}
