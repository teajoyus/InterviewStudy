package com.example.javalib.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * 请考虑一棵二叉树上所有的叶子，这些叶子的值按从左到右的顺序排列形成一个 叶值序列 。
 * 如果有两棵二叉树的叶值序列是相同，那么我们就认为它们是 叶相似 的。
 * <p>
 * 如果给定的两个头结点分别为 root1 和 root2 的树是叶相似的，则返回 true；否则返回 false 。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/leaf-similar-trees
 * <p>
 * <p>
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * <p>
 * <p>
 * 这道题目也很简单，就是要有递归的思想来求出叶子序列就可以了
 */
public class Num872 {
    public static void main(String[] args) {
        Num872 num = new Num872();
//        TreeNode treeNode1 = num.initTree(new int[]{5, 4, 8, 11, 0, 13, 4, 7, 2, 0, 0, 0, 1});
        TreeNode treeNode1 = num.initTree(new int[]{1, 2, 2, 3, 0, 0, 3, 4, 0, 0, 4});
//        TreeNode treeNode2 = num572.initTree(new int[]{4, 11, 0, 7, 2, 0, 0});
//        TreeNode treeNode1 = num.initTree(new int[]{1, 1});
//        TreeNode treeNode2 = num.initTree(new int[]{1});
        System.out.println(num.leafSimilar(treeNode1, treeNode1));
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

    public boolean leafSimilar(TreeNode root1, TreeNode root2) {
        List<Integer> rootList1 = new ArrayList<>();
        List<Integer> rootList2 = new ArrayList<>();
        getLeafToList(root1, rootList1);
        getLeafToList(root2, rootList2);
        return rootList1.equals(rootList2);
    }

    private void getLeafToList(TreeNode root, List<Integer> list) {
        if (root == null) {
            return;
        }
        if (root.left == null && root.right == null) {
            list.add(root.val);
        }
        getLeafToList(root.left, list);
        getLeafToList(root.right, list);
    }

    public int depth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int leftDepth = depth(root.left) + 1;
        int rightDepth = depth(root.right) + 1;
        return Math.max(leftDepth, rightDepth);
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

