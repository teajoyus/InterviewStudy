package com.example.javalib.leetcode.bfs;

import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * bfs
 * 给定一个二叉树，在树的最后一行找到最左边的值。
 */
public class Num513 {
    public static void main(String[] args) {
        Num513 num = new Num513();
        TreeNode node = Tree.initTree(new int[]{3, 9, 20, 0, 0, 15, 7});
        System.out.println(num.findBottomLeftValue(node));
    }

    public int findBottomLeftValue(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            boolean leastDepth = true;
            int leftFirst = 0;
            for (int i = 0; i < size; i++) {
                TreeNode treeNode = queue.poll();
                if (i == 0) {
                    leftFirst = treeNode.val;
                }
                if (treeNode.left != null || treeNode.right != null) {
                    leastDepth = false;
                }
                if (treeNode.left != null) {
                    queue.offer(treeNode.left);
                }
                if (treeNode.right != null) {
                    queue.offer(treeNode.right);
                }
            }
            if (leastDepth) {
                return leftFirst;
            }
        }
        return root.val;
    }

}

