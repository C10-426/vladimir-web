package net;

public interface Security {

    byte[] encrypt(byte[] source) throws Exception;

    byte[] decrypt(byte[] source) throws Exception;

}