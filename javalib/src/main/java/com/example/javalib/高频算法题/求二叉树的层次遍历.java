package com.example.javalib.高频算法题;

import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * author : linmh
 * date : 2021/3/17 13:48
 * description :
 * https://www.nowcoder.com/practice/04a5560e43e24e9db4595865dc9c63a3
 * <p>
 * 就是一个BFS，自己也比较熟悉了
 * 也可以递归来搞.
 * 自己在层次遍历上，递归时通过传入层级，对应到Lists中的每个List
 */
public class 求二叉树的层次遍历 {
    public static void main(String[] args) {
        求二叉树的层次遍历 demo = new 求二叉树的层次遍历();
        System.out.println(demo.levelOrder(Tree.parse("{1,2,3,4,#,#,5}")));
        System.out.println(demo.levelOrder2(Tree.parse("{1,2,3,4,#,#,5}")));
    }

    /**
     * @param root TreeNode类
     * @return int整型ArrayList<ArrayList <>>
     */
    public ArrayList<ArrayList<Integer>> levelOrder(TreeNode root) {
        ArrayList<ArrayList<Integer>> lists = new ArrayList<>();
        if (root == null) {
            return lists;
        }
        // write code here
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            ArrayList<Integer> list = new ArrayList<>();
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
        return lists;
    }

    public ArrayList<ArrayList<Integer>> levelOrder2(TreeNode root) {
        ArrayList<ArrayList<Integer>> lists = new ArrayList<>();
        if (root == null) {
            return lists;
        }
        levelOrderToList(root, 0, lists);
        return lists;
    }

    public void levelOrderToList(TreeNode root, int level, ArrayList<ArrayList<Integer>> lists) {
        if (root == null) {
            return;
        }
        if (lists.size() <= level) {
            lists.add(new ArrayList<>());
        }
        lists.get(level).add(root.val);
        levelOrderToList(root.left, level + 1, lists);
        levelOrderToList(root.right, level + 1, lists);
    }
}
