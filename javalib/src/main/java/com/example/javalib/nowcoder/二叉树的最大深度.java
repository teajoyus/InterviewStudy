package com.example.javalib.nowcoder;

import com.example.javalib.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 不要小看他，不要 用递归 太低效
 * 用DP吧
 */
public class 二叉树的最大深度 {
    public static void main(String[] args) {
//        System.out.println(new 平衡二叉树().IsBalanced_Solution(20));
    }
    public int maxDepth (TreeNode root) {
        // write code here
        if(root==null)
            return 0;
        int left=maxDepth(root.left);
        int right=maxDepth(root.right);
        return left>right?left+1:right+1;
    }

    /**
     * 求深度
     * @param root
     * @return
     */
    public int TreeDepth(TreeNode root) {
        if(root==null)
            return 0;
        int left=TreeDepth(root.left);
        int right=TreeDepth(root.right);
        return left>right?left+1:right+1;
    }
}
