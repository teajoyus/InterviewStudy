package com.example.javalib.高频算法题;

import com.example.javalib.ListNode;

/**
 * https://www.nowcoder.com/practice/b49c3dc907814e9bbfa8437c251b028e?tab=answerKey
 * <p>
 * 这道题目需要多看 毕竟自己做不出来。
 * 1、可以先写出反转链表的方法
 * 2、分段、分成三段：已反转区域、待翻转区域、未翻转区域
 * 3、借助一个pre结点来指向下一段头结点
 * 4、遍历k个链表，将他们进行反转。
 * 5、需要记录遍历到当前的点的下个点，然后当前点再断开，断开后才能去反转这段链表
 * 6、反转时需要知道反转后的尾指针，其实也就是一开始的头指针
 * 7、饭后反转后的指针交给pre的下个结点，也就是虚结点指向了第一段倒序后的头结点。
 * 8、然后尾结点之前的下个结点断开了之后需要重新赋值下个结点 才能连接上来后面的去走第二轮
 */
class 表中的节点每k个一组翻转 {
    public static void main(String[] args) {
        表中的节点每k个一组翻转 demo = new 表中的节点每k个一组翻转();
        demo.reverseKGroup(ListNode.parse("{1,2,3,4,5}"), 2).print();
        demo.reverse(ListNode.parse("{1,2,3,4,5}")).print();
    }

    /**
     * @param head ListNode类
     * @param k    int整型
     * @return ListNode类
     */
    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode node = new ListNode(0);
        node.next = head;
        ListNode pre = node;
        ListNode end = node;
        while (end.next != null) {
            for (int i = 0; i < k && end != null; i++) {
                end = end.next;
            }
            //个数不够，没得反转
            if (end == null) {
                break;
            }
            //先记录下末结点的下一个结点，以便整个末结点来断开链表
            ListNode next = end.next;
            //断开链表，这样就可以反转这一段链表
            end.next = null;
            //先记录我之前的头结点，因为它后面会变成末结点，我需要知道整个末结点才能连上第二段链表
            ListNode preNext = pre.next;
            //pre的下一个结点就是头结点，把它拿来反转后又付给虚结点的下一个结点
            pre.next = reverse(pre.next);
            //以上就完成了第一段反转，而且我也知道了反转后的头结点是pre的next
            //而反转的末结点就是我们之前的头结点，也就是preNext
            //整个时候就可以把第二段链表头结点就和第一段的末结点连上了
            preNext.next = next;
            //然后pre指针又移向第二段链表前，也就是第一段链表尾
            pre = preNext;
            //没有这一步可不行。因为end是之前第一段反转链表断开的，然后又变成头结点去了。
            //这时候需要把end继续置为第二段的头结点才能开始第二段呀。
            end = pre;
        }
        return node.next;

    }

    private ListNode reverse(ListNode node) {
        if (node == null || node.next == null) {
            return node;
        }
        ListNode rev = node;
        ListNode next = node.next;
        rev.next = null;
        while (next != null) {
            ListNode t = next.next;
            next.next = rev;
            rev = next;
            next = t;
        }
        return rev;
    }

}
