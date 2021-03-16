package com.example.javalib.高频算法题;

import com.example.javalib.ListNode;

/**
 * https://www.nowcoder.com/practice/75e878df47f24fdc9dc3e400ec6058ca
 */
class 反转链表 {
    public static void main(String[] args) {
        反转链表 demo = new 反转链表();
        demo.ReverseList(ListNode.parse("[1,2,3,4]")).print();
        demo.ReverseList2(ListNode.parse("[1,2,3,4]")).print();


    }

    public ListNode ReverseList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode rev = head;
        ListNode next = head.next;
        //注意  不能再循环中，不然链表就断了。
        //只需要一开始断开，那么末节点也就是头结点后面就不会再串上之前的。
        //而next结点必须在断开之前拿下一个
        rev.next = null;
        while (next != null) {
            ListNode node = next.next;
            next.next = rev;
            rev = next;
            next = node;
        }
        return rev;
    }

    /**
     * 1 2 3 4
     * 第一轮
     * head = 3，得到了last = 4
     * head.next.next = head  == 4->3
     * head.next = null == 4->3->null
     * 返回last = 4
     * （注意这时候3虽然后面断开了，而且4指向3，但是前面的2还连着3的）
     * 第二轮回退栈
     * head = 2 得到的返回值last = 4
     * head.next.next = head == 3->2
     * head.next=null == 3->2->null
     * （注意，3前面还有4，也就是此时是4->3->2->null）
     * 返回last还是 = 4
     * <p>
     * 第三轮
     * head = 1，得到last = 4
     * head.next.next = head == 2->1
     * head.next = null ==1->null
     * 返回last = 4
     * 这时候就是4->3->2->1->null
     *
     * @param head
     * @return
     */
    public ListNode ReverseList2(ListNode head) {
        //一定要判断head.next为空，因为这样才能直接让下面的last能拿到最后一个
        if (head == null || head.next == null) {
            return head;
        }
        //获取最后一个结点
        ListNode last = ReverseList2(head.next);
        //让我的下个结点的下个位置指向我，这样就把我们两个结点反转了过来
        head.next.next = head;
        head.next = null;
        return last;
    }
}
