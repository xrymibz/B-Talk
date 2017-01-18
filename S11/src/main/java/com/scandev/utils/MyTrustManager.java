package com.scandev.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MyTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {
        // TODO Auto-generated METHOD_LOGIN stub

    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {
        // TODO Auto-generated METHOD_LOGIN stub

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // TODO Auto-generated METHOD_LOGIN stub
        return null;
    }

}
