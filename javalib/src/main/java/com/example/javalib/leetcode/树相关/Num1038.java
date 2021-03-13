package com.example.javalib.leetcode.树相关;

import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

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
 * <p>
 * 题解：我居然没有领会到这个规律，其实就是个中序遍历就可以去做累加了， 我的天
 */
public class Num1038 {
    public static void main(String[] args) {
        Num1038 num = new Num1038();
//        TreeNode treeNode1 = num.initTree(new int[]{5, 4, 8, 11, 0, 13, 4, 7, 2, 0, 0, 0, 1});
        TreeNode treeNode1 = Tree.initTree(new int[]{1, 2, 2, 2, 0, 2});
//        TreeNode treeNode1 = num.initTree(new int[]{1, 2, 2, 3, 4, 4, 3});
//        TreeNode treeNode2 = num572.initTree(new int[]{4, 11, 0, 7, 2, 0, 0});
//        TreeNode treeNode1 = num.initTree(new int[]{1, 1});
//        TreeNode treeNode2 = num.initTree(new int[]{1});
//        num.print(treeNode1);
//        num.midOrderTraversal(treeNode1);
//        System.out.println(num.isSymmetric(treeNode1));
//        System.out.println(num.isBalanced(treeNode1));
//        System.out.println(num124.ans);
    }

    private int max = 0;

    public TreeNode bstToGst(TreeNode root) {
        inorder(root, 0);
        return root;
    }

    public void inorder(TreeNode root, int parentSum) {
        if (root == null) {
            return;
        }

        inorder(root.right, 0);
        root.val = max + root.val;
        max = root.val;
        inorder(root.left, root.val);
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

}

