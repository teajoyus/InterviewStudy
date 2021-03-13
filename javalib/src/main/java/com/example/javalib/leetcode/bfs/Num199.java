package com.example.javalib.leetcode.bfs;

import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * BFS
 * 给定一棵二叉树，想象自己站在它的右侧，按照从顶部到底部的顺序，返回从右侧所能看到的节点值。
 */
public class Num199 {
    public static void main(String[] args) {
        Num199 num = new Num199();
        TreeNode node = Tree.initTree(new int[]{3, 9, 20, 0, 0, 15, 7});
        List<Integer> list= num.rightSideView(node);
        System.out.println("lists:" + list);
    }

    public List<Integer> rightSideView(TreeNode root) {
        if (root == null) {
            return new ArrayList<>();
        }
        List<Integer> list = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode treeNode = queue.poll();
                if(i+1==size) {
                    list.add(treeNode.val);
                }
                if (treeNode.left != null) {
                    queue.offer(treeNode.left);
                }
                if (treeNode.right != null) {
                    queue.offer(treeNode.right);
                }
            }
        }
        return list;
    }



}

