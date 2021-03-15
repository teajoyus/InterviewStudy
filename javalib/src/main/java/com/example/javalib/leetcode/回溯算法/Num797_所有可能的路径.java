package com.example.javalib.leetcode.回溯算法;

import com.example.javalib.ArrayParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * author : linmh
 * date : 2021/3/12 19:23
 * description :
 */
public class Num797_所有可能的路径 {
    public static void main(String[] args) {
        Num797_所有可能的路径 demo = new Num797_所有可能的路径();
//        int[][] graph = new int[4][];
//        graph[0] = new int[]{1, 2};
//        graph[1] = new int[]{3};
//        graph[2] = new int[]{3};
//        graph[3] = new int[]{};
        demo.allPathsSourceTarget(ArrayParser.parseDoubleArray("[[1,2],[3],[3],[]]"));
        System.out.println(demo.lists);
        System.out.println(demo.lists.size());

    }

    private List<List<Integer>> lists = new ArrayList<>();

    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        backTrack(graph, 0, new LinkedList<>());
        return lists;
    }

    public void backTrack(int[][] graph, int index, LinkedList<Integer> list) {
        if (graph.length == list.size() + 1) {
            lists.add(new ArrayList<>(list));
            return;
        }
        //这个才是每次的选择列表 因为每到一个节点， graph[index]表示了这个节点连接的所有节点
        int[] selectArr = graph[index];
        if (!list.contains(index)) {
            list.add(index);
        }
        for (int value : selectArr) {
            if (!list.contains(value)) {
                list.add(value);
                backTrack(graph, value, list);
                list.removeLast();
            }
        }

    }

    public void backTrack2(int[][] graph, int start, LinkedList<Integer> list) {
        if (graph.length == list.size() + 1) {
            lists.add(new ArrayList<>(list));
            return;
        }
        for (int i = start; i < graph.length; i++) {
            if (!list.contains(i)) {
                list.add(i);
            }
            for (int j = 0; j < graph[i].length; j++) {
                if (!list.contains(graph[i][j])) {
                    System.out.println("add；" + graph[i][j]);
                    list.add(graph[i][j]);
                    backTrack2(graph, graph[i][j], list);
                    System.out.println("remove；" + list.getLast());
                    list.removeLast();
                }
            }

        }
    }
}
