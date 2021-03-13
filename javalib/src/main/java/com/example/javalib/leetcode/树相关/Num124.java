package com.example.javalib.leetcode.树相关;

import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

/**
 * https://leetcode-cn.com/problems/binary-tree-maximum-path-sum/
 *
 * 给你一个二叉树的根节点 root ，返回其最大路径和。
 * 本题中，路径被定义为一条从树中任意节点出发，沿父节点-子节点连接，达到任意节点的序列。
 * 该路径 至少包含一个 节点，且不一定经过根节点。
 */
public class Num124 {
    public static void main(String[] args) {
        TreeNode treeNode = new TreeNode(-10);
        treeNode.left = new TreeNode(9);
        treeNode.right = new TreeNode(20);
        //xxx

        treeNode.right.left = new TreeNode(15);
        treeNode.right.right = new TreeNode(7);
        Num124 num124 = new Num124();

        treeNode = new TreeNode(5);
        treeNode.left = new TreeNode(4);
        treeNode.right = new TreeNode(8);
        treeNode.left.left = new TreeNode(11);
        treeNode.right.left = new TreeNode(13);
        treeNode.right.right = new TreeNode(4);
        treeNode.left.left.left = new TreeNode(7);
        treeNode.left.left.right = new TreeNode(2);
//        TreeNode treeNode1 = num124.initTree(new int[]{-10, 9, 20, 0, 0, 15, 7});
        TreeNode treeNode1 = Tree.initTree(new int[]{5, 4, 8, 11, 0, 13, 4, 7, 2, 0, 0, 0, 1});
        System.out.println(num124.maxPathSum(treeNode1));
        System.out.println("第二次来做题：");
        TreeNode treeNode2 = new TreeNode(-2);
        treeNode2.right = new TreeNode(-1);
        System.out.println(num124.maxPathSum2(treeNode2));
//        System.out.println(num124.ans);
    }


    private int ans = Integer.MIN_VALUE;

    public int maxPathSum(TreeNode root) {
        maxPath(root);
        return ans;
    }

    /**
     * 最大路径和 首先得有递归的思想，如果你知道你的左子树的最大路径和右子树的最大路径
     * 那么你就知道经过这个节点的最大路径了
     * 而左子树和右子树又分别递归到到叶子节点确定了节点的值，
     * 然后判断右节点大还是左节点大，加上自身节点就可以知道该节点的最大路径
     *
     * @param root
     * @return
     */
    private int maxPath(TreeNode root) {
        if (root == null) return 0;
        int left = Math.max(0, maxPath(root.left));
        int right = Math.max(0, maxPath(root.right));
        ans = Math.max(ans, left + right + root.val);
        return Math.max(left, right) + root.val;
    }

    int maxPath = Integer.MIN_VALUE;

    public int maxPathSum2(TreeNode root) {
        if (root == null) {
            return 0;
        }
        maxChildPathSum(root);
        return maxPath;

    }

    /**
     * 这是第二次写的，没做出来。 样式差不多，卡在判断边界值
     * @param root
     * @return
     */
    public int maxChildPathSum(TreeNode root) {
        if (root == null) {
            return 0;
        }
//        if(root.left==null&&root.right==null){
//            maxPath = Math.max(maxPath,root.val);
//            return root.val;
//        }
        int left = maxChildPathSum(root.left);
        int right = maxChildPathSum(root.right);
        int newPath = root.val + Math.max(left, right);
        maxPath = Math.max(maxPath,Math.max(newPath,Math.max(left,right)));
        return newPath;
    }
}

