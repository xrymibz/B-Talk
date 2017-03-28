package com.scandev.model;

/**
 * Created by renjicui on 16/8/1.
 */
public enum ServerAddr {
    test ("https://s11webservice-test.cn-north-1.eb.amazonaws.com.cn/"),
    test3 ("http://10.0.2.2:8080/"),
    test4 ("http://192.168.146.1/"),
    local ("http://9801a78c0ccf.ant.amazon.com:8080/"),
    test2 ("https://54.223.223.155:8443/ZHW/"),
    prod ("https://s11webservice-prod.cn-north-1.eb.amazonaws.com.cn/");


    private String addr;

    ServerAddr(String addr) {
        this.addr = addr;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
