package com.example.javalib.leetcode;

import com.example.javalib.tree.BinaryTree;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 给定一个有序整数数组，元素各不相同且按升序排列，编写一个算法，创建一棵高度最小的二叉搜索树。
 * <p>
 * 难度不大 主要是答案不止一种  一直以为不对
 */
public class Num0402 {
    public static void main(String[] args) {
        Num0402 num = new Num0402();
        TreeNode node = num.sortedArrayToBST(new int[]{-10, -3, 0, 5, 9});
//        System.out.println(node.val);
        num.levelTraversal(node);
    }

    public TreeNode initTree(int[] array) {
        return buildTree(array, 0);
    }

    /**
     * 构造二叉树，重点在于理解2 * index + 1、2 * index + 2
     * 当你通过把完全二叉树画出来就明白了
     * 每n层都是n-1层的2倍数据，所以当n-1层是从index开始的话，那么到n层就是2*index开头
     *
     * @param nums
     * @param index
     * @return
     */
    public TreeNode buildTree(int[] nums, int index) {
        if (index >= nums.length) {
            return null;
        }
        if (nums[index] == 0) {
            return null;
        }
        TreeNode root = new TreeNode(nums[index]);
        root.left = buildTree(nums, 2 * index + 1);
        root.right = buildTree(nums, 2 * index + 2);
        return root;
    }

    public TreeNode sortedArrayToBST(int[] nums) {
        return buildBalanceTree(nums, 0, nums.length - 1);
    }

    public TreeNode buildBalanceTree(int[] nums, int left, int right) {
        if (left > right) return null;
        if (left == right) {
            return new TreeNode(nums[left]);
        }
        int index = (left + right) / 2;
        TreeNode root = new TreeNode(nums[index]);
        root.left = buildBalanceTree(nums, left, index - 1);
        root.right = buildBalanceTree(nums, index + 1, right);
        return root;
    }

    private void levelTraversal(TreeNode root) {
        if (root == null) {
            return;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            System.out.print(node.val + "\t");
            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }
        System.out.println("");
    }

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
}

