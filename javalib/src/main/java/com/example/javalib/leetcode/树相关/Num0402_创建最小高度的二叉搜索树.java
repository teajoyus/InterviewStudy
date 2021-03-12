package com.example.javalib.leetcode.树相关;

import com.example.javalib.TreeNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 给定一个有序整数数组，元素各不相同且按升序排列，编写一个算法，创建一棵高度最小的二叉搜索树。
 * <p>
 * 难度不大 主要是答案不止一种  一直以为不对
 */
public class Num0402_创建最小高度的二叉搜索树 {
    public static void main(String[] args) {
        Num0402_创建最小高度的二叉搜索树 num = new Num0402_创建最小高度的二叉搜索树();
        TreeNode node = num.sortedArrayToBST(new int[]{-10, -3, 0, 5, 9});
//        System.out.println(node.val);
        num.levelTraversal(node);
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

}

