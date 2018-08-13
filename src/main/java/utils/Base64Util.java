package utils;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class Base64Util {
    
    /**
     * Convert bytes to base64 encoding string
     * 
     * @param bytes
     * @return
     */
    public static String byteToBase64(byte[] bytes) {
        final Encoder encoder = Base64.getEncoder();
        String base64Str = encoder.encodeToString(bytes);
        return base64Str;
    }
    
    /**
     * Convert base64 encoding string to bytes
     * 
     * @param base64Str
     * @return
     */
    public static byte[] base64ToByte(String base64Str) {
        final Decoder decoder = Base64.getDecoder();
        byte[] bytes = decoder.decode(base64Str);
        return bytes;
    }
    
    /**
     * Convert a base64 encoding string to UTF-8 string
     * 
     * @param base64Str
     * @return
     */
    public static String base64ToUTF8Str(String base64Str) {
        byte[] bytes = Base64Util.base64ToByte(base64Str);
        try {
            String str = new String(bytes, "UTF-8");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
