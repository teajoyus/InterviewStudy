package com.example.javalib.nowcoder;

/**
 * 第二次做题，思考了一会后做出来，问题不大
 * 主要一开始错误的是 没有把翻转指针的next初始化为null
 * 导致虽然是翻转了，但是翻转节点最后一个 也就是head，它还跟原本的链绑定在一块
 */

public class 反转链表二次做题 {
    public static void main(String[] args) {
        ListNode node = new ListNode(10);
        node.next = new ListNode(5);
        node.next.next = new ListNode(15);
        ListNode reverse = new 反转链表二次做题().ReverseList(node);
        pritnList(reverse);
    }

    public static class ListNode {
        int val;
        ListNode next = null;

        ListNode(int val) {
            this.val = val;
        }
    }

    public static void pritnList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + "\t");
            head = head.next;
        }
        System.out.println("");
    }

    public ListNode ReverseList(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode cur = head.next;
        ListNode rev = head;
        rev.next = null;
        ListNode next = null;
        while (cur != null) {
            next = cur.next;
            cur.next = rev;
            rev = cur;
            cur = next;
        }
        return rev;
    }
}
