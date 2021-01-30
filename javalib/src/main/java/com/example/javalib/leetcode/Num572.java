package com.example.javalib.leetcode;

import java.util.logging.Level;

/**
 * 给定两个非空二叉树 s 和 t，检验 s 中是否包含和 t 具有相同结构和节点值的子树。
 * s 的一个子树包括 s 的一个节点和这个节点的所有子孙。s 也可以看做它自身的一棵子树。
 * <p>
 * 这是简单的题，自己一会就刷过了
 * 主要的意识就是递归，你想让判断是不是它的子树
 * 首先遍历当前节点如果两者一致就可以去判断两棵树是否完全一致（采用遍历法，当发现两者有任何节点不一致就不是相同的）
 * 如果当前节点两者不一致，那么就分别用它的左子树和右子树来重复上面的操作
 */
public class Num572 {
    public static void main(String[] args) {
        Num572 num572 = new Num572();
//        TreeNode treeNode1 = num572.initTree(new int[]{5, 4, 8, 11, 0, 13, 4, 7, 2, 0, 0, 0, 1});
//        TreeNode treeNode2 = num572.initTree(new int[]{4, 11, 0, 7, 2, 0, 0});
        TreeNode treeNode1 = num572.initTree(new int[]{1, 1});
        TreeNode treeNode2 = num572.initTree(new int[]{1});
        System.out.println(num572.isSubtree(treeNode1, treeNode2));
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

    private int ans = Integer.MIN_VALUE;

    public boolean isSubtree(TreeNode s, TreeNode t) {
        if (s == null || t == null) {
            return false;
        }
        if (s.val == t.val && isSame(s, t)) {
            return true;
        } else {
            return isSubtree(s.left, t) || isSubtree(s.right, t);
        }
    }

    private boolean isSame(TreeNode s, TreeNode t) {
        if (s != null && t != null) {
            if (s.val == t.val) {
                return isSame(s.left, t.left) && isSame(s.right, t.right);
            } else {
                return false;
            }
        } else {
            return s == null && t == null;
        }
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

