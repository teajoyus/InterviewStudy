package com.example.javalib.leetcode.dfs;

import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * author : linmh
 * date : 2021/3/10 18:21
 * description :
 * <p>
 * https://leetcode-cn.com/problems/binary-tree-paths/
 * <p>
 * 给定一个二叉树，返回所有从根节点到叶子节点的路径。
 * <p>
 * DFS
 *
 * 之前的误区是纠结于选择了left后要删除left 然后再选择right再删除right
 * 其实不用，在选择之前就记录位置，然后分别遍历左右完后再从这个位置删除
 */
public class Num257_二叉树的所有路径 {
    public static void main(String[] args) {
        Num257_二叉树的所有路径 demo = new Num257_二叉树的所有路径();
    }

    List<String> list = new ArrayList<>();

    public List<String> binaryTreePaths(TreeNode root) {
        List<String> list = new ArrayList<>();
        dfs(root, new StringBuilder(), list);
        return list;
    }

    private void dfs(TreeNode root, StringBuilder sb, List<String> paths) {
        if (root == null) return;
        if (root.left == null && root.right == null) {
            paths.add(sb.toString() + root.val);
            return;
        }
        int length = sb.length();
        sb.append(root.val).append("->");
        dfs(root.left, sb, paths);
        dfs(root.right, sb, paths);
        sb.delete(length, sb.length());
    }
}
