package com.scandev;

import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

// 单例模式
public class CustomHttpClient {

    private static HttpClient customHttpClient;

    private CustomHttpClient() {
        //私有构造方法防止实例化
    }

    public static synchronized HttpClient getHttpClient() throws Exception {
        if (customHttpClient == null) {
            InputStream in = null;
            try {
                in = S11Application.getInstance().getResources().openRawResource(
                        R.raw.zhengshu);
                CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
                Certificate cer = cerFactory.generateCertificate(in);
                KeyStore trustStore = KeyStore.getInstance("BKS");
                trustStore.load(null, null);
                trustStore.setCertificateEntry("trust", cer);

                SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
                Scheme sch = new Scheme("https", socketFactory, 443);
                SchemeRegistry schReg = new SchemeRegistry();
                schReg.register(sch);
                HttpParams params = new BasicHttpParams();
                //下面做一些超时的设置
                HttpConnectionParams.setConnectionTimeout(params, 4000);
                HttpConnectionParams.setSoTimeout(params, 4000);
                //允许环形重定向
                params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
                ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
                customHttpClient = new DefaultHttpClient(conMgr, params);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
        return customHttpClient;
    }
}
