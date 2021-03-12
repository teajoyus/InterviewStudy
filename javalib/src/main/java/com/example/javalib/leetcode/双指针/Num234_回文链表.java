package com.example.javalib.leetcode.双指针;


import com.example.javalib.ListNode;

/**
 * author : linmh
 * date : 2021/3/11 11:26
 * description :
 * https://leetcode-cn.com/problems/palindrome-linked-list/
 * <p>
 * 正常来说解法是：用快慢指针定位出中点，然后把中点的一半去反转，反转完再去比较
 * <p>
 * 但是自己学的是一种递归思想，虽然说空间复杂度是O(n)
 * 像这种需要知道最后面的数据，我们就可以用递归来拿到。
 * 因为递归可以进行倒退，相当于链表有了反向的功能
 */
public class Num234_回文链表 {
    public static void main(String[] args) {
        Num234_回文链表 demo = new Num234_回文链表();
        ListNode node = ListNode.build(new int[]{1, 2, 3, 4, 2, 1});
        ListNode.print(node);
        System.out.println(demo.isPalindrome(node));
        //这个需要回顾再学习，感觉是个重点，利用递归的思想来反向输出
        //其实就是跟树的后序遍历一样，遍历到末尾，再递归访问回来
        demo.reversalPrint(node);
        System.out.println("");
    }

    public boolean isPalindrome(ListNode head) {
        frontPointer = head;
        return recursivelyCheck(head);
    }

    private ListNode frontPointer;

    /**
     * 简洁的方式使用递归
     * 这里递归是需要空间开销的，O(n)
     * 但是需要的是学习一种递归的思想
     *
     * @param currentNode
     * @return
     */
    private boolean recursivelyCheck(ListNode currentNode) {
        if (currentNode != null) {
            //递归到最后一个
            if (!recursivelyCheck(currentNode.next)) {
                return false;
            }
            if (frontPointer.val != currentNode.val) {
                return false;
            }
                frontPointer = frontPointer.next;
            }
        return true;
    }

    /**
     * 自己写的一个递归
     *
     * @param head
     * @return
     */
    private boolean track(ListNode head) {
        if (head == null) {
            return true;
        }
        if (!track(head.next)) {
            return false;
        }
        if (frontPointer.val != head.val) {
            return false;
        }
        frontPointer = frontPointer.next;
        return true;
    }


    private void reversalPrint(ListNode node) {
        if (node == null) {
            System.out.println("");
            return;
        }
        reversalPrint(node.next);
        System.out.print(node.val + "\t");
    }


}
