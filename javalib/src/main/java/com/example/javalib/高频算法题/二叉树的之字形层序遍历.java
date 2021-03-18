package com.example.javalib.高频算法题;

import com.example.javalib.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 就是BFS来层次遍历
 * 要一行是左到右，一行右到左
 * 那么搞个LinkedList，加个flag标记顺序是用add还是addFirst就行了
 * 最后还需要转成ArrayList
 *
 */
class 二叉树的之字形层序遍历 {
    public ArrayList<ArrayList<Integer>> zigzagLevelOrder (TreeNode root) {
        // write code here
        ArrayList<ArrayList<Integer>> lists = new ArrayList<>();
        bfs(lists,root);
        return lists;
    }
    private void bfs(ArrayList<ArrayList<Integer>> lists, TreeNode root){
        if(root==null){
            return;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean flag = false;
        while(!queue.isEmpty()){
            int size = queue.size();
            LinkedList<Integer> list = new LinkedList<>();
            for(int i = 0;i<size;i++){
                TreeNode node = queue.poll();
                if(!flag){
                    list.add(node.val);
                }else{
                    list.addFirst(node.val);
                }

                if(node.left!=null){
                    queue.offer(node.left);
                }
                if(node.right!=null){
                    queue.offer(node.right);
                }
            }
            lists.add(new ArrayList<>(list));
            flag = !flag;

        }
    }
}
