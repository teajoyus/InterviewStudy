package com.example.interviewstudy;

import android.net.Uri;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Author:mihon
 * Time: 2019\3\21 0021.17:36
 * Description:This is ContentProviderTest
 */
public class ContentProviderTest extends AndroidTestCase {
    public static final String MY_CONTENT_PROVIDER_URI = "content://com.example.interviewstudy.contentprovider.MyContentProvider";
    public void testCall(){
        Uri uri = Uri.parse(MY_CONTENT_PROVIDER_URI+"/study");
       Bundle bundle = getContext().getContentResolver().call(uri,"callTest",null,null);
        assertEquals(bundle.getString("test"),"test");
    }
}
