package com.example.javalib.leetcode.树相关;

import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

/**
 * https://leetcode-cn.com/problems/ping-heng-er-cha-shu-lcof/
 *
 * 给定一个二叉树，判断它是否是高度平衡的二叉树。
 * <p>
 * 本题中，一棵高度平衡二叉树定义为：
 * <p>
 * 一个二叉树每个节点 的左右两个子树的高度差的绝对值不超过 1 。
 * <p>
 * 这是简单难度的 自己一开始就能写出代码
 * 不过一开始没去考虑对isBalanced的递归，直接就求出左右子树的深度差了
 * 其实还要判断如果左右子树深度差小于等于1的话，还需要去看看左子树和右子树是否也满足平衡才行
 */
public class Num110_高度平衡的二叉树 {
    public static void main(String[] args) {
        Num110_高度平衡的二叉树 num = new Num110_高度平衡的二叉树();
//        TreeNode treeNode1 = num.initTree(new int[]{5, 4, 8, 11, 0, 13, 4, 7, 2, 0, 0, 0, 1});
        TreeNode treeNode1 = Tree.initTree(new int[]{1, 2, 2, 3, 0, 0, 3, 4, 0, 0, 4});
//        TreeNode treeNode2 = num572.initTree(new int[]{4, 11, 0, 7, 2, 0, 0});
//        TreeNode treeNode1 = num.initTree(new int[]{1, 1});
//        TreeNode treeNode2 = num.initTree(new int[]{1});
        System.out.println(num.isBalanced(treeNode1));
//        System.out.println(num124.ans);
    }


    public boolean isBalanced(TreeNode root) {
        if (root == null) {
            return true;
        }
        if (Math.abs(depth(root.left) - depth(root.right)) > 1) {
            return false;
        }
        return isBalanced(root.left) && isBalanced(root.right);
    }

    public int depth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int leftDepth = depth(root.left) + 1;
        int rightDepth = depth(root.right) + 1;
        return Math.max(leftDepth, rightDepth);
    }


}

