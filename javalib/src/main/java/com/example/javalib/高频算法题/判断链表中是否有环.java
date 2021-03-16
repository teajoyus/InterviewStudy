package com.example.javalib.高频算法题;

import com.example.javalib.ListNode;

/**
 * https://www.nowcoder.com/practice/650474f313294468a4ded3ce0f7898b9
 *
 * 这个一下子就过去了
 * 需要注意的是quick.next!=null条件不成立的话要把quick置位空
 * 否则quick没继续向下走。
 */
class 判断链表中是否有环 {
    public static void main(String[] args) {
        判断链表中是否有环 demo = new 判断链表中是否有环();
    }

    public boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        ListNode slow = head;
        ListNode quick = head.next.next;
        while (quick != null) {
            if (slow == quick) {
                return true;
            }
            if (quick.next != null) {
                quick = quick.next.next;
            } else {
                //注意这句啊，不然可能就空指针了，quick.next为空就一直没继续走下一个quick了
                quick = null;
            }
            slow = slow.next;
        }
        return false;
    }
}
