package com.example.javalib;


/**
 * author : linmh
 * date : 2021/3/10 18:21
 * description :
 */
public class Tree {
    public static TreeNode parse(String line) {
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
                if ("#".equals(arr[i])) {
                    arr[i] = "0";
                }
                nums[i] = Integer.parseInt(arr[i]);
            }
            return initTree(nums);
        }
        return null;
    }

    public static TreeNode initTree(int[] array) {
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
    private static TreeNode buildTree(int[] nums, int index) {
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

    public static void prePrint(TreeNode root) {
        if (root != null) {
            System.out.print(root.val + "\t");
            prePrint(root.left);
            prePrint(root.right);
        }
    }

}
