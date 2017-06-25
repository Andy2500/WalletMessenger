package ru.ravens.service;

import java.math.BigInteger;
import java.security.MessageDigest;

public class TokenBuilder {
    public static String makeToken(String login) throws Exception {
        MessageDigest messageDigest;

        messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        messageDigest.update(login.getBytes());
        byte[] digest = messageDigest.digest();

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while (md5Hex.length() < 32) {
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }
}
