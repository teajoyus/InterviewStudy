package com.example.javalib.高频算法题;

import com.example.javalib.ListNode;

/**
 * author : linmh
 * date : 2021/3/17 19:36
 * description :
 * 这个看代码会不理解为什么能最后都走一步就相遇了。
 * 首先用快慢指针到他们相遇的地方（注意这并不是环的入口）
 *
 */
public class 链表中环的入口节点 {
    public static void main(String[] args) {
        System.out.println("222222222222222222222");
        链表中环的入口节点 demo = new 链表中环的入口节点();
        ListNode node = ListNode.parse("[1,2,3,4]");
        ListNode next = node.next;
        while(next.next!=null){
            next = next.next;
        }
        next.next = node;
        System.out.println("val:"+demo.detectCycle(node).val);
    }
    public ListNode detectCycle(ListNode head) {
        if(head==null||head.next==null){
            return null;
        }
        ListNode slow = head.next;
        ListNode quick = head.next.next;
        while(quick!=null&&quick.next!=null){
            slow = slow.next;
            quick = quick.next.next;
            if(slow==quick){
                quick = head;
                while (slow!=quick){
                    slow = slow.next;
                    quick = quick.next;
                }
                return slow;
            }


        }
        return null;
    }
}
