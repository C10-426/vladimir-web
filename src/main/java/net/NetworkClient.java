package net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * @author leo
 * @date 2018/6/25
 */
public class NetworkClient {

    private final IResponse.Factory mResponseFactory;
    private final Security mPostSecurity;

    public NetworkClient(IResponse.Factory responseFactory, Security postSecurity) {
        mResponseFactory = responseFactory;
        mPostSecurity = postSecurity;
    }

    /**
     * 同步get方式获取url内容,支持自定义Header
     */
    public IResponse get(IRequest request) throws Exception {
        String url = request.getUrl();
        InputStream is = null;
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setUseCaches(false);
            conn.setConnectTimeout(request.getConnectionTimeOut());
            conn.setReadTimeout(request.getSocketTimeOut());
            conn.setRequestMethod(request.getMethod().name());

            // 自定义header
            Map<String, String> headers = request.getHeaders();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                conn.setRequestProperty(key, value);
            }
            if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
                    || conn.getResponseCode()== HttpURLConnection.HTTP_MOVED_TEMP) {
                // 重定向地址下载
                String redirectDownloadUrl = conn.getHeaderField("location");
                request.setUrl(redirectDownloadUrl);
                return get(request);
            } else if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                return mResponseFactory.create(conn.getResponseCode(), read(is));
            }
            return mResponseFactory.create(conn.getResponseCode(), new byte[0]);
        } catch (Exception e) {
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 同步post方式获取url内容,支持自定义Header
     */
    public IResponse post(IRequest request) throws Exception {
        Map<String, String> headers = request.getHeaders();
        String url = request.getUrl();
        InputStream is = null;

        try {
            System.out.println("request:%s" + request.toString());
            byte[] data = mPostSecurity.encrypt(request.buildBody());
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(request.getConnectionTimeOut());
            conn.setReadTimeout(request.getSocketTimeOut());
            conn.setRequestMethod(request.getMethod().name());
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            conn.setRequestProperty("Charset", "UTF-8");

            // 自定义header
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                conn.setRequestProperty(key, value);
            }
//            conn.connect();
            OutputStream os = conn.getOutputStream();
            os.write(data);
            os.flush();
            os.close();
            System.out.println("response url:%s" + request.getUrl());
            System.out.println("response code:%s" + conn.getResponseCode());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                return mResponseFactory.create(conn.getResponseCode(), mPostSecurity.decrypt(getStringFromStream(is)));
            }
            return mResponseFactory.create(conn.getResponseCode(), new byte[0]);
        } catch (Exception e) {
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取输入流为字符串,最常见的是网络请求
     * @param is 输入流
     * @return 输入流内容字符串
     * @throws IOException
     */
    public static byte[] read(InputStream is) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read()) > 0) {
                out.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return out.toByteArray();
    }

    public static byte[] getStringFromStream(InputStream in) throws Exception {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }
        reader.close();
        return buffer.toString().getBytes();
    }
}
