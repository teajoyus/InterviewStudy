package com.example.javalib.leetcode;

/**
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
        TreeNode treeNode1 = num124.initTree(new int[]{5, 4, 8, 11, 0, 13, 4, 7, 2, 0, 0, 0, 1});
        System.out.println(num124.maxPathSum(treeNode1));
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

