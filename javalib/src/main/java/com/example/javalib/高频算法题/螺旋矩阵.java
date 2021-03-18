package com.example.javalib.高频算法题;

import com.example.javalib.ArrayParser;

import java.util.ArrayList;

/**
 * author : linmh
 * date : 2021/3/18 11:09
 * description :
 * https://www.nowcoder.com/practice/7edf70f2d29c4b599693dc3aaeea1d31
 *
 * 这道题纯粹就是边界的调试。
 * 最重要的思路是设置4个边界。
 */
public class 螺旋矩阵 {
    public static void main(String[] args) {

        螺旋矩阵 demo = new 螺旋矩阵();
        System.out.println(demo.spiralOrder(ArrayParser.parseDoubleArray("[[1,2,3],[4,5,6],[7,8,9]]")));
        System.out.println(demo.spiralOrder(ArrayParser.parseDoubleArray("[[2,3]]")));
        System.out.println(demo.spiralOrder(ArrayParser.parseDoubleArray("[[3],[2]]")));
        System.out.println(demo.spiralOrder(ArrayParser.parseDoubleArray("[[1,11],[2,12],[3,13],[4,14],[5,15],[6,16],[7,17],[8,18],[9,19],[10,20]]")));
    }

    public ArrayList<Integer> spiralOrder(int[][] matrix) {
        ArrayList<Integer> list = new ArrayList<>();
        if(matrix==null||matrix.length==0){
            return list;
        }
        int left = 0, right = matrix[0].length;
        int top = 0, bottom = matrix.length;
        int tempLeft = left;
        for (int i = top; i < bottom; i++) {
            for (int j = left; j < right; j++) {
                list.add(matrix[i][j]);
            }
            if (i == top) {
                tempLeft = left;
                left = right - 1;
            }
            if (i + 1 == bottom&&i!=top) {
                left = tempLeft;
                right--;
                for (int j = right - 1; j >= left; j--) {
                    list.add(matrix[i][j]);
                }
                bottom--;
                if(right!=left) {
                    for (int j = bottom - 1; j > top; j--) {
                        list.add(matrix[j][left]);
                    }
                }
                i = top;
                top++;
                left++;
//                System.out.println("left:"+left);
//                System.out.println("right:"+right);
//                System.out.println("top:"+top);
//                System.out.println("bottom:"+bottom);
                if (bottom <= top || right <= left) {
                    break;
                }
            }

        }
        return list;
    }
}
