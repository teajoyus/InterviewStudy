package com.example.javalib.leetcode.bfs;

import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * BFS
 * 给你一个二叉树的根节点 root。设根节点位于二叉树的第 1 层，而根节点的子节点位于第 2 层，依此类推。
 * <p>
 * 请你找出层内元素之和 最大 的那几层（可能只有一层）的层号，并返回其中 最小 的那个。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/maximum-level-sum-of-a-binary-tree
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class Num1161 {
    public static void main(String[] args) {
        Num1161 num = new Num1161();
        TreeNode node = Tree.initTree(new int[]{-100,-200,-300,-20,-5,-10,0});
        System.out.println(num.maxLevelSum(node));
    }

    public int maxLevelSum(TreeNode root) {
        if (root == null) {
            return 0;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int maxNumDepth = 1;
        int lastMaxTotal = Integer.MIN_VALUE;
        int depth = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            int curTotal = 0;
            for (int i = 0; i < size; i++) {
                TreeNode treeNode = queue.poll();
                curTotal += treeNode.val;
                if (treeNode.left != null) {
                    queue.offer(treeNode.left);
                }
                if (treeNode.right != null) {
                    queue.offer(treeNode.right);
                }
            }
            if (curTotal > lastMaxTotal) {
                maxNumDepth = depth;
                lastMaxTotal = curTotal;
            }
            depth++;
        }
        return maxNumDepth;
    }




}

