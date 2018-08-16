package net.security;


import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCipher {

    private static final int KEY_SIZE = 128;
    private static final int KEY_LEN = KEY_SIZE / 8;
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM = "AES";

    private static AESCipher sSelf = new AESCipher();

    private KeyGenerator mKeyGenerator;
    private SecureRandom mSecureRandom;

    private AESCipher() {
        try {
            mSecureRandom = new SecureRandom();
            mKeyGenerator = KeyGenerator.getInstance(ALGORITHM);
            mKeyGenerator.init(KEY_SIZE, mSecureRandom);
        } catch (Exception e) {
            throw new IllegalStateException(e.toString());
        }
    }

    public static AESCipher getInstance() {
        return sSelf;
    }

    /**
     * 生成随机密钥
     * 
     * @return
     */
    public synchronized KeySpec randomKey() {
        byte[] iv = new byte[KEY_LEN];
        mSecureRandom.nextBytes(iv);
        byte[] key = mKeyGenerator.generateKey().getEncoded();
        KeySpec keySpec = new KeySpec(key, iv);
        return keySpec;
    }

    /**
     * 加密
     * 
     * @param data
     * @param key
     * @return
     */
    public byte[] encrypt(byte[] data, byte[] key) {
        return encrypt(data, key, null);
    }

    /**
     * 加密
     * 
     * @param data
     * @param key
     * @param iv
     * @return
     */
    public byte[] encrypt(byte[] data, byte[] key, byte[] iv) {
        return doFinal(data, key, iv, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     * 
     * @param data
     * @param key
     * @return
     */
    public byte[] decrypt(byte[] data, byte[] key) {
        return decrypt(data, key, null);
    }

    /**
     * 解密
     * 
     * @param data
     * @param key
     * @param iv
     * @return
     */
    public byte[] decrypt(byte[] data, byte[] key, byte[] iv) {
        return doFinal(data, key, iv, Cipher.DECRYPT_MODE);
    }


    private byte[] doFinal(byte[] data, byte[] key, byte[] iv, int mode) {
        if (mode != Cipher.DECRYPT_MODE && mode != Cipher.ENCRYPT_MODE) {
            return null;
        }

        if (key == null || key.length != KEY_LEN) {
            return null;
        }

        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            if (iv != null && iv.length == KEY_LEN) {
                IvParameterSpec ivSpec = new IvParameterSpec(iv);
                cipher.init(mode, skeySpec, ivSpec);
            }
            else {
                cipher.init(mode, skeySpec);
            }
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 密钥信息
     */
    public static class KeySpec {
        private byte[] mKey;
        private byte[] mIv;

        private KeySpec(byte[] key, byte[] iv) {
            mKey = key;
            mIv = iv;
        }

        public byte[] getKey() {
            return mKey;
        }

        public byte[] getIv() {
            return mIv;
        }

        @Override
        public String toString() {
            return "{key:" + Arrays.toString(mKey) + ",iv:" + Arrays.toString(mIv) + "}";
        }

    }

}
