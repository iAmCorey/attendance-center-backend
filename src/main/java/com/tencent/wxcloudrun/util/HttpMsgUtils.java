package com.tencent.wxcloudrun.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Http request utils
 * Method:
 * 1. HTTPS POST Json String Proxy/or not
 * 2. HTTP POST Json String
 * 3. HTTPS POST form Proxy/or not
 * @author dev-qiuyu
 * @date 2021/09
 */
@Slf4j
public class HttpMsgUtils {
    private static int SOCKET_TIMEOUT = 60 * 1000; // 60s
    private static int CONNECT_TIMEOUT = 30 * 1000; // 30s
    private static String SSL_PROTOCOL = "TLS";
    private static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };

    private HttpMsgUtils() {}

    private static void trustAllHosts() throws Exception{
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL);
        sslContext.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

    /**
     * Using post method and proxy way to send a json string format request to a https url
     * @param url destination url (https)
     * @param postMsg request message
     * @param charSet charset
     * @param proxyHost proxy host
     * @param proxyPort proxy port
     * @return response
     * @throws Exception
     */
    public static String httpsProxyPostJsonStr(String url, String postMsg, String charSet, String proxyHost, int proxyPort) throws Exception {
        return _httpsPostJsonStr(url, postMsg, charSet, true, proxyHost, proxyPort);
    }

    /**
     * Using post method to send a json string format request to a https url
     * @param url destination url (https)
     * @param postMsg request message
     * @param charSet charset
     * @return response
     * @throws Exception
     */
    public static String httpsPostJsonStr(String url, String postMsg, String charSet) throws Exception{
        return _httpsPostJsonStr(url, postMsg, charSet, false, "", 0);
    }

    /**
     * Using post method to send a json string format request to a https url
     * @param url destination url (https)
     * @param postMsg request message
     * @param charSet charset
     * @param usingProxy whether use proxy or not
     * @param proxyHost proxy host
     * @param proxyPort proxy port
     * @return response
     * @throws Exception
     */
    private static String _httpsPostJsonStr(String url, String postMsg, String charSet, boolean usingProxy, String proxyHost, int proxyPort) throws Exception{
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;

        try {
            trustAllHosts();
            URL url2 = new URL(url);
            HttpsURLConnection connection = null;
            if (usingProxy) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                connection = (HttpsURLConnection) url2.openConnection(proxy);
            } else {
                connection = (HttpsURLConnection) url2.openConnection();
            }
            connection.setHostnameVerifier(DO_NOT_VERIFY);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=" + charSet);
            OutputStream os = connection.getOutputStream();
            os.write(postMsg.getBytes());
            os.flush();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while (null != (line = in.readLine())) {
                result.append(line);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e2) {
                throw e2;
            }
        }

        return result.toString();
    }

    /**
     * Using post method to send a json string format request to a http url
     * @param url destination url (http)
     * @param postMsg request message
     * @param charset charset
     * @return response
     * @throws Exception
     */
    public static String httpPostJsonStr(String url, String postMsg, String charset) throws Exception{
        CloseableHttpClient httpClient = null;
        String result = null;

        try {
            httpClient = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(postMsg, charset);
            post.setEntity(entity);
            RequestConfig config = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();
            post.setConfig(config);
            post.setHeader("accept", "*/*");
            post.setHeader("connection", "Keep-Alive");
            post.setHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            post.setHeader("Content-Type", "application/json; charset=" + charset);

            CloseableHttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            log.info("response status code: " + statusCode);

            if (statusCode < 200 || statusCode > 300) {
                throw new Exception("Request error. Returns status code: " + statusCode);
            }

            HttpEntity httpEntity = response.getEntity();
            if (null != httpEntity) {
                result = EntityUtils.toString(httpEntity, charset);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (Exception e2) {
                    throw e2;
                }
            }
        }

        return result;
    }

    /**
     * Using post method with proxy to send a form data request to a https url
     * @param url destination url(https)
     * @param postMsg request message
     * @param charSet charset
     * @param proxyHost proxy host
     * @param proxyPort proxy port
     * @return response
     * @throws Exception
     */
    public static String httpsProxyPostFormData(String url, Map<String, String> postMsg, String charSet, String proxyHost, int proxyPort) throws Exception {
        return _httpsPostFormData(url, postMsg, charSet, true, proxyHost, proxyPort);
    }

    /**
     * Using post method to send a form data request to a https url
     * @param url destination url(https)
     * @param postMsg request message
     * @param charSet charset
     * @return response
     * @throws Exception
     */
    public static String httpsPostFormData(String url, Map<String, String> postMsg, String charSet) throws Exception {
        return _httpsPostFormData(url, postMsg, charSet, false, "", 0);
    }

    /**
     * Using post method to send a form data request to a https url
     * @param url destination url(https)
     * @param postMsg request message
     * @param charSet charset
     * @param usingProxy whether use proxy or not
     * @param proxyHost proxy host
     * @param proxyPort proxy port
     * @return response
     * @throws Exception
     */
    private static String _httpsPostFormData(String url, Map<String, String> postMsg, String charSet, boolean usingProxy, String proxyHost, int proxyPort) throws Exception {
        trustAllHosts();
        URL reqURL = new URL(url); //创建URL对象
        HttpsURLConnection httpsConn;

        if (usingProxy) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            httpsConn = (HttpsURLConnection) reqURL.openConnection(proxy);
        } else {
            httpsConn = (HttpsURLConnection) reqURL.openConnection();
        }

        httpsConn.setDoOutput(true);
        httpsConn.setRequestMethod("POST");
        httpsConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + charSet);
        httpsConn.setRequestProperty("Accept-Charset", charSet);

        StringBuffer req = new StringBuffer();
        for (Map.Entry<String, String> item :
                postMsg.entrySet()) {
            String key = item.getKey();
            String value = item.getValue();
            req.append(key + "=" + value + "&");
        }
        String reqStr = req.length() > 0?  req.substring(0, req.length() - 1) : "";

        httpsConn.setRequestProperty("Content-Length", reqStr.length() + "");
        OutputStreamWriter out = new OutputStreamWriter(httpsConn.getOutputStream(),charSet);
        out.write(reqStr);
        out.flush();
        out.close();

        //取得该连接的输入流，以读取响应内容
        InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(),charSet);

        //读取服务器的响应内容并显示
        StringBuffer result = new StringBuffer();
        int respInt = insr.read();
        while(respInt != -1){
            result.append((char) respInt);
            respInt = insr.read();
        }

        // 添加流关闭 - 2022/4/15
        insr.close();

        return result.toString();
    }

    /**
     * Http get
     * @param url url
     * @param charSet charset
     * @return
     */
    public static String httpGet(String url, String charSet) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        StringBuilder entityStringBuilder = null;

        try {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            try {
                HttpEntity entity = httpResponse.getEntity();
                entityStringBuilder = new StringBuilder();
                if (null != entity) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), charSet), 8 * 1024);
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line + "\n");
                    }
                }
            } finally {
                httpResponse.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != httpClient) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entityStringBuilder.toString();

    }

}
