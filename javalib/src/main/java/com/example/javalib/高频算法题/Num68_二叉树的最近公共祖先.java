package com.example.javalib.高频算法题;


import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

/**
 * 给定一个二叉树, 找到该树中两个指定节点的最近公共祖先。
 * <p>
 * 百度百科中最近公共祖先的定义为：“对于有根树 T 的两个结点 p、q，最近公共祖先表示为一个结点 x，满足 x 是 p、q 的祖先且 x 的深度尽可能大（一个节点也可以是它自己的祖先）。”
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/er-cha-shu-de-zui-jin-gong-gong-zu-xian-lcof
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * 这道题目自己没解出来
 * 原因还是自己没有会分情况 分析不出这个场景
 * 没去看清题目说是唯一的并且都存在的
 *
 * 这道题目的两个重点前期是两个节点均存在于树中并且是唯一的，
 * 所以换一种思路，这两个节点要么分布于左右子树，要么一起分布于左子树要么一起分布于右子树。
 * 我们对一个节点分别去搜它的左子树与右子树匹配的结点，如果左右子树都匹配到说明公共祖先是根节点。
 * 如果只在左子树搜到一个节点而右子树没有，那么也可以确定一定都是都存在左子树，因为这两个节点是都存在且唯一的 这是解题的重点。
 *
 * 可以看下代码回顾下
 */
public class Num68_二叉树的最近公共祖先 {
    public static void main(String[] args) {
        Num68_二叉树的最近公共祖先 num = new Num68_二叉树的最近公共祖先();
        TreeNode treeNode1 = Tree.initTree(new int[]{3, 5, 1, 6, 2, 0, 8, 0, 0, 7, 4});
//        System.out.println(num.kthLargest(treeNode1, 3));
//        System.out.println(num.contain(treeNode1, new TreeNode(5)));
//        TreeNode node = num.lowestCommonAncestor(treeNode1, new TreeNode(5), new TreeNode(1));
        TreeNode node = num.lowestCommonAncestor(treeNode1, new TreeNode(6), new TreeNode(0));
//        TreeNode node2 = num.lowestCommonAncestor(treeNode1, new TreeNode(5), new TreeNode(4));
        System.out.println(node.val);
//        System.out.println(node2.val);
//        num.print(treeNode1);
    }


    /**
     * 二叉树的最近公共祖先
     * 思路：
     * 三种情况：
     * 1、p q 一个在左子树 一个在右子树 那么当前节点即是最近公共祖先
     * 2、p q 都在左子树
     * 3、p q 都在右子树
     *
     * @param root
     * @param p
     * @param q
     * @return
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return null;
        }
        //找到相等的一个跟节点
        if (root.val == p.val || root.val == q.val) {
            return root;
        }
        //在左子树找到相等的一个左节点
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        //在右子树找到相等的一个右节点
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        //两个都不是空 说明分别在左右子树，那么取根节点
        if (left != null && right != null) {
            return root;
        }
        if (left != null) {
            // p q 都在左子树
            return left;
        }
        if (right != null) {
            // p q 都在右子树
            return right;
        }
        return null;
    }

}

