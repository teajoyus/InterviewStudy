package com.example.javalib.leetcode.链表;

import com.example.javalib.ListNode;

/**
 * author : linmh
 * date : 2021/3/12 14:11
 * description :
 * <p>
 * https://leetcode-cn.com/problems/remove-linked-list-elements/
 */
public class Num203_移除链表元素 {
    public static void main(String[] args) {
        Num203_移除链表元素 demo = new Num203_移除链表元素();
        ListNode.print(demo.removeElements2(ListNode.build(new int[]{1, 2, 2, 1}), 2));
        ListNode.print(demo.removeElements(ListNode.build(new int[]{1, 2, 2, 1}), 2));
    }

    /**
     * 先递归，再操作
     * 所以是根据递归到最后的回退栈里，
     * 比如1 2 3 3 4 5，要删除3
     * 先执行递归最后第二个的回退栈是 head = 4，那么它的next就是下一个递归栈，返回了5==3？null:5
     * 就返回了5，
     * 回退栈的时候4就是继续指向5
     * 再继续回退到3的时候。
     * 因为节点是3，满足要删除的结点，所以返回了head.next，也极速4给上一个递归栈(3)的next
     * 此时也就是3->3
     * 再继续回退就变成2->4
     * @param head
     * @param val
     * @return
     */
    public ListNode removeElements(ListNode head, int val) {
        if (head == null) {
            return null;
        }
        head.next = removeElements(head.next, val);
        return head.val == val ? head.next : head;
    }

    public ListNode removeElements2(ListNode head, int val) {
        if (head == null) {
            return null;
        }
        while (head != null) {
            if (head.val == val) {
                head = head.next;
            } else {
                break;
            }
        }
        if (head == null || head.next == null) {
            return head;
        }
        ListNode node = head;
        while (node != null) {
            if (node.next != null) {
                if (node.next.val == val) {
                    node.next = node.next.next;
                    continue;
                }
            }
            node = node.next;
        }
        return head;
    }
}
