package com.example.javalib.nowcoder;

/**
 * 定义一个pre指针，用来指向反转后的链表头
 * 定义一个cur指针，用来表示当前遍历到的节点
 * 定义一个next指针，用来指向下一个节点（其实是因为cur的next会指向pre，所以需要一个next来保存这个cur.next指针）
 *
 * 首先cur从head起，所以初始化cur = head，
 * 第一步：用next指针拿到cur的下一个节点，即 next = cur.next;
 * 第二步：cur指针的下一个节点指向pre，也就是指向已经反转好的链表头。
 * 第三步：既然cur的next已经指向pre了，那么cur这个也就是新的反转好的链表头，所以pre = cur
 * 第四步：cur向前迈一步 继续遍历。
 *
 *
 *   链表：1 2 3 4 5
 *  第一趟： 2 1 3 4 5
 *  第二趟： 3 2 1 4 5
 *  第三趟： 4 3 2 1 5
 *  第四趟： 5 4 3 2 1
 *
 *  精华也就是定义一个pre指针用来表示反转好的链表头，然后遍历原链表，通过不断的把原链表的next指向这个反转后的链表头
 *  而这个节点就变成新的反转后的链表头。这样遍历到最后就反转了过来
 *
 *  比如 链表：1 2 3 4 5
 *  第一趟时 初始是：cur是1， next指向2，而pre本身是空
 *  然后将cur.next指向pre，也就是为null了（其实这句是反转后的链表尾部，next就是null）
 *  然后pre移动到cur来，pre就变成1
 *  cur再继续移动到next指针，变成2
 *  此时：pre指针 也就是反转后链表头指向了1
 *
 *  第二趟时：cur是2，next是3，pre是1
 *  同样的操作讲cur.next指向pre，也就是指向了1，
 *  然后pre移动到cur来，这时候pre就是2了（这时候pre.next就是1，完成了1和2的反转）
 *  cur继续移动到next变成3
 *
 *  第三趟，cur是3，next是4，pre是2
 *  同样的操作将cur.next指向pre，也就是指向了2（这时候pre.next是2，再后一个next是1，其实也就是3 -> 2-> 1 完成了这三者的反转）
 */

public class 反转链表 {
    public static void main(String[] args) {
        ListNode node = new ListNode(10);
        node.next = new ListNode(5);
        node.next.next = new ListNode(15);
        ListNode reverse = new 反转链表().ReverseList(node);
        pritnList(reverse);
    }

    public static class ListNode {
        int val;
        ListNode next = null;

        ListNode(int val) {
            this.val = val;
        }
    }
    public static void pritnList(ListNode head){
        while (head!=null){
            System.out.print(head.val+"\t");
            head = head.next;
        }
        System.out.println("");
    }
    public ListNode ReverseList(ListNode head) {
        if (head==null||head.next == null) {
            return head;
        }
        ListNode  cur = head;
        ListNode next = null;
        ListNode pre= null;
        while (cur!=null){
            next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre;
    }
}
