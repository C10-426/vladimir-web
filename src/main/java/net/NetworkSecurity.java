package net;


import net.security.AESCipher;
import net.security.Base64;
import net.security.RSACipher;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NetworkSecurity implements Security {

    private AESCipher.KeySpec mKeySpec;

    private int mEncryptCode = -1;

    protected RequestJson createRequestJson(AESCipher.KeySpec keySpec) throws Exception {
        RequestJson reqJson = new RequestJson();
        reqJson.mPublicKeyVer = RSACipher.getInstance().getPublicKeyVer();
        System.out.println(String.format("RAS key:%s %s", RSACipher.getInstance().mPublicKeyVer, new String(RSACipher.getInstance().mPublicKeyRaw)));
        reqJson.mAESKey = RSACipher.getInstance().encryptWithPublicKeyToBase64(keySpec.getKey());
        reqJson.mAESIv = RSACipher.getInstance().encryptWithPublicKeyToBase64(keySpec.getIv());
        return reqJson;
    }

    @Override
    public byte[] encrypt(byte[] source) throws Exception {
        if (mKeySpec == null) {
            mKeySpec = AESCipher.getInstance().randomKey();
        }
//        source = compressToByte(source);

        System.out.println("AES key spec 1:%s" + mKeySpec.toString());
        RequestJson reqJson = createRequestJson(mKeySpec);
        System.out.println(String.format("AES key spec 2:%s %s", reqJson.mAESKey, reqJson.mAESIv));
//        LOGGER.d("AES key spec 2`:%s %s", RSACipher.getInstance().encryptWithPublicKeyToBase64(mKeySpec.getKey()), RSACipher.getInstance().encryptWithPublicKeyToBase64(mKeySpec.getKey()));
        System.out.println(String.format("AES key spec 3:%s %s", reqJson.mAESKey, reqJson.mAESIv));
        System.out.println(String.format("RSA encode request json:%s", reqJson.build()));
        reqJson.mBase64BodyEncrypted = Base64.encode(AESCipher.getInstance().encrypt(source, mKeySpec.getKey()));
        String json = reqJson.build().toString();
//        String json = "{\"d\":\"BakPaPicMXxWdrlW7jrUX\\/HDOoXCYRV3vYEhHaGQG0Z8KkWvG2EZIfbKK2+VBKbofgCsKYVWbX\\/VuwZlgKVUxw==\",\"v\":1,\"k\":\"fdOdXrgR4e34ZzT7nG8p54BxR5tzeuNvwdAXjjzDJN2BrVbrVO3QxPdJGowKbY5+AhdussTiDilQt9SJZgGTp6Frsmtl0MkRxArPV+yVQRgf+QSgxlVaXq3iMAt5WhPZtFGOnkeZMz4HiB\\/GHPNWsUY39ggJmRfYZVqR9y3Pc6o=\"}";
        System.out.println(String.format("RSA encode and gzip request json:%s", json));
        return String.format("%s=%s","requestJson", URLEncoder.encode(json,"utf-8")).getBytes();
    }

    @Override
    public byte[] decrypt(byte[] source) throws Exception {
        if (mKeySpec == null) {
            return null;
        }

        if (source == null) {
            return null;
        }
//        source = uncompressToByte(source);

        ResponseJson respJson = ResponseJson.fromJson(new JSONObject(new String(source)));
        byte[] data = new byte[]{};
        if (respJson != null) {
            mEncryptCode = respJson.c;
            data = Base64.decode(respJson.d);
        }
        return AESCipher.getInstance().decrypt(data, mKeySpec.getKey());
    }

    public int getEncryptCode() {
        return mEncryptCode;
    }

    protected static class RequestJson {
        public int mPublicKeyVer;

        public String mAESKey;

        public String mAESIv;

        public String mBase64BodyEncrypted;

        public RequestJson() {
        }

        public JSONObject build() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("v", mPublicKeyVer);
                jsonObject.put("k", mAESKey);
                jsonObject.put("i", mAESIv);
                jsonObject.put("d", mBase64BodyEncrypted);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }

    private static class ResponseJson {
        int c = -1;

        String d = "";

        public static ResponseJson fromJson(JSONObject jsonObject) {
            ResponseJson responseJson = new ResponseJson();
            if (jsonObject != null) {
                responseJson.c = jsonObject.optInt("c");
                responseJson.d = jsonObject.optString("d");
            }
            return responseJson;
        }

        public JSONObject build() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("c", c);
                jsonObject.put("d", d);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }

    /**
     * 压缩字节数组
     *
     * @param data 待压缩 byte 数组
     * @return
     * @throws IOException
     */
    public static byte[] compressToByte(final byte[] data) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        GZIPOutputStream gzOut = null;
        try {
            gzOut = new GZIPOutputStream(byteOut);
            gzOut.write(data);
            gzOut.close();
            byteOut.close();
            return byteOut.toByteArray();
        } finally {
            //因外部需要依赖抛出的异常，所以不在 finally 块做保护
            if (gzOut != null) {
                gzOut.close();
            }
            if (byteOut != null) {
                byteOut.close();
            }
        }
    }



    /**
     * 解压字节数组
     *
     * @param compressed 待解压 byte 数组
     * @return
     * @throws IOException
     */
    public static byte[] uncompressToByte(final byte[] compressed) throws IOException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(compressed);
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        GZIPInputStream gzIn = null;
        try {
            gzIn = new GZIPInputStream(byteIn);
            int read;
            byte[] buffer = new byte[8 * 1024];
            do {
                read = gzIn.read(buffer);
                if (read > 0) {
                    byteOut.write(buffer, 0, read);
                }
            } while (read >= 0);
            return byteOut.toByteArray();
        } finally {
            //因外部需要依赖抛出的异常，所以不在 finally 块做保护
            if(gzIn != null)
                gzIn.close();
            if(byteOut != null)
                byteOut.close();
        }
    }

}
