package rest;

import net.NetworkSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rest.controller.VladimirRestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
//        try {
//            request();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        SpringApplication.run(Application.class, args);

        try {
            NetworkSecurity networkSecurity = new NetworkSecurity();
            byte[] postData = networkSecurity.encrypt(requestJsonStr.getBytes());

            String responseJson = new VladimirRestController().getConfig(URLDecoder.decode(new String(postData).split("=")[1], "utf-8"));
            networkSecurity.decrypt(responseJson.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String requestJsonStr = "{\"id\":\"95918cce-6853-43b5-bab0-36a9b763c6f1\",\"data\":{\"appId\":\"1101152570\"},\"client\":{\"appId\":\"1101152570\",\"sdkVer\":\"1.0\",\"pkgName\":\"com.dcw.app.oneads.nonplugin\",\"caller\":\"oneads_sdk\",\"uid\":\"5541de0350b8c8c2222a6afa328b9a55\",\"os\":\"Android\",\"osVer\":\"8.0.0\",\"appVer\":\"1.0\",\"appVC\":\"1\",\"ex\":{\"model\":\"MIX 2S\",\"brand\":\"Xiaomi\",\"osSdkVer\":26,\"build\":180816010822,\"imei\":\"868144033398790\",\"imsi\":\"460000300719285\",\"deviceId\":\"868144033398790\",\"ip\":\"10.137.220.42\",\"mac\":\"\",\"network\":\"NET_4G\",\"spn\":\"CMCC\",\"longitude\":\"113.370309\",\"latitude\":\"23.131852\",\"locationTime\":1534426298431,\"locationProvider\":\"network\",\"ppi\":5.699926134963945,\"deviceType\":0,\"androidId\":\"d4cd89cb4e740e74\",\"operator\":\"46000\",\"ua\":\"Mozilla\\/5.0 (Linux; Android 8.0.0; MIX 2S Build\\/OPR1.170623.032; wv) AppleWebKit\\/537.36 (KHTML, like Gecko) Version\\/4.0 Chrome\\/68.0.3440.85 Mobile Safari\\/537.36\",\"cpuAbi\":\"arm64-v8a\",\"orientation\":\"p\",\"res\":\"1080*2030\"}}}";

    public static void request() throws Exception {

        String urlPath = new String("http://134.175.51.221:8080/vladimir/getconfig?");
        //String urlPath = new String("http://localhost:8080/Test1/HelloWorld?name=丁丁".getBytes("UTF-8"));


//        String param="requestJson="+ URLEncoder.encode("{\"d\":\"BakPaPicMXxWdrlW7jrUX\\/HDOoXCYRV3vYEhHaGQG0Z8KkWvG2EZIfbKK2+VBKbofgCsKYVWbX\\/VuwZlgKVUxw==\",\"v\":1,\"k\":\"fdOdXrgR4e34ZzT7nG8p54BxR5tzeuNvwdAXjjzDJN2BrVbrVO3QxPdJGowKbY5+AhdussTiDilQt9SJZgGTp6Frsmtl0MkRxArPV+yVQRgf+QSgxlVaXq3iMAt5WhPZtFGOnkeZMz4HiB\\/GHPNWsUY39ggJmRfYZVqR9y3Pc6o=\"}","UTF-8");
        String param="requestJson="+ URLEncoder.encode("{\"d\":\"BakPaPicMXxWdrlW7jrUX\\/HDOoXCYRV3vYEhHaGQG0Z8KkWvG2EZIfbKK2+VBKbofgCsKYVWbX\\/VuwZlgKVUxw==\",\"v\":1,\"k\":\"fdOdXrgR4e34ZzT7nG8p54BxR5tzeuNvwdAXjjzDJN2BrVbrVO3QxPdJGowKbY5+AhdussTiDilQt9SJZgGTp6Frsmtl0MkRxArPV+yVQRgf+QSgxlVaXq3iMAt5WhPZtFGOnkeZMz4HiB\\/GHPNWsUY39ggJmRfYZVqR9y3Pc6o=\"}","UTF-8");

        //建立连接
        URL url=new URL(urlPath);
        HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();

        //设置参数
//        httpConn.setUseCaches(false);   //不允许缓存
        httpConn.setRequestMethod("POST");      //设置POST方式连接

        //设置请求属性
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
        httpConn.setRequestProperty("Charset", "UTF-8");
        httpConn.setRequestProperty("Content-Length", String.valueOf(param.getBytes().length));
        httpConn.setDoOutput(true);     //需要输出
        httpConn.setDoInput(true);      //需要输入
        //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
//        httpConn.connect();

        //建立输入流，向指向的URL传入参数
        OutputStream dos= httpConn.getOutputStream();
        dos.write(param.getBytes());
//        dos.flush();
//        dos.close();

        //获得响应状态
        int resultCode=httpConn.getResponseCode();
        System.out.println(resultCode);
        if(HttpURLConnection.HTTP_OK==resultCode){
            byte[] re = getStringFromStream(httpConn.getInputStream());
            System.out.println(new String(re, "utf-8"));
        }
    }

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