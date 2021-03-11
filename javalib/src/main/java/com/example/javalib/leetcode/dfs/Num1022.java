package com.example.javalib.leetcode.dfs;

import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

import java.util.LinkedList;
import java.util.List;

/**
 * 给出一棵二叉树，其上每个结点的值都是 0 或 1 。每一条从根到叶的路径都代表一个从最高有效位开始的二进制数。例如，如果路径为 0 -> 1 -> 1 -> 0 -> 1，那么它表示二进制数 01101，也就是 13 。
 * <p>
 * 对树上的每一片叶子，我们都要找出从根到该叶子的路径所表示的数字。
 * <p>
 * 返回这些数字之和。题目数据保证答案是一个 32 位 整数。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/sum-of-root-to-leaf-binary-numbers
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class Num1022 {
    public static void main(String[] args) {
        Num1022 num = new Num1022();
        TreeNode node = Tree.initTree(new int[]{1, 0, 1, 0, 1, 0, 1});
        int sum = num.sumRootToLeaf(node);
        System.out.println("sum:" + sum);
    }


    List<List<Integer>> res = new LinkedList<>();

    List<List<Integer>> permute(int[] nums) {
        // 记录「路径」
        LinkedList<Integer> track = new LinkedList<>();
        backtrack(nums, track);
        return res;
    }

    void backtrack(int[] nums, LinkedList<Integer> track) {

        for (int i = 0; i < nums.length; i++) {
            track.add(nums[i]);
            backtrack(nums, track);
            track.removeLast();
        }
    }

    public int sumRootToLeaf(TreeNode root) {
        return helper(root, 0);
    }

    private int helper(TreeNode root, int sum) {
        if (root == null) return 0;
        sum = 2 * sum + root.val;
        if (root.left == null && root.right == null) {
            return sum;
        }
        return helper(root.left, sum) + helper(root.right, sum);
    }

    public void travve(TreeNode root) {
        if (root == null) {
            return;
        }
        travve(root.left);
        travve(root.right);
    }

}

