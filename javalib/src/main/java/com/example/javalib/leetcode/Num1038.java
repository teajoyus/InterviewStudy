package com.example.javalib.leetcode;

import java.util.List;

/**
 * 给出二叉 搜索 树的根节点，该树的节点值各不相同，请你将其转换为累加树（Greater Sum Tree），使每个节点 node 的新值等于原树中大于或等于 node.val 的值之和。
 * <p>
 * 提醒一下，二叉搜索树满足下列约束条件：
 * <p>
 * 节点的左子树仅包含键 小于 节点键的节点。
 * 节点的右子树仅包含键 大于 节点键的节点。
 * 左右子树也必须是二叉搜索树。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/binary-search-tree-to-greater-sum-tree
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class Num1038 {
    public static void main(String[] args) {
        Num1038 num = new Num1038();
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

    private int calcChildNode(TreeNode root) {
        if (root == null) return 0;
        int sum = calcChildNode(root.left) + calcChildNode(root.right);
        return sum + root.val;
    }

    private int calc(TreeNode root,int flag) {
        if (root == null) return 0;
        int right = calc(root.right,1);
        if(flag==1) {
            root.val = root.val + right;
        }else{
            root.val = root.val + right;
        }
        calc(root.left,0);
//        root.val = sum;

    }

    public TreeNode bstToGst(TreeNode root) {

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

