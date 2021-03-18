package com.example.javalib.高频算法题;

import com.example.javalib.ArrayParser;
import com.example.javalib.TreeNode;

/**
 * author : linmh
 * date : 2021/3/18 15:20
 * description :
 * [1,2,3,4,5,6,7],[3,2,4,1,6,5,7]
 * 面试的时候自己画出树和写出对应的数组来
 * <p>
 * 自己一定要学习这个，GET到了
 * 前序遍历那么数组首个元素那就是树的根。
 * 通过数组首个元素去找第二个中序遍历的数组找到的那个位置，那么这个位置的左边就是它的左子树，右边就是右子树.
 * <p>
 * 自己通过这个思想了之后，但是卡在了边界。所以后面回顾一下要注意两个数组怎么筛选边界。
 * 思维模式。
 * preL、preR  和 inL、inR
 * 创建根节点，赋值pre[preL]。
 * <p>
 * 自己特别难理解的就是关于前序遍历的边界确定
 * 对于左子树来说，中序遍历的区间是[inL,i-1]，
 * 而前序遍历的区间重要的一点就是要对应上中序遍历的数目啊（二分之后中序遍历的区间个数和前序形成一颗子树，所以数量肯定是相同的），
 * 而左区间的数目就是 i - inL
 * 所以前序遍历的左边肯定是下一个元素了嘛，就是preL+1
 * 而右区间就是要确保和中序遍历的区间个数一样，也就是preL+1 + 中序遍历左子树区间的个数
 * <p>
 * 而对于右子树来说。中序遍历的区间是[i+1,inR]
 * 而前序遍历毫无疑问右区间肯定是preR，而同样的道理，两个区间个数需要一样
 * 所以前序遍历的左区间就是 preR - 中序遍历的右子树区间数量 + 1 （+1只是说从下个索引开始）
 */
public class 重建二叉树 {
    public static void main(String[] args) {
        重建二叉树 demo = new 重建二叉树();
        demo.reConstructBinaryTree(ArrayParser.parseArray("[1,2,3,4,5,6,7]"), ArrayParser.parseArray("[3,2,4,1,6,5,7]")).prePrint();
    }

    public TreeNode reConstructBinaryTree(int[] pre, int[] in) {
//        return reConstructBinaryTree(pre, 0, pre.length, in, 0, in.length);
//        return reConstructBinaryTree2(pre, 0, pre.length - 1, in, 0, in.length - 1);
        return reConstructBinaryTree3(pre, 0, pre.length - 1, in, 0, in.length - 1);
    }

    public TreeNode reConstructBinaryTree(int[] pre, int preLeft, int preRight, int[] in, int inLeft, int inRight) {
        if (preLeft >= preRight || inLeft >= inRight) { // 无左子树或右子树
            return null;
        }
        TreeNode treeNode = new TreeNode(pre[preLeft]);
        for (int i = inLeft; i < inRight; i++) {
            if (pre[preLeft] == in[i]) {
                int leftCount = i - inLeft;
                treeNode.left = reConstructBinaryTree(pre, preLeft + 1, preLeft + leftCount + 1, in, inLeft, i);
                int rightCount = inRight - i;
                treeNode.right = reConstructBinaryTree(pre, preRight - rightCount + 1, preRight, in, i + 1, inRight);
                break;
            }
        }
        return treeNode;
    }

    //[1,2,3,4,5,6,7],[3,2,4,1,6,5,7]
    //这个可能有点问题
    public TreeNode reConstructBinaryTree2(int[] pre, int preLeft, int preRight, int[] in, int inLeft, int inRight) {
        if (preLeft > preRight || inLeft > inRight) {
            //不合法区间 说明没数据了
            return null;
        }
        TreeNode root = new TreeNode(pre[preLeft]);
        //这里为了减少遍历次数 可以考虑先弄个HashMap来直接get出索引
        for (int i = inLeft; i <= inRight; i++) {
            if (in[i] == pre[preLeft]) {
                int leftCount = i - inLeft;
                // 先序遍历中「从 左边界+1 开始的 leftCount」个元素就对应了中序遍历中「从 左边界 开始到 根节点定位-1」的元素
                root.left = reConstructBinaryTree2(pre, preLeft + 1, preLeft + 1 + leftCount, in, inLeft, i - 1);
                // 先序遍历中「从 左边界+1+左子树节点数目 开始到 右边界」的元素就对应了中序遍历中「从 根节点定位+1 到 右边界」的元素
                root.right = reConstructBinaryTree2(pre, preLeft + 1 + leftCount, preRight, in, i + 1, inRight);
                break;
            }
        }
        return root;
    }

    //[1,2,3,4,5,6,7],[3,2,4,1,6,5,7]
    //第三次写了，自己感悟的重要的一点就是每趟递归都确保前序遍历和中序遍历的区间数目一样
    public TreeNode reConstructBinaryTree3(int[] pre, int preLeft, int preRight, int[] in, int inLeft, int inRight) {
        if (preLeft > preRight || inRight < inLeft) {
            return null;
        }
        TreeNode root = new TreeNode(pre[preLeft]);
        //记住可以优化成HashMap来减少遍历次数
        for (int i = inLeft; i <= inRight; i++) {
            if (in[i] == pre[preLeft]) {
                int leftCount = i - inLeft;
                //反正自己一定就记得两个区间的数量需要一样，我既然知道了前序遍历下个开始，那我的结束点就是中序遍历的区间数量
                root.left = reConstructBinaryTree3(pre, preLeft + 1, preLeft + leftCount, in, inLeft, i - 1);
                int rightCount = inRight - i;
                //我已经知道中序遍历的区间，而我前序遍历的结束点肯定是末尾，那么因为区间数量相同，所以开始点就是末尾 - 中序遍历的区间
                root.right = reConstructBinaryTree3(pre, preRight - rightCount + 1, preRight, in, i + 1, inRight);
                break;
            }
        }
        return root;

    }
}
