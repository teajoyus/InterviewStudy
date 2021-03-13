package com.example.javalib.leetcode.回溯算法;

/**
 * author : linmh
 * date : 2021/3/10 13:58
 * description :
 * 编写一个程序，通过填充空格来解决数独问题。
 * <p>
 * 一个数独的解法需遵循如下规则：
 * <p>
 * 数字 1-9 在每一行只能出现一次。
 * 数字 1-9 在每一列只能出现一次。
 * 数字 1-9 在每一个以粗实线分隔的 3x3 宫内只能出现一次。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/sudoku-solver
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * 题解：
 * 这道题目其实也不难，但是自己没有做出来。
 * 原因还是倒在了选择列表上。自己弄混淆了。选择列表不等同于去遍历数组，而是看你当下能做出什么选择
 * 在这道题里每次当下只能做出1-9的选择，所以选择列表就是1-9
 * 然后学了个换行技巧：判断j==9的话就必须要换行，所以直接return 和 递归下一个把j置为0，把i+1
 */
public class Num37_解数独 {
    public static void main(String[] args) {
        Num37_解数独 demo = new Num37_解数独();
        char[][] board = {{'5', '3', '.', '.', '7', '.', '.', '.', '.'}, {'6', '.', '.', '1', '9', '5', '.', '.', '.'}, {'.', '9', '8', '.', '.', '.', '.', '6', '.'}, {'8', '.', '.', '.', '6', '.', '.', '.', '3'}, {'4', '.', '.', '8', '.', '3', '.', '.', '1'}, {'7', '.', '.', '.', '2', '.', '.', '.', '6'}, {'.', '6', '.', '.', '.', '.', '2', '8', '.'}, {'.', '.', '.', '4', '1', '9', '.', '.', '5'}, {'.', '.', '.', '.', '8', '.', '.', '7', '9'}};
//        demo.print(board);
        demo.solveSudoku(board);
        System.out.println("之后：");
        demo.print(board);
    }

    private void print(char[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print("\t" + board[i][j]);
            }
            System.out.println("");
        }
    }

    public void solveSudoku(char[][] board) {
        backTrack(board, 0, 0);
    }

    private boolean complete(char[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == '.') {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean backTrack(char[][] board, int i, int j) {
        //如果说一行的索引完了，那么就必须换下一行
        if(j==9){
            return backTrack(board,i+1,0);
        }
        //找到一个可行的解
        if(i==9){
            return true;
        }
        //如果本来就有数字了，那么我们就不需要去做选择，直接下一轮
        if(board[i][j]!='.'){
            return backTrack(board,i,j+1);
        }

        for (char ch = '1'; ch <= '9'; ch++) {
            if (condition(board, i, j, ch)) {
                board[i][j] = ch;
                boolean b = backTrack(board, i, j+1);
                if(b){
                    return true;
                }
                board[i][j] = '.';
            }
        }
        return false;
    }

    /**
     * 自己一开始写的错误的版本
     *
     * @param board
     * @param a
     * @param b
     */
    public void backTrack2(char[][] board, int a, int b) {
        if (complete(board)) {
            print(board);
            return;
        }
        for (int i = a; i < board.length; i++) {
            for (int j = b; j < board[i].length; j++) {
                for (char k = '1'; k <= '9'; k++) {
                    if (condition(board, i, j, k)) {
                        board[i][j] = k;
                        backTrack2(board, i, j + 1);
                        board[i][j] = '.';
                    }
                }
            }
        }

    }

    /**
     * * 数字 1-9 在每一行只能出现一次。
     * * 数字 1-9 在每一列只能出现一次。
     * * 数字 1-9 在每一个以粗实线分隔的 3x3 宫内只能出现一次。
     *
     * @param board
     * @param i
     * @param j
     * @return
     */
    private boolean condition(char[][] board, int i, int j, char ch) {
        if (board[i][j] != '.') {
            return false;
        }
        for (int k = 0; k < 9; k++) {
            if (board[i][k] == ch || board[k][j] == ch) {
                return false;
            }
        }
        int a = i / 3, b = j / 3;
        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 3; l++) {
                if (board[a*3 + k][b*3+l] == ch) {
                    return false;
                }
            }
        }
        return true;
    }
}
