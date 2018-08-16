package rest.controller;

import java.security.PrivateKey;

import javax.crypto.SecretKey;

import org.json.JSONObject;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sun.org.apache.bcel.internal.generic.I2F;

import net.security.AESCipher;
import net.security.Base64;
import sun.launcher.resources.launcher;
import utils.AESUtil;
import utils.Base64Util;
import utils.RSAUtil;

@RestController
public class VladimirRestController {

    @RequestMapping(value = "/getconfig", method = RequestMethod.POST)
    public String getConfig(@RequestParam(required = true) String requestJson) {
        return getConfigHelper(requestJson);
    }

    /**
     * Decrypt data
     * 
     * Json key : { k = AES scret, v = RSA version, d = content }
     * 
     * @param json
     * @return
     */
    private String getConfigHelper(String json) {
        if (StringUtils.isEmpty(json)) {
            // TODO : logging
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            // get aes secret key
            int rsaVersion = jsonObject.getInt("v");
            String aesKey = this.rsaDecrypt(jsonObject.getString("k"), rsaVersion);
            String aesIv = this.rsaDecrypt(jsonObject.getString("i"), rsaVersion);
            // get request content
            JSONObject contentJson = this.readRequestBody(aesKey, aesIv, jsonObject.getString("d"));
            System.out.println(contentJson.toString());
            return this.buildResponseBody(contentJson, aesKey, aesIv);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Use RSA to decrypt AES Key or AES IV
     * 
     * @param source
     * @param rsaVersion
     * @return
     */
    private String rsaDecrypt(String source, int rsaVersion) {
        String rsaSecretKey = this.readRsaKey(rsaVersion);
        if (StringUtils.isEmpty(rsaSecretKey)) {
            return null;
        }
        String result = null;
        try {
            PrivateKey privateKey = RSAUtil.stringToPrivateKey(rsaSecretKey);
            byte[] decryptedAESKeyBytes = RSAUtil.privateDecrypt(Base64Util.base64ToByte(source), privateKey);
            result = Base64Util.byteToBase64(decryptedAESKeyBytes);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Read RSA private key from file
     * 
     * @param version
     * @return
     */
    private String readRsaKey(int version) {
//        if (1 != version && 2 != version) {
//            // TODO : logging
//            return null;
//        }
//        String filename = String.format(RESOURCE_DIR + "keys/rsa_key_%d", version);
//        try {
//            File file = new File(filename);
//            InputStream inputStream = new FileInputStream(file);
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            String read = "";
//            StringBuffer stringBuffer = new StringBuffer();
//            while ((read = bufferedReader.readLine()) != null) {
//                stringBuffer.append(read);
//            }
//            inputStream.close();
//            String keystr = stringBuffer.toString();
//            return keystr;
//        } catch (Exception e) {
//            // TODO : logging
//            e.printStackTrace();
//        }
        return "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIB6FtudS/hkPU6Kd15T21O6L862FMAPG1m0znA2mKo1kp8E7P3STFiXrUe/59IHw6+i9+jH7ds+4wQknLK9yhwYHNh0vSz06wJAlgZIBxE/dlocykPO+cbwXV66/Alt2tOCppyyz2bl4tw7WWbngGW3yc8AFcKGM3ko+GoSoxs3AgMBAAECgYBso1BayiqoUlLI7Cs91xjaNtEgMTVgpiWReGMr3e6/1ucxkh+4DbDDJRbAWhTzs98T0HFjJFQMFaQvJ6pdNkEEx9VkXc4R8kEqTEjEXS3J0i5bctEnwiec3CP/gTHRL+2dEhozK1JdqFwLcEJF+gcES6ILDjDDdDogtUIlseclYQJBALkUqtsMeTWrRqXYy/OjBlWfNVuqM74Mqsz8ffIaA5pd55frFr3Vwi8GWQaPs8Ox64D6jWkJ03Q3DUSSicLXsMcCQQCxtOtpM6TDs5/0XLkaeaOOcT5GxRLygBWjwW19Y6KxP77H6Mi5nv/QAb3/WJI7z0LftPtYMbpR6uGliBRRarIRAkB0HrFrqZHKyGEU4MZlX90zjyYNEuZP0ObAoZHrecLU8SHCwO6NwDWLB3KHmVVx7IkTbR+XzWBrw7aBVWc1Zdo1AkEAgry+oIQNCKli2W1KBQ9OY+IuVfRw7xWKVhJdEe22aL6kBgDdg6pVO1u8w1oPI49k/J3yeD0FfXTXE2gcwpVDcQJAKLyDKcr4k7uHFEIdHc1JIga4NY3Kh8KKhNB9Weo5LMM2NBEyAdq0MqFKGoeoGTFU7bJsX2ifd94nkymZMnNR6g==";
    }

    /**
     * Read request body
     * 
     * @param aesKey
     * @param encryptedRequestJson
     * @return
     */
    private JSONObject readRequestBody(String aesKey, String aesIv, String encryptedRequestJson) {
        if (StringUtils.isEmpty(encryptedRequestJson)) {
            // TODO: logging
            return null;
        }

        // decrypt content by aes key and aes iv
        String requestJson = null;
        try {
            byte[] key = Base64.decode(aesKey);
            byte[] iv = Base64.decode(aesIv);
            byte[] bytes = AESCipher.getInstance().decrypt(Base64.decode(encryptedRequestJson), key, iv);
            requestJson = new String(bytes, "UTF-8");
        } catch (Exception e) {
            // TODO: handle exception
        }

        JSONObject jsonObject = null;
        if (null != requestJson) {
            try {
                jsonObject = new JSONObject(requestJson);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        return jsonObject;
    }

    /**
     * Read request body from json
     * 
     * @param contentJson
     * @param aesKey
     * @return
     */
    private String buildResponseBody(JSONObject contentJson, String aesKey, String aesIv) {
        JSONObject responseJson = new JSONObject();
        JSONObject d = new JSONObject();
        JSONObject status = new JSONObject();
        JSONObject data = new JSONObject();

        // 填充d数据
        try {

            if (null == contentJson) {
                status.put("status", 400);
                status.put("msg", "Bad request");
            } else if (null == contentJson.getString("id")) {
                status.put("status", 400);
                status.put("msg", "Bad request, requestId can not be read");
            } else if (null == contentJson.get("data")) {
                status.put("status", 400);
                status.put("msg", "Bad request, data can not be read");
            } else {
                try {
                    JSONObject requestDataJson = (JSONObject) contentJson.getJSONObject("data");
                    // response data
                    data.put("timestamp", System.currentTimeMillis());
                    String newRsaKey = this.readRsaKey(2);
                    data.put("rsaPublicKey", newRsaKey);
                    // system config
                    String appId = requestDataJson.getString("appId");
                    data.put("systemConfig", appId);
                    // status code
                    status.put("status", 200);
                    status.put("msg", "OK");
                } catch (Exception e) {
                    status.put("status", 400);
                    status.put("msg", "Bad requet, appId can not be read");
                }
            }
            d.put("status", status);
            d.put("data", data);

        } catch (Exception e) {
            // TODO: handle exception
        }

        // 构建返回数据
        try {
            if (status.getInt("status") == 200) {
                responseJson.put("c", 1);
                // 加密d
                byte[] key = Base64.decode(aesKey);
                byte[] iv = Base64.decode(aesIv);
                byte[] dBytes = AESCipher.getInstance().encrypt(data.toString().getBytes(), key, iv);
                responseJson.put("d", Base64.encode(dBytes));
            } else {
                responseJson.put("c", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseJson.toString();
    }
}
