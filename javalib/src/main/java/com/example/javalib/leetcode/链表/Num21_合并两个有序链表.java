package com.example.javalib.leetcode.链表;

import com.example.javalib.ListNode;

/**
 * author : linmh
 * date : 2021/3/11 18:37
 * description :
 */
public class Num21_合并两个有序链表 {
    public static void main(String[] args) {
        Num21_合并两个有序链表 demo = new Num21_合并两个有序链表();
        ListNode node = demo.mergeTwoLists2(ListNode.build(new int[]{1, 2, 4}), ListNode.build(new int[]{1, 3, 4}));
        ListNode.print(node);
    }

    /**
     * 递归的思想啊
     * 自己一开始没有领悟到 一定要学习
     * @param l1
     * @param l2
     * @return 每次递归都返回当前一个最小的结点，然后把返回值给前面的next接起来
     */
    public ListNode mergeTwoLists2(ListNode l1, ListNode l2) {
        if (l1 == null) {
            return l2;
        }
        if (l2 == null) {
            return l1;
        }
        if (l1.val < l2.val) {
             l1.next = mergeTwoLists2(l1.next, l2);
             return l1;
        } else {
            l2.next = mergeTwoLists2(l1, l2.next);
            return l2;
        }
    }

    /**
     * 常规思想
     *
     * @param l1
     * @param l2
     * @return
     */
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if (l1 == null) {
            return l2;
        }
        if (l2 == null) {
            return l1;
        }
        ListNode first = l1.val <= l2.val ? l1 : l2;
        ListNode second = first == l1 ? l2 : l1;
        ListNode firstNode = first.next;
        ListNode cur = first;
        while (firstNode != null || second != null) {
            if (firstNode == null) {
                cur.next = second;
                break;
            } else if (second == null) {
                cur.next = firstNode;
                break;
            } else {
                if (firstNode.val < second.val) {
                    cur.next = firstNode;
                    firstNode = firstNode.next;
                } else {
                    cur.next = second;
                    second = second.next;
                }
                cur = cur.next;
            }
        }
        return first;

    }
}
