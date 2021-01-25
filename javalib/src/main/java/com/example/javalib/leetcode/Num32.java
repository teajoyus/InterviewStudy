package com.example.javalib.leetcode;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

/**
 * 从上到下按层打印二叉树，同一层的节点按从左到右的顺序打印，每一层打印到一行。
 */
public class Num32 {
    public static void main(String[] args) {
        Num32 num = new Num32();
        TreeNode treeNode1 = num.initTree(new int[]{3, 5, 1, 6, 2, 0, 8, 0, 0, 7, 4});
//        System.out.println(num.kthLargest(treeNode1, 3));
//        System.out.println(num.contain(treeNode1, new TreeNode(5)));
//        TreeNode node = num.lowestCommonAncestor(treeNode1, new TreeNode(5), new TreeNode(1));
//        TreeNode node2 = num.lowestCommonAncestor(treeNode1, new TreeNode(5), new TreeNode(4));
        System.out.println(num.levelOrder(treeNode1));

//        System.out.println(node2.val);
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

    private void toList(TreeNode root, int level, List<List<Integer>> lists) {
        if (root == null) return;
//        System.out.println(root.val + "level:" + level);
        if (lists.size() <= level) {
            lists.add(new ArrayList<>());
        }
        lists.get(level).add(root.val);
        toList(root.left, level + 1, lists);
        toList(root.right, level + 1, lists);
    }

    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> lists = new ArrayList<>();
        toList(root,0,lists);
        return lists;
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

