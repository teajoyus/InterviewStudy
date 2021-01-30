package com.example.javalib.csdn;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSDNSpider {
    private static final String URL = "https://blog.csdn.net/lc_miao/article/details/";

    public static void main(String[] args) {
//        get("https://blog.csdn.net/lc_miao/article/details/106803478");
//        get("106442804");
        List<String> blogs = new ArrayList<>();
        blogs.add("106803478");
        blogs.add("106442804");
        for (int i = 0; i < blogs.size(); i++) {
            get(blogs.get(i));
        }

    }

    public static void get(String blog) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    StringBuffer stringBuffer = new StringBuffer();
                    try {
                        URL url = new URL(URL + blog);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setConnectTimeout(5 * 1000);//链接超时
                        urlConnection.setReadTimeout(5 * 1000);//返回数据超时
                        //getResponseCode (1.200请求成功 2.404请求失败)
                        if (urlConnection.getResponseCode() == 200) {
                            //获得读取流写入
                            InputStream inputStream = urlConnection.getInputStream();
                            byte[] bytes = new byte[1024];
                            int len = 0;
                            while ((len = inputStream.read(bytes)) != -1) {
                                stringBuffer.append(new String(bytes, 0, len));
                            }
                        }
//                    System.out.println(stringBuffer);
                        String readCount = getReadCount(stringBuffer.toString());
                        String title = getTitle(stringBuffer.toString());
                        System.out.println(title);
                        System.out.println("当前阅读量："+readCount);
                        System.out.println("");
                        System.out.println("");
                        Random random = new Random();
                        Thread.sleep(random.nextInt(60)*1000);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static String getReadCount(String content) {
        Pattern pattern = Pattern.compile("read-count\">(.*)</span>");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }
    public static String getTitle(String content) {
        Pattern pattern = Pattern.compile("<title>(.*)</title>");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }
}
