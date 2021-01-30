package com.example.javalib.leetcode;


/**
 * 给定一棵二叉搜索树，请找出其中第k大的节点。
 * <p>
 * <p>
 * 通过率78%，但是一开始还是很闷逼，后来主要是理解了二叉搜索树的性质，它的左子树一定小于根节点，右子树一定大于等于。
 * 所以我们优先遍历右子树，然后访问根节点，再遍历左子树。这样就是从大到小排序了
 * 这样当遍历到第k个节点就是第k大
 */
public class Num54 {
    public static void main(String[] args) {
        Num54 num = new Num54();
        TreeNode treeNode1 = num.initTree(new int[]{5, 3, 6, 2, 4, 0, 0, 1});
        System.out.println(num.kthLargest(treeNode1, 3));
//        num.print(treeNode1);
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

    private int middleOrder(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int num = middleOrder(root.right);
        if (num > 0) {
            return num;
        }
        k--;
        if (k == 0) {
            return root.val;
        }
//        System.out.println(root.val);
        num = middleOrder(root.left);
        if (num > 0) {
            return num;
        }
        return 0;
    }

    private int k;

    public int kthLargest(TreeNode root, int k) {
        this.k = k;

        return middleOrder(root);
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

