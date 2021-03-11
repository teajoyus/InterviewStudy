package com.example.javalib;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

class duplicateFileNameDemo {
    public static Map<Long, List<String>> map = new TreeMap<>();

    public static void main(String[] args) {
//        System.out.println("222");
//        H:\文件备份\视频\Video\迅雷下载2
        check("H:\\文件备份\\视频");
        System.out.println("文件个数：" + map.size());
        long mb = 0;
        for (Map.Entry<Long, List<String>> entry :
                map.entrySet()) {
            if (entry.getValue().size() > 1) {
                long curMb = entry.getKey() / 1024 / 1024;
                System.out.print("大小：" + curMb + "mb,");
                System.out.println(entry.getValue());
                mb += curMb * (entry.getValue().size() - 1);
            }
        }
        System.out.println("预计总共节省："+mb+"mb");
    }

    public static void check(String path) {
        File file = new File(path);
        if (!file.isDirectory()) {
            add(file.getAbsolutePath(), file.length());
            return;
        }
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
//            System.out.println(files[i].length());
            check(files[i].getPath());
        }
    }

    public static void add(String name, long length) {
        if (map.containsKey(length)) {
            map.get(length).add(name);
        } else {
            List<String> list = new ArrayList<>(3);
            list.add(name);
            map.put(length, list);
        }
    }

}
