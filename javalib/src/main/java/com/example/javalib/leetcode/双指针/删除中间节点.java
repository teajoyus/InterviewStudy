package com.example.javalib.leetcode.双指针;

import com.example.javalib.ListNode;

/**
 * author : linmh
 * date : 2021/3/11 18:17
 * description :
 * https://leetcode-cn.com/problems/delete-middle-node-lcci/
 *
 * 这个领悟错了意思 是要删除中间节点 但是给的参数是中间节点 不是头结点
 */
public class 删除中间节点 {
    public static void main(String[] args) {
        删除中间节点 demo = new 删除中间节点();
        ListNode node = demo.deleteNode(ListNode.build(new int[]{1, 2, 3, 4, 5}));
        ListNode node2 = demo.deleteNode(ListNode.build(new int[]{1, 2, 3, 4, 5, 6}));
        ListNode node3 = demo.deleteNode(ListNode.build(new int[]{4, 5,1,9}));
        ListNode.print(node);
        ListNode.print(node2);
        ListNode.print(node3);
    }

    /**
     * 删除中间节点
     * @param head
     * @return
     */
    public ListNode deleteNode(ListNode head) {
        if (head == null) {
            return null;
        }
        if (head.next == null) {
            return head;
        }
        if (head.next.next == null) {
            return head.next;
        }
        ListNode before = head;
        ListNode slow = before.next;
        ListNode quick = slow.next;
        while (quick != null) {
            //这样的话就会取两个中值的右边那个
            if (quick.next != null && quick.next.next != null) {
                before = slow;
                slow = slow.next;
                quick = quick.next.next;
            } else {
                quick = null;
            }
        }
        before.next = slow.next;
        return head;
    }
}
