package net.security;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * RSA公钥加解密工具类
 * 
 */
public class RSAEncrypt {

    /**
     * 非对称加密密钥算法
     */
    public static final String ALGORITHM_RSA = "RSA";


    /**
     * 公钥解密
     * 
     * @param data
     *            待解密数据
     * @param key
     *            公钥
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static byte[] decryptWithPublicKey(byte[] data, byte[] key) throws Exception {

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);

        // 生成公钥
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     * 
     * @param data
     *            待加密数据
     * @param key
     *            公钥
     * @return byte[] 加密数据
     * @throws Exception
     */
    public static byte[] encryptWithPublicKey(byte[] data, byte[] key) throws Exception {

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);

        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }




}
