package com.example.javalib.leetcode.链表;

import com.example.javalib.ListNode;

/**
 * author : linmh
 * date : 2021/3/11 11:56
 * description :
 */
public class Num18_删除链表的节点 {
    public static void main(String[] args) {
        Num18_删除链表的节点 demo = new Num18_删除链表的节点();
        ListNode node = demo.deleteNode(ListNode.build(new int[]{1, 2, 3, 4, 5}), 5);
        ListNode node2 = demo.deleteNode2(ListNode.build(new int[]{1, 2, 3, 4, 5}), 5);
        ListNode.print(node);
        ListNode.print(node2);
    }

    /**
     * 自己通过学习了链表的递归思想来删除节点
     *
     * @param head
     * @param val
     * @return 
     * 
     * 这个是个错误的写法。 会有问题。
     * 真正的写法参考：
     * @see Num203_移除链表元素#removeElements(ListNode, int) 
     */
    public ListNode deleteNode(ListNode head, int val) {
        if (head == null) {
            return null;
        }
        if (head.val == val) {
            return head.next;
        }

        if (head.next.val == val) {
            head.next = head.next.next;
            return head;
        }
        deleteNode(head.next, val);
        return head;
    }

    public ListNode deleteNode2(ListNode head, int val) {
        if (head.val == val) {
            return head.next;
        }
        ListNode node = head;
        while (node.next != null) {
            if (node.next.val == val) {
                node.next = node.next.next;
                break;
            }
            node = node.next;
        }
        return head;
    }
}
