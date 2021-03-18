package com.example.javalib;

import java.util.ArrayList;
import java.util.List;

/**
 * author : linmh
 * date : 2021/3/12 19:25
 * description :
 */
public class ArrayParser {
    public static void main(String[] args) {
        ArrayParser demo = new ArrayParser();
        parseDoubleArray("[[4,3,1],[3,2,4],[3],[4],[]]");
    }

    public static void print(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + "\t");
        }
        System.out.println("");
    }

    public static int[] parseArray(String line) {
        if ("[]".equals(line)) {
            return new int[0];
        }
        line = line.substring(1, line.lastIndexOf("]"));
        String[] arrStr = line.split(",");
        int[] arr = new int[arrStr.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Integer.parseInt(arrStr[i]);
        }
        return arr;
    }

    public static int[][] parseDoubleArray(String line) {
        String str = line;
        str = str.substring(1, str.lastIndexOf("]"));
        List<String> lines = new ArrayList<>();
        while (str != null && str.length() > 0) {
            try {
                String newStr = str.substring(str.indexOf("[") + 1, str.indexOf("]"));
                lines.add(newStr);
                str = str.substring(str.indexOf("]") + 1);
            } catch (Exception e) {
                str = null;
                e.printStackTrace();
            }
        }
        int[][] array = new int[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            if ("".equals(lines.get(i))) {
                array[i] = new int[0];
                continue;
            }
            String[] arr = lines.get(i).split(",");
            if (arr == null | arr.length == 0) {
                array[i] = new int[0];
            } else {
                array[i] = new int[arr.length];
                for (int j = 0; j < array[i].length; j++) {
                    array[i][j] = Integer.parseInt(arr[j]);
                }
            }
        }
        System.out.println("====================");
        System.out.println("数组解析结果：");
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + "\t");
            }
                System.out.println("");
        }
        System.out.println("====================");
        return array;
    }
}
