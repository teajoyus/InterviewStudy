package com.example.javalib.高频算法题;

import com.example.javalib.ArrayParser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * author : linmh
 * date : 2021/3/17 11:28
 * description :
 * https://www.nowcoder.com/practice/e3769a5f49894d49b871c09cadd13a61
 *
 * 没做出来，打算绕过
 * 说下思路，如果要时间复杂度和空间复杂度是O(1)的话
 * 可以用HashMap+双向链表（记录头尾结点）的方式。
 * 说下set过程，
 * 当数量没达到，直接把结点put进来，并且把结点连接到末端。
 * 如果数量已经达到了限制，那么把末端的删掉
 */
public class 设计LRU缓存结构 {
    public static void main(String[] args) {
        设计LRU缓存结构 demo = new 设计LRU缓存结构();
        demo.LRU(ArrayParser.parseDoubleArray("[[1,1,1],[1,2,2],[1,3,2],[2,1],[1,4,4],[2,2]]"), 3);

    }

    public int[] LRU(int[][] operators, int k) {
        HashMap<Integer, Integer> map = new HashMap<>();
        LinkedList<Integer> linkedList = new LinkedList<>();
        for (int i = 0; i < operators.length; i++) {
            if (operators[i][0] == 1) {
                map.put(operators[i][1], operators[i][2]);
                linkedList.addFirst(operators[i][2]);
            } else {
                int value = map.get(operators[i][1]);
                linkedList.set(0, value);
            }
        }
        return null;
    }

    public static class LRUCache {
        Map<Integer,Node> cacheMap = new HashMap<>();
        Node head = new Node(-1,-1);
        Node tail = new Node(-1,-1);
        public static class Node {
            int key;
            int value;
            public Node pre;
            public Node next;

            public Node(int key, int value) {
                this.key = key;
                this.value = value;
            }
        }
        public void set(int key,int value){
            cacheMap.put(key,new Node(key,value));

        }
        public int get(int key){
            if(cacheMap.containsKey(key)){
                Node node = cacheMap.get(key);
                //前驱结点之下指向后继结点
                node.pre.next = node.next;
                //然后把自己提到链表头
                node.next = head.next;
                head.next = node;
            }
            return 0;
        }
    }
}
