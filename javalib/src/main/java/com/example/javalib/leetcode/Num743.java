package com.example.javalib.leetcode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * author : linmh
 * date : 2021/3/8 16:36
 * description :
 */
public class Num743 {
    public static void main(String[] args) {

    }

    public int networkDelayTime(int[][] times, int n, int k) {
        Queue<int[]> queue = new LinkedList<>();
        for (int i = 0; i < times.length; i++) {
            if (times[i][0] == k) {
                queue.offer(times[i]);
            }
        }
        int maxTimes = -1;
        while (!queue.isEmpty()){
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] node = queue.poll();
                int w = node[2];

            }
        }
        return 0;
    }
}
