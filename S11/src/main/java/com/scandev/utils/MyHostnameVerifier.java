package com.scandev.utils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class MyHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String arg0, SSLSession arg1) {
        // TODO Auto-generated METHOD_LOGIN stub
        return true;
    }

}
