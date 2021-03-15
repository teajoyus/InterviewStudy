package com.example.javalib.leetcode.dfs;

import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

/**
 * author : linmh
 * date : 2021/3/12 18:25
 * description :
 * https://leetcode-cn.com/problems/recover-a-tree-from-preorder-traversal/
 */
public class Num1028_从先序遍历还原二叉树 {
    public static void main(String[] args) {
        Num1028_从先序遍历还原二叉树 demo = new Num1028_从先序遍历还原二叉树();
        TreeNode node = demo.recoverFromPreorder("1-2--3--4-5--6--7");
        Tree.prePrint(node);
    }

    public TreeNode recoverFromPreorder(String S) {
        String[] str = new String[]{S};
        return recoverFromPreorder2(str, 0);
    }

    public TreeNode recoverFromPreorder2(String[] Sstr, int depth) {
        String S = Sstr[0];
        if (S.length() >= depth) {
            if (S.substring(0, depth).replace("-", "").length() != 0) {
                return null;
            }
        } else {
            return null;
        }
        String suffix = S.substring(depth);
        int end = suffix.indexOf("-");
        end = end == -1 ? suffix.length() : end;
        String str = suffix.substring(0, end);
        if ("".equals(str)) {
            return null;
        }
        TreeNode node = new TreeNode(Integer.parseInt(str));
        String substring = suffix.substring(str.length());
        Sstr[0] = substring;
        node.left = recoverFromPreorder2(Sstr, depth + 1);
        node.right = recoverFromPreorder2(Sstr, depth + 1);
        return node;
    }

    public void preOrder(TreeNode node) {
        System.out.println(node.val);
        preOrder(node.left);
        preOrder(node.right);
    }
}
