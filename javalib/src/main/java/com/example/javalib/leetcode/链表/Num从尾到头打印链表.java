package com.example.javalib.leetcode.链表;

import com.example.javalib.ListNode;

import java.util.ArrayList;
import java.util.List;

/**
 * author : linmh
 * date : 2021/3/12 15:43
 * description :
 */
public class Num从尾到头打印链表 {
    public static void main(String[] args) {
        Num从尾到头打印链表 demo = new Num从尾到头打印链表();
    }
    private int[] reversePrint(ListNode head) {
        List<Integer> list = new ArrayList<>(10000);
        reversePrint(head, list);
        int[] nums = new int[list.size()];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = list.get(i);
        }
        return nums;
    }

    private void reversePrint(ListNode head, List<Integer> list) {
        if (head == null) return;
        reversePrint(head.next, list);
        list.add(head.val);
    }
}
