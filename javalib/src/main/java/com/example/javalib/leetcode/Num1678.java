package com.example.javalib.leetcode;

public class Num1678 {
    public static void main(String[] args) {
        System.out.println(new Num1678().interpret("(al)G(al)()()G"));
    }
    public String interpret(String command) {
        return command.replace("()","o").replace("(al)","al");
    }
}
