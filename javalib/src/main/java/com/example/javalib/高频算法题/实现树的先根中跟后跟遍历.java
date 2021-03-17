package com.example.javalib.高频算法题;

import com.example.javalib.TreeNode;

import java.util.LinkedList;

/**
 * author : linmh
 * date : 2021/3/17 14:38
 * description :
 * https://www.nowcoder.com/practice/a9fec6c46a684ad5a3abd4e365a9d362
 * <p>
 * 没啥好说的
 */
public class 实现树的先根中跟后跟遍历 {
    public static void main(String[] args) {
        实现树的先根中跟后跟遍历 demo = new 实现树的先根中跟后跟遍历();
    }

    public int[][] threeOrders(TreeNode root) {
        // write code here
        int[][] arrs = new int[3][];
        LinkedList<Integer> list = new LinkedList<Integer>();
        preOrder(root, list);
        int[] arr = new int[list.size()];
        int i = 0;
        while (!list.isEmpty()) {
            arr[i++] = list.poll();
        }
        arrs[0] = arr;
        midOrder(root, list);
        i = 0;
        arr = new int[list.size()];
        while (!list.isEmpty()) {
            arr[i++] = list.poll();
        }
        arrs[1] = arr;
        postOrder(root, list);
        i = 0;
        arr = new int[list.size()];
        while (!list.isEmpty()) {
            arr[i++] = list.poll();
        }
        arrs[2] = arr;
        return arrs;

    }

    private void preOrder(TreeNode root, LinkedList<Integer> list) {
        if (root == null) {
            return;
        }
        list.addLast(root.val);
        preOrder(root.left, list);
        preOrder(root.right, list);
    }

    private void midOrder(TreeNode root, LinkedList<Integer> list) {
        if (root == null) {
            return;
        }
        midOrder(root.left, list);
        list.addLast(root.val);
        midOrder(root.right, list);
    }

    private void postOrder(TreeNode root, LinkedList<Integer> list) {
        if (root == null) {
            return;
        }
        postOrder(root.left, list);
        postOrder(root.right, list);
        list.addLast(root.val);
    }
}
