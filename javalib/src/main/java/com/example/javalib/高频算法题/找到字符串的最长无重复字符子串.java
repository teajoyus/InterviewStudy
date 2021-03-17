package com.example.javalib.高频算法题;

import com.example.javalib.ArrayParser;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * author : linmh
 * date : 2021/3/17 14:57
 * description :
 * https://www.nowcoder.com/practice/b56799ebfd684fb394bd315e89324fb4
 * 给定一个数组arr，返回arr的最长无重复子串的长度(无重复指的是所有数字都不相同)。
 *
 * 利用哈希表来解决。
 * 把数值作为key 索引为value。
 * 当遇到重复数字时，
 * 就看下一个起点位置在哪里。
 * 正常来说就是去找出哈希表对应的那个索引就是开始位置，
 * 但是可能那个位置已经太前了，start已经在后头了，所以要判断哪个大
 * 然后即使遇到存在的，也依旧需要往表里丢，因为需要更新这个数值最后的索引，
 *
 */
public class 找到字符串的最长无重复字符子串 {
    public static void main(String[] args) {
        找到字符串的最长无重复字符子串 demo = new 找到字符串的最长无重复字符子串();

        System.out.println(demo.maxLength(ArrayParser.parseArray("[2,2,3,40,3,3,7,8,90,100]")));
    }

    public int maxLength(int[] arr) {
        HashMap<Integer, Integer> map = new HashMap<>();
        int max = 1, start = 0;
        for (int i = 0; i < arr.length; i++) {
            if (map.containsKey(arr[i])) {
                //重复了
                start = Math.max(start, map.get(arr[i]) + 1);
                //注意：这里一定要取最大的start，不然就错误了
                //为什么？ 因为重复数字的索引很可能比start小
            }
            max = Math.max(max, i - start + 1);
            //这里会覆盖值对应的索引，也就是存储的就是数组值最后面的索引
            map.put(arr[i], i);
            System.out.println(map);
        }
        return max;
    }

    public int maxLength2(int[] arr) {
        // write code here
        int maxLength = 0;
        Map<Integer, Integer> map = new Hashtable<>(arr.length);
        for (int i = 0; i < arr.length; i++) {
            if (map.containsKey(arr[i])) {
                int start = map.get(arr[i]);
                maxLength = Math.max(maxLength, map.size());
            }
            map.put(arr[i], i);
        }
        return Math.max(maxLength, map.size());
    }
}
