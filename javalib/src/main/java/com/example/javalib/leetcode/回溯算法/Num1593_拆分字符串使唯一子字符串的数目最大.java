package com.example.javalib.leetcode.回溯算法;

import com.example.javalib.TreeNode;

import java.util.LinkedList;

/**
 * author : linmh
 * date : 2021/3/10 16:52
 * description :
 * 给你一个字符串 s ，请你拆分该字符串，并返回拆分后唯一子字符串的最大数目。
 * <p>
 * 字符串 s 拆分后可以得到若干 非空子字符串 ，这些子字符串连接后应当能够还原为原字符串。但是拆分出来的每个子字符串都必须是 唯一的 。
 * <p>
 * 注意：子字符串 是字符串中的一个连续字符序列。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/split-a-string-into-the-max-number-of-unique-substrings
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * <p>
 * 题解：
 * 这个一开始感觉挺难的，但是看了下答案，其实是一样的，自己一定要理解，回溯算法可以进行穷举。
 * 这种东西我们穷举一下也可以把所有的组合情况都列出来再判断哪些是符合的
 */
public class Num1593_拆分字符串使唯一子字符串的数目最大 {
    public static void main(String[] args) {
        Num1593_拆分字符串使唯一子字符串的数目最大 demo = new Num1593_拆分字符串使唯一子字符串的数目最大();
        System.out.println(demo.maxUniqueSplit("ababccc"));
    }

    public int maxUniqueSplit(String s) {
        int[] max = new int[1];
        backTrack(s, 0, new LinkedList<>(), max);
        return max[0];
    }

    private void backTrack(String s, int start, LinkedList<String> list, int[] max) {
        if (start == s.length()) {
            max[0] = Math.max(max[0], list.size());
            return;
        }
        for (int i = start + 1; i <= s.length(); i++) {
            String sub = s.substring(start, i);
            //字串必须唯一
            if (!list.contains(sub)) {
                list.add(sub);
                backTrack(s, i, list, max);
                list.removeLast();
            }
        }
    }

    /**
     * 给你一个树，请你 按中序遍历 重新排列树，使树中最左边的结点现在是树的根，并且每个结点没有左子结点，只有一个右子结点。
     * <p>
     * <p>
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     * <p>
     * <p>
     * 这道题目也很简单，就是要有递归的思想来求出叶子序列就可以了
     */
    public static class Num897 {
        public static void main(String[] args) {
            Num897 num = new Num897();
    //        TreeNode treeNode1 = num.initTree(new int[]{5, 4, 8, 11, 0, 13, 4, 7, 2, 0, 0, 0, 1});
    //        TreeNode treeNode1 = num.initTree(new int[]{1, 2, 2, 3, 0, 0, 3, 4, 0, 0, 4});
            TreeNode treeNode1 = num.initTree(new int[]{5, 3, 6, 2, 4, 0, 8, 1, 0, 0, 0, 7, 9});
    //        TreeNode treeNode2 = num572.initTree(new int[]{4, 11, 0, 7, 2, 0, 0});
    //        TreeNode treeNode1 = num.initTree(new int[]{1, 1});
    //        TreeNode treeNode2 = num.initTree(new int[]{1});
            TreeNode node = num.increasingBST(treeNode1);
            System.out.println("node:");
            num.printMiddleOrder(node);
    //        System.out.println(num124.ans);
        }

        public TreeNode initTree(int[] array) {
            return buildTree(array, 0);
        }

        /**
         * 构造二叉树，重点在于理解2 * index + 1、2 * index + 2
         * 当你通过把完全二叉树画出来就明白了
         * 每n层都是n-1层的2倍数据，所以当n-1层是从index开始的话，那么到n层就是2*index开头
         *
         * @param nums
         * @param index
         * @return
         */
        public TreeNode buildTree(int[] nums, int index) {
            if (index >= nums.length) {
                return null;
            }
            if (nums[index] == 0) {
                return null;
            }
            TreeNode root = new TreeNode(nums[index]);
            root.left = buildTree(nums, 2 * index + 1);
            root.right = buildTree(nums, 2 * index + 2);
            return root;
        }

        TreeNode node = new TreeNode();

        public TreeNode increasingBST(TreeNode root) {
            TreeNode newNode = node;
            middleOrder(root);
            print(newNode.right);
            return newNode.right;
        }

        private void middleOrder(TreeNode root) {
            if (root == null) {
                return;
            }
            middleOrder(root.left);

            node.right = new TreeNode(root.val);
            node = node.right;
    //        print(newRoot);
            System.out.println("root val:" + root.val);
    //        System.out.println("root newRoot:"+newRoot);
            middleOrder(root.right);
        }

        private void print(TreeNode node) {
            while (node != null) {
                System.out.print(node.val + "\t");
                node = node.right;
            }
            System.out.println("");
        }

        private void printMiddleOrder(TreeNode root) {
            if (root == null) {
                return;
            }
            printMiddleOrder(root.left);
            System.out.println(root.val);
            printMiddleOrder(root.right);
        }

    }
}
