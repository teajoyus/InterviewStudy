package com.example.javalib.nowcoder;

/**
 * 快慢指针的解法，
 * 一个指针走两步 一个指针走一步，如果快指针直接到了null 说明没有环， 如果有环的话 总有一次结果会让快指针和慢指针相等。
 */
public class 判断给定的链表中是否有环 {
    public static void main(String[] args) {

    }

    public boolean hasCycle(ListNode head) {
        ListNode p = head;
        ListNode q = head;
        while(p!=null && p.next!=null){
            p = p.next.next;
            q = q.next;
            if(p==q){
                return true;
            }

        }
        return false;
    }

    class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }
    }
}
