package com.example.javalib.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * 给你一个树，请你 按中序遍历 重新排列树，使树中最左边的结点现在是树的根，并且每个结点没有左子结点，只有一个右子结点。
 * <p>
 * <p>
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * <p>
 * <p>
 * 这道题目也很简单，就是要有递归的思想来求出叶子序列就可以了
 */
public class Num897 {
    public static void main(String[] args) {
        Num897 num = new Num897();
//        TreeNode treeNode1 = num.initTree(new int[]{5, 4, 8, 11, 0, 13, 4, 7, 2, 0, 0, 0, 1});
//        TreeNode treeNode1 = num.initTree(new int[]{1, 2, 2, 3, 0, 0, 3, 4, 0, 0, 4});
        TreeNode treeNode1 = num.initTree(new int[]{5, 3, 6, 2, 4, 0, 8, 1, 0, 0, 0, 7, 9});
//        TreeNode treeNode2 = num572.initTree(new int[]{4, 11, 0, 7, 2, 0, 0});
//        TreeNode treeNode1 = num.initTree(new int[]{1, 1});
//        TreeNode treeNode2 = num.initTree(new int[]{1});
        TreeNode node = num.increasingBST(treeNode1);
        System.out.println("node:");
        num.printMiddleOrder(node);
//        System.out.println(num124.ans);
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

    TreeNode node = new TreeNode();

    public TreeNode increasingBST(TreeNode root) {
        TreeNode newNode = node;
        middleOrder(root);
        print(newNode.right);
        return newNode.right;
    }

    private void middleOrder(TreeNode root) {
        if (root == null) {
            return;
        }
        middleOrder(root.left);

        node.right = new TreeNode(root.val);
        node = node.right;
//        print(newRoot);
        System.out.println("root val:" + root.val);
//        System.out.println("root newRoot:"+newRoot);
        middleOrder(root.right);
    }

    private void print(TreeNode node) {
        while (node != null) {
            System.out.print(node.val + "\t");
            node = node.right;
        }
        System.out.println("");
    }

    private void printMiddleOrder(TreeNode root) {
        if (root == null) {
            return;
        }
        printMiddleOrder(root.left);
        System.out.println(root.val);
        printMiddleOrder(root.right);
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

