package com.example.javalib.leetcode.链表;

import com.example.javalib.ListNode;

/**
 * author : linmh
 * date : 2021/3/11 17:58
 * description :
 * https://leetcode-cn.com/problems/remove-duplicates-from-sorted-list/
 *
 * 只需要用一个变量来标记当前判断的是什么值就好
 * 然后遍历列表
 * 如果判断重复，那么删除后就不用挪到指针，而是continue
 * 如果判断不重复，才挪到指针，并且标记设为下一个值
 */
public class Num83_删除排序链表中的重复元素 {
    public static void main(String[] args) {
        Num83_删除排序链表中的重复元素 demo = new Num83_删除排序链表中的重复元素();
        ListNode node = demo.deleteDuplicates(ListNode.build(new int[]{1, 1, 2, 3, 3}));
        ListNode.print(node);
    }

    public ListNode deleteDuplicates(ListNode head) {
        if(head==null){
            return null;
        }
        ListNode node = head;
        int cur = node.val;
        while (node.next != null) {
            if (node.next.val == cur) {
                node.next = node.next.next;
                continue;
            }
            node = node.next;
            cur = node.val;
        }
        return head;
    }
}
