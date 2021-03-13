package com.example.javalib.leetcode.双指针;

import com.example.javalib.ListNode;

import java.util.List;

/**
 * author : linmh
 * date : 2021/3/11 17:49
 * description :
 */
public class Num876链表的中间结点_快慢指针 {
    public static void main(String[] args) {
        Num876链表的中间结点_快慢指针 demo = new Num876链表的中间结点_快慢指针();
        ListNode node = demo.middleNode(ListNode.build(new int[]{1, 2, 3, 4, 5}));
        ListNode node2 = demo.middleNode(ListNode.build(new int[]{1, 2, 3, 4, 5, 6}));
        ListNode.print(node);
        ListNode.print(node2);
    }

    public ListNode middleNode(ListNode head) {
        if (head.next == null) {
            return head;
        }
        ListNode slow = head.next;
        ListNode quick = head.next.next;
        while (quick != null) {
            //这样的话就会取两个中值的右边那个
            if (quick.next != null) {
                slow = slow.next;
                if (quick.next.next != null) {
                    quick = quick.next.next;
                    continue;
                }
            }
            quick = null;
            //  //这样的话就会取两个中值的左边那个
//            if (quick.next != null && quick.next.next != null) {
//                slow = slow.next;
//                quick = quick.next.next;
//            } else {
//                quick = null;
//            }
        }
        return slow;
    }
}
