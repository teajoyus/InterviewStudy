package com.example.javalib.leetcode;

import com.example.javalib.tree.BinaryTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javax.sound.midi.Soundbank;
import javax.swing.event.InternalFrameListener;

/**
 * 请实现一个函数，用来判断一棵二叉树是不是对称的。如果一棵二叉树和它的镜像一样，那么它是对称的。
 * <p>
 * 这题目自己没有解出来
 * 自己的思路本来是搞一个LIst或者stack来表示 但是遇到空节点会有问题 而且空间开销
 * 看网上的答案，其实思路可以改成判断两个节点值是否相等。
 * 只要两个节点相等 并且节点的左右节点与另外一个节点的右左结点相等就OK
 * 这样题目就变成了判断两个节点值是否相等，如果相等就继续递归判断 用节点的左子树与另一个节点的右子树判断
 */
public class Num28 {
    public static void main(String[] args) {
        Num28 num = new Num28();
//        TreeNode treeNode1 = num.initTree(new int[]{5, 4, 8, 11, 0, 13, 4, 7, 2, 0, 0, 0, 1});
        TreeNode treeNode1 = num.initTree(new int[]{1, 2, 2, 2, 0, 2});
//        TreeNode treeNode1 = num.initTree(new int[]{1, 2, 2, 3, 4, 4, 3});
//        TreeNode treeNode2 = num572.initTree(new int[]{4, 11, 0, 7, 2, 0, 0});
//        TreeNode treeNode1 = num.initTree(new int[]{1, 1});
//        TreeNode treeNode2 = num.initTree(new int[]{1});
//        num.print(treeNode1);
//        num.midOrderTraversal(treeNode1);
        System.out.println(num.isSymmetric(treeNode1));
//        System.out.println(num.isBalanced(treeNode1));
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

    /**
     * 中序遍历
     */
    private void midOrderTraversal(TreeNode root, List<TreeNode> list) {
        if (root == null) {
            list.add(new TreeNode(0));
            return;
        }
        midOrderTraversal(root.left, list);
        list.add(root);
        midOrderTraversal(root.right, list);
    }

    public boolean isSymmetric(TreeNode root) {
        if (root == null) return true;
        return isSymmetric(root.left, root.right);

    }

    private boolean isSymmetric(TreeNode s, TreeNode t) {
        if (s != null && t != null) {
            return s.val == t.val && isSymmetric(s.left, t.right) && isSymmetric(s.right, t.left);
        } else return s == null && t == null;
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

