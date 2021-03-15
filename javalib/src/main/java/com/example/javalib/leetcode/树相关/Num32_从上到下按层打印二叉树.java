package com.example.javalib.leetcode.树相关;


import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

import java.util.ArrayList;
import java.util.List;


/**
 * https://leetcode-cn.com/problems/cong-shang-dao-xia-da-yin-er-cha-shu-ii-lcof/
 *
 * 从上到下按层打印二叉树，同一层的节点按从左到右的顺序打印，每一层打印到一行。
 *
 * 理解：就是一个二叉树的遍历，每次递归进入子树时传入一个level+1，这样就可以记录该节点是在哪一层。
 * 遍历到该节点就获取那一层的List来add进去
 */
public class Num32_从上到下按层打印二叉树 {
    public static void main(String[] args) {
        Num32_从上到下按层打印二叉树 num = new Num32_从上到下按层打印二叉树();
        TreeNode treeNode1 = Tree.initTree(new int[]{3, 5, 1, 6, 2, 0, 8, 0, 0, 7, 4});
//        System.out.println(num.kthLargest(treeNode1, 3));
//        System.out.println(num.contain(treeNode1, new TreeNode(5)));
//        TreeNode node = num.lowestCommonAncestor(treeNode1, new TreeNode(5), new TreeNode(1));
//        TreeNode node2 = num.lowestCommonAncestor(treeNode1, new TreeNode(5), new TreeNode(4));
        System.out.println(num.levelOrder(treeNode1));

//        System.out.println(node2.val);
    }


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

