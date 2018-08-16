package net.security;


import org.springframework.util.StringUtils;

public class RSACipher {

    private static final String DEBUG_MODE_DEFAULT_KEY = "1|MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAehbbnUv4ZD1OindeU9tTui/OthTADxtZtM5wNpiqNZKfBOz90kxYl61Hv+fSB8Ovovfox+3bPuMEJJyyvcocGBzYdL0s9OsCQJYGSAcRP3ZaHMpDzvnG8F1euvwJbdrTgqacss9m5eLcO1lm54Blt8nPABXChjN5KPhqEqMbNwIDAQAB";

    private static class InstanceHolder {
        private static final RSACipher INSTANCE = new RSACipher();
    }

    public static RSACipher getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 密钥版本号
     */
    public int mPublicKeyVer = -1;

    /**
     * 公钥
     */
    public byte[] mPublicKeyRaw;


    /**
     * 是否已通过服务器安全校验
     */
    private boolean mHasSafetyChecked = false;

    private static String sDebugModeDefaultKey = DEBUG_MODE_DEFAULT_KEY;

    public static void setDebugModeDefaultKey(String key) {
        sDebugModeDefaultKey = key;
    }

    private RSACipher() {
        loadDefaultPublicKey();
    }

    public boolean updatePublicKey(String rsaPublicKey) {
        return parseAndSaveKey(rsaPublicKey);
    }


    public boolean hasSafetyChecked() {
        return mHasSafetyChecked;
    }

    public int getPublicKeyVer() {
        return mPublicKeyVer;
    }

    /**
     * 使用公钥加密传入的 byte 数组，并返回加密后的 byte 数组。
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public byte[] encryptWithPublicKey(byte[] bytes) throws Exception {
        return RSAEncrypt.encryptWithPublicKey(bytes, mPublicKeyRaw);
    }


    /**
     * 使用公钥加密传入的 byte 数组，并返回加密结果的 Base64 编码串。
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public String encryptWithPublicKeyToBase64(byte[] bytes) throws Exception {
        return Base64.encode(encryptWithPublicKey(bytes));
    }


    /**
     * 使用公钥加密传入的字符串，并返回加密结果的Base64编码串。
     *
     * @param string
     * @return
     * @throws Exception
     */
    public String encryptWithPublicKeyToBase64(String string) throws Exception {
        byte[] bytes = string.getBytes("utf-8");
        return Base64.encode(encryptWithPublicKey(bytes));
    }

    private void loadDefaultPublicKey() {
        parseAndSaveKey(sDebugModeDefaultKey);
    }

    private boolean parseAndSaveKey(String keyInfo) {
        if (StringUtils.isEmpty(keyInfo)) {
            return false;
        }
        boolean ret = false;
        try {
            String[] s = keyInfo.split("\\|");
            mPublicKeyVer = Integer.parseInt(s[0]);
            mPublicKeyRaw = Base64.decode(s[1]);
            ret = true;
        } catch (Exception e) {
            System.out.println("解析公钥失败，keyInfo:" + keyInfo);
        }
        return ret;
    }


}
