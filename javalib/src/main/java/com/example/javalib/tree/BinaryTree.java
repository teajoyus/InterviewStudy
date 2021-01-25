package com.example.javalib.tree;

import com.example.javalib.leetcode.Num32;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class BinaryTree {
    public static void main(String[] args) {
        BinaryTree binaryTree = new BinaryTree();
        TreeNode treeNode1 = binaryTree.initTree(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        System.out.print("层次遍历：");
        binaryTree.levelTraversal(treeNode1);

        System.out.print("前序遍历：");
        binaryTree.preOrderTraversal(treeNode1);
        System.out.print("\n中序遍历：");
        binaryTree.midOrderTraversal(treeNode1);
        System.out.print("\n后序遍历：");
        binaryTree.postOrderTraversal(treeNode1);
        System.out.print("\n层次List：");
        System.out.println(binaryTree.levelOrder(treeNode1));
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

    /**
     * 前序遍历
     */
    private void preOrderTraversal(TreeNode root) {
        if (root == null) return;
        System.out.print(root.val + "\t");
        preOrderTraversal(root.left);
        preOrderTraversal(root.right);
    }
    /**
     * 中序遍历
     */
    private void midOrderTraversal(TreeNode root) {
        if (root == null) return;
        midOrderTraversal(root.left);
        System.out.print(root.val + "\t");
        midOrderTraversal(root.right);
    }
    /**
     * 后序遍历
     */
    private void postOrderTraversal(TreeNode root) {
        if (root == null) return;
        postOrderTraversal(root.left);
        postOrderTraversal(root.right);
        System.out.print(root.val + "\t");
    }

    /**
     * 利用一个level字段来记录层次 按层次来送入List数组
     * @param root
     * @param level
     * @param lists
     */
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
}
