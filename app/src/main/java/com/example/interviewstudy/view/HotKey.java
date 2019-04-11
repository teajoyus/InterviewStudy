/**
 * Copyright 2019 bejson.com
 */
package com.example.interviewstudy.view;

import java.util.List;

/**
 * Auto-generated: 2019-03-15 10:38:27
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class HotKey  {

    private List<Data> data;
    private int errorCode;
    private String errorMsg;

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
        return data;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public static class Data {

        private int id;
        private String link;
        private String name;
        private int order;
        private int visible;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getLink() {
            return link;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }

        public void setVisible(int visible) {
            this.visible = visible;
        }

        public int getVisible() {
            return visible;
        }

    }

}