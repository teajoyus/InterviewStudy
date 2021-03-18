package com.example.javalib.高频算法题;

import com.example.javalib.ListNode;

import java.util.Stack;

/**
 * https://www.nowcoder.com/practice/c56f6c70fb3f4849bc56e33ff2a50b6b
 * <p>
 * 1、使用栈来解决 FILO   (很重要的思想，自己一定要掌握)
 * 两个栈来存放链表（整个很重要，当允许用缓冲区的话，链表如果需要反着来的话就可以用栈来实现）
 * <p>
 * 两个栈取出来后让第一个栈成为最大的那个
 * <p>
 * 然后就取数据代替头结点
 */
class 两个链表生成相加链表 {
    public static void main(String[] args) {
        两个链表生成相加链表 demo = new 两个链表生成相加链表();
        demo.addInList(ListNode.parse("[9,3,7]"), ListNode.parse("[6,3]")).print();

    }

    public ListNode addInList(ListNode head1, ListNode head2) {
        // write code here
        Stack<ListNode> stack = new Stack<>();
        Stack<ListNode> stack2 = new Stack<>();
        ListNode next = head1;
        while (next != null) {
            stack.push(next);
            next = next.next;
        }
        next = head2;
        while (next != null) {
            stack2.push(next);
            next = next.next;
        }
        //确保第一个比较大
        if (stack.size() < stack2.size()) {
            Stack<ListNode> t = stack;
            stack = stack2;
            stack2 = t;
        }
        int flag = 0;
        ListNode head = null;
        while (!stack.isEmpty()) {
            ListNode node = stack.pop();
            ListNode node2 = stack2.isEmpty() ? null : stack2.pop();
            node.val = node2 == null ? node.val : (node.val + node2.val);
            node.val += flag;
            if (node.val > 9) {
                flag = 1;
                node.val -= 10;
            } else {
                flag = 0;
            }
            node.next = head;
            head = node;
        }
        if (flag == 1) {
            ListNode node = new ListNode(1);
            node.next = head;
            return node;
        }
        return head;
    }
}
