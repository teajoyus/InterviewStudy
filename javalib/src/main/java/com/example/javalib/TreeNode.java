package com.example.javalib;

/**
 * author : linmh
 * date : 2021/3/10 18:22
 * description :
 */
public class TreeNode {
    public int val;
    public TreeNode left;
    public TreeNode right;

    public TreeNode() {
    }

    public TreeNode(int val) {
        this.val = val;
    }

    public TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
    public void prePrint(){
        Tree.prePrint(this);
    }
}
