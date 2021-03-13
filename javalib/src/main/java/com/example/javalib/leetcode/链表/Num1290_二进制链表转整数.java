package com.example.javalib.leetcode.链表;

import com.example.javalib.ListNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author : linmh
 * date : 2021/3/12 15:13
 * description :
 * <p>
 * https://leetcode-cn.com/problems/convert-binary-number-in-a-linked-list-to-integer/
 * <p>
 * 利用递归的思想就行了
 * 在回退栈中，知道了总长度，减去当前的层级就知道是2的多少次方
 */
public class Num1290_二进制链表转整数 {
    public static void main(String[] args) {
        Num1290_二进制链表转整数 demo = new Num1290_二进制链表转整数();
        System.out.println(demo.getDecimalValue(ListNode.build(new int[]{1, 0, 1})));
    }

    public int getDecimalValue(ListNode head) {
        return getDecimalValue(head, 0);

    }


    private int maxDepth;

    public int getDecimalValue(ListNode head, int depth) {
        if (head == null) {
            maxDepth = depth - 1;
            return 0;
        }
        int val = getDecimalValue(head.next, depth + 1);
        if (head.val == 0) {
            return val;
        }
        return (int) (val + Math.pow(2, maxDepth - depth));

    }
}
