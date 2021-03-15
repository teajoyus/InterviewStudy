package com.example.javalib.leetcode.栈;

import com.example.javalib.ListNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * author : linmh
 * date : 2021/3/13 18:16
 * description :
 * https://leetcode-cn.com/problems/next-greater-node-in-linked-list/
 * 单调栈的题目
 */
public class Num_1019_链表中的下一个更大节点 {
    public static void main(String[] args) {
        Num_1019_链表中的下一个更大节点 demo = new Num_1019_链表中的下一个更大节点();
        demo.nextLargerNodes(ListNode.parse("[1,7,5,1,9,2,5,1]"));
    }

    public int[] nextLargerNodes(ListNode head) {
        List<Integer> list = new ArrayList<>();
        ListNode listNode = head;
        Stack<Integer> stack = new Stack<>();
        while (listNode != null) {
            while (!stack.isEmpty() && stack.peek() < listNode.val) {
                stack.pop();
            }
            stack.push(listNode.val);
            listNode = listNode.next;
        }
        System.out.println(stack);
        return null;
    }
}
