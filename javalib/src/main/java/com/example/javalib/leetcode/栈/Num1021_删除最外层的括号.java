package com.example.javalib.leetcode.栈;

import java.util.Stack;

/**
 * author : linmh
 * date : 2021/3/13 15:12
 * description :
 * https://leetcode-cn.com/problems/remove-outermost-parentheses/
 * <p>
 * 自己用了下Stack就过了
 * 一开始还是在思考 感觉找不到突破点，
 * 后来根据栈性质来突破，栈可以知道怎么匹配括号对，那么如果当前是(，然后栈顶是），那么就可以出栈
 * 等到栈为空，说明匹配到了一对括号，这时候需要把括号里面的值取出来，那么就需要知道从哪个索引取
 * 所以记录了一个index变量
 */
public class Num1021_删除最外层的括号 {
    public static void main(String[] args) {
        Num1021_删除最外层的括号 demo = new Num1021_删除最外层的括号();
        System.out.println(demo.removeOuterParentheses("(()())(())"));
    }

    public String removeOuterParentheses(String S) {
        Stack<Character> stack = new Stack<>();
        int index = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < S.length(); i++) {
            char ch = S.charAt(i);
            if (stack.isEmpty()) {
                index = i;
                stack.push(ch);
            } else {
                if (ch == ')' && stack.peek() == '(') {
                    stack.pop();
                } else {
                    stack.push(ch);
                }
            }
            if (stack.isEmpty()) {
                sb.append(S.substring(index + 1, i));
            }
        }
        return sb.toString();
    }
}
