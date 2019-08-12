package com.example.interviewstudy;

import android.test.AndroidTestCase;

/**
 * Author:mihon
 * Time: 2019\3\14 0014.9:51
 * Description:This is TestCase
 */
public class TestCase extends AndroidTestCase {
    public void testAdd() {
        int i = 1;
        int j = 2;
        int k = i + j;
        assertEquals(4,k);

    }


}
