package com.example.javalib.leetcode.树相关;


import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

/**
 * https://leetcode-cn.com/problems/er-cha-sou-suo-shu-de-di-kda-jie-dian-lcof/
 *
 * 给定一棵二叉搜索树，请找出其中第k大的节点。
 * <p>
 * <p>
 * 通过率78%，但是一开始还是很闷逼，后来主要是理解了二叉搜索树的性质，它的左子树一定小于根节点，右子树一定大于等于。
 * 所以我们优先遍历右子树，然后访问根节点，再遍历左子树。这样就是从大到小排序了
 * 这样当遍历到第k个节点就是第k大
 */
public class Num54_二叉搜索树的第k大节点 {
    public static void main(String[] args) {
        Num54_二叉搜索树的第k大节点 num = new Num54_二叉搜索树的第k大节点();
        TreeNode treeNode1 = Tree.initTree(new int[]{5, 3, 6, 2, 4, 0, 0, 1});
        System.out.println(num.kthLargest(treeNode1, 3));
//        num.print(treeNode1);
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
}

