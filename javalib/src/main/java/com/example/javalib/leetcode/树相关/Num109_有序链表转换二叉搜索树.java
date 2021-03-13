package com.example.javalib.leetcode.树相关;

import com.example.javalib.ListNode;
import com.example.javalib.TreeNode;

/**
 * author : linmh
 * date : 2021/3/12 16:09
 * description :
 * https://leetcode-cn.com/problems/convert-sorted-list-to-binary-search-tree/comments/
 * <p>
 * 自己没有做出来
 * <p>
 * 切记 需要回顾一下：
 * <p>
 * 题解;
 * 还是要用递归的思想
 * 快慢指针拿到链表的中点，
 * 然后利用中点构造出一个树的结点，
 * 把中点的前一个结点断开
 * 然后树结点的左节点就等于下一轮递归（下一轮就是链表的左区间，注意在中点那里断开了 才能下一轮继续找中点）
 * 树结点的右节点等于下一轮递归，链表的开始节点就是中点的下一个
 */
public class Num109_有序链表转换二叉搜索树 {
    public static void main(String[] args) {
        Num109_有序链表转换二叉搜索树 demo = new Num109_有序链表转换二叉搜索树();
    }

    private TreeNode treeNode;

    public TreeNode sortedListToBST(ListNode head) {
        if (head == null) {
            return null;
        }
        if (head.next == null) {
            return new TreeNode(head.val);
        }
        ListNode pre = null;
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null) {
            if (fast.next != null) {
                fast = fast.next.next;
            } else {
                break;
            }
            pre = slow;
            slow = slow.next;
        }
        if (pre != null) {
            pre.next = null;
        }
        ListNode rightNode = slow.next;
        TreeNode root = new TreeNode(slow.val);
        root.left = sortedListToBST(head);
        root.right = sortedListToBST(rightNode);
        return root;
    }


    public TreeNode sortedListToBST2(ListNode head) {
        if (head == null) {
            return null;
        }
        if (head.next == null) {
            return new TreeNode(head.val);
        }
        ListNode slow = head;
        ListNode quick = head.next.next;
        while (quick != null) {
            if (quick.next != null) {
                quick = quick.next.next;
            } else {
                break;
            }
            slow = slow.next;
        }
        treeNode = binarySearchTree(treeNode, new TreeNode(slow.val));
        return treeNode;
    }

    public TreeNode binarySearchTree(TreeNode head, TreeNode node) {
        if (head == null) {
            return node;
        }
        if (head.left == null && head.right == null) {
            if (head.val <= node.val) {
                head.right = node;
            } else {
                head.left = node;
            }
        }
        if (head.val <= node.val) {
            binarySearchTree(head.right, node);
        } else {
            binarySearchTree(head.left, node);
        }
        return head;
    }
}
