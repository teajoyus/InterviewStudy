package com.example.javalib;

/**
 * author : linmh
 * date : 2021/3/11 11:27
 * description :
 */

public class ListNode {
    public int val;
    public ListNode next;

    public ListNode() {
    }

    public ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }

    public static ListNode parse(String line) {
        String[] fuhao = new String[]{"[", "]"};
        if (line.contains("{")) {
            fuhao = new String[]{"{", "}"};
        }
        if (line.startsWith(fuhao[0])) {
            if (line.equals(fuhao[0] + fuhao[1])) {
                return null;
            }
            String str = line.replace(fuhao[0], "").replace(fuhao[1], "");
            String[] arr = str.split(",");
            int[] nums = new int[arr.length];
            for (int i = 0; i < nums.length; i++) {
                nums[i] = Integer.parseInt(arr[i]);
            }
            return build(nums);
        }
        return null;
    }

    public static ListNode build(int[] nums) {
        ListNode head = new ListNode(nums[0]);
        ListNode next = head;
        for (int i = 1; i < nums.length; i++) {
            next.next = new ListNode(nums[i]);
            next = next.next;
        }
        return head;
    }

    public static void print(ListNode node) {
        if (node != null) {
            System.out.print(node.val + "\t");
            print(node.next);
        } else {
            System.out.println("");
        }

    }

    public void print() {
        print(this);

    }

    @Override
    public String toString() {
        return val + "";
    }
}

