package com.example.javalib.nowcoder;

import com.example.javalib.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 不要小看他，不要 用递归 太低效
 * 用DP吧
 */
public class 平衡二叉树 {
    public static void main(String[] args) {
//        System.out.println(new 平衡二叉树().IsBalanced_Solution(20));
    }

    public boolean IsBalanced_Solution(TreeNode root) {
        List<String> flag = new ArrayList<>();
        TreeDepth(root, flag);
        return flag.isEmpty();
    }

    /**
     * 求深度
     *
     * @param root
     * @return
     */
    public int TreeDepth(TreeNode root, List<String> flag) {
        if (root == null)
            return 0;
        int left = TreeDepth(root.left, flag);
        int right = TreeDepth(root.right, flag);
        if (Math.abs(left - right) > 1) {
            if (flag.isEmpty()) {
                flag.add("1");
            }
        }
        return left > right ? left + 1 : right + 1;
    }
}
