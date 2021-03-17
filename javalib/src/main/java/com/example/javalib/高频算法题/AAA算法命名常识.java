package com.example.javalib.高频算法题;

import java.util.LinkedList;
import java.util.Queue;

/**
 * author : linmh
 * date : 2021/3/17 14:26
 * description :
 */
public class AAA算法命名常识 {
    public static void main(String[] args) {
        AAA算法命名常识 demo = new AAA算法命名常识();
    }

    //队列
    private void queue() {
        //可以用一个LinkedList来表示队列，它是一个链表结构的双向队列
        Queue<Integer> queue = new LinkedList<>();
        //往队列加数据是offer方法
        queue.offer(1);
        //取队列头，但不移除。 是peek
        queue.peek();
        //取对猎头，然后移除。是poll
        queue.poll();
    }

    private void treeTreavle() {
        //先序遍历
//        preOrder();
        //中序遍历
//        midOrder();
        //后续遍历
//        postOrder();
    }
    //链表结构
    private void LinkedList(){
        //双向的，所以每个操作方法提供了First和Last
        LinkedList<Integer> linkedList = new LinkedList<>();
        //头插法O(1)
        linkedList.addFirst(1);
        //尾插法O(1)
        linkedList.addLast(2);
        //末尾移除O(1)
        linkedList.removeLast();
        //头结点移除 O(1)
        linkedList.removeFirst();
        //设置索引，需要遍历
        linkedList.set(2,1);
        linkedList.offerFirst(1);
        linkedList.offerLast(1);
        linkedList.peekFirst();
        linkedList.peekLast();
        linkedList.pollFirst();
        linkedList.pollLast();
    }
}
