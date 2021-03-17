package com.example.javalib.高频算法题;

import com.example.javalib.ListNode;

/**
 * author : linmh
 * date : 2021/3/17 14:03
 * description :
 * https://www.nowcoder.com/practice/a479a3f0c4554867b35356e0d57cf03d
 */
public class 合并有序链表 {
    public static void main(String[] args) {
        合并有序链表 demo = new 合并有序链表();
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
