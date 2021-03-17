package com.example.javalib.高频算法题;

import java.util.Stack;

/**
 * author : linmh
 * date : 2021/3/17 14:39
 * description :
 * https://www.nowcoder.com/practice/54275ddae22f475981afa2244dd448c6
 * <p>
 * 自己做的是把栈两个相互翻转，来实现。
 * 但是效率太低，面试不过。
 */
public class 用两个栈实现队列 {
    public static void main(String[] args) {
        用两个栈实现队列 demo = new 用两个栈实现队列();
    }

    /**
     * 这才是最效率的做法
     * push的时候只管往第一个栈压。
     * 第二个栈用来相当于做缓冲一样。
     * 如果第二个栈的缓冲没了，就去第一个栈中取。一取一放的顺序就颠倒过来了
     */
    public static class 正规解法 {
        Stack<Integer> stack1 = new Stack<Integer>();
        Stack<Integer> stack2 = new Stack<Integer>();

        public void push(int node) {
            stack1.push(node);
        }

        public int pop() {
            if (stack2.isEmpty()) {
                while (!stack1.isEmpty()) {
                    stack2.push(stack1.pop());
                }
            }
            return stack2.pop();

        }
    }


    Stack<Integer> stack1 = new Stack<Integer>();
    Stack<Integer> stack2 = new Stack<Integer>();
    private boolean flag = false;

    public void push(int node) {
        if (flag) {
            while (!stack2.isEmpty()) {
                stack1.push(stack2.pop());
            }
            flag = false;
        }
        stack1.push(node);
    }

    public int pop() {
        if (flag) {
            return stack2.pop();
        }
        stack2.clear();
        int val = 0;
        while (!stack1.isEmpty()) {
            val = stack1.pop();
            if (!stack1.isEmpty()) {
                stack2.push(val);
            }
        }
        flag = true;
        return val;

    }
}
