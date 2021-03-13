package com.example.javalib.leetcode.回溯算法;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * author : linmh
 * date : 2021/3/10 15:27
 * description :
 * 格雷编码是一个二进制数字系统，在该系统中，两个连续的数值仅有一个位数的差异。
 * <p>
 * 给定一个代表编码总位数的非负整数 n，打印其格雷编码序列。即使有多个不同答案，你也只需要返回其中一种。
 * <p>
 * 格雷编码序列必须以 0 开头。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/gray-code
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class Num89_格雷编码 {
    public static void main(String[] args) {
        Num89_格雷编码 demo = new Num89_格雷编码();
    }

    public List<Integer> grayCode(int n) {
        List<Integer> ret = new ArrayList<>();
        for(int i = 0; i < n; ++i)
            ret.add(i ^ i>>1);
        return ret;
    }

    public void backTrack(int n, LinkedList<Integer> list) {
        //第一位数只能是0
        if(list.isEmpty()){
            list.add(0);
            backTrack(n, list);
            return;
        }
        //第一个是0了，后面的选择列表不可能包括0了 所以1开始
        for (int i = 1; i < n; i++) {
            if (!list.contains(i)) {
                list.add(i);
                backTrack(n, list);
                list.removeLast();
            }
        }
    }

    /**
     * 两个连续的数值仅有一个位数的差异。（自己想到用异或运算，就能知道那些位数的值不相同的就为1，问题转变成如何确定一个二进制中只有一个数字为1）
     * 自己解决：两个数去异或运算、然后结果如果是2的n次方那就是只有一个位为1
     * @return
     */
    private boolean condition(int a,int b){
        return true;
    }
}
