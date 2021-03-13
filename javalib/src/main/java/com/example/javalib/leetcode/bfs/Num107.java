package com.example.javalib.leetcode.bfs;

import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * BFS
 * https://leetcode-cn.com/problems/binary-tree-level-order-traversal-ii/
 * 给定一个二叉树，返回其节点值自底向上的层序遍历。 （即按从叶子节点所在层到根节点所在的层，逐层从左向右遍历）
 * <p>
 * 就是层次遍历 但是结果需要倒过来
 * <p>
 * PS： 自己是因为学习了BFS的思想，利用了这个思想后和轻易的做了出来
 * BFS算法框架：
 * 创建一个队列，（如果是图就再创建一个visited集合防止回路）
 * 然后在队列里一定注意先int size = queue.size(); 这一步很重要，因为这个size是你这一层节点的个数。
 * 然后遍历这一层节点，poll取出队列节点进行访问
 * 访问后再依次把左节点和右节点加入队列。（上面之所以要先拿到size就是因为这里在循环中又offer了数据）
 */
public class Num107 {
    public static void main(String[] args) {
        Num107 num = new Num107();
        TreeNode node = Tree.initTree(new int[]{3, 9, 20, 0, 0, 15, 7});
        List<List<Integer>> lists = num.levelOrderBottom(node);
        System.out.println("lists:" + lists);
    }

    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        if (root == null) {
            return new ArrayList<>();
        }
        List<List<Integer>> lists = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            List<Integer> list = new ArrayList<>();
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode treeNode = queue.poll();
                list.add(treeNode.val);
                if (treeNode.left != null) {
                    queue.offer(treeNode.left);
                }
                if (treeNode.right != null) {
                    queue.offer(treeNode.right);
                }
            }
            lists.add(list);
        }
        Collections.reverse(lists);
        return lists;
    }


}

