package com.example.javalib.高频算法题;

/**
 * https://www.nowcoder.com/practice/11ae12e8c6fe48f883cad618c2e81475
 * <p>
 * 其实也没啥难度 自己写一写就出来了
 * 必须知道哪个字符串比较大，把结果加在那个比较大的身上（或者是用StringBuilder来存放也行）
 * 要注意使用flag是否等于1来表示进位
 * 最重要一定要记住最后返回值也还再判断一次flag是不是等于1
 * 比如"99"+"1"
 * 不能说返回值是"00"啊
 * 所以最后面切记哟flag=1的话就最前面追加个1
 */
class 大数加法 {
    public static void main(String[] args) {
        大数加法 demo = new 大数加法();
        System.out.println(demo.solve("1290", "210"));

    }

    //  210
    // 1290
    public String solve(String s, String t) {
        // write code here
        char[] shortStr, longStr;
        if (s.length() > t.length()) {
            shortStr = t.toCharArray();
            longStr = s.toCharArray();
        } else {
            shortStr = s.toCharArray();
            longStr = t.toCharArray();
        }
        //进位标记
        int flag = 0;
        for (int i = 0; i < longStr.length; i++) {
            int l = longStr[longStr.length - 1 - i] - '0';
            int sho = 0;
            if (shortStr.length - 1 - i >= 0) {
                sho = shortStr[shortStr.length - 1 - i] - '0';
            }
            int total = l + sho + flag;
            if (total >= 10) {
                longStr[longStr.length - 1 - i] = (char) ('0' + (total - 10));
                flag = 1;
            } else {
                longStr[longStr.length - 1 - i] = (char) ('0' + total);
                flag = 0;
            }
        }
        String result = new String(longStr);
        return flag == 0 ? result : "1" + result;
    }
}
