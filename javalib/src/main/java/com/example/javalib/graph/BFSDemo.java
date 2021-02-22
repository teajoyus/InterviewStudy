package com.example.javalib.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * author : linmh
 * date : 2021/2/5 11:02
 * description :
 */
public class BFSDemo {
    public static void main(String[] args) {

    }

    public int bfs(Node node, Node target) {
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        queue.add(node);
        visited.add(node);
        int step = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Node cur = queue.poll();
                if (cur == target) {
                    return step;
                }
                for (Node node1 : cur.adj) {
                    if (!visited.contains(node1)) {
                        queue.add(node1);
                        visited.add(node1);
                    }
                }
            }
            step++;
        }
        return step;
    }

    public static class Node {
        public int val;
        public List<Node> adj;
    }
}
