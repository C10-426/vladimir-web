package utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

    /**
     * Generate a AES secret key
     * 
     * @return
     * @throws Exception
     */
    public static String genKeyAES() throws Exception{
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey key = keyGen.generateKey();
        String base64Str = Base64Util.byteToBase64(key.getEncoded());
        return base64Str;
    }
    
    /**
     * Generate secret key by string
     * 
     * @param base64Key
     * @return
     * @throws Exception
     */
    public static SecretKey loadKeyAES(String base64Key) throws Exception{
        byte[] bytes = Base64Util.base64ToByte(base64Key);
        SecretKeySpec key = new SecretKeySpec(bytes, "AES");
        return key;
    }
    
    /**
     * Encrypt byte content by secret key
     * 
     * @param content
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptAES(byte[] content, SecretKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(content);
        return bytes;
    }
    
    /**
     * Dncrypt byte content by secret key
     * 
     * @param content
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptAES(byte[] content, SecretKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(content);
        return bytes;
    }
}
