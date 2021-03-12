package com.example.javalib.leetcode.链表;

import com.example.javalib.ListNode;

/**
 * author : linmh
 * date : 2021/3/11 12:30
 * description :
 * https://leetcode-cn.com/problems/reverse-linked-list/
 * <p>
 * 比如说链表结构是 1 2 3  4  5
 * 递归到 head = 4的时候，找到last是5（因为递归head=5的时候，是base case了）
 * 然后把这个head.next指向head，也就是5->4 然后4->null
 * 然后返回last=5 到上一层递归
 * 这时候head = 3了，然后last = 5
 * 这时候又是head.next指向head 也就是 4->3, 3->null
 * （所以这就是为什么不能把last指向head，因为last始终标示当前一个链表头）
 * 这样递归到最后就反转了
 * <p>
 * 特别注意：本来以为head.next.next = head 可以用last.next = head来代替
 * 但是发现不行，因为head
 */
public class Num_反转链表_递归实现 {
    public static void main(String[] args) {
        Num_反转链表_递归实现 demo = new Num_反转链表_递归实现();
        ListNode node = ListNode.build(new int[]{1, 2, 3, 4, 5});
        ListNode.print(demo.reverseList(node));
    }

    /**
     * @param head
     * @return
     */
    public ListNode reverseList(ListNode head) {
        if (head==null||head.next == null) {
            return head;
        }
        //找到最后一个节点
        ListNode last = reverseList(head.next);
        //此时的head就是最后第二个节点，让最后这个节点来指向head，head后面需要去置空，不然又会串联上之前的
        head.next.next = head;
        //头结点后面必须置空，因为头结点方到最后去了
        head.next = null;
        //返回最后一个的头结点 last节点始终是最后一个
//        System.out.println("last:"+last.val);
        return last;

    }
}
