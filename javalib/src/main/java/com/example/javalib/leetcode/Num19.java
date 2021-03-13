package com.example.javalib.leetcode;


public class Num19 {
    public static void main(String[] args) {
        ListNode node = new ListNode(1);
        node.next = new ListNode(2);
        node.next.next = new ListNode(3);
        node.next.next.next = new ListNode(4);
        node.next.next.next.next = new ListNode(5);
        ListNode reverse = new Num19().removeNthFromEnd(node,5);
        pritnList(reverse);
    }

    public ListNode removeNthFromEnd(ListNode head, int n) {
        frontListNode = head;
        removeNthFromEnd2(head,n,new int[1]);
        return frontListNode;
    }
    private ListNode frontListNode;
    public void removeNthFromEnd2(ListNode head, int n,int[] calc) {
        if(head==null){
            return;
        }
        removeNthFromEnd2(head.next,n,calc);

        calc[0]++;
        System.out.println("calc[0]:"+calc[0]);
        if(calc[0]==n+1){
            if(head.next!=null) {
                head.next = head.next.next;
            }else{
                head.next = null;
            }
            return;
        }else if(head==frontListNode&&calc[0]==n){
            frontListNode =  head.next;
        }

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
