package com.example.javalib.高频算法题;

import com.example.javalib.ListNode;

/**
 * https://www.nowcoder.com/practice/f95dcdafbde44b22a6d741baf71653f6
 * {1,2,3,4,5},2
 * 输出2
 *
 * 两种解法：
 * 1、双指针法：快指针比慢指针提前多走n步，这样走完货慢指针的位置就是要删除的位置。
 * 但是我们需要知道删除位置的前一个结点，所以再搞一个pre结点来记录。
 * 另外一个就是要判断要删除的位置是不是头结点，是的话就是返回头结点的下一个
 *
 */
class 删除链表的倒数第n个节点 {
    public static void main(String[] args) {
        删除链表的倒数第n个节点 demo = new 删除链表的倒数第n个节点();
        demo.removeNthFromEnd(ListNode.parse("[1,2,3,4,5]"), 5).print();
//        demo.removeNthFromEnd2(ListNode.parse("[1,2]"), 2).print();
    }

    int length = 0;

    public ListNode removeNthFromEnd(ListNode head, int n) {
        return remove(head, n, 0);
    }

    /**
     * 最终自己还是捣鼓出了递归算法。比较麻烦，
     * 感觉自己要注意递归的返回值。首先需要明确我这个递归每次返回的值的含义是什么
     * 滤清了这个之后就有了脉络了
     * 要删除倒数n个结点，首先我需要先递归到最后一个，在最后一个递归函数就拿到了length
     * 然后这时候的思维需要清晰：
     * 我如果能执行递归函数下面的代码，那肯定是在回退栈的过程了
     * 回退栈中，如果index==length-n就是刚好要删除的那个结点
     * 但是我们要知道删除结点的前一个结点，所以index = length - n - 1
     * 但是整个可能会导致length-n-1等于-1了，也就是这种情况那就是要删除的结点是头结点
     * 那么我们只需要在前面先判断length==n的话就返回头结点的下一个，表示删除头结点
     * @param head
     * @param n
     * @param index
     * @return
     */
    public ListNode remove(ListNode head, int n, int index) {
        if (head == null) {
            length = index;
            return null;
        }
        remove(head.next, n, index + 1);
        if(length==n){
            return head.next;
        }
        if (index == length - n - 1) {
            head.next = head.next.next;
        }
        return head;
    }

    public ListNode removeNthFromEnd2(ListNode head, int n) {
        // write code here
        ListNode front = head;
        ListNode pre = null;
        ListNode after = head;
        for (int i = 0; i < n - 1; i++) {
            after = after.next;
        }
        if (after == null) {
            return null;
        }
        while (after.next != null) {
            after = after.next;
            pre = front;
            front = front.next;
        }
        if (pre == null) {
            return head.next;
        }
        pre.next = pre.next.next;
        return head;
    }
}
