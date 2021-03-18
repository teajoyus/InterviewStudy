package com.example.javalib.高频算法题;

import com.example.javalib.ListNode;

import java.util.Stack;

/**
 * author : linmh
 * date : 2021/3/18 14:20
 * description :
 * https://www.nowcoder.com/practice/6ab1d9a29e88450685099d45c9e31e46
 * 这道题目自己想出了用栈的思想来坐做到反转，也算是一种思路
 * 另外一个自己千万千万要掌握的是双指针的思想，你可以走到头了继续从头结点继续走，
 * 直到双指针重叠（虽然说效率也不见得多高，因为可能需要走特别多趟才能相遇，但是不需要额外空间）
 */
public class 两个链表的第一个公共结点 {
    public static void main(String[] args) {
        两个链表的第一个公共结点 demo = new 两个链表的第一个公共结点();
        ListNode node1 = ListNode.parse("[1,2,3]");
        ListNode node2 = ListNode.parse("[4,5]");
        ListNode node3 = ListNode.parse("[6,7]");
        node1.next.next.next = node3;
        node2.next.next = node3;
        demo.FindFirstCommonNode(node1, node1).print();
        demo.FindFirstCommonNode2(node1, node2).print();
    }

    /**
     * @param pHead1
     * @param pHead2
     * @return
     */
    public ListNode FindFirstCommonNode2(ListNode pHead1, ListNode pHead2) {
        if (pHead1 == null || pHead2 == null) {
            return null;
        }
        ListNode p1 = pHead1, p2 = pHead2;
        int index = 0;
        while (p1 != p2) {
            index++;
            p1 = p1 == null ? pHead1 : p1.next;
            p2 = p2 == null ? pHead2 : p2.next;
        }
        System.out.println(index);
        return p1;


    }

    /**
     * 使用一个栈来让链表得到翻转
     *
     * @param pHead1
     * @param pHead2
     * @return
     */
    public ListNode FindFirstCommonNode(ListNode pHead1, ListNode pHead2) {
        Stack<ListNode> stack = new Stack<>();
        Stack<ListNode> stack2 = new Stack<>();
        ListNode next = pHead1;
        while (next != null) {
            stack.push(next);
            next = next.next;
        }
        next = pHead2;
        while (next != null) {
            stack2.push(next);
            next = next.next;
        }
        ListNode comm = null;
        while (!stack.isEmpty() && !stack2.isEmpty()) {
            ListNode node = stack.pop();
            if (node == stack2.pop()) {
                comm = node;
            } else {
                return comm;
            }
        }
        return comm;

    }
}
