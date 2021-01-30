package com.example.javalib.nowcoder;

import java.util.List;
import java.util.Stack;

/**
 * 给出一个仅包含字符'(',')','{','}','['和']',的字符串，判断给出的字符串是否是合法的括号序列
 * 括号必须以正确的顺序关闭，"()"和"()[]{}"都是合法的括号序列，但"(]"和"([)]"不合法。
 */
public class 括号序列 {
    public static void main(String[] args) {
        System.out.println(new 括号序列().isValid("{[()]}"));
    }

    public boolean isValid(String s) {
        // write code here
        if (s == null || s.length() < 2) {
            return false;
        }
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{' || c == '[' || c == '(') {
                stack.push(s.charAt(i));
            } else {
                if (stack.empty()) {
                    return false;
                }
                char peekC = stack.peek();
                if (c == map(peekC)) {
                    stack.pop();
                } else {
                    return false;
                }
            }
//            System.out.println(stack);
        }
        return stack.empty();
    }

    private char map(char c) {
        switch (c) {
            case '{':
                return '}';
            case '[':
                return ']';
            case '(':
                return ')';
        }
        return c;
    }
}
