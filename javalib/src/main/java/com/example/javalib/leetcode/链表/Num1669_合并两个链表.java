package com.example.javalib.leetcode.链表;

import com.example.javalib.ListNode;

import java.util.Scanner;

/**
 * author : linmh
 * date : 2021/3/12 15:47
 * description :
 * https://leetcode-cn.com/problems/merge-in-between-linked-lists/
 *
 * 没啥 可以不用复习
 */
public class Num1669_合并两个链表 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Num1669_合并两个链表 demo = new Num1669_合并两个链表();
        ListNode node = demo.mergeInBetween(ListNode.parse("[0,1,2,3,4,5]"), 3, 4, ListNode.parse("[1000000,1000001,1000002]"));
        ListNode.print(node);
    }

    public ListNode mergeInBetween(ListNode list1, int a, int b, ListNode list2) {
        ListNode node1 = list1;
        ListNode node2 = list1;
        ListNode temp = list2;
        for (int i = 0; i < a - 1; i++) {
            node1 = node1.next;
        }
        node2 = node1;
        for (int i = a; i <= b; i++) {
            node2 = node2.next;
        }
        node1.next = list2;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = node2.next;
        return list1;


    }
}
