package com.example.javalib.leetcode.bfs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * bfs
 * 给定一个 N 叉树，返回其节点值的层序遍历。（即从左到右，逐层遍历）。
 * <p>
 * 树的序列化输入是用层序遍历，每组子节点都由 null 值分隔（参见示例）。
 * <p>
 * 这个还中等难度？简单得要死 一个BFS层次遍历
 */
public class Num429 {
    public static void main(String[] args) {
//        Num429 num = new Num429();
//        Node node = num.initTree(new int[]{3, 9, 20, 0, 0, 15, 7});
//        System.out.println(num.findBottomLeftValue(node));
    }

    public List<List<Integer>> levelOrder(Node root) {
        List<List<Integer>> lists = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> list = new ArrayList<>();
            if (root == null) {
                return lists;
            }
            for (int i = 0; i < size; i++) {
                Node node = queue.poll();
                list.add(node.val);
                for (int j = 0; node.children != null && j < node.children.size(); j++) {
                    queue.offer(node.children.get(j));
                }
            }
            lists.add(list);
        }
        return lists;
    }

    public static class Node {
        public int val;
        public List<Node> children;

        public Node() {
        }

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, List<Node> _children) {
            val = _val;
            children = _children;
        }
    }

    ;

}

