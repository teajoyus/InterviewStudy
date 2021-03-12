package com.example.javalib.leetcode;

import com.example.javalib.ListNode;

import java.util.ArrayList;
import java.util.List;

/**
 * author : linmh
 * date : 2021/3/4 17:08
 * description :
 */
public class 简单题 {

    public static void main(String[] args) {
        简单题 demo = new 简单题();
    }

    /**
     * 1431
     * <p>
     * 给你一个数组 candies 和一个整数 extraCandies ，其中 candies[i] 代表第 i 个孩子拥有的糖果数目。
     * <p>
     * 对每一个孩子，检查是否存在一种方案，将额外的 extraCandies 个糖果分配给孩子们之后，此孩子有 最多 的糖果。注意，允许有多个孩子同时拥有 最多 的糖果数目。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/kids-with-the-greatest-number-of-candies
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     *
     * @param candies
     * @param extraCandies * @return
     *                     题解： 没啥，别人也这么做
     */
    public List<Boolean> kidsWithCandies(int[] candies, int extraCandies) {
        List<Boolean> list = new ArrayList<>(candies.length);
        int max = candies[0];
        for (int i = 1; i < candies.length; i++) {
            if (max < candies[i]) {
                max = candies[i];
            }
        }
        for (int i = 0; i < candies.length; i++) {
            list.add(candies[i] + extraCandies >= max);
        }
        return list;
    }

    /**
     * 1720
     *
     * @param encoded
     * @param first
     * @return
     */
    public int[] decode(int[] encoded, int first) {
        int[] decode = new int[encoded.length + 1];
        decode[0] = first;
        for (int i = 0; i < encoded.length; i++) {
            decode[i + 1] = decode[i] ^ encoded[i];
        }
        return decode;
    }

    /**
     * 237
     * 删除链表节点，不给head头
     *
     * @param node
     */
    public void deleteNode(ListNode node) {
        if(node.next!=null){
            node.val = node.next.val;
            node.next = node.next.next;
        }
    }

}
