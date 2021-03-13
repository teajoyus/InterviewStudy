package com.example.javalib.leetcode.bfs;

import com.example.javalib.Tree;
import com.example.javalib.TreeNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * BFS
 *
 * 在二叉树中，根节点位于深度 0 处，每个深度为 k 的节点的子节点位于深度 k+1 处。
 * <p>
 * 如果二叉树的两个节点深度相同，但父节点不同，则它们是一对堂兄弟节点。
 * <p>
 * 我们给出了具有唯一值的二叉树的根节点 root，以及树中两个不同节点的值 x 和 y。
 * <p>
 * 只有与值 x 和 y 对应的节点是堂兄弟节点时，才返回 true。否则，返回 false。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/cousins-in-binary-tree
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class Num993 {
    public static void main(String[] args) {
        Num993 num = new Num993();
        TreeNode node = Tree.initTree(new int[]{1, 2, 3, 0, 4});
        System.out.println(num.isCousins(node, 2, 3));
    }


    public boolean isCousins(TreeNode root, int x, int y) {
        if (root == null) {
            return false;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        Queue<TreeNode> queueRoot = new LinkedList<>();
        queue.offer(root);
        queueRoot.offer(null);
        while (!queue.isEmpty()) {
            int size = queue.size();
            int parentVal[] = new int[2];
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                TreeNode nodeParent = queueRoot.poll();
                if (nodeParent != null) {
                    if (node.val == x) {
                        parentVal[0] = nodeParent.val;
                    }
                    if (node.val == y) {
                        parentVal[1] = nodeParent.val;
                    }
                }
                if (parentVal[0] != 0 && parentVal[1] != 0 && parentVal[0] != parentVal[1]) {
                    return true;
                }
                if (node.left != null) {
                    queue.offer(node.left);
                    queueRoot.offer(node);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                    queueRoot.offer(node);
                }
            }
        }
        return false;
    }



}

